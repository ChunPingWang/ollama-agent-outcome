package com.github.chunpingwang.ollamaagentoutcome.adapter.web;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import com.github.chunpingwang.ollamaagentoutcome.domain.DuplicateEmailException;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileNotFoundException;

/**
 * Global exception handler mapping domain and framework exceptions to HTTP status codes.
 */
@RestControllerAdvice
public class WebExceptionHandler {

    @ExceptionHandler(DuplicateEmailException.class)
    ResponseEntity<String> handleDuplicateEmail(DuplicateEmailException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }

    @ExceptionHandler(ProfileNotFoundException.class)
    ResponseEntity<String> handleNotFound(ProfileNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<String> handleValidation(MethodArgumentNotValidException ex) {
        var message = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .reduce((a, b) -> a + "; " + b)
                .orElse("Invalid input");
        return ResponseEntity.badRequest().body(message);
    }

    // Explicitly ignore NoResourceFoundException (404 for unknown paths) to avoid converting it to 500 or custom format.
}
