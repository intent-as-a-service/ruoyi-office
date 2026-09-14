import type { Ref } from 'vue';

import type { VbenFormSchema } from '#/adapter/form';

import { DICT_TYPE } from '@vben/constants';
import { getDictOptions } from '@vben/hooks';

/** 新增/修改的表单 */
export function useFormSchema(
  employeeSelectModalRef?: any,
  readonly?: Ref<boolean>,
): VbenFormSchema[] {
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
      fieldName: 'billCode',
      label: '单据编号',
      component: 'Input',
      dependencies: {
        triggerFields: [''],
        show: () => false,
      },
    },
    // ========== 员工信息 ==========
    // 隐藏员工ID字段，仅用于提交
    {
      fieldName: 'employeeId',
      component: 'Input',
      dependencies: {
        triggerFields: [''],
        show: () => false,
      },
    },
    // 可见字段：员工名称 + 选择弹窗
    {
      fieldName: 'name',
      label: '员工',
      rules: 'required',
      component: 'HelpInput',
      componentProps: {
        placeholder: '请选择员工',
        bind: {
          readonly,
          onClick: () => {
            if (!readonly?.value) {
              employeeSelectModalRef?.value?.modalApi.open();
            }
          },
        },
        onClick: () => {
          if (!readonly?.value) {
            employeeSelectModalRef?.value?.modalApi.open();
          }
        },
      },
    },
    {
      fieldName: 'employeeNo',
      label: '员工工号',
      component: 'Input',
      componentProps: {
        placeholder: '员工工号',
        readonly: true,
        disabled: true,
      },
    },
    // 姓名已在“员工”字段展示，这里不再重复展示
    {
      fieldName: 'sex',
      label: '性别',
      component: 'Select',
      componentProps: {
        placeholder: '性别',
        options: getDictOptions(DICT_TYPE.SYSTEM_USER_SEX, 'number'),
        readonly: true,
        disabled: true,
      },
    },
    {
      fieldName: 'empDeptName',
      label: '所属部门',
      component: 'Input',
      componentProps: {
        placeholder: '所属部门',
        readonly: true,
        disabled: true,
      },
    },
    {
      fieldName: 'empDeptId',
      label: '部门ID',
      component: 'Input',
      dependencies: {
        triggerFields: [''],
        show: () => false,
      },
    },
    {
      fieldName: 'empCompanyName',
      label: '所属公司',
      component: 'Input',
      componentProps: {
        placeholder: '所属公司',
        readonly: true,
        disabled: true,
      },
    },
    {
      fieldName: 'empCompanyId',
      label: '公司ID',
      component: 'Input',
      dependencies: {
        triggerFields: [''],
        show: () => false,
      },
    },
    {
      fieldName: 'jobPost',
      label: '职位',
      component: 'Select',
      componentProps: {
        placeholder: '职位',
        options: getDictOptions(DICT_TYPE.HRM_JOB_POST),
        readonly: true,
        disabled: true,
      },
    },
    {
      fieldName: 'jobPosition',
      label: '职务',
      component: 'Select',
      componentProps: {
        placeholder: '职务',
        options: getDictOptions(DICT_TYPE.HRM_JOB_POSITION),
        readonly: true,
        disabled: true,
      },
    },
    {
      fieldName: 'employeeStatus',
      label: '当前人员状态',
      component: 'Select',
      componentProps: {
        placeholder: '当前人员状态',
        options: getDictOptions(DICT_TYPE.HRM_EMPLOYEE_STATUS, 'number'),
        readonly: true,
        disabled: true,
      },
    },
    {
      fieldName: 'entryDate',
      label: '入职日期',
      component: 'DatePicker',
      componentProps: {
        placeholder: '入职日期',
        format: 'YYYY-MM-DD',
        valueFormat: 'YYYY-MM-DD',
        readonly: true,
        disabled: true,
      },
    },
    {
      fieldName: 'probationPeriod',
      label: '试用期（月）',
      component: 'Input',
      componentProps: {
        placeholder: '试用期（月）',
        readonly: true,
        disabled: true,
      },
    },
    // ========== 转正信息 ==========
    {
      fieldName: 'expectedFormalDate',
      label: '预计转正日期',
      component: 'DatePicker',
      componentProps: {
        placeholder: '预计转正日期',
        format: 'YYYY-MM-DD',
        valueFormat: 'YYYY-MM-DD',
        readonly: true,
        disabled: true,
      },
    },
    {
      fieldName: 'formalDate',
      label: '转正日期',
      rules: 'required',
      component: 'DatePicker',
      componentProps: {
        placeholder: '请选择转正日期',
        format: 'YYYY-MM-DD',
        valueFormat: 'YYYY-MM-DD',
      },
    },
    {
      fieldName: 'remark',
      label: '备注',
      component: 'Textarea',
      formItemClass: 'col-span-full',
      componentProps: {
        placeholder: '请输入备注',
      },
    },
  ];
}
