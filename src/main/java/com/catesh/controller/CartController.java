package com.catesh.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.catesh.dto.CartItemDTO;
import com.catesh.dto.CartResponseDTO;
import com.catesh.service.CartService;
import com.catesh.service.UserService;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<CartResponseDTO> getCart() {
        Long userId = userService.getCurrentUserId();
        return ResponseEntity.ok(cartService.getCart(userId));
    }

    @PostMapping("/items")
    public ResponseEntity<CartResponseDTO> addItem(@RequestBody CartItemDTO cartItem) {
        Long userId = userService.getCurrentUserId();
        return ResponseEntity.ok(cartService.addItem(userId, cartItem));
    }

    @PutMapping("/items/{itemId}")
    public ResponseEntity<CartResponseDTO> updateItem(
            @PathVariable Long itemId,
            @RequestBody CartItemDTO cartItem) {
        Long userId = userService.getCurrentUserId();
        return ResponseEntity.ok(cartService.updateItem(userId, itemId, cartItem));
    }

    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<CartResponseDTO> removeItem(@PathVariable Long itemId) {
        Long userId = userService.getCurrentUserId();
        return ResponseEntity.ok(cartService.removeItem(userId, itemId));
    }

    @DeleteMapping
    public ResponseEntity<CartResponseDTO> clearCart() {
        Long userId = userService.getCurrentUserId();
        return ResponseEntity.ok(cartService.clearCart(userId));
    }
} 