import type { Ref } from 'vue';

import type { VbenFormSchema } from '#/adapter/form';

import { h } from 'vue';

import { DICT_TYPE } from '@vben/constants';
import { getDictOptions } from '@vben/hooks';

import { Button, DatePicker, Input } from 'ant-design-vue';
import dayjs from 'dayjs';

import { z } from '#/adapter/form';

/** 新增/修改的表单 */
export function useFormSchema(
  _deptSelectModalRef?: any,
  _readonly?: Ref<boolean>,
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
    // ========== 员工基本信息 ==========
    {
      fieldName: 'name',
      label: '姓名',
      rules: 'required',
      component: 'Input',
      componentProps: {
        placeholder: '请输入姓名',
      },
    },
    {
      fieldName: 'sex',
      label: '性别',
      rules: 'required',
      component: 'Select',
      componentProps: {
        placeholder: '请选择性别',
        options: getDictOptions(DICT_TYPE.SYSTEM_USER_SEX, 'number'),
      },
    },
    {
      fieldName: 'mobile',
      label: '手机号码',
      rules: z
        .string()
        .min(1, '手机号不能为空')
        .regex(/^1[3-9]\d{9}$/, '请输入正确的手机号码'),
      component: 'Input',
      componentProps: {
        placeholder: '请输入手机号码',
        maxLength: 11,
      },
    },
    {
      fieldName: 'birthday',
      label: '出生日期',
      component: 'DatePicker',
      rules: 'required',
      componentProps: {
        placeholder: '请选择出生日期',
        format: 'YYYY-MM-DD',
        valueFormat: 'YYYY-MM-DD',
      },
    },
    {
      fieldName: 'idCard',
      label: '身份证号',
      rules: z
        .string()
        .min(1, '身份证号不能为空')
        .regex(
          /^[1-9]\d{5}(18|19|20)\d{2}(0[1-9]|1[0-2])(0[1-9]|[12]\d|3[01])\d{3}[0-9X]$/i,
          '请输入18位有效身份证号',
        ),
      component: 'Input',
      formItemClass: 'col-span-2',
      componentProps: {
        placeholder: '请输入身份证号',
      },
    },
    {
      fieldName: 'email',
      label: '邮箱',
      component: 'Input',
      componentProps: {
        placeholder: '请输入邮箱',
      },
    },
    {
      fieldName: 'nation',
      label: '民族',
      component: 'Select',
      componentProps: {
        placeholder: '请选择民族',
        options: getDictOptions(DICT_TYPE.HRM_NATION),
      },
    },
    {
      fieldName: 'politicalStatus',
      label: '政治面貌',
      component: 'Select',
      componentProps: {
        placeholder: '请选择政治面貌',
        options: getDictOptions(DICT_TYPE.HRM_POLITICAL_STATUS),
      },
    },
    {
      fieldName: 'maritalStatus',
      label: '婚姻状况',
      component: 'Select',
      componentProps: {
        placeholder: '请选择婚姻状况',
        options: getDictOptions(DICT_TYPE.HRM_MARITAL_STATUS),
      },
    },
    {
      fieldName: 'education',
      label: '文化程度',
      component: 'Select',
      componentProps: {
        placeholder: '请选择文化程度',
        options: getDictOptions(DICT_TYPE.HRM_EDUCATION),
      },
    },
    {
      fieldName: 'nativePlace',
      label: '籍贯',
      component: 'Input',
      componentProps: {
        placeholder: '请输入籍贯',
      },
    },
    {
      fieldName: 'currentAddress',
      label: '现居住地',
      component: 'Input',
      formItemClass: 'col-span-2',
      componentProps: {
        placeholder: '请输入现居住地',
      },
    },
    {
      fieldName: 'householdAddress',
      label: '户口所在地',
      component: 'Input',
      formItemClass: 'col-span-2',
      componentProps: {
        placeholder: '请输入户口所在地',
      },
    },
    {
      fieldName: 'emergencyContact',
      label: '紧急联系人',
      component: 'Input',
      componentProps: {
        placeholder: '请输入紧急联系人',
      },
    },
    {
      fieldName: 'emergencyPhone',
      label: '紧急联系人电话',
      component: 'Input',
      componentProps: {
        placeholder: '请输入紧急联系人电话',
      },
    },
    {
      fieldName: 'avatar',
      label: '头像',
      component: 'ImageUpload',
      componentProps: {
        contentText: '上传员工照片',
        showDescription: false,
        maxNumber: 1,
      },
    },
  ];
}

/**
 * 工作信息表单配置
 */
export function useWorkFormSchema(
  deptSelectModalRef?: any,
  readonly?: Ref<boolean>,
): VbenFormSchema[] {
  return [
    {
      fieldName: 'bankName',
      label: '工资开户行',
      component: 'Input',
      componentProps: {
        placeholder: '请输入工资开户行',
      },
    },
    {
      fieldName: 'bankAccount',
      label: '工资卡账户',
      component: 'Input',
      componentProps: {
        placeholder: '请输入工资卡账户',
      },
    },
    {
      fieldName: 'jobPost',
      label: '职位',
      component: 'Select',
      componentProps: {
        placeholder: '请选择职位',
        options: getDictOptions(DICT_TYPE.HRM_JOB_POST),
      },
    },
    {
      fieldName: 'jobPosition',
      label: '职务',
      component: 'Select',
      componentProps: {
        placeholder: '请选择职务',
        options: getDictOptions(DICT_TYPE.HRM_JOB_POSITION),
      },
    },
    {
      fieldName: 'employeeStatus',
      label: '人员状态',
      component: 'Select',
      componentProps: {
        placeholder: '请选择人员状态',
        options: getDictOptions(DICT_TYPE.HRM_EMPLOYEE_STATUS, 'number'),
      },
      rules: 'required',
    },
    {
      fieldName: 'probationPeriod',
      label: '试用期（月）',
      component: 'Input',
      componentProps: {
        placeholder: '请输入试用期',
      },
    },
    {
      fieldName: 'empDeptName',
      label: '所属部门',
      component: 'HelpInput',
      componentProps: {
        placeholder: '请选择所属部门',
        bind: {
          readonly,
          onClick: () => {
            if (!readonly?.value && deptSelectModalRef?.value) {
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
      fieldName: 'entryDate',
      label: '入职日期',
      component: 'DatePicker',
      componentProps: {
        placeholder: '请选择入职日期',
        format: 'YYYY-MM-DD',
        valueFormat: 'YYYY-MM-DD',
      },
    },
    {
      fieldName: 'expectedFormalDate',
      label: '预计转正日期',
      component: 'DatePicker',
      componentProps: {
        placeholder: '请选择预计转正日期',
        format: 'YYYY-MM-DD',
        valueFormat: 'YYYY-MM-DD',
      },
    },
    {
      fieldName: 'remark',
      label: '备注',
      component: 'Input',
      formItemClass: 'col-span-2',
      componentProps: {
        placeholder: '请输入备注',
      },
    },
  ];
}

/**
 * 工作经历列配置
 */
export function useWorkExperienceColumns(
  readonly: Ref<boolean>,
  handleDelete: (index: number) => void,
) {
  return [
    {
      title: '开始时间',
      dataIndex: 'startTime',
      width: 150,
      customRender: ({ text, record }: any) => {
        if (readonly.value) return text || '-';
        return h(DatePicker, {
          value: text ? dayjs(text) : null,
          format: 'YYYY-MM-DD',
          placeholder: '请选择开始时间',
          style: { width: '100%' },
          onChange: (date: any) => {
            if (record) {
              record.startTime = date
                ? dayjs(date).format('YYYY-MM-DD')
                : undefined;
            }
          },
        } as any);
      },
    },
    {
      title: '截止时间',
      dataIndex: 'endTime',
      width: 150,
      customRender: ({ text, record }: any) => {
        if (readonly.value) return text || '-';
        return h(DatePicker, {
          value: text ? dayjs(text) : null,
          format: 'YYYY-MM-DD',
          placeholder: '请选择截止时间',
          style: { width: '100%' },
          onChange: (date: any) => {
            if (record) {
              record.endTime = date
                ? dayjs(date).format('YYYY-MM-DD')
                : undefined;
            }
          },
        } as any);
      },
    },
    {
      title: '职务',
      dataIndex: 'jobPosition',
      width: 150,
      customRender: ({ text, record }: any) => {
        if (readonly.value) return text || '-';
        return h(Input, {
          value: text,
          placeholder: '请输入职务',
          onChange: (e: any) => {
            if (record) {
              record.jobPosition = e.target.value;
            }
          },
        } as any);
      },
    },
    {
      title: '单位名称',
      dataIndex: 'companyName',
      customRender: ({ text, record }: any) => {
        if (readonly.value) return text || '-';
        return h(Input, {
          value: text,
          placeholder: '请输入单位名称',
          onChange: (e: any) => {
            if (record) {
              record.companyName = e.target.value;
            }
          },
        } as any);
      },
    },
    {
      title: '操作',
      key: 'action',
      width: 100,
      customRender: ({ index }: any) => {
        if (readonly.value) return '-';
        return h(
          Button,
          {
            type: 'link',
            size: 'small',
            danger: true,
            onClick: () => handleDelete(index),
          },
          () => '删除',
        );
      },
    },
  ];
}

/**
 * 教育经历列配置
 */
export function useEducationColumns(
  readonly: Ref<boolean>,
  handleDelete: (index: number) => void,
) {
  return [
    {
      title: '开始时间',
      dataIndex: 'startTime',
      width: 150,
      customRender: ({ text, record }: any) => {
        if (readonly.value) return text || '-';
        return h(DatePicker, {
          value: text ? dayjs(text) : null,
          format: 'YYYY-MM-DD',
          placeholder: '请选择开始时间',
          style: { width: '100%' },
          onChange: (date: any) => {
            if (record) {
              record.startTime = date
                ? dayjs(date).format('YYYY-MM-DD')
                : undefined;
            }
          },
        } as any);
      },
    },
    {
      title: '截止时间',
      dataIndex: 'endTime',
      width: 150,
      customRender: ({ text, record }: any) => {
        if (readonly.value) return text || '-';
        return h(DatePicker, {
          value: text ? dayjs(text) : null,
          format: 'YYYY-MM-DD',
          placeholder: '请选择截止时间',
          style: { width: '100%' },
          onChange: (date: any) => {
            if (record) {
              record.endTime = date
                ? dayjs(date).format('YYYY-MM-DD')
                : undefined;
            }
          },
        } as any);
      },
    },
    {
      title: '专业',
      dataIndex: 'major',
      width: 150,
      customRender: ({ text, record }: any) => {
        if (readonly.value) return text || '-';
        return h(Input, {
          value: text,
          placeholder: '请输入专业',
          onChange: (e: any) => {
            if (record) {
              record.major = e.target.value;
            }
          },
        } as any);
      },
    },
    {
      title: '毕业院校',
      dataIndex: 'schoolName',
      customRender: ({ text, record }: any) => {
        if (readonly.value) return text || '-';
        return h(Input, {
          value: text,
          placeholder: '请输入毕业院校',
          onChange: (e: any) => {
            if (record) {
              record.schoolName = e.target.value;
            }
          },
        } as any);
      },
    },
    {
      title: '操作',
      key: 'action',
      width: 100,
      customRender: ({ index }: any) => {
        if (readonly.value) return '-';
        return h(
          Button,
          {
            type: 'link',
            size: 'small',
            danger: true,
            onClick: () => handleDelete(index),
          },
          () => '删除',
        );
      },
    },
  ];
}

/**
 * 家属信息列配置
 */
export function useFamilyColumns(
  readonly: Ref<boolean>,
  handleDelete: (index: number) => void,
) {
  return [
    {
      title: '姓名',
      dataIndex: 'name',
      width: 150,
      customRender: ({ text, record }: any) => {
        if (readonly.value) return text || '-';
        return h(Input, {
          value: text,
          placeholder: '请输入姓名',
          onChange: (e: any) => {
            if (record) {
              record.name = e.target.value;
            }
          },
        } as any);
      },
    },
    {
      title: '关系',
      dataIndex: 'relationship',
      width: 150,
      customRender: ({ text, record }: any) => {
        if (readonly.value) return text || '-';
        return h(Input, {
          value: text,
          placeholder: '请输入关系',
          onChange: (e: any) => {
            if (record) {
              record.relationship = e.target.value;
            }
          },
        } as any);
      },
    },
    {
      title: '联系方式',
      dataIndex: 'mobile',
      width: 150,
      customRender: ({ text, record }: any) => {
        if (readonly.value) return text || '-';
        return h(Input, {
          value: text,
          placeholder: '请输入联系方式',
          onChange: (e: any) => {
            if (record) {
              record.mobile = e.target.value;
            }
          },
        } as any);
      },
    },
    {
      title: '工作单位',
      dataIndex: 'workUnit',
      customRender: ({ text, record }: any) => {
        if (readonly.value) return text || '-';
        return h(Input, {
          value: text,
          placeholder: '请输入工作单位',
          onChange: (e: any) => {
            if (record) {
              record.workUnit = e.target.value;
            }
          },
        } as any);
      },
    },
    {
      title: '操作',
      key: 'action',
      width: 100,
      customRender: ({ index }: any) => {
        if (readonly.value) return '-';
        return h(
          Button,
          {
            type: 'link',
            size: 'small',
            danger: true,
            onClick: () => handleDelete(index),
          },
          () => '删除',
        );
      },
    },
  ];
}
