import type {
  LoadingConfig,
  AdaptiveConfig,
  PaginationProps
} from "@pureadmin/table";
// import { tableData } from "../data";
import { pageUserInfo } from "@/api/user";
import { ref, onMounted, reactive } from "vue";
import { delay } from "@pureadmin/utils";

export function useColumns() {
  const loading = ref(true);
  const columns: TableColumnList = [
    {
      label: "ID",
      prop: "id"
    },
    {
      label: "手机号",
      prop: "phone"
    },
    {
      label: "邮件",
      prop: "email"
    },
    {
      label: "创建日期",
      prop: "createTime"
    }
  ];
  const form = reactive({
    phone: "",
    email: "",
    phone2: "",
    email2: "",
    phone3: "",
    email3: "",
    createTime: ""
  });
  const dataList = ref([]);
  /** 分页配置 */
  const pagination = reactive<PaginationProps>({
    pageSize: 20,
    currentPage: 1,
    pageSizes: [20, 40, 60],
    total: 0,
    align: "right",
    background: true,
    small: false
  });

  /** 加载动画配置 */
  const loadingConfig = reactive<LoadingConfig>({
    text: "正在加载第一页...",
    viewBox: "-10, -10, 50, 50",
    spinner: `
        <path class="path" d="
          M 30 15
          L 28 17
          M 25.61 25.61
          A 15 15, 0, 0, 1, 15 30
          A 15 15, 0, 1, 1, 27.99 7.5
          L 15 15
        " style="stroke-width: 4px; fill: rgba(0, 0, 0, 0)"/>
      `
    // svg: "",
    // background: rgba()
  });

  /** 撑满内容区自适应高度相关配置 */
  const adaptiveConfig: AdaptiveConfig = {
    /** 表格距离页面底部的偏移量，默认值为 `96` */
    offsetBottom: 110
    /** 是否固定表头，默认值为 `true`（如果不想固定表头，fixHeader设置为false并且表格要设置table-layout="auto"） */
    // fixHeader: true
    /** 页面 `resize` 时的防抖时间，默认值为 `60` ms */
    // timeout: 60
    /** 表头的 `z-index`，默认值为 `100` */
    // zIndex: 100
  };

  function onSizeChange(val) {
    console.log("onSizeChange", val);
  }

  function onCurrentChange(val) {
    loadingConfig.text = `正在加载第${val}页...`;
    loading.value = true;
    delay(600).then(() => {
      loading.value = false;
    });
  }

  function onSearch() {
    loading.value = true;
    const queryFilter = {
      size: pagination.pageSize,
      current: pagination.currentPage,
      filter: form
    };
    console.log("请求过滤" + JSON.stringify(queryFilter));
    // const { data } = await pageUserInfo(queryFilter);
    // pagination.total = data.total
    // dataList.value = data.data;
    // pagination.total = data.total;
    // pagination.pageSize = data.pageSize;
    // pagination.currentPage = data.currentPage;
    pageUserInfo(queryFilter).then(data => {
      console.log("接口数据" + JSON.stringify(data));
      dataList.value = data.data.items;
      pagination.total = data.data.total;
    });
    setTimeout(() => {
      loading.value = false;
    }, 500);
  }

  const resetForm = formEl => {
    if (!formEl) return;
    formEl.resetFields();
    onSearch();
  };

  onMounted(() => {
    onSearch();
    // delay(600).then(() => {
    //   const newList = [];
    //   Array.from({ length: 6 }).forEach(() => {
    //     newList.push(clone(tableData, true));
    //   });
    //   newList.flat(Infinity).forEach((item, index) => {
    //     dataList.value.push({ id: index, ...item });
    //   });
    //   pagination.total = dataList.value.length;
    //   loading.value = false;
    // });
  });

  return {
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
  };
}
