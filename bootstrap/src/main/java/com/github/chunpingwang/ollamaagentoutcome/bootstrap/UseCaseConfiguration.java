package com.github.chunpingwang.ollamaagentoutcome.bootstrap;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.chunpingwang.ollamaagentoutcome.adapter.persistence.DeleteProfileByIdPortImpl;
import com.github.chunpingwang.ollamaagentoutcome.adapter.persistence.ExistsByEmailPortImpl;
import com.github.chunpingwang.ollamaagentoutcome.adapter.persistence.LoadProfilePortImpl;
import com.github.chunpingwang.ollamaagentoutcome.adapter.persistence.SaveProfilePortImpl;
import com.github.chunpingwang.ollamaagentoutcome.application.usecase.CreateProfileService;
import com.github.chunpingwang.ollamaagentoutcome.application.usecase.DeleteProfileService;
import com.github.chunpingwang.ollamaagentoutcome.application.usecase.GetProfileService;
import com.github.chunpingwang.ollamaagentoutcome.application.usecase.UpdateProfileService;

/**
 * 組裝 application 層的 use case bean，將 port 實作注入至 service。
 */
@Configuration
public class UseCaseConfiguration {

    @Bean
    CreateProfileService createProfileService(SaveProfilePortImpl saveProfilePort, ExistsByEmailPortImpl existsByEmailPort) {
        return new CreateProfileService(saveProfilePort, existsByEmailPort);
    }

    @Bean
    GetProfileService getProfileService(LoadProfilePortImpl loadProfilePort) {
        return new GetProfileService(loadProfilePort);
    }

    @Bean
    UpdateProfileService updateProfileService(LoadProfilePortImpl loadProfilePort, 
                                              SaveProfilePortImpl saveProfilePort, 
                                              ExistsByEmailPortImpl existsByEmailPort) {
        return new UpdateProfileService(loadProfilePort, saveProfilePort, existsByEmailPort);
    }

    @Bean
    DeleteProfileService deleteProfileService(LoadProfilePortImpl loadProfilePort, 
                                              DeleteProfileByIdPortImpl deleteProfileByIdPort) {
        return new DeleteProfileService(loadProfilePort, deleteProfileByIdPort);
    }
}
