package example.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import example.dto.category.CategoryDto;
import example.dto.category.CategoryRequestDto;
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
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.shaded.org.apache.commons.lang3.builder.EqualsBuilder;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CategoryControllerTest {
    protected static MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    private List<CategoryRequestDto> requestList;
    private List<CategoryDto> dtoList;

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
                    new ClassPathResource("database/category/add-categories-to-db.sql")
            );
        }
    }

    @BeforeEach
    void setUp() {
        requestList = new ArrayList<>();
        dtoList = new ArrayList<>();
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
                    new ClassPathResource("database/category/delete-categories.sql")
            );
        }
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    @DisplayName("Test valid data sent to create category return status code is 200")
    @Sql(scripts = "classpath:database/category/delete-one-category.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void createCategory_ValidData_OkStatusCode() throws Exception {
        CategoryRequestDto requestDto = requestList.get(0);
        CategoryDto expected = dtoList.get(0);

        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        MvcResult result = mockMvc.perform(
                        post("/categories")
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        CategoryDto actual = objectMapper.readValue(result.getResponse().getContentAsString(), CategoryDto.class);
        EqualsBuilder.reflectionEquals(expected, actual, "id", "isDeleted");
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    @DisplayName("Test sending invalid data to create category results in 400 Bad Request")
    @Sql(scripts = "classpath:database/category/delete-one-category.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void createCategory_InvalidRequest_BadRequest() throws Exception {
        CategoryRequestDto requestDto = new CategoryRequestDto();
        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        mockMvc.perform(
                post("/categories")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    @DisplayName("Test forbidden access when user does not have USER role")
    @Sql(scripts = "classpath:database/category/delete-one-category.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void createCategory_ForbiddenAccess_StatusForbidden() throws Exception {
        CategoryRequestDto requestDto = requestList.get(0);
        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        mockMvc.perform(
                        post("/categories")
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Test unauthorized access when user is not authenticated")
    @Sql(scripts = "classpath:database/category/delete-one-category.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void createCategory_UnauthorizedAccess_StatusForbidden() throws Exception {
        CategoryRequestDto requestDto = requestList.get(0);
        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        mockMvc.perform(
                        post("/categories")
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    @DisplayName("Test successful retrieval of all categories")
    void getAllCategories_Success_StatusOk() throws Exception {
        MvcResult result = mockMvc.perform(get("/categories")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        String jsonResponse = result.getResponse().getContentAsString();
        List<CategoryDto> categoryList = objectMapper.readValue(jsonResponse, new TypeReference<>() {
        });

        assertEquals(5, categoryList.size());
    }

    @Test
    @DisplayName("Test unauthorized access when user is not authenticated")
    void getAllCategories_UnauthorizedAccess_StatusForbidden() throws Exception {
        mockMvc.perform(get("/categories")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    @DisplayName("Test pagination when retrieving all categories on the correct page")
    void getAllCategories_Pagination_CorrectPage_SecondPageShown() throws Exception {
        MvcResult result = mockMvc.perform(get("/categories")
                        .param("page", "1")
                        .param("size", "2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        CategoryDto expected = new CategoryDto();
        expected.setName("Trololooooo");
        CategoryDto[] actual = objectMapper.readValue(result.getResponse().getContentAsByteArray(), CategoryDto[].class);
        String actualName = Arrays.stream(actual).findFirst().map(CategoryDto::getName).orElseThrow();

        assertEquals(2, actual.length);
        assertEquals(expected.getName(), actualName);
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    @DisplayName("Test getting a category by ID successfully")
    void testGetCategoryById_ValidID_Success() throws Exception {
        Long bookId = 10L;
        CategoryDto expectedCategory = new CategoryDto();
        expectedCategory.setId(bookId);
        expectedCategory.setName("Comedy");

        MvcResult result = mockMvc.perform(get("/categories/{id}", bookId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        CategoryDto actualCategory = objectMapper.readValue(result.getResponse().getContentAsString(), CategoryDto.class);
        assertEquals(expectedCategory.getName(), actualCategory.getName());
        assertEquals(expectedCategory.getId(), actualCategory.getId());
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    @DisplayName("Test getting a category by ID that does not exist")
    void testGetCategoryById_NotValidID_NotFound() throws Exception {
        Long nonExistentCategoryId = 999L;

        mockMvc.perform(get("/categories/{id}", nonExistentCategoryId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    @DisplayName("Test successful deletion of a category with status OK")
    @Sql(scripts = "classpath:database/category/add-one-category.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void deleteCategory_ValidID_Success() throws Exception {
        Long categoryId = 20L;

        mockMvc.perform(delete("/categories/{id}", categoryId))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    @DisplayName("Test deleting a category with a non-existent ID returns 404")
    void deleteCategory_NonExistID_NotFound() throws Exception {
        Long categoryId = 999L;

        mockMvc.perform(delete("/categories/{id}", categoryId))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    @DisplayName("Test user without ADMIN role cannot delete a category")
    void deleteCategory_UserRole_Forbidden() throws Exception {
        Long categoryId = 10L;

        mockMvc.perform(delete("/categories/{id}", categoryId))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    @DisplayName("Test successful update category returns status 200")
    @Sql(scripts = "classpath:database/category/add-one-category.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/category/delete-updated-category.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void updateCategory_ValidData_StatusCodeOk() throws Exception {
        Long categoryId = 20L;
        CategoryRequestDto requestDto = requestList.get(2);
        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        mockMvc.perform(put("/categories/{id}", categoryId)
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated name"))
                .andExpect(jsonPath("$.description").value("Updated desc"));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    @DisplayName("Test update non-existing category returns 404 Not Found")
    void updateCategory_NotValidId_NotFound() throws Exception {
        Long categoryId = 999L;
        CategoryRequestDto requestDto = requestList.get(2);
        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        mockMvc.perform(put("/categories/{id}", categoryId)
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Test update category without authentication should return 401 Unauthorized")
    void updateCategory_NoAuthorizedUser_StatusCodeForbidden() throws Exception {
        Long categoryId = 10L;
        CategoryRequestDto requestDto = requestList.get(2);
        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        mockMvc.perform(put("/categories/{id}", categoryId)
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    @DisplayName("Test update category without ADMIN role returns 403 Forbidden")
    void updateCategory_UserAuthorization_Forbidden() throws Exception {
        Long categoryId = 1L;
        CategoryRequestDto requestDto = requestList.get(2);
        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        mockMvc.perform(put("/categories/{id}", categoryId)
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    @DisplayName("Test getting books by category id with valid role")
    @Sql(scripts = {
            "classpath:database/book/add-books-to-db.sql",
            "classpath:database/category/set-category-to-book.sql",
    },
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            "classpath:database/book/drop-books-categories-table.sql",
            "classpath:database/book/delete-books.sql"
    },
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void testGetBooksByCategoryId_WithValidRole_TwoBooksReturn() throws Exception {
        Long categoryId = 10L;

        mockMvc.perform(get("/categories/{id}/books", categoryId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].title").value("Witcher"))
                .andExpect(jsonPath("$[1].title").value("Witcher 1"));
    }

    @Test
    @DisplayName("Test accessing get books by category id without authentication")
    void testGetBooksByCategoryId_WithoutAuthentication_StatusForbidden() throws Exception {
        Long categoryId = 10L;

        mockMvc.perform(get("/categories/{id}/books", categoryId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    @DisplayName("Test accessing get books by category id with invalid category")
    void testGetBooksByCategoryId_WithInvalidCategory_EmptyList() throws Exception {
        Long invalidCategoryId = 999L;

        mockMvc.perform(get("/categories/{id}/books", invalidCategoryId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    private void setRequestList() {
        CategoryRequestDto requestDto = new CategoryRequestDto();
        requestDto.setName("Test test");
        requestDto.setDescription("testtest test test");
        requestList.add(requestDto);

        CategoryRequestDto invalidRequest = new CategoryRequestDto();
        invalidRequest.setName("Te");
        invalidRequest.setDescription("testtest test test2");
        requestList.add(invalidRequest);

        CategoryRequestDto updateRequest = new CategoryRequestDto();
        updateRequest.setName("Updated name");
        updateRequest.setDescription("Updated desc");
        requestList.add(updateRequest);
    }

    private void setDtoList() {
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setId(16L);
        categoryDto.setName(requestList.get(0).getName());
        categoryDto.setDescription(requestList.get(0).getDescription());
        dtoList.add(categoryDto);

        CategoryDto categoryDto2 = new CategoryDto();
        categoryDto2.setId(17L);
        categoryDto2.setName(requestList.get(1).getName());
        categoryDto2.setDescription(requestList.get(1).getDescription());
        dtoList.add(categoryDto2);

        CategoryDto updateDto = new CategoryDto();
        updateDto.setId(20L);
        updateDto.setName(requestList.get(2).getName());
        updateDto.setDescription(requestList.get(2).getDescription());
        dtoList.add(updateDto);
    }
}
