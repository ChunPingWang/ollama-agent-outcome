package com.github.chunpingwang.ollamaagentoutcome.bootstrap;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.chunpingwang.ollamaagentoutcome.adapter.persistence.DeleteProfilePortImpl;
import com.github.chunpingwang.ollamaagentoutcome.adapter.persistence.ExistsByEmailPortImpl;
import com.github.chunpingwang.ollamaagentoutcome.adapter.persistence.LoadProfilePortImpl;
import com.github.chunpingwang.ollamaagentoutcome.adapter.persistence.SaveProfilePortImpl;
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
    CreateProfileUseCase createProfileUseCase(SaveProfilePortImpl saveProfilePort, ExistsByEmailPortImpl existsByEmailPort) {
        return new CreateProfileService(saveProfilePort, existsByEmailPort);
    }

    @Bean
    GetProfileUseCase getProfileUseCase(LoadProfilePortImpl loadProfilePort) {
        return new GetProfileService(loadProfilePort);
    }

    @Bean
    UpdateProfileUseCase updateProfileUseCase(LoadProfilePortImpl loadProfilePort, SaveProfilePortImpl saveProfilePort, ExistsByEmailPortImpl existsByEmailPort) {
        return new UpdateProfileService(loadProfilePort, saveProfilePort, existsByEmailPort);
    }

    @Bean
    DeleteProfileUseCase deleteProfileUseCase(LoadProfilePortImpl loadProfilePort, DeleteProfilePortImpl deleteProfilePort) {
        return new DeleteProfileService(loadProfilePort, deleteProfilePort);
    }
}
