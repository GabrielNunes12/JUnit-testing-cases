package com.devsuperior.dscatalog.services;

import com.devsuperior.dscatalog.Testes;
import com.devsuperior.dscatalog.repositories.ProductRepository;
import com.devsuperior.dscatalog.services.exceptions.DatabaseException;
import com.devsuperior.dscatalog.services.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

@SpringBootTest
public class ProductServicesIT {
  @Autowired
  private ProductService productService;
  @Autowired
  private ProductRepository productRepository;
  private Long existingId;
  private Long nonExistingId;
  private Long countTotalProducts;
  @BeforeEach
  void setUp() {
    existingId = 1L;
    nonExistingId = 1000L;
    countTotalProducts = 25L;
  }

  @Test
  public void deleteShouldDeleteResourceWhenIdExists() {
    productService.delete(existingId);
    Assertions.assertEquals(countTotalProducts - 1, productRepository.count());
  }
  @Test
  public void deleteShouldThrowResourceNotFoundWhenIdDoesNotExists() {
    Assertions.assertThrows(ResourceNotFoundException.class, () -> {
      productService.delete(nonExistingId);
    });
  }
}
