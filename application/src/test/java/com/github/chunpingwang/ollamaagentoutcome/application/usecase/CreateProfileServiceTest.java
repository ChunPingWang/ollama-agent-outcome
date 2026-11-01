package com.github.chunpingwang.ollamaagentoutcome.application.usecase;


import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.openMocks;

import com.github.chunpingwang.ollamaagentoutcome.application.port.in.CreateProfileRequest;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.SaveProfilePort;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

class CreateProfileServiceTest {

    @Mock
    private SaveProfilePort saveProfilePort;

    private CreateProfileService createProfileService;

    @BeforeEach
    void setUp() {
        openMocks(this);
        createProfileService = new CreateProfileService(saveProfilePort);
    }

    @Test
    void should_create_profile_successfully() {
        String email = "test@example.com";
        String displayName = "Test User";
        
        CreateProfileRequest request = new CreateProfileRequest(email, displayName);
        
        doNothing().when(saveProfilePort).save(any());
        
        assertThatCode(() -> createProfileService.execute(request))
            .doesNotThrowAnyException();
            
        verify(saveProfilePort).save(any());
    }
}
