package com.github.chunpingwang.ollamaagentoutcome.bootstrap;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.chunpingwang.ollamaagentoutcome.application.port.in.CreateProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.DeleteProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.GetProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.UpdateProfileDisplayNameUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.DeleteProfileByIdPort;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.FindProfileByIdPort;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.SaveProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.application.usecase.CreateProfileService;
import com.github.chunpingwang.ollamaagentoutcome.application.usecase.DeleteProfileService;
import com.github.chunpingwang.ollamaagentoutcome.application.usecase.GetProfileService;
import com.github.chunpingwang.ollamaagentoutcome.application.usecase.UpdateProfileDisplayNameService;

/**
 * 組裝 application 層的 use case bean。
 */
@Configuration
public class UseCaseConfiguration {

    @Bean
    CreateProfileUseCase createProfileUseCase(SaveProfilePort saveProfilePort) {
        return new CreateProfileService(saveProfilePort);
    }

    @Bean
    GetProfileUseCase getProfileUseCase(FindProfileByIdPort findProfileByIdPort) {
        return new GetProfileService(findProfileByIdPort);
    }

    @Bean
    UpdateProfileDisplayNameUseCase updateProfileDisplayNameUseCase(
            FindProfileByIdPort findProfileByIdPort,
            SaveProfilePort saveProfilePort) {
        return new UpdateProfileDisplayNameService(findProfileByIdPort, saveProfilePort);
    }

    @Bean
    DeleteProfileUseCase deleteProfileUseCase(
            FindProfileByIdPort findProfileByIdPort,
            DeleteProfileByIdPort deleteProfileByIdPort) {
        return new DeleteProfileService(findProfileByIdPort, deleteProfileByIdPort);
    }
}
