package com.ggg.marsmall.order.dao;

import com.ggg.marsmall.order.entity.OrderItemEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单项信息
 * 
 * @author guguanggao
 * @email ggg1169262115@163.com
 * @date 2020-04-01 11:27:45
 */
@Mapper
public interface OrderItemDao extends BaseMapper<OrderItemEntity> {
	
}
