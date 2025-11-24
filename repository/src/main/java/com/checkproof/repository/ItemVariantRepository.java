package com.checkproof.repository;

import com.checkproof.entity.ItemVariant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItemVariantRepository extends JpaRepository<ItemVariant, Long> {
  List<ItemVariant> findByItem_Id(Long itemId);
}
