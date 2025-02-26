package com.yz.mall.file.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.github.codeyunze.entity.SysFiles;

/**
 * @author yunze
 * @since 2025/2/25 16:55
 */
@TableName("sys_files")
public class YzSysFiles extends SysFiles {

    /**
     * 文件上传人id
     */
    private Long createId;

    /**
     * 公开访问(0:不公开，1:公开)
     */
    private Integer publicAccess;

    public Long getCreateId() {
        return createId;
    }

    public void setCreateId(Long createId) {
        this.createId = createId;
    }

    public Integer getPublicAccess() {
        return publicAccess;
    }

    public void setPublicAccess(Integer publicAccess) {
        this.publicAccess = publicAccess;
    }
}
