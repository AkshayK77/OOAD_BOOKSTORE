package com.bookstore.service;

import java.util.List;
import java.util.Optional;

import com.bookstore.dto.book.BookRequest;
import com.bookstore.dto.book.BookResponse;
import com.bookstore.model.Book;

public interface BookService {
    List<Book> findAll();
    
    Optional<Book> findById(Long id);
    
    Optional<Book> findByTitle(String title);
    
    List<Book> searchBooks(String keyword);
    
    List<Book> findByCategory(String category);
    
    List<Book> findOutOfStockBooks();
    
    Book createBook(BookRequest bookRequest);
    
    Book updateBook(Long id, BookRequest bookRequest);
    
    void deleteBook(Long id);
    
    Book updateStock(Long id, int quantity);
    
    BookResponse getBookResponse(Book book);
    
    List<BookResponse> getBookResponses(List<Book> books);
} 