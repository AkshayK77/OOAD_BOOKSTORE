package com.bookstore.dto.book;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Response object containing book details")
public class BookResponse {
    
    @Schema(description = "Unique identifier of the book", example = "1")
    private Long id;
    
    @Schema(description = "Title of the book", example = "The Great Gatsby")
    private String title;
    
    @Schema(description = "Author of the book", example = "F. Scott Fitzgerald")
    private String author;
    
    @Schema(description = "Price of the book", example = "29.99")
    private BigDecimal price;
    
    @Schema(description = "Category of the book", example = "Fiction")
    private String category;
    
    @Schema(description = "Number of books in stock", example = "100")
    private Integer stockQuantity;
    
    @Schema(description = "Format of the book", example = "Hardcover")
    private String format;
    
    @Schema(description = "Description of the book", example = "A story of decadence and excess...")
    private String description;
    
    @Schema(description = "Average rating of the book", example = "4.5")
    private Double averageRating;
    
    @Schema(description = "Total number of reviews for the book", example = "150")
    private Integer reviewCount;
    
    @Schema(description = "Whether the book is currently in stock", example = "true")
    private Boolean inStock;
} 