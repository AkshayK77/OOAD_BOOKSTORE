package com.bookstore.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bookstore.dto.cart.CartItemRequest;
import com.bookstore.dto.cart.CartResponse;
import com.bookstore.entity.User;
import com.bookstore.service.CartService;
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
@RequestMapping("/api/cart")
@RequiredArgsConstructor
@Tag(name = "Cart Management", description = "APIs for managing shopping cart")
@SecurityRequirement(name = "bearerAuth")
public class CartController {
    
    private final CartService cartService;
    private final UserService userService;
    
    @Operation(summary = "Get cart", description = "Retrieves the current user's shopping cart")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved cart",
            content = @Content(schema = @Schema(implementation = CartResponse.class),
                examples = @ExampleObject(value = """
                    {
                      "id": 1,
                      "userId": 1,
                      "items": [
                        {
                          "id": 1,
                          "bookId": 1,
                          "bookTitle": "The Great Gatsby",
                          "quantity": 2,
                          "price": 29.99,
                          "subtotal": 59.98
                        }
                      ],
                      "totalAmount": 59.98
                    }
                    """))),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping
    public ResponseEntity<CartResponse> getCart(Authentication authentication) {
        User user = userService.findByEmail(authentication.getName())
                .orElseThrow(() -> new IllegalStateException("User not found"));
        return ResponseEntity.ok(cartService.getCart(user));
    }
    
    @Operation(summary = "Add item to cart", description = "Adds a new item to the current user's shopping cart")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully added item to cart",
            content = @Content(schema = @Schema(implementation = CartResponse.class),
                examples = @ExampleObject(value = """
                    {
                      "id": 1,
                      "userId": 1,
                      "items": [
                        {
                          "id": 1,
                          "bookId": 1,
                          "bookTitle": "The Great Gatsby",
                          "quantity": 2,
                          "price": 29.99,
                          "subtotal": 59.98
                        }
                      ],
                      "totalAmount": 59.98
                    }
                    """))),
        @ApiResponse(responseCode = "400", description = "Invalid item data",
            content = @Content(schema = @Schema(ref = "ValidationErrorResponse"),
                examples = @ExampleObject(value = """
                    {
                      "timestamp": "2024-03-20T10:30:00Z",
                      "status": 400,
                      "error": "Bad Request",
                      "message": "Validation failed",
                      "path": "/api/cart/items",
                      "errors": [
                        {
                          "field": "bookId",
                          "message": "Book ID is required"
                        },
                        {
                          "field": "quantity",
                          "message": "Quantity must be greater than 0"
                        }
                      ]
                    }
                    """))),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "404", description = "Book not found",
            content = @Content(schema = @Schema(ref = "ErrorResponse"),
                examples = @ExampleObject(value = """
                    {
                      "timestamp": "2024-03-20T10:30:00Z",
                      "status": 404,
                      "error": "Not Found",
                      "message": "Book with ID 1 not found",
                      "path": "/api/cart/items"
                    }
                    """))),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/items")
    public ResponseEntity<CartResponse> addItem(
            @Parameter(description = "Item details to add to cart", required = true)
            @Valid @RequestBody CartItemRequest cartItemRequest,
            Authentication authentication) {
        User user = userService.findByEmail(authentication.getName())
                .orElseThrow(() -> new IllegalStateException("User not found"));
        return ResponseEntity.ok(cartService.addItem(user, cartItemRequest));
    }
    
    @Operation(summary = "Update cart item", description = "Updates an existing item in the current user's shopping cart")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully updated cart item",
            content = @Content(schema = @Schema(implementation = CartResponse.class),
                examples = @ExampleObject(value = """
                    {
                      "id": 1,
                      "userId": 1,
                      "items": [
                        {
                          "id": 1,
                          "bookId": 1,
                          "bookTitle": "The Great Gatsby",
                          "quantity": 3,
                          "price": 29.99,
                          "subtotal": 89.97
                        }
                      ],
                      "totalAmount": 89.97
                    }
                    """))),
        @ApiResponse(responseCode = "400", description = "Invalid item data",
            content = @Content(schema = @Schema(ref = "ValidationErrorResponse"),
                examples = @ExampleObject(value = """
                    {
                      "timestamp": "2024-03-20T10:30:00Z",
                      "status": 400,
                      "error": "Bad Request",
                      "message": "Validation failed",
                      "path": "/api/cart/items/1",
                      "errors": [
                        {
                          "field": "quantity",
                          "message": "Quantity must be greater than 0"
                        }
                      ]
                    }
                    """))),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "404", description = "Cart item not found",
            content = @Content(schema = @Schema(ref = "ErrorResponse"),
                examples = @ExampleObject(value = """
                    {
                      "timestamp": "2024-03-20T10:30:00Z",
                      "status": 404,
                      "error": "Not Found",
                      "message": "Cart item with ID 1 not found",
                      "path": "/api/cart/items/1"
                    }
                    """))),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping("/items/{id}")
    public ResponseEntity<CartResponse> updateItem(
            @Parameter(description = "ID of the cart item to update", required = true)
            @PathVariable Long id,
            @Parameter(description = "Updated item details", required = true)
            @Valid @RequestBody CartItemRequest cartItemRequest,
            Authentication authentication) {
        User user = userService.findByEmail(authentication.getName())
                .orElseThrow(() -> new IllegalStateException("User not found"));
        return ResponseEntity.ok(cartService.updateItem(user, id, cartItemRequest));
    }
    
    @Operation(summary = "Remove item from cart", description = "Removes an item from the current user's shopping cart")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully removed item from cart",
            content = @Content(schema = @Schema(implementation = CartResponse.class),
                examples = @ExampleObject(value = """
                    {
                      "id": 1,
                      "userId": 1,
                      "items": [],
                      "totalAmount": 0.00
                    }
                    """))),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "404", description = "Cart item not found",
            content = @Content(schema = @Schema(ref = "ErrorResponse"),
                examples = @ExampleObject(value = """
                    {
                      "timestamp": "2024-03-20T10:30:00Z",
                      "status": 404,
                      "error": "Not Found",
                      "message": "Cart item with ID 1 not found",
                      "path": "/api/cart/items/1"
                    }
                    """))),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping("/items/{id}")
    public ResponseEntity<CartResponse> removeItem(
            @Parameter(description = "ID of the cart item to remove", required = true)
            @PathVariable Long id,
            Authentication authentication) {
        User user = userService.findByEmail(authentication.getName())
                .orElseThrow(() -> new IllegalStateException("User not found"));
        return ResponseEntity.ok(cartService.removeItem(user, id));
    }
    
    @Operation(summary = "Clear cart", description = "Removes all items from the current user's shopping cart")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully cleared cart"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping
    public ResponseEntity<Void> clearCart(Authentication authentication) {
        User user = userService.findByEmail(authentication.getName())
                .orElseThrow(() -> new IllegalStateException("User not found"));
        cartService.clearCart(user);
        return ResponseEntity.ok().build();
    }
} 