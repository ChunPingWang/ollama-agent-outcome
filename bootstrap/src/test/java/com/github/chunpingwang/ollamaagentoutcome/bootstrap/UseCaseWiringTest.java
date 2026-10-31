package com.github.chunpingwang.ollamaagentoutcome.bootstrap;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import com.github.chunpingwang.ollamaagentoutcome.application.port.in.CreateProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.GetProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.UpdateProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.DeleteProfileUseCase;

/**
 * 驗證 Use Case Services 是否已正確組裝至 Spring Context 中。
 */
@SpringBootTest
@ActiveProfiles("test")
@Import(TestcontainersConfiguration.class)
class UseCaseWiringTest {

    private final ApplicationContext context;

    @Autowired
    public UseCaseWiringTest(ApplicationContext context) {
        this.context = context;
    }

    @Test
    void should_have_create_profile_service_bean() {
        assertThat(context.getBeanNamesForType(CreateProfileUseCase.class))
                .as("CreateProfileService bean should be registered in the application context")
                .isNotEmpty();
    }

    @Test
    void should_have_get_profile_service_bean() {
        assertThat(context.getBeanNamesForType(GetProfileUseCase.class))
                .as("GetProfileService bean should be registered in the application context")
                .isNotEmpty();
    }

    @Test
    void should_have_update_profile_service_bean() {
        assertThat(context.getBeanNamesForType(UpdateProfileUseCase.class))
                .as("UpdateProfileService bean should be registered in the application context")
                .isNotEmpty();
    }

    @Test
    void should_have_delete_profile_service_bean() {
        assertThat(context.getBeanNamesForType(DeleteProfileUseCase.class))
                .as("DeleteProfileService bean should be registered in the application context")
                .isNotEmpty();
    }
}
