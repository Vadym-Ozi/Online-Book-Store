package example.service.impl;

import example.dto.BookDto;
import example.dto.CreateBookRequestDto;
import example.exception.EntityNotFoundException;
import example.mapper.BookMapper;
import example.model.Book;
import example.repository.BookRepository;
import example.service.BookService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository;
    private final BookMapper bookMapper;

    @Override
    public BookDto createBook(CreateBookRequestDto requestDto) {
        Book book = bookMapper.toModel(requestDto);
        return bookMapper.toDto(bookRepository.save(book));
    }

    @Override
    public List<BookDto> getAll() {
        return bookRepository.getAll().stream()
                .map(bookMapper::toDto)
                .toList();
    }

    @Override
    public BookDto getById(Long id) {
        Book book = bookRepository.getById(id).orElseThrow(
                () -> new EntityNotFoundException("Cant find book by id: " + id));
        return bookMapper.toDto(book);
    }
}
