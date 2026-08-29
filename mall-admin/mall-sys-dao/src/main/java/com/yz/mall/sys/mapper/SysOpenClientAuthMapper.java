package com.yz.mall.sys.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yz.mall.sys.entity.SysOpenClientAuth;
import org.apache.ibatis.annotations.Mapper;

/**
 * 第三方客户端接口授权(SysOpenClientAuth)表数据库访问层
 *
 * @author yunze
 */
@Mapper
public interface SysOpenClientAuthMapper extends BaseMapper<SysOpenClientAuth> {

}
