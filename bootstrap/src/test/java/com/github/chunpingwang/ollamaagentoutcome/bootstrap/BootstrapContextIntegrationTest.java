package com.github.chunpingwang.ollamaagentoutcome.bootstrap;


import org.springframework.context.annotation.Import;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.github.chunpingwang.ollamaagentoutcome.application.usecase.CreateProfileService;
import com.github.chunpingwang.ollamaagentoutcome.application.usecase.DeleteProfileService;
import com.github.chunpingwang.ollamaagentoutcome.application.usecase.GetProfileService;
import com.github.chunpingwang.ollamaagentoutcome.application.usecase.UpdateProfileService;

/**
 * 驗證 Bootstrap Configuration 是否正確註冊了所有 Application Services。
 * 此測試在 UseCaseConfiguration 尚未定義 @Bean 時會失敗（RED），確保依賴注入鏈完整。
 */
@Import(com.github.chunpingwang.ollamaagentoutcome.bootstrap.TestcontainersConfiguration.class)  // 自動補上：測試需要 PostgreSQL 容器
@SpringBootTest
class BootstrapContextIntegrationTest {

    @Autowired(required = false)
    private CreateProfileService createProfileService;

    @Autowired(required = false)
    private GetProfileService getProfileService;

    @Autowired(required = false)
    private UpdateProfileService updateProfileService;

    @Autowired(required = false)
    private DeleteProfileService deleteProfileService;

    @Test
    void should_register_all_use_case_services() {
        assertThat(createProfileService).as("CreateProfileService must be registered").isNotNull();
        assertThat(getProfileService).as("GetProfileService must be registered").isNotNull();
        assertThat(updateProfileService).as("UpdateProfileService must be registered").isNotNull();
        assertThat(deleteProfileService).as("DeleteProfileService must be registered").isNotNull();
    }
}
