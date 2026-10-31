package com.github.chunpingwang.ollamaagentoutcome.bootstrap;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.chunpingwang.ollamaagentoutcome.application.port.in.CreateProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.usecase.CreateProfileService;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.ExistsByEmailPort;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.SaveProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.DeleteProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.usecase.DeleteProfileService;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.LoadProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.DeleteProfileByIdPort;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.GetProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.usecase.GetProfileService;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.UpdateProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.usecase.UpdateProfileService;

/**
 * 組裝 application 層的 use case。
 */
@Configuration
public class UseCaseConfiguration {

    private final SaveProfilePort saveProfilePort;
    private final LoadProfilePort loadProfilePort;
    private final ExistsByEmailPort existsByEmailPort;
    private final DeleteProfileByIdPort deleteProfileByIdPort;

    public UseCaseConfiguration(
            SaveProfilePort saveProfilePort,
            LoadProfilePort loadProfilePort,
            ExistsByEmailPort existsByEmailPort,
            DeleteProfileByIdPort deleteProfileByIdPort) {
        this.saveProfilePort = saveProfilePort;
        this.loadProfilePort = loadProfilePort;
        this.existsByEmailPort = existsByEmailPort;
        this.deleteProfileByIdPort = deleteProfileByIdPort;
    }

    @Bean
    CreateProfileUseCase createProfileService() {
        return new CreateProfileService(existsByEmailPort, saveProfilePort);
    }

    @Bean
    GetProfileUseCase getProfileService() {
        return new GetProfileService(loadProfilePort);
    }

    @Bean
    UpdateProfileUseCase updateProfileService() {
        return new UpdateProfileService(loadProfilePort, saveProfilePort, existsByEmailPort);
    }

    @Bean
    DeleteProfileUseCase deleteProfileService() {
        return new DeleteProfileService(loadProfilePort, deleteProfileByIdPort);
    }
}
