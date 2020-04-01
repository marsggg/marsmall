package com.ggg.marsmall.product.dao;

import com.ggg.marsmall.product.entity.CommentReplayEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品评价回复关系
 * 
 * @author guguanggao
 * @email ggg1169262115@163.com
 * @date 2020-04-01 10:11:05
 */
@Mapper
public interface CommentReplayDao extends BaseMapper<CommentReplayEntity> {
	
}
