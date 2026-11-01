package com.github.chunpingwang.ollamaagentoutcome.adapter.web;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import com.github.chunpingwang.ollamaagentoutcome.domain.DuplicateEmailException;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileNotFoundException;

/**
 * 全域例外處理器，將 Domain Exception 與驗證錯誤轉換為對應的 HTTP 狀態碼。
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ProfileNotFoundException.class)
    public ResponseEntity<String> handleProfileNotFound(ProfileNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(DuplicateEmailException.class)
    public ResponseEntity<String> handleDuplicateEmail(DuplicateEmailException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<List<String>> handleValidationErrors(MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult().getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.toList());
        return ResponseEntity.badRequest().body(errors);
    }
    
    // 處理無效 JSON 結構 (例如缺少大括號或語法錯誤)，這會拋出 HttpMessageNotReadableException
    // 但測試中發送 "{}" 是合法 JSON，只是欄位缺失，所以由 MethodArgumentNotValidException 處理。
    // 若需更嚴格的保護，可加入以下 handler，但根據 Spec "不要 catch Exception.class"，我們只捕捉特定型別。
    // Spring Boot 4 / WebMvcTest 對於完全無法解析的 JSON 可能會拋出其他例外，這裡暫不額外增加通用 Catcher 
    // 以符合骨架要求的不攔截所有 Exception 原則。
}
