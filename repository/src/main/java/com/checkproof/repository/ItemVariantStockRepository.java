package com.checkproof.repository;

import com.checkproof.entity.ItemVariantStock;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ItemVariantStockRepository extends JpaRepository<ItemVariantStock, Long> {
  Optional<ItemVariantStock> findByItemVariant_Id(Long variantId);
}

