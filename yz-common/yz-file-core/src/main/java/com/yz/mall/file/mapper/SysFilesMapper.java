package com.yz.mall.file.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yz.mall.file.entity.SysFiles;
import org.apache.ibatis.annotations.Mapper;

/**
 * 系统-文件表(SysFiles)表数据库访问层
 *
 * @author yunze
 * @since 2025-02-16 15:43:41
 */
@Mapper
public interface SysFilesMapper extends BaseMapper<SysFiles> {

}

