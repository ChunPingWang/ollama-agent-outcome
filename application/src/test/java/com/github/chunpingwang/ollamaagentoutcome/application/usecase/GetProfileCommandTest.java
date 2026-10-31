package com.github.chunpingwang.ollamaagentoutcome.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

/**
 * 測試 GetProfileCommand 的存在與基本結構。
 */
class GetProfileCommandTest {

    @Test
    void should_exist() {
        assertThat(GetProfileCommand.class).isNotNull();
    }
}
