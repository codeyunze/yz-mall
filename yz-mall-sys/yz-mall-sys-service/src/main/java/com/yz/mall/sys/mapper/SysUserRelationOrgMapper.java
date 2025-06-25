package com.yz.mall.sys.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yz.mall.sys.entity.SysUserRelationOrg;
import com.yz.mall.sys.vo.InternalSysUserRelationOrgVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 系统-用户关联组织数据表(SysUserRelationOrg)表数据库访问层
 *
 * @author yunze
 * @since 2024-11-17 20:26:16
 */
@Mapper
public interface SysUserRelationOrgMapper extends BaseMapper<SysUserRelationOrg> {

    /**
     * 获取指定用户加入的组织信息
     *
     * @param userId 用户信息
     * @return 用户加入的组织信息
     */
    @Select("select suro.org_id, so.org_name, so.org_path_id " +
            "from sys_user_relation_org suro " +
            "inner join sys_org so on so.id = suro.org_id " +
            "where suro.invalid = 0 and so.invalid = 0 and so.status = 1 and suro.user_id = #{userId} ")
    List<InternalSysUserRelationOrgVo> selectOrgByUserId(@Param("userId") Long userId);
}

