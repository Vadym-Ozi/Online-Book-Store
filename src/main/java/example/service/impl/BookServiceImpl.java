package example.service.impl;

import example.dto.book.BookDto;
import example.dto.book.BookSearchParameters;
import example.dto.book.CreateBookRequestDto;
import example.dto.category.BookDtoWithoutCategoryIds;
import example.exception.EntityNotFoundException;
import example.mapper.BookMapper;
import example.model.Book;
import example.model.Category;
import example.repository.book.BookRepository;
import example.repository.book.BookSpecificationBuilder;
import example.repository.category.CategoryRepository;
import example.service.BookService;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {
    private final BookSpecificationBuilder bookSpecificationBuilder;
    private final CategoryRepository categoryRepository;
    private final BookRepository bookRepository;
    private final BookMapper bookMapper;

    @Override
    public BookDto createBook(CreateBookRequestDto requestDto) {
        Book book = bookMapper.toEntity(requestDto);
        List<Long> categoryIds = requestDto.getCategoryIds();
        Set<Category> categories = new HashSet<>();
        for (Long id : categoryIds) {
            Category category = categoryRepository.findById(id).orElseThrow(
                    () -> new EntityNotFoundException("Cant find book by id: " + id)
            );
            categories.add(category);
        }
        book.setCategories(categories);
        return bookMapper.toDto(bookRepository.save(book));
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookDto> getAll(Pageable pageable) {
        return bookRepository.findAll(pageable).stream()
                .map(bookMapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    public BookDto getById(Long id) {
        Book book = bookRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Cant find book by id: " + id));
        return bookMapper.toDto(book);
    }

    @Override
    public void deleteById(Long id) {
        bookRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookDto> search(BookSearchParameters params, Pageable pageable) {
        Specification<Book> bookSpecification = bookSpecificationBuilder.build(params);
        return bookRepository.findAll(bookSpecification, pageable)
                .stream()
                .map(bookMapper::toDto)
                .toList();
    }

    @Override
    public List<BookDtoWithoutCategoryIds> findAllByCategoryId(Long categoryId) {
        return bookRepository.findAllByCategories_Id(categoryId).stream()
                .map(bookMapper::toDtoWithoutCategories)
                .toList();
    }
}
