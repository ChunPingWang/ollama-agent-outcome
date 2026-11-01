package com.github.chunpingwang.ollamaagentoutcome.application.usecase;





import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import com.github.chunpingwang.ollamaagentoutcome.domain.DisplayName;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileNotFoundException;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.LoadProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.SaveProfilePort;

@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith(MockitoExtension.class)
class UpdateProfileServiceTest {

    @Mock
    private LoadProfilePort loadProfilePort;

    @Mock
    private SaveProfilePort saveProfilePort;

    @InjectMocks
    private UpdateProfileService updateProfileService;

    @Test
    void should_update_profile_when_found() {
        String id = UUID.randomUUID().toString();
        Email existingEmail = new Email("old@example.com");
        DisplayName existingName = new DisplayName("Old Name");
        Profile existingProfile = Profile.create(new ProfileId(id), existingEmail, existingName);

        when(loadProfilePort.findById(id)).thenReturn(Optional.of(existingProfile));
        doNothing().when(saveProfilePort).save(any(Profile.class));

        // Act & Assert: 驗證不會拋出例外，且 port 被呼叫
        updateProfileService.execute(id, "new@example.com", "New Name");

        verify(loadProfilePort).findById(id);
        verify(saveProfilePort).save(any(Profile.class));
        
        // Verify the profile state was updated before saving
        verify(saveProfilePort).save(argThat(profile -> 
            profile.email().getAddress().equals("new@example.com") &&
            profile.displayName().getName().equals("New Name")
        ));
    }

    @Test
    void should_throw_exception_when_not_found_for_update() {
        String id = "non-existent-id";

        when(loadProfilePort.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> updateProfileService.execute(id, "test@test.com", "User"))
            .isInstanceOf(ProfileNotFoundException.class)
            .hasMessageContaining(id);

        verify(loadProfilePort).findById(id);
        verifyNoInteractions(saveProfilePort);
    }
}
