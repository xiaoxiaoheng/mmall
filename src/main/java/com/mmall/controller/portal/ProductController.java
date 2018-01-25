package com.mmall.controller.portal;

import com.github.pagehelper.PageInfo;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.Product;
import com.mmall.pojo.User;
import com.mmall.service.IProductService;
import com.mmall.vo.ProductDetailVo;
import com.mmall.vo.ProductListVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

@RequestMapping("/product/")
@Controller
public class ProductController {
    @Autowired
    IProductService iProductService;

    @RequestMapping(value = "detail.do" , method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<ProductDetailVo> getProductDetail(Integer productId) {
        ServerResponse<ProductDetailVo> response = iProductService.selectDetail(productId);
        return response;
    }

    // 需要修炼一下数据绑定了
    @RequestMapping(value = "list.do" , method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<PageInfo> detail(@RequestParam(required = false) Integer categoryId ,
                                                  @RequestParam(required = false) String keyword ,
                                                  @RequestParam(value = "pageSize" , defaultValue = "10") Integer pageSize ,
                                                  @RequestParam(value = "pageNum" , defaultValue = "1") Integer pageNum ,
                                                  @RequestParam(value = "orderBy" , defaultValue = "") String orderBy) {
        return iProductService.selectProductByCategoryAndKeyword(categoryId , keyword , orderBy , pageNum , pageSize);
    }
}
