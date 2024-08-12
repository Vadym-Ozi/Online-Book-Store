package example.service;

import example.dto.book.BookDto;
import example.dto.book.BookRequestDto;
import example.dto.book.BookSearchParameters;
import example.dto.category.BookDtoWithoutCategoryIds;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface BookService {
    BookDto createBook(BookRequestDto requestDtoDto);

    List<BookDto> getAll(Pageable pageable);

    BookDto getById(Long id);

    void deleteById(Long id);

    List<BookDto> search(BookSearchParameters params, Pageable pageable);

    List<BookDtoWithoutCategoryIds> findAllByCategoryId(Long categoryId);

    BookDto updateBook(Long id, BookRequestDto bookRequestDto);
}
