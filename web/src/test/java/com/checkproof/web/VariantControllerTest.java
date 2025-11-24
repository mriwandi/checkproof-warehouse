package com.checkproof.web;

import com.checkproof.helper.converter.ItemVariantConverter;
import com.checkproof.service.ItemVariantService;
import com.checkproof.servicemodel.ItemVariantResult;
import com.checkproof.servicemodel.UpdateItemVariantSpec;
import com.checkproof.webmodel.ItemVariantRequest;
import com.checkproof.webmodel.ItemVariantResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(VariantController.class)
@ContextConfiguration(classes = WebTestConfiguration.class)
@DisplayName("VariantController Unit Tests")
class VariantControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemVariantService itemVariantService;

    @MockBean
    private ItemVariantConverter itemVariantConverter;

    @Autowired
    private ObjectMapper objectMapper;

    private ItemVariantRequest variantRequest;
    private ItemVariantResponse variantResponse;
    private ItemVariantResult variantResult;

    @BeforeEach
    void setUp() {
        variantRequest = ItemVariantRequest.builder()
                .name("Small - Blue")
                .sku("TSHIRT-SM-BLUE-001")
                .price(29.99)
                .build();

        variantResponse = ItemVariantResponse.builder()
                .id(1L)
                .name("Small - Blue")
                .sku("TSHIRT-SM-BLUE-001")
                .price(29.99)
                .build();

        variantResult = new ItemVariantResult();
        variantResult.setId(1L);
        variantResult.setName("Small - Blue");
        variantResult.setSku("TSHIRT-SM-BLUE-001");
        variantResult.setPrice(29.99);
    }

    @Test
    @DisplayName("PUT /api/v1/variants/{id} - Should update variant successfully")
    void testUpdateVariant_Success() throws Exception {
        // Given
        Long variantId = 1L;
        UpdateItemVariantSpec updateSpec = new UpdateItemVariantSpec();
        updateSpec.setId(variantId);
        updateSpec.setName("Updated Variant");
        updateSpec.setSku("UPDATED-SKU-001");
        updateSpec.setPrice(39.99);

        when(itemVariantConverter.toUpdateItemVariantSpec(any(ItemVariantRequest.class))).thenReturn(updateSpec);
        when(itemVariantService.updateVariant(any())).thenReturn(variantResult);
        when(itemVariantConverter.toItemVariantResponse(variantResult)).thenReturn(variantResponse);

        // When & Then
        mockMvc.perform(put("/api/v1/variants/{id}", variantId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(variantRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Small - Blue"));

        verify(itemVariantService, times(1)).updateVariant(any());
        verify(itemVariantConverter, times(1)).toItemVariantResponse(variantResult);
    }

    @Test
    @DisplayName("PUT /api/v1/variants/{id} - Should return 400 when validation fails")
    void testUpdateVariant_ValidationError() throws Exception {
        // Given
        Long variantId = 1L;
        ItemVariantRequest invalidRequest = ItemVariantRequest.builder()
                .name("") // Empty name should fail validation
                .sku("SKU-001")
                .price(29.99)
                .build();

        // When & Then
        mockMvc.perform(put("/api/v1/variants/{id}", variantId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(itemVariantService, never()).updateVariant(any());
    }

    @Test
    @DisplayName("DELETE /api/v1/variants/{id} - Should delete variant successfully")
    void testDeleteVariant_Success() throws Exception {
        // Given
        Long variantId = 1L;
        doNothing().when(itemVariantService).deleteVariant(variantId);

        // When & Then
        mockMvc.perform(delete("/api/v1/variants/{id}", variantId))
                .andExpect(status().isOk());

        verify(itemVariantService, times(1)).deleteVariant(variantId);
    }
}

