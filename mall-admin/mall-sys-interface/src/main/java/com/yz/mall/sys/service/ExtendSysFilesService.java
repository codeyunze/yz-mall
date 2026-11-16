package com.yz.mall.sys.service;

import java.util.List;

/**
 * 文件扩展处理
 * @author yunze
 * @date 2026/1/1 星期四 22:13
 */
public interface ExtendSysFilesService {

    /**
     * 按文件 ID 批量获取预签名预览地址。
     * 返回列表与入参顺序、长度一致；文件不存在或签名失败时对应元素为 {@code null}。
     *
     * @param fileIds 文件 ID
     * @return 预签名预览地址
     */
    List<String> getFilePreviewByFileIds(List<Long> fileIds);
}
