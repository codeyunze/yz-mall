package com.yz.mall.sys.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yz.mall.sys.entity.SysDictionary;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 系统字典表(SysDictionary)表数据库访问层
 *
 * @author yunze
 * @since 2025-11-30 09:53:52
 */
@Mapper
public interface SysDictionaryMapper extends BaseMapper<SysDictionary> {

    /**
     * 递归查询指定数据字典 Key
     *
     * @param key 数据字典 key
     * @return 指定的数据字典及其下面挂载的子级字典
     */
    List<SysDictionary> selectRecursionByKey(@Param("key") String key);

    /**
     * 根据 id 查询 key
     *
     * @param id 数据字典 id
     * @return 数据字典 key
     */
    @Select("select dictionary_key from sys_dictionary where id = #{id}")
    String getKeyById(@Param("id") Long id);

}

