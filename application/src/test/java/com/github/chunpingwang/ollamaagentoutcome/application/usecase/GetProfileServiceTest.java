package com.github.chunpingwang.ollamaagentoutcome.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
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
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.LoadProfilePort;

@ExtendWith(MockitoExtension.class)
class GetProfileServiceTest {

    @Mock
    private LoadProfilePort loadProfilePort;

    @InjectMocks
    private GetProfileService getProfileService;

    @Test
    void should_return_profile_when_exists() {
        // given
        String id = UUID.randomUUID().toString();
        Profile profile = createValidProfile(id);
        
        when(loadProfilePort.findById(id)).thenReturn(Optional.of(profile));

        // when
        Optional<Profile> result = getProfileService.execute(id);

        // then
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualToComparingFieldByFieldRecursively(profile);
    }

    @Test
    void should_return_empty_optional_when_profile_does_not_exist() {
        // given
        String nonExistentId = "non-existent-id";
        when(loadProfilePort.findById(nonExistentId)).thenReturn(Optional.empty());

        // when & then
        Optional<Profile> result = getProfileService.execute(nonExistentId);
        assertThat(result).isEmpty();
    }

    private Profile createValidProfile(String idStr) {
        return Profile.create(
            new ProfileId(idStr), 
            "test@example.com", 
            "Test User"
        );
    }
}
