package com.checkproof.web;

import com.checkproof.helper.converter.ItemVariantConverter;
import com.checkproof.service.ItemVariantService;
import com.checkproof.servicemodel.ItemVariantResult;
import com.checkproof.webmodel.ItemVariantResponse;
import com.checkproof.webmodel.VariantStockRequest;
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

@WebMvcTest(StockManagementController.class)
@ContextConfiguration(classes = WebTestConfiguration.class)
@DisplayName("StockManagementController Unit Tests")
class StockManagementControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemVariantService itemVariantService;

    @MockBean
    private ItemVariantConverter itemVariantConverter;

    @Autowired
    private ObjectMapper objectMapper;

    private VariantStockRequest stockRequest;
    private ItemVariantResponse variantResponse;
    private ItemVariantResult variantResult;

    @BeforeEach
    void setUp() {
        stockRequest = VariantStockRequest.builder()
                .quantity(100)
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
    @DisplayName("PUT /api/v1/variants/{variantId}/stock - Should set stock manually")
    void testSetManualStock_Success() throws Exception {
        // Given
        Long variantId = 1L;
        when(itemVariantService.setManualStock(any())).thenReturn(variantResult);
        when(itemVariantConverter.toItemVariantResponse(variantResult)).thenReturn(variantResponse);

        // When & Then
        mockMvc.perform(put("/api/v1/variants/{variantId}/stock", variantId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(stockRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));

        verify(itemVariantService, times(1)).setManualStock(any());
        verify(itemVariantConverter, times(1)).toItemVariantResponse(variantResult);
    }

    @Test
    @DisplayName("POST /api/v1/variants/{variantId}/stock/increase - Should increase stock")
    void testIncreaseStock_Success() throws Exception {
        // Given
        Long variantId = 1L;
        when(itemVariantService.increaseStock(any())).thenReturn(variantResult);
        when(itemVariantConverter.toItemVariantResponse(variantResult)).thenReturn(variantResponse);

        // When & Then
        mockMvc.perform(post("/api/v1/variants/{variantId}/stock/increase", variantId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(stockRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));

        verify(itemVariantService, times(1)).increaseStock(any());
        verify(itemVariantConverter, times(1)).toItemVariantResponse(variantResult);
    }

    @Test
    @DisplayName("POST /api/v1/variants/{variantId}/stock/decrease - Should decrease stock")
    void testDecreaseStock_Success() throws Exception {
        // Given
        Long variantId = 1L;
        when(itemVariantService.decreaseStock(any())).thenReturn(variantResult);
        when(itemVariantConverter.toItemVariantResponse(variantResult)).thenReturn(variantResponse);

        // When & Then
        mockMvc.perform(post("/api/v1/variants/{variantId}/stock/decrease", variantId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(stockRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));

        verify(itemVariantService, times(1)).decreaseStock(any());
        verify(itemVariantConverter, times(1)).toItemVariantResponse(variantResult);
    }

    @Test
    @DisplayName("PUT /api/v1/variants/{variantId}/stock - Should return 400 when validation fails")
    void testSetManualStock_ValidationError() throws Exception {
        // Given
        Long variantId = 1L;
        VariantStockRequest invalidRequest = VariantStockRequest.builder()
                .quantity(-10) // Negative quantity should fail validation
                .build();

        // When & Then
        mockMvc.perform(put("/api/v1/variants/{variantId}/stock", variantId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(itemVariantService, never()).setManualStock(any());
    }

    @Test
    @DisplayName("POST /api/v1/variants/{variantId}/stock/increase - Should return 400 when validation fails")
    void testIncreaseStock_ValidationError() throws Exception {
        // Given
        Long variantId = 1L;
        VariantStockRequest invalidRequest = VariantStockRequest.builder()
                .quantity(null) // Null quantity should fail validation
                .build();

        // When & Then
        mockMvc.perform(post("/api/v1/variants/{variantId}/stock/increase", variantId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(itemVariantService, never()).increaseStock(any());
    }

    @Test
    @DisplayName("POST /api/v1/variants/{variantId}/stock/reserve - Should reserve stock successfully")
    void testReserveStock_Success() throws Exception {
        // Given
        Long variantId = 1L;
        when(itemVariantService.reserveStock(any())).thenReturn(variantResult);
        when(itemVariantConverter.toItemVariantResponse(variantResult)).thenReturn(variantResponse);

        // When & Then
        mockMvc.perform(post("/api/v1/variants/{variantId}/stock/reserve", variantId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(stockRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));

        verify(itemVariantService, times(1)).reserveStock(any());
        verify(itemVariantConverter, times(1)).toItemVariantResponse(variantResult);
    }

    @Test
    @DisplayName("POST /api/v1/variants/{variantId}/stock/commit - Should commit stock successfully")
    void testCommitStock_Success() throws Exception {
        // Given
        Long variantId = 1L;
        when(itemVariantService.commitStock(any())).thenReturn(variantResult);
        when(itemVariantConverter.toItemVariantResponse(variantResult)).thenReturn(variantResponse);

        // When & Then
        mockMvc.perform(post("/api/v1/variants/{variantId}/stock/commit", variantId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(stockRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));

        verify(itemVariantService, times(1)).commitStock(any());
        verify(itemVariantConverter, times(1)).toItemVariantResponse(variantResult);
    }

    @Test
    @DisplayName("POST /api/v1/variants/{variantId}/stock/release - Should release stock successfully")
    void testReleaseStock_Success() throws Exception {
        // Given
        Long variantId = 1L;
        when(itemVariantService.releaseStock(any())).thenReturn(variantResult);
        when(itemVariantConverter.toItemVariantResponse(variantResult)).thenReturn(variantResponse);

        // When & Then
        mockMvc.perform(post("/api/v1/variants/{variantId}/stock/release", variantId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(stockRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));

        verify(itemVariantService, times(1)).releaseStock(any());
        verify(itemVariantConverter, times(1)).toItemVariantResponse(variantResult);
    }
}

