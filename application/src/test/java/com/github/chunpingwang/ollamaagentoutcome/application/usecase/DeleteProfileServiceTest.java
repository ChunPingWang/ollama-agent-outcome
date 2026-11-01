package com.github.chunpingwang.ollamaagentoutcome.application.usecase;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileNotFoundException;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.DeleteProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.LoadProfilePort;

@ExtendWith(MockitoExtension.class)
class DeleteProfileServiceTest {

    private final UUID testId = UUID.randomUUID();
    
    @Mock
    private LoadProfilePort loadProfilePort;
    
    @Mock
    private DeleteProfilePort deleteProfilePort;

    @InjectMocks
    private DeleteProfileService deleteProfileService;

    @Test
    void should_delete_profile_when_exists() {
        Profile existingProfile = new Profile(
            new ProfileId(testId), 
            new com.github.chunpingwang.ollamaagentoutcome.domain.Email("test@example.com"), 
            new com.github.chunpingwang.ollamaagentoutcome.domain.DisplayName("User")
        );
        
        when(loadProfilePort.findById(new ProfileId(testId))).thenReturn(Optional.of(existingProfile));

        deleteProfileService.execute(testId);

        verify(deleteProfilePort).deleteById(new ProfileId(testId));
    }

    @Test
    void should_throw_not_found_exception_when_profile_does_not_exist() {
        when(loadProfilePort.findById(new ProfileId(testId))).thenReturn(Optional.empty());

        assertThatThrownBy(() -> 
            deleteProfileService.execute(testId)
        ).isInstanceOf(ProfileNotFoundException.class)
         .hasMessageContaining(testId.toString());
         
        // 驗證因為找不到資料，所以不會呼叫 delete
        verify(deleteProfilePort, never()).deleteById(org.mockito.ArgumentMatchers.any());
    }
}
