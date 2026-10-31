package com.github.chunpingwang.ollamaagentoutcome.bootstrap;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

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
 * 驗證 UseCaseConfiguration 能否正確組裝所有 Use Case 與 Port Bean。
 */
@SpringBootTest
@Import(TestcontainersConfiguration.class)
class ApplicationConfigTest {

    @Autowired
    private CreateProfileUseCase createProfileUseCase;

    @Autowired
    private GetProfileUseCase getProfileUseCase;

    @Autowired
    private UpdateProfileUseCase updateProfileUseCase;

    @Autowired
    private DeleteProfileUseCase deleteProfileUseCase;

    @Autowired
    private SaveProfilePort saveProfilePort;

    @Autowired
    private LoadProfilePort loadProfilePort;

    @Autowired
    private DeleteProfilePort deleteProfilePort;

    @Autowired
    private ExistsByEmailPort existsByEmailPort;

    @Test
    void should_load_all_use_case_beans() {
        // Then
        assertThat(createProfileUseCase).isInstanceOf(CreateProfileService.class);
        assertThat(getProfileUseCase).isInstanceOf(GetProfileService.class);
        assertThat(updateProfileUseCase).isInstanceOf(UpdateProfileService.class);
        assertThat(deleteProfileUseCase).isInstanceOf(DeleteProfileService.class);
    }

    @Test
    void should_load_all_port_beans() {
        // Then
        assertThat(saveProfilePort).isInstanceOf(JpaSaveProfilePort.class);
        assertThat(loadProfilePort).isInstanceOf(JpaLoadProfilePort.class);
        assertThat(deleteProfilePort).isInstanceOf(JpaDeleteProfilePort.class);
        assertThat(existsByEmailPort).isInstanceOf(JpaExistsByEmailPort.class);
    }
}
