<script lang="ts" setup>
import type { PurchaseOrderApi } from '#/api/wms/purchaseorder';

import { useVbenModal } from '@vben/common-ui';
import { message } from 'ant-design-vue';
import PurchaseOrderDetailForm from './modules/purchase-order-detail-form.vue';

import { useVbenForm } from '#/adapter/form';
import { getPurchaseOrder, submitPurchaseOrder, updatePurchaseOrder } from '#/api/wms/purchaseorder';
import { $t } from '#/locales';
import { computed, ref } from 'vue';

import { useFormSchema } from './data';

const emit = defineEmits(['success']);
const formData = ref<PurchaseOrderApi.PurchaseOrder>();
const getTitle = computed(() => {
  return formData.value?.id
    ? $t('ui.actionTitle.edit', ['采购订单'])
    : $t('ui.actionTitle.create', ['采购订单']);
});


  /** 子表的表单 */
  const subTabsName = ref('purchaseOrderDetail')
      const purchaseOrderDetailFormRef = ref<InstanceType<typeof PurchaseOrderDetailForm>>()

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
    const data = (await formApi.getValues()) as PurchaseOrderApi.PurchaseOrder;
            // 拼接子表的数据
            data.purchaseOrderDetails = purchaseOrderDetailFormRef.value?.getData();
    try {
      await (formData.value?.id ? updatePurchaseOrder(data) : submitPurchaseOrder(data));
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
    let data = modalApi.getData<PurchaseOrderApi.PurchaseOrder>();
    if (!data) {
      return;
    }
    if (data.id) {
      modalApi.lock();
      try {
        data = await getPurchaseOrder(data.id);
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
       <!-- 子表的表单 
       <Tabs v-model:active-key="subTabsName">
        <Tabs.TabPane key="purchaseOrderDetail" tab="采购订单明细" force-render>
          <PurchaseOrderDetailForm ref="purchaseOrderDetailFormRef" :purchase-order-id="formData?.id" />
        </Tabs.TabPane>
    </Tabs>-->
  </Modal>
</template>
