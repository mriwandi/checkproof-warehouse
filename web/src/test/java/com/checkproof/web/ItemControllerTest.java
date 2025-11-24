package com.checkproof.web;

import com.checkproof.helper.converter.ItemConverter;
import com.checkproof.helper.converter.ItemVariantConverter;
import com.checkproof.service.ItemService;
import com.checkproof.service.ItemVariantService;
import com.checkproof.servicemodel.ItemResult;
import com.checkproof.servicemodel.ItemSpec;
import com.checkproof.servicemodel.ItemVariantResult;
import com.checkproof.servicemodel.ItemVariantSpec;
import com.checkproof.servicemodel.UpdateItemSpec;
import com.checkproof.webmodel.ItemRequest;
import com.checkproof.webmodel.ItemResponse;
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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemController.class)
@ContextConfiguration(classes = WebTestConfiguration.class)
@DisplayName("ItemController Unit Tests")
class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemService itemService;

    @MockBean
    private ItemConverter itemConverter;

    @MockBean
    private ItemVariantService itemVariantService;

    @MockBean
    private ItemVariantConverter itemVariantConverter;

    @Autowired
    private ObjectMapper objectMapper;

    private ItemRequest itemRequest;
    private ItemResponse itemResponse;
    private ItemResult itemResult;
    private ItemVariantRequest variantRequest;
    private ItemVariantResponse variantResponse;
    private ItemVariantResult variantResult;

    @BeforeEach
    void setUp() {
        itemRequest = ItemRequest.builder()
                .itemName("Test Item")
                .description("Test Description")
                .category("Test Category")
                .build();

        itemResponse = ItemResponse.builder()
                .id(1L)
                .name("Test Item")
                .description("Test Description")
                .category("Test Category")
                .build();

        itemResult = ItemResult.builder()
                .id(1L)
                .name("Test Item")
                .description("Test Description")
                .category("Test Category")
                .build();

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
    @DisplayName("GET /api/v1/items - Should return list of items")
    void testGetListItems_Success() throws Exception {
        // Given
        List<ItemResult> results = Collections.singletonList(itemResult);
        when(itemService.getListItem()).thenReturn(results);
        when(itemConverter.toItemResponse(itemResult)).thenReturn(itemResponse);

        // When & Then
        mockMvc.perform(get("/api/v1/items"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Test Item"));

        verify(itemService, times(1)).getListItem();
        verify(itemConverter, times(1)).toItemResponse(itemResult);
    }

    @Test
    @DisplayName("GET /api/v1/items/{id} - Should return item by ID")
    void testGetItem_Success() throws Exception {
        // Given
        Long itemId = 1L;
        when(itemService.getItem(itemId)).thenReturn(itemResult);
        when(itemConverter.toItemResponse(itemResult)).thenReturn(itemResponse);

        // When & Then
        mockMvc.perform(get("/api/v1/items/{id}", itemId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Test Item"));

        verify(itemService, times(1)).getItem(itemId);
        verify(itemConverter, times(1)).toItemResponse(itemResult);
    }

    @Test
    @DisplayName("POST /api/v1/items - Should create item successfully")
    void testCreateItem_Success() throws Exception {
        // Given
        ItemSpec itemSpec = new ItemSpec();
        itemSpec.setItemName("Test Item");
        itemSpec.setDescription("Test Description");
        itemSpec.setCategory("Test Category");

        when(itemConverter.toItemSpec(any(ItemRequest.class))).thenReturn(itemSpec);
        when(itemService.createItem(itemSpec)).thenReturn(itemResult);
        when(itemConverter.toItemResponse(itemResult)).thenReturn(itemResponse);

        // When & Then
        mockMvc.perform(post("/api/v1/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Test Item"));

        verify(itemService, times(1)).createItem(any());
        verify(itemConverter, times(1)).toItemResponse(itemResult);
    }

    @Test
    @DisplayName("POST /api/v1/items - Should return 400 when validation fails")
    void testCreateItem_ValidationError() throws Exception {
        // Given
        ItemRequest invalidRequest = ItemRequest.builder()
                .itemName("") // Empty name should fail validation
                .description("Test Description")
                .build();

        // When & Then
        mockMvc.perform(post("/api/v1/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(itemService, never()).createItem(any());
    }

    @Test
    @DisplayName("PUT /api/v1/items/{id} - Should update item successfully")
    void testUpdateItem_Success() throws Exception {
        // Given
        Long itemId = 1L;
        UpdateItemSpec updateSpec = new UpdateItemSpec();
        updateSpec.setId(itemId);
        updateSpec.setItemName("Updated Item");
        updateSpec.setDescription("Updated Description");
        updateSpec.setCategory("Updated Category");

        when(itemConverter.toUpdateItemSpec(any(ItemRequest.class))).thenReturn(updateSpec);
        when(itemService.updateItem(any())).thenReturn(itemResult);
        when(itemConverter.toItemResponse(itemResult)).thenReturn(itemResponse);

        // When & Then
        mockMvc.perform(put("/api/v1/items/{id}", itemId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemRequest)))
                .andExpect(status().isOk());

        verify(itemService, times(1)).updateItem(any());
    }

    @Test
    @DisplayName("DELETE /api/v1/items/{id} - Should delete item successfully")
    void testDeleteItem_Success() throws Exception {
        // Given
        Long itemId = 1L;
        doNothing().when(itemService).deleteItem(itemId);

        // When & Then
        mockMvc.perform(delete("/api/v1/items/{id}", itemId))
                .andExpect(status().isOk());

        verify(itemService, times(1)).deleteItem(itemId);
    }

    @Test
    @DisplayName("POST /api/v1/items/{id}/variants - Should create variant successfully")
    void testCreateVariant_Success() throws Exception {
        // Given
        Long itemId = 1L;
        ItemVariantSpec variantSpec = new ItemVariantSpec();
        variantSpec.setItemId(itemId);
        variantSpec.setName("Small - Blue");
        variantSpec.setSku("TSHIRT-SM-BLUE-001");
        variantSpec.setPrice(29.99);

        when(itemVariantConverter.toItemVariantSpec(any(ItemVariantRequest.class))).thenReturn(variantSpec);
        when(itemVariantService.createVariant(any())).thenReturn(variantResult);
        when(itemVariantConverter.toItemVariantResponse(variantResult)).thenReturn(variantResponse);

        // When & Then
        mockMvc.perform(post("/api/v1/items/{id}/variants", itemId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(variantRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Small - Blue"));

        verify(itemVariantService, times(1)).createVariant(any());
    }

    @Test
    @DisplayName("GET /api/v1/items/{itemId}/variants - Should return variants of item")
    void testGetVariantsOfItem_Success() throws Exception {
        // Given
        Long itemId = 1L;
        List<ItemVariantResult> results = Collections.singletonList(variantResult);
        when(itemVariantService.getVariantsOfItem(itemId)).thenReturn(results);
        when(itemVariantConverter.toItemVariantResponse(variantResult)).thenReturn(variantResponse);

        // When & Then
        mockMvc.perform(get("/api/v1/items/{itemId}/variants", itemId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Small - Blue"));

        verify(itemVariantService, times(1)).getVariantsOfItem(itemId);
    }
}

