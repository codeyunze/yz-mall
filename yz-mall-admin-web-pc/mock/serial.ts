// 根据角色动态生成路由
import { defineFakeRoute } from "vite-plugin-fake-server/client";

export default defineFakeRoute([
  {
    url: "/internal/unqid/v3/generateSerialNumber",
    method: "post",
    response: ({ body }) => {
      if (body.prefix === "FAIL") {
        return {
          code: 1,
          message: "流水号生成失败"
        };
      } else {
        return {
          code: 200,
          message: "成功",
          data: body.prefix + "000001"
        };
      }
    }
  }
]);
