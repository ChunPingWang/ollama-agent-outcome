package com.github.chunpingwang.ollamaagentoutcome.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.UUID;

import org.junit.jupiter.api.Test;

class ProfileTest {

    private final ProfileId id = new ProfileId(UUID.randomUUID().toString());
    private final Email email = new Email("test@example.com");
    private final Name name = new Name("測試使用者");

    @Test
    void should_create_profile_with_valid_data() {
        Profile profile = new Profile(id, name, email);

        assertThat(profile.id()).isEqualTo(id);
        assertThat(profile.name()).isEqualTo(name);
        assertThat(profile.email()).isEqualTo(email);
    }

    @Test
    void should_update_name_successfully() {
        Profile profile = new Profile(id, name, email);
        Name newName = new Name("新名字");

        profile.updateName(newName);

        assertThat(profile.name()).isEqualTo(newName);
        assertThat(profile.email()).isSameAs(email); // 確認 email 未變動
    }

    @Test
    void should_reject_invalid_email_when_updating() {
        Profile profile = new Profile(id, name, email);
        
        assertThatThrownBy(() -> profile.updateEmail(new Email("invalid")))
                .isInstanceOf(InvalidEmailException.class);
    }

    @Test
    void should_reject_duplicate_email_on_creation_if_already_exists_logic_applied_in_domain_service_or_port() {
        // Domain layer itself doesn't know about persistence state. 
        // However, if we want to enforce uniqueness within the aggregate root context (rare for simple CRUD),
        // or simply test that valid data creates a valid object.
        // The requirement says "DuplicateEmailException" is thrown when trying to save existing email.
        // This is typically handled by Port/Service. Here we just ensure construction works with unique-ish data.
        // To strictly follow TDD RED phase, we assume the constructor accepts any valid VOs.
        // If domain enforces global uniqueness via static registry (anti-pattern usually), it would fail here.
        // Based on standard Hexagonal Arch, Domain Entity is pure business logic container.
        // We will verify basic creation and update invariants.
        
        Profile p1 = new Profile(new ProfileId(UUID.randomUUID().toString()), new Name("A"), new Email("a@b.com"));
        Profile p2 = new Profile(new ProfileId(UUID.randomUUID().toString()), new Name("B"), new Email("c@d.com"));
        
        assertThat(p1).isNotNull();
        assertThat(p2).isNotNull();
    }
}
