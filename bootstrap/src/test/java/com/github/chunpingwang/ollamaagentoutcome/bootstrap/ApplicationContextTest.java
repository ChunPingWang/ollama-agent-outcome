package com.github.chunpingwang.ollamaagentoutcome.bootstrap;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import com.github.chunpingwang.ollamaagentoutcome.application.port.in.CreateProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.DeleteProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.GetProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.UpdateProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.adapter.persistence.port.JpaFindProfileByIdPort;
import com.github.chunpingwang.ollamaagentoutcome.adapter.persistence.port.JpaRemoveProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.adapter.persistence.port.JpaSaveProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.adapter.persistence.port.JpaValidateEmailUniquePort;
import com.github.chunpingwang.ollamaagentoutcome.bootstrap.config.UseCaseConfiguration; // 修正：若 UseCaseConfiguration 在 config 套件則保留，但根據現有檔案它在 bootstrap 套件。
// 注意：根據提供的 existing files, UseCaseConfiguration.java 的 package 是 com.github.chunpingwang.ollamaagentoutcome.bootstrap
// 因此 import 應為 com.github.chunpingwang.ollamaagentoutcome.bootstrap.UseCaseConfiguration

@SpringBootTest(classes = Application.class) // 使用主應用類別以載入完整 Context (含 DB, Repositories)
class ApplicationContextTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    void should_load_application_context_successfully() {
        // 驗證 Inbound Ports (Use Cases) 已註冊
        assertThat(applicationContext.getBeanNamesForType(CreateProfileUseCase.class)).isNotEmpty();
        assertThat(applicationContext.getBeanNamesForType(GetProfileUseCase.class)).isNotEmpty();
        assertThat(applicationContext.getBeanNamesForType(UpdateProfileUseCase.class)).isNotEmpty();
        assertThat(applicationContext.getBeanNamesForType(DeleteProfileUseCase.class)).isNotEmpty();
        
        // 驗證 Outbound Ports (JPA Implementations) 已註冊
        assertThat(applicationContext.getBeanNamesForType(JpaSaveProfilePort.class)).isNotEmpty();
        assertThat(applicationContext.getBeanNamesForType(JpaFindProfileByIdPort.class)).isNotEmpty();
        assertThat(applicationContext.getBeanNamesForType(JpaValidateEmailUniquePort.class)).isNotEmpty();
        assertThat(applicationContext.getBeanNamesForType(JpaRemoveProfilePort.class)).isNotEmpty();
        
        var createBean = applicationContext.getBean("createProfileUseCase", Object.class);
        assertThat(createBean).isNotNull();
    }
}
