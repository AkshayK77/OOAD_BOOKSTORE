package com.bookstore.service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.bookstore.dto.review.ReviewRequest;
import com.bookstore.dto.review.ReviewResponse;
import com.bookstore.entity.Role;
import com.bookstore.entity.User;
import com.bookstore.mapper.ReviewMapper;
import com.bookstore.model.Book;
import com.bookstore.model.Review;
import com.bookstore.repository.ReviewRepository;
import com.bookstore.service.impl.ReviewServiceImpl;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private ReviewMapper reviewMapper;

    @InjectMocks
    private ReviewServiceImpl reviewService;

    private Review review;
    private ReviewRequest reviewRequest;
    private User user;
    private Book book;
    private List<Review> reviews;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setEmail("testuser@example.com");
        user.setFirstName("Test");
        user.setLastName("User");
        user.setPassword("password");
        user.setRole(Role.USER);

        book = new Book();
        book.setId(1L);
        book.setTitle("Test Book");

        review = new Review();
        review.setId(1L);
        review.setUser(user);
        review.setBook(book);
        review.setRating(5);
        review.setComment("Great book!");
        review.setCreatedAt(LocalDateTime.now());

        reviewRequest = new ReviewRequest();
        reviewRequest.setBookId(1L);
        reviewRequest.setRating(5);
        reviewRequest.setComment("Great book!");
        reviews = Arrays.asList(review);
    }

    @Test
    void createReview_ShouldReturnCreatedReview() {
        when(reviewMapper.toEntity(reviewRequest, user, book)).thenReturn(review);
        when(reviewRepository.save(review)).thenReturn(review);

        Review result = reviewService.createReview(reviewRequest, user, book);

        assertNotNull(result);
        assertEquals(review.getId(), result.getId());
        assertEquals(user.getId(), result.getUser().getId());
        assertEquals(book.getId(), result.getBook().getId());
        verify(reviewRepository).save(review);
    }

    @Test
    void findById_WhenReviewExists_ShouldReturnReview() {
        when(reviewRepository.findById(1L)).thenReturn(Optional.of(review));

        Optional<Review> result = reviewService.findById(1L);

        assertTrue(result.isPresent());
        assertEquals(review.getId(), result.get().getId());
    }

    @Test
    void findById_WhenReviewDoesNotExist_ShouldReturnEmpty() {
        when(reviewRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<Review> result = reviewService.findById(1L);

        assertFalse(result.isPresent());
    }

    @Test
    void findByBook_ShouldReturnBookReviews() {
        when(reviewRepository.findByBook(book)).thenReturn(reviews);

        List<Review> result = reviewService.findByBook(book);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(book.getId(), result.get(0).getBook().getId());
    }

    @Test
    void findByUser_ShouldReturnUserReviews() {
        when(reviewRepository.findByUser(user)).thenReturn(reviews);

        List<Review> result = reviewService.findByUser(user);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(user.getId(), result.get(0).getUser().getId());
    }

    @Test
    void findLatestReviewsForBook_ShouldReturnLatestReviews() {
        when(reviewRepository.findByBookOrderByReviewDateDesc(book)).thenReturn(reviews);

        List<Review> result = reviewService.findLatestReviewsForBook(book);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(book.getId(), result.get(0).getBook().getId());
    }

    @Test
    void updateReview_WhenReviewExists_ShouldUpdateReview() {
        when(reviewRepository.findById(1L)).thenReturn(Optional.of(review));
        when(reviewRepository.save(review)).thenReturn(review);
        when(reviewMapper.toResponse(review)).thenReturn(new ReviewResponse());

        ReviewResponse result = reviewService.updateReview(1L, reviewRequest);

        assertNotNull(result);
        verify(reviewRepository).save(review);
    }

    @Test
    void updateReview_WhenReviewDoesNotExist_ShouldThrowException() {
        when(reviewRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(IllegalStateException.class, () -> reviewService.updateReview(1L, reviewRequest));
        verify(reviewRepository, never()).save(any());
    }

    @Test
    void deleteReview_WhenReviewExists_ShouldDeleteReview() {
        when(reviewRepository.existsById(1L)).thenReturn(true);

        reviewService.deleteReview(1L);

        verify(reviewRepository).deleteById(1L);
    }

    @Test
    void deleteReview_WhenReviewDoesNotExist_ShouldThrowException() {
        when(reviewRepository.existsById(1L)).thenReturn(false);

        assertThrows(IllegalStateException.class, () -> reviewService.deleteReview(1L));
        verify(reviewRepository, never()).deleteById(any());
    }

    @Test
    void getAverageRatingForBook_ShouldReturnAverageRating() {
        when(reviewRepository.calculateAverageRating(book.getId())).thenReturn(4.5);

        Double result = reviewService.getAverageRatingForBook(book.getId());

        assertNotNull(result);
        assertEquals(4.5, result);
    }

    @Test
    void hasUserReviewedBook_WhenReviewExists_ShouldReturnTrue() {
        when(reviewRepository.findByBookIdAndUserId(book.getId(), user.getId()))
            .thenReturn(Optional.of(review));

        boolean result = reviewService.hasUserReviewedBook(user, book);

        assertTrue(result);
    }

    @Test
    void hasUserReviewedBook_WhenReviewDoesNotExist_ShouldReturnFalse() {
        when(reviewRepository.findByBookIdAndUserId(book.getId(), user.getId()))
            .thenReturn(Optional.empty());

        boolean result = reviewService.hasUserReviewedBook(user, book);

        assertFalse(result);
    }
} 