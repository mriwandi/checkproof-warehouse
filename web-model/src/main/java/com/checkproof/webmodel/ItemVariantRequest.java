package com.checkproof.webmodel;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemVariantRequest {
  @NotBlank(message = "Variant name is required")
  private String name;
  
  @NotBlank(message = "SKU is required")
  private String sku;
  
  @NotNull(message = "Price is required")
  @Positive(message = "Price must be positive")
  private Double price;
}
