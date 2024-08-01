package example.mapper;

import example.config.MapperConfig;
import example.dto.book.BookDto;
import example.dto.book.CreateBookRequestDto;
import example.dto.category.BookDtoWithoutCategoryIds;
import example.model.Book;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(config = MapperConfig.class)
public interface BookMapper {
    BookDto toDto(Book book);

    Book toModel(CreateBookRequestDto requestDto);

    BookDtoWithoutCategoryIds toDtoWithoutCategories(Book book);

    @AfterMapping
    default void setCategoryIds(@MappingTarget BookDto bookDto, Book book){
        book.setCategories(bookDto.get);
    }
}
