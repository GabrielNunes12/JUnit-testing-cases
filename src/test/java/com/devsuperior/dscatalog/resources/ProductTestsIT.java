package com.devsuperior.dscatalog.resources;

import com.devsuperior.dscatalog.Testes;
import com.devsuperior.dscatalog.dto.ProductDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class ProductTestsIT {
  @Autowired
  private MockMvc mockMvc;
  @Autowired
  private ObjectMapper objectMapper;
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
  public void findAllShouldReturnSortedPageWhenSortByName() throws Exception {
    ResultActions resultActions = mockMvc.perform(
            get("/products?page=0&size=12&sort=name,asc")
                    .accept(MediaType.APPLICATION_JSON)
    );
    resultActions.andExpect(status().isOk());
    resultActions.andExpect(jsonPath("$.content").exists());
    resultActions.andExpect(jsonPath("$.totalElements").value(countTotalProducts));
  }
  @Test
  public void updateShouldReturnProductDTOWhenIdExists() throws Exception {
    ProductDTO productDTO = Testes.createProductDTO();
    String jsonBody = objectMapper.writeValueAsString(productDTO);
    String expectedName = productDTO.getName();
    ResultActions resultActions =  mockMvc.perform(
            put("/products/{id}", existingId)
                    .content(jsonBody)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
    );
    resultActions.andExpect(status().isOk());
    resultActions.andExpect(jsonPath("$.id").exists());
    resultActions.andExpect(jsonPath("$.name").value(expectedName));
    resultActions.andExpect(jsonPath("$.description").exists());
  }
  @Test
  public void updateShouldReturnNotFoundWhenIdDoesNotExists() throws Exception {
    ProductDTO productDTO = Testes.createProductDTO();
    String jsonBody = objectMapper.writeValueAsString(productDTO);
    ResultActions resultActions = mockMvc.perform(
            put("/products/{id}", nonExistingId)
                    .content(jsonBody)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
    );
    resultActions.andExpect(status().isNotFound());
  }
}