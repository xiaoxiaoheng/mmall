package com.mmall.service;

import com.github.pagehelper.PageInfo;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.Category;
import com.mmall.pojo.Product;
import com.mmall.vo.ProductDetailVo;

public interface IProductService {
    ServerResponse<PageInfo> list(Integer pageNum, Integer pageSize);

    ServerResponse<PageInfo> search(Integer pageNum, Integer pageSize, Integer productId, String productName);


    ServerResponse<ProductDetailVo> manageDetail(Integer productId);

    ServerResponse<String> updateSaleStatus(Integer productId, Integer status);

    ServerResponse<String> updateOrSave(Product product);

    ServerResponse<ProductDetailVo> selectDetail(Integer productId);

    ServerResponse<PageInfo> selectProductByCategoryAndKeyword(Integer categoryId, String keyword, String orderBy, Integer pageNum, Integer pageSize);
}
