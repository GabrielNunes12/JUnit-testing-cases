package com.devsuperior.dscatalog.services;

import com.devsuperior.dscatalog.Testes;
import com.devsuperior.dscatalog.dto.ProductDTO;
import com.devsuperior.dscatalog.entities.Product;
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

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;
import java.util.OptionalLong;

//Não carrega o contexto, mas permite usar os recursos do Spring com JUnit (teste de unidade: service/component)
@ExtendWith(SpringExtension.class)
public class ProductServicesTests {
  @InjectMocks
  private ProductService productService;
  @Mock
  private ProductRepository productRepository;
  private Long existingId;
  private Long nonExistingId;
  private Long dependentId;
  private PageImpl<Product> page;
  private Product product;
  @BeforeEach
  void setUp() {
    existingId = 1L;
    nonExistingId = 1000L;
    dependentId = 4L;
    product = Testes.createProduct();
    page = new PageImpl<>(List.of(product));
    Mockito.doNothing().when(productRepository).deleteById(existingId);

    Mockito.when(productRepository.findAll((Pageable) ArgumentMatchers.any())).thenReturn(page);
    Mockito.when(productRepository.save(ArgumentMatchers.any())).thenReturn(product);
    Mockito.when(productRepository.findById(existingId)).thenReturn(Optional.of(product));
    Mockito.when(productRepository.findById(nonExistingId)).thenReturn(Optional.empty());
    Mockito.when(productRepository.getOne(existingId)).thenReturn(product);
    Mockito.when(productRepository.getOne(nonExistingId)).thenThrow(ResourceNotFoundException.class);

    Mockito.doThrow(EmptyResultDataAccessException.class).when(productRepository).deleteById(nonExistingId);
    Mockito.doThrow(DataIntegrityViolationException.class).when(productRepository).deleteById(dependentId);

  }
  @Test
  public void deleteShouldDoNothingWhenIdExists() {
    Assertions.assertDoesNotThrow(() -> {
      productRepository.deleteById(existingId);
    });
  }

  @Test
  public void deleteShouldThrowResourceNotFoundExceptionWhenIdDoesNotExists() {
    Assertions.assertThrows(ResourceNotFoundException.class, () -> {
      productService.delete(nonExistingId);
    });
  }
  @Test
  public void deleteShouldThrowDatabaseExceptionWhenIdDoesNotExists() {
    Assertions.assertThrows(DatabaseException.class, () -> {
      productService.delete(dependentId);
    });
  }
  @Test
  public void findAllPagedShouldReturnPage() {
    Pageable pageable = PageRequest.of(0,10);
    Page<ProductDTO> result = productService.findAllPaged(pageable);
    Assertions.assertNotNull(result);
    Mockito.verify(productRepository, Mockito.times(1)).findAll(pageable);
  }
  @Test
  public void findByIdShouldReturnProductDTOWhenIdExists() {
    ProductDTO productDTO = productService.findById(existingId);
    Assertions.assertNotNull(productDTO);
  }
  @Test
  public void findByIdShouldNotReturnProductDTOWhenIdDoesNotExists() {
    Assertions.assertThrows(ResourceNotFoundException.class, () -> {
      productService.findById(nonExistingId);
    });
  }
  @Test
  public void updateShouldReturnProductDTOWhenIdExists() {
    ProductDTO productDTO = productService.update(existingId, new ProductDTO());
    Assertions.assertNotNull(productDTO);
  }
  @Test
  public void updateShouldNotReturnProductDTOWhenIdDoesNotExists() {
    Assertions.assertThrows(ResourceNotFoundException.class, () -> {
      productService.update(nonExistingId, new ProductDTO());
    });
  }
}
