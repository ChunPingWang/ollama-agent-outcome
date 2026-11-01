package com.github.chunpingwang.ollamaagentoutcome.adapter.web;

import com.github.chunpingwang.ollamaagentoutcome.domain.DuplicateEmailException;
import com.github.chunpingwang.ollamaagentoutcome.domain.InvalidEmailException;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileNotFoundException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

/**
 * 全域錯誤處理器，將 Domain Exception 與驗證例外轉為對應的 HTTP Status Code。
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ProfileNotFoundException.class)
    ResponseEntity<Void> handleNotFound(ProfileNotFoundException ex) {
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(DuplicateEmailException.class)
    ResponseEntity<Void> handleConflict(DuplicateEmailException ex) {
        return new ResponseEntity<>(HttpStatus.CONFLICT);
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, ConstraintViolationException.class, InvalidEmailException.class})
    ResponseEntity<Void> handleBadRequest(Exception ex) {
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    ResponseEntity<Void> handleMessageNotReadable(HttpMessageNotReadableException ex) {
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
    
    // 不攔截 NoResourceFoundException (404)，讓 Spring Boot 預設行為處理未定義路徑
}
