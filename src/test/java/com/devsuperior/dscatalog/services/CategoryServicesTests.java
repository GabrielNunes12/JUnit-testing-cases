package com.devsuperior.dscatalog.services;

import com.devsuperior.dscatalog.Testes;
import com.devsuperior.dscatalog.dto.CategoryDTO;
import com.devsuperior.dscatalog.dto.ProductDTO;
import com.devsuperior.dscatalog.entities.Category;
import com.devsuperior.dscatalog.entities.Product;
import com.devsuperior.dscatalog.repositories.CategoryRepository;
import com.devsuperior.dscatalog.repositories.ProductRepository;
import com.devsuperior.dscatalog.services.exceptions.DatabaseException;
import com.devsuperior.dscatalog.services.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

//Não carrega o contexto, mas permite usar os recursos do Spring com JUnit (teste de unidade: service/component)
@ExtendWith(SpringExtension.class)
public class CategoryServicesTests {
  @InjectMocks
  private CategoryService categoryService;
  @Mock
  private CategoryRepository categoryRepository;
  private Long existingId;
  private Long nonExistingId;
  private Long dependentId;
  private PageImpl<Category> page;
  private Category category;
  @BeforeEach
  void setUp() {
    existingId = 1L;
    nonExistingId = 1000L;
    dependentId = 4L;
    category = Testes.createCategory();
    page = new PageImpl<>(List.of(category));
    Mockito.doNothing().when(categoryRepository).deleteById(existingId);

    Mockito.when(categoryRepository.findAll((Pageable) ArgumentMatchers.any())).thenReturn(page);
    Mockito.when(categoryRepository.save(ArgumentMatchers.any())).thenReturn(category);
    Mockito.when(categoryRepository.findById(existingId)).thenReturn(Optional.of(category));
    Mockito.when(categoryRepository.findById(nonExistingId)).thenReturn(Optional.empty());
    Mockito.when(categoryRepository.getOne(existingId)).thenReturn(category);
    Mockito.when(categoryRepository.getOne(nonExistingId)).thenThrow(ResourceNotFoundException.class);

    Mockito.doThrow(EmptyResultDataAccessException.class).when(categoryRepository).deleteById(nonExistingId);
    Mockito.doThrow(DataIntegrityViolationException.class).when(categoryRepository).deleteById(dependentId);

  }
  @Test
  public void deleteShouldDoNothingWhenIdExists() {
    Assertions.assertDoesNotThrow(() -> {
      categoryRepository.deleteById(existingId);
    });
  }

  @Test
  public void deleteShouldThrowResourceNotFoundExceptionWhenIdDoesNotExists() {
    Assertions.assertThrows(ResourceNotFoundException.class, () -> {
      categoryService.delete(nonExistingId);
    });
  }
  @Test
  public void deleteShouldThrowDatabaseExceptionWhenIdDoesNotExists() {
    Assertions.assertThrows(DatabaseException.class, () -> {
      categoryService.delete(dependentId);
    });
  }
  @Test
  public void findAllPagedShouldReturnPage() {
    Pageable pageable = PageRequest.of(0,10);
    Page<CategoryDTO> result = categoryService.findAllPaged(pageable);
    Assertions.assertNotNull(result);
    Mockito.verify(categoryRepository, Mockito.times(1)).findAll(pageable);
  }
  @Test
  public void findByIdShouldReturnProductDTOWhenIdExists() {
    CategoryDTO categoryDTO = categoryService.findById(existingId);
    Assertions.assertNotNull(categoryDTO);
  }
  @Test
  public void findByIdShouldNotReturnProductDTOWhenIdDoesNotExists() {
    Assertions.assertThrows(ResourceNotFoundException.class, () -> {
      categoryService.findById(nonExistingId);
    });
  }
  @Test
  public void updateShouldReturnProductDTOWhenIdExists() {
    CategoryDTO categoryDTO = categoryService.update(existingId, new CategoryDTO());
    Assertions.assertNotNull(categoryDTO);
  }
  @Test
  public void updateShouldNotReturnProductDTOWhenIdDoesNotExists() {
    Assertions.assertThrows(ResourceNotFoundException.class, () -> {
      categoryService.update(nonExistingId, new CategoryDTO());
    });
  }
}
