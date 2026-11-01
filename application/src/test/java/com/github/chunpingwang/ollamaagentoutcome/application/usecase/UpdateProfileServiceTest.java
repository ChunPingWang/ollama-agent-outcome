package com.github.chunpingwang.ollamaagentoutcome.application.usecase;



import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

import com.github.chunpingwang.ollamaagentoutcome.application.port.in.UpdateProfileRequest;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.LoadProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.SaveProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.DisplayName;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileNotFoundException;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class UpdateProfileServiceTest {

    @Mock
    private LoadProfilePort loadProfilePort;

    @Mock
    private SaveProfilePort saveProfilePort;

    private UpdateProfileService updateProfileService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        updateProfileService = new UpdateProfileService(loadProfilePort, saveProfilePort);
    }

    @Test
    void should_update_profile_successfully() {
        String idStr = UUID.randomUUID().toString();
        ProfileId id = new ProfileId(idStr);
        Email oldEmail = new Email("old@example.com");
        DisplayName oldName = new DisplayName("Old Name");
        
        Profile existingProfile = Profile.create(id, oldEmail, oldName);
        
        when(loadProfilePort.findById(idStr)).thenReturn(Optional.of(existingProfile));
        doNothing().when(saveProfilePort).save(any(Profile.class));

        UpdateProfileRequest request = new UpdateProfileRequest("new@example.com", "New Name");
        
        // Should not throw
        updateProfileService.execute(request, idStr);

        verify(loadProfilePort).findById(idStr);
        verify(saveProfilePort).save(argThat(p -> 
            p.getEmail().getAddress().equals("new@example.com") &&
            p.getDisplayName().equals("New Name")
        ));
    }

    @Test
    void should_throw_exception_when_profile_not_found() {
        String idStr = UUID.randomUUID().toString();
        
        when(loadProfilePort.findById(idStr)).thenReturn(Optional.empty());

        UpdateProfileRequest request = new UpdateProfileRequest("test@test.com", "Name");

        assertThatThrownBy(() -> updateProfileService.execute(request, idStr))
            .isInstanceOf(ProfileNotFoundException.class)
            .hasMessageContaining(idStr);
            
        verify(loadProfilePort).findById(idStr);
        verifyNoInteractions(saveProfilePort);
    }
}
