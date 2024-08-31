package example.service.impl;

import example.dto.category.CategoryRequestDto;
import example.dto.category.CategoryDto;
import example.exception.EntityNotFoundException;
import example.mapper.CategoryMapper;
import example.model.Category;
import example.repository.category.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceImplTest {
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private CategoryMapper categoryMapper;
    @InjectMocks
    private CategoryServiceImpl categoryService;
    private List<Category> categoryList;
    private List<CategoryRequestDto> requestList;
    private List<CategoryDto> categoryDtoList;

    @BeforeEach
    public void setUp() {
        categoryList = new ArrayList<>();
        requestList = new ArrayList<>();
        categoryDtoList = new ArrayList<>();
        setRequestList();
        setCategoryList();
        setCategoryDtoList();
    }


    @Test
    @DisplayName("Using valid data for test with successful result")
    public void testSave_ValidData_SuccessfullySaved() {
        CategoryRequestDto categoryRequestDto = requestList.get(0);
        Category category = categoryList.get(0);
        CategoryDto categoryDto = categoryDtoList.get(0);

        when(categoryMapper.toDto(any(Category.class))).thenReturn(categoryDto);
        when(categoryRepository.save(any(Category.class))).thenReturn(category);
        doAnswer(invocation -> {
            Category cat = invocation.getArgument(1);
            CategoryRequestDto dto = invocation.getArgument(0);
            cat.setName(dto.getName());
            return null;
        }).when(categoryMapper).updateCategoryFromDto(any(CategoryRequestDto.class), any(Category.class));

        CategoryDto result = categoryService.save(categoryRequestDto);

        assertNotNull(result);
        assertEquals(categoryDto.getId(), result.getId());
        assertEquals(categoryDto.getName(), result.getName());
        verify(categoryMapper).updateCategoryFromDto(eq(categoryRequestDto), any(Category.class));
        verify(categoryRepository).save(any(Category.class));
    }

    @Test
    @DisplayName("Due to saving process we`ve got "
            + "and error from DB as a result exception thrown")
    void testSave_DbError_ThrowsException() {
        CategoryRequestDto requestDto = new CategoryRequestDto();
        requestDto.setName("Test Category");

        when(categoryRepository.save(any(Category.class))).thenThrow(new RuntimeException("Database error"));

        assertThrows(RuntimeException.class, () -> categoryService.save(requestDto));
        verify(categoryRepository).save(any(Category.class));
    }


    @Test
    @DisplayName("Successfully returns empty list when there is no saved category in db")
    void testFindAll_EmptyData_EmptyList() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Category> emptyPage = new PageImpl<>(Collections.emptyList());

        when(categoryRepository.findAll(pageable)).thenReturn(emptyPage);

        List<CategoryDto> result = categoryService.findAll(pageable);

        assertTrue(result.isEmpty());
        verify(categoryRepository).findAll(pageable);
    }

    @Test
    @DisplayName("Get all data on one page were, values should be present")
    void testGetAll_WithPresentData_NonEmptyList() {
        Pageable pageable = PageRequest.of(0, 10);
        Category category = categoryList.get(0);
        CategoryDto categoryDto = categoryDtoList.get(0);
        Page<Category> categoryPage = new PageImpl<>(categoryList);

        when(categoryRepository.findAll(pageable)).thenReturn(categoryPage);
        when(categoryMapper.toDto(category)).thenReturn(categoryDto);

        List<CategoryDto> result = categoryService.findAll(pageable);

        assertEquals(2, result.size());
        assertEquals(categoryDto, result.get(0));
        verify(categoryRepository).findAll(pageable);
        verify(categoryMapper).toDto(category);
    }

    @Test
    @DisplayName("Get category by correct id successfully")
    void testGetById_CorrectId_WithRequestedId() {
        Long categoryId = 1L;
        Category category = categoryList.get(0);
        CategoryDto categoryDto = categoryDtoList.get(0);

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        when(categoryMapper.toDto(category)).thenReturn(categoryDto);

        CategoryDto result = categoryService.getById(categoryId);

        assertNotNull(result);
        assertEquals(categoryId, result.getId());
        verify(categoryRepository).findById(categoryId);
        verify(categoryMapper).toDto(category);
    }

    @Test
    @DisplayName("Fail to get category with not exist id")
    void testGetById_NotExistID_NotFound() {
        Long categoryId = 1L;
        String expectedErrorMessage = "Can`t find category with id: " + categoryId;

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> categoryService.getById(categoryId));
        assertEquals(expectedErrorMessage, exception.getMessage());
        verify(categoryRepository).findById(categoryId);
        verify(categoryMapper, never()).toDto(any(Category.class));
    }

    @Test
    @DisplayName("Updating category with all valid data successfully")
    void testUpdateCategory_ValidData_Success() {
        Long id = 1L;
        CategoryRequestDto categoryRequestDto = requestList.get(1);
        Category existingCategory = categoryList.get(0);
        Category updatedCategory = categoryList.get(1);
        CategoryDto categoryDto = categoryDtoList.get(1);

        when(categoryRepository.findById(id)).thenReturn(Optional.of(existingCategory));
        when(categoryRepository.save(existingCategory)).thenReturn(updatedCategory);
        when(categoryMapper.toDto(updatedCategory)).thenReturn(categoryDto);

        CategoryDto result = categoryService.update(id, categoryRequestDto);

        assertNotNull(result);
        assertEquals("Updated", result.getName());
        verify(categoryMapper, times(1))
                .updateCategoryFromDto(categoryRequestDto, existingCategory);

        verify(categoryRepository, times(1)).findById(id);
        verify(categoryRepository, times(1)).save(existingCategory);
        verify(categoryMapper, times(1)).toDto(updatedCategory);
    }

    @Test
    @DisplayName("Trying to update category which not exist in DB as result - fail")
    void testUpdateCategory_NoNExistCategory_ExceptionThrown() {
        Long id = 1L;
        CategoryRequestDto categoryRequestDto = requestList.get(1);

        when(categoryRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            categoryService.update(id, categoryRequestDto);
        });
        verify(categoryRepository, times(1)).findById(id);
        verify(categoryMapper, never()).updateCategoryFromDto(any(), any());
        verify(categoryRepository, never()).save(any());
        verify(categoryMapper, never()).toDto(any());
    }

    @Test
    @DisplayName("Successfully delete by id")
    void testDeleteById_CorrectId_SuccessDeleted() {
        Long categoryId = 1L;
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(new Category()));

        categoryService.deleteById(categoryId);

        verify(categoryRepository).deleteById(categoryId);
    }

    @Test
    @DisplayName("Fail to delete category with non-existent id")
    void testDeleteById_NotExistId_NotFound() {
        Long categoryId = 10000L;

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> categoryService.deleteById(categoryId));

        assertEquals(String.format("Category with id %d not exist", categoryId), exception.getMessage());
        verify(categoryRepository, never()).deleteById(categoryId);
    }

    private void setRequestList() {
        CategoryRequestDto requestDto = new CategoryRequestDto();
        requestDto.setName("Test");
        requestDto.setDescription("test desc");
        requestList.add(requestDto);

        CategoryRequestDto requestForUpdate = new CategoryRequestDto();
        requestForUpdate.setName("Updated");
        requestForUpdate.setDescription("Updated");
        requestList.add(requestForUpdate);
    }

    private void setCategoryList() {
        Category category = new Category();
        category.setId(1L);
        category.setName(requestList.get(0).getName());
        category.setDescription(requestList.get(0).getDescription());
        categoryList.add(category);

        Category updatedCategory = new Category();
        updatedCategory.setId(1L);
        updatedCategory.setName(requestList.get(1).getName());
        updatedCategory.setDescription(requestList.get(1).getDescription());
        categoryList.add(updatedCategory);
    }

    private void setCategoryDtoList() {
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setId(categoryList.get(0).getId());
        categoryDto.setName(categoryList.get(0).getName());
        categoryDto.setDescription(categoryList.get(0).getDescription());
        categoryDtoList.add(categoryDto);

        CategoryDto categoryDtoForUpdatedCategory = new CategoryDto();
        categoryDtoForUpdatedCategory.setId(categoryList.get(1).getId());
        categoryDtoForUpdatedCategory.setName(categoryList.get(1).getName());
        categoryDtoForUpdatedCategory.setDescription(categoryList.get(1).getDescription());
        categoryDtoList.add(categoryDtoForUpdatedCategory);
    }
}