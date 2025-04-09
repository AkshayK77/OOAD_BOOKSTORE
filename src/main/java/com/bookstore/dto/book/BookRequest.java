package com.bookstore.dto.book;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "Request object for creating or updating a book")
public class BookRequest {
    @Schema(description = "Title of the book", example = "The Great Gatsby")
    @NotBlank(message = "Title is required")
    @Size(min = 1, max = 255, message = "Title must be between 1 and 255 characters")
    private String title;
    
    @Schema(description = "Author of the book", example = "F. Scott Fitzgerald")
    @NotBlank(message = "Author is required")
    @Size(min = 1, max = 255, message = "Author name must be between 1 and 255 characters")
    private String author;
    
    @Schema(description = "ISBN of the book", example = "978-3164815467")
    @NotBlank(message = "ISBN is required")
    @Size(min = 10, max = 17, message = "ISBN must be between 10 and 17 characters")
    private String isbn;
    
    @Schema(description = "Price of the book", example = "29.99")
    @NotNull(message = "Price is required")
    @Positive(message = "Price must be positive")
    private Double price;
    
    @Schema(description = "Category of the book", example = "Fiction")
    @NotBlank(message = "Category is required")
    @Size(min = 1, max = 100, message = "Category must be between 1 and 100 characters")
    private String category;
    
    @Schema(description = "Number of books in stock", example = "100")
    @NotNull(message = "Stock quantity is required")
    @PositiveOrZero(message = "Stock quantity must be zero or positive")
    private Integer stockQuantity;
    
    @Schema(description = "Format of the book", example = "Hardcover")
    @NotBlank(message = "Format is required")
    @Size(min = 1, max = 50, message = "Format must be between 1 and 50 characters")
    private String format; // PHYSICAL or EBOOK
    
    @Schema(description = "Description of the book", example = "A story of decadence and excess...")
    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    private String description;
    
    @Schema(description = "Image URL of the book", example = "https://example.com/image.jpg")
    @Size(max = 255, message = "Image URL cannot exceed 255 characters")
    private String imageUrl;
} 