package com.devsuperior.dscatalog.repositories;

import com.devsuperior.dscatalog.Testes;
import com.devsuperior.dscatalog.entities.Product;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.EmptyResultDataAccessException;

import java.util.Optional;

//Carrega somente os componentes relacionados ao Spring Data JPA. Cada teste é transacional e dá rollback ao final.
@DataJpaTest
public class ProductRepositoryTests {
  @Autowired
  private ProductRepository productRepository;
  private Long existingId;
  private Long nonExistingId;
  private Long countTotalProducts;

  @BeforeEach
  void setup() throws Exception {
    existingId = 1L;
    nonExistingId = 1000L;
    countTotalProducts = 25L;
  }
  @Test
  public void deleteShouldDeleteObjectWhenIdExists() {
    productRepository.deleteById(existingId);
    Optional<Product> product = productRepository.findById(existingId);
    Assertions.assertFalse(product.isPresent());
  }
  @Test
  public void deleteShouldNotDeleteObjectWhenIdDoesNotExists() {
    Assertions.assertThrows(EmptyResultDataAccessException.class,() -> {
      productRepository.deleteById(nonExistingId);
    });
  }
  @Test
  public void saveShouldPersistWithAutoIncrementWhenIdIsNull() {
    Product product = Testes.createProduct();
    product.setId(null);
    product = productRepository.save(product);
    Assertions.assertNotNull(product.getId());
    Assertions.assertEquals(countTotalProducts + 1, product.getId());
  }
  @Test
  public void findByIdShouldReturnOptionalProductWhenIdExists() {
    Optional<Product> product = productRepository.findById(existingId);
    Assertions.assertNotNull(product);
  }
  @Test
  public void findByIdShouldNotReturnOptionalProductWhenIdDoesNotExists() {
    Optional<Product> product = productRepository.findById(nonExistingId);
    Assertions.assertFalse(product.isPresent());
  }
}
