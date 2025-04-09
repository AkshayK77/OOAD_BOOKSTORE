package com.bookstore.service;

import java.math.BigDecimal;
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

import com.bookstore.dto.book.BookRequest;
import com.bookstore.mapper.BookMapper;
import com.bookstore.model.Book;
import com.bookstore.repository.BookRepository;
import com.bookstore.service.impl.BookServiceImpl;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private BookMapper bookMapper;

    @InjectMocks
    private BookServiceImpl bookService;

    private Book book;
    private BookRequest bookRequest;
    private List<Book> books;

    @BeforeEach
    void setUp() {
        book = new Book();
        book.setId(1L);
        book.setTitle("Test Book");
        book.setAuthor("Test Author");
        book.setPrice(BigDecimal.valueOf(29.99));
        book.setCategory("Fiction");
        book.setStockQuantity(10);
        book.setFormat("PHYSICAL");
        book.setDescription("Test Description");

        bookRequest = new BookRequest();
        bookRequest.setTitle("Test Book");
        bookRequest.setAuthor("Test Author");
        bookRequest.setPrice(29.99);
        bookRequest.setCategory("Fiction");
        bookRequest.setStockQuantity(10);
        bookRequest.setFormat("PHYSICAL");
        bookRequest.setDescription("Test Description");

        books = Arrays.asList(book);
    }

    @Test
    void createBook_ShouldReturnCreatedBook() {
        when(bookMapper.toEntity(bookRequest)).thenReturn(book);
        when(bookRepository.save(book)).thenReturn(book);

        Book result = bookService.createBook(bookRequest);

        assertNotNull(result);
        assertEquals(book.getId(), result.getId());
        assertEquals(book.getTitle(), result.getTitle());
        verify(bookRepository).save(book);
    }

    @Test
    void findById_WhenBookExists_ShouldReturnBook() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        Optional<Book> result = bookService.findById(1L);

        assertTrue(result.isPresent());
        assertEquals(book.getId(), result.get().getId());
    }

    @Test
    void findById_WhenBookDoesNotExist_ShouldReturnEmpty() {
        when(bookRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<Book> result = bookService.findById(1L);

        assertFalse(result.isPresent());
    }

    @Test
    void findAll_ShouldReturnAllBooks() {
        when(bookRepository.findAll()).thenReturn(books);

        List<Book> result = bookService.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(book.getId(), result.get(0).getId());
    }

    @Test
    void findByCategory_ShouldReturnBooksInCategory() {
        when(bookRepository.findByCategory("Fiction")).thenReturn(books);

        List<Book> result = bookService.findByCategory("Fiction");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Fiction", result.get(0).getCategory());
    }

    @Test
    void searchBooks_ShouldReturnMatchingBooks() {
        when(bookRepository.findByTitleContainingOrAuthorContaining("Test", "Test")).thenReturn(books);

        List<Book> result = bookService.searchBooks("Test");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.get(0).getTitle().contains("Test"));
    }

    @Test
    void findOutOfStockBooks_ShouldReturnOutOfStockBooks() {
        Book outOfStockBook = new Book();
        outOfStockBook.setId(2L);
        outOfStockBook.setStockQuantity(0);
        List<Book> outOfStockBooks = Arrays.asList(outOfStockBook);
        when(bookRepository.findByStockQuantityLessThanEqual(0)).thenReturn(outOfStockBooks);

        List<Book> result = bookService.findOutOfStockBooks();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(0, result.get(0).getStockQuantity());
    }

    @Test
    void updateBook_WhenBookExists_ShouldReturnUpdatedBook() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(bookRepository.save(book)).thenReturn(book);

        Book result = bookService.updateBook(1L, bookRequest);

        assertNotNull(result);
        assertEquals(book.getId(), result.getId());
        verify(bookRepository).save(book);
    }

    @Test
    void updateBook_WhenBookDoesNotExist_ShouldThrowException() {
        when(bookRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(IllegalStateException.class, () -> bookService.updateBook(1L, bookRequest));
        verify(bookRepository, never()).save(any());
    }

    @Test
    void deleteBook_WhenBookExists_ShouldDeleteBook() {
        when(bookRepository.existsById(1L)).thenReturn(true);

        bookService.deleteBook(1L);

        verify(bookRepository).deleteById(1L);
    }

    @Test
    void deleteBook_WhenBookDoesNotExist_ShouldThrowException() {
        when(bookRepository.existsById(1L)).thenReturn(false);

        assertThrows(IllegalStateException.class, () -> bookService.deleteBook(1L));
        verify(bookRepository, never()).deleteById(any());
    }
} 