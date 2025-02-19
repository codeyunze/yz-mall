package com.yz.mall.file.dto;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 文件下载信息
 *
 * @author yunze
 * @since 2025-02-16 15:43:42
 */
@Data
public class QofFileDownloadDto implements Serializable {

    private static final long serialVesionUID = 1L;

    /**
     * 文件唯一Id
     */
    @NotNull(message = "文件id不能为空")
    private Long fileId;

    /**
     * 文件存储模式(local、cos、oss)
     * <br>
     * 对应{@link com.yz.mall.file.enums.QofStorageModeEnum}
     */
    private String fileStorageMode;

    /**
     * 文件存储桶
     */
    private String fileStorageBucket;
}

