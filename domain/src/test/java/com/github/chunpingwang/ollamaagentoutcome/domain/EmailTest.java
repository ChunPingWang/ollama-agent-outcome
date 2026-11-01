package com.github.chunpingwang.ollamaagentoutcome.domain;


import static org.assertj.core.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EmailTest {

    @ParameterizedTest
    @ValueSource(strings = {"user@example.com", "test.name+tag@domain.co.uk"})
    void should_create_valid_email(String emailStr) {
        Email email = new Email(emailStr);
        assertThat(email.value()).isEqualTo(emailStr.toLowerCase());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"invalid-email", "@example.com", "user@", ""})
    void should_reject_invalid_email(String invalidInput) {
        if (invalidInput == null || invalidInput.isBlank()) {
             // 空字串或空白在 constructor 中會因為 isBlank() 檢查而拋出，或者如果是 null 也會拋出
             // 這裡的 ValueSource 包含 empty string, NullAndEmptySource 處理 null/empty
             // 為了簡化測試邏輯，我們直接呼叫
        }
        
        // 注意：Email constructor 對 null 和 blank 都會拋出 InvalidEmailException
        assertThatThrownBy(() -> new Email(invalidInput))
                .isInstanceOf(InvalidEmailException.class);
    }
    
     @Test
    void should_normalize_case_to_lower() {
        Email email = new Email("User@Test.COM");
        assertThat(email.value()).isEqualTo("user@test.com");
    }
}
