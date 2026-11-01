package com.github.chunpingwang.ollamaagentoutcome.application.usecase;





import com.github.chunpingwang.ollamaagentoutcome.application.port.in.UpdateProfileUseCase;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.github.chunpingwang.ollamaagentoutcome.application.port.in.UpdateProfileUseCase.UpdateRequest;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.LoadProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.SaveProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.DisplayName;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileNotFoundException;

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
    void should_load_update_and_save_profile() {
        // given
        String id = UUID.randomUUID().toString();
        Profile existingProfile = createDomainProfile(id, "old@example.com", "Old Name");
        
        when(loadProfilePort.findById(id)).thenReturn(Optional.of(existingProfile));
        doNothing().when(saveProfilePort).save(org.mockito.ArgumentMatchers.any(Profile.class));

        UpdateRequest request = new UpdateRequest("new@example.com", "New Name");

        // when
        updateProfileService.execute(request, id);

        // then
        verify(loadProfilePort).findById(id);
        verify(saveProfilePort).save(org.mockito.ArgumentMatchers.argThat(profile -> 
            profile.email().address().equals("new@example.com") && 
            profile.displayName().name().equals("New Name")
        ));
    }

    @Test
    void should_throw_exception_when_not_found_for_update() {
        // given
        String id = UUID.randomUUID().toString();
        when(loadProfilePort.findById(id)).thenReturn(Optional.empty());

        UpdateRequest request = new UpdateRequest("test@test.com", "Name");

        // when & then
        assertThatThrownBy(() -> updateProfileService.execute(request, id))
                .isInstanceOf(ProfileNotFoundException.class)
                .hasMessageContaining(id);
        
        verify(loadProfilePort).findById(id);
        verifyNoInteractions(saveProfilePort);
    }
    
    private Profile createDomainProfile(String id, String email, String displayName) {
        return Profile.create(
            new ProfileId(id), 
            new Email(email), 
            new DisplayName(displayName)
        );
    }
}
