package com.ggg.marsmall.ware.service.impl;

import com.ggg.common.constant.WareConstant;
import com.ggg.marsmall.ware.entity.PurchaseDetailEntity;
import com.ggg.marsmall.ware.service.PurchaseDetailService;
import com.ggg.marsmall.ware.service.WareSkuService;
import com.ggg.marsmall.ware.vo.MergeVo;
import com.ggg.marsmall.ware.vo.PurchaseDoneVo;
import com.ggg.marsmall.ware.vo.PurchaseItemDoneVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ggg.common.utils.PageUtils;
import com.ggg.common.utils.Query;

import com.ggg.marsmall.ware.dao.PurchaseDao;
import com.ggg.marsmall.ware.entity.PurchaseEntity;
import com.ggg.marsmall.ware.service.PurchaseService;
import org.springframework.transaction.annotation.Transactional;


@Service("purchaseService")
public class PurchaseServiceImpl extends ServiceImpl<PurchaseDao, PurchaseEntity> implements PurchaseService {

    @Autowired
    private PurchaseDetailService purchaseDetailService;


    @Autowired
    private WareSkuService wareSkuService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<PurchaseEntity> page = this.page(
                new Query<PurchaseEntity>().getPage(params),
                new QueryWrapper<PurchaseEntity>()
        );

        return new PageUtils(page);
    }


    @Override
    public PageUtils queryPageUnreceive(Map<String, Object> params) {
        IPage<PurchaseEntity> page = this.page(
                new Query<PurchaseEntity>().getPage(params),
                new QueryWrapper<PurchaseEntity>().eq("status", 0).or().eq("status", 1)
        );

        return new PageUtils(page);
    }

    @Override
    public void merge(MergeVo vo) {
        Long purchaseId = vo.getPurchaseId();
        PurchaseEntity purchaseEntity = null;
        if (purchaseId == null) {
            purchaseEntity = new PurchaseEntity();
            purchaseEntity.setCreateTime(new Date());
            purchaseEntity.setUpdateTime(new Date());
            purchaseEntity.setStatus(WareConstant.PurchaseStatusEnum.CREATED.getCode());
            this.save(purchaseEntity);
            purchaseId = purchaseEntity.getId();
        }else {
            purchaseEntity = this.getById(purchaseId);
        }
        //确定采购单的状态是0 1 才可以
        if (purchaseEntity.getStatus()==WareConstant.PurchaseStatusEnum.CREATED.getCode()
                ||purchaseEntity.getStatus()==WareConstant.PurchaseStatusEnum.ASSIGNED.getCode()) {

            List<Long> items = vo.getItems();
            Long finalPurchaseId = purchaseId;
            List<PurchaseDetailEntity> detailEntities = items.stream().map(item -> {
                PurchaseDetailEntity purchaseDetailEntity = new PurchaseDetailEntity();
                purchaseDetailEntity.setPurchaseId(finalPurchaseId);
                purchaseDetailEntity.setId(item);
                purchaseDetailEntity.setStatus(WareConstant.PurchaseDetailStatusEnum.ASSIGNED.getCode());
                return purchaseDetailEntity;
            }).collect(Collectors.toList());

            purchaseDetailService.updateBatchById(detailEntities);
        }
    }

    @Transactional
    @Override
    public void received(List<Long> ids) {
        List<PurchaseEntity> purchaseEntities = ids.stream().map(id -> {
            PurchaseEntity purchaseEntity = this.getById(id);
            return purchaseEntity;
        }).filter(purchaseEntity ->
                purchaseEntity.getStatus() == WareConstant.PurchaseStatusEnum.CREATED.getCode()
                        || purchaseEntity.getStatus() == WareConstant.PurchaseStatusEnum.ASSIGNED.getCode()
        ).map(purchaseEntity -> {
            purchaseEntity.setStatus(WareConstant.PurchaseStatusEnum.RECEIVE.getCode());
            purchaseEntity.setUpdateTime(new Date());
            return purchaseEntity;
        }).collect(Collectors.toList());


        this.updateBatchById(purchaseEntities);

        //改变采购项目的状态
        purchaseEntities.forEach(purchaseEntity -> {
            List<PurchaseDetailEntity> purchaseDetailEntities = purchaseDetailService.listDetailByPurchaseId(purchaseEntity.getId());
            List<PurchaseDetailEntity> detailEntities = purchaseDetailEntities.stream().map(entity -> {
                PurchaseDetailEntity detailEntity = new PurchaseDetailEntity();
                detailEntity.setId(entity.getId());
                detailEntity.setStatus(WareConstant.PurchaseStatusEnum.RECEIVE.getCode());
                return detailEntity;
            }).collect(Collectors.toList());
            purchaseDetailService.updateBatchById(detailEntities);
        });
    }


    @Override
    public void done(PurchaseDoneVo vo) {

        Long id= vo.getId();

        //2.改变采购项的状态
        Boolean flag = true;
        List<PurchaseItemDoneVo> items = vo.getItems();
        ArrayList<PurchaseDetailEntity> updates = new ArrayList<>();
        for (PurchaseItemDoneVo item : items) {
            PurchaseDetailEntity purchaseDetailEntity = new PurchaseDetailEntity();
            if (item.getStatus() == WareConstant.PurchaseStatusEnum.HASERROR.getCode()) {
                flag = false;
                purchaseDetailEntity.setStatus(item.getStatus());
            }else{
                purchaseDetailEntity.setStatus(WareConstant.PurchaseStatusEnum.FINISH.getCode());
                //入库(库存)操作
                PurchaseDetailEntity detailEntity = purchaseDetailService.getById(item.getItemId());
                wareSkuService.addStock(detailEntity.getSkuId(),detailEntity.getWareId(),detailEntity.getSkuNum());
            }

            purchaseDetailEntity.setId(item.getItemId());
            updates.add(purchaseDetailEntity);
        }

        purchaseDetailService.updateBatchById(updates);

        //1.改变采购单状态
        PurchaseEntity purchaseEntity = new PurchaseEntity();
        purchaseEntity.setId(id);
        purchaseEntity.setStatus(flag?WareConstant.PurchaseStatusEnum.FINISH.getCode():WareConstant.PurchaseStatusEnum.HASERROR.getCode());
        this.updateById(purchaseEntity);
        //3.将成功采购入库
    }
}
