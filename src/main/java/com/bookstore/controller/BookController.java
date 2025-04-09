package com.bookstore.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bookstore.dto.book.BookRequest;
import com.bookstore.dto.book.BookResponse;
import com.bookstore.model.Book;
import com.bookstore.service.BookService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
@Tag(name = "Book", description = "Book management APIs")
public class BookController {
    
    private final BookService bookService;
    
    @Operation(summary = "Get all books", description = "Retrieves a list of all books")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved list of books"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping
    public ResponseEntity<List<BookResponse>> getAllBooks() {
        List<Book> books = bookService.findAll();
        return ResponseEntity.ok(bookService.getBookResponses(books));
    }
    
    @Operation(summary = "Get book by ID", description = "Retrieves a book by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved book"),
        @ApiResponse(responseCode = "404", description = "Book not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/{id}")
    public ResponseEntity<BookResponse> getBookById(
            @Parameter(description = "ID of the book to retrieve", required = true)
            @PathVariable Long id) {
        return bookService.findById(id)
                .map(book -> ResponseEntity.ok(bookService.getBookResponse(book)))
                .orElse(ResponseEntity.notFound().build());
    }
    
    @Operation(summary = "Search books", description = "Search books by title or author")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved matching books"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/search")
    public ResponseEntity<List<BookResponse>> searchBooks(
            @Parameter(description = "Search keyword", required = true)
            @RequestParam String keyword) {
        List<Book> books = bookService.searchBooks(keyword);
        return ResponseEntity.ok(bookService.getBookResponses(books));
    }
    
    @Operation(summary = "Get books by category", description = "Retrieves books by category")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved books"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/category/{category}")
    public ResponseEntity<List<BookResponse>> getBooksByCategory(
            @Parameter(description = "Category of books to retrieve", required = true)
            @PathVariable String category) {
        List<Book> books = bookService.findByCategory(category);
        return ResponseEntity.ok(bookService.getBookResponses(books));
    }
    
    @Operation(summary = "Create a new book", description = "Creates a new book (Admin only)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Successfully created book",
            content = @Content(schema = @Schema(implementation = BookResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/admin")
    public ResponseEntity<BookResponse> createBook(@Valid @RequestBody BookRequest bookRequest) {
        // Ensure ISBN is never null
        if (bookRequest.getIsbn() == null || bookRequest.getIsbn().trim().isEmpty()) {
            bookRequest.setIsbn(generateUniqueISBN());
        }
        
        Book book = bookService.createBook(bookRequest);
        BookResponse bookResponse = bookService.getBookResponse(book);
        return ResponseEntity.status(HttpStatus.CREATED).body(bookResponse);
    }
    
    // Helper method to generate a unique ISBN
    private String generateUniqueISBN() {
        String timestamp = String.valueOf(System.currentTimeMillis()).substring(7);
        String random = String.format("%04d", new java.util.Random().nextInt(10000));
        return "978-" + timestamp + random;
    }
    
    @Operation(summary = "Update a book", description = "Updates an existing book by ID (Admin only)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully updated book",
                content = @Content(schema = @Schema(implementation = BookResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        @ApiResponse(responseCode = "404", description = "Book not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping("/admin/{id}")
    public ResponseEntity<BookResponse> updateBook(
            @Parameter(description = "Book ID", required = true)
            @PathVariable Long id,
            @Parameter(description = "Updated book details", required = true)
            @Valid @RequestBody BookRequest bookRequest) {
        // Ensure ISBN is never null
        if (bookRequest.getIsbn() == null || bookRequest.getIsbn().trim().isEmpty()) {
            bookRequest.setIsbn(generateUniqueISBN());
        }
        
        Book book = bookService.updateBook(id, bookRequest);
        return ResponseEntity.ok(bookService.getBookResponse(book));
    }
    
    @Operation(summary = "Delete a book", description = "Deletes a book by its ID (Admin only)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Successfully deleted book"),
        @ApiResponse(responseCode = "404", description = "Book not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping("/admin/{id}")
    public ResponseEntity<Void> deleteBook(
            @Parameter(description = "ID of the book to delete", required = true)
            @PathVariable Long id) {
        try {
            bookService.deleteBook(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            if (e.getMessage() != null && e.getMessage().contains("foreign key constraint")) {
                // Return a more specific error message for foreign key constraint failures
                return ResponseEntity.status(HttpStatus.CONFLICT)
                    .header("X-Error", "This book is referenced by orders and cannot be deleted")
                    .build();
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .header("X-Error", e.getMessage())
                .build();
        }
    }
    
    @Operation(summary = "Get out of stock books", description = "Retrieves all books that are out of stock (Admin only)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved out of stock books"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/admin/stock")
    public ResponseEntity<List<BookResponse>> getOutOfStockBooks() {
        List<Book> books = bookService.findOutOfStockBooks();
        return ResponseEntity.ok(bookService.getBookResponses(books));
    }
    
    @Operation(summary = "Update book stock", description = "Updates the stock quantity of a book")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully updated book stock"),
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        @ApiResponse(responseCode = "404", description = "Book not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PatchMapping("/admin/{id}/stock")
    public ResponseEntity<BookResponse> updateBookStock(
            @Parameter(description = "ID of the book to update", required = true)
            @PathVariable Long id,
            @Parameter(description = "New stock quantity", required = true)
            @RequestParam int quantity) {
        Book book = bookService.updateStock(id, quantity);
        return ResponseEntity.ok(bookService.getBookResponse(book));
    }
    
    @Operation(summary = "Update all book prices", description = "Updates all book prices to be either 400, 500, or 600 rupees")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully updated prices for all books"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/admin/update-prices")
    public ResponseEntity<String> updateAllBookPrices() {
        List<Book> books = bookService.findAll();
        int updatedCount = 0;
        
        for (Book book : books) {
            try {
                // Generate a random price between 400, 500, or 600
                int[] priceOptions = {400, 500, 600};
                int randomIndex = new java.util.Random().nextInt(priceOptions.length);
                Double newPrice = Double.valueOf(priceOptions[randomIndex]);
                
                // Create a BookRequest with the current book's data
                BookRequest bookRequest = new BookRequest();
                bookRequest.setTitle(book.getTitle());
                bookRequest.setAuthor(book.getAuthor());
                bookRequest.setIsbn(book.getIsbn());
                bookRequest.setPrice(newPrice);
                bookRequest.setDescription(book.getDescription());
                bookRequest.setCategory(book.getCategory());
                bookRequest.setStockQuantity(book.getStockQuantity());
                bookRequest.setFormat(book.getFormat());
                bookRequest.setImageUrl(book.getImageUrl());
                
                // Use the existing updateBook method
                bookService.updateBook(book.getId(), bookRequest);
                updatedCount++;
            } catch (Exception e) {
                System.err.println("Error updating price for book ID " + book.getId() + ": " + e.getMessage());
            }
        }
        
        return ResponseEntity.ok("Successfully updated prices for " + updatedCount + " books to be either 400, 500, or 600 rupees");
    }
} 