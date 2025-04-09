package com.bookstore.controller;

import java.util.List;

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

import com.bookstore.dto.review.ReviewRequest;
import com.bookstore.dto.review.ReviewResponse;
import com.bookstore.entity.User;
import com.bookstore.mapper.ReviewMapper;
import com.bookstore.model.Book;
import com.bookstore.model.Review;
import com.bookstore.service.BookService;
import com.bookstore.service.ReviewService;
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
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
@Tag(name = "Review Management", description = "APIs for managing book reviews")
public class ReviewController {
    
    private final ReviewService reviewService;
    private final UserService userService;
    private final BookService bookService;
    private final ReviewMapper reviewMapper;
    
    @Operation(summary = "Get book reviews", description = "Retrieves all reviews for a specific book")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved reviews",
            content = @Content(schema = @Schema(implementation = ReviewResponse.class),
                examples = @ExampleObject(value = """
                    [
                      {
                        "id": 1,
                        "userId": 1,
                        "username": "john_doe",
                        "bookId": 1,
                        "bookTitle": "The Great Gatsby",
                        "rating": 5,
                        "comment": "A masterpiece of American literature!",
                        "createdAt": "2024-03-20T10:30:00",
                        "updatedAt": "2024-03-20T10:30:00"
                      }
                    ]
                    """))),
        @ApiResponse(responseCode = "404", description = "Book not found",
            content = @Content(schema = @Schema(ref = "ErrorResponse"),
                examples = @ExampleObject(value = """
                    {
                      "timestamp": "2024-03-20T10:30:00Z",
                      "status": 404,
                      "error": "Not Found",
                      "message": "Book with ID 1 not found",
                      "path": "/api/reviews/book/1"
                    }
                    """))),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/book/{bookId}")
    public ResponseEntity<List<ReviewResponse>> getBookReviews(
            @Parameter(description = "ID of the book to get reviews for", required = true)
            @PathVariable Long bookId) {
        Book book = bookService.findById(bookId)
                .orElseThrow(() -> new IllegalStateException("Book not found"));
        return ResponseEntity.ok(reviewMapper.toResponse(reviewService.findByBook(book)));
    }
    
    @Operation(summary = "Get latest book reviews", description = "Retrieves the most recent reviews for a specific book")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved latest reviews",
            content = @Content(schema = @Schema(implementation = ReviewResponse.class),
                examples = @ExampleObject(value = """
                    [
                      {
                        "id": 1,
                        "userId": 1,
                        "username": "john_doe",
                        "bookId": 1,
                        "bookTitle": "The Great Gatsby",
                        "rating": 5,
                        "comment": "A masterpiece of American literature!",
                        "createdAt": "2024-03-20T10:30:00",
                        "updatedAt": "2024-03-20T10:30:00"
                      }
                    ]
                    """))),
        @ApiResponse(responseCode = "404", description = "Book not found",
            content = @Content(schema = @Schema(ref = "ErrorResponse"),
                examples = @ExampleObject(value = """
                    {
                      "timestamp": "2024-03-20T10:30:00Z",
                      "status": 404,
                      "error": "Not Found",
                      "message": "Book with ID 1 not found",
                      "path": "/api/reviews/book/1/latest"
                    }
                    """))),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/book/{bookId}/latest")
    public ResponseEntity<List<ReviewResponse>> getLatestBookReviews(
            @Parameter(description = "ID of the book to get latest reviews for", required = true)
            @PathVariable Long bookId) {
        Book book = bookService.findById(bookId)
                .orElseThrow(() -> new IllegalStateException("Book not found"));
        return ResponseEntity.ok(reviewMapper.toResponse(reviewService.findLatestReviewsForBook(book)));
    }
    
    @Operation(summary = "Get book average rating", description = "Retrieves the average rating for a specific book")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved average rating",
            content = @Content(schema = @Schema(implementation = Double.class),
                examples = @ExampleObject(value = "4.5"))),
        @ApiResponse(responseCode = "404", description = "Book not found",
            content = @Content(schema = @Schema(ref = "ErrorResponse"),
                examples = @ExampleObject(value = """
                    {
                      "timestamp": "2024-03-20T10:30:00Z",
                      "status": 404,
                      "error": "Not Found",
                      "message": "Book with ID 1 not found",
                      "path": "/api/reviews/book/1/rating"
                    }
                    """))),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/book/{bookId}/rating")
    public ResponseEntity<Double> getBookAverageRating(
            @Parameter(description = "ID of the book to get average rating for", required = true)
            @PathVariable Long bookId) {
        Book book = bookService.findById(bookId)
                .orElseThrow(() -> new IllegalStateException("Book not found"));
        return ResponseEntity.ok(reviewService.getAverageRatingForBook(book.getId()));
    }
    
    @Operation(summary = "Create review", description = "Creates a new review for a book")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully created review",
            content = @Content(schema = @Schema(implementation = ReviewResponse.class),
                examples = @ExampleObject(value = """
                    {
                      "id": 1,
                      "userId": 1,
                      "username": "john_doe",
                      "bookId": 1,
                      "bookTitle": "The Great Gatsby",
                      "rating": 5,
                      "comment": "A masterpiece of American literature!",
                      "createdAt": "2024-03-20T10:30:00",
                      "updatedAt": "2024-03-20T10:30:00"
                    }
                    """))),
        @ApiResponse(responseCode = "400", description = "Invalid review data",
            content = @Content(schema = @Schema(ref = "ValidationErrorResponse"),
                examples = @ExampleObject(value = """
                    {
                      "timestamp": "2024-03-20T10:30:00Z",
                      "status": 400,
                      "error": "Bad Request",
                      "message": "Validation failed",
                      "path": "/api/reviews/book/1",
                      "errors": [
                        {
                          "field": "rating",
                          "message": "Rating must be between 1 and 5"
                        },
                        {
                          "field": "comment",
                          "message": "Comment must be between 10 and 1000 characters"
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
                      "path": "/api/reviews/book/1"
                    }
                    """))),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/book/{bookId}")
    public ResponseEntity<ReviewResponse> createReview(
            @Parameter(description = "ID of the book to review", required = true)
            @PathVariable Long bookId,
            @Parameter(description = "Review details", required = true)
            @Valid @RequestBody ReviewRequest reviewRequest,
            Authentication authentication) {
        User user = userService.findByEmail(authentication.getName())
                .orElseThrow(() -> new IllegalStateException("User not found"));
        Book book = bookService.findById(bookId)
                .orElseThrow(() -> new IllegalStateException("Book not found"));
        
        Review review = reviewService.createReview(reviewRequest, user, book);
        return ResponseEntity.ok(reviewMapper.toResponse(review));
    }
    
    @Operation(summary = "Update review", description = "Updates an existing review (User can only update their own reviews)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully updated review",
            content = @Content(schema = @Schema(implementation = ReviewResponse.class),
                examples = @ExampleObject(value = """
                    {
                      "id": 1,
                      "userId": 1,
                      "username": "john_doe",
                      "bookId": 1,
                      "bookTitle": "The Great Gatsby",
                      "rating": 5,
                      "comment": "A masterpiece of American literature!",
                      "createdAt": "2024-03-20T10:30:00",
                      "updatedAt": "2024-03-20T10:35:00"
                    }
                    """))),
        @ApiResponse(responseCode = "400", description = "Invalid review data",
            content = @Content(schema = @Schema(ref = "ValidationErrorResponse"),
                examples = @ExampleObject(value = """
                    {
                      "timestamp": "2024-03-20T10:30:00Z",
                      "status": 400,
                      "error": "Bad Request",
                      "message": "Validation failed",
                      "path": "/api/reviews/1",
                      "errors": [
                        {
                          "field": "rating",
                          "message": "Rating must be between 1 and 5"
                        },
                        {
                          "field": "comment",
                          "message": "Comment must be between 10 and 1000 characters"
                        }
                      ]
                    }
                    """))),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Not owner of the review"),
        @ApiResponse(responseCode = "404", description = "Review not found",
            content = @Content(schema = @Schema(ref = "ErrorResponse"),
                examples = @ExampleObject(value = """
                    {
                      "timestamp": "2024-03-20T10:30:00Z",
                      "status": 404,
                      "error": "Not Found",
                      "message": "Review with ID 1 not found",
                      "path": "/api/reviews/1"
                    }
                    """))),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PutMapping("/{id}")
    public ResponseEntity<ReviewResponse> updateReview(
            @Parameter(description = "ID of the review to update", required = true)
            @PathVariable Long id,
            @Parameter(description = "Updated review details", required = true)
            @Valid @RequestBody ReviewRequest reviewRequest,
            Authentication authentication) {
        User user = userService.findByEmail(authentication.getName())
                .orElseThrow(() -> new IllegalStateException("User not found"));
        Review review = reviewService.findById(id)
                .orElseThrow(() -> new IllegalStateException("Review not found"));
        
        if (!review.getUser().getId().equals(user.getId())) {
            return ResponseEntity.status(403).build();
        }
        
        ReviewResponse updatedReview = reviewService.updateReview(id, reviewRequest);
        return ResponseEntity.ok(updatedReview);
    }
    
    @Operation(summary = "Delete review", description = "Deletes a review (User can only delete their own reviews, Admin can delete any)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully deleted review"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Not owner of the review"),
        @ApiResponse(responseCode = "404", description = "Review not found",
            content = @Content(schema = @Schema(ref = "ErrorResponse"),
                examples = @ExampleObject(value = """
                    {
                      "timestamp": "2024-03-20T10:30:00Z",
                      "status": 404,
                      "error": "Not Found",
                      "message": "Review with ID 1 not found",
                      "path": "/api/reviews/1"
                    }
                    """))),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReview(
            @Parameter(description = "ID of the review to delete", required = true)
            @PathVariable Long id, 
            Authentication authentication) {
        User user = userService.findByEmail(authentication.getName())
                .orElseThrow(() -> new IllegalStateException("User not found"));
        Review review = reviewService.findById(id)
                .orElseThrow(() -> new IllegalStateException("Review not found"));
        
        if (!review.getUser().getId().equals(user.getId()) && !user.getRole().equals("ROLE_ADMIN")) {
            return ResponseEntity.status(403).build();
        }
        
        reviewService.deleteReview(id);
        return ResponseEntity.ok().build();
    }
} 