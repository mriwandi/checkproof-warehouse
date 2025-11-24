package com.checkproof.serviceimpl;

import com.checkproof.entity.Item;
import com.checkproof.entity.ItemVariant;
import com.checkproof.entity.ItemVariantStock;
import com.checkproof.helper.converter.ItemVariantConverter;
import com.checkproof.repository.ItemRepository;
import com.checkproof.repository.ItemVariantRepository;
import com.checkproof.repository.ItemVariantStockRepository;
import com.checkproof.service.ItemVariantService;
import com.checkproof.servicemodel.ItemVariantResult;
import com.checkproof.servicemodel.ItemVariantSpec;
import com.checkproof.servicemodel.UpdateItemVariantSpec;
import com.checkproof.servicemodel.VariantStockSpec;
import com.checkproof.service.exception.OutOfStockException;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
public class ItemVariantServiceImpl implements ItemVariantService {
  private final ItemVariantRepository itemVariantRepository;
  private final ItemVariantStockRepository itemVariantStockRepository;
  private final ItemVariantConverter itemVariantConverter;
  private final ItemRepository itemRepository;

  @Override
  @Transactional
  public ItemVariantResult createVariant(ItemVariantSpec spec) {
    var item = itemRepository.findById(spec.getItemId())
        .orElseThrow(() -> new EntityNotFoundException("Item not found"));
    var entity = itemVariantConverter.toItemVariantEntity(spec);
    entity.setItem(item);
    var result = itemVariantRepository.save(entity);
    
    var stock = new ItemVariantStock();
    stock.setItemVariant(result);
    stock.setAvailableStock(0);
    stock.setAllocatedStock(0);
    itemVariantStockRepository.save(stock);
    
    return itemVariantConverter.toItemVariantResult(result);
  }

  @Override
  public List<ItemVariantResult> getVariantsOfItem(Long itemId) {
    var variants = itemVariantRepository.findByItem_Id(itemId);
    return variants.stream().map(itemVariantConverter::toItemVariantResult).toList();
  }

  @Override
  @Transactional
  public ItemVariantResult updateVariant(UpdateItemVariantSpec spec) {
    var variant = itemVariantRepository.findById(spec.getId())
        .orElseThrow(() -> new EntityNotFoundException("item_variant not found"));
    variant.setName(spec.getName());
    variant.setPrice(spec.getPrice());
    variant.setSku(spec.getSku());
    itemVariantRepository.save(variant);
    return itemVariantConverter.toItemVariantResult(variant);
  }

  @Override
  @Transactional
  public Void deleteVariant(Long variantId) {
    var variant = itemVariantRepository.findById(variantId)
        .orElseThrow(() -> new EntityNotFoundException("item_variant not found"));
    
    itemVariantStockRepository.findByItemVariant_Id(variantId)
        .ifPresent(itemVariantStockRepository::delete);
    
    itemVariantRepository.delete(variant);
    return null;
  }

  @Override
  @Transactional
  public ItemVariantResult setManualStock(VariantStockSpec spec) {
    var variant = itemVariantRepository.findById(spec.getVariantId())
        .orElseThrow(() -> new EntityNotFoundException("item_variant not found"));
    
    var stock = itemVariantStockRepository.findByItemVariant_Id(spec.getVariantId())
        .orElseGet(() -> {
          var newStock = new ItemVariantStock();
          newStock.setItemVariant(variant);
          return newStock;
        });
    
    stock.setAvailableStock(spec.getQuantity());
    stock.setAllocatedStock(0);
    itemVariantStockRepository.save(stock);
    
    return itemVariantConverter.toItemVariantResult(variant);
  }

  @Override
  @Transactional
  public ItemVariantResult increaseStock(VariantStockSpec spec) {
    var variant = itemVariantRepository.findById(spec.getVariantId())
        .orElseThrow(() -> new EntityNotFoundException("item_variant not found"));
    
    var stock = itemVariantStockRepository.findByItemVariant_Id(spec.getVariantId())
        .orElseGet(() -> {
          var newStock = new ItemVariantStock();
          newStock.setItemVariant(variant);
          newStock.setAvailableStock(0);
          newStock.setAllocatedStock(0);
          return newStock;
        });
    
    stock.setAvailableStock(stock.getAvailableStock() + spec.getQuantity());
    itemVariantStockRepository.save(stock);
    
    return itemVariantConverter.toItemVariantResult(variant);
  }

  @Override
  @Transactional
  public ItemVariantResult decreaseStock(VariantStockSpec spec) {
    var variant = itemVariantRepository.findById(spec.getVariantId())
        .orElseThrow(() -> new EntityNotFoundException("item_variant not found"));
    
    var stock = itemVariantStockRepository.findByItemVariant_Id(spec.getVariantId())
        .orElseThrow(() -> new EntityNotFoundException("stock not found for variant"));
    
    int availableQuantity = stock.getAvailableStock() - stock.getAllocatedStock();
    if (availableQuantity < spec.getQuantity()) {
      throw new OutOfStockException(
          String.format("Insufficient stock. Available: %d, Requested: %d", 
              availableQuantity, spec.getQuantity()));
    }
    
    stock.setAvailableStock(stock.getAvailableStock() - spec.getQuantity());
    itemVariantStockRepository.save(stock);
    
    return itemVariantConverter.toItemVariantResult(variant);
  }

  @Override
  @Transactional
  public ItemVariantResult reserveStock(VariantStockSpec spec) {
    var variant = itemVariantRepository.findById(spec.getVariantId())
        .orElseThrow(() -> new EntityNotFoundException("item_variant not found"));
    
    var stock = itemVariantStockRepository.findByItemVariant_Id(spec.getVariantId())
        .orElseThrow(() -> new EntityNotFoundException("stock not found for variant"));
    
    int availableQuantity = stock.getAvailableStock() - stock.getAllocatedStock();
    if (availableQuantity < spec.getQuantity()) {
      throw new OutOfStockException(
          String.format("Insufficient stock to reserve. Available: %d, Requested: %d", 
              availableQuantity, spec.getQuantity()));
    }
    
    stock.setAllocatedStock(stock.getAllocatedStock() + spec.getQuantity());
    itemVariantStockRepository.save(stock);
    
    return itemVariantConverter.toItemVariantResult(variant);
  }

  @Override
  @Transactional
  public ItemVariantResult commitStock(VariantStockSpec spec) {
    var variant = itemVariantRepository.findById(spec.getVariantId())
        .orElseThrow(() -> new EntityNotFoundException("item_variant not found"));
    
    var stock = itemVariantStockRepository.findByItemVariant_Id(spec.getVariantId())
        .orElseThrow(() -> new EntityNotFoundException("stock not found for variant"));
    
    if (stock.getAllocatedStock() < spec.getQuantity()) {
      throw new IllegalArgumentException(
          String.format("Cannot commit more stock than allocated. Allocated: %d, Requested: %d", 
              stock.getAllocatedStock(), spec.getQuantity()));
    }
    
    // Commit: decrease both availableStock and allocatedStock
    stock.setAvailableStock(stock.getAvailableStock() - spec.getQuantity());
    stock.setAllocatedStock(stock.getAllocatedStock() - spec.getQuantity());
    itemVariantStockRepository.save(stock);
    
    return itemVariantConverter.toItemVariantResult(variant);
  }

  @Override
  @Transactional
  public ItemVariantResult releaseStock(VariantStockSpec spec) {
    var variant = itemVariantRepository.findById(spec.getVariantId())
        .orElseThrow(() -> new EntityNotFoundException("item_variant not found"));
    
    var stock = itemVariantStockRepository.findByItemVariant_Id(spec.getVariantId())
        .orElseThrow(() -> new EntityNotFoundException("stock not found for variant"));
    
    if (stock.getAllocatedStock() < spec.getQuantity()) {
      throw new IllegalArgumentException(
          String.format("Cannot release more stock than allocated. Allocated: %d, Requested: %d", 
              stock.getAllocatedStock(), spec.getQuantity()));
    }
    
    stock.setAllocatedStock(stock.getAllocatedStock() - spec.getQuantity());
    itemVariantStockRepository.save(stock);
    
    return itemVariantConverter.toItemVariantResult(variant);
  }
}
