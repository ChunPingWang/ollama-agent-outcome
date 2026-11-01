package com.github.chunpingwang.ollamaagentoutcome.bootstrap;


import com.github.chunpingwang.ollamaagentoutcome.adapter.persistence.ProfileJpaRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.chunpingwang.ollamaagentoutcome.adapter.persistence.JpaDeleteProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.adapter.persistence.JpaFindProfileByIdPort;
import com.github.chunpingwang.ollamaagentoutcome.adapter.persistence.JpaSaveProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.CreateProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.DeleteProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.GetProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.UpdateProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.usecase.CreateProfileService;
import com.github.chunpingwang.ollamaagentoutcome.application.usecase.DeleteProfileService;
import com.github.chunpingwang.ollamaagentoutcome.application.usecase.GetProfileService;
import com.github.chunpingwang.ollamaagentoutcome.application.usecase.UpdateProfileService;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileNotFoundException;

/**
 * Configuration class to assemble Use Cases and their dependencies (Ports).
 */
@Configuration
public class UseCaseConfiguration {

    @Bean
    CreateProfileUseCase createProfileUseCase(JpaSaveProfilePort saveProfilePort) {
        return new CreateProfileService(saveProfilePort);
    }

    @Bean
    GetProfileUseCase getProfileUseCase(JpaFindProfileByIdPort findProfileByIdPort) {
        return new GetProfileService(findProfileByIdPort);
    }

    @Bean
    UpdateProfileUseCase updateProfileUseCase(JpaFindProfileByIdPort findProfileByIdPort, JpaSaveProfilePort saveProfilePort) {
        return new UpdateProfileService(findProfileByIdPort, saveProfilePort);
    }

    @Bean
    DeleteProfileUseCase deleteProfileUseCase(JpaFindProfileByIdPort findProfileByIdPort, JpaDeleteProfilePort deleteProfilePort) {
        return new DeleteProfileService(findProfileByIdPort, deleteProfilePort);
    }

    // Persistence Adapter Ports
    @Bean
    JpaSaveProfilePort jpaSaveProfilePort(com.github.chunpingwang.ollamaagentoutcome.adapter.persistence.ProfileJpaRepository repository) {
        return new JpaSaveProfilePort(repository);
    }

    @Bean
    JpaFindProfileByIdPort jpaFindProfileByIdPort(com.github.chunpingwang.ollamaagentoutcome.adapter.persistence.ProfileJpaRepository repository) {
        return new JpaFindProfileByIdPort(repository);
    }

    @Bean
    JpaDeleteProfilePort jpaDeleteProfilePort(com.github.chunpingwang.ollamaagentoutcome.adapter.persistence.ProfileJpaRepository repository) {
        return new JpaDeleteProfilePort(repository);
    }
}
