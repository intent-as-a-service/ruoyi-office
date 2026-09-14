<script lang="ts" setup>
import type { PurchaseInWarehousingApi } from '#/api/wms/purchaseinwarehousing';

import { computed, ref } from 'vue';

import { useVbenModal } from '@vben/common-ui';

import { message, Tabs } from 'ant-design-vue';

import { useVbenForm } from '#/adapter/form';
import {
  createPurchaseInWarehousing,
  getPurchaseInWarehousing,
  updatePurchaseInWarehousing,
} from '#/api/wms/purchaseinwarehousing';
import { $t } from '#/locales';

import { useFormSchema } from '../list/data';
import GoodsWarehousingDetailForm from './goods-warehousing-detail-form.vue';

const emit = defineEmits(['success']);
const formData = ref<PurchaseInWarehousingApi.PurchaseInWarehousing>();
const getTitle = computed(() => {
  return formData.value?.id
    ? $t('ui.actionTitle.edit', ['采购入库'])
    : $t('ui.actionTitle.create', ['采购入库']);
});

/** 子表的表单 */
const subTabsName = ref('goodsWarehousingDetail');
const goodsWarehousingDetailFormRef =
  ref<InstanceType<typeof GoodsWarehousingDetailForm>>();

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
  showDefaultActions: false,
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
    const data =
      (await formApi.getValues()) as PurchaseInWarehousingApi.PurchaseInWarehousing;
    // 拼接子表的数据
    data.goodsWarehousingDetails =
      goodsWarehousingDetailFormRef.value?.getData();
    try {
      await (formData.value?.id
        ? updatePurchaseInWarehousing(data)
        : createPurchaseInWarehousing(data));
      // 关闭并提示
      await modalApi.close();
      emit('success');
      message.success($t('ui.actionMessage.operationSuccess'));
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
    let data =
      modalApi.getData<PurchaseInWarehousingApi.PurchaseInWarehousing>();
    if (!data) {
      return;
    }
    if (data.id) {
      modalApi.lock();
      try {
        data = await getPurchaseInWarehousing(data.id);
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
      <Tabs.TabPane
        key="goodsWarehousingDetail"
        tab="采购入库、领用、退库、归还、借用、调拨明细"
        force-render
      >
        <GoodsWarehousingDetailForm
          ref="goodsWarehousingDetailFormRef"
          :purchase-order-id="formData?.id"
        />
      </Tabs.TabPane>
    </Tabs>
  </Modal>
</template>
