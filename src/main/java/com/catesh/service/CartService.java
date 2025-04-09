package com.catesh.service;

import com.catesh.dto.CartItemDTO;
import com.catesh.dto.CartResponseDTO;

public interface CartService {
    CartResponseDTO getCart(Long userId);
    CartResponseDTO addItem(Long userId, CartItemDTO cartItem);
    CartResponseDTO updateItem(Long userId, Long itemId, CartItemDTO cartItem);
    CartResponseDTO removeItem(Long userId, Long itemId);
    CartResponseDTO clearCart(Long userId);
} 