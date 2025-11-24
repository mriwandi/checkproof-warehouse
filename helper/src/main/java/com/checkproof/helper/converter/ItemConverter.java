package com.checkproof.helper.converter;

import com.checkproof.entity.Item;
import com.checkproof.servicemodel.ItemResult;
import com.checkproof.servicemodel.ItemSpec;
import com.checkproof.servicemodel.UpdateItemSpec;
import com.checkproof.webmodel.ItemRequest;
import com.checkproof.webmodel.ItemResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ItemConverter {
  ItemResponse toItemResponse(ItemResult spec);
  
  @Mapping(target = "name", source = "itemName")
  @Mapping(target = "createdAt", expression = "java(entity.getCreatedAt() != null ? java.sql.Timestamp.valueOf(entity.getCreatedAt()) : null)")
  @Mapping(target = "updatedAt", expression = "java(entity.getUpdatedAt() != null ? java.sql.Timestamp.valueOf(entity.getUpdatedAt()) : null)")
  ItemResult toItemResult(Item entity);
  
  ItemSpec toItemSpec(ItemRequest request);

  @Mapping(target = "id", ignore = true)
  UpdateItemSpec toUpdateItemSpec(ItemRequest request);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "createdBy", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  @Mapping(target = "updatedBy", ignore = true)
  @Mapping(target = "itemVariants", ignore = true)
  Item toItemEntity(ItemSpec spec);
}
