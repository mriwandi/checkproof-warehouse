package com.checkproof.serviceimpl;

import com.checkproof.entity.Item;
import com.checkproof.helper.converter.ItemConverter;
import com.checkproof.repository.ItemRepository;
import com.checkproof.servicemodel.ItemResult;
import com.checkproof.servicemodel.ItemSpec;
import com.checkproof.servicemodel.UpdateItemSpec;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ItemService Unit Tests")
class ItemServiceImplTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private ItemConverter itemConverter;

    @InjectMocks
    private ItemServiceImpl itemService;

    private Item testItem;
    private ItemResult testItemResult;
    private ItemSpec testItemSpec;
    private UpdateItemSpec testUpdateItemSpec;

    @BeforeEach
    void setUp() {
        testItem = new Item();
        testItem.setId(1L);
        testItem.setItemName("Test Item");
        testItem.setDescription("Test Description");
        testItem.setCategory("Test Category");
        testItem.setCreatedAt(LocalDateTime.now());
        testItem.setUpdatedAt(LocalDateTime.now());

        testItemResult = ItemResult.builder()
                .id(1L)
                .name("Test Item")
                .description("Test Description")
                .category("Test Category")
                .build();

        testItemSpec = new ItemSpec();
        testItemSpec.setItemName("New Item");
        testItemSpec.setDescription("New Description");
        testItemSpec.setCategory("New Category");

        testUpdateItemSpec = new UpdateItemSpec();
        testUpdateItemSpec.setId(1L);
        testUpdateItemSpec.setItemName("Updated Item");
        testUpdateItemSpec.setDescription("Updated Description");
        testUpdateItemSpec.setCategory("Updated Category");
    }

    @Test
    @DisplayName("Should return all items when getListItem is called")
    void testGetListItem_Success() {
        // Given
        List<Item> items = Arrays.asList(testItem);
        when(itemRepository.findAll()).thenReturn(items);
        when(itemConverter.toItemResult(testItem)).thenReturn(testItemResult);

        // When
        List<ItemResult> result = itemService.getListItem();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testItemResult, result.get(0));
        verify(itemRepository, times(1)).findAll();
        verify(itemConverter, times(1)).toItemResult(testItem);
    }

    @Test
    @DisplayName("Should return empty list when no items exist")
    void testGetListItem_EmptyList() {
        // Given
        when(itemRepository.findAll()).thenReturn(List.of());

        // When
        List<ItemResult> result = itemService.getListItem();

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(itemRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should return item when getItem is called with valid ID")
    void testGetItem_Success() {
        // Given
        Long itemId = 1L;
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(testItem));
        when(itemConverter.toItemResult(testItem)).thenReturn(testItemResult);

        // When
        ItemResult result = itemService.getItem(itemId);

        // Then
        assertNotNull(result);
        assertEquals(testItemResult, result);
        verify(itemRepository, times(1)).findById(itemId);
        verify(itemConverter, times(1)).toItemResult(testItem);
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException when item not found")
    void testGetItem_NotFound() {
        // Given
        Long itemId = 999L;
        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(EntityNotFoundException.class, () -> itemService.getItem(itemId));
        verify(itemRepository, times(1)).findById(itemId);
        verify(itemConverter, never()).toItemResult(any());
    }

    @Test
    @DisplayName("Should create item successfully")
    void testCreateItem_Success() {
        // Given
        Item newItem = new Item();
        newItem.setId(1L);
        newItem.setItemName("New Item");
        newItem.setDescription("New Description");
        newItem.setCategory("New Category");

        when(itemConverter.toItemEntity(testItemSpec)).thenReturn(newItem);
        when(itemRepository.save(newItem)).thenReturn(newItem);
        when(itemConverter.toItemResult(newItem)).thenReturn(testItemResult);

        // When
        ItemResult result = itemService.createItem(testItemSpec);

        // Then
        assertNotNull(result);
        assertEquals(testItemResult, result);
        verify(itemConverter, times(1)).toItemEntity(testItemSpec);
        verify(itemRepository, times(1)).save(newItem);
        verify(itemConverter, times(1)).toItemResult(newItem);
    }

    @Test
    @DisplayName("Should update item successfully")
    void testUpdateItem_Success() {
        // Given
        when(itemRepository.findById(1L)).thenReturn(Optional.of(testItem));
        when(itemRepository.save(testItem)).thenReturn(testItem);
        when(itemConverter.toItemResult(testItem)).thenReturn(testItemResult);

        // When
        ItemResult result = itemService.updateItem(testUpdateItemSpec);

        // Then
        assertNotNull(result);
        assertEquals("Updated Item", testItem.getItemName());
        assertEquals("Updated Description", testItem.getDescription());
        assertEquals("Updated Category", testItem.getCategory());
        verify(itemRepository, times(1)).findById(1L);
        verify(itemRepository, times(1)).save(testItem);
        verify(itemConverter, times(1)).toItemResult(testItem);
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException when updating non-existent item")
    void testUpdateItem_NotFound() {
        // Given
        when(itemRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(EntityNotFoundException.class, () -> itemService.updateItem(testUpdateItemSpec));
        verify(itemRepository, times(1)).findById(1L);
        verify(itemRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should delete item successfully")
    void testDeleteItem_Success() {
        // Given
        Long itemId = 1L;
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(testItem));
        doNothing().when(itemRepository).delete(testItem);

        // When
        Void result = itemService.deleteItem(itemId);

        // Then
        assertNull(result);
        verify(itemRepository, times(1)).findById(itemId);
        verify(itemRepository, times(1)).delete(testItem);
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException when deleting non-existent item")
    void testDeleteItem_NotFound() {
        // Given
        Long itemId = 999L;
        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(EntityNotFoundException.class, () -> itemService.deleteItem(itemId));
        verify(itemRepository, times(1)).findById(itemId);
        verify(itemRepository, never()).delete(any());
    }
}

