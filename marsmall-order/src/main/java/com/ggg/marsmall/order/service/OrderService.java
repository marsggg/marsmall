package com.ggg.marsmall.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ggg.common.utils.PageUtils;
import com.ggg.marsmall.order.entity.OrderEntity;

import java.util.Map;

/**
 * 订单
 *
 * @author guguanggao
 * @email ggg1169262115@163.com
 * @date 2020-04-01 11:27:45
 */
public interface OrderService extends IService<OrderEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

