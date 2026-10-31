package com.github.chunpingwang.ollamaagentoutcome.bootstrap;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Import;

import com.github.chunpingwang.ollamaagentoutcome.adapter.persistence.JpaDeleteProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.adapter.persistence.JpaLoadProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.adapter.persistence.JpaSaveProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.adapter.persistence.ProfileEntityConverter;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.CreateProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.DeleteProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.GetProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.UpdateProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.DeleteProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.LoadProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.SaveProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.application.usecase.CreateProfileService;
import com.github.chunpingwang.ollamaagentoutcome.application.usecase.DeleteProfileService;
import com.github.chunpingwang.ollamaagentoutcome.application.usecase.GetProfileService;
import com.github.chunpingwang.ollamaagentoutcome.application.usecase.UpdateProfileService;

/**
 * 驗證 UseCaseConfiguration 能正確組裝所有 Bean 並啟動 Spring Context。
 */
@SpringBootTest
@Import(TestcontainersConfiguration.class)
class UseCaseConfigurationTest {

    @Autowired
    private ApplicationContext context;

    @Test
    void should_load_all_use_case_beans() {
        // Verify Use Case beans
        assertThat(context.getBean(CreateProfileUseCase.class)).isInstanceOf(CreateProfileService.class);
        assertThat(context.getBean(GetProfileUseCase.class)).isInstanceOf(GetProfileService.class);
        assertThat(context.getBean(UpdateProfileUseCase.class)).isInstanceOf(UpdateProfileService.class);
        assertThat(context.getBean(DeleteProfileUseCase.class)).isInstanceOf(DeleteProfileService.class);
    }

    @Test
    void should_load_all_port_beans() {
        // Verify Port implementation beans
        assertThat(context.getBean(SaveProfilePort.class)).isInstanceOf(JpaSaveProfilePort.class);
        assertThat(context.getBean(LoadProfilePort.class)).isInstanceOf(JpaLoadProfilePort.class);
        assertThat(context.getBean(DeleteProfilePort.class)).isInstanceOf(JpaDeleteProfilePort.class);
    }

    @Test
    void should_load_converter_beans() {
        // Verify Converter bean
        assertThat(context.getBean(ProfileEntityConverter.class)).isNotNull();
    }
}
