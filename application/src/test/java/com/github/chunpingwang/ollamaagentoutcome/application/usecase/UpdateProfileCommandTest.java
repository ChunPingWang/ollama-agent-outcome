package com.github.chunpingwang.ollamaagentoutcome.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

/**
 * 測試 UpdateProfileCommand 的存在與基本結構。
 */
class UpdateProfileCommandTest {

    @Test
    void should_exist() {
        assertThat(UpdateProfileCommand.class).isNotNull();
    }
}
