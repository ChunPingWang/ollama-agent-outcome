package com.github.chunpingwang.ollamaagentoutcome.bootstrap;

import com.github.chunpingwang.ollamaagentoutcome.application.usecase.CreateProfileService;
import com.github.chunpingwang.ollamaagentoutcome.application.usecase.GetProfileService;
import com.github.chunpingwang.ollamaagentoutcome.application.usecase.UpdateProfileService;
import com.github.chunpingwang.ollamaagentoutcome.application.usecase.DeleteProfileService;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.SaveProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.LoadProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.DeleteProfilePort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
    GetProfileService getProfileService(LoadProfilePort loadProfilePort) {
        return new GetProfileService(loadProfilePort);
    }

    @Bean
    UpdateProfileService updateProfileService(LoadProfilePort loadProfilePort, SaveProfilePort saveProfilePort) {
        return new UpdateProfileService(loadProfilePort, saveProfilePort);
    }

    @Bean
    DeleteProfileService deleteProfileService(LoadProfilePort loadProfilePort, DeleteProfilePort deleteProfilePort) {
        return new DeleteProfileService(loadProfilePort, deleteProfilePort);
    }
}
