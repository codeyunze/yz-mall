package com.yz.mall.file.dto;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 文件上传信息
 *
 * @author yunze
 * @since 2025-02-16 15:43:42
 */
@Data
public class QofFileUploadDto implements Serializable {

    private static final long serialVesionUID = 1L;


    /**
     * 文件名称
     *
     * @mock 靓图.png
     */
    @Length(max = 36, message = "文件名称超过最大长度限制")
    private String fileName;

    /**
     * 文件存储模式(local、cos、oss)
     * <br>
     * 对应{@link com.yz.mall.file.enums.QofStorageModeEnum}
     */
    @Length(max = 10, message = "文件存储模式超过最大长度限制")
    @NotBlank(message = "文件存储模式不能为空")
    private String fileStorageMode;

    /**
     * 文件存储桶
     */
    @Length(max = 36, message = "文件存储桶超过最大长度限制")
    private String fileStorageBucket;
}

