package com.github.chunpingwang.ollamaagentoutcome.bootstrap;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.chunpingwang.ollamaagentoutcome.application.port.in.CreateProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.DeleteProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.GetProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.UpdateProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.DeleteProfileByIdPort;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.FindProfileByEmailPort;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.FindProfileByIdPort;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.SaveProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.application.usecase.CreateProfileService;
import com.github.chunpingwang.ollamaagentoutcome.application.usecase.DeleteProfileService;
import com.github.chunpingwang.ollamaagentoutcome.application.usecase.GetProfileService;
import com.github.chunpingwang.ollamaagentoutcome.application.usecase.UpdateProfileService;

@Configuration
class UseCaseConfiguration {

    @Bean
    CreateProfileUseCase createProfileUseCase(SaveProfilePort saveProfilePort, FindProfileByEmailPort findProfileByEmailPort) {
        return new CreateProfileService(saveProfilePort, findProfileByEmailPort);
    }

    @Bean
    GetProfileUseCase getProfileUseCase(FindProfileByIdPort findProfileByIdPort) {
        return new GetProfileService(findProfileByIdPort);
    }

    @Bean
    UpdateProfileUseCase updateProfileUseCase(FindProfileByIdPort findProfileByIdPort,
                                              FindProfileByEmailPort findProfileByEmailPort,
                                              SaveProfilePort saveProfilePort) {
        return new UpdateProfileService(findProfileByIdPort, findProfileByEmailPort, saveProfilePort);
    }

    @Bean
    DeleteProfileUseCase deleteProfileUseCase(FindProfileByIdPort findProfileByIdPort,
                                              DeleteProfileByIdPort deleteProfileByIdPort) {
        return new DeleteProfileService(findProfileByIdPort, deleteProfileByIdPort);
    }
}
