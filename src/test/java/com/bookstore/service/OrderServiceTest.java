package com.bookstore.service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
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

import com.bookstore.dto.order.OrderRequest;
import com.bookstore.entity.Role;
import com.bookstore.entity.User;
import com.bookstore.mapper.OrderMapper;
import com.bookstore.model.Book;
import com.bookstore.model.Order;
import com.bookstore.model.OrderItem;
import com.bookstore.repository.OrderRepository;
import com.bookstore.service.impl.OrderServiceImpl;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderMapper orderMapper;

    @Mock
    private BookService bookService;

    @InjectMocks
    private OrderServiceImpl orderService;

    private Order order;
    private OrderRequest orderRequest;
    private User user;
    private Book book;
    private OrderItem orderItem;
    private OrderRequest.OrderItemRequest orderItemRequest;
    private List<Order> orders;

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

        orderItem = new OrderItem();
        orderItem.setId(1L);
        orderItem.setBook(book);
        orderItem.setQuantity(2);
        orderItem.setPrice(book.getPrice());

        orderItemRequest = new OrderRequest.OrderItemRequest();
        orderItemRequest.setBookId(1L);
        orderItemRequest.setQuantity(2);

        order = new Order();
        order.setId(1L);
        order.setUser(user);
        order.setOrderItems(Arrays.asList(orderItem));
        order.setStatus("PENDING");
        order.setPaymentStatus("PENDING");
        order.setShippingAddress("123 Test St");

        orderRequest = new OrderRequest();
        orderRequest.setOrderItems(Arrays.asList(orderItemRequest));
        orderRequest.setShippingAddress("123 Test St");

        orders = Arrays.asList(order);
    }

    @Test
    void createOrder_ShouldReturnCreatedOrder() {
        when(bookService.findById(1L)).thenReturn(Optional.of(book));
        when(orderMapper.toEntity(orderRequest, user)).thenReturn(order);
        when(orderRepository.save(order)).thenReturn(order);

        Order result = orderService.createOrder(orderRequest, user);

        assertNotNull(result);
        assertEquals(order.getId(), result.getId());
        assertEquals(user.getId(), result.getUser().getId());
        verify(orderRepository).save(order);
    }

    @Test
    void findById_WhenOrderExists_ShouldReturnOrder() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        Optional<Order> result = orderService.findById(1L);

        assertTrue(result.isPresent());
        assertEquals(order.getId(), result.get().getId());
    }

    @Test
    void findById_WhenOrderDoesNotExist_ShouldReturnEmpty() {
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<Order> result = orderService.findById(1L);

        assertFalse(result.isPresent());
    }

    @Test
    void findByUser_ShouldReturnUserOrders() {
        when(orderRepository.findByUser(user)).thenReturn(orders);

        List<Order> result = orderService.findByUser(user);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(user.getId(), result.get(0).getUser().getId());
    }

    @Test
    void updateStatus_WhenOrderExists_ShouldUpdateStatus() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(order)).thenReturn(order);

        Order result = orderService.updateStatus(1L, "PROCESSING");

        assertNotNull(result);
        assertEquals("PROCESSING", result.getStatus());
        verify(orderRepository).save(order);
    }

    @Test
    void updateStatus_WhenOrderDoesNotExist_ShouldThrowException() {
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(IllegalStateException.class, () -> orderService.updateStatus(1L, "PROCESSING"));
        verify(orderRepository, never()).save(any());
    }

    @Test
    void updatePaymentStatus_WhenOrderExists_ShouldUpdatePaymentStatus() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(order)).thenReturn(order);

        Order result = orderService.updatePaymentStatus(1L, "PAID");

        assertNotNull(result);
        assertEquals("PAID", result.getPaymentStatus());
        verify(orderRepository).save(order);
    }

    @Test
    void processOrder_WhenOrderExists_ShouldUpdateStatusToProcessing() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(order)).thenReturn(order);

        orderService.processOrder(1L);

        assertEquals("PROCESSING", order.getStatus());
        verify(orderRepository).save(order);
    }

    @Test
    void shipOrder_WhenOrderExists_ShouldUpdateStatusToShipped() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(order)).thenReturn(order);

        orderService.shipOrder(1L);

        assertEquals("SHIPPED", order.getStatus());
        verify(orderRepository).save(order);
    }

    @Test
    void deliverOrder_WhenOrderExists_ShouldUpdateStatusToDelivered() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(order)).thenReturn(order);

        orderService.deliverOrder(1L);

        assertEquals("DELIVERED", order.getStatus());
        verify(orderRepository).save(order);
    }

    @Test
    void cancelOrder_WhenOrderExists_ShouldUpdateStatusToCancelled() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(order)).thenReturn(order);

        orderService.cancelOrder(1L);

        assertEquals("CANCELLED", order.getStatus());
        verify(orderRepository).save(order);
    }
} 