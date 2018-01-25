package com.mmall.service.impl;

import com.google.common.collect.Lists;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.dao.CartMapper;
import com.mmall.dao.ProductMapper;
import com.mmall.pojo.Cart;
import com.mmall.pojo.Product;
import com.mmall.service.ICartService;
import com.mmall.util.BigDecimalUtil;
import com.mmall.util.PropertiesUtil;
import com.mmall.vo.CartProductVo;
import com.mmall.vo.CartVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service("iCartService")
public class CartServiceImpl implements ICartService {

    @Autowired
    CartMapper cartMapper;

    @Autowired
    ProductMapper productMapper;

    @Override
    public ServerResponse list(Integer id) {
        // 购物车表和用户表是多对一的关系，产品和购物车表是一对一的关系
        CartVo cartVo = getCartVoLimit(id);
        return ServerResponse.createBySuccess(cartVo);
    }

    // 这些都有并发性的需求
    @Override
    public ServerResponse add(Integer userId, Integer productId, Integer count) {
        if (userId == null || productId == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        // 购买商品，先查看购物车是否有该商品
        Cart cart = cartMapper.selectByUserIdAndProductId(userId, productId);
        if (cart != null) {
            // 增加产品数量，实际中此处还有很多校验
            cart.setQuantity(count + cart.getQuantity());
            cartMapper.updateByPrimaryKeySelective(cart);
        }

        // 向购物车中增加该商品
        cart = new Cart();
        cart.setQuantity(count);
        cart.setChecked(Const.Cart.CHECKED);
        cart.setUserId(userId);
        cart.setProductId(productId);
        cartMapper.insert(cart);

        // 跳转到购物车中，在京东的实现和淘宝的实现中是不跳转到购物车的
        return list(userId);
    }

    @Override
    public ServerResponse update(Integer userId, Integer productId, Integer count) {
        if (userId == null || productId == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Cart cart = cartMapper.selectByUserIdAndProductId(userId, productId);
        if (cart != null) {
            cart.setQuantity(count);
        }
        cartMapper.updateByPrimaryKey(cart);
        return list(userId);
    }

    @Override
    public ServerResponse delete(Integer userId, String productIds) {
        String[] productList = productIds.split(",");
        if (productList == null || productList.length == 0) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        cartMapper.deleleteByUserIdAndProductIds(userId, productList);
        return list(userId);
    }

    @Override
    public ServerResponse selectOrUnSelect(Integer id, Integer productId, Integer checked) {
        cartMapper.updateByUserIdAndProductId(id, productId, checked);
        return list(id);
    }

    @Override
    public ServerResponse<Integer> getCartProductCount(Integer id) {
        return ServerResponse.createBySuccess(cartMapper.selectProductCountByUser(id));
    }


    /**
     * 高复用组装购物车响应对象 ， 最核心的业务方法。
     *
     * @param userId
     * @return
     */
    private CartVo getCartVoLimit(Integer userId) {
        CartVo cartVo = new CartVo();
        List<Cart> cartList = cartMapper.selectByUserId(userId);
        List<CartProductVo> cartProductVoList = Lists.newArrayList();
        BigDecimal cartTotalPrice = new BigDecimal("0");
        for (Cart cartItem : cartList) {
            CartProductVo cartProductVo = new CartProductVo();

            cartProductVo.setId(cartItem.getId());
            cartProductVo.setUserId(cartItem.getUserId());
            cartProductVo.setProductChecked(cartItem.getChecked());
            cartProductVo.setProductId(cartItem.getProductId());

            Product product = productMapper.selectByPrimaryKey(cartItem.getProductId());
            if (product != null) {
                cartProductVo.setProductMainImage(product.getMainImage());
                cartProductVo.setProductPrice(product.getPrice());
                cartProductVo.setProductName(product.getName());
                cartProductVo.setProductStatus(product.getStatus());
                cartProductVo.setProductStock(product.getStock());
                cartProductVo.setProductSubtitle(product.getSubtitle());

                int buyLimitCount = 0;

                if (product.getStock() < cartItem.getQuantity()) {
                    // 库存不足
                    buyLimitCount = product.getStock();
                    cartProductVo.setLimitQuantity(Const.Cart.LIMIT_NUM_FAIL);
                    // 更新购物车对象
                    Cart cart = new Cart();
                    cart.setId(cartItem.getId());
                    cart.setQuantity(buyLimitCount);
                    cartMapper.updateByPrimaryKeySelective(cart);
                } else {
                    buyLimitCount = cartItem.getQuantity();
                    cartProductVo.setLimitQuantity(Const.Cart.LIMIT_NUM_SUCCESS);
                }

                cartProductVo.setQuantity(buyLimitCount);
                // 该宗购物车商品总价
                cartProductVo.setProdutctTotalPrice(BigDecimalUtil.mul(cartProductVo.getProductPrice().doubleValue(), buyLimitCount));
            }

            if (cartProductVo.getProductChecked() == Const.Cart.CHECKED) {
                // 计算到总价中去
                cartTotalPrice = BigDecimalUtil.add(cartTotalPrice.doubleValue(), cartProductVo.getProdutctTotalPrice().doubleValue());
            }
            cartProductVoList.add(cartProductVo);
        }
        cartVo.setCartProductVoList(cartProductVoList);
        cartVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix", "http://img.happymmall.com/"));
        cartVo.setCartTotalPrice(cartTotalPrice);
        cartVo.setAllChecked(selectAllChecked(userId));
        return cartVo;
    }

    private boolean selectAllChecked(Integer userId) {
        return cartMapper.selectUnChecked(userId , Const.Cart.UN_CHECKED) == 0;
    }
}
