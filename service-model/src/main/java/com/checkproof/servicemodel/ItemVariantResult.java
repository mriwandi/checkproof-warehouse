package com.checkproof.servicemodel;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemVariantResult {
  private Long id;
  private ItemResult item;
  private String name;
  private String sku;
  private Double price;
  private Date createdAt;
  private String createdBy;
  private Date updatedAt;
  private String updatedBy;
}
