package com.checkproof.serviceimpl;

import com.checkproof.helper.converter.ItemConverter;
import com.checkproof.repository.ItemRepository;
import com.checkproof.service.ItemService;
import com.checkproof.servicemodel.ItemResult;
import com.checkproof.servicemodel.ItemSpec;
import com.checkproof.servicemodel.UpdateItemSpec;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class ItemServiceImpl implements ItemService {
  private final ItemRepository itemRepository;
  private final ItemConverter itemConverter;

  @Override
  public List<ItemResult> getListItem() {
    var items = itemRepository.findAll();
    return items.stream().map(itemConverter::toItemResult).toList();
  }

  @Override
  public ItemResult getItem(Long id) {
    var item = itemRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Item Not Found"));
    return itemConverter.toItemResult(item);
  }

  @Override
  public ItemResult createItem(ItemSpec spec) {
    var item = itemConverter.toItemEntity(spec);
    itemRepository.save(item);
    return itemConverter.toItemResult(item);
  }

  @Override
  public ItemResult updateItem(UpdateItemSpec spec) {
    var item = itemRepository.findById(spec.getId()).orElseThrow(() -> new EntityNotFoundException("Item Not Found"));
    item.setItemName(spec.getItemName());
    item.setDescription(spec.getDescription());
    item.setCategory(spec.getCategory());
    itemRepository.save(item);
    return itemConverter.toItemResult(item);
  }

  @Override
  public Void deleteItem(Long id) {
    var item = itemRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Item Not Found"));
    itemRepository.delete(item);
    return null;
  }
}
