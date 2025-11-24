package com.checkproof.web;

import com.checkproof.helper.converter.ItemConverter;
import com.checkproof.helper.converter.ItemVariantConverter;
import com.checkproof.service.ItemService;
import com.checkproof.service.ItemVariantService;
import com.checkproof.webmodel.ItemRequest;
import com.checkproof.webmodel.ItemResponse;
import com.checkproof.webmodel.ItemVariantRequest;
import com.checkproof.webmodel.ItemVariantResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping(value = "api/v1/items")
@AllArgsConstructor
public class ItemController {
  private final ItemService itemService;
  private final ItemConverter itemConverter;
  private final ItemVariantService itemVariantService;
  private final ItemVariantConverter itemVariantConverter;

  @GetMapping
  public ResponseEntity<List<ItemResponse>> getListItems() {
    var result = itemService.getListItem();
    return ResponseEntity.ok(result.stream().map(itemConverter::toItemResponse).toList());
  }

  @GetMapping("/{id}")
  public ResponseEntity<ItemResponse> getItem(@PathVariable Long id) {
    var result = itemService.getItem(id);
    return ResponseEntity.ok(itemConverter.toItemResponse(result));
  }

  @PostMapping
  public ResponseEntity<ItemResponse> createItem(@Valid @RequestBody ItemRequest body) {
    var request = itemConverter.toItemSpec(body);
    var result = itemService.createItem(request);
    return ResponseEntity.ok(itemConverter.toItemResponse(result));
  }

  @PutMapping("/{id}")
  public ResponseEntity<ItemResponse> updateItem(@PathVariable Long id, @Valid @RequestBody ItemRequest body) {
    var request = itemConverter.toUpdateItemSpec(body);
    request.setId(id);
    var result = itemService.updateItem(request);
    return ResponseEntity.ok(itemConverter.toItemResponse(result));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Object> deleteItem(@PathVariable Long id) {
    var result = itemService.deleteItem(id);
    return ResponseEntity.ok(result);
  }

  @PostMapping("/{id}/variants")
  public ResponseEntity<ItemVariantResponse> createVariant(@PathVariable Long id, @Valid @RequestBody ItemVariantRequest body) {
    var request = itemVariantConverter.toItemVariantSpec(body);
    request.setItemId(id);
    var result = itemVariantService.createVariant(request);
    return ResponseEntity.ok(itemVariantConverter.toItemVariantResponse(result));
  }

  @GetMapping("/{itemId}/variants")
  public ResponseEntity<List<ItemVariantResponse>> getVariantsOfItem(@PathVariable Long itemId) {
    var result = itemVariantService.getVariantsOfItem(itemId);
    return ResponseEntity.ok(result.stream().map(itemVariantConverter::toItemVariantResponse).toList());
  }
}
