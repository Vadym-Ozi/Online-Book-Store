package example.mapper;

import example.config.MapperConfig;
import example.dto.category.CategoryDto;
import example.dto.category.CategoryRequestDto;
import example.model.Category;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(config = MapperConfig.class)
public interface CategoryMapper {
    CategoryDto toDto(Category category);

    Category toEntity(CategoryRequestDto categoryDTO);

    void updateCategoryFromDto(CategoryRequestDto categoryRequestDto, @MappingTarget Category category);
}
