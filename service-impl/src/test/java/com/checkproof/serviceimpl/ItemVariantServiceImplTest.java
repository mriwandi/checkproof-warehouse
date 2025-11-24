package com.checkproof.serviceimpl;

import com.checkproof.entity.Item;
import com.checkproof.entity.ItemVariant;
import com.checkproof.entity.ItemVariantStock;
import com.checkproof.helper.converter.ItemVariantConverter;
import com.checkproof.repository.ItemRepository;
import com.checkproof.repository.ItemVariantRepository;
import com.checkproof.repository.ItemVariantStockRepository;
import com.checkproof.servicemodel.ItemVariantResult;
import com.checkproof.servicemodel.ItemVariantSpec;
import com.checkproof.servicemodel.UpdateItemVariantSpec;
import com.checkproof.servicemodel.VariantStockSpec;
import com.checkproof.service.exception.OutOfStockException;
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
@DisplayName("ItemVariantService Unit Tests")
class ItemVariantServiceImplTest {

    @Mock
    private ItemVariantRepository itemVariantRepository;

    @Mock
    private ItemVariantStockRepository itemVariantStockRepository;

    @Mock
    private ItemVariantConverter itemVariantConverter;

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private ItemVariantServiceImpl itemVariantService;

    private Item testItem;
    private ItemVariant testVariant;
    private ItemVariantStock testStock;
    private ItemVariantResult testVariantResult;
    private ItemVariantSpec testVariantSpec;
    private UpdateItemVariantSpec testUpdateVariantSpec;

    @BeforeEach
    void setUp() {
        testItem = new Item();
        testItem.setId(1L);
        testItem.setItemName("Test Item");
        testItem.setDescription("Test Description");

        testVariant = new ItemVariant();
        testVariant.setId(1L);
        testVariant.setItem(testItem);
        testVariant.setName("Small - Blue");
        testVariant.setSku("TSHIRT-SM-BLUE-001");
        testVariant.setPrice(29.99);
        testVariant.setCreatedAt(LocalDateTime.now());
        testVariant.setUpdatedAt(LocalDateTime.now());

        testStock = new ItemVariantStock();
        testStock.setId(1L);
        testStock.setItemVariant(testVariant);
        testStock.setAvailableStock(100);
        testStock.setAllocatedStock(0);

        testVariantResult = new ItemVariantResult();
        testVariantResult.setId(1L);
        testVariantResult.setName("Small - Blue");
        testVariantResult.setSku("TSHIRT-SM-BLUE-001");
        testVariantResult.setPrice(29.99);

        testVariantSpec = new ItemVariantSpec();
        testVariantSpec.setItemId(1L);
        testVariantSpec.setName("New Variant");
        testVariantSpec.setSku("NEW-SKU-001");
        testVariantSpec.setPrice(39.99);

        testUpdateVariantSpec = new UpdateItemVariantSpec();
        testUpdateVariantSpec.setId(1L);
        testUpdateVariantSpec.setName("Updated Variant");
        testUpdateVariantSpec.setSku("UPDATED-SKU-001");
        testUpdateVariantSpec.setPrice(49.99);
    }

    @Test
    @DisplayName("Should create variant and stock successfully")
    void testCreateVariant_Success() {
        // Given
        ItemVariant newVariant = new ItemVariant();
        newVariant.setId(1L);
        newVariant.setName("New Variant");
        newVariant.setSku("NEW-SKU-001");
        newVariant.setPrice(39.99);

        when(itemRepository.findById(testVariantSpec.getItemId())).thenReturn(Optional.of(testItem));
        when(itemVariantConverter.toItemVariantEntity(testVariantSpec)).thenReturn(newVariant);
        when(itemVariantRepository.save(newVariant)).thenReturn(newVariant);
        when(itemVariantConverter.toItemVariantResult(newVariant)).thenReturn(testVariantResult);

        // When
        ItemVariantResult result = itemVariantService.createVariant(testVariantSpec);

        // Then
        assertNotNull(result);
        assertEquals(testItem, newVariant.getItem()); // Verify item was set
        verify(itemRepository, times(1)).findById(testVariantSpec.getItemId());
        verify(itemVariantRepository, times(1)).save(newVariant);
        verify(itemVariantStockRepository, times(1)).save(any(ItemVariantStock.class));
        verify(itemVariantConverter, times(1)).toItemVariantResult(newVariant);
    }

    @Test
    @DisplayName("Should return variants of an item")
    void testGetVariantsOfItem_Success() {
        // Given
        Long itemId = 1L;
        List<ItemVariant> variants = Arrays.asList(testVariant);
        when(itemVariantRepository.findByItem_Id(itemId)).thenReturn(variants);
        when(itemVariantConverter.toItemVariantResult(testVariant)).thenReturn(testVariantResult);

        // When
        List<ItemVariantResult> result = itemVariantService.getVariantsOfItem(itemId);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(itemVariantRepository, times(1)).findByItem_Id(itemId);
        verify(itemVariantConverter, times(1)).toItemVariantResult(testVariant);
    }

    @Test
    @DisplayName("Should update variant successfully")
    void testUpdateVariant_Success() {
        // Given
        when(itemVariantRepository.findById(1L)).thenReturn(Optional.of(testVariant));
        when(itemVariantRepository.save(testVariant)).thenReturn(testVariant);
        when(itemVariantConverter.toItemVariantResult(testVariant)).thenReturn(testVariantResult);

        // When
        ItemVariantResult result = itemVariantService.updateVariant(testUpdateVariantSpec);

        // Then
        assertNotNull(result);
        assertEquals("Updated Variant", testVariant.getName());
        assertEquals("UPDATED-SKU-001", testVariant.getSku());
        assertEquals(49.99, testVariant.getPrice());
        verify(itemVariantRepository, times(1)).findById(1L);
        verify(itemVariantRepository, times(1)).save(testVariant);
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException when updating non-existent variant")
    void testUpdateVariant_NotFound() {
        // Given
        when(itemVariantRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(EntityNotFoundException.class, () -> itemVariantService.updateVariant(testUpdateVariantSpec));
        verify(itemVariantRepository, times(1)).findById(1L);
        verify(itemVariantRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should delete variant and associated stock")
    void testDeleteVariant_Success() {
        // Given
        Long variantId = 1L;
        when(itemVariantRepository.findById(variantId)).thenReturn(Optional.of(testVariant));
        when(itemVariantStockRepository.findByItemVariant_Id(variantId)).thenReturn(Optional.of(testStock));
        doNothing().when(itemVariantStockRepository).delete(testStock);
        doNothing().when(itemVariantRepository).delete(testVariant);

        // When
        Void result = itemVariantService.deleteVariant(variantId);

        // Then
        assertNull(result);
        verify(itemVariantRepository, times(1)).findById(variantId);
        verify(itemVariantStockRepository, times(1)).findByItemVariant_Id(variantId);
        verify(itemVariantStockRepository, times(1)).delete(testStock);
        verify(itemVariantRepository, times(1)).delete(testVariant);
    }

    @Test
    @DisplayName("Should set stock manually successfully")
    void testSetManualStock_Success() {
        // Given
        VariantStockSpec spec = new VariantStockSpec(1L, 150);
        when(itemVariantRepository.findById(1L)).thenReturn(Optional.of(testVariant));
        when(itemVariantStockRepository.findByItemVariant_Id(1L)).thenReturn(Optional.of(testStock));
        when(itemVariantStockRepository.save(testStock)).thenReturn(testStock);
        when(itemVariantConverter.toItemVariantResult(testVariant)).thenReturn(testVariantResult);

        // When
        ItemVariantResult result = itemVariantService.setManualStock(spec);

        // Then
        assertNotNull(result);
        assertEquals(150, testStock.getAvailableStock());
        assertEquals(0, testStock.getAllocatedStock());
        verify(itemVariantStockRepository, times(1)).save(testStock);
    }

    @Test
    @DisplayName("Should create stock if not exists when setting manual stock")
    void testSetManualStock_CreateStockIfNotExists() {
        // Given
        VariantStockSpec spec = new VariantStockSpec(1L, 150);
        when(itemVariantRepository.findById(1L)).thenReturn(Optional.of(testVariant));
        when(itemVariantStockRepository.findByItemVariant_Id(1L)).thenReturn(Optional.empty());
        when(itemVariantStockRepository.save(any(ItemVariantStock.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(itemVariantConverter.toItemVariantResult(testVariant)).thenReturn(testVariantResult);

        // When
        ItemVariantResult result = itemVariantService.setManualStock(spec);

        // Then
        assertNotNull(result);
        verify(itemVariantStockRepository, times(1)).save(any(ItemVariantStock.class));
    }

    @Test
    @DisplayName("Should increase stock successfully")
    void testIncreaseStock_Success() {
        // Given
        VariantStockSpec spec = new VariantStockSpec(1L, 50);
        when(itemVariantRepository.findById(1L)).thenReturn(Optional.of(testVariant));
        when(itemVariantStockRepository.findByItemVariant_Id(1L)).thenReturn(Optional.of(testStock));
        when(itemVariantStockRepository.save(testStock)).thenReturn(testStock);
        when(itemVariantConverter.toItemVariantResult(testVariant)).thenReturn(testVariantResult);

        // When
        ItemVariantResult result = itemVariantService.increaseStock(spec);

        // Then
        assertNotNull(result);
        assertEquals(150, testStock.getAvailableStock()); // 100 + 50
        verify(itemVariantStockRepository, times(1)).save(testStock);
    }

    @Test
    @DisplayName("Should create stock if not exists when increasing stock")
    void testIncreaseStock_CreateStockIfNotExists() {
        // Given
        VariantStockSpec spec = new VariantStockSpec(1L, 50);
        when(itemVariantRepository.findById(1L)).thenReturn(Optional.of(testVariant));
        when(itemVariantStockRepository.findByItemVariant_Id(1L)).thenReturn(Optional.empty());
        when(itemVariantStockRepository.save(any(ItemVariantStock.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(itemVariantConverter.toItemVariantResult(testVariant)).thenReturn(testVariantResult);

        // When
        ItemVariantResult result = itemVariantService.increaseStock(spec);

        // Then
        assertNotNull(result);
        verify(itemVariantStockRepository, times(1)).save(any(ItemVariantStock.class));
    }

    @Test
    @DisplayName("Should decrease stock successfully when sufficient stock available")
    void testDecreaseStock_Success() {
        // Given
        testStock.setAvailableStock(100);
        testStock.setAllocatedStock(10); // 10 reserved
        VariantStockSpec spec = new VariantStockSpec(1L, 30);
        when(itemVariantRepository.findById(1L)).thenReturn(Optional.of(testVariant));
        when(itemVariantStockRepository.findByItemVariant_Id(1L)).thenReturn(Optional.of(testStock));
        when(itemVariantStockRepository.save(testStock)).thenReturn(testStock);
        when(itemVariantConverter.toItemVariantResult(testVariant)).thenReturn(testVariantResult);

        // When
        ItemVariantResult result = itemVariantService.decreaseStock(spec);

        // Then
        assertNotNull(result);
        assertEquals(70, testStock.getAvailableStock()); // 100 - 30
        assertEquals(10, testStock.getAllocatedStock()); // Unchanged
        verify(itemVariantStockRepository, times(1)).save(testStock);
    }

    @Test
    @DisplayName("Should throw OutOfStockException when insufficient stock")
    void testDecreaseStock_InsufficientStock() {
        // Given
        testStock.setAvailableStock(100);
        testStock.setAllocatedStock(20); // 20 reserved, so only 80 available
        VariantStockSpec spec = new VariantStockSpec(1L, 90); // Requesting more than available (80)
        when(itemVariantRepository.findById(1L)).thenReturn(Optional.of(testVariant));
        when(itemVariantStockRepository.findByItemVariant_Id(1L)).thenReturn(Optional.of(testStock));

        // When & Then
        OutOfStockException exception = assertThrows(OutOfStockException.class, 
            () -> itemVariantService.decreaseStock(spec));
        
        assertTrue(exception.getMessage().contains("Insufficient stock"));
        assertTrue(exception.getMessage().contains("Available: 80"));
        assertTrue(exception.getMessage().contains("Requested: 90"));
        verify(itemVariantStockRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException when stock not found for decrease")
    void testDecreaseStock_StockNotFound() {
        // Given
        VariantStockSpec spec = new VariantStockSpec(1L, 30);
        when(itemVariantRepository.findById(1L)).thenReturn(Optional.of(testVariant));
        when(itemVariantStockRepository.findByItemVariant_Id(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(EntityNotFoundException.class, () -> itemVariantService.decreaseStock(spec));
        verify(itemVariantStockRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should reserve stock successfully")
    void testReserveStock_Success() {
        // Given
        testStock.setAvailableStock(100);
        testStock.setAllocatedStock(0);
        VariantStockSpec spec = new VariantStockSpec(1L, 30);
        when(itemVariantRepository.findById(1L)).thenReturn(Optional.of(testVariant));
        when(itemVariantStockRepository.findByItemVariant_Id(1L)).thenReturn(Optional.of(testStock));
        when(itemVariantStockRepository.save(testStock)).thenReturn(testStock);
        when(itemVariantConverter.toItemVariantResult(testVariant)).thenReturn(testVariantResult);

        // When
        ItemVariantResult result = itemVariantService.reserveStock(spec);

        // Then
        assertNotNull(result);
        assertEquals(30, testStock.getAllocatedStock()); // 0 + 30
        assertEquals(100, testStock.getAvailableStock()); // Unchanged
        verify(itemVariantStockRepository, times(1)).save(testStock);
    }

    @Test
    @DisplayName("Should throw OutOfStockException when insufficient stock to reserve")
    void testReserveStock_InsufficientStock() {
        // Given
        testStock.setAvailableStock(100);
        testStock.setAllocatedStock(80); // 80 reserved, so only 20 available
        VariantStockSpec spec = new VariantStockSpec(1L, 30); // Requesting more than available (20)
        when(itemVariantRepository.findById(1L)).thenReturn(Optional.of(testVariant));
        when(itemVariantStockRepository.findByItemVariant_Id(1L)).thenReturn(Optional.of(testStock));

        // When & Then
        OutOfStockException exception = assertThrows(OutOfStockException.class, 
            () -> itemVariantService.reserveStock(spec));
        
        assertTrue(exception.getMessage().contains("Insufficient stock to reserve"));
        assertTrue(exception.getMessage().contains("Available: 20"));
        assertTrue(exception.getMessage().contains("Requested: 30"));
        verify(itemVariantStockRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should commit stock successfully")
    void testCommitStock_Success() {
        // Given
        testStock.setAvailableStock(100);
        testStock.setAllocatedStock(50);
        VariantStockSpec spec = new VariantStockSpec(1L, 20);
        when(itemVariantRepository.findById(1L)).thenReturn(Optional.of(testVariant));
        when(itemVariantStockRepository.findByItemVariant_Id(1L)).thenReturn(Optional.of(testStock));
        when(itemVariantStockRepository.save(testStock)).thenReturn(testStock);
        when(itemVariantConverter.toItemVariantResult(testVariant)).thenReturn(testVariantResult);

        // When
        ItemVariantResult result = itemVariantService.commitStock(spec);

        // Then
        assertNotNull(result);
        assertEquals(30, testStock.getAllocatedStock()); // 50 - 20
        assertEquals(80, testStock.getAvailableStock()); // 100 - 20
        verify(itemVariantStockRepository, times(1)).save(testStock);
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when committing more than allocated")
    void testCommitStock_MoreThanAllocated() {
        // Given
        testStock.setAvailableStock(100);
        testStock.setAllocatedStock(20);
        VariantStockSpec spec = new VariantStockSpec(1L, 30); // Requesting more than allocated (20)
        when(itemVariantRepository.findById(1L)).thenReturn(Optional.of(testVariant));
        when(itemVariantStockRepository.findByItemVariant_Id(1L)).thenReturn(Optional.of(testStock));

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> itemVariantService.commitStock(spec));
        
        assertTrue(exception.getMessage().contains("Cannot commit more stock than allocated"));
        assertTrue(exception.getMessage().contains("Allocated: 20"));
        assertTrue(exception.getMessage().contains("Requested: 30"));
        verify(itemVariantStockRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should release stock successfully")
    void testReleaseStock_Success() {
        // Given
        testStock.setAvailableStock(100);
        testStock.setAllocatedStock(50);
        VariantStockSpec spec = new VariantStockSpec(1L, 20);
        when(itemVariantRepository.findById(1L)).thenReturn(Optional.of(testVariant));
        when(itemVariantStockRepository.findByItemVariant_Id(1L)).thenReturn(Optional.of(testStock));
        when(itemVariantStockRepository.save(testStock)).thenReturn(testStock);
        when(itemVariantConverter.toItemVariantResult(testVariant)).thenReturn(testVariantResult);

        // When
        ItemVariantResult result = itemVariantService.releaseStock(spec);

        // Then
        assertNotNull(result);
        assertEquals(30, testStock.getAllocatedStock()); // 50 - 20
        assertEquals(100, testStock.getAvailableStock()); // Unchanged
        verify(itemVariantStockRepository, times(1)).save(testStock);
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when releasing more than allocated")
    void testReleaseStock_MoreThanAllocated() {
        // Given
        testStock.setAvailableStock(100);
        testStock.setAllocatedStock(20);
        VariantStockSpec spec = new VariantStockSpec(1L, 30); // Requesting more than allocated (20)
        when(itemVariantRepository.findById(1L)).thenReturn(Optional.of(testVariant));
        when(itemVariantStockRepository.findByItemVariant_Id(1L)).thenReturn(Optional.of(testStock));

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> itemVariantService.releaseStock(spec));
        
        assertTrue(exception.getMessage().contains("Cannot release more stock than allocated"));
        assertTrue(exception.getMessage().contains("Allocated: 20"));
        assertTrue(exception.getMessage().contains("Requested: 30"));
        verify(itemVariantStockRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException when variant not found for stock operations")
    void testStockOperations_VariantNotFound() {
        // Given
        VariantStockSpec spec = new VariantStockSpec(999L, 50);
        when(itemVariantRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(EntityNotFoundException.class, () -> itemVariantService.setManualStock(spec));
        assertThrows(EntityNotFoundException.class, () -> itemVariantService.increaseStock(spec));
        assertThrows(EntityNotFoundException.class, () -> itemVariantService.decreaseStock(spec));
        assertThrows(EntityNotFoundException.class, () -> itemVariantService.reserveStock(spec));
        assertThrows(EntityNotFoundException.class, () -> itemVariantService.commitStock(spec));
        assertThrows(EntityNotFoundException.class, () -> itemVariantService.releaseStock(spec));
    }
}

