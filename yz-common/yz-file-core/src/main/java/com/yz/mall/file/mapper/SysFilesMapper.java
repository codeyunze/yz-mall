package com.yz.mall.file.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yz.mall.file.bo.QofFileInfoBo;
import com.yz.mall.file.entity.SysFiles;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 系统-文件表(SysFiles)表数据库访问层
 *
 * @author yunze
 * @since 2025-02-16 15:43:41
 */
@Mapper
public interface SysFilesMapper extends BaseMapper<SysFiles> {

    /**
     * 根据文件Id获取文件信息
     *
     * @param fileId 文件Id
     * @return 文件基础信息
     */
    @Select("select id, create_time, file_name, file_path, file_type, file_label, file_size from sys_files where invalid = 0 and id = #{fileId}")
    QofFileInfoBo selectByFileId(@Param("fileId") Long fileId);
}

