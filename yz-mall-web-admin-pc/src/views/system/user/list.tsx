import Adaptive from "./adaptive/index.vue";

const rendContent = (val: string) =>
  `代码位置：src/views/table/high/${val}/index.vue`;

export const list = [
  {
    key: "adaptive",
    content: rendContent("adaptive"),
    title: "自适应内容区高度",
    component: Adaptive
  }
];
