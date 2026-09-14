import { defineOverridesPreferences } from '@vben/preferences';

/**
 * @description 项目配置文件
 * 只需要覆盖项目中的一部分配置，不需要的配置不用覆盖，会自动使用默认配置
 * !!! 更改配置后请清空缓存，否则可能不生效
 */
export const overridesPreferences = defineOverridesPreferences({
  // overrides
  app: {
    /** 后端路由模式 */
    accessMode: 'backend',
    name: import.meta.env.VITE_APP_TITLE,
    enableRefreshToken: true,
    layout: 'mixed-nav',
    /** 默认首页路径，改为我的首页 */
    defaultHomePath: '/intent/center',
  },
  // 自定义应用 Logo（将图片放在 apps/web-antd/public/static/imgs/ 目录下）
  /* eslint-disable */
  logo: {
    enable: true,
    fit: 'contain',
    // 使用 BASE_URL 动态获取 base 路径，支持部署在子路径下
    // 开发环境: /static/imgs/logo.png
    // 生产环境: /web/static/imgs/logo.png
    // 注意：BASE_URL 已包含末尾斜杠，所以不需要再加斜杠
    source: `${import.meta.env.BASE_URL}static/imgs/logo.png`,
  },
  /* eslint-enable */
  theme: {
    mode: 'light',
    semiDarkHeader: false,
  },
  sidebar: {
    autoActivateChild: true,
  },
  footer: {
    /** 默认关闭 footer 页脚，因为有一定遮挡 */
    enable: false,
    fixed: false,
  },
  copyright: {
    companyName: import.meta.env.VITE_APP_TITLE,
    companySiteLink: 'https://gitee.com/yudaocode/yudao-ui-admin-vben',
  },
  // 标签页配置 - 隐藏图标
  tabbar: {
    showIcon: false,
  },
  // 面包屑配置 - 隐藏图标
  breadcrumb: {
    showIcon: false,
  },
  navigation: {
    showIcon: true,
  },
});
