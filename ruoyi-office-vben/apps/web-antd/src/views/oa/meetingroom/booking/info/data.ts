import type { Ref } from 'vue';

import type { VbenFormSchema } from '#/adapter/form';

import { DICT_TYPE } from '@vben/constants';
import { getDictOptions } from '@vben/hooks';

import { message } from 'ant-design-vue';

import { getUserSelectList } from '#/api/system/user';

/** 新增/修改的表单 */
export function useFormSchema(
  modalRef?: any,
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
    {
      fieldName: 'roomName',
      label: '会议室',
      rules: 'required',
      component: 'HelpInput',
      componentProps: {
        placeholder: '请选择会议室',
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
      fieldName: 'roomId',
      label: '会议室ID',
      component: 'Input',
      dependencies: {
        triggerFields: [''],
        show: () => false,
      },
    },
    {
      fieldName: 'roomLocation',
      label: '会议室位置',
      component: 'Input',
      componentProps: {
        placeholder: '自动填充',
        disabled: true,
      },
    },
    {
      fieldName: 'roomType',
      label: '会议室类型',
      component: 'Input',
      dependencies: {
        triggerFields: [''],
        show: () => false,
      },
    },
    {
      fieldName: 'meetingTitle',
      label: '会议主题',
      rules: 'required',
      component: 'Input',
      formItemClass: 'col-span-2',
      componentProps: {
        placeholder: '请输入会议主题',
        maxlength: 200,
      },
    },
    {
      fieldName: 'meetingStartTime',
      label: '会议开始时间',
      rules: 'required',
      component: 'DatePicker',
      componentProps: {
        showTime: {
          format: 'HH:mm',
          minuteStep: 30, // 限制只能选择整点或半点
        },
        format: 'YYYY-MM-DD HH:mm',
        valueFormat: 'x', // 使用时间戳格式
        placeholder: '请选择会议开始时间',
        disabledDate: (current: any) => {
          // 禁用过去的日期
          return current && current < new Date(new Date().setHours(0, 0, 0, 0));
        },
      },
      dependencies: {
        triggerFields: ['meetingEndTime'],
        trigger: (values, formApi) => {
          if (
            values.meetingEndTime &&
            values.meetingStartTime &&
            values.meetingEndTime <= values.meetingStartTime
          ) {
            message.error('会议开始时间不能晚于或等于会议结束时间');
            formApi?.setFieldValue('meetingEndTime', undefined);
          }
        },
      },
    },
    {
      fieldName: 'meetingEndTime',
      label: '会议结束时间',
      rules: 'required',
      component: 'DatePicker',
      componentProps: {
        showTime: {
          format: 'HH:mm',
          minuteStep: 30, // 限制只能选择整点或半点
        },
        format: 'YYYY-MM-DD HH:mm',
        valueFormat: 'x', // 使用时间戳格式
        placeholder: '请选择会议结束时间',
        disabledDate: (current: any) => {
          // 禁用过去的日期
          return current && current < new Date(new Date().setHours(0, 0, 0, 0));
        },
      },
      dependencies: {
        triggerFields: ['meetingStartTime'],
        trigger: (values, formApi) => {
          if (
            values.meetingStartTime &&
            values.meetingEndTime &&
            values.meetingEndTime <= values.meetingStartTime
          ) {
            message.error('会议结束时间必须大于会议开始时间');
            formApi?.setFieldValue('meetingStartTime', undefined);
          }
        },
      },
    },
    {
      fieldName: 'moderatorId',
      label: '主持人',
      rules: 'required',
      component: 'ApiSelect',
      componentProps: (_values, formApi) => ({
        placeholder: '请选择主持人',
        api: getUserSelectList,
        labelField: 'nickname',
        valueField: 'id',
        immediate: true,
        onChange: (_value: any, option: any) => {
          if (option && formApi) {
            formApi.setFieldValue('moderatorName', option.label || '');
          }
        },
      }),
    },
    {
      fieldName: 'moderatorName',
      label: '主持人姓名',
      component: 'Input',
      dependencies: {
        triggerFields: [''],
        show: () => false,
      },
    },
    {
      fieldName: 'reminderType',
      label: '会议提醒',
      component: 'Select',
      componentProps: {
        options: getDictOptions(DICT_TYPE.OA_MEETING_REMINDER_TYPE, 'number'),
        placeholder: '请选择会议提醒方式',
      },
    },
    {
      fieldName: 'attendees',
      label: '参会人员',
      component: 'ApiSelect',
      formItemClass: 'col-span-2',
      componentProps: (_values, formApi) => ({
        mode: 'multiple',
        placeholder: '请选择参会人员',
        api: getUserSelectList,
        labelField: 'nickname',
        valueField: 'id',
        immediate: true,
        onChange: (_value: any, options: any) => {
          if (formApi) {
            if (options && Array.isArray(options)) {
              const names = options.map(
                (opt: any) => opt.label || opt.nickname || '',
              );
              formApi.setFieldValue('attendeeNames', names);
            } else {
              formApi.setFieldValue('attendeeNames', []);
            }
          }
        },
      }),
    },
    {
      fieldName: 'attendeeNames',
      label: '参会人员姓名',
      component: 'Input',
      dependencies: {
        triggerFields: [''],
        show: () => false,
      },
    },
    {
      fieldName: 'meetingRemark',
      label: '会议说明',
      component: 'Textarea',
      formItemClass: 'col-span-full',
      componentProps: {
        placeholder: '请输入会议备注信息（如会议议程、注意事项等）',
        maxlength: 500,
        showCount: true,
        rows: 3,
      },
    },
    {
      fieldName: 'remark',
      label: '申请备注',
      component: 'Textarea',
      formItemClass: 'col-span-full',
      componentProps: {
        placeholder: '请输入申请备注',
        maxlength: 500,
        showCount: true,
        rows: 3,
      },
    },
  ];
}
