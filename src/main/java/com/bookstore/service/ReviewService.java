package com.bookstore.service;

import java.util.List;
import java.util.Optional;

import com.bookstore.dto.review.ReviewRequest;
import com.bookstore.dto.review.ReviewResponse;
import com.bookstore.entity.User;
import com.bookstore.model.Book;
import com.bookstore.model.Review;

public interface ReviewService {
    Review createReview(Review review);
    Optional<Review> findById(Long id);
    List<ReviewResponse> findByBookId(Long bookId);
    List<ReviewResponse> findByUserId(Long userId);
    Optional<Review> findByBookIdAndUserId(Long bookId, Long userId);
    Review updateReview(Review review);
    void deleteReview(Long id);
    Double calculateAverageRating(Long bookId);
    
    List<Review> findByBook(Book book);
    List<Review> findByUser(User user);
    List<Review> findLatestReviewsForBook(Book book);
    
    long getReviewCountForBook(Long bookId);
    double getAverageRatingForBook(Long bookId);
    
    boolean hasUserReviewedBook(User user, Book book);
    
    Review createReview(ReviewRequest request, User user, Book book);
    
    ReviewResponse updateReview(Long id, ReviewRequest request);
} 