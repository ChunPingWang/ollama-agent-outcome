package com.github.chunpingwang.ollamaagentoutcome.bootstrap;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import com.github.chunpingwang.ollamaagentoutcome.application.port.in.CreateProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.DeleteProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.GetProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.UpdateProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.adapter.persistence.port.JpaDeleteProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.adapter.persistence.port.JpaLoadProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.adapter.persistence.port.JpaSaveProfilePort;

/**
 * 驗證 Bootstrap 組裝後的應用程式上下文可正常啟動，且所有關鍵 Bean（Use Case、Port）皆存在。
 */
@SpringBootTest(classes = UseCaseConfiguration.class)
class BootstrapContextTest {

    @Autowired
    private ApplicationContext context;

    @Test
    void should_start_application_context_successfully() {
        // Then: Context 已透過 @SpringBootTest 載入，若無例外即代表基本依賴注入成功
        assertThat(context).isNotNull();
    }

    @Test
    void should_have_all_use_case_beans_registered() {
        // Given & When & Then: 檢查 Inbound Port 實作是否存在於容器中
        assertThat(context.getBean(CreateProfileUseCase.class)).isInstanceOf(CreateProfileService.class);
        assertThat(context.getBean(GetProfileUseCase.class)).isInstanceOf(GetProfileService.class);
        assertThat(context.getBean(UpdateProfileUseCase.class)).isInstanceOf(UpdateProfileService.class);
        assertThat(context.getBean(DeleteProfileUseCase.class)).isInstanceOf(DeleteProfileService.class);
    }

    @Test
    void should_have_persistence_port_beans_registered() {
        // Given & When & Then: 檢查 Outbound Port 實作是否存在於容器中
        assertThat(context.getBean(JpaSaveProfilePort.class)).isNotNull();
        assertThat(context.getBean(JpaLoadProfilePort.class)).isNotNull();
        assertThat(context.getBean(JpaDeleteProfilePort.class)).isNotNull();
    }

    /**
     * 輔助類別：確保測試中使用的 Service 類別存在且可被引用。
     */
    private static class CreateProfileService extends com.github.chunpingwang.ollamaagentoutcome.application.usecase.CreateProfileService {
        public CreateProfileService(com.github.chunpingwang.ollamaagentoutcome.application.port.out.SaveProfilePort port) {
            super(port);
        }
    }

    /**
     * 輔助類別：確保測試中使用的 Service 類別存在且可被引用。
     */
    private static class GetProfileService extends com.github.chunpingwang.ollamaagentoutcome.application.usecase.GetProfileService {
        public GetProfileService(com.github.chunpingwang.ollamaagentoutcome.application.port.out.LoadProfilePort port) {
            super(port);
        }
    }

    /**
     * 輔助類別：確保測試中使用的 Service 類別存在且可被引用。
     */
    private static class UpdateProfileService extends com.github.chunpingwang.ollamaagentoutcome.application.usecase.UpdateProfileService {
        public UpdateProfileService(
                com.github.chunpingwang.ollamaagentoutcome.application.port.out.LoadProfilePort loadPort,
                com.github.chunpingwang.ollamaagentoutcome.application.port.out.SaveProfilePort savePort) {
            super(loadPort, savePort);
        }
    }

    /**
     * 輔助類別：確保測試中使用的 Service 類別存在且可被引用。
     */
    private static class DeleteProfileService extends com.github.chunpingwang.ollamaagentoutcome.application.usecase.DeleteProfileService {
        public DeleteProfileService(com.github.chunpingwang.ollamaagentoutcome.application.port.out.DeleteProfilePort port) {
            super(port);
        }
    }
}
