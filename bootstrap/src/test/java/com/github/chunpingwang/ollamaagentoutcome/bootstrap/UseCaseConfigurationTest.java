package com.github.chunpingwang.ollamaagentoutcome.bootstrap;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.junit.jupiter.api.extension.ExtendWith;

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
import com.github.chunpingwang.ollamaagentoutcome.adapter.persistence.JpaDeleteProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.adapter.persistence.JpaLoadProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.adapter.persistence.JpaSaveProfilePort;

/**
 * 驗證 UseCaseConfiguration 能正確組裝所有 Bean，且 Spring Context 啟動無誤。
 */
@SpringBootTest
@Import(TestcontainersConfiguration.class)
class UseCaseConfigurationTest {

    @Autowired
    private ApplicationContext context;

    @Test
    void should_start_context_and_inject_all_use_cases() {
        // given & when & then
        // 驗證 Context 啟動成功
        assertThat(context).isNotNull();

        // 驗證所有 Use Case Bean 存在
        assertThat(context.getBean(CreateProfileUseCase.class)).isInstanceOf(CreateProfileService.class);
        assertThat(context.getBean(GetProfileUseCase.class)).isInstanceOf(GetProfileService.class);
        assertThat(context.getBean(UpdateProfileUseCase.class)).isInstanceOf(UpdateProfileService.class);
        assertThat(context.getBean(DeleteProfileUseCase.class)).isInstanceOf(DeleteProfileService.class);

        // 驗證 Port 實作 Bean 存在
        assertThat(context.getBean(SaveProfilePort.class)).isInstanceOf(JpaSaveProfilePort.class);
        assertThat(context.getBean(LoadProfilePort.class)).isInstanceOf(JpaLoadProfilePort.class);
        assertThat(context.getBean(DeleteProfilePort.class)).isInstanceOf(JpaDeleteProfilePort.class);
    }

    @Test
    void should_have_no_circular_dependencies() {
        // 如果存在循環依賴，Spring Context 啟動時會拋出 BeanCurrentlyInCreationException
        // 此測試主要確保 context 能正常啟動，若啟動失敗則測試會失敗
        assertThat(context).isNotNull();
    }
}
