package com.checkproof.servicemodel;

import lombok.Data;

@Data
public class ItemVariantSpec {
  private Long itemId;
  private String name;
  private String sku;
  private Double price;
}
