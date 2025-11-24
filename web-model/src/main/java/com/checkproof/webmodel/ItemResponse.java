package com.checkproof.webmodel;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemResponse {
  private Long id;
  private String name;
  private String description;
  private String category;
  private Date createdAt;
  private String createdBy;
  private Date updatedAt;
  private String updatedBy;
}
