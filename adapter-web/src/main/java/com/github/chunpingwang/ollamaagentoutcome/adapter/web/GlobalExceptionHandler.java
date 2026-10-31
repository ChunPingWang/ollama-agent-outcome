package com.github.chunpingwang.ollamaagentoutcome.adapter.web;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * 全域異常處理器，將 Domain Exception 與驗證錯誤轉換為 HTTP 回應。
 */
@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(com.github.chunpingwang.ollamaagentoutcome.domain.DuplicateEmailException.class)
    ResponseEntity<Map<String, String>> handleDuplicateEmail(
            com.github.chunpingwang.ollamaagentoutcome.domain.DuplicateEmailException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", "duplicate_email");
        error.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(com.github.chunpingwang.ollamaagentoutcome.domain.ProfileNotFoundException.class)
    ResponseEntity<Map<String, String>> handleNotFound(
            com.github.chunpingwang.ollamaagentoutcome.domain.ProfileNotFoundException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", "not_found");
        error.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(com.github.chunpingwang.ollamaagentoutcome.domain.InvalidEmailFormatException.class)
    ResponseEntity<Map<String, String>> handleInvalidFormat(
            com.github.chunpingwang.ollamaagentoutcome.domain.InvalidEmailFormatException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", "invalid_format");
        error.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<Map<String, Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            errors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("errors", errors);
        return ResponseEntity.badRequest().body(response);
    }
}
