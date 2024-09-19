package com.example.shoppingdotcom.repository;

import com.example.shoppingdotcom.model.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepository extends JpaRepository<Cart, Integer> {
    public Cart findByProductIdAndUserId(Integer productId, Integer userId);
}
