package com.github.chunpingwang.ollamaagentoutcome.bootstrap;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.chunpingwang.ollamaagentoutcome.application.port.in.CreateProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.DeleteProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.GetProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.UpdateProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.usecase.CreateProfileService;
import com.github.chunpingwang.ollamaagentoutcome.application.usecase.DeleteProfileService;
import com.github.chunpingwang.ollamaagentoutcome.application.usecase.GetProfileService;
import com.github.chunpingwang.ollamaagentoutcome.application.usecase.UpdateProfileService;
import com.github.chunpingwang.ollamaagentoutcome.adapter.persistence.port.out.JpaDeleteProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.adapter.persistence.port.out.JpaLoadProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.adapter.persistence.port.out.JpaSaveProfilePort;

/**
 * 組裝 application 層的 use case。
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
}
