package com.checkproof.webmodel;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemRequest {
  @NotBlank(message = "Item name is required")
  private String itemName;
  
  @NotBlank(message = "Description is required")
  private String description;
  
  private String category;
}
