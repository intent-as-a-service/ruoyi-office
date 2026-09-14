/**
 * CRM 客户管理演示页：展示「页面上下文自动带参 + 意图按钮组 + 结果卡片」
 * 的产品化集成方式——业务页面只需两步：
 *   1. IntentUI.configure({ token, tenantId }) ← 登录态沿用宿主系统
 *   2. IntentUI.mount({ container, page, getContext, onEvent })
 */
(function () {
  'use strict';

  var TENANT_ID = 1;
  var state = { user: null, customers: [], selected: null, panel: null };

  // ------------------------------------------------------------ 工具

  function api(path, options) {
    var token = localStorage.getItem('iui_token');
    var headers = { 'Content-Type': 'application/json' };
    if (token) headers['Authorization'] = 'Bearer ' + token;
    headers['tenant-id'] = String(TENANT_ID);
    return fetch(path, Object.assign({ headers: headers }, options || {}))
      .then(function (res) { return res.json(); })
      .then(function (body) {
        if (body.code !== 0) {
          var err = new Error(body.msg || ('HTTP ' + body.code));
          err.code = body.code;
          throw err;
        }
        return body.data;
      });
  }

  function esc(s) {
    return s == null ? '' : String(s).replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;');
  }

  function fmtTime(ts) {
    if (!ts) return '—';
    var d = new Date(ts);
    return d.getFullYear() + '-' + String(d.getMonth() + 1).padStart(2, '0') + '-' +
      String(d.getDate()).padStart(2, '0');
  }

  // ------------------------------------------------------------ 登录

  function showLogin() {
    document.getElementById('login-view').style.display = 'flex';
    document.getElementById('app-view').style.display = 'none';
    document.getElementById('login-btn').onclick = doLogin;
    document.getElementById('login-password').addEventListener('keydown', function (e) {
      if (e.key === 'Enter') doLogin();
    });
  }

  function doLogin() {
    var btn = document.getElementById('login-btn');
    var errBox = document.getElementById('login-err');
    btn.disabled = true;
    errBox.classList.remove('on');
    api('/admin-api/system/auth/login', {
      method: 'POST',
      body: JSON.stringify({
        username: document.getElementById('login-username').value,
        password: document.getElementById('login-password').value
      })
    }).then(function (data) {
      localStorage.setItem('iui_token', data.accessToken);
      boot(data.user ? data.user.nickname || data.user.username : '');
    }).catch(function (e) {
      errBox.textContent = '登录失败：' + e.message + (String(e.message).indexOf('验证码') >= 0 ? '（本地环境可在 application-local.yaml 关闭验证码）' : '');
      errBox.classList.add('on');
      btn.disabled = false;
    });
  }

  // ------------------------------------------------------------ 应用

  function boot(nickname) {
    document.getElementById('login-view').style.display = 'none';
    document.getElementById('app-view').style.display = 'block';
    document.getElementById('app-user').textContent = '👤 ' + (nickname || 'admin');

    IntentUI.configure({ token: localStorage.getItem('iui_token'), tenantId: TENANT_ID });
    state.panel = IntentUI.mount({
      container: document.getElementById('intent-panel'),
      page: 'crm/customer',
      getContext: function () {
        // ★ 页面上下文自动带参：SDK 执行意图时自动调用，把当前客户并入请求
        return state.selected
          ? { page: 'crm/customer', customerId: String(state.selected.id) }
          : { page: 'crm/customer' };
      },
      onEvent: function (type, payload) {
        if (type === 'executed') loadCustomers(); // 写操作后刷新列表
      }
    });

    api('/admin-api/intent/status').then(function (status) {
      document.getElementById('gateway-status').innerHTML =
        '网关：<span class="pill ' + (status.gatewayEnabled ? 'g' : 'n') + '">' +
        (status.gatewayEnabled ? '已装配' : '未装配（本地模式）') + '</span>';
    });

    loadCustomers();
  }

  function loadCustomers() {
    api('/admin-api/crm/customer/page?pageNo=1&pageSize=20').then(function (page) {
      state.customers = page.list || [];
      var rows = document.getElementById('customer-rows');
      document.getElementById('list-meta').textContent = '共 ' + page.total + ' 家';
      rows.innerHTML = state.customers.map(function (c) {
        var active = state.selected && state.selected.id === c.id ? ' active' : '';
        return '<tr class="row' + active + '" data-id="' + c.id + '">' +
          '<td><b>' + esc(c.name) + '</b><br><span style="font-size:11px;color:var(--iui-text-3)">' +
          esc(c.detailAddress || '') + '</span></td>' +
          '<td>' + (c.levelName || levelText(c.level)) + '</td>' +
          '<td><span class="pill ' + (c.dealStatus ? 'g' : 'n') + '">' + (c.dealStatus ? '已成交' : '未成交') + '</span></td>' +
          '<td style="max-width:180px;overflow:hidden;text-overflow:ellipsis;white-space:nowrap">' +
          esc(c.contactLastContent || '—') + '</td>' +
          '<td>' + (c.sourceName || sourceText(c.source)) + '</td></tr>';
      }).join('');
      rows.querySelectorAll('tr.row').forEach(function (tr) {
        tr.addEventListener('click', function () { selectCustomer(Number(tr.getAttribute('data-id'))); });
      });
      // 默认选中第一个客户，页面打开即可直接体验意图
      if (!state.selected && state.customers.length) selectCustomer(state.customers[0].id);
    }).catch(function (e) {
      document.getElementById('customer-rows').innerHTML =
        '<tr><td colspan="5"><div class="iui-alert err">客户列表加载失败：' + esc(e.message) + '</div></td></tr>';
    });
  }

  function selectCustomer(id) {
    state.selected = state.customers.find(function (c) { return c.id === id; }) || null;
    document.querySelectorAll('#customer-rows tr.row').forEach(function (tr) {
      tr.classList.toggle('active', Number(tr.getAttribute('data-id')) === id);
    });
    document.getElementById('ctx-customer').textContent =
      state.selected ? state.selected.name + '（ID=' + state.selected.id + '）' : '未选择客户';
  }

  // 字典回显（来自 CRM page 接口的枚举值；演示页内置映射）
  function levelText(v) { return { 1: 'A 级', 2: 'B 级', 3: 'C 级' }[v] || v || '—'; }
  function sourceText(v) {
    return { 1: '官网咨询', 2: '广告投放', 3: '客户转介绍', 4: '展会活动', 5: '其他' }[v] || v || '—';
  }

  // ------------------------------------------------------------ 启动

  if (localStorage.getItem('iui_token')) {
    // 已有登录态：先校验有效性
    api('/admin-api/system/auth/get-permission-info')
      .then(function (info) { boot(info.user && (info.user.nickname || info.user.username)); })
      .catch(function () { localStorage.removeItem('iui_token'); showLogin(); });
  } else {
    showLogin();
  }
})();
