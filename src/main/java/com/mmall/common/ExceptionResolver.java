package com.mmall.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.json.MappingJacksonJsonView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by cjq on 2018-01-31 17:24
 */
@Slf4j
@Component
public class ExceptionResolver implements HandlerExceptionResolver {
    @Override
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        log.error("{} Exception" , request.getRequestURI() , ex);
        // 因为使用的是jackson1.x，所以要使用MappingJacksonJsonView。否则可以使用MappingJackson2JsonView
        ModelAndView modelAndView = new ModelAndView(new MappingJacksonJsonView());
        modelAndView.addObject("status" , ResponseCode.ERROR.getCode());
        modelAndView.addObject("msg" , "接口异常，详情查看服务端日志");
        modelAndView.addObject("data" , ex.toString());
        return modelAndView;
    }
}
