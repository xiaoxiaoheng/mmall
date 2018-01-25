package com.mmall.service;

import com.github.pagehelper.PageInfo;
import com.mmall.common.ServerResponse;
import com.mmall.vo.OrderVo;

import java.util.Map;

/**
 * Created by cjq on 2018-01-15 10:18
 */
public interface IOrderService {

    ServerResponse pay(Integer id, Long orderNo , String path);

    ServerResponse aliCallback(Map<String, String> params);

    ServerResponse queryOrderPayStatus(Integer id, Long orderNo);

    ServerResponse getOrderList(Integer id, int pageNum, int pageSize);

    ServerResponse getOrderDetail(Integer id, Long orderNo);

    ServerResponse getOrderCartProduct(Integer id);

    ServerResponse cancel(Integer id, Long orderNo);

    ServerResponse createOrder(Integer id, Integer shippingId);

    ServerResponse<PageInfo> manageSearch(Long orderNo, int pageNum, int pageSize);

    ServerResponse<String> manageSendGoods(Long orderNo);

    ServerResponse<OrderVo> manageDetail(Long orderNo);

    ServerResponse manageList(int pageNum, int pageSize);
}
