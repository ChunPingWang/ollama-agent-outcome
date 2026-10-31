package com.github.chunpingwang.ollamaagentoutcome.bootstrap.config;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;

import com.github.chunpingwang.ollamaagentoutcome.application.usecase.CreateProfileService;
import com.github.chunpingwang.ollamaagentoutcome.application.usecase.DeleteProfileService;
import com.github.chunpingwang.ollamaagentoutcome.application.usecase.GetProfileService;
import com.github.chunpingwang.ollamaagentoutcome.application.usecase.UpdateProfileService;
import com.github.chunpingwang.ollamaagentoutcome.bootstrap.UseCaseConfiguration;
import com.github.chunpingwang.ollamaagentoutcome.bootstrap.TestcontainersConfiguration;

/**
 * 驗證 Use Case Configuration 能正確組裝所有 Bean，且 Spring Context 可順利啟動。
 */
@SpringBootTest(classes = {
        UseCaseConfiguration.class,
        TestcontainersConfiguration.class
})
@ContextConfiguration(classes = {
        UseCaseConfiguration.class,
        TestcontainersConfiguration.class
})
class UseCaseConfigurationIntegrationTest {

    @Autowired
    private ApplicationContext context;

    @Test
    void should_start_application_context_successfully() {
        // 若上下文無法啟動（例如缺少 Bean、循環依賴），此測試會失敗
        assertThat(context).isNotNull();
    }

    @Test
    void should_have_create_profile_service_bean() {
        CreateProfileService service = context.getBean(CreateProfileService.class);
        assertThat(service).isNotNull();
    }

    @Test
    void should_have_get_profile_service_bean() {
        GetProfileService service = context.getBean(GetProfileService.class);
        assertThat(service).isNotNull();
    }

    @Test
    void should_have_update_profile_service_bean() {
        UpdateProfileService service = context.getBean(UpdateProfileService.class);
        assertThat(service).isNotNull();
    }

    @Test
    void should_have_delete_profile_service_bean() {
        DeleteProfileService service = context.getBean(DeleteProfileService.class);
        assertThat(service).isNotNull();
    }
}
