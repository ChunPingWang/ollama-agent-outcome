package com.github.chunpingwang.ollamaagentoutcome.bootstrap;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.chunpingwang.ollamaagentoutcome.application.usecase.CreateProfileService;
import com.github.chunpingwang.ollamaagentoutcome.application.usecase.DeleteProfileService;
import com.github.chunpingwang.ollamaagentoutcome.application.usecase.LoadProfileService;
import com.github.chunpingwang.ollamaagentoutcome.application.usecase.UpdateProfileService;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.FindProfileByIdPort;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.RemoveProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.SaveProfilePort;

/**
 * 組裝 application 層的 use case。
 */
@Configuration
public class UseCaseConfiguration {

    @Bean
    CreateProfileService createProfileService(SaveProfilePort saveProfilePort) {
        return new CreateProfileService(saveProfilePort);
    }

    @Bean
    LoadProfileService loadProfileService(FindProfileByIdPort findProfileByIdPort) {
        return new LoadProfileService(findProfileByIdPort);
    }

    @Bean
    UpdateProfileService updateProfileService(FindProfileByIdPort findProfileByIdPort, SaveProfilePort saveProfilePort) {
        return new UpdateProfileService(findProfileByIdPort, saveProfilePort);
    }

    @Bean
    DeleteProfileService deleteProfileService(FindProfileByIdPort findProfileByIdPort, RemoveProfilePort removeProfilePort) {
        return new DeleteProfileService(findProfileByIdPort, removeProfilePort);
    }
}
