package com.bookstore.mapper;

import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.bookstore.dto.cart.CartResponse;
import com.bookstore.model.Cart;
import com.bookstore.model.CartItem;

@Component
public class CartMapper {
    
    public CartResponse toResponse(Cart cart) {
        CartResponse response = new CartResponse();
        response.setId(cart.getId());
        response.setUserId(cart.getUser().getId());
        response.setCartItems(cart.getCartItems().stream()
            .map(this::toCartItemResponse)
            .collect(Collectors.toList()));
        response.setTotalAmount(cart.getTotalAmount());
        return response;
    }
    
    private CartResponse.CartItemResponse toCartItemResponse(CartItem cartItem) {
        CartResponse.CartItemResponse response = new CartResponse.CartItemResponse();
        response.setId(cartItem.getId());
        response.setBookId(cartItem.getBook().getId());
        response.setBookTitle(cartItem.getBook().getTitle());
        response.setQuantity(cartItem.getQuantity());
        response.setPrice(cartItem.getPrice());
        response.setSubtotal(cartItem.getSubtotal());
        return response;
    }
} 