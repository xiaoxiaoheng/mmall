package com.mmall.controller.portal;

import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.demo.trade.config.Configs;
import com.google.common.collect.Maps;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.Order;
import com.mmall.pojo.User;
import com.mmall.service.IOrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Iterator;
import java.util.Map;

@RequestMapping("/order/")
@Controller
public class OrderController {

    /**
     * order和orderItem是一对多的关系。userId和orderNo作为联合索引。
     */

    private Logger logger = LoggerFactory.getLogger(OrderController.class);

    @Autowired
    private IOrderService iOrderService;

    @RequestMapping("create.do")
    @ResponseBody
    public ServerResponse create(HttpSession session , Integer shippingId) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user ==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
        return iOrderService.createOrder(user.getId(),shippingId);
    }

    @RequestMapping("cancel.do")
    @ResponseBody
    public ServerResponse cancel(HttpSession session , Long orderNo) {
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user ==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iOrderService.cancel(user.getId() , orderNo);
    }

    // 获取购物车中已经选中的商品详情。
    @RequestMapping("get_order_cart_product.do")
    @ResponseBody
    public ServerResponse getOrderCartProduct(HttpSession session){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user ==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iOrderService.getOrderCartProduct(user.getId());

    }

    @RequestMapping("detail.do")
    @ResponseBody
    public ServerResponse detail(HttpSession session , Long orderNo){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user ==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iOrderService.getOrderDetail(user.getId() , orderNo);
    }

    @RequestMapping("list.do")
    @ResponseBody
    public ServerResponse list(HttpSession session, @RequestParam(value = "pageNum",defaultValue = "1") int pageNum, @RequestParam(value = "pageSize",defaultValue = "10") int pageSize){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iOrderService.getOrderList(user.getId() , pageNum , pageSize);
    }





    // 支付模块

    @RequestMapping("pay.do")
    @ResponseBody
    public ServerResponse pay(HttpSession session , Long orderNo , HttpServletRequest request) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode() , ResponseCode.NEED_LOGIN.getDesc());
        }
        String path = request.getServletContext().getRealPath("upload");
        return iOrderService.pay(user.getId() , orderNo , path);
    }

    @RequestMapping("alipay_callback.do")
    @ResponseBody
    public Object alipayCallback(HttpServletRequest request) {
        Map<String , String> params = Maps.newHashMap();
        Map requestParams = request.getParameterMap();
        Iterator iterator = requestParams.keySet().iterator();
        while (iterator.hasNext()) {
            String name = (String) iterator.next();
            String[] values = (String[]) requestParams.get(name);
            String valuesStr = "";
            for(int i = 0 ; i < values.length ; i ++) {
                valuesStr = (i == values.length - 1) ? valuesStr + values[i] : valuesStr + values[i] + ",";
            }
            params.put(name , valuesStr);
        }

        logger.info("支付宝回调，sign:{} , trade_status:{} , 参数:{}" , params.get("sign") , params.get("trade_status") , params.toString());

        // 根据支付宝接入文档说明，需要除去sign,sign_type这两个参数，其余的都要进行验签
        params.remove("sign");
        params.remove("sign_type");

        // 根据读源码可知，对rsa2进行验签
        try {
            boolean alipayRSACheckedV2 = AlipaySignature.rsaCheckV2(params , Configs.getPublicKey() , "utf-8" , Configs.getSignType());
            if(!alipayRSACheckedV2) {
                return ServerResponse.createByErrorMessage("非法请求,验证不通过,再恶意请求我就报警找网警了");
            }
        } catch (AlipayApiException e) {
            logger.error("支付宝验证回调异常" , e);
        }

        // TODO: 2018/1/15 验证有关订单的数据
        ServerResponse serverResponse = iOrderService.aliCallback(params);
        if(serverResponse.isSuccess()) {
            return Const.AlipayCallback.RESPONSE_SUCCESS;
        }
        return Const.AlipayCallback.RESPONSE_FAILED;
    }

    @RequestMapping("query_order_pay_status.do")
    @ResponseBody
    public ServerResponse<Boolean> queryOrderPayStatus(HttpSession session , Long orderNo) {
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user ==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }

        ServerResponse serverResponse = iOrderService.queryOrderPayStatus(user.getId() , orderNo);
        if(serverResponse.isSuccess()) {
            // 成功
            return ServerResponse.createBySuccess(true);
        }
        return ServerResponse.createByError();
    }
}
