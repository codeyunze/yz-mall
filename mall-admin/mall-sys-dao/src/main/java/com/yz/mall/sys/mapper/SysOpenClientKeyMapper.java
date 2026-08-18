package com.yz.mall.sys.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yz.mall.sys.entity.SysOpenClientKey;
import org.apache.ibatis.annotations.Mapper;

/**
 * 第三方客户端SM2公钥(SysOpenClientKey)表数据库访问层
 *
 * @author yunze
 */
@Mapper
public interface SysOpenClientKeyMapper extends BaseMapper<SysOpenClientKey> {

}
