<script setup lang="ts">
import { ref } from "vue";
import { useColumns } from "./columns";

const tableRef = ref();

const {
  loading,
  columns,
  dataList,
  pagination,
  onSearch,
  loadingConfig,
  adaptiveConfig,
  onSizeChange,
  onCurrentChange
} = useColumns();
</script>

<template>
  <div class="main">
    <el-form
      ref="formRef"
      :inline="true"
      :model="form"
      class="search-form bg-bg_color w-[99/100] pl-8 pt-[12px] overflow-auto"
    >
      <el-form-item label="所属模块" prop="module">
        <el-input
          v-model="form.module"
          placeholder="请输入所属模块"
          clearable
          class="!w-[170px]"
        />
      </el-form-item>
      <el-form-item label="操作状态" prop="status">
        <el-select
          v-model="form.status"
          placeholder="请选择"
          clearable
          class="!w-[150px]"
        >
          <el-option label="成功" value="1" />
          <el-option label="失败" value="0" />
        </el-select>
      </el-form-item>
      <el-form-item label="操作时间" prop="operatingTime">
        <el-date-picker
          v-model="form.operatingTime"
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
