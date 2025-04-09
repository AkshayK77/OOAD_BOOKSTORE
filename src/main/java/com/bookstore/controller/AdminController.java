package com.bookstore.controller;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Scanner;

import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bookstore.dto.book.BookResponse;
import com.bookstore.model.Book;
import com.bookstore.service.BookService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Tag(name = "Admin", description = "Admin operations")
public class AdminController {
    
    private final JdbcTemplate jdbcTemplate;
    private final BookService bookService;
    
    @Operation(summary = "Get all books (admin)", description = "Admin endpoint to retrieve all books")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved books")
    })
    @GetMapping("/books")
    public ResponseEntity<List<BookResponse>> getAllBooks() {
        List<Book> books = bookService.findAll();
        return ResponseEntity.ok(bookService.getBookResponses(books));
    }
    
    @Operation(summary = "Populate additional books", description = "Adds a large selection of books to the database")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully added books"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/populate-books")
    @Transactional
    public ResponseEntity<String> populateBooks() {
        try {
            // Load SQL file from resources
            InputStream inputStream = getClass().getResourceAsStream("/additional-books.sql");
            if (inputStream == null) {
                return ResponseEntity.badRequest().body("SQL file not found");
            }
            
            // Read the SQL file content
            String sql;
            try (Scanner scanner = new Scanner(inputStream, StandardCharsets.UTF_8)) {
                sql = scanner.useDelimiter("\\A").next();
            }
            
            // Split the SQL into individual statements (approximately)
            String[] statements = sql.split("\\),\\s*\\(");
            
            int totalBooks = 0;
            if (statements.length > 0) {
                // Handle the first statement which includes the INSERT INTO part
                String firstStatement = statements[0] + ")";
                try {
                    jdbcTemplate.update(firstStatement);
                    totalBooks++;
                } catch (Exception e) {
                    System.out.println("Skipping book due to: " + e.getMessage());
                }
                
                // Handle middle statements
                for (int i = 1; i < statements.length - 1; i++) {
                    String statement = "INSERT INTO books (title, author, isbn, price, description, category, stock_quantity, format, image_url, created_at, updated_at) VALUES (" + statements[i] + ")";
                    try {
                        jdbcTemplate.update(statement);
                        totalBooks++;
                    } catch (Exception e) {
                        System.out.println("Skipping book due to: " + e.getMessage());
                    }
                }
                
                // Handle the last statement which includes the final semicolon
                if (statements.length > 1) {
                    String lastStatement = "INSERT INTO books (title, author, isbn, price, description, category, stock_quantity, format, image_url, created_at, updated_at) VALUES (" + statements[statements.length - 1];
                    try {
                        jdbcTemplate.update(lastStatement);
                        totalBooks++;
                    } catch (Exception e) {
                        System.out.println("Skipping book due to: " + e.getMessage());
                    }
                }
            }
            
            return ResponseEntity.ok("Successfully added " + totalBooks + " books to the database");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error populating books: " + e.getMessage());
        }
    }
} 