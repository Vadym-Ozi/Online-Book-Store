package example.service;

import example.dto.BookDto;
import example.dto.CreateBookRequestDto;
import java.util.List;

public interface BookService {
    BookDto createBook(CreateBookRequestDto requestDtoDto);

    List<BookDto> getAll();

    BookDto getById(Long id);

    void deleteById(Long id);
}
