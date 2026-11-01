package com.github.chunpingwang.ollamaagentoutcome.application.usecase;





import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.github.chunpingwang.ollamaagentoutcome.application.port.in.CreateProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.SaveProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.DuplicateEmailException;
import com.github.chunpingwang.ollamaagentoutcome.domain.DisplayName;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith(MockitoExtension.class)
class CreateProfileServiceTest {

    @Mock
    private SaveProfilePort saveProfilePort;

    private CreateProfileService service;

    @BeforeEach
    void setUp() {
        service = new CreateProfileService(saveProfilePort);
    }

    @Test
    void should_create_profile_and_return_id_when_save_succeeds() {
        UUID testUuid = UUID.randomUUID();
        Email email = new Email("user@example.com");
        DisplayName displayName = new DisplayName("Display Name");
        
        Profile savedProfile = new Profile(new ProfileId(testUuid), email, displayName);
        
        when(saveProfilePort.save(any(Profile.class))).thenReturn(savedProfile);

        ProfileId resultId = service.execute(email, displayName);

        assertThat(resultId.id()).isEqualTo(testUuid);
        verify(saveProfilePort).save(any(Profile.class));
    }

    @Test
    void should_throw_duplicate_email_exception_when_port_throws_it() {
        Email duplicateEmail = new Email("duplicate@test.com");
        DisplayName displayName = new DisplayName("Name");
        
        when(saveProfilePort.save(any(Profile.class)))
                .thenThrow(new DuplicateEmailException(duplicateEmail.value()));

        assertThatThrownBy(() -> service.execute(duplicateEmail, displayName))
                .isInstanceOf(DuplicateEmailException.class)
                .hasMessageContaining("duplicate email: " + duplicateEmail.value());
                
        verify(saveProfilePort).save(any(Profile.class));
    }
}
