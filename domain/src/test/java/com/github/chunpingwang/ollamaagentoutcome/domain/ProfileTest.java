package com.github.chunpingwang.ollamaagentoutcome.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.github.chunpingwang.ollamaagentoutcome.domain.exception.InvalidEmailException;
import com.github.chunpingwang.ollamaagentoutcome.domain.exception.InvalidNameException;

import org.junit.jupiter.api.Test;

class ProfileTest {

    @Test
    void should_create_profile_with_valid_data() {
        ProfileId id = new ProfileId(java.util.UUID.randomUUID());
        String name = "John Doe";
        Email email = new Email("john@example.com");

        Profile profile = new Profile(id, name, email);

        assertThat(profile.id()).isEqualTo(id);
        assertThat(profile.name()).isEqualTo(name);
        assertThat(profile.email()).isEqualTo(email);
    }

    @Test
    void should_reject_null_name() {
        ProfileId id = new ProfileId(java.util.UUID.randomUUID());
        Email email = new Email("john@example.com");

        assertThatThrownBy(() -> new Profile(id, null, email))
                .isInstanceOf(InvalidNameException.class)
                .hasMessageContaining("Name cannot be null");
    }

    @Test
    void should_reject_blank_name() {
        ProfileId id = new ProfileId(java.util.UUID.randomUUID());
        Email email = new Email("john@example.com");

        assertThatThrownBy(() -> new Profile(id, "   ", email))
                .isInstanceOf(InvalidNameException.class)
                .hasMessageContaining("Name cannot be blank");
    }

    @Test
    void should_reject_null_email() {
        ProfileId id = new ProfileId(java.util.UUID.randomUUID());
        String name = "John Doe";

        assertThatThrownBy(() -> new Profile(id, name, null))
                .isInstanceOf(InvalidEmailException.class)
                .hasMessageContaining("Email cannot be null");
    }

    @Test
    void should_update_name() {
        ProfileId id = new ProfileId(java.util.UUID.randomUUID());
        Email email = new Email("john@example.com");
        Profile profile = new Profile(id, "John Doe", email);

        // updateName 應回傳新的 Profile 實例，原實例不變
        Profile updatedProfile = profile.updateName("Jane Doe");

        // 驗證新實例的資料
        assertThat(updatedProfile.name()).isEqualTo("Jane Doe");
        assertThat(updatedProfile.email()).isEqualTo(email);
        assertThat(updatedProfile.id()).isEqualTo(id);
        
        // 驗證原實例未被修改（不可變性）
        assertThat(profile.name()).isEqualTo("John Doe");
    }

    @Test
    void should_reject_blank_name_on_update() {
        ProfileId id = new ProfileId(java.util.UUID.randomUUID());
        Email email = new Email("john@example.com");
        Profile profile = new Profile(id, "John Doe", email);

        assertThatThrownBy(() -> profile.updateName("   "))
                .isInstanceOf(InvalidNameException.class)
                .hasMessageContaining("Name cannot be blank");
    }

    @Test
    void should_update_email() {
        ProfileId id = new ProfileId(java.util.UUID.randomUUID());
        Email oldEmail = new Email("john@example.com");
        Profile profile = new Profile(id, "John Doe", oldEmail);
        Email newEmail = new Email("jane@example.com");

        // updateEmail 應回傳新的 Profile 實例
        Profile updatedProfile = profile.updateEmail(newEmail);

        // 驗證新實例的資料
        assertThat(updatedProfile.email()).isEqualTo(newEmail);
        assertThat(updatedProfile.name()).isEqualTo("John Doe");
        assertThat(updatedProfile.id()).isEqualTo(id);

        // 驗證原實例未被修改（不可變性）
        assertThat(profile.email()).isEqualTo(oldEmail);
    }

    @Test
    void should_reject_invalid_email_on_update() {
        ProfileId id = new ProfileId(java.util.UUID.randomUUID());
        Email email = new Email("john@example.com");
        Profile profile = new Profile(id, "John Doe", email);

        assertThatThrownBy(() -> profile.updateEmail(new Email("invalid")))
                .isInstanceOf(InvalidEmailException.class)
                .hasMessageContaining("Invalid email format");
    }
}
