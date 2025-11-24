package com.checkproof.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "item_variant_stocks")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemVariantStock extends BaseEntity {
  @OneToOne
  @JoinColumn(name="variant_id", nullable = false, unique = true)
  private ItemVariant itemVariant;
  @Column(nullable = false)
  private Integer availableStock = 0;
  @Column(nullable = false)
  private Integer allocatedStock = 0;
  @Version
  private Long version;
}
