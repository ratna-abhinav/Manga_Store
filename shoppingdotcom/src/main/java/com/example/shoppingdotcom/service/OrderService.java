package com.example.shoppingdotcom.service;

import com.example.shoppingdotcom.model.OrderRequestDTO;
import com.example.shoppingdotcom.model.ProductOrder;
import org.springframework.data.domain.Page;

import java.util.List;

public interface OrderService {

    public void saveOrder(Integer userid, OrderRequestDTO orderRequest) throws Exception;

    public List<ProductOrder> getOrdersByUser(Integer userId);

    public ProductOrder updateOrderStatus(Integer id, String status);

    List<ProductOrder> getAllOrders();

    ProductOrder getOrdersByOrderId(String orderId);

    Page<ProductOrder> getAllOrdersPagination(Integer pageNo, Integer pageSize);
}
