import type { VbenFormSchema } from '#/adapter/form';
import type { VxeGridProps } from '#/adapter/vxe-table';
import type { EmployeeArchiveApi } from '#/api/hrm/employee';

import { DICT_TYPE } from '@vben/constants';
import { getDictOptions } from '@vben/hooks';
import { handleTree } from '@vben/utils';

import dayjs from 'dayjs';

import { getEmployeeArchiveSelectPage } from '#/api/hrm/employee';
import { getDeptList } from '#/api/system/dept';

/** 员工选择组件配置 */
export interface EmployeeSelectConfig {
  /** 包含的人员状态列表（优先级高于excludeEmployeeStatusList） */
  includeEmployeeStatusList?: number[];
  /** 排除的人员状态列表 */
  excludeEmployeeStatusList?: number[];
  /** 是否在搜索表单中显示人员状态筛选 */
  showEmployeeStatusFilter?: boolean;
  /** 自定义表格列（可选） */
  customColumns?: VxeGridProps['columns'];
}

/** 选择弹窗 - 搜索条件 */
export function useEmployeeSelectFormSchema(
  config?: EmployeeSelectConfig,
): VbenFormSchema[] {
  const showEmployeeStatusFilter = config?.showEmployeeStatusFilter !== false;

  // 如果指定了包含状态列表，需要过滤下拉选项
  let statusOptions = getDictOptions(DICT_TYPE.HRM_EMPLOYEE_STATUS, 'number');
  const includeStatusList = config?.includeEmployeeStatusList;
  if (includeStatusList && includeStatusList.length > 0) {
    statusOptions = statusOptions.filter((item) =>
      includeStatusList.includes(item.value as number),
    );
  }

  return [
    {
      fieldName: 'deptId',
      label: '所属部门',
      component: 'ApiTreeSelect',
      componentProps: {
        allowClear: true,
        placeholder: '请选择所属部门',
        api: async () => {
          const data = await getDeptList();
          return handleTree(data, 'id');
        },
        fieldNames: {
          label: 'name',
          value: 'id',
        },
      },
    },
    {
      fieldName: 'employeeNo',
      label: '员工工号',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入员工工号',
      },
    },
    {
      fieldName: 'name',
      label: '员工姓名',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入员工姓名',
      },
    },
    ...(showEmployeeStatusFilter
      ? [
          {
            fieldName: 'employeeStatus',
            label: '人员状态',
            component: 'Select',
            componentProps: {
              allowClear: true,
              options: statusOptions,
              placeholder: '请选择人员状态',
            },
          } as VbenFormSchema,
        ]
      : []),
    {
      fieldName: 'jobPost',
      label: '职位',
      component: 'Select',
      componentProps: {
        allowClear: true,
        options: getDictOptions(DICT_TYPE.HRM_JOB_POST),
        placeholder: '请选择职位',
      },
    },
    {
      fieldName: 'jobPosition',
      label: '职务',
      component: 'Select',
      componentProps: {
        allowClear: true,
        options: getDictOptions(DICT_TYPE.HRM_JOB_POSITION),
        placeholder: '请选择职务',
      },
    },
  ];
}

/** 选择弹窗 - 表格列 */
export function useEmployeeSelectColumns(
  config?: EmployeeSelectConfig,
): VxeGridProps['columns'] {
  const baseColumns: VxeGridProps['columns'] = [
    {
      type: 'radio',
      width: 60,
      align: 'center',
    },
    {
      title: '员工工号',
      field: 'employeeNo',
      width: 120,
    },
    {
      title: '员工姓名',
      field: 'name',
      width: 120,
    },
    {
      title: '人员状态',
      field: 'employeeStatus',
      width: 110,
      cellRender: {
        name: 'CellDict',
        props: {
          type: DICT_TYPE.HRM_EMPLOYEE_STATUS,
        },
      },
    },
    {
      title: '所属部门',
      field: 'deptName',
      width: 160,
    },
    {
      title: '所属单位',
      field: 'companyName',
      width: 200,
    },
    {
      title: '职位',
      field: 'jobPost',
      width: 140,
      cellRender: {
        name: 'CellDict',
        props: {
          type: DICT_TYPE.HRM_JOB_POST,
        },
      },
    },
    {
      title: '职务',
      field: 'jobPosition',
      width: 140,
      cellRender: {
        name: 'CellDict',
        props: {
          type: DICT_TYPE.HRM_JOB_POSITION,
        },
      },
    },
    {
      title: '入职日期',
      field: 'entryDate',
      width: 130,
      formatter: ({ cellValue }) =>
        cellValue ? dayjs(cellValue).format('YYYY-MM-DD') : '',
    },
  ];

  // 如果有自定义列，使用自定义列，否则使用基础列
  if (config?.customColumns) {
    return config.customColumns;
  }

  return baseColumns;
}

/** 选择弹窗 - 查询方法 */
export async function queryEmployeeSelectPage(
  page: { currentPage: number; pageSize: number },
  formValues: Record<string, any>,
  config?: EmployeeSelectConfig,
) {
  const queryParams: EmployeeArchiveApi.EmployeeArchiveSelectReqVO = {
    pageNo: page.currentPage,
    pageSize: page.pageSize,
    ...formValues,
  };

  // 如果指定了包含状态列表，直接传递给后端（后端会处理）
  if (
    config?.includeEmployeeStatusList &&
    config.includeEmployeeStatusList.length > 0
  ) {
    queryParams.includeEmployeeStatusList = config.includeEmployeeStatusList;
  } else if (
    config?.excludeEmployeeStatusList &&
    config.excludeEmployeeStatusList.length > 0
  ) {
    // 如果指定了排除状态列表，传递给后端（后端会自动合并默认排除的6和7）
    queryParams.excludeEmployeeStatusList = config.excludeEmployeeStatusList;
  }
  // 如果没有指定任何列表，后端会默认排除离职（6）和退休（7）

  return await getEmployeeArchiveSelectPage(queryParams);
}
