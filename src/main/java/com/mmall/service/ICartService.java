package com.mmall.service;

import com.mmall.common.ServerResponse;

public interface ICartService {
    ServerResponse list(Integer id);

    ServerResponse add(Integer id, Integer productId, Integer count);

    ServerResponse update(Integer id, Integer productId, Integer count);

    ServerResponse delete(Integer id, String productIds);

    ServerResponse selectOrUnSelect(Integer id, Integer productId , Integer checked);

    ServerResponse<Integer> getCartProductCount(Integer id);

}
