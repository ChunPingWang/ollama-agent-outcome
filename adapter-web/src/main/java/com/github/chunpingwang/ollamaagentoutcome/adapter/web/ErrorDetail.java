package com.github.chunpingwang.ollamaagentoutcome.adapter.web;

import java.util.List;

/**
 * 錯誤回應格式。
 */
public record ErrorDetail(
    String message,
    List<FieldError> errors
) {
    public record FieldError(String field, String reason) {}
    
    public static ErrorDetail of(String message) {
        return new ErrorDetail(message, null);
    }
    
    public static ErrorDetail ofValidationErrors(List<org.springframework.validation.FieldError> fieldErrors) {
        var details = fieldErrors.stream()
            .map(fe -> new FieldError(fe.getField(), fe.getDefaultMessage()))
            .toList();
        return new ErrorDetail("validation failed", details);
    }
}
