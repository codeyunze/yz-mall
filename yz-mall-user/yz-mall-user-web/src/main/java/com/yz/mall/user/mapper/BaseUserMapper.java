package com.yz.mall.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yz.mall.user.entity.BaseUser;
import com.yz.mall.user.vo.BaseUserVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * 基础-用户(BaseUser)表数据库访问层
 *
 * @author yunze
 * @since 2024-06-16 23:25:56
 */
@Mapper
public interface BaseUserMapper extends BaseMapper<BaseUser> {

    /**
     * 获取用户信息
     *
     * @param account 登录账号（手机号|邮箱）
     * @return 用户信息
     */
    @Select("select id, phone, email, password from base_user where phone = #{account} or email = #{account}")
    BaseUserVo get(String account);
}

