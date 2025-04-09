package com.bookstore.service;

import java.util.List;

import com.bookstore.dto.cart.CartItemRequest;
import com.bookstore.dto.cart.CartResponse;
import com.bookstore.entity.User;
import com.bookstore.model.Cart;
import com.bookstore.model.CartItem;

public interface CartService {
    CartResponse getCart(User user);
    CartResponse addItem(User user, CartItemRequest request);
    CartResponse updateItem(User user, Long itemId, CartItemRequest request);
    CartResponse removeItem(User user, Long itemId);
    void clearCart(User user);
    CartResponse getCartById(Long cartId);
} 