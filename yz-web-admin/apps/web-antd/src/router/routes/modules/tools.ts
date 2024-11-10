import type { RouteRecordRaw } from 'vue-router';

import { BasicLayout } from '#/layouts';
// import { $t } from '#/locales';

const routes: RouteRecordRaw[] = [
  {
    component: BasicLayout,
    meta: {
      icon: 'ion:layers-outline',
      keepAlive: true,
      order: 1000,
      title: '工具',
    },
    name: 'Tools',
    path: '/tools',
    children: [
      {
        name: 'ToolsUnqid',
        path: '/tools/unqid',
        component: () => import('#/views/tools/unqid/index.vue'),
        meta: {
          icon: 'system-uicons:window-content',
          title: '流水号生成',
        },
      },
    ],
  },
];

export default routes;
