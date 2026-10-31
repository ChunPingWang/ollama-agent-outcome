package com.github.chunpingwang.ollamaagentoutcome.bootstrap;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.chunpingwang.ollamaagentoutcome.adapter.persistence.port.JpaDeleteProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.adapter.persistence.port.JpaLoadProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.adapter.persistence.port.JpaSaveProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.adapter.persistence.repository.ProfileJpaRepository;
import com.github.chunpingwang.ollamaagentoutcome.application.usecase.CreateProfileService;
import com.github.chunpingwang.ollamaagentoutcome.application.usecase.DeleteProfileService;
import com.github.chunpingwang.ollamaagentoutcome.application.usecase.GetProfileService;
import com.github.chunpingwang.ollamaagentoutcome.application.usecase.UpdateProfileService;

/**
 * 組裝 application 層的 use case 與 persistence adapter 的 port 實作。
 */
@Configuration
public class UseCaseConfiguration {

    @Bean
    JpaSaveProfilePort jpaSaveProfilePort(ProfileJpaRepository repository) {
        return new JpaSaveProfilePort(repository);
    }

    @Bean
    JpaLoadProfilePort jpaLoadProfilePort(ProfileJpaRepository repository) {
        return new JpaLoadProfilePort(repository);
    }

    @Bean
    JpaDeleteProfilePort jpaDeleteProfilePort(ProfileJpaRepository repository) {
        return new JpaDeleteProfilePort(repository);
    }

    @Bean
    CreateProfileService createProfileService(JpaSaveProfilePort saveProfilePort) {
        return new CreateProfileService(saveProfilePort);
    }

    @Bean
    GetProfileService getProfileService(JpaLoadProfilePort loadProfilePort) {
        return new GetProfileService(loadProfilePort);
    }

    @Bean
    UpdateProfileService updateProfileService(JpaLoadProfilePort loadProfilePort, JpaSaveProfilePort saveProfilePort) {
        return new UpdateProfileService(loadProfilePort, saveProfilePort);
    }

    @Bean
    DeleteProfileService deleteProfileService(JpaLoadProfilePort loadProfilePort) {
        return new DeleteProfileService(loadProfilePort);
    }
}
