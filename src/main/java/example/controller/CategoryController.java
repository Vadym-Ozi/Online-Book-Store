package example.controller;

import example.dto.category.CategoryDto;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/filter")
@Tag(name = "Category Management", description = "Endpoints for managing categories")
public class CategoryController {
    public CategoryDto createCategory(CategoryDto categoryDto){

    }

    public List getAll() {

    }

    public CategoryDto getCategoryById(Long id) {

    }

    public CategoryDto updateCategory(Long id, CategoryDto categoryDto) {

    }

    public void deleteCategory(Long id) {

    }

    public List<> getBooksByCategoryId(Long id) {
//        (endpoint: "/{id}/books")
    }
}
