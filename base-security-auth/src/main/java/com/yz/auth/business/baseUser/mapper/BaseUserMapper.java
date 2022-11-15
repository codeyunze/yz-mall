package com.yz.auth.business.baseUser.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yz.auth.business.baseUser.entity.BaseUser;
import org.springframework.stereotype.Repository;

/**
 * <p>
 * 基础-用户表 Mapper 接口
 * </p>
 *
 * @author gaohan
 * @since 2022-09-13
 */
@Repository
public interface BaseUserMapper extends BaseMapper<BaseUser> {

    BaseUser findOneByUserAccount(String userAccount);
}
