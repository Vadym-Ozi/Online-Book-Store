package example.repository.book;

import example.model.Book;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;
import java.util.List;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class BookRepositoryTest {
    @Autowired
    private BookRepository bookRepository;

    @Test
    @Sql(scripts = {
            "classpath:database/book/add-books-to-db.sql",
            "classpath:database/category/add-categories-to-db.sql",
            "classpath:database/category/set-category-to-book.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            "classpath:database/book/drop-books-categories-table.sql",
            "classpath:database/book/delete-books.sql",
            "classpath:database/category/delete-categories.sql"
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @DisplayName("Get present books with valid category id")
    void findAllByCategories_Id() {
        List<Book> actualResult = bookRepository.findAllByCategories_Id(10L);

        Assertions.assertEquals(2, actualResult.size());
        Assertions.assertEquals("Witcher", actualResult.get(0).getTitle());
        Assertions.assertEquals("Witcher 1", actualResult.get(1).getTitle());
    }

    @Test
    @DisplayName("Return empty list when no books are found for a category ID")
    void findAllBooksByCategoryIdWhenNoneExist() {
        Long categoryId = 999L;

        List<Book> actualResult = bookRepository.findAllByCategories_Id(categoryId);

        Assertions.assertEquals(0, actualResult.size());
    }
}
