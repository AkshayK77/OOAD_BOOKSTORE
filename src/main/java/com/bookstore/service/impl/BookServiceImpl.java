package com.bookstore.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bookstore.dto.book.BookRequest;
import com.bookstore.dto.book.BookResponse;
import com.bookstore.exception.ResourceNotFoundException;
import com.bookstore.mapper.BookMapper;
import com.bookstore.model.Book;
import com.bookstore.repository.BookRepository;
import com.bookstore.service.BookService;
import com.bookstore.service.ReviewService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {
    
    private final BookRepository bookRepository;
    private final BookMapper bookMapper;
    private final ReviewService reviewService;
    
    @Override
    @Transactional
    public Book createBook(BookRequest bookRequest) {
        Book book = bookMapper.toEntity(bookRequest);
        return bookRepository.save(book);
    }
    
    @Override
    public Optional<Book> findById(Long id) {
        return bookRepository.findById(id);
    }
    
    @Override
    public Optional<Book> findByTitle(String title) {
        return bookRepository.findByTitle(title);
    }
    
    @Override
    public List<Book> findAll() {
        return bookRepository.findAll();
    }
    
    @Override
    public List<Book> findByCategory(String category) {
        return bookRepository.findByCategory(category);
    }
    
    @Override
    public List<Book> searchBooks(String keyword) {
        return bookRepository.findByTitleContainingOrAuthorContaining(keyword, keyword);
    }
    
    @Override
    public List<Book> findOutOfStockBooks() {
        return bookRepository.findByStockQuantityLessThanEqual(0);
    }
    
    @Override
    @Transactional
    public Book updateBook(Long id, BookRequest bookRequest) {
        Book existingBook = bookRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("Book not found"));
        
        bookMapper.updateEntity(existingBook, bookRequest);
        return bookRepository.save(existingBook);
    }
    
    @Override
    @Transactional
    public void deleteBook(Long id) {
        if (!bookRepository.existsById(id)) {
            throw new IllegalStateException("Book not found");
        }
        bookRepository.deleteById(id);
    }
    
    @Override
    @Transactional
    public Book updateStock(Long id, int quantity) {
        Book book = bookRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + id));
        book.setStockQuantity(quantity);
        return bookRepository.save(book);
    }
    
    @Override
    public BookResponse getBookResponse(Book book) {
        BookResponse response = bookMapper.toResponse(book);
        Double averageRating = reviewService.getAverageRatingForBook(book.getId());
        int reviewCount = reviewService.findByBook(book).size();
        bookMapper.enrichWithReviewData(response, averageRating, reviewCount);
        return response;
    }
    
    @Override
    public List<BookResponse> getBookResponses(List<Book> books) {
        return books.stream()
            .map(this::getBookResponse)
            .collect(Collectors.toList());
    }
} 