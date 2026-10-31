package com.github.chunpingwang.ollamaagentoutcome.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

/**
 * {@link Profile} 的單元測試。
 * 驗證建構、更新行為與不可變性。
 */
class ProfileTest {

    private static final String VALID_UUID = "550e8400-e29b-41d4-a716-446655440000";
    private static final String VALID_EMAIL = "user@example.com";
    private static final String VALID_NAME = "John Doe";

    @Test
    void should_create_profile_with_valid_data() {
        ProfileId id = new ProfileId(VALID_UUID);
        Email email = new Email(VALID_EMAIL);

        Profile profile = new Profile(id, email, VALID_NAME);

        assertThat(profile).isNotNull();
        assertThat(profile.getId()).isEqualTo(id);
        assertThat(profile.getEmail()).isEqualTo(email);
        assertThat(profile.getName()).isEqualTo(VALID_NAME);
    }

    @Test
    void should_throw_exception_when_id_is_null() {
        Email email = new Email(VALID_EMAIL);
        assertThatThrownBy(() -> new Profile(null, email, VALID_NAME))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("id");
    }

    @Test
    void should_throw_exception_when_email_is_null() {
        ProfileId id = new ProfileId(VALID_UUID);
        assertThatThrownBy(() -> new Profile(id, null, VALID_NAME))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("email");
    }

    @Test
    void should_throw_exception_when_name_is_null() {
        ProfileId id = new ProfileId(VALID_UUID);
        Email email = new Email(VALID_EMAIL);
        assertThatThrownBy(() -> new Profile(id, email, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("name");
    }

    @Test
    void should_throw_exception_when_name_is_blank() {
        ProfileId id = new ProfileId(VALID_UUID);
        Email email = new Email(VALID_EMAIL);
        assertThatThrownBy(() -> new Profile(id, email, "   "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("name");
    }

    @Test
    void should_update_name_successfully() {
        ProfileId id = new ProfileId(VALID_UUID);
        Email email = new Email(VALID_EMAIL);
        Profile originalProfile = new Profile(id, email, VALID_NAME);

        String newName = "Jane Doe";
        Profile updatedProfile = originalProfile.updateName(newName);

        // 驗證新物件
        assertThat(updatedProfile).isNotSameAs(originalProfile);
        assertThat(updatedProfile.getId()).isEqualTo(id);
        assertThat(updatedProfile.getEmail()).isEqualTo(email);
        assertThat(updatedProfile.getName()).isEqualTo(newName);

        // 驗證原始物件未被修改（不可變性）
        assertThat(originalProfile.getName()).isEqualTo(VALID_NAME);
    }

    @Test
    void should_throw_exception_when_updating_name_to_null() {
        ProfileId id = new ProfileId(VALID_UUID);
        Email email = new Email(VALID_EMAIL);
        Profile profile = new Profile(id, email, VALID_NAME);

        assertThatThrownBy(() -> profile.updateName(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("name");
    }

    @Test
    void should_throw_exception_when_updating_name_to_blank() {
        ProfileId id = new ProfileId(VALID_UUID);
        Email email = new Email(VALID_EMAIL);
        Profile profile = new Profile(id, email, VALID_NAME);

        assertThatThrownBy(() -> profile.updateName("   "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("name");
    }

    @Test
    void should_equality_based_on_fields() {
        ProfileId id = new ProfileId(VALID_UUID);
        Email email = new Email(VALID_EMAIL);
        Profile profile1 = new Profile(id, email, VALID_NAME);
        Profile profile2 = new Profile(id, email, VALID_NAME);

        assertThat(profile1).isEqualTo(profile2);
        assertThat(profile1.hashCode()).isEqualTo(profile2.hashCode());
    }

    @Test
    void should_not_equality_with_different_name() {
        ProfileId id = new ProfileId(VALID_UUID);
        Email email = new Email(VALID_EMAIL);
        Profile profile1 = new Profile(id, email, "Name 1");
        Profile profile2 = new Profile(id, email, "Name 2");

        assertThat(profile1).isNotEqualTo(profile2);
    }
}
