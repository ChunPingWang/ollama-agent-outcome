package com.github.chunpingwang.ollamaagentoutcome.domain;


import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class EmailTest {

    @Test
    void should_create_valid_email() {
        Email email = new Email("user@example.com");
        assertThat(email.address()).isEqualTo("user@example.com");
    }

    @Test
    void should_reject_invalid_email_format() {
        assertThatThrownBy(() -> new Email("invalid-email"))
            .isInstanceOf(InvalidEmailFormatException.class)
            .hasMessageContaining("invalid format");
            
        assertThatThrownBy(() -> new Email(""))
            .isInstanceOf(IllegalArgumentException.class);
            
        assertThatThrownBy(() -> new Email(null))
            .isInstanceOf(NullPointerException.class);
    }
    
    @Test
    void should_normalize_case_if_needed_or_preserve() {
         // Assuming case preservation or lowercasing is a business rule, let's just test valid input works
         Email email = new Email("User@Example.COM");
         assertThat(email.address()).isEqualTo("User@Example.COM"); 
    }
}
