package com.example.shoppingdotcom.service;

import com.example.shoppingdotcom.model.CartItem;

import java.util.List;

public interface CartService {

    public CartItem saveCart(Integer productId, Integer userId);

    public List<CartItem> getCartsByUser(Integer userId);

    public void resetCart(Integer userId);

    public Integer getCountCart(Integer userId);

    public void updateQuantity(String sy, Integer cid);
}
