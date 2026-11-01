package com.github.chunpingwang.ollamaagentoutcome.application.usecase;




import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import com.github.chunpingwang.ollamaagentoutcome.application.port.out.SaveProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.DuplicateEmailException;
import com.github.chunpingwang.ollamaagentoutcome.domain.DisplayName;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;

@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith(MockitoExtension.class)
class CreateProfileServiceTest {

    @Mock
    private SaveProfilePort saveProfilePort;

    @InjectMocks
    private CreateProfileService createProfileService;

    @Test
    void should_create_profile_successfully() {
        // given
        String email = "user@example.com";
        String displayName = "John Doe";
        
        Profile expectedProfile = new Profile(
            new ProfileId(UUID.randomUUID()), 
            new Email(email), 
            new DisplayName(displayName)
        );
        
        when(saveProfilePort.save(any(Profile.class))).thenReturn(expectedProfile);

        // when & then
        assertThatNoException().isThrownBy(() -> 
            createProfileService.execute(new CreateProfileCommand(email, displayName))
        );
        
        verify(saveProfilePort).save(argThat(profile -> 
            profile.email().value().equals(email) && 
            profile.displayName().value().equals(displayName)
        ));
    }

    @Test
    void should_propagate_duplicate_email_exception() {
        // given
        String email = "duplicate@test.com";
        String displayName = "Another User";
        
        doThrow(new DuplicateEmailException("email already exists"))
            .when(saveProfilePort).save(any(Profile.class));

        // when & then
        assertThatThrownBy(() -> 
            createProfileService.execute(new CreateProfileCommand(email, displayName))
        ).isInstanceOf(DuplicateEmailException.class)
         .hasMessageContaining("email already exists");
    }
}
