package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;

import static org.assertj.core.api.Assertions.assertThat;
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
import org.springframework.dao.DataIntegrityViolationException;

import com.github.chunpingwang.ollamaagentoutcome.domain.DuplicateEmailException;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;
import com.github.chunpingwang.ollamaagentoutcome.domain.Name;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.Bio;

/**
 * Unit tests for JpaSaveProfilePort using mocked Repository.
 */
@ExtendWith(MockitoExtension.class)
class JpaSaveProfilePortTest {

    @Mock
    private ProfileJpaRepository repository;

    @InjectMocks
    private JpaSaveProfilePort jpaSaveProfilePort;

    @Test
    void should_save_profile_and_convert_to_domain() {
        UUID id = UUID.randomUUID();
        String name = "John Doe";
        String email = "john@example.com";
        String bio = "Developer";

        // 直接使用 Domain 類別，移除多餘的內部 Helper Class
        Profile profile = new Profile(new ProfileId(id), new Name(name), new Email(email), new Bio(bio));
        
        ProfileEntity savedEntity = new ProfileEntity(id, name, email, bio);
        when(repository.save(any(ProfileEntity.class))).thenReturn(savedEntity);

        Profile result = jpaSaveProfilePort.save(profile);

        assertThat(result.id().value()).isEqualTo(id);
        assertThat(result.name().value()).isEqualTo(name);
        assertThat(result.email().value()).isEqualTo(email);
        verify(repository).save(any(ProfileEntity.class));
    }

    @Test
    void should_throw_duplicate_email_exception_on_data_integrity_violation() {
        Profile profile = new Profile(
                new ProfileId(UUID.randomUUID()), 
                new Name("Name"), 
                new Email("duplicate@test.com"), 
                new Bio("")
        );

        DataIntegrityViolationException dbEx = new DataIntegrityViolationException("Duplicate entry");
        when(repository.save(any(ProfileEntity.class))).thenThrow(dbEx);

        assertThatThrownBy(() -> jpaSaveProfilePort.save(profile))
            .isInstanceOf(DuplicateEmailException.class);
            
        verify(repository).save(any(ProfileEntity.class));
    }
}
