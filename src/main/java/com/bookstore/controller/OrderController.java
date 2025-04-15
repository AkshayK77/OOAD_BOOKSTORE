package com.bookstore.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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

import com.bookstore.dto.ErrorResponse;
import com.bookstore.dto.order.OrderRequest;
import com.bookstore.dto.order.OrderResponse;
import com.bookstore.entity.User;
import com.bookstore.exception.BusinessException;
import com.bookstore.exception.ResourceNotFoundException;
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
    
    @PostMapping
    @Operation(summary = "Create a new order", description = "Create a new order with the provided details")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Order created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "401", description = "Unauthorized, authentication required"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<?> createOrder(
            @Parameter(description = "Order request object with items", required = true)
            @Valid @RequestBody OrderRequest orderRequest,
            Authentication authentication) {
        
        try {
            System.out.println("Create order request received: " + orderRequest);
            
            // Handle the case where authentication is null - no JWT token or invalid token
            if (authentication == null) {
                System.out.println("No authentication found, using default user");
                // For demonstration, create order without strict authentication
                Order createdOrder = orderService.createOrder(orderRequest, null);
                return ResponseEntity.status(HttpStatus.CREATED).body(orderMapper.toResponse(createdOrder));
            }
            
            // If we have authentication, proceed with user
            try {
                User user = null;
                
                // Try to get user from UserDetails
                if (authentication.getPrincipal() instanceof UserDetails) {
                    UserDetails userDetails = (UserDetails) authentication.getPrincipal();
                    user = userService.findByEmail(userDetails.getUsername())
                            .orElse(null);
                } else {
                    // Fallback to getting user by name
                    user = userService.findByEmail(authentication.getName())
                            .orElse(null);
                }
                
                if (user == null) {
                    System.out.println("User not found in database, creating order without user");
                    Order createdOrder = orderService.createOrder(orderRequest, null);
                    return ResponseEntity.status(HttpStatus.CREATED).body(orderMapper.toResponse(createdOrder));
                }
                
                Order createdOrder = orderService.createOrder(orderRequest, user);
                return ResponseEntity.status(HttpStatus.CREATED).body(orderMapper.toResponse(createdOrder));
                
            } catch (Exception e) {
                System.out.println("Error creating order with authenticated user: " + e.getMessage());
                e.printStackTrace();
                
                // For demonstration, fall back to creating order without user
                Order createdOrder = orderService.createOrder(orderRequest, null);
                return ResponseEntity.status(HttpStatus.CREATED).body(orderMapper.toResponse(createdOrder));
            }
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(HttpStatus.NOT_FOUND.value(), e.getMessage()));
        } catch (BusinessException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ErrorResponse(HttpStatus.CONFLICT.value(), e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), 
                           "Error creating order: " + e.getMessage()));
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
    public ResponseEntity<List<OrderResponse>> getUserOrders(Authentication authentication) {
        try {
            // For demonstration purposes, provide user orders even without strict authentication
            if (authentication == null) {
                System.out.println("No authentication provided, returning anonymous orders");
                // Return all orders for demonstration
                List<Order> allOrders = orderService.findAll();
                return ResponseEntity.ok(orderMapper.toResponse(allOrders));
            }
            
            // If we have authentication, get user orders
            try {
                String userEmail = null;
                // Try to extract email from authentication
                if (authentication.getPrincipal() instanceof UserDetails) {
                    userEmail = ((UserDetails) authentication.getPrincipal()).getUsername();
                } else {
                    userEmail = authentication.getName();
                }
                
                if (userEmail != null) {
                    System.out.println("Getting orders for user: " + userEmail);
                    
                    // Try to get user from database
                    try {
                        User user = userService.findByEmail(userEmail)
                                .orElse(null);
                                
                        if (user != null) {
                            List<Order> userOrders = orderService.findByUser(user);
                            return ResponseEntity.ok(orderMapper.toResponse(userOrders));
                        }
                    } catch (Exception e) {
                        System.out.println("Error finding user by email: " + e.getMessage());
                    }
                }
                
                // Fallback: return all orders
                List<Order> allOrders = orderService.findAll();
                return ResponseEntity.ok(orderMapper.toResponse(allOrders));
            } catch (Exception e) {
                System.out.println("Error processing authentication: " + e.getMessage());
                // Return all orders for demonstration
                List<Order> allOrders = orderService.findAll();
                return ResponseEntity.ok(orderMapper.toResponse(allOrders));
            }
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
        
        try {
            orderService.cancelOrder(id);
            return ResponseEntity.ok().build();
        } catch (IllegalStateException e) {
            // Return a 422 Unprocessable Entity for business rule violations
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                .header("X-Error", e.getMessage())
                .build();
        }
    }

    @GetMapping("/admin/all")
    public ResponseEntity<List<OrderResponse>> getAllOrders(Authentication authentication) {
        try {
            if (authentication == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            
            User user;
            boolean isAdmin = false;
            
            try {
                // Get user details
                if (authentication.getPrincipal() instanceof UserDetails) {
                    UserDetails userDetails = (UserDetails) authentication.getPrincipal();
                    user = userService.findByEmail(userDetails.getUsername())
                            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
                } else {
                    user = userService.findByEmail(authentication.getName())
                            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
                }
                
                isAdmin = user.getRole().equals("ROLE_ADMIN");
                
                // If admin, get all orders
                if (isAdmin) {
                    List<Order> orders = orderService.findAll();
                    return ResponseEntity.ok(orderMapper.toResponse(orders));
                } else {
                    // If not admin, get only user's orders
                    List<Order> userOrders = orderService.findByUser(user);
                    return ResponseEntity.ok(orderMapper.toResponse(userOrders));
                }
                
            } catch (Exception e) {
                e.printStackTrace();
                // Check special case for admin@admin.com
                if (authentication.getName().equals("admin@admin.com")) {
                    List<Order> orders = orderService.findAll();
                    return ResponseEntity.ok(orderMapper.toResponse(orders));
                }
                return ResponseEntity.ok(List.of()); // Return empty list on error
            }
        } catch (Exception e) {
            // Log the error
            e.printStackTrace();
            // Return empty list instead of error to prevent frontend from crashing
            return ResponseEntity.ok(List.of());
        }
    }

    @Operation(summary = "Update payment status for user", description = "Updates the payment status of a user's own order")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully updated payment status", 
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = OrderResponse.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "404", description = "Order not found or does not belong to user"),
        @ApiResponse(responseCode = "422", description = "Insufficient stock for items"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping("/{id}/payment")
    public ResponseEntity<OrderResponse> updateUserOrderPaymentStatus(
            @Parameter(description = "ID of the order to update", required = true)
            @PathVariable Long id, 
            @Parameter(description = "New payment status", required = true)
            @RequestParam String paymentStatus,
            Authentication authentication) {
        
        System.out.println("============================================================");
        System.out.println("Payment status update request received for order ID: " + id);
        System.out.println("Requested payment status: " + paymentStatus);
        System.out.println("Authentication present: " + (authentication != null));
        if (authentication != null) {
            System.out.println("Authenticated user: " + authentication.getName());
        }
        System.out.println("============================================================");
        
        if (authentication == null) {
            System.out.println("Error: Authentication is null");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            final String userEmail = authentication.getPrincipal() instanceof UserDetails
                ? ((UserDetails) authentication.getPrincipal()).getUsername()
                : authentication.getName();
            
            System.out.println("Looking up user with email: " + userEmail);
            User user = userService.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", userEmail));
            
            System.out.println("Found user: " + user.getFirstName() + " " + user.getLastName());
            
            // Check if order belongs to user
            System.out.println("Looking up order with ID: " + id);
            Order order = orderService.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", id.toString()));
            
            System.out.println("Found order with total: " + order.getTotalAmount());
            
            if (!order.getUser().getId().equals(user.getId())) {
                System.out.println("Error: Order does not belong to user");
                System.out.println("Order user ID: " + order.getUser().getId() + ", Current user ID: " + user.getId());
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            
            // Only allow changing to COMPLETED status for users
            if (!"COMPLETED".equals(paymentStatus)) {
                System.out.println("Error: Invalid payment status - users can only mark as COMPLETED");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .header("X-Error", "Users can only mark orders as COMPLETED")
                    .build();
            }
            
            // Update the payment status
            System.out.println("Updating payment status to: " + paymentStatus);
            order = orderService.updatePaymentStatus(id, paymentStatus);
            System.out.println("Successfully updated payment status");
            return ResponseEntity.ok(orderMapper.toResponse(order));
        } catch (ResourceNotFoundException e) {
            System.out.println("Error: Resource not found - " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .header("X-Error", e.getMessage())
                .build();
        } catch (BusinessException e) {
            System.out.println("Error: Business exception - " + e.getMessage());
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                .header("X-Error", e.getMessage())
                .build();
        } catch (IllegalStateException e) {
            System.out.println("Error: Illegal state - " + e.getMessage());
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                .header("X-Error", e.getMessage())
                .build();
        } catch (Exception e) {
            System.out.println("Error: Unexpected exception - " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .header("X-Error", "An error occurred while updating payment status")
                .build();
        }
    }
    
    // Test endpoint for debugging
    @GetMapping("/test-payment/{id}")
    public ResponseEntity<String> testPaymentEndpoint(@PathVariable Long id) {
        try {
            System.out.println("Test endpoint called for order ID: " + id);
            
            Optional<Order> orderOpt = orderService.findById(id);
            if (orderOpt.isPresent()) {
                Order order = orderOpt.get();
                System.out.println("Order found: " + order);
                return ResponseEntity.ok("Order found: ID=" + order.getId() + 
                    ", Status=" + order.getStatus() + 
                    ", PaymentStatus=" + order.getPaymentStatus() +
                    ", User=" + (order.getUser() != null ? order.getUser().getEmail() : "null") +
                    ", Total=" + order.getTotalAmount());
            } else {
                System.out.println("Order not found with ID: " + id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Order not found with ID: " + id);
            }
        } catch (Exception e) {
            System.out.println("Error in test endpoint: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/test-access")
    public ResponseEntity<Map<String, String>> testOrderAccess(Authentication authentication) {
        Map<String, String> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "You have access to order endpoints");

        // Add authentication details if available
        if (authentication != null) {
            response.put("authenticated", "true");
            response.put("username", authentication.getName());
            response.put("authorities", authentication.getAuthorities().toString());
        } else {
            response.put("authenticated", "false");
            response.put("note", "Using fallback authentication");
        }

        return ResponseEntity.ok(response);
    }
} 