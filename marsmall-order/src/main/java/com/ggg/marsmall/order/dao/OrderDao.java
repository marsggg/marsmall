package com.ggg.marsmall.order.dao;

import com.ggg.marsmall.order.entity.OrderEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单
 * 
 * @author guguanggao
 * @email ggg1169262115@163.com
 * @date 2020-04-01 11:27:45
 */
@Mapper
public interface OrderDao extends BaseMapper<OrderEntity> {
	
}
