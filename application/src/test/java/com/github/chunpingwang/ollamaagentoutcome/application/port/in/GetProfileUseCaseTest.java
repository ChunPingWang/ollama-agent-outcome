package com.github.chunpingwang.ollamaagentoutcome.application.port.in;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

/**
 * 測試 GetProfileUseCase 介面的存在與基本結構。
 */
class GetProfileUseCaseTest {

    @Test
    void should_have_execute_method() {
        assertThat(GetProfileUseCase.class.getMethods())
                .anyMatch(m -> m.getName().equals("execute"));
    }
}
