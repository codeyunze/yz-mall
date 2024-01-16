package com.yz.mall.seata.tcc.order.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yz.mall.seata.tcc.order.entity.TccOrder;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

/**
 * 订单信息(TOrder)表数据库访问层
 *
 * @author yunze
 * @since 2023-11-05 19:59:16
 */
@Mapper
public interface TccOrderMapper extends BaseMapper<TccOrder> {

    /**
     * 保存订单
     *
     * @param order 订单信息
     * @return 影响数据条数
     */
    @Insert("insert into tcc_order (id, account_id, product_id, num, state) values (#{id}, #{accountId}, #{productId}, #{num}, 0)")
    int insert(TccOrder order);

    /**
     * 更新订单状态
     *
     * @param id    订单信息主键ID
     * @param state 订单状态（0：下单未支付；1：下单已支付）
     * @return 影响数据条数
     */
    @Update("update tcc_order set state = #{state} WHERE invalid = 0 and id = #{id}")
    int updateOrderStatus(@Param("id") Long id, @Param("state") int state);
}

