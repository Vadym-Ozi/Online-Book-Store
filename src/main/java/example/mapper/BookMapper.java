package example.mapper;

import example.config.MapperConfig;
import example.dto.book.BookDto;
import example.dto.book.CreateBookRequestDto;
import example.dto.category.BookDtoWithoutCategoryIds;
import example.model.Book;
import example.model.Category;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.springframework.data.jpa.repository.EntityGraph;
import java.util.List;

@Mapper(config = MapperConfig.class)
public interface BookMapper {
    BookDto toDto(Book book);

    Book toEntity(CreateBookRequestDto requestDto);

    BookDtoWithoutCategoryIds toDtoWithoutCategories(Book book);

    @AfterMapping
    @EntityGraph(attributePaths = "categories")
    default void setCategoryIds(@MappingTarget BookDto bookDto, Book book) {
        List<Long> categoryIds = book.getCategories().stream()
                .map(Category::getId)
                .toList();
        bookDto.setCategoryIds(categoryIds);
    }
}
