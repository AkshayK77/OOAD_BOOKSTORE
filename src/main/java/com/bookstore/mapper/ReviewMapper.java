package com.bookstore.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.bookstore.dto.review.ReviewRequest;
import com.bookstore.dto.review.ReviewResponse;
import com.bookstore.entity.User;
import com.bookstore.model.Book;
import com.bookstore.model.Review;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ReviewMapper {
    
    public Review toEntity(ReviewRequest request, User user, Book book) {
        Review review = new Review();
        review.setBook(book);
        review.setUser(user);
        review.setRating(request.getRating());
        review.setComment(request.getComment());
        review.setReviewDate(java.time.LocalDateTime.now());
        return review;
    }
    
    public ReviewResponse toResponse(Review review) {
        ReviewResponse response = new ReviewResponse();
        response.setId(review.getId());
        response.setUserId(review.getUser().getId());
        response.setUsername(review.getUser().getUsername());
        response.setBookId(review.getBook().getId());
        response.setBookTitle(review.getBook().getTitle());
        response.setRating(review.getRating());
        response.setComment(review.getComment());
        response.setCreatedAt(review.getCreatedAt());
        response.setReviewDate(review.getReviewDate());
        response.setUpdatedAt(review.getUpdatedAt());
        return response;
    }
    
    public List<ReviewResponse> toResponse(List<Review> reviews) {
        return reviews.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public void updateEntity(Review review, ReviewRequest request) {
        review.setRating(request.getRating());
        review.setComment(request.getComment());
        review.setReviewDate(java.time.LocalDateTime.now());
    }
} 