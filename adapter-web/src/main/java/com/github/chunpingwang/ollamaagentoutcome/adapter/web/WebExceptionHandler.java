package com.github.chunpingwang.ollamaagentoutcome.adapter.web;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.github.chunpingwang.ollamaagentoutcome.domain.DuplicateEmailException;
import com.github.chunpingwang.ollamaagentoutcome.domain.InvalidEmailException;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileNotFoundException;

import java.util.HashMap;
import java.util.Map;

/**
 * Web 層的全域例外處理器，將 Domain/Application 例外映射為 HTTP 狀態碼。
 */
@RestControllerAdvice(annotations = {org.springframework.web.bind.annotation.RestController.class})
public class WebExceptionHandler {

    @ExceptionHandler(ProfileNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleNotFound(ProfileNotFoundException ex) {
        Map<String, String> body = new HashMap<>();
        body.put("error", "Profile not found");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    @ExceptionHandler(DuplicateEmailException.class)
    public ResponseEntity<Map<String, String>> handleConflict(DuplicateEmailException ex) {
        Map<String, String> body = new HashMap<>();
        body.put("error", "Duplicate email address");
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    @ExceptionHandler(InvalidEmailException.class)
    public ResponseEntity<Map<String, String>> handleBadRequest(InvalidEmailException ex) {
        Map<String, String> body = new HashMap<>();
        body.put("error", "Invalid email format");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, Object> errors = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            // 使用 field name 作為 key，錯誤訊息作為 value
            errors.put(error.getField(), error.getDefaultMessage());
        }
        return ResponseEntity.badRequest().body(errors);
    }
}
