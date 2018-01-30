package com.mmall.service;

import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;

public interface IUserService {

    public ServerResponse selectLogin(String username , String password);

    ServerResponse register(User user);

    ServerResponse checkValid(String str, String type);

    ServerResponse<User> updateInformation(User user);

    ServerResponse getInformation(Integer id);

    ServerResponse getQuestion(String username);

    ServerResponse<String> checkAnswer(String username, String question, String answer);

    ServerResponse<String> forgetResetPassword(String username, String password, String forgetToken);

    ServerResponse<String> resetPassword(User currentUser , String passwordOld, String passwordNew);

    ServerResponse checkAdminRole(User user);

}
