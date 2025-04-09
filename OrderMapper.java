package com.bookstore.mapper;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.bookstore.dto.order.OrderRequest;
import com.bookstore.dto.order.OrderResponse;
import com.bookstore.entity.User;
import com.bookstore.model.Book;
import com.bookstore.model.Order;
import com.bookstore.model.OrderItem;
import com.bookstore.service.BookService;

@Component
public class OrderMapper {
    
    private final BookService bookService;
    
    // Explicit constructor instead of using @RequiredArgsConstructor
    public OrderMapper(BookService bookService) {
        this.bookService = bookService;
    }
    
    public Order toEntity(OrderRequest request, User user) {
        Order order = new Order();
        order.setUser(user);
        order.setShippingAddress(request.getShippingAddress());
        
        List<OrderItem> orderItems = request.getOrderItems().stream()
            .map(itemRequest -> {
                OrderItem orderItem = new OrderItem();
                orderItem.setOrder(order);
                
                Book book = bookService.findById(itemRequest.getBookId())
                    .orElseThrow(() -> new IllegalStateException("Book not found"));
                orderItem.setBook(book);
                orderItem.setQuantity(itemRequest.getQuantity());
                orderItem.setPrice(book.getPrice());
                
                return orderItem;
            })
            .collect(Collectors.toList());
        
        order.setOrderItems(orderItems);
        
        // Calculate total amount from order items
        BigDecimal totalAmount = orderItems.stream()
            .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        // If the request has a totalAmount, use that instead
        if (request.getTotalAmount() != null) {
            totalAmount = request.getTotalAmount();
        }
        
        // Make sure we set the totalAmount on the order
        order.setTotalAmount(totalAmount);
        
        return order;
    }
    
    public OrderResponse toResponse(Order order) {
        OrderResponse response = new OrderResponse();
        response.setId(order.getId());
        response.setUserId(order.getUser().getId());
        response.setUsername(order.getUser().getUsername());
        response.setOrderItems(order.getOrderItems().stream()
            .map(this::toOrderItemResponse)
            .collect(Collectors.toList()));
        response.setTotalAmount(order.getTotalAmount());
        response.setShippingAddress(order.getShippingAddress());
        response.setStatus(order.getStatus());
        response.setPaymentStatus(order.getPaymentStatus());
        response.setOrderDate(order.getOrderDate());
        return response;
    }
    
    public List<OrderResponse> toResponse(List<Order> orders) {
        return orders.stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    }
    
    private OrderResponse.OrderItemResponse toOrderItemResponse(OrderItem orderItem) {
        OrderResponse.OrderItemResponse response = new OrderResponse.OrderItemResponse();
        response.setId(orderItem.getId());
        response.setBookId(orderItem.getBook().getId());
        response.setBookTitle(orderItem.getBook().getTitle());
        response.setQuantity(orderItem.getQuantity());
        response.setPrice(orderItem.getPrice());
        response.setSubtotal(orderItem.getSubtotal());
        return response;
    }
} 