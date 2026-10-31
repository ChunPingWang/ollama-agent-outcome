package com.github.chunpingwang.ollamaagentoutcome.bootstrap.config.profile;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.chunpingwang.ollamaagentoutcome.application.port.in.CreateProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.DeleteProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.GetProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.UpdateProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.LoadProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.RemoveProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.SaveProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.application.usecase.CreateProfileService;
import com.github.chunpingwang.ollamaagentoutcome.application.usecase.DeleteProfileService;
import com.github.chunpingwang.ollamaagentoutcome.application.usecase.GetProfileService;
import com.github.chunpingwang.ollamaagentoutcome.application.usecase.UpdateProfileService;
import com.github.chunpingwang.ollamaagentoutcome.adapter.persistence.port.JpaLoadProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.adapter.persistence.port.JpaRemoveProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.adapter.persistence.port.JpaSaveProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.adapter.persistence.repository.JpaProfileRepository;

/**
 * 組裝 Profile 相關的 Use Case、Ports 與 Persistence Adapter。
 */
@Configuration
public class ProfileUseCaseConfiguration {

    @Bean
    JpaLoadProfilePort jpaLoadProfilePort(JpaProfileRepository repository) {
        return new JpaLoadProfilePort(repository);
    }

    @Bean
    JpaSaveProfilePort jpaSaveProfilePort(JpaProfileRepository repository) {
        return new JpaSaveProfilePort(repository);
    }

    @Bean
    JpaRemoveProfilePort jpaRemoveProfilePort(JpaProfileRepository repository) {
        return new JpaRemoveProfilePort(repository);
    }

    @Bean
    CreateProfileUseCase createProfileService(LoadProfilePort loadProfilePort, SaveProfilePort saveProfilePort) {
        return new CreateProfileService(loadProfilePort, saveProfilePort);
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
    DeleteProfileUseCase deleteProfileService(LoadProfilePort loadProfilePort, RemoveProfilePort removeProfilePort) {
        return new DeleteProfileService(loadProfilePort, removeProfilePort);
    }
}
