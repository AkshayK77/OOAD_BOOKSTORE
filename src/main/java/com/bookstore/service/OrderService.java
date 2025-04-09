package com.bookstore.service;

import java.util.List;
import java.util.Optional;

import com.bookstore.dto.order.OrderRequest;
import com.bookstore.entity.User;
import com.bookstore.model.Order;

public interface OrderService {
    List<Order> findAll();
    
    Optional<Order> findById(Long id);
    
    List<Order> findByUser(User user);
    
    List<Order> findByUserId(Long userId);
    
    Optional<Order> findByIdAndUserId(Long id, Long userId);
    
    Order createOrder(OrderRequest orderRequest, User user);
    
    Order updateOrder(Order order);
    
    void deleteOrder(Long id);
    
    Order updateStatus(Long id, String status);
    
    Order updatePaymentStatus(Long id, String paymentStatus);
    
    void processOrder(Long id);
    
    void shipOrder(Long id);
    
    void deliverOrder(Long id);
    
    void cancelOrder(Long id);
} 