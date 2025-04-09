package com.catesh.controller;

import java.math.BigDecimal;
import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.catesh.dto.CartItemDTO;
import com.catesh.dto.CartResponseDTO;
import com.catesh.service.CartService;
import com.catesh.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(CartController.class)
@ContextConfiguration(classes = com.bookstore.BookstoreApplication.class)
class CartControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CartService cartService;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    private CartResponseDTO mockCartResponse;
    private CartItemDTO mockCartItem;

    @BeforeEach
    void setUp() {
        mockCartItem = new CartItemDTO();
        mockCartItem.setId(1L);
        mockCartItem.setProductId(1L);
        mockCartItem.setQuantity(1);
        mockCartItem.setPrice(BigDecimal.valueOf(10.00));

        mockCartResponse = new CartResponseDTO();
        mockCartResponse.setId(1L);
        mockCartResponse.setUserId(1L);
        mockCartResponse.setItems(new ArrayList<>());
        mockCartResponse.getItems().add(mockCartItem);
        mockCartResponse.setTotalAmount(BigDecimal.valueOf(10.00));
    }

    @Test
    @WithMockUser
    void getCart_ShouldReturnCart() throws Exception {
        when(cartService.getCart(anyLong())).thenReturn(mockCartResponse);

        mockMvc.perform(get("/api/cart"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.items[0].id").value(1))
                .andExpect(jsonPath("$.totalAmount").value(10.00));
    }

    @Test
    @WithMockUser
    void addItem_ShouldReturnUpdatedCart() throws Exception {
        when(cartService.addItem(anyLong(), any(CartItemDTO.class))).thenReturn(mockCartResponse);

        mockMvc.perform(post("/api/cart/items")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(mockCartItem)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.items[0].id").value(1))
                .andExpect(jsonPath("$.totalAmount").value(10.00));
    }

    @Test
    @WithMockUser
    void updateItem_ShouldReturnUpdatedCart() throws Exception {
        when(cartService.updateItem(anyLong(), anyLong(), any(CartItemDTO.class))).thenReturn(mockCartResponse);

        mockMvc.perform(put("/api/cart/items/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(mockCartItem)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.items[0].id").value(1))
                .andExpect(jsonPath("$.totalAmount").value(10.00));
    }

    @Test
    @WithMockUser
    void removeItem_ShouldReturnUpdatedCart() throws Exception {
        when(cartService.removeItem(anyLong(), anyLong())).thenReturn(mockCartResponse);

        mockMvc.perform(delete("/api/cart/items/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.items[0].id").value(1))
                .andExpect(jsonPath("$.totalAmount").value(10.00));
    }

    @Test
    @WithMockUser
    void clearCart_ShouldReturnEmptyCart() throws Exception {
        CartResponseDTO emptyCart = new CartResponseDTO();
        emptyCart.setId(1L);
        emptyCart.setUserId(1L);
        emptyCart.setItems(new ArrayList<>());
        emptyCart.setTotalAmount(BigDecimal.ZERO);

        when(cartService.clearCart(anyLong())).thenReturn(emptyCart);

        mockMvc.perform(delete("/api/cart"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.items").isEmpty())
                .andExpect(jsonPath("$.totalAmount").value(0));
    }
} 