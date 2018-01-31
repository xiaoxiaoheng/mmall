package com.mmall.controller.common.interceptor;

import com.mmall.common.Const;
import com.mmall.common.RedisSharedPool;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.util.CookieUtil;
import com.mmall.util.JsonUtil;
import com.mmall.util.RedisShardedPoolUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by cjq on 2018-01-31 17:44
 */
@Slf4j
public class AuthorityInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object handler) throws Exception {
        log.info("preHandler");
        HandlerMethod handlerMethod = (HandlerMethod)handler;

        // 解析HandlerMethod
        String methodName = handlerMethod.getMethod().getName();
        String className = handlerMethod.getBean().getClass().getSimpleName();

        // 解析参数，对方法具体的参数打印日志
        StringBuffer requestParamBuffer = new StringBuffer();
        Map paramMap = httpServletRequest.getParameterMap();
        Iterator it = paramMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            String mapkey = (String) entry.getKey();
            String mapValue = StringUtils.EMPTY;
            Object temp = entry.getValue();
            if(temp instanceof String[]) {
                String[] strs = (String[])temp;
                mapValue = Arrays.toString(strs);
            }
            requestParamBuffer.append(mapkey).append("=").append(mapValue);
        }

        log.info("权限拦截器拦截到请求，className:{},methodName:{},param:{}" , className , methodName , requestParamBuffer);

        User user = null;

        // 判断是不是登陆请求，如果是登陆请求则不进行拦截
        if (StringUtils.equals(className , "UserManageController") && StringUtils.equals(methodName , "login")) {
            log.info("权限拦截器拦截到请求,className:{},methodName:{}",className,methodName);
            //如果是拦截到登录请求，不打印参数，因为参数里面有密码，全部会打印到日志中，防止日志泄露
            return true;
        }


        String loginToken = CookieUtil.readLoginToken(httpServletRequest);
        if(StringUtils.isNotEmpty(loginToken)) {
            String userJsonStr = RedisShardedPoolUtil.get(loginToken);
            user = JsonUtil.string2Obj(userJsonStr , User.class);
        }

        if(user == null || user.getRole() != Const.Role.ROLE_ADMIN) {
            // 因为在这个方法中返回的是false。需要对前端返回信息
            // 重置response  拦截器的重要知识点
            httpServletResponse.reset();
            httpServletResponse.setCharacterEncoding("UTF-8");
            httpServletResponse.setContentType("application/json;charset=UTF-8");
            PrintWriter out = httpServletResponse.getWriter();

            if(user == null) {
                if (StringUtils.equals(className , "ProductManageController") && StringUtils.equals(methodName , "richtextImgUpload")) {
                    Map<String, Object> resultMap = new HashMap<>();
                    resultMap.put("success", false);
                    resultMap.put("msg", "请登录管理员");
                    out.print(JsonUtil.obj2String(resultMap));
                } else {
                    out.print(JsonUtil.obj2String("拦截器拦截，用户未登陆"));
                }
            } else {
                if (StringUtils.equals(className , "ProductManageController") && StringUtils.equals(methodName , "richtextImgUpload")) {
                    Map<String, Object> resultMap = new HashMap<>();
                    resultMap.put("success", false);
                    resultMap.put("msg", "无权限操作");
                    out.print(JsonUtil.obj2String(resultMap));
                } else {
                    out.print(JsonUtil.obj2String("拦截器拦截，用户无权限操作"));
                }
            }

            out.flush();
            out.close();
            return false;
        }


        return true;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {
         log.info("postHandler");
    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {
        log.info("afterCompletion");
    }
}
