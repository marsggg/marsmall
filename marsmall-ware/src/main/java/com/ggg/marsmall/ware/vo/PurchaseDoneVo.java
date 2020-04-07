package com.ggg.marsmall.ware.vo;

import lombok.Data;

import java.util.List;

@Data
public class PurchaseDoneVo {

    private Long id;//采购单Id
    private List<PurchaseItemDoneVo> items;
}
