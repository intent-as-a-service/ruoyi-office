import type { Ref } from 'vue';

import type { VbenFormSchema } from '#/adapter/form';

import { message } from 'ant-design-vue';

/** 新增/修改的表单 */
export function useFormSchema(
  modalRef?: any,
  readonly?: Ref<boolean>,
  applyModalRef?: any,
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
    {
      fieldName: 'applyBill',
      label: '用车申请单',
      rules: 'required',
      component: 'HelpInput',
      componentProps: {
        placeholder: '请选择用车申请单',
        onClick: () => {
          applyModalRef?.value?.modalApi.open();
        },
      },
    },
    {
      fieldName: 'carNo',
      label: '车辆',
      rules: 'required',
      component: 'HelpInput',
      componentProps: {
        placeholder: '请选择车辆',
        bind: {
          readonly,
          onClick: () => {
            modalRef.value?.modalApi.open();
          },
        },
        onClick: () => {
          modalRef.value?.modalApi.open();
        },
      },
    },
    {
      fieldName: 'goTime',
      label: '实际出车时间',
      rules: 'required',
      component: 'DatePicker',
      componentProps: {
        showTime: true,
        format: 'YYYY-MM-DD HH:mm:ss',
        valueFormat: 'x',
        placeholder: '请选择实际出车时间',
      },
      dependencies: {
        triggerFields: ['returnTime'],
        trigger: (values, formApi) => {
          if (
            values.returnTime &&
            values.goTime &&
            values.returnTime <= values.goTime
          ) {
            message.error('实际出车时间不能晚于或等于实际回车时间');
            // 立即清空出车时间字段
            formApi?.setFieldValue('returnTime', undefined);
          }
        },
      },
    },
    {
      fieldName: 'returnTime',
      label: '实际回车时间',
      rules: 'required',
      component: 'DatePicker',
      componentProps: {
        showTime: true,
        format: 'YYYY-MM-DD HH:mm:ss',
        valueFormat: 'x',
        placeholder: '请选择实际回车时间',
      },
      dependencies: {
        triggerFields: ['goTime'],
        trigger: (values, formApi) => {
          if (
            values.goTime &&
            values.returnTime &&
            values.returnTime <= values.goTime
          ) {
            message.error('实际回车时间必须大于实际出车时间');
            // 立即清空回车时间字段
            formApi?.setFieldValue('goTime', undefined);
          }
        },
      },
    },
    {
      fieldName: 'goArea',
      label: '出车地点',
      rules: 'required',
      component: 'Input',
      componentProps: {
        placeholder: '请输入出车地点',
      },
    },
    {
      fieldName: 'returnArea',
      label: '回车地点',
      rules: 'required',
      component: 'Input',
      componentProps: {
        placeholder: '请输入回车地点',
      },
    },
    {
      fieldName: 'passenger',
      label: '随行人',
      component: 'Input',
      formItemClass: 'col-span-3',
      componentProps: {
        placeholder: '请输入随行人',
      },
    },
    {
      fieldName: 'cause',
      label: '用车事由',
      rules: 'required',
      component: 'Textarea',
      formItemClass: 'col-span-full', // 添加这行
      componentProps: {
        placeholder: '请输入用车事由',
      },
    },
    {
      fieldName: 'remark',
      label: '还车说明',
      component: 'Textarea',
      formItemClass: 'col-span-full', // 添加这行
      componentProps: {
        placeholder: '请输入还车说明',
      },
    },
  ];
}
