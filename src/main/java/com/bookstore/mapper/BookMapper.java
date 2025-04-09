package com.bookstore.mapper;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.bookstore.dto.book.BookRequest;
import com.bookstore.dto.book.BookResponse;
import com.bookstore.model.Book;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class BookMapper {
    
    public Book toEntity(BookRequest request) {
        Book book = new Book();
        book.setTitle(request.getTitle());
        book.setAuthor(request.getAuthor());
        book.setIsbn(request.getIsbn());
        book.setPrice(BigDecimal.valueOf(request.getPrice()));
        book.setCategory(request.getCategory());
        book.setStockQuantity(request.getStockQuantity());
        book.setFormat(request.getFormat());
        book.setDescription(request.getDescription());
        book.setImageUrl(request.getImageUrl());
        return book;
    }
    
    public BookResponse toResponse(Book book) {
        BookResponse response = new BookResponse();
        response.setId(book.getId());
        response.setTitle(book.getTitle());
        response.setAuthor(book.getAuthor());
        response.setPrice(book.getPrice());
        response.setCategory(book.getCategory());
        response.setStockQuantity(book.getStockQuantity());
        response.setFormat(book.getFormat());
        response.setDescription(book.getDescription());
        response.setInStock(book.getStockQuantity() > 0);
        return response;
    }
    
    public List<BookResponse> toResponse(List<Book> books) {
        return books.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
    
    public void updateEntity(Book book, BookRequest request) {
        book.setTitle(request.getTitle());
        book.setAuthor(request.getAuthor());
        book.setIsbn(request.getIsbn());
        book.setPrice(BigDecimal.valueOf(request.getPrice()));
        book.setCategory(request.getCategory());
        book.setStockQuantity(request.getStockQuantity());
        book.setFormat(request.getFormat());
        book.setDescription(request.getDescription());
        book.setImageUrl(request.getImageUrl());
    }
    
    public void enrichWithReviewData(BookResponse response, Double averageRating, int reviewCount) {
        response.setAverageRating(averageRating);
        response.setReviewCount(reviewCount);
    }
} 