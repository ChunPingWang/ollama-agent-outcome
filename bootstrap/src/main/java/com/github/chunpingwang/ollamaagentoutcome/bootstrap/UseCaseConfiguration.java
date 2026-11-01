package com.github.chunpingwang.ollamaagentoutcome.bootstrap;

import com.github.chunpingwang.ollamaagentoutcome.application.port.in.CreateProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.DeleteProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.GetProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.UpdateProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.usecase.CreateProfileService;
import com.github.chunpingwang.ollamaagentoutcome.application.usecase.DeleteProfileService;
import com.github.chunpingwang.ollamaagentoutcome.application.usecase.GetProfileService;
import com.github.chunpingwang.ollamaagentoutcome.application.usecase.UpdateProfileService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 組裝 application 層的 use case。
 */
@Configuration
public class UseCaseConfiguration {

    @Bean("createProfileUseCase")
    CreateProfileUseCase createProfileUseCase(CreateProfileService service) {
        return service;
    }

    @Bean("getProfileUseCase")
    GetProfileUseCase getProfileUseCase(GetProfileService service) {
        return service;
    }

    @Bean("updateProfileUseCase")
    UpdateProfileUseCase updateProfileUseCase(UpdateProfileService service) {
        return service;
    }

    @Bean("deleteProfileUseCase")
    DeleteProfileUseCase deleteProfileUseCase(DeleteProfileService service) {
        return service;
    }

    // 註冊 Application Layer Service Beans，讓 Spring 能夠注入到上述 Use Case Bean 定義中
    @Bean
    CreateProfileService createProfileService(com.github.chunpingwang.ollamaagentoutcome.application.port.out.SaveProfilePort saveProfilePort) {
        return new CreateProfileService(saveProfilePort);
    }

    @Bean
    GetProfileService getProfileService(com.github.chunpingwang.ollamaagentoutcome.application.port.out.LoadProfilePort loadProfilePort) {
        return new GetProfileService(loadProfilePort);
    }

    @Bean
    UpdateProfileService updateProfileService(
            com.github.chunpingwang.ollamaagentoutcome.application.port.out.LoadProfilePort loadProfilePort,
            com.github.chunpingwang.ollamaagentoutcome.application.port.out.SaveProfilePort saveProfilePort) {
        return new UpdateProfileService(loadProfilePort, saveProfilePort);
    }

    @Bean
    DeleteProfileService deleteProfileService(
            com.github.chunpingwang.ollamaagentoutcome.application.port.out.LoadProfilePort loadProfilePort,
            com.github.chunpingwang.ollamaagentoutcome.application.port.out.RemoveProfilePort removeProfilePort) {
        return new DeleteProfileService(loadProfilePort, removeProfilePort);
    }
}
