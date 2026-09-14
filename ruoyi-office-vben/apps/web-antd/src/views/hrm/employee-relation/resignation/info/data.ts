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
      fieldName: 'mobile',
      label: '手机号',
      component: 'Input',
      componentProps: {
        placeholder: '手机号',
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
      dependencies: {
        triggerFields: [''],
        show: () => false,
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
      dependencies: {
        triggerFields: [''],
        show: () => false,
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
  ];
}

/** 离职信息表单 */
export function useResignationFormSchema(
  handoverPersonSelectModalRef?: any,
  readonly?: Ref<boolean>,
): VbenFormSchema[] {
  return [
    {
      fieldName: 'resignationType',
      label: '离职类型',
      rules: 'required',
      component: 'Select',
      componentProps: {
        placeholder: '请选择离职类型',
        options: getDictOptions(DICT_TYPE.HRM_RESIGNATION_TYPE),
      },
    },
    {
      fieldName: 'applicationDate',
      label: '申请日期',
      rules: 'required',
      component: 'DatePicker',
      componentProps: {
        placeholder: '请选择申请日期',
        format: 'YYYY-MM-DD',
        valueFormat: 'YYYY-MM-DD',
      },
    },
    {
      fieldName: 'resignationDate',
      label: '离职日期',
      rules: 'required',
      component: 'DatePicker',
      componentProps: {
        placeholder: '请选择离职日期',
        format: 'YYYY-MM-DD',
        valueFormat: 'YYYY-MM-DD',
      },
    },
    {
      fieldName: 'lastWorkingDate',
      label: '最后工作日期',
      rules: 'required',
      component: 'DatePicker',
      componentProps: {
        placeholder: '请选择最后工作日期',
        format: 'YYYY-MM-DD',
        valueFormat: 'YYYY-MM-DD',
      },
    },
    // 隐藏工作交接人ID字段，仅用于提交
    {
      fieldName: 'handoverPersonId',
      component: 'Input',
      dependencies: {
        triggerFields: [''],
        show: () => false,
      },
    },
    // 可见字段：工作交接人名称 + 选择弹窗
    {
      fieldName: 'handoverPersonName',
      label: '工作交接人',
      component: 'HelpInput',
      componentProps: {
        placeholder: '请选择工作交接人',
        bind: {
          readonly,
          onClick: () => {
            if (!readonly?.value) {
              handoverPersonSelectModalRef?.value?.modalApi.open();
            }
          },
        },
        onClick: () => {
          if (!readonly?.value) {
            handoverPersonSelectModalRef?.value?.modalApi.open();
          }
        },
      },
    },
    {
      fieldName: 'resignationReason',
      label: '离职原因',
      rules: 'required',
      component: 'Select',
      componentProps: {
        placeholder: '请选择离职原因',
        options: getDictOptions(DICT_TYPE.HRM_RESIGNATION_REASON),
      },
    },
    {
      fieldName: 'resignationReasonDesc',
      label: '离职原因说明',
      component: 'Textarea',
      formItemClass: 'col-span-full',
      componentProps: {
        placeholder: '请输入离职原因说明',
        rows: 3,
      },
    },
    {
      fieldName: 'remark',
      label: '备注',
      component: 'Textarea',
      formItemClass: 'col-span-full',
      componentProps: {
        placeholder: '请输入备注',
        rows: 3,
      },
    },
  ];
}
