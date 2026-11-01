package com.github.chunpingwang.ollamaagentoutcome.adapter.web;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import com.github.chunpingwang.ollamaagentoutcome.domain.DuplicateEmailException;
import com.github.chunpingwang.ollamaagentoutcome.domain.InvalidDisplayNameException;
import com.github.chunpingwang.ollamaagentoutcome.domain.InvalidEmailException;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileNotFoundException;

import tools.jackson.databind.ObjectMapper;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @ExceptionHandler(ProfileNotFoundException.class)
    ResponseEntity<Void> handleNotFound(ProfileNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @ExceptionHandler(DuplicateEmailException.class)
    ResponseEntity<Void> handleConflict(DuplicateEmailException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).build();
    }

    @ExceptionHandler({InvalidEmailException.class, InvalidDisplayNameException.class})
    ResponseEntity<Void> handleBadRequest(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        StringBuilder sb = new StringBuilder("Validation failed: ");
        ex.getBindingResult().getFieldErrors()
           .forEach(e -> sb.append(e.getField()).append(": ").append(e.getDefaultMessage()).append("; "));
        return ResponseEntity.badRequest().body(sb.toString());
    }

    @ExceptionHandler(NoResourceFoundException.class)
    ResponseEntity<Void> handleNoResourceFound(NoResourceFoundException ex) {
        // Let Spring's default 404 handling for unknown paths pass through or explicitly return 404
        return ResponseEntity.notFound().build();
    }
}
