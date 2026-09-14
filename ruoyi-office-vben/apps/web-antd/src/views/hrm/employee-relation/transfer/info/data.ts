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

/** 调动信息表单 */
export function useTransferFormSchema(
  deptSelectModalRef?: any,
  readonly?: Ref<boolean>,
): VbenFormSchema[] {
  return [
    {
      fieldName: 'transferType',
      label: '异动类型',
      rules: 'required',
      component: 'Select',
      componentProps: {
        placeholder: '请选择异动类型',
        options: getDictOptions(DICT_TYPE.HRM_TRANSFER_TYPE),
      },
    },
    {
      fieldName: 'transferReason',
      label: '异动原因',
      component: 'Select',
      componentProps: {
        placeholder: '请选择异动原因',
        options: getDictOptions(DICT_TYPE.HRM_TRANSFER_REASON),
      },
    },
    {
      fieldName: 'originalJobPost',
      label: '原职位',
      component: 'Select',
      componentProps: {
        placeholder: '原职位',
        options: getDictOptions(DICT_TYPE.HRM_JOB_POST),
        readonly: true,
        disabled: true,
      },
    },
    {
      fieldName: 'newJobPost',
      label: '变更为职位',
      component: 'Select',
      componentProps: {
        placeholder: '请选择变更为职位',
        options: getDictOptions(DICT_TYPE.HRM_JOB_POST),
      },
    },
    {
      fieldName: 'originalJobPosition',
      label: '原职务',
      component: 'Select',
      componentProps: {
        placeholder: '原职务',
        options: getDictOptions(DICT_TYPE.HRM_JOB_POSITION),
        readonly: true,
        disabled: true,
      },
    },
    {
      fieldName: 'newJobPosition',
      label: '变更为职务',
      component: 'Select',
      componentProps: {
        placeholder: '请选择变更为职务',
        options: getDictOptions(DICT_TYPE.HRM_JOB_POSITION),
      },
    },
    {
      fieldName: 'originalCompanyName',
      label: '原公司',
      component: 'Input',
      componentProps: {
        placeholder: '原公司',
        readonly: true,
        disabled: true,
      },
    },
    {
      fieldName: 'originalCompanyId',
      label: '原公司ID',
      component: 'Input',
      dependencies: {
        triggerFields: [''],
        show: () => false,
      },
    },
    {
      fieldName: 'originalDeptName',
      label: '原部门',
      component: 'Input',
      componentProps: {
        placeholder: '原部门',
        readonly: true,
        disabled: true,
      },
    },
    {
      fieldName: 'originalDeptId',
      label: '原部门ID',
      component: 'Input',
      dependencies: {
        triggerFields: [''],
        show: () => false,
      },
    },
    {
      fieldName: 'newCompanyName',
      label: '变更为公司',
      component: 'Input',
      componentProps: {
        placeholder: '变更为公司',
        readonly: true,
        disabled: true,
      },
    },
    {
      fieldName: 'newCompanyId',
      label: '变更为公司ID',
      component: 'Input',
      dependencies: {
        triggerFields: [''],
        show: () => false,
      },
    },
    {
      fieldName: 'newDeptName',
      label: '变更为部门',
      rules: 'required',
      component: 'HelpInput',
      componentProps: {
        placeholder: '请选择变更为部门',
        bind: {
          readonly,
          onClick: () => {
            if (!readonly?.value && deptSelectModalRef?.value) {
              // 选择部门前清空公司与生效日期，确保由新部门决定公司
              deptSelectModalRef.value.modalApi.open();
            }
          },
        },
        onClick: () => {
          if (!readonly?.value && deptSelectModalRef?.value) {
            deptSelectModalRef.value.modalApi.open();
          }
        },
      },
    },
    {
      fieldName: 'newDeptId',
      label: '变更为部门ID',
      component: 'Input',
      dependencies: {
        triggerFields: [''],
        show: () => false,
      },
    },
    {
      fieldName: 'effectiveImmediately',
      label: '是否立即生效',
      component: 'RadioGroup',
      componentProps: {
        options: [
          { label: '是', value: true },
          { label: '否', value: false },
        ],
      },
    },
    {
      fieldName: 'effectiveDate',
      label: '生效日期',
      component: 'DatePicker',
      dependencies: {
        triggerFields: ['effectiveImmediately'],
        rules: (values) => (values.effectiveImmediately ? '' : 'required'),
      },
      componentProps: (values) => {
        const disabled = values.effectiveImmediately === true;
        return {
          placeholder: '请选择生效日期',
          format: 'YYYY-MM-DD',
          valueFormat: 'YYYY-MM-DD',
          disabled,
        };
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
