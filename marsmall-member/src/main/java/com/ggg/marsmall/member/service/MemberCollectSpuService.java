package com.ggg.marsmall.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ggg.common.utils.PageUtils;
import com.ggg.marsmall.member.entity.MemberCollectSpuEntity;

import java.util.Map;

/**
 * 会员收藏的商品
 *
 * @author guguanggao
 * @email ggg1169262115@163.com
 * @date 2020-04-01 11:22:37
 */
public interface MemberCollectSpuService extends IService<MemberCollectSpuEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

