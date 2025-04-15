package com.bookstore.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bookstore.dto.order.OrderRequest;
import com.bookstore.entity.User;
import com.bookstore.exception.BusinessException;
import com.bookstore.exception.ResourceNotFoundException;
import com.bookstore.mapper.OrderMapper;
import com.bookstore.model.Book;
import com.bookstore.model.Order;
import com.bookstore.model.OrderItem;
import com.bookstore.repository.OrderRepository;
import com.bookstore.service.BookService;
import com.bookstore.service.OrderService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final BookService bookService;
    
    @Override
    public List<Order> findAll() {
        return orderRepository.findAll();
    }
    
    @Override
    public Optional<Order> findById(Long id) {
        return orderRepository.findById(id);
    }
    
    @Override
    public List<Order> findByUser(User user) {
        return orderRepository.findByUser(user);
    }
    
    @Override
    public List<Order> findByUserId(Long userId) {
        return orderRepository.findByUserId(userId);
    }
    
    @Override
    public Optional<Order> findByIdAndUserId(Long id, Long userId) {
        return orderRepository.findByIdAndUserId(id, userId);
    }
    
    @Override
    @Transactional
    public Order createOrder(OrderRequest orderRequest, User user) {
        try {
            // First check stock availability for all items
            for (OrderRequest.OrderItemRequest itemRequest : orderRequest.getOrderItems()) {
                Book book = bookService.findById(itemRequest.getBookId())
                    .orElseThrow(() -> new ResourceNotFoundException("Book", "id", itemRequest.getBookId().toString()));
                
                if (book.getStockQuantity() < itemRequest.getQuantity()) {
                    throw new BusinessException("Insufficient stock for book: " + book.getTitle());
                }
            }
            
            // If all stock checks pass, create the order
            Order order = orderMapper.toEntity(orderRequest, user);
            
            // Set appropriate initial statuses
            order.setStatus("PENDING");
            order.setPaymentStatus("PENDING");
            
            Order savedOrder = orderRepository.save(order);
            
            // Don't update stock yet - we'll do that when payment is confirmed
            // This prevents stock reduction for orders that are never paid
            
            return savedOrder;
        } catch (ResourceNotFoundException e) {
            throw e; // Re-throw to be handled by controller
        } catch (BusinessException e) {
            throw e; // Re-throw to be handled by controller
        } catch (Exception e) {
            e.printStackTrace();
            throw new BusinessException("Failed to create order: " + e.getMessage());
        }
    }
    
    @Override
    @Transactional
    public Order updateOrder(Order order) {
        if (!orderRepository.existsById(order.getId())) {
            throw new ResourceNotFoundException("Order not found with id: " + order.getId());
        }
        return orderRepository.save(order);
    }
    
    @Override
    @Transactional
    public void deleteOrder(Long id) {
        if (!orderRepository.existsById(id)) {
            throw new ResourceNotFoundException("Order not found with id: " + id);
        }
        orderRepository.deleteById(id);
    }
    
    @Override
    @Transactional
    public Order updateStatus(Long id, String status) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("Order not found"));
                
        String previousStatus = order.getStatus();
        order.setStatus(status);
        
        // Stock is now updated when payment status changes to COMPLETED, not here
        
        return orderRepository.save(order);
    }
    
    /**
     * Updates the stock quantity for all books in an order
     */
    private void updateBookStock(Order order) {
        for (OrderItem item : order.getOrderItems()) {
            Book book = item.getBook();
            int currentStock = book.getStockQuantity();
            int orderedQuantity = item.getQuantity();
            
            // Check if we have enough stock
            if (currentStock < orderedQuantity) {
                throw new IllegalStateException("Not enough stock for book: " + book.getTitle());
            }
            
            // Update the stock in the database
            bookService.updateStock(book.getId(), currentStock - orderedQuantity);
        }
    }
    
    @Override
    @Transactional
    public Order updatePaymentStatus(Long id, String paymentStatus) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("Order not found"));
        
        String previousPaymentStatus = order.getPaymentStatus();
        order.setPaymentStatus(paymentStatus);
        
        // Only update stock if the payment status is changed to COMPLETED and wasn't already COMPLETED
        if ("COMPLETED".equals(paymentStatus) && !"COMPLETED".equals(previousPaymentStatus)) {
            updateBookStock(order);
        }
        
        return orderRepository.save(order);
    }
    
    @Override
    @Transactional
    public void processOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("Order not found"));
        order.setStatus("PROCESSING");
        orderRepository.save(order);
    }
    
    @Override
    @Transactional
    public void shipOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("Order not found"));
        order.setStatus("SHIPPED");
        orderRepository.save(order);
    }
    
    @Override
    @Transactional
    public void deliverOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("Order not found"));
        order.setStatus("DELIVERED");
        orderRepository.save(order);
    }
    
    @Override
    @Transactional
    public void cancelOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("Order not found"));
        
        // Check if payment is completed - prevent cancellation of paid orders
        if ("COMPLETED".equals(order.getPaymentStatus())) {
            throw new IllegalStateException("Cannot cancel an order that has been paid for");
        }
        
        order.setStatus("CANCELLED");
        orderRepository.save(order);
    }
} 