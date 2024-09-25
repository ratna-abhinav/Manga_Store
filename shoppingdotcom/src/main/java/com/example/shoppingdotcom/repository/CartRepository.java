package com.example.shoppingdotcom.repository;

import com.example.shoppingdotcom.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CartRepository extends JpaRepository<CartItem, Integer> {

    public CartItem findByProductIdAndUserId(Integer productId, Integer userId);

    public Integer countByUserId(Integer userId);

    public List<CartItem> findByUserId(Integer userId);
}
