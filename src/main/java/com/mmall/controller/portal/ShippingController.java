package com.mmall.controller.portal;

import com.github.pagehelper.PageInfo;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.Shipping;
import com.mmall.pojo.User;
import com.mmall.service.IShippingService;
import com.mmall.util.CookieUtil;
import com.mmall.util.JsonUtil;
import com.mmall.util.RedisPoolUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@RequestMapping("/shipping/")
@Controller
public class ShippingController {

    @Autowired
    IShippingService shippingService;

    @RequestMapping("add.do")
    @ResponseBody
    public ServerResponse add(Shipping shipping , HttpServletRequest httpServletRequest) {
        String loginToken = CookieUtil.readLoginToken(httpServletRequest);
        if(loginToken == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode() , ResponseCode.NEED_LOGIN.getDesc());
        }
        String userJsonStr = RedisPoolUtil.get(loginToken);
        User user = JsonUtil.string2Obj(userJsonStr , User.class);
        if(user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode() , ResponseCode.NEED_LOGIN.getDesc());
        }
        // add
        return shippingService.add(user.getId() , shipping);
    }

    @RequestMapping("del.do")
    @ResponseBody
    public ServerResponse del(Integer shippingId , HttpServletRequest httpServletRequest) {
        String loginToken = CookieUtil.readLoginToken(httpServletRequest);
        if(loginToken == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode() , ResponseCode.NEED_LOGIN.getDesc());
        }
        String userJsonStr = RedisPoolUtil.get(loginToken);
        User user = JsonUtil.string2Obj(userJsonStr , User.class);
        if(user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode() , ResponseCode.NEED_LOGIN.getDesc());
        }
        // 还是将用户自己的id传过去为好，避免横向越权
        return shippingService.del(user.getId() , shippingId);
    }

    @RequestMapping("update.do")
    @ResponseBody
    public ServerResponse update(Shipping shipping , HttpServletRequest httpServletRequest) {
        String loginToken = CookieUtil.readLoginToken(httpServletRequest);
        if(loginToken == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode() , ResponseCode.NEED_LOGIN.getDesc());
        }
        String userJsonStr = RedisPoolUtil.get(loginToken);
        User user = JsonUtil.string2Obj(userJsonStr , User.class);
        if(user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode() , ResponseCode.NEED_LOGIN.getDesc());
        }
        return shippingService.update(user.getId() , shipping);
    }

    @RequestMapping("select.do")
    @ResponseBody
    public ServerResponse<Shipping> select(Integer shippingId , HttpServletRequest httpServletRequest) {
        String loginToken = CookieUtil.readLoginToken(httpServletRequest);
        if(loginToken == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode() , ResponseCode.NEED_LOGIN.getDesc());
        }
        String userJsonStr = RedisPoolUtil.get(loginToken);
        User user = JsonUtil.string2Obj(userJsonStr , User.class);
        if(user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode() , ResponseCode.NEED_LOGIN.getDesc());
        }
        return shippingService.select(user.getId() , shippingId);
    }

    @RequestMapping("list.do")
    @ResponseBody
    public ServerResponse<PageInfo> list(HttpServletRequest httpServletRequest ,
                                         @RequestParam(value = "pageSize" , defaultValue = "10") Integer pageSize ,
                                         @RequestParam(value = "pageNum" , defaultValue = "1") Integer pageNum) {
        //User user = (User) session.getAttribute(Const.CURRENT_USER);
        String loginToken = CookieUtil.readLoginToken(httpServletRequest);
        if(loginToken == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode() , ResponseCode.NEED_LOGIN.getDesc());
        }
        String userJsonStr = RedisPoolUtil.get(loginToken);
        User user = JsonUtil.string2Obj(userJsonStr , User.class);
        if(user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode() , ResponseCode.NEED_LOGIN.getDesc());
        }
        return shippingService.list(user.getId() , pageNum , pageSize);
    }
}
