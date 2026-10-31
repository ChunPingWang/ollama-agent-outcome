package com.github.chunpingwang.ollamaagentoutcome.application.usecase;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.github.chunpingwang.ollamaagentoutcome.application.port.out.LoadProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.SaveProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.exception.DuplicateEmailException;
import com.github.chunpingwang.ollamaagentoutcome.domain.model.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.model.Name;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CreateProfileServiceTest {

    @Mock
    private LoadProfilePort loadProfilePort;

    @Mock
    private SaveProfilePort saveProfilePort;

    @InjectMocks
    private CreateProfileService createProfileService;

    @Test
    void should_create_profile_successfully() {
        // Given
        Name name = new Name("王小明");
        Email email = new Email("xiaoming@example.com");
        when(loadProfilePort.existsByEmail(email)).thenReturn(false);

        // When
        createProfileService.execute(name, email);

        // Then
        verify(loadProfilePort).existsByEmail(email);
        verify(saveProfilePort).save(any());
    }

    @Test
    void should_throw_duplicate_email_exception_when_email_exists() {
        // Given
        Name name = new Name("李大明");
        Email email = new Email("duplicate@test.com");
        when(loadProfilePort.existsByEmail(email)).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> createProfileService.execute(name, email))
                .isInstanceOf(DuplicateEmailException.class)
                .hasMessageContaining("email already exists");
        
        verify(loadProfilePort).existsByEmail(email);
        verify(saveProfilePort, org.mockito.Mockito.never()).save(any());
    }
}
