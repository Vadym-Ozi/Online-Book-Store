package example.repository;

import example.model.Book;
import java.util.List;
import java.util.Optional;

public interface BookRepository {
    Book save(Book book);

    List<Book> getAll();

    Optional<Book> getById(Long id);
}
