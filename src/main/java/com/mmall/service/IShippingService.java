package com.mmall.service;

import com.github.pagehelper.PageInfo;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.Shipping;

import java.util.Map;


public interface IShippingService {
    ServerResponse<Map> add(Integer id, Shipping shipping);

    ServerResponse del(Integer id, Integer shippingId);

    ServerResponse update(Integer id, Shipping shipping);

    ServerResponse<Shipping> select(Integer id, Integer shippingId);

    ServerResponse<PageInfo> list(Integer id, Integer pageNum, Integer pageSize);
}
