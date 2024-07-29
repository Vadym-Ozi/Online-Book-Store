package example.service;

import example.dto.book.BookDto;
import example.dto.book.BookSearchParameters;
import example.dto.book.CreateBookRequestDto;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface BookService {
    BookDto createBook(CreateBookRequestDto requestDtoDto);

    List<BookDto> getAll(Pageable pageable);

    BookDto getById(Long id);

    void deleteById(Long id);

    List<BookDto> search(BookSearchParameters params, Pageable pageable);
}
