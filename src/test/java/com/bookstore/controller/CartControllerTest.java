package com.bookstore.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.bookstore.dto.cart.CartItemRequest;
import com.bookstore.dto.cart.CartResponse;
import com.bookstore.entity.Role;
import com.bookstore.entity.User;
import com.bookstore.service.CartService;
import com.bookstore.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(CartController.class)
class CartControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CartService cartService;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    private User user;
    private CartResponse cartResponse;
    private CartItemRequest cartItemRequest;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setEmail("testuser@example.com");
        user.setFirstName("Test");
        user.setLastName("User");
        user.setPassword("password");
        user.setRole(Role.USER);

        cartResponse = new CartResponse();
        cartResponse.setId(1L);
        cartResponse.setUserId(1L);

        cartItemRequest = new CartItemRequest();
        cartItemRequest.setBookId(1L);
        cartItemRequest.setQuantity(2);
    }

    @Test
    @WithMockUser
    void getCart_ShouldReturnCart() throws Exception {
        when(userService.getCurrentUser()).thenReturn(user);
        when(cartService.getCart(user)).thenReturn(cartResponse);

        mockMvc.perform(get("/api/cart"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.userId").value(1));
    }

    @Test
    @WithMockUser
    void addItem_ShouldAddItemToCart() throws Exception {
        when(userService.getCurrentUser()).thenReturn(user);
        when(cartService.addItem(user, cartItemRequest)).thenReturn(cartResponse);

        mockMvc.perform(post("/api/cart/items")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cartItemRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.userId").value(1));
    }

    @Test
    @WithMockUser
    void updateItem_ShouldUpdateItemInCart() throws Exception {
        when(userService.getCurrentUser()).thenReturn(user);
        when(cartService.updateItem(user, 1L, cartItemRequest)).thenReturn(cartResponse);

        mockMvc.perform(put("/api/cart/items/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cartItemRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.userId").value(1));
    }

    @Test
    @WithMockUser
    void removeItem_ShouldRemoveItemFromCart() throws Exception {
        when(userService.getCurrentUser()).thenReturn(user);
        when(cartService.removeItem(user, 1L)).thenReturn(cartResponse);

        mockMvc.perform(delete("/api/cart/items/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.userId").value(1));
    }

    @Test
    @WithMockUser
    void clearCart_ShouldClearCart() throws Exception {
        when(userService.getCurrentUser()).thenReturn(user);
        doReturn(cartResponse).when(cartService).clearCart(user);

        mockMvc.perform(delete("/api/cart"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.userId").value(1));
    }
} 