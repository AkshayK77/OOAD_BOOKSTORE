package com.bookstore.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.bookstore.entity.User;
import com.bookstore.model.Book;
import com.bookstore.model.Review;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByBook(Book book);
    List<Review> findByUser(User user);
    
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.book = ?1")
    Double getAverageRatingForBook(Book book);
    
    @Query("SELECT COUNT(r) FROM Review r WHERE r.book = ?1")
    Long getReviewCountForBook(Book book);
    
    @Query("SELECT r FROM Review r WHERE r.book = ?1 ORDER BY r.reviewDate DESC")
    List<Review> findByBookOrderByReviewDateDesc(Book book);

    List<Review> findByBookId(Long bookId);
    List<Review> findByUserId(Long userId);
    Optional<Review> findByBookIdAndUserId(Long bookId, Long userId);
    
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.book.id = :bookId")
    Double calculateAverageRating(Long bookId);
    
    @Query("SELECT COUNT(r) FROM Review r WHERE r.book.id = :bookId")
    Long countByBookId(Long bookId);
} 