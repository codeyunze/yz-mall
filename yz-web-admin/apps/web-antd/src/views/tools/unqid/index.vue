<script lang="ts" setup>
import type { UserInfo } from '@vben/types';

import { Page } from '@vben/common-ui';

import { Button, Card, message } from 'ant-design-vue';

import { useVbenForm } from '#/adapter/form';
import { getUnqidApi, getUserInfoApi } from '#/api';

const [BaseForm, baseFormApi] = useVbenForm({
  // 所有表单项共用，可单独在表单内覆盖
  commonConfig: {
    // 所有表单项
    componentProps: {
      class: 'w-full',
    },
  },

  // 提交函数
  handleSubmit: onSubmit,
  // 垂直布局，label和input在不同行，值为vertical
  // 水平布局，label和input在同一行
  layout: 'horizontal',
  schema: [
    {
      // 组件需要在 #/adapter.ts内注册，并加上类型
      component: 'Input',
      // 对应组件的参数
      componentProps: {
        placeholder: '请输入前缀',
      },
      // 字段名
      fieldName: 'prefix',
      // 界面显示的label
      label: '字符串',
    },
  ],
  // 大屏一行显示3个，中屏一行显示2个，小屏一行显示1个
  wrapperClass: 'grid-cols-1 md:grid-cols-2 lg:grid-cols-3',
});

async function onSubmit(values: Record<string, any>) {
  message.success({
    content: `form values: ${JSON.stringify(values)}`,
  });
  let userInfo: null | UserInfo = null;
  userInfo = await getUserInfoApi();
  console.log(userInfo);
  // let unqidResult: null | UnqidResult = null;
  const unqidApi = getUnqidApi({ prefix: values.prefix });
  await unqidApi;
  console.log(unqidApi);

  // message.warning({
  //   content: `api request result: ${unqidResult.data}`,
  // });
}

function handleSetFormValue() {
  /**
   * 设置表单值(多个)
   */
  baseFormApi.setValues({
    prefix: 'ABC241113',
  });

  // 设置单个表单值
  baseFormApi.setFieldValue('checkbox', true);
}
</script>

<template>
  <Page
    content-class="flex flex-col gap-4"
    description="流水号生成组件示例"
    title="流水号生成组件"
  >
    <Card title="基础示例">
      <template #extra>
        <Button type="primary" @click="handleSetFormValue">设置表单值</Button>
      </template>
      <BaseForm />
    </Card>
    <!--<Card title="使用tailwind自定义布局">
      <CustomLayoutForm />
    </Card>-->
  </Page>
</template>
