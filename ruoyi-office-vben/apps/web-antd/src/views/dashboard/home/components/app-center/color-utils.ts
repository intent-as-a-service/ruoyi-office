// 应用中心颜色工具：根据名称或路径稳定映射到一组预设主题色
// 基于 vben 内置主题色，使用中等亮度（排除深色：zinc, neutral, slate, gray, deep-blue, deep-green）

const COLOR_PALETTE = [
  // 参考原型图的鲜艳颜色，使用更饱和、更丰富的色彩
  // 蓝色系（绩效考核、智慧人大）
  '#2563eb', // blue-600 - 鲜艳蓝色
  '#1d4ed8', // blue-700 - 深蓝色
  '#3b82f6', // blue-500 - 标准蓝色
  '#60a5fa', // blue-400 - 亮蓝色
  '#0ea5e9', // sky-500 - 天蓝色

  // 橙色系（培训考试、物品申领）
  '#ea580c', // orange-600 - 鲜艳橙色
  '#f97316', // orange-500 - 标准橙色
  '#fb923c', // orange-400 - 亮橙色
  '#f59e0b', // amber-500 - 琥珀色

  // 青色/蓝绿色系（会议管理）
  '#0891b2', // cyan-600 - 鲜艳青色
  '#06b6d4', // cyan-500 - 标准青色
  '#14b8a6', // teal-500 - 青绿色
  '#0d9488', // teal-600 - 深青绿色

  // 红色/珊瑚红系（信息发布、工资查询）
  '#dc2626', // red-600 - 鲜艳红色
  '#ef4444', // red-500 - 标准红色
  '#f43f5e', // rose-500 - 玫瑰红
  '#ec4899', // pink-500 - 樱花粉
  '#f472b6', // pink-400 - 浅粉色

  // 绿色系（出国境审批）
  '#16a34a', // green-600 - 鲜艳绿色
  '#22c55e', // green-500 - 标准绿色
  '#10b981', // emerald-500 - 翠绿色
  '#84cc16', // lime-500 - 酸橙绿

  // 紫色系
  '#9333ea', // purple-600 - 鲜艳紫色
  '#a855f7', // purple-500 - 标准紫色
  '#8b5cf6', // violet-500 - 紫罗兰
  '#c084fc', // purple-400 - 亮紫色

  // 其他鲜艳颜色
  '#d946ef', // fuchsia-500 - 紫红色
  '#6366f1', // indigo-500 - 靛蓝色
  '#fbbf24', // yellow-400 - 柠檬黄
  '#eab308', // yellow-500 - 金黄色
];

function hashString(str: string): number {
  let hash = 0;
  for (let i = 0; i < str.length; i++) {
    const codePoint = str.codePointAt(i);
    if (codePoint !== undefined) {
      hash = (hash * 31 + codePoint) >>> 0;
      // 如果 codePoint 超过 0xFFFF，需要跳过下一个代理对
      // eslint-disable-next-line no-bitwise
      if (codePoint > 0xffff) {
        i++;
      }
    }
  }
  return hash;
}

export function getColorByKey(key: null | string | undefined = ''): string {
  const palette = COLOR_PALETTE as string[];
  if (!key || palette.length === 0) {
    return palette[0] ?? '#3b82f6'; // 默认蓝色
  }
  const idx = hashString(key) % palette.length;
  return palette[idx] ?? '#3b82f6'; // 默认蓝色
}
