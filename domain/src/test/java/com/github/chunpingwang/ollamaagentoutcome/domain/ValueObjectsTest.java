package com.github.chunpingwang.ollamaagentoutcome.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.UUID;

import org.junit.jupiter.api.Test;

class ValueObjectsTest {

    @Test
    void should_create_profile_id_with_uuid() {
        UUID uuid = UUID.randomUUID();
        ProfileId id = new ProfileId(uuid);

        assertThat(id.value()).isEqualTo(uuid);
    }

    @Test
    void profile_id_should_be_immutable() {
        UUID originalUuid = UUID.randomUUID();
        ProfileId id1 = new ProfileId(originalUuid);
        
        // Record fields are effectively final, but we verify equality behavior
        ProfileId id2 = new ProfileId(originalUuid);
        assertThat(id1).isEqualTo(id2);
        assertThat(id1.hashCode()).isEqualTo(id2.hashCode());
    }

    @Test
    void name_should_accept_valid_string() {
        Name name = new Name("John Doe");
        assertThat(name.value()).isEqualTo("John Doe");
    }

    @Test
    void name_should_reject_null() {
        assertThatThrownBy(() -> new Name(null))
            .isInstanceOf(InvalidInputException.class)
            .hasMessageContaining("name cannot be null");
    }

    @Test
    void name_should_reject_blank() {
        assertThatThrownBy(() -> new Name(""))
            .isInstanceOf(InvalidInputException.class)
            .hasMessageContaining("name must not be blank");
            
        assertThatThrownBy(() -> new Name("   "))
            .isInstanceOf(InvalidInputException.class)
            .hasMessageContaining("name must not be blank");
    }

    @Test
    void email_should_accept_valid_format() {
        Email email = new Email("user@example.com");
        assertThat(email.value()).isEqualTo("user@example.com");
    }

    @Test
    void email_should_reject_null() {
        assertThatThrownBy(() -> new Email(null))
            .isInstanceOf(InvalidInputException.class)
            .hasMessageContaining("email cannot be null");
    }

    @Test
    void email_should_reject_blank() {
        assertThatThrownBy(() -> new Email(""))
            .isInstanceOf(InvalidInputException.class)
            .hasMessageContaining("email must not be blank");
    }

    @Test
    void bio_can_be_empty_or_present() {
        Bio emptyBio = new Bio("");
        assertThat(emptyBio.value()).isEmpty();

        Bio presentBio = new Bio("Hello World");
        assertThat(presentBio.value()).isEqualTo("Hello World");
        
        // Null should also be allowed for optional fields like Bio, 
        // but let's stick to strict non-null if VO is immutable record without default.
        // Assuming Bio allows null or empty string based on typical domain design.
        // If strict: new Bio(null) might throw. Let's assume it accepts null as "not set" or empty string.
        // Based on spec: "可為空或空白字串". Usually means String can be "" or null.
        // To keep VO simple, we'll allow null in constructor or treat null as empty.
        // Let's implement Bio to accept null and normalize to empty or just store null?
        // Record with nullable field: public record Bio(String value) {}
        // But validation logic usually handles nulls.
        // Let's make Bio handle null gracefully by treating it as empty or allowing it.
        // For simplicity in TDD RED phase, we test the expected behavior defined in Plan.
        // Plan says: "可為空或空白字串". We will allow null input and maybe normalize or just store.
        // Let's try creating with null first.
        Bio nullBio = new Bio(null);
        assertThat(nullBio.value()).isNull(); // Or assertEmpty() if normalized. Let's assume raw storage for now unless specified otherwise. 
        // Actually, better practice is often non-null. But let's stick to flexible.
    }
}
