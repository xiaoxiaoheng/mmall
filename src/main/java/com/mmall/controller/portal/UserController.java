package com.mmall.controller.portal;

import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/user/")
public class UserController {

    @Autowired
    IUserService iUserService;

    @RequestMapping(value = "login.do" , method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse login(String username , String password , HttpSession session) {
        ServerResponse response = iUserService.selectLogin(username , password);
        if(response.isSuccess()) {
            // 登陆成功
            session.setAttribute(Const.CURRENT_USER , response.getData());
        }
        return response;
    }

    @RequestMapping(value = "register.do" , method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse register(User user , HttpSession session) {
        return iUserService.register(user);
    }

    @RequestMapping(value = "logout.do" , method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse logout(HttpSession session) {
        session.removeAttribute(Const.CURRENT_USER);
        return ServerResponse.createBySuccess("退出成功");
    }

    @RequestMapping(value = "check_valid.do" , method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse checkValid(String str , String type) {
        return  iUserService.checkValid(str , type);
    }

    // 查看个人信息
    @RequestMapping(value = "get_user_info.do" , method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse getUserInfo(HttpSession session) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user == null) {
            return ServerResponse.createByErrorMessage("用户未登陆，无法获取当前用户信息");
        }
        return ServerResponse.createBySuccess(user);
    }

    // 登陆状态下更新个人信息,首先检查用户是否登陆，然后检查email是否存在，最后进行更新。
    @RequestMapping(value = "update_information.do" , method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse updateInformation(User user , HttpSession session) {
        User currentUser = (User) session.getAttribute(Const.CURRENT_USER);
        if(currentUser == null) {
            return ServerResponse.createByErrorMessage("用户未登陆");
        }
        user.setId(currentUser.getId());
        user.setUsername(currentUser.getUsername());
        ServerResponse response = iUserService.updateInformation(user);
        if(response.isSuccess()) {
            session.setAttribute(Const.CURRENT_USER , response.getData());
        }
        return response;
    }

    @RequestMapping(value = "get_information.do" , method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse getInformation(HttpSession session) {
        User currentUser = (User) session.getAttribute(Const.CURRENT_USER);
        if(currentUser == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode() , ResponseCode.NEED_LOGIN.getDesc());
        }
        return iUserService.getInformation(currentUser.getId());
    }

    @RequestMapping(value = "forget_get_question.do" , method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse forgetGetQuestion(String username) {
        return iUserService.getQuestion(username);
    }

    @RequestMapping(value = "forget_check_answer.do" , method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> forgetCheckAnswer(String username , String question , String answer) {
        return iUserService.checkAnswer(username , question , answer);
    }

    @RequestMapping(value = "forget_reset_password.do" , method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> forgetResetPassword(String username , String password , String forgetToken) {
        return iUserService.forgetResetPassword(username , password , forgetToken);
    }

    @RequestMapping(value = "reset_password.do" , method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> resetPassword(String passwordOld , String passwordNew , HttpSession session) {
        User currentUser = (User) session.getAttribute(Const.CURRENT_USER);
        if(currentUser == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode() , ResponseCode.NEED_LOGIN.getDesc());
        }
        return iUserService.resetPassword(currentUser , passwordOld , passwordNew);
    }
}
