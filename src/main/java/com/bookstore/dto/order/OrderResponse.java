package com.bookstore.dto.order;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Response object containing order details")
public class OrderResponse {
    
    @Schema(description = "Unique identifier of the order", example = "1")
    private Long id;
    
    @Schema(description = "ID of the user who placed the order", example = "1")
    private Long userId;
    
    @Schema(description = "Username of the user who placed the order", example = "john_doe")
    private String username;
    
    @Schema(description = "List of items in the order")
    private List<OrderItemResponse> orderItems;
    
    @Schema(description = "Total amount of the order", example = "59.98")
    private BigDecimal totalAmount;
    
    @Schema(description = "Shipping address for the order", example = "123 Main St, City, Country")
    private String shippingAddress;
    
    @Schema(description = "Current status of the order", example = "PROCESSING")
    private String status;
    
    @Schema(description = "Payment status of the order", example = "PAID")
    private String paymentStatus;
    
    @Schema(description = "Date and time when the order was placed", example = "2024-03-20T10:30:00")
    private LocalDateTime orderDate;
    
    @Data
    @Schema(description = "Response object for an order item")
    public static class OrderItemResponse {
        
        @Schema(description = "Unique identifier of the order item", example = "1")
        private Long id;
        
        @Schema(description = "ID of the book", example = "1")
        private Long bookId;
        
        @Schema(description = "Title of the book", example = "The Great Gatsby")
        private String bookTitle;
        
        @Schema(description = "Quantity ordered", example = "2")
        private Integer quantity;
        
        @Schema(description = "Price per unit", example = "29.99")
        private BigDecimal price;
        
        @Schema(description = "Subtotal for this item", example = "59.98")
        private BigDecimal subtotal;
    }
} 