package com.bookstore.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bookstore.dto.review.ReviewRequest;
import com.bookstore.dto.review.ReviewResponse;
import com.bookstore.entity.User;
import com.bookstore.exception.ResourceNotFoundException;
import com.bookstore.mapper.ReviewMapper;
import com.bookstore.model.Book;
import com.bookstore.model.Review;
import com.bookstore.repository.ReviewRepository;
import com.bookstore.service.ReviewService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {
    
    private final ReviewRepository reviewRepository;
    private final ReviewMapper reviewMapper;
    
    @Override
    @Transactional
    public Review createReview(Review review) {
        return reviewRepository.save(review);
    }
    
    @Override
    @Transactional
    public Review updateReview(Review review) {
        if (!reviewRepository.existsById(review.getId())) {
            throw new IllegalStateException("Review not found");
        }
        return reviewRepository.save(review);
    }
    
    @Override
    @Transactional
    public Review createReview(ReviewRequest reviewRequest, User user, Book book) {
        Review review = reviewMapper.toEntity(reviewRequest, user, book);
        return reviewRepository.save(review);
    }
    
    @Override
    public Optional<Review> findById(Long id) {
        return reviewRepository.findById(id);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ReviewResponse> findByBookId(Long bookId) {
        List<Review> reviews = reviewRepository.findByBookId(bookId);
        return reviews.stream()
                .map(reviewMapper::toResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ReviewResponse> findByUserId(Long userId) {
        List<Review> reviews = reviewRepository.findByUserId(userId);
        return reviews.stream()
                .map(reviewMapper::toResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    public Optional<Review> findByBookIdAndUserId(Long bookId, Long userId) {
        return reviewRepository.findByBookIdAndUserId(bookId, userId);
    }
    
    @Override
    @Transactional
    public ReviewResponse updateReview(Long id, ReviewRequest request) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found with id: " + id));
        
        review.setRating(request.getRating());
        review.setComment(request.getComment());
        validateRating(review.getRating());
        
        review = reviewRepository.save(review);
        return reviewMapper.toResponse(review);
    }
    
    @Override
    @Transactional
    public void deleteReview(Long id) {
        if (!reviewRepository.existsById(id)) {
            throw new ResourceNotFoundException("Review not found with id: " + id);
        }
        reviewRepository.deleteById(id);
    }
    
    @Override
    public Double calculateAverageRating(Long bookId) {
        return reviewRepository.calculateAverageRating(bookId);
    }
    
    @Override
    public List<Review> findByBook(Book book) {
        return reviewRepository.findByBook(book);
    }
    
    @Override
    public List<Review> findByUser(User user) {
        return reviewRepository.findByUser(user);
    }
    
    @Override
    public List<Review> findLatestReviewsForBook(Book book) {
        return reviewRepository.findByBookOrderByReviewDateDesc(book);
    }
    
    @Override
    @Transactional(readOnly = true)
    public long getReviewCountForBook(Long bookId) {
        return reviewRepository.countByBookId(bookId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public double getAverageRatingForBook(Long bookId) {
        return reviewRepository.findByBookId(bookId).stream()
                .mapToInt(Review::getRating)
                .average()
                .orElse(0.0);
    }
    
    @Override
    public boolean hasUserReviewedBook(User user, Book book) {
        return reviewRepository.findByBookIdAndUserId(book.getId(), user.getId()).isPresent();
    }
    
    private void validateRating(int rating) {
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }
    }
} 