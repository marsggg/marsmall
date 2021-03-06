package com.ggg.marsmall.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ggg.common.utils.PageUtils;
import com.ggg.marsmall.ware.entity.WareOrderTaskDetailEntity;

import java.util.Map;

/**
 * 库存工作单
 *
 * @author guguanggao
 * @email ggg1169262115@163.com
 * @date 2020-04-01 11:31:10
 */
public interface WareOrderTaskDetailService extends IService<WareOrderTaskDetailEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

