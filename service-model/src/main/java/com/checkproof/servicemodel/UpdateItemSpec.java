package com.checkproof.servicemodel;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class UpdateItemSpec extends ItemSpec {
  private Long id;
}
