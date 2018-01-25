package com.mmall.service;

import com.mmall.common.ServerResponse;
import com.mmall.pojo.Category;

import java.util.List;

public interface ICategoryService {
    ServerResponse<List<Category>> getChildParallelCategory(Integer categoryId);

    ServerResponse addCategory(Integer parentId, String categoryName);

    ServerResponse setCategoryName(Integer categoryId, String categoryName);

    ServerResponse<List<Integer>> getDeepCategory(Integer categoryId);
}
