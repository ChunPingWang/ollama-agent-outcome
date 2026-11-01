package com.github.chunpingwang.ollamaagentoutcome.bootstrap;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.chunpingwang.ollamaagentoutcome.application.port.in.CreateProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.DeleteProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.GetProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.UpdateProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.usecase.CreateProfileService;
import com.github.chunpingwang.ollamaagentoutcome.application.usecase.DeleteProfileService;
import com.github.chunpingwang.ollamaagentoutcome.application.usecase.GetProfileService;
import com.github.chunpingwang.ollamaagentoutcome.application.usecase.UpdateProfileService;
import com.github.chunpingwang.ollamaagentoutcome.adapter.persistence.JpaDeleteProfileByIdPort;
import com.github.chunpingwang.ollamaagentoutcome.adapter.persistence.JpaExistsByEmailPort;
import com.github.chunpingwang.ollamaagentoutcome.adapter.persistence.JpaFindProfileByIdPort;
import com.github.chunpingwang.ollamaagentoutcome.adapter.persistence.JpaSaveProfilePort;

/**
 * 組裝 application 層的 use case。
 */
@Configuration
public class UseCaseConfiguration {

    @Bean
    CreateProfileUseCase createProfileUseCase(
            JpaExistsByEmailPort existsByEmailPort,
            JpaSaveProfilePort saveProfilePort) {
        return new CreateProfileService(existsByEmailPort, saveProfilePort);
    }

    @Bean
    GetProfileUseCase getProfileUseCase(JpaFindProfileByIdPort findProfileByIdPort) {
        return new GetProfileService(findProfileByIdPort);
    }

    @Bean
    UpdateProfileUseCase updateProfileUseCase(
            JpaFindProfileByIdPort findProfileByIdPort,
            JpaExistsByEmailPort existsByEmailPort,
            JpaSaveProfilePort saveProfilePort) {
        return new UpdateProfileService(findProfileByIdPort, existsByEmailPort, saveProfilePort);
    }

    @Bean
    DeleteProfileUseCase deleteProfileUseCase(
            JpaFindProfileByIdPort findProfileByIdPort,
            JpaDeleteProfileByIdPort deleteProfileByIdPort) {
        return new DeleteProfileService(findProfileByIdPort, deleteProfileByIdPort);
    }
}
