import type { VbenFormSchema } from '#/adapter/form';
import type { VxeTableGridOptions } from '#/adapter/vxe-table';
import type { CarApi } from '#/api/oa/car/carinfo';

import { h } from 'vue';

import { Tag } from 'ant-design-vue';

import { DICT_TYPE } from '@vben/constants';
import { getDictOptions } from '@vben/hooks';

/** 车辆选择搜索表单配置 */
export function useCarSelectFormSchema(): VbenFormSchema[] {
  return [
    {
      fieldName: 'carNo',
      label: '车牌号',
      component: 'Input',
      componentProps: {
        placeholder: '请输入车牌号',
      },
    },
    {
      fieldName: 'brand',
      label: '品牌型号',
      component: 'Input',
      componentProps: {
        placeholder: '请输入品牌型号',
      },
    },
    {
      fieldName: 'carType',
      label: '车型',
      component: 'Select',
      componentProps: {
        placeholder: '请选择车型',
        options: getDictOptions(DICT_TYPE.OA_CAR_TYPE, 'number'),
      },
    },
    {
      fieldName: 'carCls',
      label: '车辆分类',
      component: 'Select',
      componentProps: {
        placeholder: '请选择车辆分类',
        options: getDictOptions(DICT_TYPE.OA_CAR_CLS, 'number'),
      },
    },
  ];
}

/** 车辆选择表格列配置 */
export function useCarSelectColumns(): VxeTableGridOptions<CarApi.Car>['columns'] {
  return [
    {
      type: 'radio',
      width: 60,
      align: 'center',
    },
    {
      field: 'carNo',
      title: '车牌号',
      width: 120,
      slots: {
        default: ({ row }: { row: CarApi.Car }) => {
          return h(Tag, { color: 'blue' }, { default: () => row.carNo });
        },
      },
    },
    {
      field: 'carName',
      title: '车辆名称',
      width: 200,
    },
    {
      field: 'brand',
      title: '品牌型号',
      width: 150,
    },
    {
      field: 'carCls',
      title: '车辆分类',
      width: 100,
      slots: {
        default: ({ row }: { row: CarApi.Car }) => {
          const clsMap: Record<number, { color: string; text: string }> = {
            1: { text: '轿车', color: 'blue' },
            2: { text: 'SUV', color: 'green' },
            3: { text: '商务车', color: 'orange' },
            4: { text: '货车', color: 'purple' },
          };
          const cls = clsMap[row.carCls!] || { text: '未知', color: 'default' };
          return h(Tag, { color: cls.color }, { default: () => cls.text });
        },
      },
    },
    {
      field: 'seatNum',
      title: '座位数',
      align: 'center',
    },
  ];
}
