package com.github.chunpingwang.ollamaagentoutcome.adapter.web;


import com.github.chunpingwang.ollamaagentoutcome.domain.DuplicateEmailException;
import com.github.chunpingwang.ollamaagentoutcome.domain.InvalidEmailFormatException;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

/**
 * 全域異常處理器，將 Domain Exception 轉譯為 HTTP Status Code。
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ProfileNotFoundException.class)
    ResponseEntity<ErrorDetail> handleNotFound(ProfileNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ErrorDetail.of(ex.getMessage()));
    }

    @ExceptionHandler(DuplicateEmailException.class)
    ResponseEntity<ErrorDetail> handleDuplicateEmail(DuplicateEmailException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ErrorDetail.of(ex.getMessage()));
    }

    @ExceptionHandler(InvalidEmailFormatException.class)
    ResponseEntity<ErrorDetail> handleInvalidEmailFormat(InvalidEmailFormatException ex) {
        return ResponseEntity.badRequest()
                .body(ErrorDetail.of(ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<ErrorDetail> handleValidationErrors(MethodArgumentNotValidException ex) {
        List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();
        var errorDetails = fieldErrors.stream()
            .map(fe -> new ErrorDetail.FieldError(fe.getField(), fe.getDefaultMessage()))
            .toList();
        
        return ResponseEntity.badRequest()
                .body(new ErrorDetail("validation failed", errorDetails));
    }
}
