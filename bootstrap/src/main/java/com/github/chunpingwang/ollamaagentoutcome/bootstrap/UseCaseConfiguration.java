package com.github.chunpingwang.ollamaagentoutcome.bootstrap;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.chunpingwang.ollamaagentoutcome.application.port.in.CreateProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.DeleteProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.GetProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.UpdateProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.DeleteProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.ExistsByEmailPort;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.LoadProfileByIdPort;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.SaveProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.application.usecase.CreateProfileService;
import com.github.chunpingwang.ollamaagentoutcome.application.usecase.DeleteProfileService;
import com.github.chunpingwang.ollamaagentoutcome.application.usecase.GetProfileService;
import com.github.chunpingwang.ollamaagentoutcome.application.usecase.UpdateProfileService;

/**
 * 組裝 application 層的 use case。
 */
@Configuration
public class UseCaseConfiguration {

    @Bean
    CreateProfileUseCase createProfileService(SaveProfilePort saveProfilePort, ExistsByEmailPort existsByEmailPort) {
        return new CreateProfileService(saveProfilePort, existsByEmailPort);
    }

    @Bean
    GetProfileUseCase getProfileService(LoadProfileByIdPort loadProfileByIdPort) {
        return new GetProfileService(loadProfileByIdPort);
    }

    @Bean
    UpdateProfileUseCase updateProfileService(LoadProfileByIdPort loadProfileByIdPort, 
                                              ExistsByEmailPort existsByEmailPort,
                                              SaveProfilePort saveProfilePort) {
        return new UpdateProfileService(loadProfileByIdPort, existsByEmailPort, saveProfilePort);
    }

    @Bean
    DeleteProfileUseCase deleteProfileService(DeleteProfilePort deleteProfilePort) {
        return new DeleteProfileService(deleteProfilePort);
    }
}
