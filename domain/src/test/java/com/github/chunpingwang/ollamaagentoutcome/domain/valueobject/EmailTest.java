package com.github.chunpingwang.ollamaagentoutcome.domain.valueobject;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * Email 值物件的單元測試。
 */
class EmailTest {

    @ParameterizedTest(name = "[{index}] email={0} should be invalid")
    @MethodSource("invalidEmailsProvider")
    void should_throw_exception_for_invalid_email(String input) {
        assertThatThrownBy(() -> new Email(input))
            .isInstanceOf(IllegalArgumentException.class);
    }

    private static Stream<Arguments> invalidEmailsProvider() {
        return Stream.of(
                Arguments.arguments(null),
                Arguments.arguments(""),
                Arguments.arguments("user@"),
                Arguments.arguments("@domain.com"),
                Arguments.arguments("user@domain"),
                Arguments.arguments("user name@domain.com"),
                Arguments.arguments("user@@domain.com")
        );
    }

    @ParameterizedTest(name = "[{index}] email={0} should be valid")
    @MethodSource("validEmailsProvider")
    void should_create_successfully_for_valid_email(String input) {
        // AssertJ 3.x+ 使用 assertDoesNotThrow，若環境為舊版則改用 try-catch 或移除該測試
        // 此處假設環境支援，若不支援請確認 AssertJ 版本 >= 3.24.0
        try {
            new Email(input);
        } catch (Exception e) {
            throw new AssertionError("Should not throw", e);
        }
    }

    private static Stream<Arguments> validEmailsProvider() {
        return Stream.of(
                Arguments.arguments("user@example.com"),
                Arguments.arguments("test.email+tag@domain.co.jp")
        );
    }
    
    /**
     * 修正：提供有效的參數來源。原程式碼中 validEmailsProvider 可能未正確定義或返回 null，導致 JUnit5 初始化錯誤。
     */
}
