package com.yz.mall.file.dto;

import io.github.codeyunze.dto.QofFileUploadDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 自定义文件上传入参
 * @author yunze
 * @since 2025/2/25 19:04
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class YzFileUploadDto extends QofFileUploadDto {

    /**
     * 公开访问(0:不公开，1:公开)
     */
    private Integer publicAccess;
}
