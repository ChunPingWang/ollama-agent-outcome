package com.github.chunpingwang.ollamaagentoutcome.application.usecase;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.github.chunpingwang.ollamaagentoutcome.domain.DuplicateEmailException;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.Name;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileNotFoundException;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.UpdateProfileUseCase.UpdateProfileRequest;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.LoadProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.SaveProfilePort;

@ExtendWith(MockitoExtension.class)
class UpdateProfileServiceTest {

    @Mock
    private LoadProfilePort loadProfilePort;

    @Mock
    private SaveProfilePort saveProfilePort;

    @InjectMocks
    private UpdateProfileService updateProfileService;

    @Test
    void should_update_profile_successfully() {
        String id = UUID.randomUUID().toString();
        Profile existingProfile = new Profile(
            new ProfileId(id), 
            new Name("舊名字"), 
            new Email("old@test.com")
        );

        when(loadProfilePort.findById(id)).thenReturn(Optional.of(existingProfile));
        doNothing().when(saveProfilePort).save(any());

        UpdateProfileRequest request = new UpdateProfileRequest(id, "新名字", "new@example.com");
        
        updateProfileService.execute(request);

        verify(saveProfilePort).save(any());
    }

    @Test
    void should_throw_not_found_exception_when_profile_does_not_exist() {
        String id = "non-existent-id";
        UpdateProfileRequest request = new UpdateProfileRequest(id, "新名字", "test@example.com");

        when(loadProfilePort.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> updateProfileService.execute(request))
                .isInstanceOf(ProfileNotFoundException.class)
                .hasMessageContaining(id);
        
        verify(saveProfilePort, org.mockito.Mockito.never()).save(any());
    }

    @Test
    void should_throw_duplicate_email_exception_on_save_failure() {
        String id = UUID.randomUUID().toString();
        Profile existingProfile = new Profile(
            new ProfileId(id), 
            new Name("舊名字"), 
            new Email("old@test.com")
        );

        when(loadProfilePort.findById(id)).thenReturn(Optional.of(existingProfile));
        doThrow(new DuplicateEmailException())
                .when(saveProfilePort).save(any());

        UpdateProfileRequest request = new UpdateProfileRequest(id, "新名字", "duplicate@test.com");

        assertThatThrownBy(() -> updateProfileService.execute(request))
                .isInstanceOf(DuplicateEmailException.class);
    }
}
