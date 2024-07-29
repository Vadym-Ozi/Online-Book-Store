package example.mapper;

import example.config.MapperConfig;
import example.dto.book.BookDto;
import example.dto.book.CreateBookRequestDto;
import example.model.Book;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class)
public interface BookMapper {
    BookDto toDto(Book book);

    Book toModel(CreateBookRequestDto requestDto);
}
