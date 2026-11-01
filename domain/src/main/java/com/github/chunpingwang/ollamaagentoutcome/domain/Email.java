package com.github.chunpingwang.ollamaagentoutcome.domain;

/**
 * Email address value object with validation rules.
 */
public record Email(String value) {
    public Email {
        if (value == null || value.isBlank()) {
            throw new InvalidEmailException("email must not be empty or blank");
        }
        
        int atIndex = value.indexOf('@');
        boolean hasAt = value.contains("@");
        int lastAtIndex = value.lastIndexOf('@');
        
        // Check for missing @ symbol entirely
        if (!hasAt) {
            throw new InvalidEmailException("missing @ in email address");
        }
        
        // Check for multiple @ symbols or starting with @
        if (atIndex <= 0 || lastAtIndex != atIndex) {
             String localPart = value.substring(0, atIndex);
             if (localPart.isEmpty()) {
                 throw new InvalidEmailException("missing local part in email address");
             } else {
                 // Multiple @ signs case
                 throw new InvalidEmailException("invalid format: too many @ symbols");
             }
        }
        
        String localPart = value.substring(0, atIndex);
        String domainPart = value.substring(atIndex + 1);
        
        if (domainPart.isEmpty()) {
            throw new InvalidEmailException("missing domain in email address");
        }
    }
}
