package com.example.shoppingdotcom.repository;

import com.example.shoppingdotcom.model.ProductOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductOrderRepository extends JpaRepository<ProductOrder, Integer> {

    List<ProductOrder> findByUserId(Integer userId);

    boolean existsByOrderId(String orderId);
}
