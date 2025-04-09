package com.bookstore.dto.review;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Response object containing review details")
public class ReviewResponse {
    
    @Schema(description = "Unique identifier of the review", example = "1")
    private Long id;
    
    @Schema(description = "ID of the user who wrote the review", example = "1")
    private Long userId;
    
    @Schema(description = "Username of the reviewer", example = "john_doe")
    private String username;
    
    @Schema(description = "ID of the book being reviewed", example = "1")
    private Long bookId;
    
    @Schema(description = "Title of the book being reviewed", example = "The Great Gatsby")
    private String bookTitle;
    
    @Schema(description = "Rating given to the book (1-5)", example = "5")
    private Integer rating;
    
    @Schema(description = "Review comment", example = "A masterpiece of American literature!")
    private String comment;
    
    @Schema(description = "When the review was created", example = "2024-03-20T10:30:00")
    private LocalDateTime createdAt;
    
    @Schema(description = "When the review was made", example = "2024-03-20T10:30:00")
    private LocalDateTime reviewDate;
    
    @Schema(description = "When the review was last updated", example = "2024-03-20T10:30:00")
    private LocalDateTime updatedAt;
} 