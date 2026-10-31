package com.github.chunpingwang.ollamaagentoutcome.application.usecase;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.github.chunpingwang.ollamaagentoutcome.domain.DuplicateEmailException;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.SaveProfilePort;

@ExtendWith(MockitoExtension.class)
class CreateProfileServiceTest {

    @Mock
    private SaveProfilePort saveProfilePort;

    @InjectMocks
    private CreateProfileService createProfileService;

    @Test
    void should_create_profile_and_call_save_port() {
        String name = "王小明";
        String email = "xiaoming@example.com";

        createProfileService.execute(name, email);

        verify(saveProfilePort).save(ArgumentMatchers.argThat(profile -> 
            profile.getName().equals(name) && profile.getEmail().equals(email)));
    }

    @Test
    void should_throw_duplicate_email_exception_when_port_throws_it() {
        String name = "李大明";
        String email = "duplicate@test.com";

        doThrow(new DuplicateEmailException("Duplicate email: " + email))
                .when(saveProfilePort).save(ArgumentMatchers.any());

        assertThatThrownBy(() -> createProfileService.execute(name, email))
                .isInstanceOf(DuplicateEmailException.class)
                .hasMessageContaining("Duplicate email");
    }
}
