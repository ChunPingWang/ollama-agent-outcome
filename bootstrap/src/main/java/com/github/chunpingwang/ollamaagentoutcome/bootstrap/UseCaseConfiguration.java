package com.github.chunpingwang.ollamaagentoutcome.bootstrap;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
 * 組裝 application 層的 use case。
 * 此配置類別負責將 outbound port 實作注入到 use case service 中。
 */
@Configuration
public class UseCaseConfiguration {

    private final SaveProfilePort saveProfilePort;
    private final LoadProfilePort loadProfilePort;
    private final DeleteProfilePort deleteProfilePort;

    public UseCaseConfiguration(SaveProfilePort saveProfilePort, LoadProfilePort loadProfilePort, DeleteProfilePort deleteProfilePort) {
        this.saveProfilePort = saveProfilePort;
        this.loadProfilePort = loadProfilePort;
        this.deleteProfilePort = deleteProfilePort;
    }

    @Bean
    public CreateProfileUseCase createProfileUseCase() {
        return new CreateProfileService(saveProfilePort);
    }

    @Bean
    public GetProfileUseCase getProfileUseCase() {
        return new GetProfileService(loadProfilePort);
    }

    @Bean
    public UpdateProfileUseCase updateProfileUseCase() {
        return new UpdateProfileService(loadProfilePort, saveProfilePort);
    }

    @Bean
    public DeleteProfileUseCase deleteProfileUseCase() {
        return new DeleteProfileService(loadProfilePort, deleteProfilePort);
    }
}
