import type { VbenFormSchema } from '#/adapter/form';
import type { VxeTableGridOptions } from '#/adapter/vxe-table';
import type { SealApi } from '#/api/oa/seal/sealinfo';

import { getSimpleUserList } from '#/api/system/user';
import { DICT_TYPE } from '@vben/constants';
import { getDictOptions } from '@vben/hooks';
import { handleTree } from '@vben/utils';

import { getCompanyList } from '#/api/system/dept';

/** 新增/修改的表单 */
export function useFormSchema(): VbenFormSchema[] {
  return [
    {
      fieldName: 'id',
      component: 'Input',
      dependencies: {
        triggerFields: [''],
        show: () => false,
      },
    },
    {
      fieldName: 'companyId',
      label: '所属公司',
      component: 'ApiTreeSelect',
      componentProps: (values, formApi) => ({
        allowClear: true,
        api: async () => {
          const data = await getCompanyList();
          return handleTree(data);
        },
        labelField: 'name',
        valueField: 'id',
        childrenField: 'children',
        placeholder: '请选择公司',
        treeDefaultExpandAll: true,
        onChange: (value: any, option: any) => {
          if (value && option) {
            // 选择了公司，设置公司名称
            formApi.setFieldValue('companyName', option[0]);
          } else {
            // 清空选择，清空公司名称
            formApi.setFieldValue('companyName', '');
          }
        },
      }),
      rules: 'selectRequired',
    },
    {
      fieldName: 'companyName',
      label: '公司名称',
      component: 'Input',
      dependencies: {
        triggerFields: [''],
        show: () => false,
      },
    },
    {
      fieldName: 'sealNo',
      label: '印章编号',
      rules: 'required',
      component: 'Input',
      componentProps: {
        placeholder: '请输入印章编号',
      },
    },
    {
      fieldName: 'sealName',
      label: '印章名称',
      component: 'Input',
      componentProps: {
        placeholder: '请输入印章名称',
      },
    },
    {
      fieldName: 'status',
      label: '状态',
      component: 'Select',
      componentProps: {
        options: getDictOptions(DICT_TYPE.OA_SEAL_STATUS, 'number'),
        placeholder: '请选择状态',
      },
    },
    {
      fieldName: 'sealType',
      label: '印章类型',
      rules: 'required',
      component: 'Select',
      componentProps: {
        options: getDictOptions(DICT_TYPE.OA_SEAL_TYPE, 'number'),
        placeholder: '请选择印章类型',
      },
    },
    {
      fieldName: 'sealCls',
      label: '分类',
      rules: 'required',
      component: 'Select',
      componentProps: {
        options: getDictOptions(DICT_TYPE.OA_SEAL_CLS, 'number'),
        placeholder: '请选择分类',
      },
    },
    {
      fieldName: 'keeperId',
      label: '保管人',
      component: 'ApiSelect',
      componentProps: (values, formApi) => ({
        allowClear: true,
        api: async () => {
          const data = await getSimpleUserList();
          return data;
        },
        labelField: 'nickname',
        valueField: 'id',
        placeholder: '请选择保管人',
        showSearch: true,
        filterOption: (input: string, option: any) => {
          return option.nickname?.toLowerCase().includes(input.toLowerCase());
        },
        onChange: (value: any, option: any) => {
          if (value && option) {
            // 选择了保管人，设置保管人名称和部门信息
            formApi.setFieldValue('keeperName', option.label);
            formApi.setFieldValue('keeperDeptId', option.deptId);
            formApi.setFieldValue('keeperDeptName', option.deptName);
          } else {
            // 清空选择
            formApi.setFieldValue('keeperName', '');
            formApi.setFieldValue('keeperDeptId', '');
            formApi.setFieldValue('keeperDeptName', '');
          }
        },
      }),
    },
    {
      fieldName: 'keeperName',
      label: '保管人名称',
      component: 'Input',
      dependencies: {
        triggerFields: [''],
        show: () => false,
      },
    },
    {
      fieldName: 'keeperDeptId',
      label: '保管部门ID',
      component: 'Input',
      dependencies: {
        triggerFields: [''],
        show: () => false,
      },
    },
    {
      fieldName: 'keeperDeptName',
      label: '保管部门',
      component: 'Input',
      componentProps: {
        placeholder: '自动填充',
        disabled: true,
      },
    },
    {
      fieldName: 'purchaseDate',
      label: '购买日期',
      component: 'DatePicker',
      componentProps: {
        format: 'YYYY-MM-DD',
        valueFormat: 'YYYY-MM-DD',
      },
    },
    {
      fieldName: 'enableDate',
      label: '启用日期',
      component: 'DatePicker',
      componentProps: {
        format: 'YYYY-MM-DD',
        valueFormat: 'YYYY-MM-DD',
      },
    },
    {
      fieldName: 'disableDate',
      label: '停用日期',
      component: 'DatePicker',
      componentProps: {
        format: 'YYYY-MM-DD',
        valueFormat: 'YYYY-MM-DD',
      },
    },
    {
      fieldName: 'picUrl',
      label: '上传照片',
      component: 'ImageUpload',
    },
    {
      fieldName: 'sort',
      label: '显示顺序',
      component: 'Input',
      componentProps: {
        placeholder: '请输入显示顺序',
      },
    },
    {
      fieldName: 'remark',
      label: '备注',
      component: 'Input',
      // 备注字段占据整行
      formItemClass: 'col-span-2',
      componentProps: {
        placeholder: '请输入备注',
      },
    },
  ];
}

/** 列表的搜索表单 */
export function useGridFormSchema(): VbenFormSchema[] {
  return [
    {
      fieldName: 'companyId',
      label: '所属公司',
      component: 'ApiTreeSelect',
      componentProps: {
        allowClear: true,
        api: async () => {
          const data = await getCompanyList();
          return handleTree(data);
        },
        labelField: 'name',
        valueField: 'id',
        childrenField: 'children',
        placeholder: '请选择公司',
        treeDefaultExpandAll: true,
      },
    },
    {
      fieldName: 'sealNo',
      label: '印章编号',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入印章编号',
      },
    },
    {
      fieldName: 'sealName',
      label: '印章名称',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入印章名称',
      },
    },
    {
      fieldName: 'status',
      label: '状态',
      component: 'Select',
      componentProps: {
        allowClear: true,
        options: getDictOptions(DICT_TYPE.OA_SEAL_STATUS, 'number'),
        placeholder: '请选择状态',
      },
    },
    {
      fieldName: 'sealType',
      label: '印章类型',
      component: 'Select',
      componentProps: {
        allowClear: true,
        options: getDictOptions(DICT_TYPE.OA_SEAL_TYPE, 'number'),
        placeholder: '请选择印章类型',
      },
    },
    {
      fieldName: 'keeperName',
      label: '保管人',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入保管人名称',
      },
    },
    {
      fieldName: 'purchaseDate',
      label: '购买日期',
      component: 'RangePicker',
      componentProps: {
        allowClear: true,
        format: 'YYYY-MM-DD',
        valueFormat: 'YYYY-MM-DD',
      },
    },
    {
      fieldName: 'enableDate',
      label: '启用日期',
      component: 'RangePicker',
      componentProps: {
        allowClear: true,
        format: 'YYYY-MM-DD',
        valueFormat: 'YYYY-MM-DD',
      },
    },
    {
      fieldName: 'disableDate',
      label: '停用日期',
      component: 'RangePicker',
      componentProps: {
        allowClear: true,
        format: 'YYYY-MM-DD',
        valueFormat: 'YYYY-MM-DD',
      },
    },
  ];
}

/** 列表的字段 */
export function useGridColumns(): VxeTableGridOptions<SealApi.Seal>['columns'] {
  return [
    { type: 'checkbox', width: 40 },
    {
      field: 'companyId',
      title: '公司ID',
      minWidth: 120,
    },
    {
      field: 'companyName',
      title: '公司名称',
      minWidth: 120,
    },
    {
      field: 'sealNo',
      title: '印章编号',
      minWidth: 120,
    },
    {
      field: 'sealName',
      title: '印章名称',
      minWidth: 120,
    },
    {
      field: 'status',
      title: '状态',
      minWidth: 120,
      cellRender: {
        name: 'CellDict',
        props: { type: DICT_TYPE.OA_SEAL_STATUS },
      },
    },
    {
      field: 'picUrl',
      title: '印章照片',
      minWidth: 120,
      cellRender: {
        name: 'CellImage',
      },
    },
    {
      field: 'sealType',
      title: '印章类型',
      minWidth: 120,
      cellRender: {
        name: 'CellDict',
        props: { type: DICT_TYPE.OA_SEAL_TYPE },
      },
    },
    {
      field: 'sealCls',
      title: '分类',
      minWidth: 120,
      cellRender: {
        name: 'CellDict',
        props: { type: DICT_TYPE.OA_SEAL_CLS },
      },
    },
    {
      field: 'keeperName',
      title: '保管人',
      minWidth: 120,
    },
    {
      field: 'keeperDeptName',
      title: '保管部门',
      minWidth: 120,
    },
    {
      field: 'purchaseDate',
      title: '购买日期',
      minWidth: 120,
      formatter: 'formatDate',
    },
    {
      field: 'enableDate',
      title: '启用日期',
      minWidth: 120,
      formatter: 'formatDate',
    },
    {
      field: 'disableDate',
      title: '停用日期',
      minWidth: 120,
      formatter: 'formatDate',
    },
    {
      field: 'sort',
      title: '显示顺序',
      minWidth: 120,
    },
    {
      field: 'remark',
      title: '备注',
      minWidth: 120,
    },
    {
      field: 'createTime',
      title: '创建时间',
      minWidth: 120,
      formatter: 'formatDateTime',
    },
    {
      title: '操作',
      width: 200,
      fixed: 'right',
      slots: { default: 'actions' },
    },
  ];
}

