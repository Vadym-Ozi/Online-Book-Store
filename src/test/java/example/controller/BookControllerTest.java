package example.controller;

import example.dto.book.BookDto;
import example.dto.book.BookRequestDto;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.test.context.support.WithMockUser;
import org.testcontainers.shaded.org.apache.commons.lang3.builder.EqualsBuilder;
import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BookControllerTest {
    protected static MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    private List<BookDto> dtoList;
    private List<BookRequestDto> requestList;

    @BeforeAll
    static void beforeAll(
            @Autowired WebApplicationContext applicationContext,
            @Autowired DataSource dataSource
            ) throws SQLException {
        mockMvc = MockMvcBuilders.webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .build();
        tearDown(dataSource);
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("database/book/add-books-to-db.sql")
            );
        }
    }

    @BeforeEach
    void setUp() {
        dtoList = new ArrayList<>();
        requestList = new ArrayList<>();
        setRequestList();
        setDtoList();
    }

    @AfterAll
    static void afterAll(@Autowired DataSource dataSource) {
        tearDown(dataSource);
    }

    @SneakyThrows
    static void tearDown(DataSource dataSource) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("database/book/drop-books-categories-table.sql")
            );
        }
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("database/book/delete-books.sql")
            );
        }
    }

@Test
@WithMockUser(username = "admin", roles = "ADMIN")
@DisplayName("Test valid data sent to create book with 200 status code")
@Sql(scripts = "classpath:database/book/delete-single-book.sql",
        executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
void createBook_ValidData_OkStatusCode() throws Exception {
    BookRequestDto requestDto = requestList.get(0);
    BookDto expected = dtoList.get(0);
    String jsonRequest = objectMapper.writeValueAsString(requestDto);

    MvcResult result = mockMvc.perform(
                    post("/books")
                            .content(jsonRequest)
                            .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isOk())
            .andReturn();

    BookDto actual = objectMapper.readValue(result.getResponse().getContentAsString(), BookDto.class);
    EqualsBuilder.reflectionEquals(expected, actual, "id", "isDeleted");
}

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    @DisplayName("Test sending invalid data to create book results in 400 Bad Request")
    @Sql(scripts = "classpath:database/book/delete-single-book.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void createBook_InvalidRequest_BadRequest() throws Exception {
        BookRequestDto requestDto = new BookRequestDto();
        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        MvcResult result = mockMvc.perform(
                        post("/books")
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andReturn();

        int actualStatusCode = result.getResponse().getStatus();

        assertEquals(400, actualStatusCode);
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    @DisplayName("Test forbidden access when user does not have ADMIN role")
    @Sql(scripts = "classpath:database/book/delete-single-book.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void createBook_ForbiddenAccess_StatusForbidden() throws Exception {
        BookRequestDto requestDto = requestList.get(0);
        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        mockMvc.perform(
                        post("/books")
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Test unauthorized access when user is not authenticated")
    @Sql(scripts = "classpath:database/book/delete-single-book.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void createBook_UnauthorizedAccess_StatusForbidden() throws Exception {
        BookRequestDto requestDto = requestList.get(0);
        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        mockMvc.perform(
                        post("/books")
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    @DisplayName("Test successful retrieval of all books with pagination")
    void getAllBooks_Success_StatusOk() throws Exception {
        MvcResult result = mockMvc.perform(get("/books")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        String jsonResponse = result.getResponse().getContentAsString();
        List<BookDto> books = objectMapper.readValue(jsonResponse, new TypeReference<>() {});

        assertEquals(6, books.size());
    }

    @Test
    @DisplayName("Test unauthorized access when user is not authenticated")
    void getAllBooks_UnauthorizedAccess_StatusForbidden() throws Exception {
        mockMvc.perform(get("/books")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    @DisplayName("Test pagination when retrieving all books on the correct page")
    void getAllBooks_Pagination_CorrectPage_SecondPageWithOneBook() throws Exception {
        MvcResult result = mockMvc.perform(get("/books")
                        .param("page", "1")
                        .param("size", "5")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        BookDto expected = new BookDto();
        expected.setTitle("GTA 5");
        BookDto[] actual = objectMapper.readValue(result.getResponse().getContentAsByteArray(), BookDto[].class);
        String actualTitle = Arrays.stream(actual).findFirst().map(BookDto::getTitle).orElseThrow();

        assertEquals(1, actual.length);
        assertEquals(expected.getTitle(), actualTitle);
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    @DisplayName("Test getting a book by ID successfully")
    void testGetBookById_ValidID_Success() throws Exception {
        Long bookId = 10L;
        BookDto expectedBook = new BookDto();
        expectedBook.setId(bookId);
        expectedBook.setTitle("Witcher");

        MvcResult result = mockMvc.perform(get("/books/{id}", bookId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        BookDto actualBook = objectMapper.readValue(result.getResponse().getContentAsString(), BookDto.class);
        assertEquals(expectedBook.getTitle(), actualBook.getTitle());
        assertEquals(expectedBook.getId(), actualBook.getId());
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    @DisplayName("Test getting a book by ID that does not exist")
    void testGetBookById_NotValidID_NotFound() throws Exception {
        Long nonExistentBookId = 999L;

        mockMvc.perform(get("/books/{id}", nonExistentBookId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    @DisplayName("Test successful deletion of a book with status OK")
    @Sql(scripts = "classpath:database/book/add-one-book.sql",
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void deleteBook_ValidID_Success() throws Exception {
        Long bookId = 16L;

        mockMvc.perform(delete("/books/{id}", bookId))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    @DisplayName("Test deleting a book with a non-existent ID returns 404")
    void deleteBook_NonExistID_NotFound() throws Exception {
        Long bookId = 999L;

        mockMvc.perform(delete("/books/{id}", bookId))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    @DisplayName("Test user without ADMIN role cannot delete a book")
    void deleteBook_UserRole_Forbidden() throws Exception {
        Long bookId = 10L;

        mockMvc.perform(delete("/books/{id}", bookId))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    @DisplayName("Test successful search returns list of books with status 200")
    void search_Success() throws Exception {
        List<BookDto> expected = List.of(dtoList.get(3), dtoList.get(4));

        MvcResult result = mockMvc.perform(get("/books/search")

                        .param("page", "0")
                        .param("size", "3")

                        .param("prices", "10"))
                .andExpect(status().isOk())
                .andReturn();

        BookDto[] actual = objectMapper.readValue(result.getResponse().getContentAsString(), BookDto[].class);
        assertEquals(expected.size(), actual.length);
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    @DisplayName("Test search with invalid parameters and return empty list")
    void search_InvalidParameters_EmptyList() throws Exception {
        String [] authors = new String[] {"aaaaaaa"};
        String [] prices = new String[] {"0000"};

        MvcResult result = mockMvc.perform(get("/books/search")
                        .param("authors", authors)
                        .param("prices", prices))
                .andExpect(status().isOk())
                .andReturn();

        BookDto[] actual = objectMapper.readValue(result.getResponse().getContentAsString(), BookDto[].class);
        assertEquals(0, actual.length);
    }

    @Test
    @WithMockUser(username = "guest", roles = "GUEST")
    @DisplayName("Test search without appropriate role returns 403 Forbidden")
    void search_NoRole_Forbidden() throws Exception {
        mockMvc.perform(get("/books/search")
                        .param("title", "Sample Title"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    @DisplayName("Test successful update book returns status 200")
    @Sql(scripts = "classpath:database/book/add-book-for-update.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/book/delete-book-after-update.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void updateBook_ValidData_StatusCodeOk() throws Exception {
        Long bookId = 17L;
        BookRequestDto requestDto = requestList.get(2);
        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        mockMvc.perform(put("/books/{id}", bookId)
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Title"))
                .andExpect(jsonPath("$.author").value("Updated Author"));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    @DisplayName("Test update non-existing book returns 404 Not Found")
    void updateBook_NotValidId_NotFound() throws Exception {
        Long bookId = 999L;
        BookRequestDto requestDto = requestList.get(2);
        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        mockMvc.perform(put("/books/{id}", bookId)
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Test update book without authentication returns Forbidden status")
    void updateBook_NoAuthorizedUser_StatusCodeForbidden() throws Exception {
        Long bookId = 10L;
        BookRequestDto requestDto = requestList.get(2);
        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        mockMvc.perform(put("/books/{id}", bookId)
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    @DisplayName("Test update book without ADMIN role returns 403 Forbidden")
    void updateBook_UserAuthorization_Forbidden() throws Exception {
        Long bookId = 1L;
        BookRequestDto requestDto = requestList.get(2);
        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        mockMvc.perform(put("/books/{id}", bookId)
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    private void setRequestList() {
        BookRequestDto requestDto = new BookRequestDto();
        requestDto.setTitle("Witcher 0");
        requestDto.setAuthor("me");
        requestDto.setPrice(BigDecimal.valueOf(10.0));
        requestDto.setIsbn("1122334455664");
        requestDto.setDescription("test");
        requestDto.setCoverImage("test.img");
        requestDto.setCategoryIds(List.of());
        requestList.add(requestDto);

        BookRequestDto requestDto2 = new BookRequestDto();
        requestDto2.setTitle("Witcher 1");
        requestDto2.setAuthor("me");
        requestDto2.setPrice(BigDecimal.valueOf(10.0));
        requestDto2.setIsbn("1122334455668");
        requestDto2.setDescription("test");
        requestDto2.setCoverImage("test.img");
        requestDto2.setCategoryIds(List.of());
        requestList.add(requestDto2);

        BookRequestDto updateRequest = new BookRequestDto();
        updateRequest.setTitle("Updated Title");
        updateRequest.setAuthor("Updated Author");
        updateRequest.setPrice(BigDecimal.valueOf(2222.9));
        updateRequest.setIsbn("1122334455000");
        updateRequest.setDescription("Updated description");
        updateRequest.setCoverImage("http//Updated.jpg");
        updateRequest.setCategoryIds(List.of());
        requestList.add(updateRequest);
    }

    private void setDtoList() {
        BookDto bookDto = new BookDto();
        bookDto.setId(10L);
        bookDto.setTitle(requestList.get(0).getTitle());
        bookDto.setAuthor(requestList.get(0).getAuthor());
        bookDto.setPrice(requestList.get(0).getPrice());
        bookDto.setIsbn(requestList.get(0).getIsbn());
        bookDto.setDescription(requestList.get(0).getDescription());
        bookDto.setCoverImage(requestList.get(0).getCoverImage());
        bookDto.setCategoryIds(requestList.get(0).getCategoryIds());
        dtoList.add(bookDto);

        BookDto bookDto2 = new BookDto();
        bookDto2.setId(11L);
        bookDto2.setTitle("Witcher 1");
        bookDto2.setAuthor("me");
        bookDto2.setPrice(BigDecimal.valueOf(10.0));
        bookDto2.setIsbn("1122334455668");
        bookDto2.setDescription("test");
        bookDto2.setCoverImage("test.img");
        bookDto2.setCategoryIds(List.of());
        dtoList.add(bookDto2);

        BookDto updateDto = new BookDto();
        updateDto.setId(17L);
        updateDto.setTitle(requestList.get(2).getTitle());
        updateDto.setAuthor(requestList.get(2).getAuthor());
        updateDto.setPrice(requestList.get(2).getPrice());
        updateDto.setIsbn(requestList.get(2).getIsbn());
        updateDto.setDescription(requestList.get(2).getDescription());
        updateDto.setCoverImage(requestList.get(2).getCoverImage());
        updateDto.setCategoryIds(requestList.get(2).getCategoryIds());
        dtoList.add(updateDto);

        BookDto bookDto3 = new BookDto();
        bookDto3.setId(10L);
        bookDto3.setTitle("Witcher");
        bookDto3.setAuthor("me");
        bookDto3.setPrice(BigDecimal.valueOf(10.0));
        bookDto3.setIsbn("1122334455667");
        bookDto3.setDescription("test");
        bookDto3.setCoverImage("test.img");
        bookDto3.setCategoryIds(List.of());
        dtoList.add(bookDto3);

        BookDto bookDto4 = new BookDto();
        bookDto4.setId(11L);
        bookDto4.setTitle("Witcher 1");
        bookDto4.setAuthor("me");
        bookDto4.setPrice(BigDecimal.valueOf(10.0));
        bookDto4.setIsbn("1122334455668");
        bookDto4.setDescription("test");
        bookDto4.setCoverImage("test.img");
        bookDto4.setCategoryIds(List.of());
        dtoList.add(bookDto4);
    }
}
