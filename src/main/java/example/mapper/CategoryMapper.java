package example.mapper;

import example.config.MapperConfig;
import example.dto.category.CategoryDto;
import example.dto.category.CategoryRequestDto;
import example.model.Category;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(config = MapperConfig.class)
public interface CategoryMapper {
    CategoryDto toDto(Category category);

    Category toEntity(CategoryDto categoryDTO);

    @AfterMapping
    default void updateCategoryFromDto(@MappingTarget CategoryRequestDto categoryRequestDto, Category category) {
        category.setName(categoryRequestDto.getName());
        category.setDescription(categoryRequestDto.getDescription());
    }
}
