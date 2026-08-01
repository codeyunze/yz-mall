package com.yz.mall.sys.dto;

import cn.hutool.core.date.DatePattern;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 系统文件表(SysFiles)表查询过滤条件数据模型类
 *
 * @author yunze
 * @date 2025/12/21 星期日
 */
@Data
public class SysFileQueryDto implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键标识
     */
    private Long id;

    /**
     * 文件名称
     */
    private String fileName;

    /**
     * 文件类型
     */
    private String fileType;

    /**
     * 文件存储模式(local、cos、oss)
     */
    private String fileStorageMode;

    /**
     * 存储别名
     */
    private String fileStorageStation;

    /**
     * 创建时间-开始
     */
    @JsonFormat(pattern = DatePattern.NORM_DATETIME_PATTERN)
    @DateTimeFormat(pattern = DatePattern.NORM_DATETIME_PATTERN)
    private LocalDateTime createTimeFrom;

    /**
     * 创建时间-结束
     */
    @JsonFormat(pattern = DatePattern.NORM_DATETIME_PATTERN)
    @DateTimeFormat(pattern = DatePattern.NORM_DATETIME_PATTERN)
    private LocalDateTime createTimeTo;
}

