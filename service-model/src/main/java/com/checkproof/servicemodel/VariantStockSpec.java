package com.checkproof.servicemodel;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class VariantStockSpec {
  private Long variantId;
  private Integer quantity;
}
