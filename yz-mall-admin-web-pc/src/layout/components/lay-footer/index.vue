<script lang="ts" setup>
import { getConfig } from "@/config";

const TITLE = getConfig("Title");
const ICPRecord = getConfig("ICPRecord");
const PublicNetworkRecord = getConfig("PublicNetworkRecord");
</script>

<template>
  <footer
    class="layout-footer text-[rgba(0,0,0,0.6)] dark:text-[rgba(220,220,242,0.8)]"
  >
    Copyright © 2022-present
    <a
      class="hover:text-primary"
      href="https://gitee.com/cnyunze/3xj"
      target="_blank"
    >
      &nbsp;{{ TITLE }}
    </a>
    <img
      alt="备案图标"
      src="../../../../public/national-emblem-icon.png"
      height="20"
      width="18"
      style="margin: 0 5px 0 20px"
    />
    <a
      href="https://beian.mps.gov.cn/#/query/webSearch"
      rel="noreferrer"
      target="_blank"
    >
      {{ PublicNetworkRecord }}
    </a>
    <a
      href="https://beian.miit.gov.cn/"
      target="_blank"
      style="margin-left: 20px"
    >
      {{ ICPRecord }}
    </a>
  </footer>
</template>

<style lang="scss" scoped>
.layout-footer {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 100%;
  padding: 0 0 8px;
  font-size: 14px;
}
</style>
