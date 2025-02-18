package com.yz.mall.file.core;

import com.yz.mall.file.dto.QofFileInfoDto;

import java.io.InputStream;

/**
 * QOF客户端操作接口
 *
 * @author yunze
 * @date 2025/2/17 08:46
 */
public interface QofClient {

    /**
     * 上传文件信息
     * @param fis 上传文件的输入流
     * @param info 上传文件的基础信息
     * @return 文件唯一id
     */
    Long uploadFile(InputStream fis, QofFileInfoDto info);

}
