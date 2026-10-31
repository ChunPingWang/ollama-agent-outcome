package com.github.chunpingwang.ollamaagentoutcome.bootstrap;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import jakarta.persistence.EntityManager;

import com.github.chunpingwang.ollamaagentoutcome.application.port.in.CreateProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.usecase.CreateProfileService;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.SaveProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.adapter.persistence.JpaSaveProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.GetProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.usecase.GetProfileService;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.LoadProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.adapter.persistence.JpaLoadProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.DeleteProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.usecase.DeleteProfileService;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.DeleteProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.adapter.persistence.JpaDeleteProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.UpdateProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.usecase.UpdateProfileService;

/**
 * 組裝 application 層的 use case 與 persistence adapter bean。
 */
@Configuration
@EnableJpaRepositories(basePackages = "com.github.chunpingwang.ollamaagentoutcome.adapter.persistence")
public class UseCaseConfiguration {

    @Bean
    SaveProfilePort saveProfilePort(EntityManager entityManager) {
        return new JpaSaveProfilePort(entityManager);
    }

    @Bean
    LoadProfilePort loadProfilePort(EntityManager entityManager) {
        return new JpaLoadProfilePort(entityManager);
    }

    @Bean
    DeleteProfilePort deleteProfilePort(EntityManager entityManager) {
        return new JpaDeleteProfilePort(entityManager);
    }

    @Bean
    CreateProfileUseCase createProfileService(SaveProfilePort saveProfilePort) {
        return new CreateProfileService(saveProfilePort);
    }

    @Bean
    GetProfileUseCase getProfileService(LoadProfilePort loadProfilePort) {
        return new GetProfileService(loadProfilePort);
    }

    @Bean
    UpdateProfileUseCase updateProfileService(LoadProfilePort loadProfilePort, SaveProfilePort saveProfilePort) {
        return new UpdateProfileService(loadProfilePort, saveProfilePort);
    }

    @Bean
    DeleteProfileUseCase deleteProfileService(LoadProfilePort loadProfilePort, DeleteProfilePort deleteProfilePort) {
        return new DeleteProfileService(loadProfilePort, deleteProfilePort);
    }
}
