package com.checkproof.web;

import com.checkproof.helper.converter.ItemVariantConverter;
import com.checkproof.service.ItemVariantService;
import com.checkproof.servicemodel.VariantStockSpec;
import com.checkproof.webmodel.ItemVariantResponse;
import com.checkproof.webmodel.VariantStockRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.Valid;

@RestController
@RequestMapping(value = "api/v1/variants/{variantId}/stock")
@AllArgsConstructor
@Slf4j
public class StockManagementController {
  private final ItemVariantService itemVariantService;
  private final ItemVariantConverter itemVariantConverter;

  @PutMapping
  public ResponseEntity<ItemVariantResponse> setManualStock(@PathVariable Long variantId,
      @Valid @RequestBody VariantStockRequest body) {
    var request = new VariantStockSpec(variantId, body.getQuantity());
    var result = itemVariantService.setManualStock(request);
    return ResponseEntity.ok(itemVariantConverter.toItemVariantResponse(result));
  }

  @PostMapping("/increase")
  public ResponseEntity<ItemVariantResponse> increaseStock(@PathVariable Long variantId,
      @Valid @RequestBody VariantStockRequest body) {
    var request = new VariantStockSpec(variantId, body.getQuantity());
    var result = itemVariantService.increaseStock(request);
    return ResponseEntity.ok(itemVariantConverter.toItemVariantResponse(result));
  }

  @PostMapping("/decrease")
  public ResponseEntity<ItemVariantResponse> decreaseStock(@PathVariable Long variantId,
      @Valid @RequestBody VariantStockRequest body) {
    var request = new VariantStockSpec(variantId, body.getQuantity());
    var result = itemVariantService.decreaseStock(request);
    return ResponseEntity.ok(itemVariantConverter.toItemVariantResponse(result));
  }

  @PostMapping("/reserve")
  public ResponseEntity<ItemVariantResponse> reserveStock(@PathVariable Long variantId,
      @Valid @RequestBody VariantStockRequest body) {
    log.info("Reserving stock for variant {} with quantity {}", variantId, body.getQuantity());
    var request = new VariantStockSpec(variantId, body.getQuantity());
    var result = itemVariantService.reserveStock(request);
    log.info("Stock reserved successfully for variant {}", variantId);
    return ResponseEntity.ok(itemVariantConverter.toItemVariantResponse(result));
  }

  @PostMapping("/commit")
  public ResponseEntity<ItemVariantResponse> commitStock(@PathVariable Long variantId,
      @Valid @RequestBody VariantStockRequest body) {
    log.info("Committing stock for variant {} with quantity {}", variantId, body.getQuantity());
    var request = new VariantStockSpec(variantId, body.getQuantity());
    var result = itemVariantService.commitStock(request);
    log.info("Stock committed successfully for variant {}", variantId);
    return ResponseEntity.ok(itemVariantConverter.toItemVariantResponse(result));
  }

  @PostMapping("/release")
  public ResponseEntity<ItemVariantResponse> releaseStock(@PathVariable Long variantId,
      @Valid @RequestBody VariantStockRequest body) {
    log.info("Releasing stock for variant {} with quantity {}", variantId, body.getQuantity());
    var request = new VariantStockSpec(variantId, body.getQuantity());
    var result = itemVariantService.releaseStock(request);
    log.info("Stock released successfully for variant {}", variantId);
    return ResponseEntity.ok(itemVariantConverter.toItemVariantResponse(result));
  }
}
