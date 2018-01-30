package com.mmall.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by cjq on 2018-01-30 21:14
 */
@Slf4j
public class CookieUtil {
    private final static String COOKIE_DOMAIN = ".happymmall.com";
    private final static String COOKIE_NAME = "mmall_login_token";

    public static void writeLoginToken(HttpServletResponse response , String token) {
        Cookie cookie = new Cookie(COOKIE_NAME , token);
        cookie.setDomain(COOKIE_DOMAIN);
        // 代表设置在根目录
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        // 单位是秒
        // 如果这个maxage不设置的话，cookie就不会写入，而是写在内存。只在当前页面有效
        cookie.setMaxAge(60 * 60 * 24 * 365);
        log.info("write cookieName:{},cookieValue:{}" , cookie.getName() , cookie.getValue());
        response.addCookie(cookie);
    }

    /**
     * 从客户端读取cookie值，与客户端保存的比较。
     * @param request
     * @return
     */
    public static String readLoginToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if(cookies != null) {
            for(Cookie ck : cookies) {
                log.info("read CookieName:{} , cookieValue:{}",ck.getName() , ck.getValue());
                if(StringUtils.equals(ck.getName() , COOKIE_NAME)) {
                    log.info("return cookieName:{},cookieValue:{}",ck.getName(),ck.getValue());
                    return ck.getValue();
                }
            }
        }
        return null;
    }

    public static void delLoginToken(HttpServletRequest request,HttpServletResponse response){
        Cookie[] cookies = request.getCookies();
        if(cookies != null) {
            for(Cookie cookie : cookies) {
                if(StringUtils.equals(cookie.getName() , COOKIE_NAME)) {
                    cookie.setDomain(COOKIE_DOMAIN);
                    cookie.setPath("/");
                    cookie.setMaxAge(0); // 设置成0，客户端删除这个cookie
                    log.info("del cookieName:{},cookieValue:{}",cookie.getName(),cookie.getValue());
                    response.addCookie(cookie);
                    return;
                }
            }
        }
    }
}
