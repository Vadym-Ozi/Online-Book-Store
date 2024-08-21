package example.service.impl;

import example.dto.book.BookDto;
import example.dto.book.BookRequestDto;
import example.dto.book.BookSearchParameters;
import example.dto.book.BookDtoWithoutCategoryIds;
import example.exception.EntityNotFoundException;
import example.mapper.BookMapper;
import example.model.Book;
import example.model.Category;
import example.repository.book.BookRepository;
import example.repository.book.BookSpecificationBuilder;
import example.repository.category.CategoryRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookServiceImplTest {
    @InjectMocks
    private BookServiceImpl bookService;
    @Mock
    private BookRepository bookRepository;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private BookMapper bookMapper;
    @Mock
    private BookSpecificationBuilder bookSpecificationBuilder;
    private List<Book> books;
    private List<BookDto> booksDto;
    private List<BookDtoWithoutCategoryIds> noCategoryDtoList;
    private List<Category> categoryList;
    private List<BookRequestDto> requestList;

    @BeforeEach
    void setUp() {
        requestList = new ArrayList<>();
        noCategoryDtoList = new ArrayList<>();
        categoryList = new ArrayList<>();
        booksDto = new ArrayList<>();
        books = new ArrayList<>();
        createData();
        createBooks();
        createDto();
        createDtoWithNoCategories();
    }

    @Test
    @DisplayName("Verify createBook method works with valid data")
    void createBook_ValidData_BookDto() {
        when(bookMapper.toEntity(any(BookRequestDto.class))).thenReturn(books.get(0));
        when(bookRepository.save(any(Book.class))).thenReturn(books.get(0));
        when(bookMapper.toDto(any(Book.class))).thenReturn(booksDto.get(0));
        when(categoryRepository.findById(any())).thenReturn(Optional.of(categoryList.get(0)));

        BookDto result = bookService.createBook(requestList.get(0));

        verify(bookMapper).toEntity(requestList.get(0));
        verify(bookRepository).save(books.get(0));
        verify(bookMapper).toDto(books.get(0));
        assertEquals(booksDto.get(0), result);
    }

    @Test
    @DisplayName("Create situation when during save we`ve got an exception")
    void createBook_WhenSavingFails_ShouldThrowException() {
        when(bookMapper.toEntity(any(BookRequestDto.class))).thenReturn(books.get(0));
        when(bookRepository.save(any(Book.class))).thenThrow(new RuntimeException("Saving failed"));
        when(categoryRepository.findById(any())).thenReturn(Optional.of(categoryList.get(0)));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            bookService.createBook(requestList.get(0));
        });

        assertEquals("Saving failed", exception.getMessage());
    }

    @Test
    @DisplayName("Create situation when request dto is null")
    void createBook_NullRequestDto_ThrownException() {
        assertThrows(NullPointerException.class, () -> {
            bookService.createBook(null);
        });
    }

    @Test
    @DisplayName("Create a book with no set up categories in it")
    void testCreateBook_SetNoCategories_CreatedWithEmptyCategories() {
        requestList.get(0).setCategoryIds(List.of());
        when(bookMapper.toEntity(requestList.get(0))).thenReturn(books.get(0));
        when(bookRepository.save(any(Book.class))).thenReturn(books.get(0));
        when(bookMapper.toDto(any(Book.class))).thenReturn(booksDto.get(0));

        BookDto result = bookService.createBook(requestList.get(0));

        assertEquals(booksDto.get(0), result);
        assertTrue(books.get(0).getCategories().isEmpty());
        verify(bookMapper).toEntity(requestList.get(0));
        verify(bookRepository).save(books.get(0));
        verify(bookMapper).toDto(books.get(0));
    }

    @Test
    @DisplayName("Fail to create book with non-existent category")
    void testCreateBook_SetWrongIdCategory_ThrownEntityNotFoundException() {
        Book book = books.get(0);
        book.setCategories(null);
        BookRequestDto newRequest = requestList.get(0);
        newRequest.setCategoryIds(List.of(300l));
        when(bookMapper.toEntity(newRequest)).thenReturn(book);
        when(categoryRepository.findById(300L)).thenThrow(new EntityNotFoundException("Can`t find category by id: " + 300L));

        EntityNotFoundException actualException = assertThrows(EntityNotFoundException.class, () -> {
            bookService.createBook(requestList.get(0));
        });

        assertEquals("Can`t find category by id: " + 300L, actualException.getMessage());
        verify(bookMapper).toEntity(requestList.get(0));
    }

    @Test
    @DisplayName("Successfully returns empty list when there is no saved data")
    void testGetAll_EmptyData_EmptyList() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Book> emptyPage = new PageImpl<>(Collections.emptyList());

        when(bookRepository.findAll(pageable)).thenReturn(emptyPage);

        List<BookDto> result = bookService.getAll(pageable);

        assertTrue(result.isEmpty());
        verify(bookRepository).findAll(pageable);
    }

    @Test
    @DisplayName("Get all data on one page were, values should be present")
    void testGetAll_WithPresentData_NonEmptyList() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Book> bookPage = new PageImpl<>(List.of(books.get(0)));

        when(bookRepository.findAll(pageable)).thenReturn(bookPage);
        when(bookMapper.toDto(books.get(0))).thenReturn(booksDto.get(0));

        List<BookDto> result = bookService.getAll(pageable);

        assertEquals(1, result.size());
        assertEquals(booksDto.get(0), result.get(0));
        verify(bookRepository).findAll(pageable);
        verify(bookMapper).toDto(books.get(0));
    }

    @Test
    @DisplayName("Get all items on page two")
    void testGetAll_SelectSecondPage_ListOfTwoElementsOnTheSecondPage() {
        Pageable pageable = PageRequest.of(1, 2);

        Page<Book> bookPage = new PageImpl<>(books.subList(2, 4), pageable, books.size());

        when(bookRepository.findAll(pageable)).thenReturn(bookPage);
        when(bookMapper.toDto(books.get(2))).thenReturn(booksDto.get(2));
        when(bookMapper.toDto(books.get(3))).thenReturn(booksDto.get(3));

        List<BookDto> result = bookService.getAll(pageable);

        assertEquals(2, result.size());
        assertEquals(booksDto.get(2), result.get(0));
        assertEquals(booksDto.get(3), result.get(1));

        verify(bookRepository).findAll(pageable);
        verify(bookMapper).toDto(books.get(2));
        verify(bookMapper).toDto(books.get(3));
    }

    @Test
    @DisplayName("Get book by correct id successfully")
    void testGetById_CorrectId_BookWithRequestedId() {
        Long bookId = 1L;

        when(bookRepository.findById(bookId)).thenReturn(Optional.of(books.get(0)));
        when(bookMapper.toDto(books.get(0))).thenReturn(booksDto.get(0));

        BookDto result = bookService.getById(bookId);

        assertNotNull(result);
        assertEquals(bookId, result.getId());
        verify(bookRepository).findById(bookId);
        verify(bookMapper).toDto(books.get(0));
    }

    @Test
    @DisplayName("Fail to get book with not exist id")
    void testGetById_NotExistID_BookNotFound() {
        Long bookId = 1L;
        String expectedErrorMessage = "Can`t find book by id: " + bookId;

        when(bookRepository.findById(bookId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> bookService.getById(bookId));
        assertEquals(expectedErrorMessage, exception.getMessage());
        verify(bookRepository).findById(bookId);
        verify(bookMapper, never()).toDto(any(Book.class));
    }

    @Test
    @DisplayName("Successfully delete by id")
    void testDeleteById_CorrectId_SuccessDeleted() {
        Long bookId = 1L;

        bookService.deleteById(bookId);

        verify(bookRepository).deleteById(bookId);
    }

    @Test
    @DisplayName("Fail to delete book with not exist id")
    void testDeleteById_NotExistId_BookNotFound() {
        Long bookId = 10000L;

        doThrow(new EntityNotFoundException("Cant find book by id: " + bookId))
                .when(bookRepository).deleteById(bookId);

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> bookService.deleteById(bookId));
        assertEquals("Cant find book by id: " + bookId, exception.getMessage());
        verify(bookRepository).deleteById(bookId);
    }

    @Test
    @DisplayName("Search books by params successfully")
    void testSearch_WithAuthorsAndPrices_SecondPageSortedByAuthorsAndPrices() {
        String[] authors = {"Test Book", "Author2"};
        String[] prices = {"10-20", "20-30"};
        BookSearchParameters params = new BookSearchParameters(authors, prices);
        Pageable pageable = PageRequest.of(0, 2);

        Page<Book> bookPage = new PageImpl<>(books.subList(2, 4), pageable, books.size());

        Specification<Book> bookSpecification = mock(Specification.class);
        when(bookSpecificationBuilder.build(params)).thenReturn(bookSpecification);
        when(bookRepository.findAll(bookSpecification, pageable)).thenReturn(bookPage);

        when(bookMapper.toDto(books.get(2))).thenReturn(booksDto.get(0));
        when(bookMapper.toDto(books.get(3))).thenReturn(booksDto.get(1));

        List<BookDto> result = bookService.search(params, pageable);

        assertEquals(2, result.size());
        assertEquals(booksDto.get(0), result.get(0));
        assertEquals(booksDto.get(1), result.get(1));

        verify(bookSpecificationBuilder).build(params);
        verify(bookRepository).findAll(bookSpecification, pageable);
        verify(bookMapper).toDto(books.get(2));
        verify(bookMapper).toDto(books.get(3));
    }

    @Test
    @DisplayName("Do not enter any sort params as a result all data shown")
    void testSearch_WithEmptyParameters_SecondNotSortedPage() {
        String[] authors = {};
        String[] prices = {};
        BookSearchParameters params = new BookSearchParameters(authors, prices);
        Pageable pageable = PageRequest.of(0, 2);

        Page<Book> bookPage = new PageImpl<>(books.subList(2, 4), pageable, books.size());

        Specification<Book> bookSpecification = mock(Specification.class);
        when(bookSpecificationBuilder.build(params)).thenReturn(bookSpecification);
        when(bookRepository.findAll(bookSpecification, pageable)).thenReturn(bookPage);

        when(bookMapper.toDto(books.get(2))).thenReturn(booksDto.get(0));
        when(bookMapper.toDto(books.get(3))).thenReturn(booksDto.get(1));

        List<BookDto> result = bookService.search(params, pageable);

        assertEquals(2, result.size());
        assertEquals(booksDto.get(0), result.get(0));
        assertEquals(booksDto.get(1), result.get(1));

        verify(bookSpecificationBuilder).build(params);
        verify(bookRepository).findAll(bookSpecification, pageable);
        verify(bookMapper).toDto(books.get(2));
        verify(bookMapper).toDto(books.get(3));
    }

    @Test
    @DisplayName("Test normal work of findAllByCategoryId method with valid data for category ID 1")
    public void testFindAllByCategoryId_ValidData_CategoryId1_ReturnBookDtoWithoutCategoryIds() {
        List<Book> booksWithCategory1 = new ArrayList<>();
        booksWithCategory1.add(books.get(0));
        booksWithCategory1.add(books.get(1));
        when(bookRepository.findAllByCategories_Id(1L)).thenReturn(booksWithCategory1);
        when(bookMapper.toDtoWithoutCategories(books.get(0))).thenReturn(noCategoryDtoList.get(0));
        when(bookMapper.toDtoWithoutCategories(books.get(1))).thenReturn(noCategoryDtoList.get(1));

        List<BookDtoWithoutCategoryIds> actual = bookService.findAllByCategoryId(1L);

        List<BookDtoWithoutCategoryIds> expected = new ArrayList<>();
        expected.add(noCategoryDtoList.get(0));
        expected.add(noCategoryDtoList.get(1));

        assertEquals(expected, actual);

        verify(bookRepository).findAllByCategories_Id(1L);
        verify(bookMapper).toDtoWithoutCategories(books.get(0));
        verify(bookMapper).toDtoWithoutCategories(books.get(1));
    }

    @Test
    @DisplayName("Test normal work of findAllByCategoryId method with valid data for category ID 2")
    public void testFindAllByCategoryId_ValidData_CategoryId2_ReturnBookDtoWithoutCategoryIds() {
        List<Book> booksWithCategory2 = new ArrayList<>();
        booksWithCategory2.add(books.get(2));
        booksWithCategory2.add(books.get(3));
        when(bookRepository.findAllByCategories_Id(2L)).thenReturn(booksWithCategory2);
        when(bookMapper.toDtoWithoutCategories(books.get(2))).thenReturn(noCategoryDtoList.get(2));
        when(bookMapper.toDtoWithoutCategories(books.get(3))).thenReturn(noCategoryDtoList.get(3));

        List<BookDtoWithoutCategoryIds> actual = bookService.findAllByCategoryId(2L);

        List<BookDtoWithoutCategoryIds> expected = new ArrayList<>();
        expected.add(noCategoryDtoList.get(2));
        expected.add(noCategoryDtoList.get(3));

        assertEquals(expected, actual);

        verify(bookRepository).findAllByCategories_Id(2L);
        verify(bookMapper).toDtoWithoutCategories(books.get(2));
        verify(bookMapper).toDtoWithoutCategories(books.get(3));
    }

    @Test
    @DisplayName("Set category which do not have books yet"
            + " as a result no books found")
    public void testFindAllByCategoryId_CategoryWithoutBooks_EmptyList() {
        when(bookRepository.findAllByCategories_Id(1L)).thenReturn(List.of());

        List<BookDtoWithoutCategoryIds> result = bookService.findAllByCategoryId(1L);

        assertEquals(List.of(), result);
        verify(bookRepository).findAllByCategories_Id(1L);
        verifyNoInteractions(bookMapper);
    }

    @Test
    @DisplayName("Update book successfully with all changed data")
    void testUpdateBook_ValidData_SuccessfullyUpdated() {
        Long bookId = 1L;
        Book existingBook = books.get(0);
        BookRequestDto updateRequest = requestList.get(1);

        Book updatedBook = new Book();
        updatedBook.setId(1L);
        updatedBook.setTitle(updateRequest.getTitle());
        updatedBook.setAuthor(updateRequest.getAuthor());
        updatedBook.setPrice(updateRequest.getPrice());
        updatedBook.setIsbn(updateRequest.getIsbn());
        updatedBook.setDescription(updateRequest.getDescription());
        updatedBook.setCoverImage(updateRequest.getCoverImage());
        updatedBook.setCategories(Set.of(categoryList.get(0)));

        BookDto updatedBookDto = new BookDto();
        updatedBookDto.setId(updatedBook.getId());
        updatedBookDto.setTitle(updatedBook.getTitle());
        updatedBookDto.setAuthor(updatedBook.getAuthor());
        updatedBookDto.setPrice(updatedBook.getPrice());
        updatedBookDto.setIsbn(updatedBook.getIsbn());
        updatedBookDto.setDescription(updatedBook.getDescription());
        updatedBookDto.setCoverImage(updatedBook.getCoverImage());
        updatedBookDto.setCategoryIds(updateRequest.getCategoryIds());

        when(bookRepository.findById(bookId)).thenReturn(Optional.of(existingBook));
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(categoryList.get(1)));
        when(bookRepository.save(existingBook)).thenReturn(updatedBook);
        when(bookMapper.toDto(updatedBook)).thenReturn(updatedBookDto);

        BookDto result = bookService.updateBook(bookId, updateRequest);

        assertNotNull(result);
        assertEquals(updatedBookDto.getTitle(), result.getTitle());
        assertEquals(updatedBookDto.getAuthor(), result.getAuthor());
        assertEquals(updatedBookDto.getPrice(), result.getPrice());
        assertEquals(updatedBookDto.getIsbn(), result.getIsbn());
        assertEquals(updatedBookDto.getDescription(), result.getDescription());
        assertEquals(updatedBookDto.getCoverImage(), result.getCoverImage());
        assertEquals(updatedBookDto.getCategoryIds(), result.getCategoryIds());

        verify(bookRepository).findById(bookId);
        verify(bookMapper).updateBookFromDto(updateRequest, existingBook);
        verify(bookRepository).save(existingBook);
        verify(bookMapper).toDto(updatedBook);
    }


    @Test
    @DisplayName("Update book throws exception when book not found")
    void testUpdateBook_NotValidId_BookNotFound() {
        Long bookId = 1L;
        BookRequestDto bookRequestDto = new BookRequestDto();

        when(bookRepository.findById(bookId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            bookService.updateBook(bookId, bookRequestDto);
        });
        assertEquals("Can`t find book by id: " + bookId, exception.getMessage());
        verify(bookRepository).findById(bookId);
        verifyNoMoreInteractions(bookMapper);
        verifyNoMoreInteractions(bookRepository);
    }

    @Test
    @DisplayName("Update book throws exception when category not found")
    void testUpdateBook_NotValidCategoryId_ThrownException() {
        Long bookId = 1L;
        BookRequestDto requestDto = new BookRequestDto();
        requestDto.setCategoryIds(List.of(1L));

        Book existingBook = books.get(0);
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(existingBook));
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            bookService.updateBook(bookId, requestDto);
        });
        assertEquals("Can`t find category by id: " + 1L, exception.getMessage());

        verify(bookRepository).findById(bookId);
        verify(categoryRepository).findById(1L);
    }

    @Test
    @DisplayName("Update book throws exception when save fails")
    void testUpdateBook_UnknownExceptionThrownDueToSaving_ThrownException() {
        Long bookId = 1L;
        BookRequestDto newRequestDto = new BookRequestDto();
        newRequestDto.setTitle("Updated Title");
        newRequestDto.setAuthor("Updated Author");
        newRequestDto.setCategoryIds(List.of(1L));

        Book existingBook = books.get(0);
        Category newCategory = categoryList.get(1);
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(existingBook));
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(newCategory));
        when(bookRepository.save(existingBook)).thenThrow(new RuntimeException("Database error"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            bookService.updateBook(bookId, newRequestDto);
        });
        assertEquals("Database error", exception.getMessage());
        verify(bookRepository).findById(bookId);
        verify(bookMapper).updateBookFromDto(newRequestDto, existingBook);
        verify(bookRepository).save(existingBook);
        verifyNoMoreInteractions(bookMapper);
    }

    private void createData() {
        Category category = new Category();
        category.setId(1L);
        category.setName("Fiction");
        category.setDescription("test desc");
        categoryList.add(category);

        Category category2 = new Category();
        category2.setId(2L);
        category2.setName("Crime");
        category2.setDescription("test desc");
        categoryList.add(category2);

        BookRequestDto requestDto = new BookRequestDto();
        requestDto.setTitle("Test Book");
        requestDto.setAuthor("Test Author");
        requestDto.setPrice(BigDecimal.valueOf(29.9));
        requestDto.setIsbn("1122334455667");
        requestDto.setDescription("test description");
        requestDto.setCoverImage("http//cover.jpg");
        requestDto.setCategoryIds(List.of(categoryList.get(0).getId()));
        requestList.add(requestDto);

        BookRequestDto updateRequest = new BookRequestDto();
        updateRequest.setTitle("Updated Book");
        updateRequest.setAuthor("Updated Author");
        updateRequest.setPrice(BigDecimal.valueOf(2222.9));
        updateRequest.setIsbn("1122334455663");
        updateRequest.setDescription("Updated description");
        updateRequest.setCoverImage("http//Updated.jpg");
        updateRequest.setCategoryIds(List.of(categoryList.get(0).getId()));
        requestList.add(updateRequest);
    }

    private void createBooks() {
        Book book = new Book();
        book.setId(1L);
        book.setTitle(requestList.get(0).getTitle());
        book.setAuthor(requestList.get(0).getAuthor());
        book.setPrice(requestList.get(0).getPrice());
        book.setIsbn(requestList.get(0).getIsbn());
        book.setDescription(requestList.get(0).getDescription());
        book.setCoverImage(requestList.get(0).getCoverImage());
        book.setCategories(Set.of(categoryList.get(0)));
        books.add(book);

        Book book2 = new Book();
        book2.setId(2L);
        book2.setTitle("requestDto.getTitle()");
        book2.setAuthor(requestList.get(0).getAuthor());
        book2.setPrice(BigDecimal.valueOf(10.0));
        book2.setIsbn("requestDto.getIsbn()");
        book2.setDescription("requestDto.getDescription()");
        book2.setCoverImage("requestDto.getCoverImage()");
        book2.setCategories(Set.of(categoryList.get(0)));
        books.add(book2);

        Book book3 = new Book();
        book3.setId(3L);
        book3.setTitle("requestDto");
        book3.setAuthor("requestDto");
        book3.setPrice(BigDecimal.valueOf(10.0));
        book3.setIsbn("requestDto");
        book3.setDescription("requestDto.");
        book3.setCoverImage("requestDto");
        book3.setCategories(Set.of(categoryList.get(1)));
        books.add(book3);

        Book book4 = new Book();
        book4.setId(4L);
        book4.setTitle("Test Book");
        book4.setAuthor("getAuthor()");
        book4.setPrice(requestList.get(0).getPrice());
        book4.setIsbn("getIsbn()");
        book4.setDescription("getDescription()");
        book4.setCoverImage("getCoverImage()");
        book4.setCategories(Set.of(categoryList.get(1)));
        books.add(book4);
    }

    private void createDto() {
        BookDto bookDto = new BookDto();
        bookDto.setId(books.get(0).getId());
        bookDto.setTitle(books.get(0).getTitle());
        bookDto.setAuthor(books.get(0).getAuthor());
        bookDto.setPrice(books.get(0).getPrice());
        bookDto.setIsbn(books.get(0).getIsbn());
        bookDto.setDescription(books.get(0).getDescription());
        bookDto.setCoverImage(books.get(0).getCoverImage());
        bookDto.setCategoryIds(requestList.get(0).getCategoryIds());
        booksDto.add(bookDto);

        BookDto bookDto2 = new BookDto();
        bookDto2.setId(books.get(1).getId());
        bookDto2.setTitle(books.get(1).getTitle());
        bookDto2.setAuthor(books.get(1).getAuthor());
        bookDto2.setPrice(books.get(1).getPrice());
        bookDto2.setIsbn(books.get(1).getIsbn());
        bookDto2.setDescription(books.get(1).getDescription());
        bookDto2.setCoverImage(books.get(1).getCoverImage());
        bookDto2.setCategoryIds(requestList.get(0).getCategoryIds());
        booksDto.add(bookDto2);

        BookDto bookDto3 = new BookDto();
        bookDto3.setId(books.get(2).getId());
        bookDto3.setTitle(books.get(2).getTitle());
        bookDto3.setAuthor(books.get(2).getAuthor());
        bookDto3.setPrice(books.get(2).getPrice());
        bookDto3.setIsbn(books.get(2).getIsbn());
        bookDto3.setDescription(books.get(2).getDescription());
        bookDto3.setCoverImage(books.get(2).getCoverImage());
        bookDto3.setCategoryIds(requestList.get(0).getCategoryIds());
        booksDto.add(bookDto3);

        BookDto bookDto4= new BookDto();
        bookDto4.setId(books.get(3).getId());
        bookDto4.setTitle(books.get(3).getTitle());
        bookDto4.setAuthor(books.get(3).getAuthor());
        bookDto4.setPrice(books.get(3).getPrice());
        bookDto4.setIsbn(books.get(3).getIsbn());
        bookDto4.setDescription(books.get(3).getDescription());
        bookDto4.setCoverImage(books.get(3).getCoverImage());
        bookDto4.setCategoryIds(requestList.get(0).getCategoryIds());
        booksDto.add(bookDto4);
    }

    private void createDtoWithNoCategories() {
        BookDtoWithoutCategoryIds bookDtoWithoutCategoryIds = new BookDtoWithoutCategoryIds();
        bookDtoWithoutCategoryIds.setId(books.get(0).getId());
        bookDtoWithoutCategoryIds.setTitle(books.get(0).getTitle());
        bookDtoWithoutCategoryIds.setAuthor(books.get(0).getAuthor());
        bookDtoWithoutCategoryIds.setPrice(books.get(0).getPrice());
        bookDtoWithoutCategoryIds.setIsbn(books.get(0).getIsbn());
        bookDtoWithoutCategoryIds.setDescription(books.get(0).getDescription());
        bookDtoWithoutCategoryIds.setCoverImage(books.get(0).getCoverImage());
        noCategoryDtoList.add(bookDtoWithoutCategoryIds);

        BookDtoWithoutCategoryIds bookDtoWithoutCategoryIds2 = new BookDtoWithoutCategoryIds();
        bookDtoWithoutCategoryIds2.setId(books.get(1).getId());
        bookDtoWithoutCategoryIds2.setTitle(books.get(1).getTitle());
        bookDtoWithoutCategoryIds2.setAuthor(books.get(1).getAuthor());
        bookDtoWithoutCategoryIds2.setPrice(books.get(1).getPrice());
        bookDtoWithoutCategoryIds2.setIsbn(books.get(1).getIsbn());
        bookDtoWithoutCategoryIds2.setDescription(books.get(1).getDescription());
        bookDtoWithoutCategoryIds2.setCoverImage(books.get(1).getCoverImage());
        noCategoryDtoList.add(bookDtoWithoutCategoryIds2);

        BookDtoWithoutCategoryIds bookDtoWithoutCategoryIds3 = new BookDtoWithoutCategoryIds();
        bookDtoWithoutCategoryIds3.setId(books.get(2).getId());
        bookDtoWithoutCategoryIds3.setTitle(books.get(2).getTitle());
        bookDtoWithoutCategoryIds3.setAuthor(books.get(2).getAuthor());
        bookDtoWithoutCategoryIds3.setPrice(books.get(2).getPrice());
        bookDtoWithoutCategoryIds3.setIsbn(books.get(2).getIsbn());
        bookDtoWithoutCategoryIds3.setDescription(books.get(2).getDescription());
        bookDtoWithoutCategoryIds3.setCoverImage(books.get(2).getCoverImage());
        noCategoryDtoList.add(bookDtoWithoutCategoryIds3);

        BookDtoWithoutCategoryIds bookDtoWithoutCategoryIds4 = new BookDtoWithoutCategoryIds();
        bookDtoWithoutCategoryIds4.setId(books.get(3).getId());
        bookDtoWithoutCategoryIds4.setTitle(books.get(3).getTitle());
        bookDtoWithoutCategoryIds4.setAuthor(books.get(3).getAuthor());
        bookDtoWithoutCategoryIds4.setPrice(books.get(3).getPrice());
        bookDtoWithoutCategoryIds4.setIsbn(books.get(3).getIsbn());
        bookDtoWithoutCategoryIds4.setDescription(books.get(3).getDescription());
        bookDtoWithoutCategoryIds4.setCoverImage(books.get(3).getCoverImage());
        noCategoryDtoList.add(bookDtoWithoutCategoryIds4);
    }
}
