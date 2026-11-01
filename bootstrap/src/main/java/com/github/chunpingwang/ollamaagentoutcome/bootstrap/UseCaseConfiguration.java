package com.github.chunpingwang.ollamaagentoutcome.bootstrap;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.chunpingwang.ollamaagentoutcome.adapter.persistence.JpaDeleteProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.adapter.persistence.JpaLoadProfileByIdPort;
import com.github.chunpingwang.ollamaagentoutcome.adapter.persistence.JpaSaveProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.CreateProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.DeleteProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.GetProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.UpdateProfileUseCase;
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
    CreateProfileUseCase createProfileService(JpaSaveProfilePort saveProfilePort, JpaSaveProfilePort existsByEmailPort) {
        return new CreateProfileService(saveProfilePort, existsByEmailPort);
    }

    @Bean
    GetProfileUseCase getProfileService(JpaLoadProfileByIdPort loadProfileByIdPort) {
        return new GetProfileService(loadProfileByIdPort);
    }

    @Bean
    UpdateProfileUseCase updateProfileService(JpaLoadProfileByIdPort loadProfileByIdPort, 
                                              JpaSaveProfilePort existsByEmailPort,
                                              JpaSaveProfilePort saveProfilePort) {
        return new UpdateProfileService(loadProfileByIdPort, existsByEmailPort, saveProfilePort);
    }

    @Bean
    DeleteProfileUseCase deleteProfileService(JpaDeleteProfilePort deleteProfilePort) {
        return new DeleteProfileService(deleteProfilePort);
    }
}
