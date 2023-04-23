package com.devsuperior.dscatalog.services;

import com.devsuperior.dscatalog.dto.ProductDTO;
import com.devsuperior.dscatalog.repositories.ProductRepository;
import com.devsuperior.dscatalog.services.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;


@SpringBootTest
@Transactional
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
  @Test
  public void findAllPagedShouldReturnPageWhenPage0Size10() {
    PageRequest pageRequest = PageRequest.of(0,10);
    Page<ProductDTO> page = productService.findAllPaged(pageRequest);
    Assertions.assertFalse(page.isEmpty());
    Assertions.assertEquals(0, page.getNumber());
    Assertions.assertEquals(10, page.getSize());
    Assertions.assertEquals(countTotalProducts, page.getTotalElements());
  }
  @Test
  public void findAllPagedShouldReturnEmptyPageWhenPageDoesNotExists() {
    PageRequest pageRequest = PageRequest.of(50,10);
    Page<ProductDTO> page = productService.findAllPaged(pageRequest);
    Assertions.assertTrue(page.isEmpty());
  }
  @Test
  public void findAllPagedShouldReturnSortedPageWhenSortByName() {
    PageRequest pageRequest = PageRequest.of(0,10, Sort.by("name"));
    Page<ProductDTO> page = productService.findAllPaged(pageRequest);
    Assertions.assertFalse(page.isEmpty());
    Assertions.assertEquals("Macbook Pro", page.getContent().get(0).getName());
    Assertions.assertEquals("PC Gamer", page.getContent().get(1).getName());
    Assertions.assertEquals("PC Gamer Alfa", page.getContent().get(2).getName());
  }
}
