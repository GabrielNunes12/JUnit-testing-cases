package com.devsuperior.dscatalog.resources;

import com.devsuperior.dscatalog.Testes;
import com.devsuperior.dscatalog.dto.ProductDTO;
import com.devsuperior.dscatalog.services.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

@WebMvcTest(ProductResource.class)
public class ProductResourcesTests {
  @Autowired
  private MockMvc mockMvc;
  @MockBean
  private ProductService productService;
  private ProductDTO productDTO;
  private PageImpl<ProductDTO> page;

  @BeforeEach
  void setUp() {
    productDTO = Testes.createProductDTO();
    page = new PageImpl<>(List.of(productDTO));
    Mockito.when(productService.findAllPaged(ArgumentMatchers.any())).thenReturn(page);
  }
  @Test
  public void findAllShouldReturnPage() throws Exception {
    mockMvc.perform(get("/products/")).andExpect(status().isOk());
  }
  @Test
  public void findAllShouldReturnPageDifferent() throws Exception {
    ResultActions resultActions =  mockMvc.perform(
            get("/products/")
          .accept(MediaType.APPLICATION_JSON)
    );
    resultActions.andExpect(status().isOk());
  }
}
