package com.ggg.marsmall.ware.vo;

import lombok.Data;

@Data
public class PurchaseItemDoneVo {
    private Long itemId;//采购项id
    private Integer status;
    private String reason;
}
