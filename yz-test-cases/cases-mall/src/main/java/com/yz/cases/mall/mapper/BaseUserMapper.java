package com.yz.cases.mall.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yz.cases.mall.dto.ProductAddDto;
import com.yz.cases.mall.entity.BaseUser;
import com.yz.cases.mall.entity.TProduct;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;

/**
 * 基础-用户(BaseUser)表数据库访问层
 *
 * @author yunze
 * @since 2024-06-11 23:16:13
 */
public interface BaseUserMapper extends BaseMapper<BaseUser> {


}

