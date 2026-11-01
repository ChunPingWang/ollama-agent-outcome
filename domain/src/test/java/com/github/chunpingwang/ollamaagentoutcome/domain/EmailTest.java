package com.github.chunpingwang.ollamaagentoutcome.domain;


import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.UUID;
import org.junit.jupiter.api.Test;

class EmailTest {

    @Test
    void should_create_valid_email() {
        String validEmail = "user@example.com";
        Email email = new Email(validEmail);
        assertThat(email.value()).isEqualTo(validEmail);
    }

    @Test
    void should_reject_null_email() {
        assertThatThrownBy(() -> new Email(null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("email");
    }

    @Test
    void should_reject_blank_email() {
        assertThatThrownBy(() -> new Email(""))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("email");
        
        assertThatThrownBy(() -> new Email("   "))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("email");
    }

    @Test
    void should_reject_invalid_format() {
        // 簡單的正則表達式檢查，不涵蓋所有 RFC5322 邊緣情況，但應拒絕明顯無效格式
        assertThatThrownBy(() -> new Email("invalid-email"))
            .isInstanceOf(InvalidEmailFormatException.class);
            
        assertThatThrownBy(() -> new Email("@example.com"))
            .isInstanceOf(InvalidEmailFormatException.class);
            
        assertThatThrownBy(() -> new Email("user@"))
            .isInstanceOf(InvalidEmailFormatException.class);
    }
    
    @Test
    void should_equality_check_based_on_value() {
        String emailValue = "test@example.com";
        Email e1 = new Email(emailValue);
        Email e2 = new Email(emailValue);
        assertThat(e1).isEqualTo(e2);
        assertThat(e1.hashCode()).isEqualTo(e2.hashCode());
    }
}
