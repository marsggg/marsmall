package com.ggg.marsmall.coupon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ggg.common.utils.PageUtils;
import com.ggg.marsmall.coupon.entity.SkuLadderEntity;

import java.util.Map;

/**
 * 商品阶梯价格
 *
 * @author guguanggao
 * @email ggg1169262115@163.com
 * @date 2020-04-01 11:14:51
 */
public interface SkuLadderService extends IService<SkuLadderEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

