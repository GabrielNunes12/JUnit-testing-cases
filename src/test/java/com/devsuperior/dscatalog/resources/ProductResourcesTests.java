package com.devsuperior.dscatalog.resources;

import com.devsuperior.dscatalog.Testes;
import com.devsuperior.dscatalog.dto.ProductDTO;
import com.devsuperior.dscatalog.services.ProductService;
import com.devsuperior.dscatalog.services.exceptions.DatabaseException;
import com.devsuperior.dscatalog.services.exceptions.ResourceNotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

@WebMvcTest(ProductResource.class)
public class ProductResourcesTests {
  @Autowired
  private MockMvc mockMvc;
  @MockBean
  private ProductService productService;

  @Autowired
  private ObjectMapper objectMapper;
  private ProductDTO productDTO;
  private PageImpl<ProductDTO> page;
  private Long existingId;
  private Long nonExistingId;
  private Long dependentId;

  @BeforeEach
  void setUp() {
    productDTO = Testes.createProductDTO();
    existingId = 1L;
    nonExistingId = 1000L;
    dependentId = 3L;
    page = new PageImpl<>(List.of(productDTO));
    Mockito.when(productService.findAllPaged(any())).thenReturn(page);
    Mockito.when(productService.findById(existingId)).thenReturn(productDTO);
    Mockito.when(productService.findById(nonExistingId)).thenThrow(ResourceNotFoundException.class);

    Mockito.when(productService.update(eq(existingId), any())).thenReturn(productDTO);
    Mockito.when(productService.update(eq(nonExistingId), any())).thenThrow(ResourceNotFoundException.class);

    Mockito.doNothing().when(productService).delete(existingId);
    Mockito.doThrow(ResourceNotFoundException.class).when(productService).delete(nonExistingId);
    Mockito.doThrow(DatabaseException.class).when(productService).delete(dependentId);

    Mockito.when(productService.insert(any())).thenReturn(productDTO);
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
  @Test
  public void findByIdShouldReturnProductWhenIdExists() throws Exception {
    ResultActions resultActions =  mockMvc.perform(
            get("/products/{id}", existingId)
                    .accept(MediaType.APPLICATION_JSON)
    );
    resultActions.andExpect(status().isOk());
    resultActions.andExpect(jsonPath("$.id").exists());
    resultActions.andExpect(jsonPath("$.name").exists());
    resultActions.andExpect(jsonPath("$.description").exists());
  }
  @Test
  public void findByIdShouldNotReturnProductWhenIdDoesNotExists() throws Exception {
    ResultActions resultActions =  mockMvc.perform(
            get("/products/{id}", nonExistingId)
                    .accept(MediaType.APPLICATION_JSON)
    );
    resultActions.andExpect(status().isNotFound());
  }
  @Test
  public void updateShouldReturnProductDTOWhenIdExists() throws Exception {
    String jsonBody = objectMapper.writeValueAsString(productDTO);
    ResultActions resultActions =  mockMvc.perform(
            put("/products/{id}", existingId)
                    .content(jsonBody)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
    );
    resultActions.andExpect(status().isOk());
    resultActions.andExpect(jsonPath("$.id").exists());
    resultActions.andExpect(jsonPath("$.name").exists());
    resultActions.andExpect(jsonPath("$.description").exists());
  }
  @Test
  public void updateShouldReturnNotFoundWhenIdDoesNotExists() throws Exception {
    String jsonBody = objectMapper.writeValueAsString(productDTO);
    ResultActions resultActions =  mockMvc.perform(
            put("/products/{id}", nonExistingId)
                    .content(jsonBody)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
    );
    resultActions.andExpect(status().isNotFound());
  }
  @Test
  public void insertShouldReturnCreatedAndNewProductDTO() throws Exception {
    ProductDTO productDTO1 = Testes.createProductDTO();
    String jsonBody = objectMapper.writeValueAsString(productDTO1);
    ResultActions resultActions =  mockMvc.perform(
            post("/products")
                    .content(jsonBody)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
    );
    resultActions.andExpect(status().isCreated());
    resultActions.andExpect(jsonPath("$.id").exists());
    resultActions.andExpect(jsonPath("$.name").exists());
    resultActions.andExpect(jsonPath("$.description").exists());
  }
  @Test
  public void deleteShouldReturnNoContentWhenIdExists() throws Exception {
    ResultActions resultActions =  mockMvc.perform(
            delete("/products/{id}", existingId)
                    .accept(MediaType.APPLICATION_JSON)
    );
    resultActions.andExpect(status().isNoContent());
  }
  @Test
  public void deleteShouldReturnNotFoundWhenIdDoesNotExists() throws Exception {
    ResultActions resultActions =  mockMvc.perform(
            delete("/products/{id}", nonExistingId)
                    .accept(MediaType.APPLICATION_JSON)
    );
    resultActions.andExpect(status().isNotFound());
  }
}
