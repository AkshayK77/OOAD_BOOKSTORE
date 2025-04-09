package com.bookstore.dto.cart;

import java.math.BigDecimal;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Response object containing cart details")
public class CartResponse {
    
    @Schema(description = "Unique identifier of the cart", example = "1")
    private Long id;
    
    @Schema(description = "ID of the user who owns the cart", example = "1")
    private Long userId;
    
    @Schema(description = "List of items in the cart")
    private List<CartItemResponse> cartItems;
    
    @Schema(description = "Total amount of the cart", example = "59.98")
    private BigDecimal totalAmount;
    
    @Data
    @Schema(description = "Response object for a cart item")
    public static class CartItemResponse {
        
        @Schema(description = "Unique identifier of the cart item", example = "1")
        private Long id;
        
        @Schema(description = "ID of the book", example = "1")
        private Long bookId;
        
        @Schema(description = "Title of the book", example = "The Great Gatsby")
        private String bookTitle;
        
        @Schema(description = "Quantity in cart", example = "2")
        private Integer quantity;
        
        @Schema(description = "Price per unit", example = "29.99")
        private BigDecimal price;
        
        @Schema(description = "Subtotal for this item", example = "59.98")
        private BigDecimal subtotal;
    }
} 