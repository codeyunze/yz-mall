package com.yz.mall.file.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yz.mall.file.dto.YzFileInterviewDto;
import com.yz.mall.file.entity.YzSysFiles;
import io.github.codeyunze.bo.QofFileInfoBo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 系统-文件表(SysFiles)表数据库访问层
 *
 * @author yunze
 * @since 2025-02-16 15:43:41
 */
@Mapper
public interface YzSysFilesMapper extends BaseMapper<YzSysFiles> {

    /**
     * 根据文件Id获取文件信息
     *
     * @param fileId 文件Id
     * @return 文件基础信息
     */
    @Select("select id as file_id, create_time, file_name, file_path, file_type, file_label, file_size, file_storage_station, create_id, public_access from sys_files where invalid = 0 and id = #{fileId}")
    QofFileInfoBo<YzFileInterviewDto> selectByFileId(@Param("fileId") Long fileId);

    /**
     * 根据文件Id获取文件上传人、文件是否公开访问信息
     *
     * @param fileId 文件Id
     * @return 文件基础信息
     */
    @Select("select id as file_id, create_id, public_access from sys_files where invalid = 0 and id = #{fileId}")
    YzFileInterviewDto selectFileOwnerByFileId(@Param("fileId") Long fileId);

    /**
     * 根据文件Id列表获取公开的文件信息
     *
     * @param fileIds 文件Id
     * @return 文件基础信息
     */
    List<QofFileInfoBo<String>> selectByFileIdsAndPublic(@Param("fileIds") List<Long> fileIds);
}

