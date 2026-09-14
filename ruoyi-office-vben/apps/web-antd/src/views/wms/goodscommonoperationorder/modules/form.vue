<script lang="ts" setup>
import type { GoodsCommonOperationOrderApi } from '#/api/wms/goodscommonoperationorder';

import { useVbenModal } from '@vben/common-ui';
import { message, Tabs, Checkbox, Input, Textarea, Select,RadioGroup,CheckboxGroup, DatePicker } from 'ant-design-vue';
  import GoodsWarehousingDetailForm from './goods-warehousing-detail-form.vue'

import { computed, ref } from 'vue';
import { $t } from '#/locales';
import { useVbenForm } from '#/adapter/form';
import { getGoodsCommonOperationOrder, createGoodsCommonOperationOrder, updateGoodsCommonOperationOrder } from '#/api/wms/goodscommonoperationorder';

import { useFormSchema } from '../data';

const emit = defineEmits(['success']);
const formData = ref<GoodsCommonOperationOrderApi.GoodsCommonOperationOrder>();
const getTitle = computed(() => {
  return formData.value?.id
    ? $t('ui.actionTitle.edit', ['领用、退库、归还、借用、调拨主'])
    : $t('ui.actionTitle.create', ['领用、退库、归还、借用、调拨主']);
});


  /** 子表的表单 */
  const subTabsName = ref('goodsWarehousingDetail')
      const goodsWarehousingDetailFormRef = ref<InstanceType<typeof GoodsWarehousingDetailForm>>()

const [Form, formApi] = useVbenForm({
  commonConfig: {
    componentProps: {
      class: 'w-full',
    },
    formItemClass: 'col-span-2',
    labelWidth: 80,
  },
  layout: 'horizontal',
  schema: useFormSchema(),
  showDefaultActions: false
});

const [Modal, modalApi] = useVbenModal({
  async onConfirm() {
    const { valid } = await formApi.validate();
    if (!valid) {
      return;
    }
            // 校验子表单
                 modalApi.lock();
    // 提交表单
    const data = (await formApi.getValues()) as GoodsCommonOperationOrderApi.GoodsCommonOperationOrder;
            // 拼接子表的数据
            data.goodsWarehousingDetails = goodsWarehousingDetailFormRef.value?.getData();
    try {
      await (formData.value?.id ? updateGoodsCommonOperationOrder(data) : createGoodsCommonOperationOrder(data));
      // 关闭并提示
      await modalApi.close();
      emit('success');
      message.success( $t('ui.actionMessage.operationSuccess') );
    } finally {
      modalApi.unlock();
    }
  },
  async onOpenChange(isOpen: boolean) {
    if (!isOpen) {
      formData.value = undefined;
      return;
    }
    // 加载数据
    let data = modalApi.getData<GoodsCommonOperationOrderApi.GoodsCommonOperationOrder>();
    if (!data) {
      return;
    }
    if (data.id) {
      modalApi.lock();
      try {
        data = await getGoodsCommonOperationOrder(data.id);
      } finally {
        modalApi.unlock();
      }
    }
    // 设置到 values
    formData.value = data;
    await formApi.setValues(formData.value);
  },
});
</script>

<template>
  <Modal :title="getTitle">
    <Form class="mx-4" />
          <!-- 子表的表单 -->
      <Tabs v-model:active-key="subTabsName">
          <Tabs.TabPane key="goodsWarehousingDetail" tab="采购入库、领用、退库、归还、借用、调拨明细" force-render>
            <GoodsWarehousingDetailForm ref="goodsWarehousingDetailFormRef" :common-operation-id="formData?.id" />
          </Tabs.TabPane>
      </Tabs>
  </Modal>
</template>