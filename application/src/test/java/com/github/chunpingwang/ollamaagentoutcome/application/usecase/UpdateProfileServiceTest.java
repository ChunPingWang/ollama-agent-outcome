package com.github.chunpingwang.ollamaagentoutcome.application.usecase;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.github.chunpingwang.ollamaagentoutcome.domain.DuplicateEmailException;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileNotFoundException;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.UpdateProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.FindProfileByIdPort;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.SaveProfilePort;

@ExtendWith(MockitoExtension.class)
class UpdateProfileServiceTest {

    @Mock
    private FindProfileByIdPort findProfileByIdPort;

    @Mock
    private SaveProfilePort saveProfilePort;

    @InjectMocks
    private UpdateProfileService updateProfileService;

    @Test
    void should_update_profile_and_call_save_port() {
        UUID id = UUID.randomUUID();
        String newName = "新名字";
        String newEmail = "new@example.com";

        Profile existingProfile = new Profile(id, "舊名字", "old@test.com");

        when(findProfileByIdPort.findById(id)).thenReturn(Optional.of(existingProfile));

        updateProfileService.execute(id, newName, newEmail);

        verify(saveProfilePort).save(ArgumentMatchers.argThat(profile -> 
            profile.getId().equals(id) && 
            profile.getName().equals(newName) && 
            profile.getEmail().equals(newEmail)));
    }

    @Test
    void should_throw_exception_when_profile_not_found_for_update() {
        UUID id = UUID.randomUUID();

        when(findProfileByIdPort.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> updateProfileService.execute(id, "New Name", "new@test.com"))
                .isInstanceOf(ProfileNotFoundException.class)
                .hasMessageContaining("not found");
    }

    @Test
    void should_propagate_duplicate_email_exception_during_update() {
        UUID id = UUID.randomUUID();
        String newName = "更新的名字";
        String duplicateEmail = "duplicate@test.com";

        Profile existingProfile = new Profile(id, "舊名字", "old@test.com");

        when(findProfileByIdPort.findById(id)).thenReturn(Optional.of(existingProfile));
        doThrow(new DuplicateEmailException("Duplicate email: " + duplicateEmail))
                .when(saveProfilePort).save(ArgumentMatchers.any());

        assertThatThrownBy(() -> updateProfileService.execute(id, newName, duplicateEmail))
                .isInstanceOf(DuplicateEmailException.class)
                .hasMessageContaining("Duplicate email");
    }
}
