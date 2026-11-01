package com.github.chunpingwang.ollamaagentoutcome.bootstrap;




import org.springframework.context.annotation.Import;
import static org.assertj.core.api.Assertions.*;import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;

import com.github.chunpingwang.ollamaagentoutcome.application.port.in.CreateProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.GetProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.UpdateProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.DeleteProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.usecase.CreateProfileService;
import com.github.chunpingwang.ollamaagentoutcome.application.usecase.GetProfileService;
import com.github.chunpingwang.ollamaagentoutcome.application.usecase.UpdateProfileService;
import com.github.chunpingwang.ollamaagentoutcome.application.usecase.DeleteProfileService;
import com.github.chunpingwang.ollamaagentoutcome.adapter.persistence.JpaSaveProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.adapter.persistence.JpaLoadProfileByIdPort;
import com.github.chunpingwang.ollamaagentoutcome.adapter.persistence.JpaDeleteProfilePort;

/**
 * 驗證 Bootstrap Configuration 是否正確組裝了所有 Application Services 與 Persistence Ports。
 * 此測試在 T5 RED 階段應失敗，因為 UseCaseConfiguration 尚未註冊這些 Bean。
 */
@Import(com.github.chunpingwang.ollamaagentoutcome.bootstrap.TestcontainersConfiguration.class)  // 自動補上：測試需要 PostgreSQL 容器
@SpringBootTest
@ActiveProfiles("test") // 確保使用 test profile (若有的話)，通常預設即可
class UseCaseAssemblyTest {

    @Autowired
    private ApplicationContext context;

    @Test
    void should_have_create_profile_service_bean() {
        assertThat(context.getBean(CreateProfileUseCase.class))
                .isInstanceOf(CreateProfileService.class);
    }

    @Test
    void should_have_get_profile_service_bean() {
        assertThat(context.getBean(GetProfileUseCase.class))
                .isInstanceOf(GetProfileService.class);
    }

    @Test
    void should_have_update_profile_service_bean() {
        assertThat(context.getBean(UpdateProfileUseCase.class))
                .isInstanceOf(UpdateProfileService.class);
    }

    @Test
    void should_have_delete_profile_service_bean() {
        assertThat(context.getBean(DeleteProfileUseCase.class))
                .isInstanceOf(DeleteProfileService.class);
    }

    @Test
    void should_have_jpa_save_profile_port_bean() {
        assertThat(context.getBean(JpaSaveProfilePort.class)).isNotNull();
    }

    @Test
    void should_have_jpa_load_profile_by_id_port_bean() {
        assertThat(context.getBean(JpaLoadProfileByIdPort.class)).isNotNull();
    }

    @Test
    void should_have_jpa_delete_profile_port_bean() {
        assertThat(context.getBean(JpaDeleteProfilePort.class)).isNotNull();
    }
}
