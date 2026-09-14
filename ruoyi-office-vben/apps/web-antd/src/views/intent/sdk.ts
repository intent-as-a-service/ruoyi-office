import { useAccessStore } from '@vben/stores';

import { IntentUI } from 'intent-ui-sdk';
import 'intent-ui-sdk/css/intent-ui.css';

import { requestClient } from '#/api/request';

/**
 * 意图 SDK 的宿主注入点（幂等）。
 *
 * SDK 自身不认识 Vben：登录态、实体下拉、层级都要宿主给。
 * 悬浮入口与调试台都会调用这里，保证谁先挂载都不会漏注入（后者拿到同一份配置）。
 */
let configured = false;

/** 实体槽位的候选值：由宿主自己的接口提供，SDK 只负责渲染候选下拉 */
function entityOptions() {
  return {
    customerId: async () => {
      const list: any[] =
        (await requestClient.get('/crm/customer/simple-list')) || [];
      return list.map((c) => ({ label: c.name, value: String(c.id) }));
    },
    businessId: async () => {
      const list: any[] =
        (await requestClient.get('/crm/business/simple-all-list')) || [];
      return list.map((b) => ({ label: b.name, value: String(b.id) }));
    },
    contractId: async () => {
      const page: any = await requestClient.get('/crm/contract/page', {
        params: { pageNo: 1, pageSize: 100 },
      });
      return (page?.list || []).map((c: any) => ({
        label: `${c.name}（${c.no}）`,
        value: String(c.id),
      }));
    },
    clueId: async () => {
      const page: any = await requestClient.get('/crm/clue/page', {
        params: { pageNo: 1, pageSize: 100 },
      });
      return (page?.list || []).map((c: any) => ({
        label: c.name,
        value: String(c.id),
      }));
    },
    contactId: async () => {
      const list: any[] =
        (await requestClient.get('/crm/contact/simple-all-list')) || [];
      return list.map((c) => ({
        label: c.customerName ? `${c.name}（${c.customerName}）` : c.name,
        value: String(c.id),
      }));
    },
    receivableId: async () => {
      const page: any = await requestClient.get('/crm/receivable/page', {
        params: { pageNo: 1, pageSize: 100 },
      });
      return (page?.list || []).map((c: any) => ({
        label: `${c.no}（${c.price} 元）`,
        value: String(c.id),
      }));
    },
    productId: async () => {
      const list: any[] =
        (await requestClient.get('/crm/product/simple-list')) || [];
      return list.map((c) => ({ label: c.name, value: String(c.id) }));
    },
  };
}

/** 鉴权头每次请求现取：宿主刷新 token 后无需重新挂载 */
function authHeaders() {
  const accessStore = useAccessStore();
  const headers: Record<string, string> = {};
  if (accessStore.accessToken) {
    headers.Authorization = `Bearer ${accessStore.accessToken}`;
  }
  if (accessStore.tenantId !== null && accessStore.tenantId !== undefined) {
    headers['tenant-id'] = String(accessStore.tenantId);
  }
  return headers;
}

export function configureIntentUi() {
  if (configured) return IntentUI;
  configured = true;
  IntentUI.configure({
    apiPrefix: '/admin-api/intent',
    authHeaders,
    entityOptions: entityOptions(),
    // 底座层级：悬浮球 900 / 遮罩 901 / 抽屉 902 / 弹层 903
    // 压在页面内容之上、antd 弹窗（1000）之下
    locale: 'zh-CN',
    zIndex: 900,
  });
  return IntentUI;
}

export { IntentUI };
