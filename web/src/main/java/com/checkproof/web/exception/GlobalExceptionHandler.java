package com.checkproof.web.exception;

import com.checkproof.service.exception.OutOfStockException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

  @ExceptionHandler(EntityNotFoundException.class)
  public ResponseEntity<Map<String, Object>> handleEntityNotFound(EntityNotFoundException ex) {
    log.error("Entity not found: {}", ex.getMessage());
    Map<String, Object> response = new HashMap<>();
    response.put("error", "Entity Not Found");
    response.put("message", ex.getMessage());
    response.put("status", HttpStatus.NOT_FOUND.value());
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<Map<String, Object>> handleIllegalArgument(IllegalArgumentException ex) {
    log.error("Illegal argument: {}", ex.getMessage());
    Map<String, Object> response = new HashMap<>();
    response.put("error", "Bad Request");
    response.put("message", ex.getMessage());
    response.put("status", HttpStatus.BAD_REQUEST.value());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
  }

  @ExceptionHandler(OutOfStockException.class)
  public ResponseEntity<Map<String, Object>> handleOutOfStock(OutOfStockException ex) {
    log.error("Out of stock: {}", ex.getMessage());
    Map<String, Object> response = new HashMap<>();
    response.put("error", "Out of Stock");
    response.put("message", ex.getMessage());
    response.put("status", HttpStatus.BAD_REQUEST.value());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<Map<String, Object>> handleValidationExceptions(
      MethodArgumentNotValidException ex) {
    Map<String, String> errors = new HashMap<>();
    ex.getBindingResult().getAllErrors().forEach((error) -> {
      String fieldName = ((FieldError) error).getField();
      String errorMessage = error.getDefaultMessage();
      errors.put(fieldName, errorMessage);
    });
    
    Map<String, Object> response = new HashMap<>();
    response.put("error", "Validation Failed");
    response.put("message", "Invalid input parameters");
    response.put("errors", errors);
    response.put("status", HttpStatus.BAD_REQUEST.value());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
  }

  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<Map<String, Object>> handleConstraintViolation(ConstraintViolationException ex) {
    log.error("Constraint violation: {}", ex.getMessage());
    Map<String, Object> response = new HashMap<>();
    response.put("error", "Validation Failed");
    response.put("message", ex.getMessage());
    response.put("status", HttpStatus.BAD_REQUEST.value());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex) {
    log.error("Unexpected error: ", ex);
    Map<String, Object> response = new HashMap<>();
    response.put("error", "Internal Server Error");
    response.put("message", "An unexpected error occurred");
    response.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
  }
}

