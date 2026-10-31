package com.github.chunpingwang.ollamaagentoutcome.bootstrap.config.profile;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Import;

import com.github.chunpingwang.ollamaagentoutcome.application.port.in.CreateProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.DeleteProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.GetProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.UpdateProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.bootstrap.TestcontainersConfiguration;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
class ProfileContextStartupTest {

    @Autowired
    private ApplicationContext context;

    @Test
    void should_start_application_context_successfully() {
        // 驗證 Spring Boot 應用程式上下文能正常啟動，無 Bean 衝突或找不到依賴的問題。
        assertThat(context).isNotNull();
    }

    @Test
    void should_have_all_profile_use_cases_registered_as_beans() {
        // 驗證所有 Profile 相關的 Use Case 都已正確註冊為 Spring Beans。
        CreateProfileUseCase create = context.getBean(CreateProfileUseCase.class);
        GetProfileUseCase get = context.getBean(GetProfileUseCase.class);
        UpdateProfileUseCase update = context.getBean(UpdateProfileUseCase.class);
        DeleteProfileUseCase delete = context.getBean(DeleteProfileUseCase.class);

        assertThat(create).isNotNull();
        assertThat(get).isNotNull();
        assertThat(update).isNotNull();
        assertThat(delete).isNotNull();
    }
}
