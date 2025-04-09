package com.bookstore.dto.order;

import java.math.BigDecimal;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "Request object for creating a new order")
public class OrderRequest {
    
    @Schema(description = "List of items in the order")
    @NotEmpty(message = "Order must contain at least one item")
    @Valid
    private List<OrderItemRequest> orderItems;
    
    @Schema(description = "Shipping address for the order", example = "123 Main St, City, Country")
    @NotBlank(message = "Shipping address is required")
    private String shippingAddress;
    
    @Schema(description = "Total amount of the order", example = "59.98")
    private BigDecimal totalAmount;
    
    @Data
    @Schema(description = "Request object for an order item")
    public static class OrderItemRequest {
        
        @Schema(description = "ID of the book to order", example = "1")
        @NotNull(message = "Book ID is required")
        private Long bookId;
        
        @Schema(description = "Quantity of the book to order", example = "2")
        @NotNull(message = "Quantity is required")
        private Integer quantity;
    }
} 