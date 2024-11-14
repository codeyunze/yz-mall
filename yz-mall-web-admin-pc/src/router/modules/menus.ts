const Layout = () => import("@/layout/index.vue");

export default {
  path: "/menus",
  component: Layout,
  redirect: "/menus/index",
  meta: {
    title: "菜单管理"
  },
  children: [
    {
      path: "/menus/index",
      name: "MenusManage",
      component: () => import("@/views/menus/index.vue"),
      meta: {
        title: "菜单管理"
      }
    }
  ]
} satisfies RouteConfigsTable;
