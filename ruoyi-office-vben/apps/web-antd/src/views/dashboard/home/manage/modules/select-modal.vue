<script lang="ts" setup>
import type { SystemHomePageApi } from '#/api/system/home';

import { onMounted, ref } from 'vue';

import { useVbenModal } from '@vben/common-ui';

import { message, Radio, RadioGroup } from 'ant-design-vue';

import { enableHomePage, getSimpleHomePageList } from '#/api/system/home';

const emit = defineEmits(['success']);

const homePageList = ref<SystemHomePageApi.HomePage[]>([]);
const selectedPageId = ref<number>();
const loading = ref(false);

/** 加载首页列表 */
async function loadHomePageList() {
  loading.value = true;
  try {
    homePageList.value = await getSimpleHomePageList();
  } finally {
    loading.value = false;
  }
}

const [Modal, modalApi] = useVbenModal({
  async onConfirm() {
    if (!selectedPageId.value) {
      message.warning('请选择一个首页');
      return;
    }
    modalApi.lock();
    try {
      await enableHomePage(selectedPageId.value);
      await modalApi.close();
      emit('success');
      message.success('启用首页成功');
    } finally {
      modalApi.unlock();
    }
  },
  async onOpenChange(isOpen: boolean) {
    if (!isOpen) {
      selectedPageId.value = undefined;
      return;
    }
    // 加载首页列表
    await loadHomePageList();
  },
});

onMounted(() => {
  loadHomePageList();
});
</script>

<template>
  <Modal class="w-[600px]" title="选择首页">
    <div class="mx-4 py-4">
      <RadioGroup v-model:value="selectedPageId">
        <div
          v-for="page in homePageList"
          :key="page.id"
          class="mb-4 rounded border p-4 hover:border-primary"
        >
          <Radio :value="page.id">
            <div class="ml-2">
              <div class="flex items-center">
                <span class="font-medium">{{ page.name }}</span>
                <span
                  v-if="page.isDefault"
                  class="ml-2 rounded bg-primary/10 px-2 py-0.5 text-xs text-primary"
                >
                  默认
                </span>
              </div>
              <div class="mt-1 text-sm text-gray-500">
                {{ page.description || '暂无描述' }}
              </div>
              <div class="mt-1 text-xs text-gray-400">
                编码: {{ page.code }}
              </div>
            </div>
          </Radio>
        </div>
      </RadioGroup>
      <div
        v-if="homePageList.length === 0 && !loading"
        class="py-8 text-center text-gray-400"
      >
        暂无可用首页
      </div>
    </div>
  </Modal>
</template>
