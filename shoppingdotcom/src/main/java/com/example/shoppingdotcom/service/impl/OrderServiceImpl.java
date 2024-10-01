package com.example.shoppingdotcom.service.impl;

import com.example.shoppingdotcom.model.*;
import com.example.shoppingdotcom.repository.CartRepository;
import com.example.shoppingdotcom.repository.ProductOrderRepository;
import com.example.shoppingdotcom.service.OrderService;
import com.example.shoppingdotcom.util.OrderStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private ProductOrderRepository orderRepository;

    @Autowired
    private CartRepository cartRepository;

    @Override
    public void saveOrder(Integer userid, OrderRequestDTO orderRequest) {

        List<CartItem> carts = cartRepository.findByUserId(userid);
        for (CartItem cart : carts) {
            ProductOrder order = new ProductOrder();
            String uniqueOrderId = generateUniqueOrderId();
            order.setOrderId(uniqueOrderId);
            order.setOrderDate(LocalDate.now());

            order.setProduct(cart.getProduct());
            order.setPrice(cart.getProduct().getDiscountedPrice());

            order.setQuantity(cart.getQuantity());
            order.setUser(cart.getUser());

            order.setStatus(OrderStatus.IN_PROGRESS.getName());
            order.setPaymentType(orderRequest.getPaymentType());

            OrderAddress address = getOrderAddress(orderRequest);
            order.setOrderAddress(address);
            orderRepository.save(order);
        }
    }

    @Override
    public List<ProductOrder> getOrdersByUser(Integer userId) {
        return orderRepository.findByUserId(userId);
    }

    @Override
    public Boolean updateOrderStatus(Integer orderId, String status) {
        Optional<ProductOrder> curProductOrder = orderRepository.findById(orderId);
        if (curProductOrder.isPresent()) {
            ProductOrder updatedProductOrder = curProductOrder.get();
            updatedProductOrder.setStatus(status);
            if (Objects.equals(status, OrderStatus.CANCELLED.getName())) {
                Product product = curProductOrder.get().getProduct();
                int curQuantity = curProductOrder.get().getQuantity();
                int oldStock = product.getStock();
                product.setStock(oldStock + curQuantity);
            }
            orderRepository.save(updatedProductOrder);
            return true;
        }
        return false;
    }

    private static OrderAddress getOrderAddress(OrderRequestDTO orderRequest) {
        OrderAddress address = new OrderAddress();
        address.setFirstName(orderRequest.getFirstName());
        address.setLastName(orderRequest.getLastName());
        address.setEmail(orderRequest.getEmail());
        address.setMobileNo(orderRequest.getMobileNo());
        address.setAddress(orderRequest.getAddress());
        address.setCity(orderRequest.getCity());
        address.setState(orderRequest.getState());
        address.setPincode(orderRequest.getPincode());
        return address;
    }

    private String generateUniqueOrderId() {
        String orderId;
        do {
            orderId = UUID.randomUUID().toString();
        } while (orderRepository.existsByOrderId(orderId));
        return orderId;
    }
}
