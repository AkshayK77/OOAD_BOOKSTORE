package com.bookstore.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.bookstore.dto.cart.CartItemRequest;
import com.bookstore.dto.cart.CartResponse;
import com.bookstore.entity.Role;
import com.bookstore.entity.User;
import com.bookstore.exception.ResourceNotFoundException;
import com.bookstore.mapper.CartMapper;
import com.bookstore.model.Book;
import com.bookstore.model.Cart;
import com.bookstore.model.CartItem;
import com.bookstore.repository.CartItemRepository;
import com.bookstore.repository.CartRepository;
import com.bookstore.service.impl.CartServiceImpl;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private BookService bookService;

    @Mock
    private CartMapper cartMapper;

    @InjectMocks
    private CartServiceImpl cartService;

    private Cart cart;
    private CartItem cartItem;
    private User user;
    private Book book;
    private CartItemRequest cartItemRequest;
    private CartResponse cartResponse;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setEmail("testuser@example.com");
        user.setFirstName("Test");
        user.setLastName("User");
        user.setPassword("password");
        user.setRole(Role.USER);

        book = new Book();
        book.setId(1L);
        book.setTitle("Test Book");
        book.setPrice(BigDecimal.valueOf(29.99));

        cart = new Cart();
        cart.setId(1L);
        cart.setUser(user);
        cart.setCartItems(new ArrayList<>());

        cartItem = new CartItem();
        cartItem.setId(1L);
        cartItem.setCart(cart);
        cartItem.setBook(book);
        cartItem.setQuantity(2);
        cartItem.setPrice(book.getPrice());

        cart.getCartItems().add(cartItem);

        cartItemRequest = new CartItemRequest();
        cartItemRequest.setBookId(1L);
        cartItemRequest.setQuantity(2);

        cartResponse = new CartResponse();
        cartResponse.setId(1L);
        cartResponse.setUserId(1L);
    }

    @Test
    void getCart_WhenCartExists_ShouldReturnCart() {
        when(cartRepository.findByUser(user)).thenReturn(Optional.of(cart));
        when(cartMapper.toResponse(cart)).thenReturn(cartResponse);

        CartResponse result = cartService.getCart(user);

        assertNotNull(result);
        assertEquals(cart.getId(), result.getId());
        assertEquals(user.getId(), result.getUserId());
    }

    @Test
    void getCart_WhenCartDoesNotExist_ShouldCreateNewCart() {
        when(cartRepository.findByUser(user)).thenReturn(Optional.empty());
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);
        when(cartMapper.toResponse(cart)).thenReturn(cartResponse);

        CartResponse result = cartService.getCart(user);

        assertNotNull(result);
        assertEquals(cart.getId(), result.getId());
        assertEquals(user.getId(), result.getUserId());
        verify(cartRepository).save(any(Cart.class));
    }

    @Test
    void addItem_WhenBookExists_ShouldAddItemToCart() {
        when(cartRepository.findByUser(user)).thenReturn(Optional.of(cart));
        when(bookService.findById(1L)).thenReturn(Optional.of(book));
        when(cartItemRepository.findByCartAndBook(cart, book)).thenReturn(Optional.empty());
        when(cartItemRepository.save(any(CartItem.class))).thenReturn(cartItem);
        when(cartMapper.toResponse(cart)).thenReturn(cartResponse);

        CartResponse result = cartService.addItem(user, cartItemRequest);

        assertNotNull(result);
        assertEquals(cart.getId(), result.getId());
        verify(cartItemRepository).save(any(CartItem.class));
    }

    @Test
    void addItem_WhenItemExists_ShouldUpdateQuantity() {
        when(cartRepository.findByUser(user)).thenReturn(Optional.of(cart));
        when(bookService.findById(1L)).thenReturn(Optional.of(book));
        when(cartItemRepository.findByCartAndBook(cart, book)).thenReturn(Optional.of(cartItem));
        when(cartItemRepository.save(cartItem)).thenReturn(cartItem);
        when(cartMapper.toResponse(cart)).thenReturn(cartResponse);

        CartResponse result = cartService.addItem(user, cartItemRequest);

        assertNotNull(result);
        assertEquals(cart.getId(), result.getId());
        assertEquals(4, cartItem.getQuantity()); // 2 + 2
        verify(cartItemRepository).save(cartItem);
    }

    @Test
    void addItem_WhenBookDoesNotExist_ShouldThrowException() {
        when(cartRepository.findByUser(user)).thenReturn(Optional.of(cart));
        when(bookService.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> cartService.addItem(user, cartItemRequest));
        verify(cartItemRepository, never()).save(any());
    }

    @Test
    void updateItem_WhenItemExists_ShouldUpdateQuantity() {
        when(cartRepository.findByUser(user)).thenReturn(Optional.of(cart));
        when(cartItemRepository.findById(1L)).thenReturn(Optional.of(cartItem));
        when(cartItemRepository.save(cartItem)).thenReturn(cartItem);
        when(cartMapper.toResponse(cart)).thenReturn(cartResponse);

        CartResponse result = cartService.updateItem(user, 1L, cartItemRequest);

        assertNotNull(result);
        assertEquals(cart.getId(), result.getId());
        assertEquals(2, cartItem.getQuantity());
        verify(cartItemRepository).save(cartItem);
    }

    @Test
    void updateItem_WhenItemDoesNotExist_ShouldThrowException() {
        when(cartRepository.findByUser(user)).thenReturn(Optional.of(cart));
        when(cartItemRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> cartService.updateItem(user, 1L, cartItemRequest));
        verify(cartItemRepository, never()).save(any());
    }

    @Test
    void removeItem_WhenItemExists_ShouldRemoveItem() {
        when(cartRepository.findByUser(user)).thenReturn(Optional.of(cart));
        when(cartItemRepository.findById(1L)).thenReturn(Optional.of(cartItem));
        when(cartMapper.toResponse(cart)).thenReturn(cartResponse);

        CartResponse result = cartService.removeItem(user, 1L);

        assertNotNull(result);
        assertEquals(cart.getId(), result.getId());
        verify(cartItemRepository).delete(cartItem);
    }

    @Test
    void removeItem_WhenItemDoesNotExist_ShouldThrowException() {
        when(cartRepository.findByUser(user)).thenReturn(Optional.of(cart));
        when(cartItemRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> cartService.removeItem(user, 1L));
        verify(cartItemRepository, never()).delete(any());
    }

    @Test
    void clearCart_WhenCartExists_ShouldClearItems() {
        when(cartRepository.findByUser(user)).thenReturn(Optional.of(cart));

        cartService.clearCart(user);

        assertTrue(cart.getCartItems().isEmpty());
        verify(cartRepository).save(cart);
    }

    @Test
    void clearCart_WhenCartDoesNotExist_ShouldThrowException() {
        when(cartRepository.findByUser(user)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> cartService.clearCart(user));
        verify(cartRepository, never()).save(any());
    }

    @Test
    void getCartById_WhenCartExists_ShouldReturnCart() {
        when(cartRepository.findById(1L)).thenReturn(Optional.of(cart));
        when(cartMapper.toResponse(cart)).thenReturn(cartResponse);

        CartResponse result = cartService.getCartById(1L);

        assertNotNull(result);
        assertEquals(cart.getId(), result.getId());
        assertEquals(user.getId(), result.getUserId());
    }

    @Test
    void getCartById_WhenCartDoesNotExist_ShouldThrowException() {
        when(cartRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> cartService.getCartById(1L));
        verify(cartMapper, never()).toResponse(any());
    }
} 