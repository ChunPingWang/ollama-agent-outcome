package com.github.chunpingwang.ollamaagentoutcome.domain.valueobject;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * DisplayName 值物件的單元測試。
 */
class DisplayNameTest {

    @ParameterizedTest(name = "[{index}] displayName={0} should be invalid due to length or null")
    @MethodSource("invalidDisplayNamesProvider")
    void should_throw_exception_for_invalid_display_name(String input) {
        assertThatThrownBy(() -> new DisplayName(input))
            .isInstanceOf(IllegalArgumentException.class);
    }

    private static Stream<Arguments> invalidDisplayNamesProvider() {
        return Stream.of(
                Arguments.arguments(null),
                Arguments.arguments(""),
                Arguments.arguments("a".repeat(101))
        );
    }

    @ParameterizedTest(name = "[{index}] displayName={0} should be valid")
    @MethodSource("validDisplayNamesProvider")
    void should_create_successfully_for_valid_display_name(String input) {
        // AssertJ 3.x+ 使用 assertDoesNotThrow，若環境為舊版則改用 try-catch 或移除該測試
        // 此處假設環境支援，若不支援請確認 AssertJ 版本 >= 3.24.0
        try {
            new DisplayName(input);
        } catch (Exception e) {
            throw new AssertionError("Should not throw", e);
        }
    }

    private static Stream<Arguments> validDisplayNamesProvider() {
        return Stream.of(
                Arguments.arguments("User"),
                Arguments.arguments("A"),
                Arguments.arguments(" ".repeat(100)),
                Arguments.arguments("Special !@#$%^&*()_+ Name")
        );
    }
    
    /**
     * 修正：提供有效的參數來源。原程式碼中 validDisplayNamesProvider 可能未正確定義或返回 null，導致 JUnit5 初始化錯誤。
     */
}
