import { otherRequestClient } from "#/api/request";

export namespace UnqidApi {
  export interface UnqidParams {
    prefix: string;
    numberLength?: number;
    quantity?: number;
  }

  export interface UnqidResult {
    data: string;
  }
}

/**
 * 获取示例表格数据
 */
export async function getUnqidApi(params: UnqidApi.UnqidParams) {
  return otherRequestClient.post<UnqidApi.UnqidResult>(
    '/unqid/generateSerialNumber',
    params,
  );
}

// export { getUnqidApi };
