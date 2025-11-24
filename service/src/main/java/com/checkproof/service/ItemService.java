package com.checkproof.service;

import com.checkproof.servicemodel.ItemResult;
import com.checkproof.servicemodel.ItemSpec;
import com.checkproof.servicemodel.UpdateItemSpec;

import java.util.List;

public interface ItemService {
  List<ItemResult> getListItem();
  ItemResult getItem(Long id);
  ItemResult createItem(ItemSpec spec);
  ItemResult updateItem(UpdateItemSpec spec);
  Void deleteItem(Long id);
}
