package com.devsuperior.dscatalog;

import com.devsuperior.dscatalog.dto.ProductDTO;
import com.devsuperior.dscatalog.entities.Category;
import com.devsuperior.dscatalog.entities.Product;

import java.time.Instant;

public class Testes {
  public static Product createProduct() {
    Product product = new Product(1L, "Phone", "good phone", 800.0, "img.com/img.png", Instant.now());
    product.getCategories().add(new Category(2L, "Eletronics"));
    return product;
  }
  public static ProductDTO createProductDTO() {
    Product product = createProduct();
    return new ProductDTO(product, product.getCategories());
  }

  public static Category createCategory() {
    Category category = new Category(2L, "Eletronics");
    return category;
  }
}
