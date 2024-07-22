package example.mapper;

import example.config.MapperConfig;
import example.dto.bookDtos.BookDto;
import example.dto.bookDtos.CreateBookRequestDto;
import example.model.Book;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class)
public interface BookMapper {
    BookDto toDto(Book book);

    Book toModel(CreateBookRequestDto requestDto);
}
