package com.checkproof.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "item_variants")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemVariant extends BaseEntity {
  @ManyToOne(optional = false)
  @JoinColumn(name="item_id")
  private Item item;
  @Column(nullable = false)
  private String name;
  @Column(nullable = false, unique = true)
  private String sku;
  @Column(nullable = false)
  private Double price;
  @Version
  private Long version;
}
