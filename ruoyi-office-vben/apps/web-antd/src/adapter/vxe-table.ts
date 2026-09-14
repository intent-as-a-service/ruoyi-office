import type { VxeTableGridOptions } from '@vben/plugins/vxe-table';
import type { Recordable } from '@vben/types';

import { h } from 'vue';
import { useRouter } from 'vue-router';

import { IconifyIcon } from '@vben/icons';
import { $te } from '@vben/locales';
import {
  AsyncVxeColumn,
  AsyncVxeTable,
  createRequiredValidation,
  setupVbenVxeTable,
  useVbenVxeGrid,
} from '@vben/plugins/vxe-table';
import {
  erpCountInputFormatter,
  erpNumberFormatter,
  fenToYuan,
  formatFileSize,
  formatPast2,
  isFunction,
  isString,
} from '@vben/utils';

import {
  Button,
  Image,
  ImagePreviewGroup,
  Popconfirm,
  Switch,
  Tag,
} from 'ant-design-vue';

import { DictTag } from '#/components/dict-tag';
import { $t } from '#/locales';

import { useVbenForm } from './form';

setupVbenVxeTable({
  configVxeTable: (vxeUI) => {
    vxeUI.setConfig({
      grid: {
        align: 'center',
        border: false,
        columnConfig: {
          resizable: true,
        },
        minHeight: 180,
        formConfig: {
          // 全局禁用vxe-table的表单配置，使用formOptions
          enabled: false,
        },
        toolbarConfig: {
          import: false, // 是否导入
          export: false, // 是否导出
          refresh: true, // 是否刷新
          print: false, // 是否打印
          zoom: true, // 是否缩放
          custom: true, // 是否自定义配置
        },
        customConfig: {
          mode: 'modal',
        },
        proxyConfig: {
          autoLoad: true,
          response: {
            result: 'list',
            total: 'total',
          },
          showActiveMsg: true,
          showResponseMsg: false,
        },
        pagerConfig: {
          enabled: true,
        },
        sortConfig: {
          multiple: true,
        },
        round: true,
        showOverflow: true,
        size: 'small',
      } as VxeTableGridOptions,
    });

    // 表格配置项可以用 cellRender: { name: 'CellImage' },
    vxeUI.renderer.add('CellImage', {
      renderTableDefault(renderOpts, params) {
        const { props } = renderOpts;
        const { column, row } = params;
        return h(Image, { src: row[column.field], ...props });
      },
    });

    vxeUI.renderer.add('CellImages', {
      renderTableDefault(_renderOpts, params) {
        const { column, row } = params;
        if (column && column.field && row[column.field]) {
          return h(ImagePreviewGroup, {}, () => {
            return row[column.field].map((item: any) =>
              h(Image, { src: item }),
            );
          });
        }
        return '';
      },
    });

    // 表格配置项可以用 cellRender: { name: 'CellLink' },
    vxeUI.renderer.add('CellLink', {
      renderTableDefault(renderOpts) {
        const { props } = renderOpts;
        return h(
          Button,
          { size: 'small', type: 'link' },
          { default: () => props?.text },
        );
      },
    });

    // 表格配置项可以用 cellRender: { name: 'CellRouterLink', props: { path: '/path', field: 'fieldName', idField: 'id' } },
    // 推荐使用 createRouterLinkColumn() 辅助函数，自动设置默认宽度为180
    //
    // 支持的 props 属性：
    // - path/name: 路由路径或名称
    // - field: 显示字段
    // - idField: 单一ID字段（向后兼容）
    // - queryFields: 批量字段映射 [{ key, field }]
    // - fixedQuery: 固定查询参数对象
    // - variableQuery: 变量查询参数，支持函数动态计算
    // - query: 直接查询参数对象

    /**
     * CellRouterLink 使用示例：
     *
     * // 1. 基础用法（从行字段取值）
     * cellRender: {
     *   name: 'CellRouterLink',
     *   props: {
     *     name: 'BpmProcessInstanceDetail',
     *     queryFields: [
     *       { key: 'id', field: 'id' },
     *     ],
     *   },
     * },
     *
     * // 2. 添加固定参数
     * cellRender: {
     *   name: 'CellRouterLink',
     *   props: {
     *     name: 'BpmProcessInstanceDetail',
     *     queryFields: [
     *       { key: 'id', field: 'id' },
     *     ],
     *     fixedQuery: {
     *       tab: 'detail',
     *       source: 'list',
     *     },
     *   },
     * },
     *
     * // 3. 使用变量参数（函数动态计算）
     * cellRender: {
     *   name: 'CellRouterLink',
     *   props: {
     *     name: 'BpmProcessInstanceDetail',
     *     queryFields: [
     *       { key: 'id', field: 'id' },
     *     ],
     *     variableQuery: (row, column, params) => {
     *       return {
     *         timestamp: Date.now(),
     *         userId: getCurrentUserId(),
     *         tenantId: row.tenantId || 'default',
     *       };
     *     },
     *   },
     * },
     *
     * // 4. 直接传递查询参数对象（优先级最高）
     * cellRender: {
     *   name: 'CellRouterLink',
     *   props: {
     *     name: 'BpmProcessInstanceDetail',
     *     query: {
     *       id: 'fixed-id',
     *       mode: 'view',
     *       readonly: true,
     *     },
     *   },
     * },
     *
     * // 5. 混合使用（参数合并优先级：query > variableQuery > fixedQuery > queryFields > idField）
     * cellRender: {
     *   name: 'CellRouterLink',
     *   props: {
     *     name: 'BpmProcessInstanceDetail',
     *     queryFields: [
     *       { key: 'id', field: 'id' },
     *       { key: 'type', field: 'processDefinitionKey' },
     *     ],
     *     fixedQuery: {
     *       tab: 'detail',
     *       source: 'list',
     *     },
     *     variableQuery: (row, column, params) => {
     *       return {
     *         timestamp: Date.now(),
     *         status: row.status === 'active' ? 'running' : 'completed',
     *       };
     *     },
     *   },
     * },
     */

    vxeUI.renderer.add('CellRouterLink', {
      renderTableDefault(renderOpts, params) {
        const { props } = renderOpts;
        const { column, row } = params;
        const router = useRouter();

        const getValueByPath = (obj: any, path: string) => {
          if (!path) return undefined;
          const keys = path.split('.');
          let current: any = obj;
          for (const key of keys) {
            if (current === null) return undefined;
            current = current[key];
          }
          return current;
        };

        if (!props?.path && !props?.name) {
          console.warn('CellRouterLink: 需要提供 path 或 name 属性');
          return row[column.field];
        }

        const handleClick = () => {
          const routeConfig: any = {};

          if (props.name) {
            routeConfig.name = props.name;
          } else if (props.path) {
            routeConfig.path = props.path;
          }

          // 初始化 query 对象
          routeConfig.query = {};

          // 1. 基础单一 id 传参（向后兼容）
          if (props.idField) {
            const idVal =
              getValueByPath(row, props.idField) ?? row[props.idField];
            if (idVal !== undefined) {
              routeConfig.query[props.queryParam || 'id'] = idVal;
            }
          }

          // 2. 批量 query 参数：[{ key, field }]
          if (Array.isArray(props.queryFields)) {
            props.queryFields.forEach((q: any) => {
              const val = getValueByPath(row, q.field) ?? row[q.field];
              if (val !== undefined) {
                routeConfig.query[q.key] = val;
              }
            });
          }

          // 3. 固定查询参数（优先级：中等）
          if (props.fixedQuery && typeof props.fixedQuery === 'object') {
            Object.assign(routeConfig.query, props.fixedQuery);
          }

          // 4. 变量查询参数，支持函数动态计算（优先级：高）
          if (props.variableQuery) {
            if (typeof props.variableQuery === 'function') {
              const variableParams = props.variableQuery(row, column, params);
              if (variableParams && typeof variableParams === 'object') {
                Object.assign(routeConfig.query, variableParams);
              }
            } else if (typeof props.variableQuery === 'object') {
              Object.assign(routeConfig.query, props.variableQuery);
            }
          }

          // 5. 直接查询参数对象（优先级：最高）
          if (props.query && typeof props.query === 'object') {
            Object.assign(routeConfig.query, props.query);
          }

          router.push(routeConfig);
        };

        const displayField = props.field || column.field;
        const displayText =
          getValueByPath(row, displayField) ?? row[displayField];

        return h(
          'span',
          {
            style: {
              cursor: 'pointer',
              userSelect: 'text',
              color: '#1890ff',
            },
            onClick: handleClick,
            title: '点击查看详情',
          },
          displayText,
        );
      },
    });

    // 表格配置项可以用 cellRender: { name: 'CellTag' },
    vxeUI.renderer.add('CellTag', {
      renderTableDefault(renderOpts, params) {
        const { props } = renderOpts;
        const { column, row } = params;
        return h(Tag, { color: props?.color }, () => row[column.field]);
      },
    });

    vxeUI.renderer.add('CellTags', {
      renderTableDefault(renderOpts, params) {
        const { props } = renderOpts;
        const { column, row } = params;
        if (!row[column.field] || row[column.field].length === 0) {
          return '';
        }
        return h(
          'div',
          { class: 'flex items-center justify-center' },
          {
            default: () =>
              row[column.field].map((item: any) =>
                h(Tag, { color: props?.color }, { default: () => item }),
              ),
          },
        );
      },
    });

    // 表格配置项可以用 cellRender: { name: 'CellDict', props:{dictType: ''} },
    vxeUI.renderer.add('CellDict', {
      renderTableDefault(renderOpts, params) {
        const { props } = renderOpts;
        const { column, row } = params;
        if (!props) {
          return '';
        }
        // 使用 DictTag 组件替代原来的实现
        return h(DictTag, {
          type: props.type,
          value: row[column.field]?.toString(),
        });
      },
    });

    // 表格配置项可以用 cellRender: { name: 'CellSwitch', props: { beforeChange: () => {} } },
    // add by 芋艿：from https://github.com/vbenjs/vue-vben-admin/blob/main/playground/src/adapter/vxe-table.ts#L97-L123
    vxeUI.renderer.add('CellSwitch', {
      renderTableDefault({ attrs, props }, { column, row }) {
        const loadingKey = `__loading_${column.field}`;
        const finallyProps = {
          checkedChildren: $t('common.enabled'),
          checkedValue: 1,
          unCheckedChildren: $t('common.disabled'),
          unCheckedValue: 0,
          ...props,
          checked: row[column.field],
          loading: row[loadingKey] ?? false,
          'onUpdate:checked': onChange,
        };

        async function onChange(newVal: any) {
          row[loadingKey] = true;
          try {
            const result = await attrs?.beforeChange?.(newVal, row);
            if (result !== false) {
              row[column.field] = newVal;
            }
          } finally {
            row[loadingKey] = false;
          }
        }

        return h(Switch, finallyProps);
      },
    });

    // 注册表格的操作按钮渲染器 cellRender: { name: 'CellOperation', options: ['edit', 'delete'] }
    // add by 芋艿：from https://github.com/vbenjs/vue-vben-admin/blob/main/playground/src/adapter/vxe-table.ts#L125-L255
    vxeUI.renderer.add('CellOperation', {
      renderTableDefault({ attrs, options, props }, { column, row }) {
        const defaultProps = { size: 'small', type: 'link', ...props };
        let align = 'end';
        switch (column.align) {
          case 'center': {
            align = 'center';
            break;
          }
          case 'left': {
            align = 'start';
            break;
          }
          default: {
            align = 'end';
            break;
          }
        }
        const presets: Recordable<Recordable<any>> = {
          delete: {
            danger: true,
            text: $t('common.delete'),
          },
          edit: {
            text: $t('common.edit'),
          },
        };
        const operations: Array<Recordable<any>> = (
          options || ['edit', 'delete']
        )
          .map((opt) => {
            if (isString(opt)) {
              return presets[opt]
                ? { code: opt, ...presets[opt], ...defaultProps }
                : {
                    code: opt,
                    text: $te(`common.${opt}`) ? $t(`common.${opt}`) : opt,
                    ...defaultProps,
                  };
            } else {
              return { ...defaultProps, ...presets[opt.code], ...opt };
            }
          })
          .map((opt) => {
            const optBtn: Recordable<any> = {};
            Object.keys(opt).forEach((key) => {
              optBtn[key] = isFunction(opt[key]) ? opt[key](row) : opt[key];
            });
            return optBtn;
          })
          .filter((opt) => opt.show !== false);

        function renderBtn(opt: Recordable<any>, listen = true) {
          return h(
            Button,
            {
              ...props,
              ...opt,
              icon: undefined,
              onClick: listen
                ? () =>
                    attrs?.onClick?.({
                      code: opt.code,
                      row,
                    })
                : undefined,
            },
            {
              default: () => {
                const content = [];
                if (opt.icon) {
                  content.push(
                    h(IconifyIcon, { class: 'size-5', icon: opt.icon }),
                  );
                }
                content.push(opt.text);
                return content;
              },
            },
          );
        }

        function renderConfirm(opt: Recordable<any>) {
          return h(
            Popconfirm,
            {
              getPopupContainer(el) {
                return el.closest('tbody') || document.body;
              },
              placement: 'topLeft',
              title: $t('ui.actionTitle.delete', [attrs?.nameTitle || '']),
              ...props,
              ...opt,
              icon: undefined,
              onConfirm: () => {
                attrs?.onClick?.({
                  code: opt.code,
                  row,
                });
              },
            },
            {
              default: () => renderBtn({ ...opt }, false),
              description: () =>
                h(
                  'div',
                  { class: 'truncate' },
                  $t('ui.actionMessage.deleteConfirm', [
                    row[attrs?.nameField || 'name'],
                  ]),
                ),
            },
          );
        }

        const btns = operations.map((opt) =>
          opt.code === 'delete' ? renderConfirm(opt) : renderBtn(opt),
        );
        return h(
          'div',
          {
            class: 'flex table-operations',
            style: { justifyContent: align },
          },
          btns,
        );
      },
    });

    // 这里可以自行扩展 vxe-table 的全局配置，比如自定义格式化
    // vxeUI.formats.add

    vxeUI.formats.add('formatPast2', {
      tableCellFormatMethod({ cellValue }) {
        return formatPast2(cellValue);
      },
    });

    // add by 星语：数量格式化，保留 3 位
    vxeUI.formats.add('formatAmount3', {
      tableCellFormatMethod({ cellValue }) {
        return erpCountInputFormatter(cellValue);
      },
    });

    // 金额格式化（不带元后缀，用于通用金额显示）
    vxeUI.formats.add('formatAmount', {
      tableCellFormatMethod({ cellValue }) {
        if (cellValue === null || cellValue === undefined || cellValue === '') {
          return '';
        }
        const number = Number.parseFloat(cellValue);
        if (Number.isNaN(number)) {
          return cellValue;
        }
        // 保留两位小数并添加千分位分割
        return number.toLocaleString('zh-CN', {
          minimumFractionDigits: 2,
          maximumFractionDigits: 2,
        });
      },
    });

    // 金额格式化（带元后缀）
    vxeUI.formats.add('formatAmount2', {
      tableCellFormatMethod({ cellValue }, digits = 2) {
        return `${erpNumberFormatter(cellValue, digits)}`;
      },
    });
    vxeUI.formats.add('formatFenToYuanAmount', {
      tableCellFormatMethod({ cellValue }, digits = 2) {
        return `${erpNumberFormatter(fenToYuan(cellValue), digits)}`;
      },
    });

    // add by 星语：文件大小格式化
    vxeUI.formats.add('formatFileSize', {
      tableCellFormatMethod({ cellValue }, digits = 2) {
        return formatFileSize(cellValue, digits);
      },
    });
  },
  useVbenForm,
});

/**
 * 创建路由链接列配置的辅助函数
 * @param config 列配置选项
 * @param config.align 列对齐方式
 * @param config.field 字段名
 * @param config.headerAlign 表头对齐方式
 * @param config.idField ID字段名
 * @param config.minWidth 最小宽度
 * @param config.path 路由路径
 * @param config.queryParam 查询参数名
 * @param config.title 列标题
 * @param config.name 路由名称（可选，优先于 path）
 * @param config.queryFields 作为查询参数传递的键值来源映射 [{key, field}]
 * @returns 完整的列配置对象
 */
export function createRouterLinkColumn(config: {
  align?: 'center' | 'left' | 'right';
  field: string;
  headerAlign?: 'center' | 'left' | 'right';
  idField?: string;
  minWidth?: number;
  name?: string;
  path: string;
  queryFields?: Array<{ field: string; key: string }>;
  queryParam?: string;
  title: string;
}) {
  return {
    field: config.field,
    title: config.title,
    minWidth: config.minWidth ?? 160, // 默认宽度160
    headerAlign: config.headerAlign ?? 'center',
    align: config.align ?? 'center',
    cellRender: {
      name: 'CellRouterLink',
      props: {
        path: config.path,
        name: config.name,
        field: config.field,
        idField: config.idField,
        queryParam: config.queryParam,
        queryFields: config.queryFields,
      },
    },
  };
}

export { createRequiredValidation, useVbenVxeGrid };

export const [VxeTable, VxeColumn] = [AsyncVxeTable, AsyncVxeColumn];

export * from '#/components/table-action';

export type * from '@vben/plugins/vxe-table';
