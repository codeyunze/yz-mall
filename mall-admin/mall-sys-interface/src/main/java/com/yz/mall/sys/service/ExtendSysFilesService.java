package com.yz.mall.sys.service;

import java.util.List;

/**
 * 文件扩展处理
 * @author yunze
 * @date 2026/1/1 星期四 22:13
 */
public interface ExtendSysFilesService {

    /**
     * 获取文件预览地址
     *
     * @param fileIds 文件 ID
     * @return 文件预览地址
     */
    List<String> getFilePreviewByFileIds(List<Long> fileIds);
}
