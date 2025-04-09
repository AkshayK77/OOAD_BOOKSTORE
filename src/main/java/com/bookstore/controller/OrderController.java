package com.bookstore.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bookstore.dto.order.OrderRequest;
import com.bookstore.dto.order.OrderResponse;
import com.bookstore.entity.User;
import com.bookstore.mapper.OrderMapper;
import com.bookstore.model.Order;
import com.bookstore.service.OrderService;
import com.bookstore.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Tag(name = "Order Management", description = "APIs for managing orders in the bookstore")
@SecurityRequirement(name = "bearerAuth")
public class OrderController {
    
    private final OrderService orderService;
    private final UserService userService;
    private final OrderMapper orderMapper;
    
    @Operation(summary = "Create a new order", description = "Creates a new order for the authenticated user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Successfully created order", 
                    content = @Content(mediaType = "application/json", 
                    schema = @Schema(implementation = OrderResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input", 
                    content = @Content),
        @ApiResponse(responseCode = "401", description = "Unauthorized", 
                    content = @Content),
        @ApiResponse(responseCode = "403", description = "Forbidden", 
                    content = @Content),
        @ApiResponse(responseCode = "422", description = "Insufficient stock for items", 
                    content = @Content)
    })
    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(
            @Parameter(description = "Order details", required = true)
            @Valid @RequestBody OrderRequest orderRequest,
            Authentication authentication) {
        try {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            User user = userService.findByEmail(userDetails.getUsername())
                    .orElseThrow(() -> new IllegalStateException("User not found"));
            Order order = orderService.createOrder(orderRequest, user);
            OrderResponse orderResponse = orderMapper.toResponse(order);
            return ResponseEntity.status(HttpStatus.CREATED).body(orderResponse);
        } catch (IllegalStateException e) {
            // Handle out of stock exception
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                .header("X-Error", e.getMessage())
                .build();
        }
    }
    
    @Operation(summary = "Get user's orders", description = "Retrieves all orders for the authenticated user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved orders",
            content = @Content(schema = @Schema(implementation = OrderResponse.class),
                examples = @ExampleObject(value = """
                    [
                      {
                        "id": 1,
                        "userId": 1,
                        "username": "john_doe",
                        "orderItems": [
                          {
                            "id": 1,
                            "bookId": 1,
                            "bookTitle": "The Great Gatsby",
                            "quantity": 2,
                            "price": 29.99,
                            "subtotal": 59.98
                          }
                        ],
                        "totalAmount": 59.98,
                        "shippingAddress": "123 Main St, City, Country",
                        "status": "PROCESSING",
                        "paymentStatus": "PAID",
                        "orderDate": "2024-03-20T10:30:00"
                      }
                    ]
                    """))),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/my-orders")
    public ResponseEntity<List<OrderResponse>> getMyOrders(Authentication authentication) {
        try {
            if (authentication == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            
            User user;
            try {
                // Try to get user from UserDetails
                if (authentication.getPrincipal() instanceof UserDetails) {
                    UserDetails userDetails = (UserDetails) authentication.getPrincipal();
                    user = userService.findByEmail(userDetails.getUsername())
                            .orElseThrow(() -> new IllegalStateException("User not found"));
                } else {
                    // Fallback to getting user by name
                    user = userService.findByEmail(authentication.getName())
                            .orElseThrow(() -> new IllegalStateException("User not found"));
                }
            } catch (Exception e) {
                e.printStackTrace();
                // Special case for admin users 
                if (authentication.getName().equals("admin@admin.com")) {
                    // Get admin user from DB or create a fake user for the response
                    user = userService.findByEmail("admin@admin.com")
                            .orElseThrow(() -> new IllegalStateException("Admin user not found"));
                } else {
                    throw e;
                }
            }
            
            List<Order> orders = orderService.findByUser(user);
            return ResponseEntity.ok(orderMapper.toResponse(orders));
        } catch (Exception e) {
            e.printStackTrace();
            // Return empty list on error
            return ResponseEntity.ok(List.of());
        }
    }
    
    @Operation(summary = "Get order by ID", description = "Retrieves a specific order by its ID (User can only access their own orders, Admin can access all)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved order",
            content = @Content(schema = @Schema(implementation = OrderResponse.class),
                examples = @ExampleObject(value = """
                    {
                      "id": 1,
                      "userId": 1,
                      "username": "john_doe",
                      "orderItems": [
                        {
                          "id": 1,
                          "bookId": 1,
                          "bookTitle": "The Great Gatsby",
                          "quantity": 2,
                          "price": 29.99,
                          "subtotal": 59.98
                        }
                      ],
                      "totalAmount": 59.98,
                      "shippingAddress": "123 Main St, City, Country",
                      "status": "PROCESSING",
                      "paymentStatus": "PAID",
                      "orderDate": "2024-03-20T10:30:00"
                    }
                    """))),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Not owner of the order"),
        @ApiResponse(responseCode = "404", description = "Order not found",
            content = @Content(schema = @Schema(ref = "ErrorResponse"),
                examples = @ExampleObject(value = """
                    {
                      "timestamp": "2024-03-20T10:30:00Z",
                      "status": 404,
                      "error": "Not Found",
                      "message": "Order with ID 1 not found",
                      "path": "/api/orders/1"
                    }
                    """))),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrderById(
            @Parameter(description = "ID of the order to retrieve", required = true)
            @PathVariable Long id, 
            Authentication authentication) {
        try {
            if (authentication == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            
            User user;
            boolean isAdmin = false;
            try {
                // Try to get user from UserDetails
                if (authentication.getPrincipal() instanceof UserDetails) {
                    UserDetails userDetails = (UserDetails) authentication.getPrincipal();
                    user = userService.findByEmail(userDetails.getUsername())
                            .orElseThrow(() -> new IllegalStateException("User not found"));
                } else {
                    // Fallback to getting user by name
                    user = userService.findByEmail(authentication.getName())
                            .orElseThrow(() -> new IllegalStateException("User not found"));
                }
                
                isAdmin = "ROLE_ADMIN".equals(user.getRole());
            } catch (Exception e) {
                e.printStackTrace();
                // Special case for admin users 
                if (authentication.getName().equals("admin@admin.com")) {
                    // Get admin user from DB
                    user = userService.findByEmail("admin@admin.com")
                            .orElseThrow(() -> new IllegalStateException("Admin user not found"));
                    isAdmin = true;
                } else {
                    throw e;
                }
            }
            
            // Store user and isAdmin in final variables for use in lambda
            final User finalUser = user;
            final boolean finalIsAdmin = isAdmin;
            
            return orderService.findById(id)
                    .filter(order -> order.getUser().getId().equals(finalUser.getId()) || finalIsAdmin)
                    .map(order -> ResponseEntity.ok(orderMapper.toResponse(order)))
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @Operation(summary = "Update order status", description = "Updates the status of an order (Admin only)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully updated order status",
            content = @Content(schema = @Schema(implementation = OrderResponse.class),
                examples = @ExampleObject(value = """
                    {
                      "id": 1,
                      "userId": 1,
                      "username": "john_doe",
                      "orderItems": [
                        {
                          "id": 1,
                          "bookId": 1,
                          "bookTitle": "The Great Gatsby",
                          "quantity": 2,
                          "price": 29.99,
                          "subtotal": 59.98
                        }
                      ],
                      "totalAmount": 59.98,
                      "shippingAddress": "123 Main St, City, Country",
                      "status": "PROCESSING",
                      "paymentStatus": "PAID",
                      "orderDate": "2024-03-20T10:30:00"
                    }
                    """))),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Admin access required"),
        @ApiResponse(responseCode = "404", description = "Order not found",
            content = @Content(schema = @Schema(ref = "ErrorResponse"),
                examples = @ExampleObject(value = """
                    {
                      "timestamp": "2024-03-20T10:30:00Z",
                      "status": 404,
                      "error": "Not Found",
                      "message": "Order with ID 1 not found",
                      "path": "/api/orders/admin/1/status"
                    }
                    """))),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping("/admin/{id}/status")
    public ResponseEntity<OrderResponse> updateOrderStatus(
            @Parameter(description = "ID of the order to update", required = true)
            @PathVariable Long id, 
            @Parameter(description = "New order status", required = true)
            @RequestParam String status) {
        Order order = orderService.updateStatus(id, status);
        return ResponseEntity.ok(orderMapper.toResponse(order));
    }
    
    @Operation(summary = "Update payment status", description = "Updates the payment status of an order and updates inventory when marked as COMPLETED (Admin only)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully updated payment status", 
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = OrderResponse.class), 
                examples = @ExampleObject(value = "{\"id\": 1, \"userId\": 2, \"orderItems\": [{\"bookId\": 1, \"quantity\": 2, \"price\": 29.99}], \"totalAmount\": 59.98, \"shippingAddress\": \"123 Main St, City, Country\", \"status\": \"PROCESSING\", \"paymentStatus\": \"PAID\"}"))),
        @ApiResponse(responseCode = "404", description = "Order not found"),
        @ApiResponse(responseCode = "422", description = "Insufficient stock for items"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping("/admin/{id}/payment")
    public ResponseEntity<OrderResponse> updatePaymentStatus(
            @Parameter(description = "ID of the order to update", required = true)
            @PathVariable Long id, 
            @Parameter(description = "New payment status", required = true)
            @RequestParam String paymentStatus) {
        try {
            // Update the payment status, which may trigger the stock update if marked as COMPLETED
            Order order = orderService.updatePaymentStatus(id, paymentStatus);
            return ResponseEntity.ok(orderMapper.toResponse(order));
        } catch (IllegalStateException e) {
            // Handle out of stock exception
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                .header("X-Error", e.getMessage())
                .build();
        }
    }
    
    @Operation(summary = "Process order", description = "Marks an order as processed (Admin only)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully processed order", 
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = OrderResponse.class), 
                examples = @ExampleObject(value = "{\"id\": 1, \"userId\": 2, \"orderItems\": [{\"bookId\": 1, \"quantity\": 2, \"price\": 29.99}], \"totalAmount\": 59.98, \"shippingAddress\": \"123 Main St, City, Country\", \"status\": \"PROCESSED\", \"paymentStatus\": \"PAID\"}"))),
        @ApiResponse(responseCode = "404", description = "Order not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/admin/{id}/process")
    public ResponseEntity<OrderResponse> processOrder(
            @Parameter(description = "ID of the order to process", required = true)
            @PathVariable Long id) {
        orderService.processOrder(id);
        Order order = orderService.findById(id)
                .orElseThrow(() -> new IllegalStateException("Order not found"));
        return ResponseEntity.ok(orderMapper.toResponse(order));
    }
    
    @Operation(summary = "Ship order", description = "Marks an order as shipped (Admin only)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully shipped order"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Admin access required"),
        @ApiResponse(responseCode = "404", description = "Order not found",
            content = @Content(schema = @Schema(ref = "ErrorResponse"),
                examples = @ExampleObject(value = """
                    {
                      "timestamp": "2024-03-20T10:30:00Z",
                      "status": 404,
                      "error": "Not Found",
                      "message": "Order with ID 1 not found",
                      "path": "/api/orders/admin/1/ship"
                    }
                    """))),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/admin/{id}/ship")
    public ResponseEntity<OrderResponse> shipOrder(
            @Parameter(description = "ID of the order to ship", required = true)
            @PathVariable Long id) {
        orderService.shipOrder(id);
        Order order = orderService.findById(id)
                .orElseThrow(() -> new IllegalStateException("Order not found"));
        return ResponseEntity.ok(orderMapper.toResponse(order));
    }
    
    @Operation(summary = "Deliver order", description = "Marks an order as delivered (Admin only)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully delivered order"),
        @ApiResponse(responseCode = "404", description = "Order not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/admin/{id}/deliver")
    public ResponseEntity<OrderResponse> deliverOrder(
            @Parameter(description = "ID of the order to deliver", required = true)
            @PathVariable Long id) {
        orderService.deliverOrder(id);
        Order order = orderService.findById(id)
                .orElseThrow(() -> new IllegalStateException("Order not found"));
        return ResponseEntity.ok(orderMapper.toResponse(order));
    }
    
    @Operation(summary = "Complete order", description = "Marks an order as completed (Admin only)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully completed order"),
        @ApiResponse(responseCode = "404", description = "Order not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/admin/{id}/complete")
    public ResponseEntity<OrderResponse> completeOrder(
            @Parameter(description = "ID of the order to complete", required = true)
            @PathVariable Long id) {
        // Update the order status to COMPLETED
        Order order = orderService.updateStatus(id, "COMPLETED");
        return ResponseEntity.ok(orderMapper.toResponse(order));
    }
    
    @Operation(summary = "Cancel order", description = "Cancels an order (User can only cancel their own orders, Admin can cancel any)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully cancelled order"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Not owner of the order"),
        @ApiResponse(responseCode = "404", description = "Order not found",
            content = @Content(schema = @Schema(ref = "ErrorResponse"),
                examples = @ExampleObject(value = """
                    {
                      "timestamp": "2024-03-20T10:30:00Z",
                      "status": 404,
                      "error": "Not Found",
                      "message": "Order with ID 1 not found",
                      "path": "/api/orders/1/cancel"
                    }
                    """))),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/{id}/cancel")
    public ResponseEntity<Void> cancelOrder(
            @Parameter(description = "ID of the order to cancel", required = true)
            @PathVariable Long id, 
            Authentication authentication) {
        User user = userService.findByEmail(authentication.getName())
                .orElseThrow(() -> new IllegalStateException("User not found"));
        Order order = orderService.findById(id)
                .orElseThrow(() -> new IllegalStateException("Order not found"));
        
        if (!order.getUser().getId().equals(user.getId()) && !user.getRole().equals("ROLE_ADMIN")) {
            return ResponseEntity.status(403).build();
        }
        
        orderService.cancelOrder(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/admin/all")
    public ResponseEntity<List<OrderResponse>> getAllOrders() {
        try {
            List<Order> orders = orderService.findAll();
            return ResponseEntity.ok(orderMapper.toResponse(orders));
        } catch (Exception e) {
            // Log the error
            e.printStackTrace();
            // Return empty list instead of error to prevent frontend from crashing
            return ResponseEntity.ok(List.of());
        }
    }
} 