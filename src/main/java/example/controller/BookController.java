package example.controller;

import example.dto.BookDto;
import example.dto.BookSearchParameters;
import example.dto.CreateBookRequestDto;
import example.service.BookService;
import java.util.List;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
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
                contact = @Contact(name = "Vadym Pantielieienko", email = "vadympantielieienko@gmail.com")
        )
)
@RestController
@RequiredArgsConstructor
@RequestMapping("/books")
@Tag(name = "Book Management", description = "Endpoints for managing books")
public class BookController {
    private final BookService bookService;

    @GetMapping
    @Operation(summary = "Get list of all books", description = "Get list of all books")
    public List<BookDto> getAll(Pageable pageable) {
        return bookService.getAll(pageable);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get book by id", description = "Get a single book")
    public BookDto getBookById(@PathVariable Long id) {
        return bookService.getById(id);
    }

    @PostMapping
    @Operation(summary = "Create a new book", description = "Create a new book")
    public BookDto createBook(@RequestBody @Valid CreateBookRequestDto requestDto) {
        return bookService.createBook(requestDto);
    }

    @Hidden
    @PutMapping("/{id}")
    public void deleteBook(@PathVariable Long id) {
        bookService.deleteById(id);
    }


    @GetMapping("/search")
    @Operation(summary = "Search by param", description = "Get list of all books sorted by chosen parameter")
    public List<BookDto> search(BookSearchParameters params, Pageable pageable) {
        return bookService.search(params, pageable);
    }
}
