package com.github.chunpingwang.ollamaagentoutcome.bootstrap;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.chunpingwang.ollamaagentoutcome.adapter.persistence.JpaDeleteProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.adapter.persistence.JpaLoadProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.adapter.persistence.JpaSaveProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.adapter.persistence.ProfileEntityConverter;
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
 * 將 application 層的 Service 與 adapter 層的 Port 實作串接。
 */
@Configuration
public class UseCaseConfiguration {

    @Bean
    CreateProfileUseCase createProfileUseCase(JpaSaveProfilePort saveProfilePort) {
        return new CreateProfileService(saveProfilePort);
    }

    @Bean
    GetProfileUseCase getProfileUseCase(JpaLoadProfilePort loadProfilePort) {
        return new GetProfileService(loadProfilePort);
    }

    @Bean
    UpdateProfileUseCase updateProfileUseCase(JpaLoadProfilePort loadProfilePort, JpaSaveProfilePort saveProfilePort) {
        return new UpdateProfileService(loadProfilePort, saveProfilePort);
    }

    @Bean
    DeleteProfileUseCase deleteProfileUseCase(JpaLoadProfilePort loadProfilePort, JpaDeleteProfilePort deleteProfilePort) {
        return new DeleteProfileService(loadProfilePort, deleteProfilePort);
    }

    @Bean
    JpaSaveProfilePort jpaSaveProfilePort(com.github.chunpingwang.ollamaagentoutcome.adapter.persistence.ProfileJpaRepository repository, ProfileEntityConverter converter) {
        return new JpaSaveProfilePort(repository, converter);
    }

    @Bean
    JpaLoadProfilePort jpaLoadProfilePort(com.github.chunpingwang.ollamaagentoutcome.adapter.persistence.ProfileJpaRepository repository, ProfileEntityConverter converter) {
        return new JpaLoadProfilePort(repository, converter);
    }

    @Bean
    JpaDeleteProfilePort jpaDeleteProfilePort(com.github.chunpingwang.ollamaagentoutcome.adapter.persistence.ProfileJpaRepository repository) {
        return new JpaDeleteProfilePort(repository);
    }

    @Bean
    ProfileEntityConverter profileEntityConverter() {
        return new ProfileEntityConverter();
    }
}
