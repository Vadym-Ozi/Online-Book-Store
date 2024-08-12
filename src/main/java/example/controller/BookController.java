package example.controller;

import example.dto.book.BookDto;
import example.dto.book.BookSearchParameters;
import example.dto.book.BookRequestDto;
import example.service.BookService;
import java.util.List;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@OpenAPIDefinition(
        info = @Info(
                title = "Online Book Store API",
                version = "1.0",
                description = "Online Book Store API",
                contact = @Contact(name = "Vadym Pantielieienko",
                        email = "vadympantielieienko@gmail.com")
        )
)
@RestController
@RequiredArgsConstructor
@RequestMapping("/books")
@Tag(name = "Book Management", description = "Endpoints for managing books")
public class BookController {
    private final BookService bookService;

    @PreAuthorize("hasRole('USER')")
    @GetMapping
    @Operation(summary = "Get list of all books", description = "Get list of all books")
    public List<BookDto> getAll(@ParameterObject @PageableDefault Pageable pageable) {
        return bookService.getAll(pageable);
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/{id}")
    @Operation(summary = "Get book by id", description = "Get a single book")
    public BookDto getBookById(@PathVariable @Positive Long id) {
        return bookService.getById(id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    @Operation(summary = "Create a new book", description = "Create a new book")
    public BookDto createBook(@RequestBody @Valid BookRequestDto requestDto) {
        return bookService.createBook(requestDto);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete book by ID",
            description = "Delete existing book by id for user, but do not delete from DB")
    public void deleteBook(@PathVariable @Positive Long id) {
        bookService.deleteById(id);
    }

    @PreAuthorize("hasAnyRole('USER')")
    @GetMapping("/search")
    @Operation(summary = "Search by param",
            description = "Get list of all books sorted by chosen parameter")
    public List<BookDto> search(@Valid BookSearchParameters params,
                                @ParameterObject @PageableDefault Pageable pageable) {
        return bookService.search(params, pageable);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    @Operation(summary = "Update book information",
            description = "Update information about book with chosen id")
    public BookDto updateBook(@PathVariable @Positive Long id,
                                      @RequestBody @Valid BookRequestDto bookRequestDto) {
        return bookService.updateBook(id, bookRequestDto);
    }
}
