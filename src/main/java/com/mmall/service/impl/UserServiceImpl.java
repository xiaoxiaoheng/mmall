package com.mmall.service.impl;

import com.github.pagehelper.PageHelper;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.common.TokenCache;
import com.mmall.dao.UserMapper;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import com.mmall.util.MD5Util;
import com.mmall.util.RedisPoolUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service("iUserService")
public class UserServiceImpl implements IUserService{

    @Autowired
    private UserMapper userMapper;

    @Override
    public ServerResponse selectLogin(String username , String password) {
        ServerResponse response = checkValid(username , Const.Type.USERNAME);
        // 成功了代表没有该用户，则直接返回
        if(response.isSuccess()) {
            return response;
        }
        // md5对密码进行加密
        password = MD5Util.MD5EncodeUtf8(password);
        User user = userMapper.selectLogin(username , password);
        if(user != null) {
            user.setPassword(StringUtils.EMPTY);
            response = ServerResponse.createBySuccess(user);
        } else {
            response = ServerResponse.createByErrorMessage("密码错误");
        }
        return response;
    }

    @Override
    public ServerResponse register(User user) {
        // 检查email和username是否已存在
        ServerResponse response = checkValid(user.getUsername() , Const.Type.USERNAME);
        if(!response.isSuccess())
            return response;
        response = checkValid(user.getEmail() , Const.Type.EMAIL);
        if(!response.isSuccess())
            return response;
        // 校验完毕，执行插入操作
        user.setRole(Const.Role.ROLE_CUSTOMER);
        user.setPassword(MD5Util.MD5EncodeUtf8(user.getPassword()));
        int count = userMapper.insert(user);
        if(count > 0) {
            return ServerResponse.createBySuccessMessage("注册成功");
        }
        return ServerResponse.createByErrorMessage("注册失败");
    }

    @Override
    public ServerResponse checkValid(String str, String type) {
        if(StringUtils.isBlank(type) || StringUtils.isBlank(str)) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode() , ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }

        if(type.equals(Const.Type.USERNAME)) {
            int count = userMapper.checkUsername(str);
            if(count > 0) {
                return ServerResponse.createByErrorMessage("用户名已存在");
            } else {
                return ServerResponse.createBySuccess();
            }
        }

        if(type.equals(Const.Type.EMAIL)) {
            int count = userMapper.checkEmail(str);
            if(count > 0) {
                return ServerResponse.createByErrorMessage("email已存在");
            } else {
                return ServerResponse.createBySuccess();
            }
        }

        return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode() , ResponseCode.ILLEGAL_ARGUMENT.getDesc());
    }

    @Override
    public ServerResponse<User> updateInformation(User user) {
        // email检查，检查这个email是否和其它用户的Email冲突
        int resultCount = userMapper.checkEmailByUserId(user.getEmail() , user.getId());
        if(resultCount > 0) {
            return ServerResponse.createByErrorMessage("email已存在，请更换email再进行尝试");
        }

        User updateUser = new User();
        updateUser.setId(user.getId());
        updateUser.setQuestion(user.getQuestion());
        updateUser.setEmail(user.getEmail());
        updateUser.setPhone(user.getPhone());
        updateUser.setAnswer(user.getAnswer());

        int updateCount = userMapper.updateByPrimaryKeySelective(updateUser);
        if(updateCount > 0) {
            return ServerResponse.createBySuccess(updateUser);
        }

        return ServerResponse.createByErrorMessage("更新个人信息失败");
    }

    @Override
    public ServerResponse getInformation(Integer id) {
        User user = userMapper.selectByPrimaryKey(id);
        if(user != null) {
            user.setPassword(StringUtils.EMPTY);
            return ServerResponse.createBySuccess(user);
        } else {
            return ServerResponse.createByErrorMessage("找不到当前用户");
        }
    }

    @Override
    public ServerResponse getQuestion(String username) {
        ServerResponse response = checkValid(username , Const.Type.USERNAME);
        if(response.isSuccess()) {
            return response;
        }
        // 去找出问题
        String question = userMapper.selectQuestionByUsername(username);
        if(question != null) {
            return ServerResponse.createBySuccess(question);
        } else {
            return ServerResponse.createByErrorMessage("找回密码的问题为空");
        }
    }

    @Override
    public ServerResponse<String> checkAnswer(String username, String question, String answer) {
        int resultCount =  userMapper.checkAnswer(username , question , answer);
        if(resultCount == 0) {
            return ServerResponse.createByErrorMessage("问题答案错误");
        }
        String forgetToken = UUID.randomUUID().toString();
        RedisPoolUtil.setEx(Const.TOKEN_PREFIX + username , forgetToken , 60 * 60 * 12);
        return ServerResponse.createBySuccess(forgetToken);
    }

    @Override
    public ServerResponse<String> forgetResetPassword(String username, String password, String forgetToken) {
        // 验证参数是否有效
        if(StringUtils.isBlank(forgetToken)) {
            return ServerResponse.createByErrorMessage("参数错误，token需要传递");
        }
        ServerResponse validResponse  = checkValid(username , Const.Type.USERNAME);
        if(validResponse.isSuccess()) {
            return ServerResponse.createByErrorMessage("用户不存在");
        }
        // 有效，进行密码的更新 , token的key值和用户唯一对应
        String token = RedisPoolUtil.get(Const.TOKEN_PREFIX + username);
        if(StringUtils.isBlank(token)) {
            return ServerResponse.createByErrorMessage("token无效或token过期");
        }

        if(StringUtils.equals(forgetToken , token)) {
            String md5Password = MD5Util.MD5EncodeUtf8(password);
            int rowCount = userMapper.updatePasswordByUsername(username , md5Password);
            if(rowCount > 0) {
                return ServerResponse.createBySuccessMessage("修改密码成功");
            }
            return ServerResponse.createByErrorMessage("修改密码失败");
        } else {
            return ServerResponse.createByErrorMessage("token错误，请重新获取重置密码的token");
        }
    }

    @Override
    public ServerResponse<String> resetPassword(User user , String passwordOld, String passwordNew) {
        // 根据用户id和密码的对应关系在数据库中查找。
        int resultCount = userMapper.checkPassword(user.getId() , MD5Util.MD5EncodeUtf8(passwordOld));
        if(resultCount == 0) {
            return ServerResponse.createByErrorMessage("旧密码错误");
        }
        user.setPassword(MD5Util.MD5EncodeUtf8(passwordNew));
        resultCount = userMapper.updateByPrimaryKeySelective(user);
        if(resultCount > 0) {
            return ServerResponse.createBySuccessMessage("密码更新成功");
        }
        return ServerResponse.createByErrorMessage("密码更新失败");
    }

    public ServerResponse checkAdminRole(User user) {
        if(user.getRole().intValue() == Const.Role.ROLE_ADMIN) {
            return ServerResponse.createBySuccess();
        }
        return ServerResponse.createByError();
    }
}
