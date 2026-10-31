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
import com.github.chunpingwang.ollamaagentoutcome.adapter.persistence.port.JpaFindProfileByIdPort;
import com.github.chunpingwang.ollamaagentoutcome.adapter.persistence.port.JpaRemoveProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.adapter.persistence.port.JpaSaveProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.adapter.persistence.port.JpaValidateEmailUniquePort;

/**
 * 組裝 application 層的 use case。
 */
@Configuration
public class UseCaseConfiguration {

    @Bean
    CreateProfileUseCase createProfileUseCase(JpaValidateEmailUniquePort validateEmailUniquePort, JpaSaveProfilePort saveProfilePort) {
        return new CreateProfileService(validateEmailUniquePort, saveProfilePort);
    }

    @Bean
    GetProfileUseCase getProfileUseCase(JpaFindProfileByIdPort findProfileByIdPort) {
        return new GetProfileService(findProfileByIdPort);
    }

    @Bean
    UpdateProfileUseCase updateProfileUseCase(JpaFindProfileByIdPort findProfileByIdPort, 
                                              JpaValidateEmailUniquePort validateEmailUniquePort, 
                                              JpaSaveProfilePort saveProfilePort) {
        return new UpdateProfileService(findProfileByIdPort, validateEmailUniquePort, saveProfilePort);
    }

    @Bean
    DeleteProfileUseCase deleteProfileUseCase(JpaFindProfileByIdPort findProfileByIdPort, 
                                              JpaRemoveProfilePort removeProfilePort) {
        return new DeleteProfileService(findProfileByIdPort, removeProfilePort);
    }
}
