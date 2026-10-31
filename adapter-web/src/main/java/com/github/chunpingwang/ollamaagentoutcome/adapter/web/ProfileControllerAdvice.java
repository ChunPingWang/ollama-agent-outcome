package com.github.chunpingwang.ollamaagentoutcome.adapter.web;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.github.chunpingwang.ollamaagentoutcome.domain.DuplicateEmailException;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileNotFoundException;

/**
 * 全域例外處理 Advice。
 */
@RestControllerAdvice
class ProfileControllerAdvice {

    @ExceptionHandler(ProfileNotFoundException.class)
    ResponseEntity<Map<String, String>> handleNotFound(ProfileNotFoundException ex) {
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
        body.put("error", "輸入驗證失敗");
        ex.getBindingResult().getFieldErrors().forEach(e ->
            body.put(e.getField(), e.getDefaultMessage())
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }
}
