package example.service.impl;

import example.dto.category.CategoryDto;
import example.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    @Override
    public CategoryDto save(CategoryDto categoryDto) {
        return null;
    }

    @Override
    public List findAll() {
        return List.of();
    }

    @Override
    public CategoryDto getById(Long id) {
        return null;
    }

    @Override
    public CategoryDto update(Long id, CategoryDto categoryDto) {
        return null;
    }

    @Override
    public void deleteById(Long id) {

    }
}
