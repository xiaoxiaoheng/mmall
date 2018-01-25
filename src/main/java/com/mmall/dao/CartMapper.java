package com.mmall.dao;

import com.mmall.pojo.Cart;
import com.sun.org.glassfish.external.probe.provider.annotations.ProbeParam;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CartMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Cart record);

    int insertSelective(Cart record);

    Cart selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Cart record);

    int updateByPrimaryKey(Cart record);

    int updateByUserIdAndProductId(@Param("userId") Integer userId, @Param("productId") Integer productId , @Param("checked") Integer checked);

    void deleleteByUserIdAndProductIds(@Param("userId") Integer userId, @Param("productList") String[] productList);

    int selectProductCountByUser(Integer id);

    Cart selectByUserIdAndProductId(@Param("userId") Integer userId, @Param("productId") Integer productId);

    List<Cart> selectByUserId(Integer userId);

    int selectUnChecked(@Param("userId") Integer userId , @Param("unChecked") Integer unChecked);

    List<Cart> selectCheckedCartByUserId(Integer userId);
}