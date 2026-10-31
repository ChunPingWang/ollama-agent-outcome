package com.github.chunpingwang.ollamaagentoutcome.application.port.in;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

/**
 * 測試 CreateProfileUseCase 介面的存在與基本結構。
 * 此測試在 T2 階段主要用於驗證介面定義是否正確，實作細節將在 T3 測試。
 */
class CreateProfileUseCaseTest {

    @Test
    void should_have_execute_method() {
        // 驗證介面存在且包含 execute 方法
        // 由於尚未實作，此測試主要確保介面定義符合預期
        assertThat(CreateProfileUseCase.class.getMethods())
                .anyMatch(m -> m.getName().equals("execute"));
    }
}
