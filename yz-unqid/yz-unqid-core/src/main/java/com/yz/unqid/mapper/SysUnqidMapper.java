package com.yz.unqid.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yz.unqid.entity.SysUnqid;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 系统-流水号表(SysUnqid)表数据库访问层
 *
 * @author yunze
 * @since 2024-06-23 22:52:36
 */
@Mapper
public interface SysUnqidMapper extends BaseMapper<SysUnqid> {

    /**
     * 查询指定前缀流水号此时的序号
     *
     * @param prefix 前缀
     * @return 此时的序号
     */
    @DS("slave")
    @Select("select serial_number from sys_unqid where prefix = #{prefix}")
    Integer getSerialNumberByPrefix(@Param("prefix") String prefix);

    /**
     * 记录已经生成的流水号
     * @param code 流水号
     */
    @Insert("insert into test_serial_number (code) values (#{code})")
    void record(@Param("code") String code);
}

