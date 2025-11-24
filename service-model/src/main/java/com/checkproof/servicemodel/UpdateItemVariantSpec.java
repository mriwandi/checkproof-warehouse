package com.checkproof.servicemodel;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class UpdateItemVariantSpec extends ItemVariantSpec {
  private Long id;
}
