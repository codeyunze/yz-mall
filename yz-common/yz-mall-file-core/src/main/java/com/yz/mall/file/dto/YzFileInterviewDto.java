package com.yz.mall.file.dto;

import lombok.Data;

/**
 * 自定义文件上传入参
 *
 * @author yunze
 * @since 2025/2/25 19:04
 */
@Data
public class YzFileInterviewDto {

    /**
     * 创建人Id
     */
    private Long createId;

    /**
     * 公开访问(0:不公开，1:公开)
     */
    private Integer publicAccess;
}
