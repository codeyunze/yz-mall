package com.yz.mall.file.bo;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.InputStream;

/**
 * 下载文件信息
 *
 * @author yunze
 * @date 2025/2/18 18:41
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class QofFileDownloadBo extends QofFileInfoBo {

    /**
     * 文件
     */
    private InputStream inputStream;
}
