package com.mmall.service.impl;

import com.github.pagehelper.StringUtil;
import com.mmall.common.ServerResponse;
import com.mmall.dao.CategoryMapper;
import com.mmall.pojo.Category;
import com.mmall.service.ICategoryService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service("iCategoryService")
@Slf4j
public class CategoryServiceImpl implements ICategoryService{

    @Autowired
    private CategoryMapper categoryMapper;

    @Override
    public ServerResponse<List<Category>> getChildParallelCategory(Integer categoryId) {
        List<Category> categoryList = categoryMapper.selectChildParallelByParentId(categoryId);
        if(categoryList.isEmpty()) {
            log.info("未找到当前分类的子类");
        }
        return ServerResponse.createBySuccess(categoryList);
    }

    @Override
    public ServerResponse addCategory(Integer parentId, String categoryName) {
        if(parentId == null || StringUtils.isBlank(categoryName)) {
            return ServerResponse.createByErrorMessage("添加品类参数错误");
        }
        Category category = new Category();
        category.setParentId(parentId);
        category.setName(categoryName);
        category.setStatus(true);
        int rowCount = categoryMapper.insert(category);
        if(rowCount > 0) {
            return ServerResponse.createBySuccessMessage("添加品类成功");
        }
        return ServerResponse.createByErrorMessage("添加品类失败");
    }

    @Override
    public ServerResponse setCategoryName(Integer categoryId, String categoryName) {
        if(categoryId == null || StringUtils.isBlank(categoryName)) {
            return ServerResponse.createByErrorMessage("添加品类参数错误");
        }
        Category category = new Category();
        category.setName(categoryName);
        category.setId(categoryId);
        int rowCount = categoryMapper.updateByPrimaryKeySelective(category);
        if(rowCount > 0) {
            return ServerResponse.createBySuccessMessage("更新品类名字成功");
        }
        return ServerResponse.createByErrorMessage("更新品类名字失败");
    }

    @Override
    public ServerResponse<List<Integer>> getDeepCategory(Integer categoryId) {
        List<Integer> resultList = new ArrayList<>();
        // 因为0节点是不存在的，所以需要进行判断，对于前端传过来的不合法的节点，这样也能进行过滤
        Category category = categoryMapper.selectByPrimaryKey(categoryId);
        if(category != null || categoryId == 0) {
            findParent(categoryId , resultList);
        }
        return ServerResponse.createBySuccess(resultList);
    }


    private void findParent(Integer categoryId , List<Integer> resultList) {
        // 因为0节点是不存在的，所以需要进行判断，对于前端传过来的不合法的节点，这样也能进行过滤
        if(categoryId != 0)
            resultList.add(categoryId);
        List<Integer> childrentList = categoryMapper.selectChildByParentId(categoryId);
        for(Integer id : childrentList) {
            findParent(id , resultList);
        }
    }
}
