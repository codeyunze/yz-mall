package com.yz.mall.sys.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

/**
 * 系统文件表(SysFiles)表更新数据模型类
 *
 * @author yunze
 * @date 2025/12/21 星期日
 */
@Data
public class SysFileUpdateDto implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键标识
     */
    @NotNull(message = "主键标识不能为空")
    private Long id;

    /**
     * 文件名称
     */
    private String fileName;

    /**
     * 文件存储模式(local、cos、oss)
     */
    private String fileStorageMode;

    /**
     * 存储别名
     */
    private String fileStorageStation;
}

