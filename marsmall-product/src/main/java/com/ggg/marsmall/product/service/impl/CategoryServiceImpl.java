package com.ggg.marsmall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ggg.common.utils.PageUtils;
import com.ggg.common.utils.Query;
import com.ggg.marsmall.product.dao.CategoryDao;
import com.ggg.marsmall.product.entity.CategoryEntity;
import com.ggg.marsmall.product.service.CategoryBrandRelationService;
import com.ggg.marsmall.product.service.CategoryService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;


@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {


    @Autowired
    private CategoryBrandRelationService categoryBrandRelationService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryEntity> page = this.page(
                new Query<CategoryEntity>().getPage(params),
                new QueryWrapper<CategoryEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<CategoryEntity> listWithTree() {
        //首先查询所有分类
        List<CategoryEntity> entityList = baseMapper.selectList(null);

        //组装成父子的树形结构
        List<CategoryEntity> level1Menus = entityList.stream().filter(categoryEntity -> categoryEntity.getParentCid() == 0)
                .map(menu -> {
                    menu.setChildren(getChildrens(menu, entityList));
                    return menu;
                })
                .sorted((menu1, menu2) -> {
                    return (menu1.getSort()==null?0:menu1.getSort()) - (menu2.getSort()==null?0:menu2.getSort()) ;
                })
                .collect(Collectors.toList());

        return level1Menus;
    }


    private List<CategoryEntity> getChildrens(CategoryEntity root, List<CategoryEntity> all) {
        List<CategoryEntity> childrens = all.stream().filter(categoryEntity -> {
            return Objects.equals(categoryEntity.getParentCid(), root.getCatId());
        }).map(categoryEntity -> {
            categoryEntity.setChildren(getChildrens(categoryEntity, all));
            return categoryEntity;
        }).sorted((menu1, menu2) -> {
            return (menu1.getSort()==null?0:menu1.getSort())-(menu2.getSort()==null?0:menu2.getSort()) ;
        }).collect(Collectors.toList());

        return childrens;
    }


    @Override
    public void removeMenuByIds(List<Long> asList) {
        //TODO 检查当前删除的带单，是否被别的地方引用
        baseMapper.deleteBatchIds(asList);
    }

    @Override
    public Long[] findCatelogPath(Long catelogId) {
        List<Long> paths = new ArrayList<Long>();
        this.findParentPath(catelogId,paths);
        return (Long[]) paths.toArray(new Long[paths.size()]);
    }


    private List<Long> findParentPath(Long catelogId, List<Long> paths){
        CategoryEntity categoryEntity = this.getById(catelogId);
        if (categoryEntity.getParentCid() !=0){
            findParentPath(categoryEntity.getParentCid(),paths);
        }
        paths.add(catelogId);
        return paths;
    }

    @Transactional
    @Override
    public void updateCascade(CategoryEntity category) {
        this.save(category);
        //更新关联字段
        if (StringUtils.isNotBlank(category.getName())){
            categoryBrandRelationService.updateCategory(category.getCatId(),category.getName());

        }
    }
}
