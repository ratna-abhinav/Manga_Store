package com.example.shoppingdotcom.service;

import com.example.shoppingdotcom.model.Cart;

import java.util.List;

public interface CartService {

    public Cart saveCart(Integer productId, Integer userId);

    public List<Cart> getCartsByUser(Integer userId);
}
