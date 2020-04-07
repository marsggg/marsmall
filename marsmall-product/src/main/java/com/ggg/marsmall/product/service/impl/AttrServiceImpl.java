package com.ggg.marsmall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ggg.common.constant.ProductConstant;
import com.ggg.marsmall.product.entity.AttrAttrgroupRelationEntity;
import com.ggg.marsmall.product.entity.AttrGroupEntity;
import com.ggg.marsmall.product.entity.CategoryEntity;
import com.ggg.marsmall.product.service.AttrAttrgroupRelationService;
import com.ggg.marsmall.product.service.AttrGroupService;
import com.ggg.marsmall.product.service.CategoryService;
import com.ggg.marsmall.product.vo.AttrGroupRelationVo;
import com.ggg.marsmall.product.vo.AttrGroupWithAttrsVo;
import com.ggg.marsmall.product.vo.AttrRespVo;
import com.ggg.marsmall.product.vo.AttrVo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ggg.common.utils.PageUtils;
import com.ggg.common.utils.Query;

import com.ggg.marsmall.product.dao.AttrDao;
import com.ggg.marsmall.product.entity.AttrEntity;
import com.ggg.marsmall.product.service.AttrService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;


@Service("attrService")
public class AttrServiceImpl extends ServiceImpl<AttrDao, AttrEntity> implements AttrService {

    @Autowired
    private AttrAttrgroupRelationService attrAttrgroupRelationService;

    @Autowired
    private AttrGroupService attrGroupService;

    @Autowired
    private CategoryService categoryService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrEntity> page = this.page(
                new Query<AttrEntity>().getPage(params),
                new QueryWrapper<AttrEntity>()
        );

        return new PageUtils(page);
    }


    @Transactional
    @Override
    public void saveAttr(AttrVo attr) {
        AttrEntity attrEntity = new AttrEntity();
        BeanUtils.copyProperties(attr, attrEntity);
        //1保存基本数据
        this.save(attrEntity);

        if (attr.getAttrType() == ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode()) {
            //2保存关联关系
            AttrAttrgroupRelationEntity attrAttrgroupRelationEntity = new AttrAttrgroupRelationEntity();
            attrAttrgroupRelationEntity.setAttrGroupId(attr.getAttrGroupId());
            attrAttrgroupRelationEntity.setAttrId(attrEntity.getAttrId());
            attrAttrgroupRelationService.save(attrAttrgroupRelationEntity);
        }
    }


    @Override
    public PageUtils queryBaseAttrPage(Map<String, Object> params, Long catelogId, String attrType) {

        QueryWrapper<AttrEntity> wrapper = new QueryWrapper<AttrEntity>();
        if (catelogId != 0) {
            wrapper.eq("catelog_id", catelogId);
        }
        wrapper.eq("attr_type", "base".equalsIgnoreCase(attrType) ? 1 : 0);
        String key = (String) params.get("key");
        if (StringUtils.isNotBlank(key)) {
            wrapper.and((obj) -> {
                obj.eq("attr_id", key).or().like("attr_name", key);
            });
        }

        IPage<AttrEntity> page = this.page(
                new Query<AttrEntity>().getPage(params),
                wrapper
        );
        PageUtils pageUtils = new PageUtils(page);
        List<AttrEntity> records = page.getRecords();
        List<AttrRespVo> attrRespVos = records.stream().map(attrEntity -> {
            AttrRespVo attrRespVo = new AttrRespVo();
            BeanUtils.copyProperties(attrEntity, attrRespVo);

            if ("base".equalsIgnoreCase(attrType)) {
                AttrAttrgroupRelationEntity agrEntity = attrAttrgroupRelationService.getOne(
                        new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", attrEntity.getAttrId()));

                if (agrEntity != null && agrEntity.getAttrGroupId() != null) {
                    AttrGroupEntity attrGroupEntity = attrGroupService.getById(agrEntity.getAttrGroupId());
                    if (attrGroupEntity != null) {
                        attrRespVo.setGroupName(attrGroupEntity.getAttrGroupName());
                    }
                }
            }

            CategoryEntity categoryEntity = categoryService.getById(attrEntity.getCatelogId());
            if (categoryEntity != null) {
                attrRespVo.setCatelogName(categoryEntity.getName());
            }
            return attrRespVo;
        }).collect(Collectors.toList());
        pageUtils.setList(attrRespVos);
        return pageUtils;
    }

    @Override
    public AttrRespVo getAttrInfo(Long attrId) {

        AttrEntity attrEntity = this.getById(attrId);
        AttrRespVo respVo = new AttrRespVo();
        BeanUtils.copyProperties(attrEntity, respVo);

        Long[] catelogPath = categoryService.findCatelogPath(attrEntity.getCatelogId());
        CategoryEntity categoryEntity = categoryService.getById(attrEntity.getCatelogId());
        if (categoryEntity != null) {
            respVo.setCatelogName(categoryEntity.getName());
        }
        respVo.setCatelogPath(catelogPath);

        AttrAttrgroupRelationEntity attrAttrgroupRelationEntity = attrAttrgroupRelationService.getOne(
                new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", attrEntity.getAttrId()));

        if (attrAttrgroupRelationEntity != null) {
            respVo.setAttrGroupId(attrAttrgroupRelationEntity.getAttrGroupId());
            AttrGroupEntity attrGroupEntity = attrGroupService.getById(attrAttrgroupRelationEntity.getAttrGroupId());
            if (attrGroupEntity != null) {
                respVo.setGroupName(attrGroupEntity.getAttrGroupName());
            }
        }

        return respVo;
    }


    @Transactional
    @Override
    public void updateAttr(AttrVo attr) {
        AttrEntity attrEntity = new AttrEntity();
        BeanUtils.copyProperties(attr, attrEntity);
        this.save(attrEntity);

        if (attr.getAttrType() == ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode()) {
            int attrRelationCount = attrAttrgroupRelationService.count(new QueryWrapper<AttrAttrgroupRelationEntity>().
                    eq("attr_id", attr.getAttrId()));
            AttrAttrgroupRelationEntity attrAttrgroupRelationEntity = new AttrAttrgroupRelationEntity();
            attrAttrgroupRelationEntity.setAttrId(attr.getAttrId());
            attrAttrgroupRelationEntity.setAttrGroupId(attr.getAttrGroupId());
            if (attrRelationCount > 0) {
                //修改分组关联
                attrAttrgroupRelationService.update(attrAttrgroupRelationEntity,
                        new UpdateWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", attr.getAttrId()));
            } else {
                attrAttrgroupRelationService.save(attrAttrgroupRelationEntity);
            }
        }
    }

    @Override
    public void removeAttrs(List<Long> asList) {
        this.removeByIds(asList);
        //删除关联表
        attrAttrgroupRelationService.remove(
                new UpdateWrapper<AttrAttrgroupRelationEntity>().in("attr_id", asList));
    }

    /**
     * 根据分组Id查找关联的基本属性
     *
     * @param attrgroupId
     * @return
     */
    @Override
    public List<AttrEntity> getRelationAttr(Long attrgroupId) {

        List<AttrAttrgroupRelationEntity> entities = attrAttrgroupRelationService.list(
                new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_group_id", attrgroupId));

        List<Long> attrIds = entities.stream().map(entity -> entity.getAttrId())
                .collect(Collectors.toList());

        if (attrIds == null || attrIds.size() == 0) {
            return Collections.emptyList();
        }

        Collection<AttrEntity> attrEntities = this.listByIds(attrIds);
        return (List<AttrEntity>) attrEntities;
    }

    @Override
    public void deleteRelation(AttrGroupRelationVo[] vos) {


        List<AttrAttrgroupRelationEntity> entities = Arrays.asList(vos).stream().map(entity -> {
            AttrAttrgroupRelationEntity attrAttrgroupRelationEntity = new AttrAttrgroupRelationEntity();
            BeanUtils.copyProperties(entity, attrAttrgroupRelationEntity);
            return attrAttrgroupRelationEntity;
        }).collect(Collectors.toList());

        attrAttrgroupRelationService.deleteBatchRelation(entities);
    }

    /**
     * 获取当前分组没有关联的属性
     *
     * @param params
     * @param attrgroupId
     * @return
     */
    @Override
    public PageUtils getNoRelationAttr(Map<String, Object> params, Long attrgroupId) {
        //1.当前分组只能关联自己所属的分类
        AttrGroupEntity attrGroupEntity = attrGroupService.getById(attrgroupId);

        //2.当前分组只能关联别的分组没有引用的分组
        //2.1当前分类下的其他分组
        List<AttrGroupEntity> group = attrGroupService.list(new QueryWrapper<AttrGroupEntity>()
                .eq("catelog_id", attrGroupEntity.getCatelogId()));
        List<Long> groupIds = group.stream().map(item -> item.getAttrGroupId()).collect(Collectors.toList());
        //2.2这些分组的属性
        List<Long> attrIds = new ArrayList<>();
        if (!CollectionUtils.isEmpty(group)) {
            List<AttrAttrgroupRelationEntity> attrgroupRelationEntities = attrAttrgroupRelationService.list(
                    new QueryWrapper<AttrAttrgroupRelationEntity>().in("attr_group_id", groupIds));
            attrIds = attrgroupRelationEntities.stream().map(item -> item.getAttrId()).collect(Collectors.toList());
        }
        QueryWrapper<AttrEntity> wrapper = new QueryWrapper<AttrEntity>()
                .eq("catelog_id", attrGroupEntity.getCatelogId())
                .eq("attr_type", ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode());
        if (!CollectionUtils.isEmpty(attrIds)) {
            wrapper.notIn("attr_id", attrIds);
        }
        //2.3从当前分类的所有属性中移除这些属性

        String key = (String) params.get("key");
        if (StringUtils.isNotBlank(key)) {
            wrapper.and(obj -> {
                obj.eq("attr_id", key).or().eq("attr_name", key);
            });
        }
        IPage<AttrEntity> attrEntityIPage = this.baseMapper.selectPage(new Query<AttrEntity>().getPage(params), wrapper);
        return new PageUtils(attrEntityIPage);
    }
}
