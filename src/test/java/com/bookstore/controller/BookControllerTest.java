package com.bookstore.controller;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.bookstore.dto.book.BookRequest;
import com.bookstore.dto.book.BookResponse;
import com.bookstore.mapper.BookMapper;
import com.bookstore.model.Book;
import com.bookstore.service.BookService;

@ExtendWith(MockitoExtension.class)
class BookControllerTest {

    @Mock
    private BookService bookService;

    @Mock
    private BookMapper bookMapper;

    @InjectMocks
    private BookController bookController;

    private Book book;
    private BookRequest bookRequest;
    private BookResponse bookResponse;
    private List<Book> books;
    private List<BookResponse> bookResponses;

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

        bookResponse = new BookResponse();
        bookResponse.setId(1L);
        bookResponse.setTitle("Test Book");
        bookResponse.setAuthor("Test Author");
        bookResponse.setPrice(BigDecimal.valueOf(29.99));
        bookResponse.setCategory("Fiction");
        bookResponse.setStockQuantity(10);
        bookResponse.setFormat("PHYSICAL");
        bookResponse.setDescription("Test Description");
        bookResponse.setAverageRating(4.5);
        bookResponse.setReviewCount(10);
        bookResponse.setInStock(true);

        books = Arrays.asList(book);
        bookResponses = Arrays.asList(bookResponse);
    }

    @Test
    void getAllBooks_ShouldReturnAllBooks() {
        when(bookService.findAll()).thenReturn(books);
        when(bookMapper.toResponse(books)).thenReturn(bookResponses);

        ResponseEntity<List<BookResponse>> response = bookController.getAllBooks();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<BookResponse> body = response.getBody();
        assertNotNull(body);
        assertEquals(1, body.size());
        assertEquals(book.getId(), body.get(0).getId());
    }

    @Test
    void getBookById_WhenBookExists_ShouldReturnBook() {
        when(bookService.findById(1L)).thenReturn(Optional.of(book));
        when(bookMapper.toResponse(book)).thenReturn(bookResponse);

        ResponseEntity<BookResponse> response = bookController.getBookById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        BookResponse body = response.getBody();
        assertNotNull(body);
        assertEquals(book.getId(), body.getId());
    }

    @Test
    void getBookById_WhenBookDoesNotExist_ShouldReturnNotFound() {
        when(bookService.findById(1L)).thenReturn(Optional.empty());

        ResponseEntity<BookResponse> response = bookController.getBookById(1L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void searchBooks_ShouldReturnMatchingBooks() {
        when(bookService.searchBooks("Test")).thenReturn(books);
        when(bookMapper.toResponse(books)).thenReturn(bookResponses);

        ResponseEntity<List<BookResponse>> response = bookController.searchBooks("Test");
        List<BookResponse> body = response.getBody();
        assertNotNull(body);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, body.size());
        assertTrue(body.get(0).getTitle().contains("Test"));
    }

    @Test
    void getBooksByCategory_ShouldReturnBooksInCategory() {
        when(bookService.findByCategory("Fiction")).thenReturn(books);
        when(bookMapper.toResponse(books)).thenReturn(bookResponses);

        ResponseEntity<List<BookResponse>> response = bookController.getBooksByCategory("Fiction");
        List<BookResponse> body = response.getBody();
        assertNotNull(body);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, body.size());
        assertEquals("Fiction", body.get(0).getCategory());
    }

    @Test
    void getOutOfStockBooks_ShouldReturnOutOfStockBooks() {
        Book outOfStockBook = new Book();
        outOfStockBook.setId(2L);
        outOfStockBook.setStockQuantity(0);
        List<Book> outOfStockBooks = Arrays.asList(outOfStockBook);
        when(bookService.findOutOfStockBooks()).thenReturn(outOfStockBooks);
        when(bookMapper.toResponse(outOfStockBooks)).thenReturn(Arrays.asList(bookResponse));

        ResponseEntity<List<BookResponse>> response = bookController.getOutOfStockBooks();
        List<BookResponse> body = response.getBody();
        assertNotNull(body);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, body.size());
        assertEquals(0, body.get(0).getStockQuantity());
    }

    @Test
    void createBook_ShouldReturnCreatedBook() {
        when(bookService.createBook(bookRequest)).thenReturn(book);
        when(bookMapper.toResponse(book)).thenReturn(bookResponse);

        ResponseEntity<BookResponse> response = bookController.createBook(bookRequest);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        BookResponse body = response.getBody();
        assertNotNull(body);
        assertEquals(book.getId(), body.getId());
    }

    @Test
    void updateBook_WhenBookExists_ShouldReturnUpdatedBook() {
        when(bookService.updateBook(1L, bookRequest)).thenReturn(book);
        when(bookMapper.toResponse(book)).thenReturn(bookResponse);

        ResponseEntity<BookResponse> response = bookController.updateBook(1L, bookRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        BookResponse body = response.getBody();
        assertNotNull(body);
        assertEquals(book.getId(), body.getId());
    }

    @Test
    void deleteBook_WhenBookExists_ShouldReturnNoContent() {
        doNothing().when(bookService).deleteBook(1L);

        ResponseEntity<Void> response = bookController.deleteBook(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(bookService).deleteBook(1L);
    }

    @Test
    void updateStock_WhenBookExists_ShouldReturnUpdatedBook() {
        when(bookService.updateStock(1L, 5)).thenReturn(book);

        ResponseEntity<BookResponse> response = bookController.updateBookStock(1L, 5);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        BookResponse body = response.getBody();
        assertNotNull(body);
        assertEquals(book.getId(), body.getId());
    }
} 