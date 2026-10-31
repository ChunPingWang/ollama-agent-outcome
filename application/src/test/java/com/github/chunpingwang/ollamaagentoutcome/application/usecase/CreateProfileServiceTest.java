package com.github.chunpingwang.ollamaagentoutcome.application.usecase;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
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
import com.github.chunpingwang.ollamaagentoutcome.domain.Name;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;
import com.github.chunpingwang.ollamaagentoutcome.domain.Bio;
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
    void should_create_profile_when_email_is_unique() {
        UUID id = UUID.randomUUID();
        String name = "John Doe";
        String email = "john@example.com";
        String bio = "Developer";

        when(existsByEmailPort.existsByEmail(email)).thenReturn(false);
        
        Profile savedProfile = new Profile(new ProfileId(id), new Name(name), new Email(email), new Bio(bio));
        // Mocking return value for verification, though execute is void in interface? 
        // Interface says void. But Service might not need to return it if interface is void.
        // Let's stick to verify interaction.
        
        CreateProfileCommand command = new CreateProfileCommand(name, email, bio);
        createProfileService.execute(command);

        verify(existsByEmailPort).existsByEmail(email);
        verify(saveProfilePort).save(any(Profile.class));
    }

    @Test
    void should_throw_duplicate_email_exception_when_email_exists() {
        String email = "duplicate@example.com";

        when(existsByEmailPort.existsByEmail(email)).thenReturn(true);

        CreateProfileCommand command = new CreateProfileCommand("Name", email, "Bio");

        assertThatThrownBy(() -> createProfileService.execute(command))
            .isInstanceOf(DuplicateEmailException.class)
            .hasMessageContaining("email already exists");

        verify(existsByEmailPort).existsByEmail(email);
        verify(saveProfilePort, never()).save(any());
    }
}
