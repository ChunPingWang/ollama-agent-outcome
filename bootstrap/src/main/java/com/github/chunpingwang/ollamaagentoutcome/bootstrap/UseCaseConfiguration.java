package com.github.chunpingwang.ollamaagentoutcome.bootstrap;

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

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 組裝 Use Case 與 Port 實作的配置類別。
 */
@Configuration
public class UseCaseConfiguration {

    @Bean
    CreateProfileUseCase createProfileUseCase(SaveProfilePort saveProfilePort) {
        return new CreateProfileService(saveProfilePort);
    }

    @Bean
    GetProfileUseCase getProfileUseCase(LoadProfilePort loadProfilePort) {
        return new GetProfileService(loadProfilePort);
    }

    @Bean
    UpdateProfileUseCase updateProfileUseCase(LoadProfilePort loadProfilePort, SaveProfilePort saveProfilePort) {
        return new UpdateProfileService(loadProfilePort, saveProfilePort);
    }

    @Bean
    DeleteProfileUseCase deleteProfileUseCase(LoadProfilePort loadProfilePort, DeleteProfilePort deleteProfilePort) {
        return new DeleteProfileService(loadProfilePort, deleteProfilePort);
    }

    @Bean
    SaveProfilePort saveProfilePort(JpaSaveProfilePort jpaSaveProfilePort) {
        return jpaSaveProfilePort;
    }

    @Bean
    LoadProfilePort loadProfilePort(JpaLoadProfilePort jpaLoadProfilePort) {
        return jpaLoadProfilePort;
    }

    @Bean
    DeleteProfilePort deleteProfilePort(JpaDeleteProfilePort jpaDeleteProfilePort) {
        return jpaDeleteProfilePort;
    }
}
