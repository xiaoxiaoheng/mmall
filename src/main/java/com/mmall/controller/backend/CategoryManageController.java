package com.mmall.controller.backend;


import com.mmall.common.ServerResponse;
import com.mmall.service.ICategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@RequestMapping("/manage/category/")
@Controller
public class CategoryManageController {
    @Autowired
    private ICategoryService iCategoryService;

    @RequestMapping("get_category.do")
    @ResponseBody
    public ServerResponse getCategory(@RequestParam(value = "categoryId" , defaultValue = "0") Integer categoryId , HttpServletRequest httpServletRequest) {
        return iCategoryService.getChildParallelCategory(categoryId);
    }

    @RequestMapping("add_category.do")
    @ResponseBody
    public ServerResponse addCategory(@RequestParam(value = "parentId" , defaultValue = "0") Integer parentId ,
                                      String categoryName,
                                      HttpServletRequest httpServletRequest) {
        return iCategoryService.addCategory(parentId , categoryName);
    }

    @RequestMapping("set_category_name.do")
    @ResponseBody
    public ServerResponse setCategoryName(Integer categoryId ,
                                      String categoryName,
                                      HttpServletRequest httpServletRequest) {
        return iCategoryService.setCategoryName(categoryId , categoryName);
    }

    @RequestMapping("get_deep_category.do")
    @ResponseBody
    public ServerResponse getDeepCategory(Integer categoryId ,
                                          HttpServletRequest httpServletRequest) {
        return iCategoryService.getDeepCategory(categoryId);
    }
}
