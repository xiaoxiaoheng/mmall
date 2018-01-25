package com.mmall.controller.backend;

import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import net.sf.jsqlparser.schema.Server;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

@RequestMapping("/manage/user/")
public class UserController {

    @Autowired
    private IUserService iUserService;

    @RequestMapping("login.do")
    @ResponseBody
    public ServerResponse login(String username , String password , HttpSession session) {
        ServerResponse response = iUserService.selectLogin(username , password);
        if(response.isSuccess()) {
            User user = (User) response.getData();
            if(user.getRole() == Const.Role.ROLE_ADMIN) {
                session.setAttribute(Const.CURRENT_USER , user);
                return response;
            } else {
                return ServerResponse.createByErrorMessage("不是管理员，无法登陆");
            }
        }
        return response;
    }
}
