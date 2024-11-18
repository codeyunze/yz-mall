import { system } from "@/router/enums";

export default {
  path: "/system",
  redirect: "/system/serial",
  meta: {
    icon: "ri:settings-3-line",
    title: "系统管理",
    rank: system
  },
  children: [
    {
      path: "/system/serial",
      name: "SerialNumber",
      component: () => import("@/views/system/serial/index.vue"),
      meta: {
        title: "流水号配置"
      }
    },
    {
      path: "/system/user",
      name: "DataDictionary",
      component: () => import("@/views/system/user/index.vue"),
      meta: {
        title: "用户管理"
      }
    }
  ]
} satisfies RouteConfigsTable;
