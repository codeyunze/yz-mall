package com.yz.mall.sys.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yz.mall.sys.entity.SysOrg;
import com.yz.mall.sys.entity.SysRole;
import com.yz.mall.sys.entity.SysUser;
import com.yz.mall.sys.entity.SysUserRelationRole;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 系统-用户与组织关联角色表(SysUserRelationRole)表数据库访问层
 *
 * @author yunze
 * @since 2024-11-26 11:46:13
 */
@Mapper
public interface SysUserRelationRoleMapper extends BaseMapper<SysUserRelationRole> {

    /**
     * 获取指定用户所拥有的角色编码
     *
     * @param relationId 指定用户Id {@link SysUser#getId()} 或 组织Id {@link SysOrg#getId()}
     * @return 用户所拥有的角色Id {@link SysRole#getRoleCode()}
     */
    @Select("select sr.role_code from sys_user_relation_role surr inner join sys_role sr on surr.role_id = sr.id " +
            "where surr.invalid = 0 and sr.invalid = 0 and surr.relation_id = #{relationId}")
    List<String> selectRoleCodesByRelationId(@Param("relationId") Long relationId);
}

