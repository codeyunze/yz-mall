<script setup lang="ts">
import {
  generateSerialNumber,
  type SerialNumberResult
} from "@/api/serialNumberGenerate";

defineOptions({
  // name 作为一种规范最好必须写上并且和路由的name保持一致
  name: "SerialNumber"
});

function onGenerateSerialNumber() {
  generateSerialNumber({ prefix: "TEST" }).then(({ data }) => {
    console.log(data);
  });
}
</script>

<template>
  <h1>流水号配置页面</h1>
  <el-button @click="onGenerateSerialNumber">生成流水号</el-button>
</template>
