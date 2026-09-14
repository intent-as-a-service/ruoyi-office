/**
 * Intent UI SDK v0.4.0
 * 嵌入式 AI 意图组件（框架无关）：业务页面一行挂载即可获得
 * 悬浮入口 / 意图菜单 / 参数补全表单 / 待办与推荐 / 结果卡片 / 执行轨迹 / 历史 / 反馈 全套能力。
 *
 * 用法（悬浮入口 + 抽屉，业务系统全站一个入口）：
 *   IntentUI.configure({
 *     apiPrefix: '/dev-api/intent',                          // 后端意图接口前缀（含宿主的 API 前缀）
 *     authHeaders: () => ({ Authorization: 'Bearer ' + getToken(), clientid: import.meta.env.VITE_APP_CLIENT_ID }),
 *     locale: 'zh-CN',
 *     entityOptions: { userId: () => api.get('/system/user/list') },  // 槽位实体下拉（宿主提供）
 *   });
 *   IntentUI.mountFloating({
 *     getPage: () => route.path.replace(/^\//, ''),           // 页面标识 → 按页面装载意图
 *     getContext: () => ({ userId: page.currentUserId, userName: page.currentUserName }),
 *   });
 *
 * 用法（内嵌面板，某个页面里放一块）：
 *   IntentUI.mount({ container: document.querySelector('#intent-panel'), page: 'crm/contract' });
 *
 * 设计约束：本文件不依赖任何前端框架、不假设后端是 ruoyi——
 * 路径、鉴权、文案、主题、实体下拉、页面上报全部经 configure/mount 注入。
 */
(function (global) {
  'use strict';

  var VERSION = '0.4.0';

  // ------------------------------------------------------------ 配置

  var CONFIG = {
    basePath: '',
    apiPrefix: '/intent',
    token: null,
    tenantId: null,
    headers: null,
    authHeaders: null,
    /** 自定义请求器（宿主想走自己的 HTTP 客户端/加密/拦截器时注入） */
    fetcher: null,
    locale: 'zh-CN',
    theme: 'auto',
    zIndex: null,
    strings: null,
    /** 抽屉宽度（px） */
    drawerWidth: 920,
    /** 待办分组内每页条数 */
    todoPageSize: 3,
    /** 最多并列展示几个待办分组 */
    maxTodoGroups: 5,
    /** 「当前对象快捷条」最多展示几个意图（0 = 不展示） */
    quickIntents: 3,
    /** 槽位实体下拉的数据来源：{ key: [{label,value}] | () => 数组/Promise } */
    entityOptions: null
  };

  function url(path) {
    return CONFIG.basePath + CONFIG.apiPrefix + path;
  }

  function authHeaders() {
    var headers = { 'Content-Type': 'application/json' };
    if (CONFIG.token) headers['Authorization'] = 'Bearer ' + CONFIG.token;
    if (CONFIG.tenantId != null) headers['tenant-id'] = String(CONFIG.tenantId);
    if (typeof CONFIG.authHeaders === 'function') {
      Object.assign(headers, CONFIG.authHeaders() || {});
    }
    if (CONFIG.headers) Object.assign(headers, CONFIG.headers);
    return headers;
  }

  function request(path, options) {
    var opts = options || {};
    if (typeof CONFIG.fetcher === 'function') {
      return Promise.resolve(CONFIG.fetcher(url(path), opts, authHeaders())).then(unwrap);
    }
    return fetch(url(path), Object.assign({ headers: authHeaders() }, opts))
      .then(function (res) { return res.json(); })
      .then(unwrap);
  }

  function unwrap(body) {
    // 兼容两套统一响应码：0（SDK 参考宿主）与 200（RuoYi-Vue-Plus 的 R<T>）
    if (body && body.code !== undefined && body.code !== 0 && body.code !== 200) {
      var err = new Error(body.msg || ('HTTP ' + body.code));
      err.code = body.code;
      throw err;
    }
    return body && body.code !== undefined ? body.data : body;
  }

  var api = {
    catalog: function (page) {
      return request(page ? ('/catalog?page=' + encodeURIComponent(page)) : '/catalog');
    },
    execute: function (payload) {
      return request('/execute', { method: 'POST', body: JSON.stringify(payload) });
    },
    history: function (limit) {
      return request('/history?limit=' + (limit || 20));
    },
    trace: function (traceId) {
      return request('/trace/' + encodeURIComponent(traceId));
    },
    feedback: function (payload) {
      return request('/feedback', { method: 'POST', body: JSON.stringify(payload) });
    }
  };

  // ------------------------------------------------------------ 文案（内置 zh-CN / en-US，可被 configure.strings 覆盖）

  var STRINGS = {
    'zh-CN': {
      panelTitle: 'AI 智能操作',
      fabTitle: 'AI 智能操作（在线）· 可拖到任意位置',
      fabTitleOffline: 'AI 智能操作 · 可拖动',
      history: '执行历史',
      analyzing: '正在分析…',
      running: '执行中…',
      executing: '正在执行「{name}」…',
      currentPage: '当前页面：',
      currentObject: '当前对象：',
      emptyCatalog: '该页面暂未配置意图',
      emptyCatalogDesc: '请尽快到后台「意图中心 - 意图管理」配置意图并声明页面挂载点（pages）；配置保存后本页面即时生效，无需发版。',
      catalogFailed: '意图目录加载失败：',
      callFailed: '调用失败：',
      failed: '执行失败',
      errorCode: '错误码 ',
      trace: ' · 轨迹 ',
      resultTitle: '执行结果',
      cardKicker: 'AI 生成 · 已通过输出校验',
      followupHint: '点击按此方向重新分析',
      copy: '复制',
      copied: '已复制',
      rerun: '重新生成',
      good: '👍',
      bad: '👎',
      rateThanks: '感谢反馈',
      steps: '执行过程',
      noSteps: '无执行步骤',
      stepTool: '工具',
      stepLlm: '推理',
      stepFailed: '失败',
      kvSeparator: '：',
      feedbackFailed: '反馈失败: ',
      collapse: '收起',
      expand: '展开',
      nextStep: '下一步',
      todoAndSuggest: '待办与推荐',
      followupGroup: '🔁 补充要求重跑（仍是当前意图 · 点击后把这句补进槽位）',
      followupUnsupported: '该意图暂不支持补充要求重跑',
      nextIntentGroup: '⭐ AI 推荐下一步（换个意图接着干 · 点击直接执行）',
      todoGroup: '📌 待办 · ',
      moreGroups: '另有 {groups} 个待办分组（{items} 条）未展示',
      todoColItem: '事项',
      todoColAction: '',
      run: '执行',
      pageOf: '第 {page} / {total} 页',
      prevPage: '上一页',
      nextPage: '下一页',
      intentMenu: '意图菜单',
      searchPlaceholder: '说出来想做什么，比如「这客户咋样了」',
      matchFound: '按你的说法匹配到 {count} 个意图',
      matchNone: '没听明白你想做什么 —— 换个说法，或直接从下面完整菜单里选',
      matchHit: '命中：',
      clear: '清空',
      quickGroup: '针对当前页面对象',
      quickGroupNamed: '针对「{name}」',
      slots: '填写参数槽位后执行',
      formTitle: '补充信息后执行',
      formTitleSlots: '填写参数槽位',
      formDesc: '「{name}」需要以下信息',
      pleaseSelect: '请选择',
      pleaseInput: '请输入',
      pleaseInputWith: '请输入（{title}）',
      cancel: '取消',
      confirm: '确认执行',
      fillRequired: '请填写：',
      historyTitle: '我的执行历史',
      emptyHistory: '暂无执行记录',
      historyFailed: '加载历史失败: ',
      back: '返回列表',
      viewTrace: '查看轨迹',
      statusSuccess: '成功',
      statusNeedInput: '待补参',
      statusFailed: '失败',
      remoteTag: '远程',
      remoteDisabled: '需要装配意图网关（当前未装配），装配后此跨系统意图可用',
      errRemoteUnavailable: '该意图需要跨系统网关支持，当前未装配网关',
      errValidation: '参数不符合规范',
      errLlm: '模型服务暂不可用',
      errOutputInvalid: '结果未通过输出校验（已自动重试）',
      errTimeout: '执行超时，请稍后重试',
      errIntentNotFound: '意图不存在',
      errForbidden: '没有使用该意图的权限',
      errMissingParams: '缺少必填参数，请补全后重新执行',
      done: '「{name}」执行完成',
      doneGeneric: '执行完成'
    },
    'en-US': {
      panelTitle: 'AI Actions',
      fabTitle: 'AI Actions (online) · drag to move',
      fabTitleOffline: 'AI Actions · drag to move',
      history: 'History',
      analyzing: 'Analyzing…',
      running: 'Running…',
      executing: 'Running "{name}"…',
      currentPage: 'Page: ',
      currentObject: 'Object: ',
      emptyCatalog: 'No actions configured for this page',
      emptyCatalogDesc: 'Add intents in the admin console and declare pages; changes take effect immediately.',
      catalogFailed: 'Failed to load actions: ',
      callFailed: 'Request failed: ',
      failed: 'Failed',
      errorCode: 'Error code ',
      trace: ' · trace ',
      resultTitle: 'Result',
      cardKicker: 'AI generated · output validated',
      followupHint: 'Click to re-analyze in this direction',
      copy: 'Copy',
      copied: 'Copied',
      rerun: 'Regenerate',
      good: '👍',
      bad: '👎',
      rateThanks: 'Thanks for the feedback',
      steps: 'Steps',
      noSteps: 'No steps',
      stepTool: 'tool',
      stepLlm: 'llm',
      stepFailed: 'failed',
      kvSeparator: ': ',
      feedbackFailed: 'Feedback failed: ',
      collapse: 'Collapse',
      expand: 'Expand',
      nextStep: 'Next steps',
      todoAndSuggest: 'To-do & suggested',
      followupGroup: '🔁 Re-run with extra requirement (same action)',
      followupUnsupported: 'This action does not support follow-up re-run',
      nextIntentGroup: '⭐ Suggested next actions (switch action · click to run)',
      todoGroup: '📌 To-do · ',
      moreGroups: '{groups} more groups ({items} items) hidden',
      todoColItem: 'Item',
      todoColAction: '',
      run: 'Run',
      pageOf: 'Page {page} / {total}',
      prevPage: 'Prev',
      nextPage: 'Next',
      intentMenu: 'Actions',
      searchPlaceholder: 'Say what you want to do, e.g. "how is this customer doing"',
      matchFound: '{count} matching actions',
      matchNone: 'Could not understand that — try another wording, or pick from the full menu below',
      matchHit: 'Matched: ',
      clear: 'Clear',
      quickGroup: 'For the current object',
      quickGroupNamed: 'For "{name}"',
      slots: 'Fill parameters and run',
      formTitle: 'Complete the input',
      formTitleSlots: 'Parameters',
      formDesc: '"{name}" needs the following',
      pleaseSelect: 'Select',
      pleaseInput: 'Enter a value',
      pleaseInputWith: 'Enter {title}',
      cancel: 'Cancel',
      confirm: 'Run',
      fillRequired: 'Required: ',
      historyTitle: 'My history',
      emptyHistory: 'No runs yet',
      historyFailed: 'Failed to load history: ',
      back: 'Back to list',
      viewTrace: 'View trace',
      statusSuccess: 'Success',
      statusNeedInput: 'Needs input',
      statusFailed: 'Failed',
      remoteTag: 'remote',
      remoteDisabled: 'Requires the intent gateway, which is not configured',
      errRemoteUnavailable: 'This action needs the cross-system gateway, which is not configured',
      errValidation: 'Invalid parameters',
      errLlm: 'Model service unavailable',
      errOutputInvalid: 'Output failed validation (auto-retried)',
      errTimeout: 'Timed out, please retry',
      errIntentNotFound: 'Action not found',
      errForbidden: 'You are not allowed to use this action',
      errMissingParams: 'Missing required parameters',
      done: '"{name}" finished',
      doneGeneric: 'Done'
    }
  };

  function dict() {
    var base = STRINGS[CONFIG.locale] || STRINGS['zh-CN'];
    if (!CONFIG.strings) return base;
    var merged = {};
    Object.keys(base).forEach(function (k) { merged[k] = base[k]; });
    Object.keys(CONFIG.strings).forEach(function (k) { merged[k] = CONFIG.strings[k]; });
    return merged;
  }

  function t(key, vars) {
    var text = dict()[key];
    if (text == null) return key;
    if (!vars) return text;
    return text.replace(/\{(\w+)\}/g, function (m, name) {
      return vars[name] == null ? m : String(vars[name]);
    });
  }

  var ERROR_TEXT = {
    REMOTE_UNAVAILABLE: 'errRemoteUnavailable',
    VALIDATION_ERROR: 'errValidation',
    LLM_ERROR: 'errLlm',
    OUTPUT_INVALID: 'errOutputInvalid',
    TIMEOUT: 'errTimeout',
    INTENT_NOT_FOUND: 'errIntentNotFound',
    FORBIDDEN: 'errForbidden',
    MISSING_PARAMS: 'errMissingParams'
  };

  var LEVEL_COLOR = { info: 'info', warning: 'warning', danger: 'danger', success: 'success' };

  function esc(s) {
    return s == null ? '' : String(s)
      .replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;')
      .replace(/"/g, '&quot;');
  }

  function toast(text, kind) {
    if (!text) return;
    var host = document.body || document.documentElement;
    var box = host.querySelector('.iui-toasts');
    if (!box) {
      box = document.createElement('div');
      box.className = 'iui-toasts';
      host.appendChild(box);
    }
    var item = document.createElement('div');
    item.className = 'iui-toast' + (kind ? ' ' + kind : '');
    item.textContent = text;
    box.appendChild(item);
    setTimeout(function () {
      item.classList.add('out');
      setTimeout(function () { item.remove(); }, 300);
    }, 2200);
  }
  /** 宿主事件回调（executed 等），宿主未提供时静默忽略 */
  function notify(h, type, payload) {
    if (!h || typeof h.options.onEvent !== 'function') return;
    try { h.options.onEvent(type, payload); } catch (e) { /* 宿主回调异常不影响执行 */ }
  }

  // ------------------------------------------------------------ 面板状态

  function createState() {
    return {
      catalog: null,
      entries: [],
      allEntries: [],
      suggestions: [],
      result: null,
      currentEntry: null,
      error: null,
      running: false,
      runningIntent: null,
      runningText: '',
      resultCollapsed: false,
      stepsOpen: false,
      expandedGroups: {},
      todoPages: {},
      entityCache: {},
      rated: {},
      /** 口语匹配框的当前输入（渲染时可回填，避免切页丢失） */
      query: ''
    };
  }

  // ------------------------------------------------------------ 面板

  function Panel(handle) {
    this.handle = handle;
  }

  Panel.prototype.mountHtml = function () {
    var h = this.handle;
    h.root = document.createElement('div');
    h.root.className = 'iui-root';
    h.root.innerHTML =
      '<div class="iui-panel">' +
        '<div class="iui-panel-head">' +
          '<span class="iui-panel-title">' +
            '<svg class="iui-spark" viewBox="0 0 24 24" fill="none"><path d="M12 2l1.9 5.7L20 9.5l-5.2 3.4L16 19l-4-3.2L8 19l1.2-6.1L4 9.5l6.1-1.8L12 2z" fill="#2f6bff"/></svg>' +
            '<span class="iui-panel-title-text"></span>' +
          '</span>' +
          '<a href="javascript:void 0" class="iui-hist-link"></a>' +
        '</div>' +
        '<div class="iui-panel-meta"></div>' +
        '<div class="iui-running"><span class="iui-spinner"></span><span class="iui-running-text"></span></div>' +
        '<div class="iui-scroll"></div>' +
      '</div>';
    h.scroll = h.root.querySelector('.iui-scroll');
    h.root.querySelector('.iui-hist-link').addEventListener('click', function () { openHistory(h); });
    return h.root;
  };

  Panel.prototype.load = function () {
    var h = this.handle;
    var page = typeof h.options.getPage === 'function' ? h.options.getPage() : h.options.page;
    h.page = page || '';
    return Promise.all([api.catalog(h.page), api.catalog()]).then(function (res) {
      h.state.catalog = res[0];
      h.state.entries = res[0].entries || [];
      h.state.allEntries = res[1].entries || [];
      h.state.suggestions = res[0].suggestions || [];
      h.state.error = null;
      this.render();
      if (h.options.autoRun && h.state.entries.length) {
        this.run(h.state.entries[0], {});
      }
    }.bind(this)).catch(function (e) {
      h.state.error = t('catalogFailed') + (e && e.message ? e.message : e);
      this.render();
    }.bind(this));
  };

  /**
   * 重置运行态：清空上一个意图的结果卡 / 选中项 / 报错与分组展开状态。
   *
   * 为什么需要它：悬浮入口是**全站常驻**的（挂在 layout 上，切换菜单不会重新挂载），
   * 面板 state 会跨页面一直存活。切页时若不重置，"上一个页面点过的意图结果"会继续
   * 显示在新页面上，让人误以为它是当前页面的产出——上下文串页比不显示更糟。
   *
   * 刻意保留两项与页面无关的缓存：
   *   · entityCache —— 实体下拉候选，拉取有成本且与页面无关；
   *   · rated       —— 按 traceId 记的"已评价"标记，清掉会让历史里的评价状态丢失。
   */
  Panel.prototype.reset = function () {
    var st = this.handle.state;
    st.result = null;
    st.currentEntry = null;
    st.error = null;
    st.resultCollapsed = false;
    st.stepsOpen = false;
    // 分组折叠与待办翻页是"当前页面目录"的 UI 状态，换页即失效
    st.expandedGroups = {};
    st.todoPages = {};
    st.query = '';
    // 执行中的请求不打断（结果回来仍会渲染），只清掉"执行中"提示，避免卡在旧提示上
    if (!st.running) {
      st.runningIntent = null;
      st.runningText = '';
    }
    this.render();
    return this;
  };

  Panel.prototype.render = function (keepScroll) {
    var h = this.handle;
    var st = h.state;
    if (!h.scroll) return;
    var top = keepScroll === false ? 0 : h.scroll.scrollTop;
    h.root.querySelector('.iui-panel-title-text').textContent = t('panelTitle');
    var hist = h.root.querySelector('.iui-hist-link');
    hist.textContent = t('history');
    var meta = h.root.querySelector('.iui-panel-meta');
    var ctx = this.context();
    var lines = [];
    if (h.page) lines.push('<span>' + t('currentPage') + esc(h.page) + '</span>');
    var objName = this.objectName(ctx);
    if (objName) lines.push('<span>' + t('currentObject') + esc(objName) + '</span>');
    meta.innerHTML = lines.join('');
    meta.style.display = lines.length ? '' : 'none';

    var runBox = h.root.querySelector('.iui-running');
    runBox.classList.toggle('on', !!st.running);
    runBox.querySelector('.iui-running-text').textContent = st.runningText || t('analyzing');

    h.scroll.innerHTML = this.renderNextSteps() + this.renderResult() + this.renderMenu();
    h.scroll.scrollTop = top;
    this.bind();
  };

  Panel.prototype.context = function () {
    var h = this.handle;
    try {
      return (typeof h.options.getContext === 'function' ? h.options.getContext() : h.options.context) || {};
    } catch (e) {
      return {};
    }
  };

  /** 「当前对象」的显示名：宿主可在上下文里给 customerName/contractName，或自行给 objectName */
  Panel.prototype.objectName = function (ctx) {
    return ctx.customerName || ctx.contractName || ctx.clueName || ctx.businessName || ctx.objectName || '';
  };

  // ---- 待办与推荐（三组：补充要求重跑 / AI 推荐意图 / 宿主待办）

  Panel.prototype.nextEntries = function () {
    var h = this.handle;
    var list = (h.state.result && h.state.result.output && h.state.result.output.nextIntents) || [];
    return list.filter(function (n) {
      return n && n.intentId && h.state.allEntries.some(function (e) { return e.id === n.intentId; });
    });
  };

  Panel.prototype.suggestionGroups = function () {
    var h = this.handle;
    var groups = [];
    var index = {};
    (h.state.suggestions || []).forEach(function (s) {
      if (!index[s.intentId]) {
        var entry = h.state.allEntries.find(function (e) { return e.id === s.intentId; });
        index[s.intentId] = { intentId: s.intentId, name: (entry && entry.name) || s.intentId, items: [] };
        groups.push(index[s.intentId]);
      }
      index[s.intentId].items.push(s);
    });
    return groups;
  };

  Panel.prototype.renderNextSteps = function () {
    var h = this.handle;
    var st = h.state;
    var followups = (st.result && st.result.output && st.result.output.followups) || [];
    var nexts = this.nextEntries();
    var groups = this.suggestionGroups();
    var total = followups.length + nexts.length + (st.suggestions || []).length;
    if (!total) return '';

    var html = '<div class="iui-sec">' +
      '<div class="iui-sec-head"><span class="iui-sec-title">' +
      esc(st.result ? t('nextStep') : t('todoAndSuggest')) + '</span>' +
      '<span class="iui-tag iui-tag-count">' + total + '</span></div>';

    if (followups.length) {
      html += '<div class="iui-group iui-group-dashed">' +
        '<div class="iui-group-title">' + esc(t('followupGroup')) + '</div>' +
        '<div class="iui-chips">' + followups.map(function (f, i) {
          return '<button type="button" class="iui-chip" data-act="followup" data-idx="' + i + '">' + esc(f) + '</button>';
        }).join('') + '</div></div>';
    }

    if (nexts.length) {
      html += '<div class="iui-group iui-group-blue">' +
        '<div class="iui-group-title">' + esc(t('nextIntentGroup')) + '</div>' +
        '<div class="iui-group-body">' + nexts.map(function (n, i) {
          return '<button type="button" class="iui-next" data-act="next" data-idx="' + i + '">' +
            '<span class="iui-next-ico">⭐</span>' +
            '<span class="iui-next-body"><span class="iui-next-title">' + esc(n.title || n.intentId) + '</span>' +
            '<span class="iui-next-desc">' + esc(n.intentId + (n.reason ? ' · ' + n.reason : '')) + '</span></span>' +
            '</button>';
        }).join('') + '</div></div>';
    }

    var maxGroups = CONFIG.maxTodoGroups;
    var shown = groups.slice(0, maxGroups);
    var hidden = groups.slice(maxGroups);
    shown.forEach(function (g) {
      var expanded = !!st.expandedGroups[g.intentId];
      var pageSize = CONFIG.todoPageSize;
      var pageCount = Math.max(1, Math.ceil(g.items.length / pageSize));
      var page = Math.min(Math.max(1, st.todoPages[g.intentId] || 1), pageCount);
      st.todoPages[g.intentId] = page;
      var slice = g.items.slice((page - 1) * pageSize, page * pageSize);
      html += '<div class="iui-group iui-group-orange">' +
        '<button type="button" class="iui-group-toggle" data-act="toggle-group" data-intent="' + esc(g.intentId) + '">' +
          '<span class="iui-caret">' + (expanded ? '▾' : '▸') + '</span>' +
          '<span class="iui-group-title-inline">' + esc(t('todoGroup') + g.name) + '</span>' +
          '<span class="iui-tag iui-tag-orange">' + g.items.length + '</span>' +
          '<span class="iui-group-hint">' + esc(expanded ? t('collapse') : t('expand')) + '</span>' +
        '</button>';
      if (expanded) {
        html += '<table class="iui-todo-table"><tbody>' + slice.map(function (s, i) {
          var idx = g.items.indexOf(s);
          var title = s.title || s.intentId;
          var sub = [];
          if (s.subtitle) sub.push(esc(s.subtitle));
          if (s.reason) sub.push(esc(s.reason));
          return '<tr>' +
            '<td><div class="iui-todo-title">' + esc(title) +
              (s.count ? ' <span class="iui-todo-count">· ' + s.count + '</span>' : '') + '</div>' +
              (sub.length ? '<div class="iui-todo-sub">' + sub.join('<br>') + '</div>' : '') + '</td>' +
            '<td class="iui-todo-act"><button type="button" class="iui-mini" data-act="run-suggestion" data-group="' +
              esc(g.intentId) + '" data-idx="' + idx + '">' + esc(t('run')) + '</button></td>' +
            '</tr>';
        }).join('') + '</tbody></table>';
        if (pageCount > 1) {
          html += '<div class="iui-pager">' +
            '<button type="button" class="iui-mini" data-act="page" data-intent="' + esc(g.intentId) + '" data-page="' + (page - 1) + '"' +
              (page <= 1 ? ' disabled' : '') + '>' + esc(t('prevPage')) + '</button>' +
            '<span class="iui-pager-text">' + esc(t('pageOf', { page: page, total: pageCount })) + '</span>' +
            '<button type="button" class="iui-mini" data-act="page" data-intent="' + esc(g.intentId) + '" data-page="' + (page + 1) + '"' +
              (page >= pageCount ? ' disabled' : '') + '>' + esc(t('nextPage')) + '</button>' +
            '</div>';
        }
      }
      html += '</div>';
    });
    if (hidden.length) {
      var hiddenItems = hidden.reduce(function (acc, g) { return acc + g.items.length; }, 0);
      html += '<div class="iui-more">' + esc(t('moreGroups', { groups: hidden.length, items: hiddenItems })) + '</div>';
    }
    return html + '</div>';
  };

  // ---- 结果卡 / 失败提示

  Panel.prototype.renderResult = function () {
    var st = this.handle.state;
    var result = st.result;
    if (!result) return '';
    if (result.status === 'FAILED') {
      var code = result.error && result.error.code;
      var friendly = (code && ERROR_TEXT[code] ? t(ERROR_TEXT[code]) : null) ||
        (result.error && result.error.message) || t('failed');
      return '<div class="iui-alert err"><span class="iui-alert-ico">⚠️</span><span>' +
        '<b>' + esc(t('failed')) + '</b>' + esc(t('kvSeparator')) + esc(friendly) +
        '<br><span class="iui-alert-sub">' + esc(t('errorCode')) + esc(code || '-') + esc(t('trace')) + esc(result.traceId) + '</span>' +
        '</span></div>';
    }
    if (result.status !== 'SUCCESS' || !result.output) return '';
    return this.renderCard(result);
  };

  Panel.prototype.renderCard = function (result) {
    var st = this.handle.state;
    var out = result.output || {};
    var entry = this.entry(result.intentId);
    var collapsed = st.resultCollapsed;
    var html = '<div class="iui-card" data-trace="' + esc(result.traceId) + '">' +
      '<div class="iui-card-head">' +
        '<div class="iui-card-headrow">' +
          '<span class="iui-tag iui-tag-blue">' + esc(t('cardKicker')) + '</span>' +
          '<button type="button" class="iui-mini iui-collapse" data-act="collapse">' + esc(collapsed ? t('expand') : t('collapse')) + '</button>' +
        '</div>' +
        '<div class="iui-card-title">' + esc(out.title || (entry && entry.name) || t('resultTitle')) + '</div>' +
      '</div>';
    if (!collapsed) {
      if (out.summary) html += '<div class="iui-card-summary">' + esc(out.summary) + '</div>';
      html += '<div class="iui-card-body">' + (out.blocks || []).map(renderBlock).join('') + '</div>';
      html += '<div class="iui-card-foot">' +
        '<button type="button" class="iui-mini" data-act="copy">' + esc(t('copy')) + '</button>' +
        '<button type="button" class="iui-mini" data-act="rerun">' + esc(t('rerun')) + '</button>' +
        '<button type="button" class="iui-mini' + (st.rated[result.traceId] === 'UP' ? ' on' : '') + '" data-act="good">' + esc(t('good')) + '</button>' +
        '<button type="button" class="iui-mini' + (st.rated[result.traceId] === 'DOWN' ? ' on' : '') + '" data-act="bad">' + esc(t('bad')) + '</button>' +
        '<button type="button" class="iui-mini" data-act="steps">' + esc(t('steps')) + '</button>' +
        '<span class="iui-foot-note">' + esc(result.intentId) + ' · ' + ((result.durationMs || 0) / 1000).toFixed(1) + 's · ' +
        esc((result.usage && result.usage.totalTokens) || 0) + ' tokens · ' + esc(result.traceId) + '</span>' +
        '</div>';
      html += '<div class="iui-steps' + (st.stepsOpen ? ' on' : '') + '">' + renderSteps(result) + '</div>';
    }
    return html + '</div>';
  };

  function renderBlock(block) {
    var html = '<div class="iui-block">';
    if (block.title) html += '<div class="iui-block-title">' + esc(block.title) + '</div>';
    if (block.kind === 'text') {
      html += '<div class="iui-text">' + esc(block.text || '') + '</div>';
    } else if (block.kind === 'kv') {
      html += '<div class="iui-kv">' + (block.items || []).map(function (item) {
        return '<div class="iui-kv-item"><div class="iui-kv-label">' + esc(item.label) + '</div>' +
          '<div class="iui-kv-value">' + esc(item.value) + '</div></div>';
      }).join('') + '</div>';
    } else if (block.kind === 'list') {
      html += '<div class="iui-list">' + (block.items || []).map(function (item) {
        var text = typeof item === 'string' ? item : (item.label + (item.value ? t('kvSeparator') + item.value : ''));
        return '<div class="iui-list-item"><span>' + esc(text) + '</span></div>';
      }).join('') + '</div>';
    } else if (block.kind === 'table') {
      html += '<div class="iui-table-wrap"><table class="iui-table"><thead><tr>' +
        (block.columns || []).map(function (c) { return '<th>' + esc(c) + '</th>'; }).join('') +
        '</tr></thead><tbody>' +
        (block.rows || []).map(function (row) {
          return '<tr>' + row.map(function (cell) { return '<td>' + esc(cell) + '</td>'; }).join('') + '</tr>';
        }).join('') + '</tbody></table></div>';
    } else if (block.kind === 'badges') {
      html += '<div class="iui-badges">' + (block.items || []).map(function (item) {
        var text = typeof item === 'string' ? item : (item.label + (item.value ? t('kvSeparator') + item.value : ''));
        return '<span class="iui-badge ' + esc(LEVEL_COLOR[block.level] || 'info') + '">' + esc(text) + '</span>';
      }).join('') + '</div>';
    }
    return html + '</div>';
  }

  function renderSteps(result) {
    if (!result.steps || !result.steps.length) return '<div class="iui-empty">' + esc(t('noSteps')) + '</div>';
    return result.steps.map(function (s) {
      return '<div class="iui-step">' +
        '<span class="iui-step-kind ' + esc(s.kind) + '">' + esc(s.kind === 'tool' ? t('stepTool') : t('stepLlm')) + '</span>' +
        '<span class="iui-step-name">' + esc(s.name) + '</span>' +
        '<span class="iui-step-detail">' + esc(s.ok ? (s.resultSummary || s.argsSummary || '') : (s.error || t('stepFailed'))) +
        (s.durationMs ? ' <span class="iui-dim">(' + s.durationMs + 'ms)</span>' : '') + '</span></div>';
    }).join('');
  }

  // ---- 意图菜单

  Panel.prototype.renderMenu = function () {
    var h = this.handle;
    var st = h.state;
    var quick = this.quickEntries();
    var html = '<div class="iui-sec iui-sec-menu">' +
      '<div class="iui-sec-head"><span class="iui-sec-title">' + esc(t('intentMenu')) + '</span></div>';
    if (quick.length) {
      var objectName = this.objectName(this.context());
      html += '<div class="iui-quick">' +
        '<div class="iui-quick-head">' +
          esc(objectName ? t('quickGroupNamed', { name: objectName }) : t('quickGroup')) +
        '</div>' +
        '<div class="iui-chips">' + quick.map(function (entry) {
          return '<button type="button" class="iui-chip iui-chip-quick" data-act="quick" data-intent="' +
            esc(entry.id) + '" title="' + esc(entry.description || entry.name) + '">✨ ' + esc(entry.name) +
            '</button>';
        }).join('') + '</div></div>';
    }
    html += '<div class="iui-search">' +
        '<input type="text" class="iui-search-input" autocomplete="off"' +
          ' placeholder="' + esc(t('searchPlaceholder')) + '" value="' + esc(st.query) + '" />' +
        '<button type="button" class="iui-search-clear" data-act="search-clear"' +
          (st.query ? '' : ' style="display:none"') + '>' + esc(t('clear')) + '</button>' +
      '</div>' +
      '<div class="iui-menu-results">' + this.renderMenuResults() + '</div>' +
      '</div>';
    return html;
  };

  /** 意图菜单主体：有输入 = 按口语说法匹配；无输入 = 当前页面装载的全量菜单。 */
  Panel.prototype.renderMenuResults = function () {
    var st = this.handle.state;
    var query = String(st.query || '').trim();
    if (query) {
      var matched = this.matchEntries(query);
      if (!matched.length) {
        return '<div class="iui-alert warn"><span class="iui-alert-ico">🔍</span><span>' +
          esc(t('matchNone')) + '</span></div>';
      }
      var self = this;
      return '<div class="iui-match-hint">' + esc(t('matchFound', { count: matched.length })) + '</div>' +
        '<div class="iui-btn-group">' + matched.map(function (item) {
          return self.renderEntry(item.entry, t('matchHit') + item.hit);
        }).join('') + '</div>';
    }
    if (!st.entries.length) {
      return '<div class="iui-alert warn"><span class="iui-alert-ico">⚠️</span><span>' +
        '<b>' + esc(t('emptyCatalog')) + '</b><br>' +
        '<span class="iui-alert-sub">' + esc(t('emptyCatalogDesc')) + '</span></span></div>';
    }
    var panel = this;
    return '<div class="iui-btn-group">' + st.entries.map(function (entry) {
      return panel.renderEntry(entry, null);
    }).join('') + '</div>';
  };

  /** 单个意图按钮（含动态提示徽标、远程标记与参数槽位入口）。 */
  Panel.prototype.renderEntry = function (entry, hint) {
    var st = this.handle.state;
    var gatewayUnavailable = st.catalog && st.catalog.gatewayStatus === 'UNAVAILABLE';
    var isRemote = entry.scope !== 'LOCAL';
    var disabled = isRemote && gatewayUnavailable;
    var title = disabled ? t('remoteDisabled') : (entry.description || entry.name);
    var hasSlots = entry.paramsSchema && entry.paramsSchema.properties &&
      Object.keys(entry.paramsSchema.properties).length > 0;
    return '<div class="iui-entry">' +
      '<button type="button" class="iui-btn" data-intent="' + esc(entry.id) + '"' + (disabled ? ' disabled' : '') +
        ' title="' + esc(title) + '">' +
        '<span class="iui-btn-ico">' + (isRemote ? '🔗' : '✨') + '</span>' +
        '<span class="iui-btn-body">' +
          '<span class="iui-btn-name-row">' +
            '<span class="iui-btn-name">' + esc(entry.name) + '</span>' +
            (entry.badge ? '<span class="iui-tag iui-tag-' + esc(LEVEL_COLOR[entry.badge.level] || 'info') + '">' +
              esc(entry.badge.text) + '</span>' : '') +
          '</span>' +
          '<span class="iui-btn-desc">' + esc(entry.description || '') + '</span>' +
          (hint ? '<span class="iui-btn-hit">' + esc(hint) + '</span>' : '') +
        '</span>' +
        (isRemote ? '<span class="iui-tag iui-tag-remote">' + esc(entry.targetSystem || t('remoteTag')) + '</span>' : '') +
      '</button>' +
      (hasSlots ? '<button type="button" class="iui-slot" data-slots="' + esc(entry.id) + '" title="' + esc(t('slots')) + '">⚙</button>' : '') +
      '</div>';
  };

  Panel.prototype.entry = function (intentId) {
    return (this.handle.state.allEntries || []).find(function (e) { return e.id === intentId; });
  };

  // ---- 口语匹配：别名 + 名称 + 描述，按相似度排序

  /** 一条意图的"可命中短语"：名称与别名在前（它们是用户会说的话），描述兜底。 */
  function matchPhrases(entry) {
    var phrases = [entry.name];
    (entry.aliases || []).forEach(function (alias) { phrases.push(alias); });
    if (entry.description) phrases.push(entry.description);
    return phrases.filter(Boolean);
  }

  /** 归一化：大小写 + 去掉空白与常见标点（口语输入常随手带标点）。 */
  function normalizeText(text) {
    return String(text == null ? '' : text).toLowerCase()
      .replace(/[\s，。？！、；：,?!.;:「」【】()（）"'']/g, '');
  }

  /** 相邻二字切片：中文没有空格可切词，bigram 是对短句最稳的近似。 */
  function bigrams(text) {
    var set = {};
    if (text.length <= 1) {
      if (text) set[text] = true;
      return set;
    }
    for (var i = 0; i < text.length - 1; i++) {
      set[text.substring(i, i + 2)] = true;
    }
    return set;
  }

  /** 口语里的"废话词"：去掉它们之后剩下的才是真意图（"这客户咋样了" → "客户"）。 */
  var FILLER_PHRASES = ['帮我看看', '麻烦看', '怎么样', '怎么办', '咋样', '咋办', '如何',
    '一下', '一个', '帮我', '帮忙', '麻烦', '给我', '我要', '我想', '看看', '请问', '能不能'];
  var FILLER_CHARS = /[这那了的吧呢啊呀吗嘛哦唉嘿额噢喔]/g;

  function stripFillers(text) {
    var out = String(text == null ? '' : text);
    FILLER_PHRASES.forEach(function (phrase) { out = out.split(phrase).join(''); });
    return out.replace(FILLER_CHARS, '');
  }

  /** 单次比对：包含关系加成 与 Dice 二元组重合率 取大（0~1）。 */
  function rawSimilarity(query, phrase) {
    var q = normalizeText(query);
    var p = normalizeText(phrase);
    if (!q || !p) return 0;
    if (q === p) return 1;
    var base = (p.indexOf(q) >= 0 || q.indexOf(p) >= 0) ? 0.85 : 0;
    var qa = bigrams(q);
    var pa = bigrams(p);
    var qKeys = Object.keys(qa);
    var hit = 0;
    qKeys.forEach(function (key) { if (pa[key]) hit++; });
    var dice = (2 * hit) / (qKeys.length + Object.keys(pa).length);
    return Math.max(base, dice);
  }

  /**
   * 相似度：原文比对 与 去废话词后比对 取大。
   * 两个通道各有各的强项——原文比对认"哪些钱没收回来"，去词后比对认"这客户咋样了"。
   */
  function similarity(query, phrase) {
    return Math.max(rawSimilarity(query, phrase), rawSimilarity(stripFillers(query), stripFillers(phrase)));
  }

  /**
   * 纯函数版口语匹配（也对外暴露为 IntentUI.match，宿主可复用同一口径做高亮/测试）。
   * 低于阈值的一律不给（宁可少给，也别把不相干的意图推给用户）。
   */
  function matchIntentEntries(entries, query) {
    var min = 0.34;
    var results = [];
    (entries || []).forEach(function (entry) {
      var best = 0;
      var bestPhrase = '';
      matchPhrases(entry).forEach(function (phrase) {
        var score = similarity(query, phrase);
        if (score > best) {
          best = score;
          bestPhrase = phrase;
        }
      });
      if (best >= min) {
        // nameScore 只在分数完全打平时用来分先后：意图名是"标准说法"，
        // 同分时名字沾得上边的更可能是用户想说的那件事
        results.push({ entry: entry, score: best, hit: bestPhrase,
          nameScore: similarity(query, entry.name) });
      }
    });
    return results.sort(function (a, b) {
      return b.score - a.score || b.nameScore - a.nameScore
        || String(a.entry.name).localeCompare(String(b.entry.name));
    });
  }

  Panel.prototype.matchEntries = function (query) {
    return matchIntentEntries(this.handle.state.entries, query);
  };

  /**
   * 当前对象快捷条：必填参数能被"页面上下文"补上的意图——就是详情页里点开即用的那几个。
   * 没有必填参数的全局意图不算（否则每个页面都挂同一排按钮，快捷条就失去意义）。
   */
  Panel.prototype.quickEntries = function () {
    var max = CONFIG.quickIntents;
    if (!max) return [];
    var ctx = this.context();
    var entries = (this.handle.state.entries || []).filter(function (entry) {
      var required = (entry.paramsSchema && entry.paramsSchema.required) || [];
      if (!required.length) return false;
      return required.every(function (key) {
        var value = ctx[key];
        return value !== undefined && value !== null && String(value).trim() !== '';
      });
    });
    return entries.slice(0, max);
  };

  /** 快捷条点击 = 用上下文里的对象直接执行（不再弹表单）。 */
  Panel.prototype.quickParams = function (entry) {
    var ctx = this.context();
    var params = {};
    ((entry.paramsSchema && entry.paramsSchema.required) || []).forEach(function (key) {
      if (ctx[key] !== undefined && ctx[key] !== null && String(ctx[key]).trim() !== '') {
        params[key] = String(ctx[key]);
      }
    });
    return params;
  };
  // ------------------------------------------------------------ 事件绑定

  Panel.prototype.bind = function () {
    var h = this.handle;
    this.bindEntries(h.scroll);
    // 匹配框：只换结果区，不整块重渲染——否则中文输入法正在拼字时输入框会被重建
    var self = this;
    var input = h.scroll.querySelector('.iui-search-input');
    if (input) {
      input.addEventListener('input', function () {
        h.state.query = input.value;
        var results = h.scroll.querySelector('.iui-menu-results');
        if (results) {
          results.innerHTML = self.renderMenuResults();
          self.bindEntries(results);
        }
        var clear = h.scroll.querySelector('.iui-search-clear');
        if (clear) clear.style.display = input.value ? '' : 'none';
      });
    }
  };

  Panel.prototype.bindEntries = function (root) {
    var self = this;
    var h = this.handle;
    root.querySelectorAll('.iui-btn[data-intent]').forEach(function (btn) {
      btn.addEventListener('click', function () {
        if (btn.disabled) return;
        var entry = self.entry(btn.getAttribute('data-intent'));
        if (!entry) return;
        if (h.options.mode === 'debug') openForm(h, entry, [], true);
        else self.run(entry, {});
      });
    });
    root.querySelectorAll('.iui-slot').forEach(function (btn) {
      btn.addEventListener('click', function () {
        var entry = self.entry(btn.getAttribute('data-slots'));
        if (entry) openForm(h, entry, [], true);
      });
    });
    root.querySelectorAll('[data-act]').forEach(function (el) {
      el.addEventListener('click', function () { self.onAction(el); });
    });
  };

  Panel.prototype.onAction = function (el) {
    var self = this;
    var h = this.handle;
    var st = h.state;
    var act = el.getAttribute('data-act');
    var result = st.result;

    if (act === 'collapse') {
      st.resultCollapsed = !st.resultCollapsed;
      this.render();
    } else if (act === 'copy') {
      var text = JSON.stringify((result && result.output) || {}, null, 2);
      if (navigator.clipboard) {
        navigator.clipboard.writeText(text).then(function () {
          el.textContent = t('copied');
          setTimeout(function () { el.textContent = t('copy'); }, 1500);
        });
      }
    } else if (act === 'rerun') {
      if (st.currentEntry) this.run(st.currentEntry, {});
    } else if (act === 'steps') {
      st.stepsOpen = !st.stepsOpen;
      this.render();
    } else if (act === 'good' || act === 'bad') {
      if (!result) return;
      api.feedback({
        traceId: result.traceId, intentId: result.intentId,
        rating: act === 'good' ? 'UP' : 'DOWN'
      }).then(function () {
        st.rated[result.traceId] = act === 'good' ? 'UP' : 'DOWN';
        self.render();
        toast(t('rateThanks'), 'ok');
      }).catch(function (e) { toast(t('feedbackFailed') + e.message, 'err'); });
    } else if (act === 'followup') {
      var list = (result && result.output && result.output.followups) || [];
      var text = list[Number(el.getAttribute('data-idx'))];
      var entry = st.currentEntry || this.entry(result.intentId);
      var props = (entry && entry.paramsSchema && entry.paramsSchema.properties) || {};
      var key = ['focus', 'note', 'question', 'goal'].find(function (k) { return props[k]; });
      if (!key) { toast(t('followupUnsupported')); return; }
      var params = {};
      params[key] = text;
      this.run(entry, params);
    } else if (act === 'next') {
      var n = this.nextEntries()[Number(el.getAttribute('data-idx'))];
      if (n) this.run(this.entry(n.intentId), n.params || {});
    } else if (act === 'toggle-group') {
      var id = el.getAttribute('data-intent');
      st.expandedGroups[id] = !st.expandedGroups[id];
      this.render();
    } else if (act === 'page') {
      st.todoPages[el.getAttribute('data-intent')] = Number(el.getAttribute('data-page'));
      this.render();
    } else if (act === 'run-suggestion') {
      var gid = el.getAttribute('data-group');
      var group = this.suggestionGroups().find(function (g) { return g.intentId === gid; });
      if (!group) return;
      var s = group.items[Number(el.getAttribute('data-idx'))];
      if (s) this.run(this.entry(s.intentId), s.params || {});
    } else if (act === 'quick') {
      var quickEntry = this.entry(el.getAttribute('data-intent'));
      if (quickEntry) this.run(quickEntry, this.quickParams(quickEntry));
    } else if (act === 'search-clear') {
      st.query = '';
      this.render();
      var box = h.scroll.querySelector('.iui-search-input');
      if (box) box.focus();
    }
  };

  // ------------------------------------------------------------ 执行

  Panel.prototype.run = function (entry, params) {
    var self = this;
    var h = this.handle;
    var st = h.state;
    if (!entry) return Promise.resolve();
    st.running = true;
    st.runningIntent = entry.id;
    st.runningText = t('executing', { name: entry.name });
    st.error = null;
    st.currentEntry = entry;
    this.render();
    return api.execute({
      intentId: entry.id,
      params: params || {},
      context: this.context()
    }).then(function (res) {
      st.running = false;
      st.runningIntent = null;
      if (res.status === 'NEED_INPUT') {
        st.result = null;
        self.render();
        openForm(h, entry, res.missingParams || [], false);
        return;
      }
      st.result = res;
      st.resultCollapsed = false;
      st.stepsOpen = false;
      self.render();
      notify(h, 'executed', res);
      if (res.status === 'SUCCESS') toast(t('done', { name: entry.name }), 'ok');
      else if (res.error) toast(res.error.message || t('failed'), 'err');
    }).catch(function (e) {
      st.running = false;
      st.runningIntent = null;
      self.render();
      toast(t('callFailed') + (e && e.message ? e.message : e), 'err');
    });
  };

  // ------------------------------------------------------------ 表单（缺参补全 / 槽位填写）

  function paramTitle(entry, key) {
    var props = (entry && entry.paramsSchema && entry.paramsSchema.properties) || {};
    return (props[key] && props[key].title) || key;
  }

  /** 实体/枚举槽位的候选值：宿主经 configure.entityOptions 注入 */
  function optionsOf(h, entry, key) {
    if (h.state.entityCache[key]) return h.state.entityCache[key];
    var src = CONFIG.entityOptions && CONFIG.entityOptions[key];
    if (!src) {
      var p = (entry.paramsSchema && entry.paramsSchema.properties && entry.paramsSchema.properties[key]) || {};
      if (p.enum) {
        h.state.entityCache[key] = p.enum.map(function (v) { return { label: v, value: v }; });
        return h.state.entityCache[key];
      }
      return null;
    }
    var val = typeof src === 'function' ? src() : src;
    if (val && typeof val.then === 'function') {
      h.state.entityCache[key] = [];
      val.then(function (list) {
        h.state.entityCache[key] = normalizeOptions(list);
        var dl = document.querySelector('.iui-mask [data-datalist="' + key + '"]');
        if (dl) {
          dl.innerHTML = h.state.entityCache[key].map(function (o) {
            return '<option value="' + esc(o.label) + '"></option>';
          }).join('');
        }
      }).catch(function () { h.state.entityCache[key] = []; });
      return h.state.entityCache[key];
    }
    h.state.entityCache[key] = normalizeOptions(val);
    return h.state.entityCache[key];
  }

  function normalizeOptions(list) {
    return (list || []).map(function (o) {
      if (o && o.label !== undefined) return { label: String(o.label), value: String(o.value) };
      return { label: String(o), value: String(o) };
    });
  }

  function openForm(h, entry, missing, showAll) {
    var schema = (entry && entry.paramsSchema) || {};
    var props = schema.properties || {};
    var required = schema.required || [];
    var keys = showAll ? Object.keys(props) : (missing.length ? missing : Object.keys(props));
    if (!keys.length) return;

    var overlay = document.createElement('div');
    overlay.className = 'iui-mask';
    applyZIndex(overlay);
    var fields = keys.map(function (key) {
      var p = props[key] || {};
      var req = required.indexOf(key) >= 0;
      var opts = optionsOf(h, entry, key);
      var datalistId = 'iui-dl-' + key;
      var placeholder = p.description || (p.title ? t('pleaseInputWith', { title: p.title }) : t('pleaseInput'));
      return '<label class="iui-field">' +
        '<span class="iui-field-label">' + esc(p.title || key) + (req ? '<span class="iui-req">*</span>' : '') + '</span>' +
        '<input class="iui-input" name="' + esc(key) + '" placeholder="' + esc(placeholder) + '"' +
        (opts ? ' list="' + datalistId + '" autocomplete="off"' : '') + '>' +
        (opts ? '<datalist class="iui-datalist" data-datalist="' + esc(key) + '" id="' + datalistId + '">' +
          opts.map(function (o) { return '<option value="' + esc(o.label) + '"></option>'; }).join('') + '</datalist>' : '') +
        '</label>';
    }).join('');

    overlay.innerHTML =
      '<div class="iui-modal">' +
        '<div class="iui-modal-head">' +
          '<div class="iui-modal-title">' + esc(showAll ? t('formTitleSlots') + ' — ' + (entry.name || entry.id) : t('formTitle') + '「' + (entry.name || entry.id) + '」') + '</div>' +
          (showAll ? '' : '<div class="iui-modal-desc">' + esc(t('formDesc', { name: entry.name || entry.id })) + '</div>') +
        '</div>' +
        '<div class="iui-modal-body">' +
          '<div class="iui-form-err"></div>' + fields +
        '</div>' +
        '<div class="iui-modal-foot">' +
          '<button type="button" class="iui-btn-ghost" data-act="cancel">' + esc(t('cancel')) + '</button>' +
          '<button type="button" class="iui-btn-primary" data-act="ok">' + esc(t('confirm')) + '</button>' +
        '</div>' +
      '</div>';

    function close() {
      overlay.remove();
      document.removeEventListener('keydown', onKey);
    }
    function onKey(e) { if (e.key === 'Escape') close(); }
    function collect() {
      var values = {};
      overlay.querySelectorAll('.iui-input').forEach(function (input) {
        var key = input.getAttribute('name');
        var raw = input.value;
        var opts = h.state.entityCache[key];
        values[key] = raw;
        if (opts && opts.length) {
          var hit = opts.find(function (o) { return o.label === raw; });
          if (hit) values[key] = hit.value;
        }
      });
      return values;
    }
    function contextKeys() {
      var ctx = (typeof h.options.getContext === 'function' ? h.options.getContext() : h.options.context) || {};
      return Object.keys(ctx).filter(function (k) {
        return ctx[k] !== undefined && ctx[k] !== null && String(ctx[k]).trim() !== '';
      });
    }
    function submit() {
      var values = collect();
      var fromCtx = contextKeys();
      var missed = required.filter(function (k) {
        return keys.indexOf(k) >= 0 && fromCtx.indexOf(k) < 0 && !String(values[k] == null ? '' : values[k]).trim();
      });
      if (missed.length) {
        overlay.querySelector('.iui-form-err').textContent = t('fillRequired') + missed.join('、');
        return;
      }
      var ok = overlay.querySelector('[data-act="ok"]');
      ok.disabled = true;
      ok.textContent = t('running');
      api.execute({ intentId: entry.id, params: values, context: (typeof h.options.getContext === 'function' ? h.options.getContext() : h.options.context) || {} })
        .then(function (res) {
          ok.disabled = false;
          ok.textContent = t('confirm');
          if (res.status === 'NEED_INPUT') {
            overlay.querySelector('.iui-form-err').textContent = (res.error && res.error.message) || t('errMissingParams');
            return;
          }
          close();
          h.state.currentEntry = entry;
          h.state.result = res;
          h.state.resultCollapsed = false;
          h.state.stepsOpen = false;
          var panel = h.panel;
          if (panel) panel.render();
          notify(h, 'executed', res);
          if (res.status === 'SUCCESS') toast(t('doneGeneric'), 'ok');
          else if (res.error) toast(res.error.message || t('failed'), 'err');
        })
        .catch(function (e) {
          ok.disabled = false;
          ok.textContent = t('confirm');
          overlay.querySelector('.iui-form-err').textContent = t('callFailed') + (e && e.message ? e.message : e);
        });
    }

    overlay.querySelector('[data-act="cancel"]').addEventListener('click', close);
    overlay.querySelector('[data-act="ok"]').addEventListener('click', submit);
    overlay.addEventListener('click', function (e) { if (e.target === overlay) close(); });
    document.addEventListener('keydown', onKey);
    (document.body || document.documentElement).appendChild(overlay);
    var first = overlay.querySelector('input');
    if (first) first.focus();
  }

  // ------------------------------------------------------------ 执行历史

  function openHistory(h) {
    var mask = document.createElement('div');
    mask.className = 'iui-mask';
    applyZIndex(mask);
    mask.innerHTML = '<div class="iui-drawer">' +
      '<div class="iui-drawer-head"><span>' + esc(t('historyTitle')) + '</span>' +
      '<button type="button" class="iui-close" data-act="close">×</button></div>' +
      '<div class="iui-drawer-body">' + esc(t('analyzing')) + '</div></div>';
    function close() { mask.remove(); }
    mask.querySelector('[data-act="close"]').addEventListener('click', close);
    mask.addEventListener('click', function (e) { if (e.target === mask) close(); });
    (document.body || document.documentElement).appendChild(mask);
    var body = mask.querySelector('.iui-drawer-body');

    function listView(records) {
      if (!records.length) { body.innerHTML = '<div class="iui-empty">' + esc(t('emptyHistory')) + '</div>'; return; }
      body.innerHTML = '<div class="iui-hist-list">' + records.map(function (r) {
        var statusText = r.status === 'SUCCESS' ? t('statusSuccess') : (r.status === 'NEED_INPUT' ? t('statusNeedInput') : t('statusFailed'));
        var cls = r.status === 'SUCCESS' ? 'ok' : (r.status === 'NEED_INPUT' ? 'warn' : 'err');
        return '<div class="iui-hist-item" data-trace="' + esc(r.traceId) + '">' +
          '<div class="iui-hist-row"><span class="iui-hist-intent">' + esc(r.intentId) + '</span>' +
          '<span class="iui-tag iui-tag-' + cls + '">' + esc(statusText) + '</span></div>' +
          '<div class="iui-hist-sub">' + esc(r.createTime || '') + ' · ' + ((r.durationMs || 0) / 1000).toFixed(1) + 's</div>' +
          '</div>';
      }).join('') + '</div>';
      body.querySelectorAll('.iui-hist-item').forEach(function (item) {
        item.addEventListener('click', function () {
          api.trace(item.getAttribute('data-trace')).then(function (detail) {
            body.innerHTML = '<button type="button" class="iui-mini" data-act="back">' + esc(t('back')) + '</button>' +
              '<pre class="iui-pre">' + esc(JSON.stringify(detail, null, 2)) + '</pre>';
            body.querySelector('[data-act="back"]').addEventListener('click', function () { listView(records); });
          });
        });
      });
    }

    api.history(20).then(listView).catch(function (e) {
      body.innerHTML = '<div class="iui-empty">' + esc(t('historyFailed') + (e && e.message ? e.message : e)) + '</div>';
    });
  }
  // ------------------------------------------------------------ 内嵌面板挂载

  function applyZIndex(root) {
    var z = CONFIG.zIndex;
    if (z == null) return;
    root.style.setProperty('--iui-z-fab', String(z));
    root.style.setProperty('--iui-z-mask', String(z + 1));
    root.style.setProperty('--iui-z-drawer', String(z + 2));
    root.style.setProperty('--iui-z-modal', String(z + 3));
  }

  function mount(options) {
    var opts = options || {};
    var host = opts.container || document.body || document.documentElement;
    var handle = { options: opts, state: createState(), container: null, panel: null, page: opts.page || '' };
    var panel = new Panel(handle);
    handle.panel = panel;
    var root = panel.mountHtml();
    applyZIndex(root);
    host.appendChild(root);
    panel.render();
    panel.load();

    var apiHandle = {
      root: root,
      panelRef: panel,
      reload: function () { return panel.load(); },
      render: function () { panel.render(); },
      /** 清空运行态（内嵌面板换页时宿主调用） */
      reset: function () { return panel.reset(); },
      destroy: function () { root.remove(); }
    };
    return apiHandle;
  }

  // ------------------------------------------------------------ 悬浮入口（可拖动）+ 抽屉

  var FAB_SIZE = 48;
  var FAB_MARGIN = 8;
  var DRAG_THRESHOLD = 4;

  function mountFloating(options) {
    var opts = options || {};
    var host = opts.container || document.body || document.documentElement;
    var posKey = opts.storageKey || 'intent-fab-position';

    var fab = document.createElement('button');
    fab.type = 'button';
    fab.className = 'iui-fab';
    fab.title = t('fabTitle');
    fab.innerHTML = '<span class="iui-fab-text">AI</span><span class="iui-fab-dot"></span>';
    host.appendChild(fab);
    applyZIndex(fab);

    var mask = document.createElement('div');
    mask.className = 'iui-drawer-mask';
    var drawer = document.createElement('div');
    drawer.className = 'iui-drawer-panel';
    drawer.style.width = CONFIG.drawerWidth + 'px';
    drawer.innerHTML =
      '<div class="iui-drawer-head">' +
        '<span class="iui-drawer-title">' + esc(t('panelTitle')) + '</span>' +
        '<span class="iui-drawer-tools">' +
          '<a href="javascript:void 0" class="iui-drawer-hist">' + esc(t('history')) + '</a>' +
          '<button type="button" class="iui-close" aria-label="close">×</button>' +
        '</span>' +
      '</div>' +
      '<div class="iui-drawer-body"></div>';
    var wrap = document.createElement('div');
    wrap.className = 'iui-drawer-wrap';
    wrap.appendChild(mask);
    wrap.appendChild(drawer);
    applyZIndex(wrap);
    host.appendChild(wrap);

    var body = drawer.querySelector('.iui-drawer-body');
    var handle = { options: opts, state: createState(), container: body, panel: null, page: null };
    var panel = new Panel(handle);
    handle.panel = panel;
    var root = panel.mountHtml();
    root.classList.add('iui-in-drawer');
    // 抽屉头已提供「标题 / 执行历史 / 关闭」，内层面板头是重复的，隐藏掉
    var panelHead = root.querySelector('.iui-panel-head');
    if (panelHead) panelHead.style.display = 'none';
    body.appendChild(root);
    applyZIndex(root);
    panel.render();

    var open = false;
    function currentPage() {
      return (typeof opts.getPage === 'function' ? opts.getPage() : opts.page) || '';
    }
    function setOpen(next) {
      open = next;
      wrap.classList.toggle('on', open);
      fab.classList.toggle('iui-fab-hidden', open);
      if (open) {
        var page = currentPage();
        if (page !== handle.page) {
          handle.page = page;
          // 换页 = 换上下文：先把上一页的意图结果清掉，再拉新页目录。
          // 少了 reset 这一步，切菜单后重开面板会看到上一个页面的结果卡。
          panel.reset();
          panel.load();
        } else {
          panel.render();
        }
      }
    }
    drawer.querySelector('.iui-close').addEventListener('click', function () { setOpen(false); });
    mask.addEventListener('click', function () { setOpen(false); });
    drawer.querySelector('.iui-drawer-hist').addEventListener('click', function () { openHistory(handle); });
    document.addEventListener('keydown', function (e) { if (e.key === 'Escape' && open) setOpen(false); });

    // ---- 拖动
    var dragging = false, moved = false, startX = 0, startY = 0, originLeft = 0, originTop = 0;
    function clampPos(left, top) {
      var maxLeft = window.innerWidth - FAB_SIZE - FAB_MARGIN;
      var maxTop = window.innerHeight - FAB_SIZE - FAB_MARGIN;
      return {
        left: Math.min(Math.max(left, FAB_MARGIN), Math.max(FAB_MARGIN, maxLeft)),
        top: Math.min(Math.max(top, FAB_MARGIN), Math.max(FAB_MARGIN, maxTop))
      };
    }
    function persist(left, top) {
      try {
        if (left == null) localStorage.removeItem(posKey);
        else localStorage.setItem(posKey, JSON.stringify({ left: left, top: top }));
      } catch (e) { /* 隐私模式等：忽略 */ }
    }
    function restore() {
      try {
        var saved = JSON.parse(localStorage.getItem(posKey) || 'null');
        if (saved && typeof saved.left === 'number' && typeof saved.top === 'number') {
          var p = clampPos(saved.left, saved.top);
          fab.style.left = p.left + 'px';
          fab.style.top = p.top + 'px';
          fab.style.right = 'auto';
          fab.style.bottom = 'auto';
        }
      } catch (e) { /* 本地记录损坏：回退默认位置 */ }
    }
    restore();

    fab.addEventListener('pointerdown', function (e) {
      if (e.button !== 0) return;
      var rect = fab.getBoundingClientRect();
      dragging = true;
      moved = false;
      startX = e.clientX;
      startY = e.clientY;
      originLeft = rect.left;
      originTop = rect.top;
      fab.classList.add('iui-fab-dragging');
      if (fab.setPointerCapture) fab.setPointerCapture(e.pointerId);
    });
    fab.addEventListener('pointermove', function (e) {
      if (!dragging) return;
      var dx = e.clientX - startX;
      var dy = e.clientY - startY;
      if (!moved && Math.abs(dx) + Math.abs(dy) < DRAG_THRESHOLD) return;
      moved = true;
      var p = clampPos(originLeft + dx, originTop + dy);
      fab.style.left = p.left + 'px';
      fab.style.top = p.top + 'px';
      fab.style.right = 'auto';
      fab.style.bottom = 'auto';
    });
    fab.addEventListener('pointerup', function (e) {
      if (!dragging) return;
      dragging = false;
      fab.classList.remove('iui-fab-dragging');
      if (fab.releasePointerCapture) fab.releasePointerCapture(e.pointerId);
      if (moved) {
        var rect = fab.getBoundingClientRect();
        var p = clampPos(rect.left, rect.top);
        fab.style.left = p.left + 'px';
        fab.style.top = p.top + 'px';
        persist(p.left, p.top);
      } else {
        setOpen(!open);
      }
    });
    fab.addEventListener('pointercancel', function () { dragging = false; fab.classList.remove('iui-fab-dragging'); });

    return {
      root: wrap,
      fab: fab,
      open: function () { setOpen(true); },
      close: function () { setOpen(false); },
      isOpen: function () { return open; },
      reload: function () { handle.page = currentPage(); return panel.load(); },
      /** 清空运行态（切菜单/切页面时宿主调用；重开面板也会自动重置） */
      reset: function () { return panel.reset(); },
      /** 当前面板已装载的页面标识（宿主用它判断是否真的换了页面） */
      page: function () { return handle.page; },
      resetPosition: function () {
        fab.style.left = '';
        fab.style.top = '';
        fab.style.right = '';
        fab.style.bottom = '';
        persist(null);
      },
      destroy: function () { wrap.remove(); fab.remove(); }
    };
  }

  // ------------------------------------------------------------ 对外 API

  var IntentUI = {
    version: VERSION,
    configure: function (options) {
      Object.assign(CONFIG, options || {});
      return this;
    },
    config: function () { return Object.assign({}, CONFIG); },
    mount: mount,
    mountFloating: mountFloating,
    /** 口语匹配（纯函数）：返回 [{entry, score, hit}]，宿主可复用同一口径做高亮或自测 */
    match: matchIntentEntries,
    t: t,
    strings: STRINGS
  };

  global.IntentUI = IntentUI;
  if (typeof module !== 'undefined' && module.exports) module.exports = IntentUI;
})(typeof globalThis !== 'undefined' ? globalThis : (typeof window !== 'undefined' ? window : this));
