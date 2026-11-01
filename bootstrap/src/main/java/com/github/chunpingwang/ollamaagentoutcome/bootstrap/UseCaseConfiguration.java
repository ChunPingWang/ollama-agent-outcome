package com.github.chunpingwang.ollamaagentoutcome.bootstrap;

import com.github.chunpingwang.ollamaagentoutcome.application.port.in.CreateProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.usecase.CreateProfileService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 組裝 application 層的 use case。
 */
@Configuration
public class UseCaseConfiguration {

    @Bean
    public CreateProfileUseCase createProfileService(com.github.chunpingwang.ollamaagentoutcome.application.port.out.SaveProfilePort saveProfilePort) {
        return new CreateProfileService(saveProfilePort);
    }
}
