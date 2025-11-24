package com.checkproof.web;

import com.checkproof.helper.converter.ItemVariantConverter;
import com.checkproof.service.ItemVariantService;
import com.checkproof.servicemodel.UpdateItemVariantSpec;
import com.checkproof.webmodel.ItemResponse;
import com.checkproof.webmodel.ItemVariantRequest;
import com.checkproof.webmodel.ItemVariantResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.Valid;

@RestController
@RequestMapping(value = "api/v1/variants")
@AllArgsConstructor
@Slf4j
public class VariantController {
  private final ItemVariantService itemVariantService;
  private final ItemVariantConverter itemVariantConverter;

  @PutMapping("/{id}")
  public ResponseEntity<ItemVariantResponse> updateVariant(@PathVariable Long id, @Valid @RequestBody ItemVariantRequest body) {
    var request = itemVariantConverter.toUpdateItemVariantSpec(body);
    request.setId(id);
    var result = itemVariantService.updateVariant(request);
    return ResponseEntity.ok(itemVariantConverter.toItemVariantResponse(result));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Object> deleteVariant(@PathVariable Long id) {
    var result = itemVariantService.deleteVariant(id);
    return ResponseEntity.ok(result);
  }
}
