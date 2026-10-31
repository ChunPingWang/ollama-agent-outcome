package com.github.chunpingwang.ollamaagentoutcome.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.github.chunpingwang.ollamaagentoutcome.domain.DuplicateEmailException;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.ExistsByEmailPort;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.SaveProfilePort;

@ExtendWith(MockitoExtension.class)
class CreateProfileServiceTest {

    @Mock
    private ExistsByEmailPort existsByEmailPort;

    @Mock
    private SaveProfilePort saveProfilePort;

    @InjectMocks
    private CreateProfileService createProfileService;

    @Test
    void should_create_profile_with_new_uuid() {
        String email = "new@example.com";
        String displayName = "New User";

        when(existsByEmailPort.exists(any(Email.class))).thenReturn(false);

        ProfileId resultId = createProfileService.create(email, displayName);

        assertThat(resultId).isNotNull();
        verify(saveProfilePort).save(argThat(profile -> profile.getId().equals(resultId)));
    }

    @Test
    void should_reject_duplicate_email() {
        String email = "duplicate@example.com";
        String displayName = "Duplicate User";

        when(existsByEmailPort.exists(any(Email.class))).thenReturn(true);

        assertThatThrownBy(() -> createProfileService.create(email, displayName))
                .isInstanceOf(DuplicateEmailException.class);

        verify(saveProfilePort, never()).save(any());
    }
}
