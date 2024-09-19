package com.example.shoppingdotcom.service.impl;

import com.example.shoppingdotcom.model.Cart;
import com.example.shoppingdotcom.model.Product;
import com.example.shoppingdotcom.model.Users;
import com.example.shoppingdotcom.repository.CartRepository;
import com.example.shoppingdotcom.repository.ProductRepository;
import com.example.shoppingdotcom.repository.UserRepository;
import com.example.shoppingdotcom.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Override
    public Cart saveCart(Integer productId, Integer userId) {
        Users user = userRepository.findById(userId).get();
        Product product = productRepository.findById(productId).get();

        Cart cartStatus = cartRepository.findByProductIdAndUserId(productId, userId);
        Cart cart = null;

        if (ObjectUtils.isEmpty(cartStatus)) {
            cart = new Cart();
            cart.setProduct(product);
            cart.setUser(user);
            cart.setQuantity(1);
            cart.setTotalPrice(1 * product.getDiscountedPrice());
        } else {
            cart = cartStatus;
            cart.setQuantity(cart.getQuantity() + 1);
            cart.setTotalPrice(cart.getQuantity() * cart.getProduct().getDiscountedPrice());
        }
        return cartRepository.save(cart);
    }

    @Override
    public List<Cart> getCartsByUser(Integer userId) {
        return List.of();
    }
}
