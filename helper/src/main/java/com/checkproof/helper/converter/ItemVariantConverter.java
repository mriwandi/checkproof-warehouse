package com.checkproof.helper.converter;

import com.checkproof.entity.ItemVariant;
import com.checkproof.servicemodel.ItemVariantResult;
import com.checkproof.servicemodel.ItemVariantSpec;
import com.checkproof.servicemodel.UpdateItemVariantSpec;
import com.checkproof.webmodel.ItemVariantRequest;
import com.checkproof.webmodel.ItemVariantResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = ItemConverter.class)
public interface ItemVariantConverter {
  @Mapping(target = "itemId", ignore = true)
  ItemVariantSpec toItemVariantSpec(ItemVariantRequest request);
  
  ItemVariantResponse toItemVariantResponse(ItemVariantResult result);
  
  @Mapping(target = "createdAt", expression = "java(entity.getCreatedAt() != null ? java.sql.Timestamp.valueOf(entity.getCreatedAt()) : null)")
  @Mapping(target = "updatedAt", expression = "java(entity.getUpdatedAt() != null ? java.sql.Timestamp.valueOf(entity.getUpdatedAt()) : null)")
  ItemVariantResult toItemVariantResult(ItemVariant entity);
  
  @Mapping(target = "itemId", ignore = true)
  @Mapping(target = "id", ignore = true)
  UpdateItemVariantSpec toUpdateItemVariantSpec(ItemVariantRequest request);
  
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "item", ignore = true)
  @Mapping(target = "version", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "createdBy", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  @Mapping(target = "updatedBy", ignore = true)
  ItemVariant toItemVariantEntity(ItemVariantSpec spec);
}
