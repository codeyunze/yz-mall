import { able } from "@/router/enums";

export default {
  path: "/able",
  redirect: "/able/watermark",
  meta: {
    icon: "ri:ubuntu-fill",
    title: "功能",
    rank: able
  },
  children: [
    {
      path: "/able/mqtt-client",
      name: "MqttClient",
      component: () => import("@/views/able/mqtt-client.vue"),
      meta: {
        title: "MQTT客户端(mqtt)",
        extraIcon: "IF-pure-iconfont-new svg"
      }
    },
    {
      path: "/able/verify",
      name: "Verify",
      component: () => import("@/views/able/verify.vue"),
      meta: {
        title: "图形验证码"
      }
    },
    {
      path: "/able/watermark",
      name: "WaterMark",
      component: () => import("@/views/able/watermark.vue"),
      meta: {
        title: "水印"
      }
    },
    {
      path: "/able/print",
      name: "Print",
      component: () => import("@/views/able/print/index.vue"),
      meta: {
        title: "打印"
      }
    },
    {
      path: "/able/download",
      name: "Download",
      component: () => import("@/views/able/download.vue"),
      meta: {
        title: "下载"
      }
    },
    {
      path: "/able/excel",
      name: "Excel",
      component: () => import("@/views/able/excel.vue"),
      meta: {
        title: "导出Excel"
      }
    },
    {
      path: "/components/ripple",
      name: "Ripple",
      component: () => import("@/views/able/ripple.vue"),
      meta: {
        title: "波纹(Ripple)"
      }
    },
    {
      path: "/able/debounce",
      name: "Debounce",
      component: () => import("@/views/able/debounce.vue"),
      meta: {
        title: "防抖节流"
      }
    },
    {
      path: "/able/directives",
      name: "Directives",
      component: () => import("@/views/able/directives.vue"),
      meta: {
        title: "防抖、截流、复制、长按指令"
      }
    },
    {
      path: "/able/draggable",
      name: "Draggable",
      component: () => import("@/views/able/draggable.vue"),
      meta: {
        title: "拖拽",
        transition: {
          enterTransition: "animate__zoomIn",
          leaveTransition: "animate__zoomOut"
        }
      }
    },
    {
      path: "/able/pdf",
      name: "Pdf",
      component: () => import("@/views/able/pdf.vue"),
      meta: {
        title: "PDF预览"
      }
    },
    {
      path: "/able/barcode",
      name: "BarCode",
      component: () => import("@/views/able/barcode.vue"),
      meta: {
        title: "条形码"
      }
    },
    {
      path: "/able/qrcode",
      name: "QrCode",
      component: () => import("@/views/able/qrcode.vue"),
      meta: {
        title: "二维码登录"
      }
    },
    {
      path: "/able/map",
      name: "MapPage",
      component: () => import("@/views/able/map.vue"),
      meta: {
        title: "地图",
        keepAlive: true,
        transition: {
          name: "fade"
        }
      }
    },
    {
      path: "/able/wavesurfer",
      name: "Wavesurfer",
      component: () => import("@/views/able/wavesurfer/index.vue"),
      meta: {
        title: "音频可视化"
      }
    },
    {
      path: "/able/infinite-scroll",
      name: "InfiniteScroll",
      component: () => import("@/views/able/infinite-scroll.vue"),
      meta: {
        title: "表格无限滚动"
      }
    },
    {
      path: "/able/menu-tree",
      name: "MenuTree",
      component: () => import("@/views/able/menu-tree.vue"),
      meta: {
        title: "菜单树结构"
      }
    },
    {
      path: "/able/line-tree",
      name: "LineTree",
      component: () => import("@/views/able/line-tree.vue"),
      meta: {
        title: "树形连接线"
      }
    },
    {
      path: "/able/typeit",
      name: "Typeit",
      component: () => import("@/views/able/typeit.vue"),
      meta: {
        title: "打字机"
      }
    },
    {
      path: "/able/sensitive",
      name: "Sensitive",
      component: () => import("@/views/able/sensitive.vue"),
      meta: {
        title: "敏感词过滤"
      }
    },
    {
      path: "/able/pinyin",
      name: "Pinyin",
      component: () => import("@/views/able/pinyin.vue"),
      meta: {
        title: "汉语拼音"
      }
    }
  ]
} satisfies RouteConfigsTable;
