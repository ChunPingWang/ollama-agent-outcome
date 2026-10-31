package com.github.chunpingwang.ollamaagentoutcome.bootstrap;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Import;

import com.github.chunpingwang.ollamaagentoutcome.bootstrap.TestcontainersConfiguration;

/**
 * 測試 UseCaseConfiguration 是否能正確組裝所有 Bean 並啟動 Spring 上下文。
 * 此測試驗證整個應用程式上下文（包含資料庫連線）可正常啟動。
 */
@SpringBootTest
@Import(TestcontainersConfiguration.class)
class UseCaseConfigurationTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    @DisplayName("應能成功啟動 Spring 上下文並解析所有 Use Case Bean")
    void should_start_application_context_and_resolve_beans() {
        // 驗證上下文已啟動
        assertThat(applicationContext).isNotNull();

        // 驗證所有 Inbound Ports (Use Cases) 的 Bean 存在
        assertThat(applicationContext.containsBean("createProfileUseCase")).isTrue();
        assertThat(applicationContext.containsBean("getProfileUseCase")).isTrue();
        assertThat(applicationContext.containsBean("updateProfileUseCase")).isTrue();
        assertThat(applicationContext.containsBean("deleteProfileUseCase")).isTrue();

        // 驗證所有 Outbound Ports 的 Bean 存在
        assertThat(applicationContext.containsBean("saveProfilePort")).isTrue();
        assertThat(applicationContext.containsBean("loadProfilePort")).isTrue();
        assertThat(applicationContext.containsBean("deleteProfilePort")).isTrue();
    }

    @Test
    @DisplayName("Use Case Bean 應為正確的型別")
    void should_have_correct_bean_types() {
        // 驗證 CreateProfileUseCase Bean 的型別
        assertThat(applicationContext.getBean("createProfileUseCase"))
            .isInstanceOf(com.github.chunpingwang.ollamaagentoutcome.application.port.in.CreateProfileUseCase.class);

        // 驗證 GetProfileUseCase Bean 的型別
        assertThat(applicationContext.getBean("getProfileUseCase"))
            .isInstanceOf(com.github.chunpingwang.ollamaagentoutcome.application.port.in.GetProfileUseCase.class);

        // 驗證 UpdateProfileUseCase Bean 的型別
        assertThat(applicationContext.getBean("updateProfileUseCase"))
            .isInstanceOf(com.github.chunpingwang.ollamaagentoutcome.application.port.in.UpdateProfileUseCase.class);

        // 驗證 DeleteProfileUseCase Bean 的型別
        assertThat(applicationContext.getBean("deleteProfileUseCase"))
            .isInstanceOf(com.github.chunpingwang.ollamaagentoutcome.application.port.in.DeleteProfileUseCase.class);
    }
}
