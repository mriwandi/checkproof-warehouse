package com.checkproof.servicemodel;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ItemResult {
  private Long id;
  private String name;
  private String description;
  private String category;
  private Date createdAt;
  private String createdBy;
  private Date updatedAt;
  private String updatedBy;
}
