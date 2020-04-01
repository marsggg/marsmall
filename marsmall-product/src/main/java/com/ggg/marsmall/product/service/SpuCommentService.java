package com.ggg.marsmall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ggg.common.utils.PageUtils;
import com.ggg.marsmall.product.entity.SpuCommentEntity;

import java.util.Map;

/**
 * 商品评价
 *
 * @author guguanggao
 * @email ggg1169262115@163.com
 * @date 2020-04-01 10:11:05
 */
public interface SpuCommentService extends IService<SpuCommentEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

