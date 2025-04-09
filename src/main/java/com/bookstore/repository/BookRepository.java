package com.bookstore.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.bookstore.model.Book;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    Optional<Book> findByTitle(String title);
    List<Book> findByCategory(String category);
    
    @Query("SELECT b FROM Book b WHERE b.title LIKE %:keyword% OR b.author LIKE %:keyword% OR b.description LIKE %:keyword%")
    List<Book> searchBooks(String keyword);
    
    @Query("SELECT b FROM Book b WHERE b.stockQuantity = 0")
    List<Book> findOutOfStockBooks();

    List<Book> findByTitleContainingOrAuthorContaining(String title, String author);
    List<Book> findByStockQuantityLessThanEqual(int quantity);
} 