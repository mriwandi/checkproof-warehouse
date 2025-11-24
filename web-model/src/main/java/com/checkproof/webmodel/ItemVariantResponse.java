package com.checkproof.webmodel;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class ItemVariantResponse {
  private Long id;
  private ItemResponse item;
  private String name;
  private String sku;
  private Double price;
  private Date createdAt;
  private String createdBy;
  private Date updatedAt;
  private String updatedBy;
}
