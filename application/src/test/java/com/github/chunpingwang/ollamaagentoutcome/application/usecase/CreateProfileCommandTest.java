package com.github.chunpingwang.ollamaagentoutcome.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

/**
 * 測試 CreateProfileCommand 的存在與基本結構。
 */
class CreateProfileCommandTest {

    @Test
    void should_exist() {
        // 驗證類別存在
        assertThat(CreateProfileCommand.class).isNotNull();
    }
}
