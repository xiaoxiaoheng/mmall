package com.mmall.controller.backend;

import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.ICategoryService;
import com.mmall.util.CookieUtil;
import com.mmall.util.JsonUtil;
import com.mmall.util.RedisPoolUtil;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@RequestMapping("/manage/category/")
@Controller
public class CategoryManageController {
    @Autowired
    private ICategoryService iCategoryService;

    @RequestMapping("get_category.do")
    @ResponseBody
    public ServerResponse getCategory(@RequestParam(value = "categoryId" , defaultValue = "0") Integer categoryId , HttpServletRequest httpServletRequest) {
        String loginToken = CookieUtil.readLoginToken(httpServletRequest);
        if(loginToken == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode() , ResponseCode.NEED_LOGIN.getDesc());
        }
        String userJsonStr = RedisPoolUtil.get(loginToken);
        User user = JsonUtil.string2Obj(userJsonStr , User.class);
        if(user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode() , ResponseCode.NEED_LOGIN.getDesc());
        }

        if(user.getRole() != Const.Role.ROLE_ADMIN) {
            return ServerResponse.createByErrorMessage("无权限操作，需要管理员权限");
        }
        return iCategoryService.getChildParallelCategory(categoryId);
    }

    @RequestMapping("add_category.do")
    @ResponseBody
    public ServerResponse addCategory(@RequestParam(value = "parentId" , defaultValue = "0") Integer parentId ,
                                      String categoryName,
                                      HttpServletRequest httpServletRequest) {
        String loginToken = CookieUtil.readLoginToken(httpServletRequest);
        if(loginToken == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode() , ResponseCode.NEED_LOGIN.getDesc());
        }
        String userJsonStr = RedisPoolUtil.get(loginToken);
        User user = JsonUtil.string2Obj(userJsonStr , User.class);
        if(user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode() , ResponseCode.NEED_LOGIN.getDesc());
        }
        if(user.getRole() != Const.Role.ROLE_ADMIN) {
            return ServerResponse.createByErrorMessage("无权限操作，需要管理员权限");
        }
        return iCategoryService.addCategory(parentId , categoryName);
    }

    @RequestMapping("set_category_name.do")
    @ResponseBody
    public ServerResponse setCategoryName(Integer categoryId ,
                                      String categoryName,
                                      HttpServletRequest httpServletRequest) {
        String loginToken = CookieUtil.readLoginToken(httpServletRequest);
        if(loginToken == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode() , ResponseCode.NEED_LOGIN.getDesc());
        }
        String userJsonStr = RedisPoolUtil.get(loginToken);
        User user = JsonUtil.string2Obj(userJsonStr , User.class);
        if(user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode() , ResponseCode.NEED_LOGIN.getDesc());
        }
        if(user.getRole() != Const.Role.ROLE_ADMIN) {
            return ServerResponse.createByErrorMessage("无权限操作，需要管理员权限");
        }
        return iCategoryService.setCategoryName(categoryId , categoryName);
    }

    @RequestMapping("get_deep_category.do")
    @ResponseBody
    public ServerResponse getDeepCategory(Integer categoryId ,
                                          HttpServletRequest httpServletRequest) {
        String loginToken = CookieUtil.readLoginToken(httpServletRequest);
        if(loginToken == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode() , ResponseCode.NEED_LOGIN.getDesc());
        }
        String userJsonStr = RedisPoolUtil.get(loginToken);
        User user = JsonUtil.string2Obj(userJsonStr , User.class);
        if(user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode() , ResponseCode.NEED_LOGIN.getDesc());
        }
        if(user.getRole() != Const.Role.ROLE_ADMIN) {
            return ServerResponse.createByErrorMessage("无权限操作，需要管理员权限");
        }
        return iCategoryService.getDeepCategory(categoryId);
    }


}
