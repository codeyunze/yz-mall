<script setup lang="ts">
import { ref } from "vue";
import { useColumns } from "./columns";
import { getPickerShortcuts } from "../../utils";
import { useRenderIcon } from "@/components/ReIcon/src/hooks";
import "plus-pro-components/es/components/search/style/css";

import { type PlusColumn, PlusSearch } from "plus-pro-components";

import Refresh from "@iconify-icons/ep/refresh";

const formRef = ref();
const tableRef = ref();

const {
  loading,
  columns,
  form,
  dataList,
  pagination,
  loadingConfig,
  adaptiveConfig,
  onSearch,
  resetForm,
  onSizeChange,
  onCurrentChange
} = useColumns();

const state = ref({
  status: "0",
  time: new Date().toString()
});

const filterColumns: PlusColumn[] = [
  {
    label: "手机号",
    prop: "phone"
  },
  {
    label: "邮件",
    prop: "email"
  },
  {
    label: "创建时间",
    prop: "createTime",
    valueType: "date-picker",
    fieldProps: {
      type: "datetimerange",
      startPlaceholder: "请选择",
      endPlaceholder: "请选择"
    }
  },
  {
    label: "手机号1",
    prop: "phone1"
  },
  {
    label: "邮件1",
    prop: "email1"
  },
  {
    label: "手机号2",
    prop: "phone2"
  },
  {
    label: "邮件2",
    prop: "email2"
  }
];

const handleChange = (values: any) => {
  console.log(values, "change");
};
const handleSearch = (values: any) => {
  console.log(values, "search");
};
const handleRest = () => {
  console.log("handleRest");
};
</script>

<template>
  <div class="main">
    <el-form
      ref="formRef"
      :inline="true"
      :model="form"
      class="search-form bg-bg_color w-[99/100] pl-8 pt-[12px] overflow-auto"
    >
      <el-form-item label="手机号" prop="phone">
        <el-input
          v-model="form.phone"
          placeholder="请输入手机号"
          clearable
          class="!w-[170px]"
        />
      </el-form-item>
      <el-form-item label="邮件" prop="email">
        <el-input
          v-model="form.email"
          placeholder="请输入邮件"
          clearable
          class="!w-[170px]"
        />
      </el-form-item>
      <el-form-item label="手机号2" prop="phone">
        <el-input
          v-model="form.phone2"
          placeholder="请输入手机号2"
          clearable
          class="!w-[170px]"
        />
      </el-form-item>
      <el-form-item label="邮件2" prop="email">
        <el-input
          v-model="form.email2"
          placeholder="请输入邮件2"
          clearable
          class="!w-[170px]"
        />
      </el-form-item>
      <el-form-item label="手机号3" prop="phone">
        <el-input
          v-model="form.phone3"
          placeholder="请输入手机号3"
          clearable
          class="!w-[170px]"
        />
      </el-form-item>
      <el-form-item label="邮件3" prop="email">
        <el-input
          v-model="form.email3"
          placeholder="请输入邮件3"
          clearable
          class="!w-[170px]"
        />
      </el-form-item>
      <el-form-item label="创建时间" prop="createTime">
        <el-date-picker
          v-model="form.createTime"
          :shortcuts="getPickerShortcuts()"
          type="datetimerange"
          range-separator="至"
          start-placeholder="开始日期时间"
          end-placeholder="结束日期时间"
        />
      </el-form-item>
      <el-form-item>
        <el-button
          type="primary"
          :icon="useRenderIcon('ri:search-line')"
          :loading="loading"
          @click="onSearch"
        >
          搜索
        </el-button>
        <el-button :icon="useRenderIcon(Refresh)" @click="resetForm(formRef)">
          重置
        </el-button>
      </el-form-item>
    </el-form>

    <PlusSearch
      v-model="state"
      :columns="filterColumns"
      :show-number="2"
      label-width="80"
      label-position="right"
      @change="handleChange"
      @search="handleSearch"
      @reset="handleRest"
    />

    <pure-table
      ref="tableRef"
      border
      adaptive
      stripe
      :adaptiveConfig="adaptiveConfig"
      row-key="id"
      alignWhole="center"
      showOverflowTooltip
      :loading="loading"
      :loading-config="loadingConfig"
      :data="
        dataList.slice(
          (pagination.currentPage - 1) * pagination.pageSize,
          pagination.currentPage * pagination.pageSize
        )
      "
      :columns="columns"
      :header-cell-style="{
        background: 'var(--el-fill-color-light)',
        color: 'var(--el-text-color-primary)'
      }"
      :pagination="pagination"
      @page-size-change="onSizeChange"
      @page-current-change="onCurrentChange"
    />
  </div>
</template>
