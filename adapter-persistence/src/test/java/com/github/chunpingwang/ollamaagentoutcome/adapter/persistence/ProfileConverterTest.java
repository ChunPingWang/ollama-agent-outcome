package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;



import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.github.chunpingwang.ollamaagentoutcome.domain.DisplayName;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class ProfileConverterTest {

    @Test
    void should_convert_domain_to_entity_with_is_new_true() {
        UUID id = UUID.randomUUID();
        String emailStr = "conv@example.com";
        String displayNameStr = "轉換測試";

        Profile profile = new Profile(new ProfileId(id), new Email(emailStr), new DisplayName(displayNameStr));
        
        ProfileEntity entity = ProfileConverter.toEntity(profile);

        assertThat(entity.getId()).isEqualTo(id);
        assertThat(entity.getEmail()).isEqualTo(emailStr);
        assertThat(entity.getDisplayName()).isEqualTo(displayNameStr);
        assertThat(entity.isNew()).isTrue();
    }

    @Test
    void should_convert_entity_to_domain_preserving_state() {
        UUID id = UUID.randomUUID();
        String emailStr = "rev@example.com";
        String displayNameStr = "反向轉換";

        ProfileEntity entity = new ProfileEntity();
        entity.setId(id);
        entity.setEmail(emailStr);
        entity.setDisplayName(displayNameStr);
        // 模擬持久化後的狀態轉移 (isNew -> false)
        entity.isNew = false;

        Profile profile = ProfileConverter.toDomain(entity);

        assertThat(profile.id().value()).isEqualTo(id);
        assertThat(profile.email().value()).isEqualTo(emailStr);
        assertThat(profile.displayName().value()).isEqualTo(displayNameStr);
    }
}
