package com.bookstore.service.impl;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bookstore.dto.cart.CartItemRequest;
import com.bookstore.dto.cart.CartResponse;
import com.bookstore.entity.User;
import com.bookstore.exception.ResourceNotFoundException;
import com.bookstore.mapper.CartMapper;
import com.bookstore.model.Book;
import com.bookstore.model.Cart;
import com.bookstore.model.CartItem;
import com.bookstore.repository.CartItemRepository;
import com.bookstore.repository.CartRepository;
import com.bookstore.service.BookService;
import com.bookstore.service.CartService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {
    
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final BookService bookService;
    private final CartMapper cartMapper;
    
    @Override
    public CartResponse getCart(User user) {
        Cart cart = cartRepository.findByUser(user)
            .orElseGet(() -> {
                Cart newCart = new Cart();
                newCart.setUser(user);
                return cartRepository.save(newCart);
            });
        return cartMapper.toResponse(cart);
    }
    
    @Override
    @Transactional
    public CartResponse addItem(User user, CartItemRequest request) {
        Cart cart = cartRepository.findByUser(user)
            .orElseGet(() -> {
                Cart newCart = new Cart();
                newCart.setUser(user);
                return cartRepository.save(newCart);
            });
        
        Book book = bookService.findById(request.getBookId())
            .orElseThrow(() -> new ResourceNotFoundException("Book not found"));
            
        Optional<CartItem> existingItem = cartItemRepository.findByCartAndBook(cart, book);
        
        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + request.getQuantity());
            cartItemRepository.save(item);
        } else {
            CartItem newItem = new CartItem();
            newItem.setCart(cart);
            newItem.setBook(book);
            newItem.setQuantity(request.getQuantity());
            newItem.setPrice(book.getPrice());
            cartItemRepository.save(newItem);
        }
        
        return cartMapper.toResponse(cart);
    }
    
    @Override
    @Transactional
    public CartResponse updateItem(User user, Long itemId, CartItemRequest request) {
        Cart cart = cartRepository.findByUser(user)
            .orElseThrow(() -> new ResourceNotFoundException("Cart not found"));
            
        CartItem item = cartItemRepository.findById(itemId)
            .orElseThrow(() -> new ResourceNotFoundException("Cart item not found"));
            
        if (!item.getCart().getId().equals(cart.getId())) {
            throw new ResourceNotFoundException("Cart item not found in user's cart");
        }
        
        item.setQuantity(request.getQuantity());
        cartItemRepository.save(item);
        
        return cartMapper.toResponse(cart);
    }
    
    @Override
    @Transactional
    public CartResponse removeItem(User user, Long itemId) {
        Cart cart = cartRepository.findByUser(user)
            .orElseThrow(() -> new ResourceNotFoundException("Cart not found"));
            
        CartItem item = cartItemRepository.findById(itemId)
            .orElseThrow(() -> new ResourceNotFoundException("Cart item not found"));
            
        if (!item.getCart().getId().equals(cart.getId())) {
            throw new ResourceNotFoundException("Cart item not found in user's cart");
        }
        
        cartItemRepository.delete(item);
        
        return cartMapper.toResponse(cart);
    }
    
    @Override
    @Transactional
    public void clearCart(User user) {
        Cart cart = cartRepository.findByUser(user)
            .orElseThrow(() -> new ResourceNotFoundException("Cart not found"));
            
        cart.getCartItems().clear();
        cartRepository.save(cart);
    }
    
    @Override
    public CartResponse getCartById(Long cartId) {
        Cart cart = cartRepository.findById(cartId)
            .orElseThrow(() -> new ResourceNotFoundException("Cart not found"));
        return cartMapper.toResponse(cart);
    }
} 