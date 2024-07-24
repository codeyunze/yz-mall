import { http } from "@/utils/http";

export type SerialNumberResult = {
  /**
   * 返回200代表成功
   */
  code: number;
  message: string;
  /**
   * 序列号
   */
  data: string;
};

/**
 * 生成序列号
 * @param data 流水号前缀 {"prefix": "TEST"}
 */
export const generateSerialNumber = (data?: object) => {
  return http.request<SerialNumberResult>(
    "post",
    "/internal/unqid/v3/generateSerialNumber",
    { data }
  );
};
