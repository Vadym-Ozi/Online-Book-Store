package example.service;

import example.dto.book.BookDto;
import example.dto.category.CategoryDto;
import example.model.Book;
import org.springframework.stereotype.Service;

import java.util.List;


public interface CategoryService {
    List<> findAll();

    CategoryDto getById(Long id);

    CategoryDto save(CategoryDto categoryDto);

    CategoryDto update(Long id, CategoryDto categoryDto);

    void deleteById(Long id);

}
