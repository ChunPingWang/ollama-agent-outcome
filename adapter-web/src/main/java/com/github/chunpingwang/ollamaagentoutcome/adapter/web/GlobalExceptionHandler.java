package com.github.chunpingwang.ollamaagentoutcome.adapter.web;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.github.chunpingwang.ollamaagentoutcome.domain.DuplicateEmailException;
import com.github.chunpingwang.ollamaagentoutcome.domain.InvalidEmailException;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileNotFoundException;

import java.util.stream.Collectors;

/**
 * 全域例外處理器，將領域例外與驗證異常轉換為對應的 HTTP 狀態碼。
 */
@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(DuplicateEmailException.class)
    ResponseEntity<ErrorDto> handleDuplicateEmail(DuplicateEmailException ex) {
        return new ResponseEntity<>(new ErrorDto("Email already exists"), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(ProfileNotFoundException.class)
    ResponseEntity<ErrorDto> handleNotFound(ProfileNotFoundException ex) {
        return new ResponseEntity<>(new ErrorDto(ex.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InvalidEmailException.class)
    ResponseEntity<ErrorDto> handleInvalidEmail(InvalidEmailException ex) {
        return new ResponseEntity<>(new ErrorDto(ex.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<ErrorDto> handleValidationErrors(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        return new ResponseEntity<>(new ErrorDto(message), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    ResponseEntity<ErrorDto> handleMessageNotReadable(HttpMessageNotReadableException ex) {
        return new ResponseEntity<>(new ErrorDto("Malformed JSON request"), HttpStatus.BAD_REQUEST);
    }
}
