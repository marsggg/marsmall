package com.ggg.marsmall.product.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.ggg.marsmall.product.entity.ProductAttrValueEntity;
import com.ggg.marsmall.product.service.ProductAttrValueService;
import com.ggg.marsmall.product.vo.AttrGroupRelationVo;
import com.ggg.marsmall.product.vo.AttrRespVo;
import com.ggg.marsmall.product.vo.AttrVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.ggg.marsmall.product.entity.AttrEntity;
import com.ggg.marsmall.product.service.AttrService;
import com.ggg.common.utils.PageUtils;
import com.ggg.common.utils.R;


/**
 * 商品属性
 *
 * @author guguanggao
 * @email ggg1169262115@163.com
 * @date 2020-04-01 10:46:36
 */
@RestController
@RequestMapping("product/attr")
public class AttrController {
    @Autowired
    private AttrService attrService;

    @Autowired
    ProductAttrValueService productAttrValueService;

    @GetMapping("/{attrType}/list/{catelogId}")
    public R baseAttrList(@RequestParam Map<String, Object> params,
                          @PathVariable("catelogId") Long catelogId,
                          @PathVariable("attrType") String attrType) {
        PageUtils page = attrService.queryBaseAttrPage(params, catelogId, attrType);
        return R.ok().put("page", page);
    }

    /**
     * 列表
     */
    @RequestMapping("/base/listforspu/{spuId}")
    //@RequiresPermissions("product:attr:list")
    public R baseAttrListForspu(@PathVariable("spuId")Long spuId) {
       List<ProductAttrValueEntity> list= productAttrValueService.baseAttrListForspu(spuId);

        return R.ok().put("data", list);
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    //@RequiresPermissions("product:attr:list")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = attrService.queryPage(params);

        return R.ok().put("page", page);
    }

    /**
     * 信息
     */
    @RequestMapping("/info/{attrId}")
    //@RequiresPermissions("product:attr:info")
    public R info(@PathVariable("attrId") Long attrId) {
//            AttrEntity attr = attrService.getById(attrId);
        AttrRespVo attr = attrService.getAttrInfo(attrId);
        return R.ok().put("attr", attr);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("product:attr:save")
    public R save(@RequestBody AttrVo attr) {
        attrService.saveAttr(attr);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("product:attr:update")
    public R update(@RequestBody AttrVo attr) {
        attrService.updateAttr(attr);

        return R.ok();
    }

    @PostMapping("/update/{spuId}")
    //@RequiresPermissions("product:attr:update")
    public R updateSpuAttr(@PathVariable("spuId")Long spuId,
                           @RequestBody List<ProductAttrValueEntity> entities) {
        productAttrValueService.updateSpuAttr(spuId,entities);

        return R.ok();
    }



    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("product:attr:delete")
    public R delete(@RequestBody Long[] attrIds) {
        attrService.removeAttrs(Arrays.asList(attrIds));

        return R.ok();
    }

}
