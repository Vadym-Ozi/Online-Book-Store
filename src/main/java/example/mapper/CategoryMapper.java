package example.mapper;

import example.config.MapperConfig;
import example.dto.category.CategoryDto;
import example.model.Category;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class)
public interface CategoryMapper {
    CategoryDto toDto(Category category);

    Category toEntity(CategoryDto categoryDTO);
}
