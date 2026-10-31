package com.github.chunpingwang.ollamaagentoutcome.adapter.web;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.github.chunpingwang.ollamaagentoutcome.domain.DuplicateEmailException;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileNotFoundException;

/**
 * 全域例外處理器，將領域例外轉換為 HTTP 狀態碼。
 */
@RestControllerAdvice
class ProfileExceptionHandler {

    @ExceptionHandler(ProfileNotFoundException.class)
    ResponseEntity<Map<String, String>> handleProfileNotFound(ProfileNotFoundException ex) {
        Map<String, String> body = new HashMap<>();
        body.put("error", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    @ExceptionHandler(DuplicateEmailException.class)
    ResponseEntity<Map<String, String>> handleDuplicateEmail(DuplicateEmailException ex) {
        Map<String, String> body = new HashMap<>();
        body.put("error", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<Map<String, String>> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> body = new HashMap<>();
        body.put("error", "Validation failed");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    ResponseEntity<Map<String, String>> handleNotReadable(HttpMessageNotReadableException ex) {
        Map<String, String> body = new HashMap<>();
        body.put("error", "Request body is missing or invalid");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }
}
