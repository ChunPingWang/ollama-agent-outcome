package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;



import com.github.chunpingwang.ollamaagentoutcome.domain.DisplayName;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;

import org.junit.jupiter.api.Test;

class ProfileConverterTest {

    private final ProfileConverter converter = new ProfileConverter();

    @Test
    void should_convert_domain_to_entity() {
        String id = UUID.randomUUID().toString();
        var domain = new com.github.chunpingwang.ollamaagentoutcome.domain.Profile(
                id, 
                new com.github.chunpingwang.ollamaagentoutcome.domain.Email("test@example.com"), 
                new com.github.chunpingwang.ollamaagentoutcome.domain.DisplayName("Name")
        );

        var entity = converter.toEntity(domain);

        assertThat(entity.getId()).isEqualTo(id);
        assertThat(entity.getEmail()).isEqualTo("test@example.com");
        assertThat(entity.getDisplayName()).isEqualTo("Name");
    }

    @Test
    void should_convert_entity_to_domain() {
        String id = UUID.randomUUID().toString();
        var entity = new ProfileEntity();
        entity.setId(id);
        entity.setEmail("test@example.com");
        entity.setDisplayName("Name");

        var domain = converter.toDomain(entity);

        assertThat(domain.id()).isEqualTo(id);
        assertThat(domain.email().address()).isEqualTo("test@example.com");
        assertThat(domain.displayName().name()).isEqualTo("Name");
    }
}
