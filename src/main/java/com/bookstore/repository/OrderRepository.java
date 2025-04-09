package com.bookstore.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.bookstore.entity.User;
import com.bookstore.model.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUser(User user);
    List<Order> findByUserId(Long userId);
    Optional<Order> findByIdAndUserId(Long id, Long userId);
    List<Order> findByStatus(String status);
    List<Order> findByPaymentStatus(String paymentStatus);
    
    @Query("SELECT o FROM Order o WHERE o.orderDate BETWEEN ?1 AND ?2")
    List<Order> findByOrderDateBetween(LocalDateTime start, LocalDateTime end);
    
    @Query("SELECT o FROM Order o WHERE o.user = ?1 AND o.status = ?2")
    List<Order> findByUserAndStatus(User user, String status);
    
    @Query("SELECT o FROM Order o WHERE o.user = ?1 AND o.paymentStatus = ?2")
    List<Order> findByUserAndPaymentStatus(User user, String paymentStatus);
} 