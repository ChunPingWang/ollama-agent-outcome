package com.github.chunpingwang.ollamaagentoutcome.adapter.web;

import com.github.chunpingwang.ollamaagentoutcome.domain.DuplicateEmailException;
import com.github.chunpingwang.ollamaagentoutcome.domain.InvalidEmailFormatException;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 全域例外處理器，將 Domain Exception 與 Validation Exception 轉換為正確的 HTTP 狀態碼。
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 處理 Jakarta Validation 失敗 (400 Bad Request)。
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<Map<String, Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors()
            .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
        
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("error", "Bad Request");
        body.put("details", errors);
        
        return ResponseEntity.badRequest().body(body);
    }

    /**
     * 處理 JSON 解析錯誤 (400 Bad Request)。
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    ResponseEntity<Map<String, Object>> handleMessageNotReadable(HttpMessageNotReadableException ex) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("error", "Bad Request");
        body.put("message", "Malformed JSON request");
        return ResponseEntity.badRequest().body(body);
    }

    /**
     * 處理重複電子郵件衝突 (409 Conflict)。
     */
    @ExceptionHandler(DuplicateEmailException.class)
    ResponseEntity<Map<String, Object>> handleDuplicateEmail(DuplicateEmailException ex) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("error", "Conflict");
        body.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    /**
     * 處理無效的 Email 格式 (Domain Exception -> 400 Bad Request)。
     */
    @ExceptionHandler(InvalidEmailFormatException.class)
    ResponseEntity<Map<String, Object>> handleInvalidEmailFormat(InvalidEmailFormatException ex) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("error", "Bad Request");
        body.put("message", ex.getMessage());
        return ResponseEntity.badRequest().body(body);
    }

    /**
     * 處理找不到個人資料 (404 Not Found)。
     */
    @ExceptionHandler(ProfileNotFoundException.class)
    ResponseEntity<Map<String, Object>> handleProfileNotFound(ProfileNotFoundException ex) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("error", "Not Found");
        body.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }
}
