package com.github.chunpingwang.ollamaagentoutcome.bootstrap;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.chunpingwang.ollamaagentoutcome.application.port.in.CreateProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.DeleteProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.GetProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.UpdateProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.DeleteProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.ExistsByEmailPort;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.LoadProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.SaveProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.application.usecase.CreateProfileService;
import com.github.chunpingwang.ollamaagentoutcome.application.usecase.DeleteProfileService;
import com.github.chunpingwang.ollamaagentoutcome.application.usecase.GetProfileService;
import com.github.chunpingwang.ollamaagentoutcome.application.usecase.UpdateProfileService;
import com.github.chunpingwang.ollamaagentoutcome.adapter.persistence.JpaDeleteProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.adapter.persistence.JpaExistsByEmailPort;
import com.github.chunpingwang.ollamaagentoutcome.adapter.persistence.JpaLoadProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.adapter.persistence.JpaSaveProfilePort;

/**
 * 應用程式組態類別，負責組裝 Use Case Services 與 Port 實作。
 * 此配置類將 Application 層的 Use Case 與 Persistence 層的 Port 實作進行綁定。
 */
@Configuration
public class UseCaseConfiguration {

    /**
     * 組裝建立個人資料的 Use Case。
     */
    @Bean
    CreateProfileUseCase createProfileUseCase(ExistsByEmailPort existsByEmailPort, SaveProfilePort saveProfilePort) {
        return new CreateProfileService(existsByEmailPort, saveProfilePort);
    }

    /**
     * 組裝查詢個人資料的 Use Case。
     */
    @Bean
    GetProfileUseCase getProfileUseCase(LoadProfilePort loadProfilePort) {
        return new GetProfileService(loadProfilePort);
    }

    /**
     * 組裝更新個人資料的 Use Case。
     */
    @Bean
    UpdateProfileUseCase updateProfileUseCase(LoadProfilePort loadProfilePort, SaveProfilePort saveProfilePort) {
        return new UpdateProfileService(loadProfilePort, saveProfilePort);
    }

    /**
     * 組裝刪除個人資料的 Use Case。
     */
    @Bean
    DeleteProfileUseCase deleteProfileUseCase(LoadProfilePort loadProfilePort, DeleteProfilePort deleteProfilePort) {
        return new DeleteProfileService(loadProfilePort, deleteProfilePort);
    }

    /**
     * 組裝儲存個人資料的 Port 實作。
     */
    @Bean
    SaveProfilePort saveProfilePort(com.github.chunpingwang.ollamaagentoutcome.adapter.persistence.ProfileJpaRepository repository) {
        return new JpaSaveProfilePort(repository);
    }

    /**
     * 組裝載入個人資料的 Port 實作。
     */
    @Bean
    LoadProfilePort loadProfilePort(com.github.chunpingwang.ollamaagentoutcome.adapter.persistence.ProfileJpaRepository repository) {
        return new JpaLoadProfilePort(repository);
    }

    /**
     * 組裝刪除個人資料的 Port 實作。
     */
    @Bean
    DeleteProfilePort deleteProfilePort(com.github.chunpingwang.ollamaagentoutcome.adapter.persistence.ProfileJpaRepository repository) {
        return new JpaDeleteProfilePort(repository);
    }

    /**
     * 組裝檢查電子郵件是否存在的 Port 實作。
     */
    @Bean
    ExistsByEmailPort existsByEmailPort(com.github.chunpingwang.ollamaagentoutcome.adapter.persistence.ProfileJpaRepository repository) {
        return new JpaExistsByEmailPort(repository);
    }
}
