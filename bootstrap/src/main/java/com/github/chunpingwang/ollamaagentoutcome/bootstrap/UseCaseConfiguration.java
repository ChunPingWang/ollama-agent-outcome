package com.github.chunpingwang.ollamaagentoutcome.bootstrap;

import javax.sql.DataSource;

import jakarta.persistence.EntityManagerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import com.github.chunpingwang.ollamaagentoutcome.application.port.in.CreateProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.DeleteProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.GetProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.UpdateProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.usecase.CreateProfileService;
import com.github.chunpingwang.ollamaagentoutcome.application.usecase.DeleteProfileService;
import com.github.chunpingwang.ollamaagentoutcome.application.usecase.GetProfileService;
import com.github.chunpingwang.ollamaagentoutcome.application.usecase.UpdateProfileService;
import com.github.chunpingwang.ollamaagentoutcome.adapter.persistence.port.JpaDeleteProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.adapter.persistence.port.JpaLoadProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.adapter.persistence.port.JpaSaveProfilePort;

/**
 * 組裝 application 層的 use case。依賴 Spring Boot Auto-configuration 處理基礎設施（JPA/DB）。
 */
@Configuration
@EnableJpaRepositories(basePackages = "com.github.chunpingwang.ollamaagentoutcome.adapter.persistence")
@ComponentScan(basePackages = {
    "com.github.chunpingwang.ollamaagentoutcome.adapter.persistence",
    "com.github.chunpingwang.ollamaagentoutcome.bootstrap"
})
public class UseCaseConfiguration {

    private final JpaSaveProfilePort saveProfilePort;
    private final JpaLoadProfilePort loadProfilePort;
    private final JpaDeleteProfilePort deleteProfilePort;

    public UseCaseConfiguration(
            JpaSaveProfilePort saveProfilePort,
            JpaLoadProfilePort loadProfilePort,
            JpaDeleteProfilePort deleteProfilePort) {
        this.saveProfilePort = saveProfilePort;
        this.loadProfilePort = loadProfilePort;
        this.deleteProfilePort = deleteProfilePort;
    }

    @Bean
    CreateProfileUseCase createProfileService() {
        return new CreateProfileService(saveProfilePort);
    }

    @Bean
    GetProfileUseCase getProfileService() {
        return new GetProfileService(loadProfilePort);
    }

    @Bean
    UpdateProfileService updateProfileService() {
        return new UpdateProfileService(loadProfilePort, saveProfilePort);
    }

    @Bean
    DeleteProfileUseCase deleteProfileService() {
        return new DeleteProfileService(deleteProfilePort);
    }

    /**
     * 明確宣告 TransactionManager，確保 Spring Boot 自動組裝的 DataSource 與 EntityManagerFactory 
     * 能正確被 Port 實作（如 JpaSaveProfilePort）使用。
     */
    @Bean
    PlatformTransactionManager transactionManager(EntityManagerFactory emf) {
        return new JpaTransactionManager(emf);
    }
}
