package example.service;

import example.dto.bookDtos.BookDto;
import example.dto.bookDtos.BookSearchParameters;
import example.dto.bookDtos.CreateBookRequestDto;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface BookService {
    BookDto createBook(CreateBookRequestDto requestDtoDto);

    List<BookDto> getAll(Pageable pageable);

    BookDto getById(Long id);

    void deleteById(Long id);

    List<BookDto> search(BookSearchParameters params, Pageable pageable);
}
