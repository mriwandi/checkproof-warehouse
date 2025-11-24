package com.checkproof.service;

import com.checkproof.servicemodel.ItemVariantResult;
import com.checkproof.servicemodel.ItemVariantSpec;
import com.checkproof.servicemodel.UpdateItemVariantSpec;
import com.checkproof.servicemodel.VariantStockSpec;

import java.util.List;

public interface ItemVariantService {
  ItemVariantResult createVariant(ItemVariantSpec spec);
  List<ItemVariantResult> getVariantsOfItem(Long itemId);
  ItemVariantResult updateVariant(UpdateItemVariantSpec spec);
  Void deleteVariant(Long variantId);
  ItemVariantResult setManualStock(VariantStockSpec spec);
  ItemVariantResult increaseStock(VariantStockSpec spec);
  ItemVariantResult decreaseStock(VariantStockSpec spec);
  ItemVariantResult reserveStock(VariantStockSpec spec);
  ItemVariantResult commitStock(VariantStockSpec spec);
  ItemVariantResult releaseStock(VariantStockSpec spec);
}
