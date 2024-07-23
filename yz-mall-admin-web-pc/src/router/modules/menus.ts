import { $t } from "@/plugins/i18n";

const Layout = () => import("@/layout/index.vue");

export default {
  path: "/menus",
  component: Layout,
  redirect: "/menus/index",
  meta: {
    title: $t("menus.menusManage")
  },
  children: [
    {
      path: "/menus/index",
      name: "MenusManage",
      component: () => import("@/views/menus/index.vue"),
      meta: {
        title: $t("menus.menusManage")
      }
    }
  ]
};
