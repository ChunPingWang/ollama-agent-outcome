package com.github.chunpingwang.ollamaagentoutcome.adapter.web;

import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.github.chunpingwang.ollamaagentoutcome.domain.DuplicateEmailException;
import com.github.chunpingwang.ollamaagentoutcome.domain.InvalidEmailException;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileNotFoundException;

/**
 * 全域異常處理器，將領域例外映射為 HTTP 狀態碼。
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ProfileNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(ProfileNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(DuplicateEmailException.class)
    public ResponseEntity<ErrorResponse> handleConflict(DuplicateEmailException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(InvalidEmailException.class)
    public ResponseEntity<ErrorResponse> handleBadRequest(InvalidEmailException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        String errors = ex.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining("; "));
        ValidationErrorResponse response = new ValidationErrorResponse(errors);
        return ResponseEntity.badRequest().body(response);
    }

    /**
     * 一般錯誤回應結構。
     */
    static class ErrorResponse {
        private final String message;

        public ErrorResponse(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }
    }

    /**
     * 驗證錯誤回應結構。
     */
    record ValidationErrorResponse(String message) {}
}
