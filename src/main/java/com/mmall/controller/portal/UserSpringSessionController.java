package com.mmall.controller.portal;

import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import com.mmall.util.CookieUtil;
import com.mmall.util.JsonUtil;
import com.mmall.util.RedisShardedPoolUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Created by cjq on 2018-01-31 15:31
 */
@Controller
@RequestMapping("/user/springsession/")
public class UserSpringSessionController {
    @Autowired
    IUserService iUserService;

    @RequestMapping(value = "login.do" , method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse login(String username , String password , HttpSession session , HttpServletResponse httpServletResponse) {
        ServerResponse response = iUserService.selectLogin(username , password);
        if(response.isSuccess()) {
            // 登陆成功
            session.setAttribute(Const.CURRENT_USER , response.getData());
        }
        return response;
    }

    @RequestMapping(value = "logout.do" , method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse logout(HttpSession session , HttpServletRequest httpServletRequest , HttpServletResponse httpServletResponse) {
        session.removeAttribute(Const.CURRENT_USER);
        return ServerResponse.createBySuccess("退出成功");
    }

    // 查看个人信息
    @RequestMapping(value = "get_user_info.do" , method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse getUserInfo(HttpSession session , HttpServletRequest httpServletRequest) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user == null) {
            return ServerResponse.createByErrorMessage("用户未登陆，无法获取当前用户信息");
        }
        return ServerResponse.createBySuccess(user);
    }

}
