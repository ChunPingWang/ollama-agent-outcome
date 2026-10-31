package com.github.chunpingwang.ollamaagentoutcome.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class ProfileTest {

    @Nested
    @DisplayName("ProfileId 值物件測試")
    class ProfileIdTests {

        @Test
        @DisplayName("應能建立有效的 ProfileId")
        void should_create_valid_profile_id() {
            String uuid = UUID.randomUUID().toString();
            ProfileId id = new ProfileId(uuid);
            assertThat(id.value()).isEqualTo(uuid);
        }

        @Test
        @DisplayName("建立 ProfileId 時，若 ID 為 null 應拋出例外")
        void should_throw_when_id_is_null() {
            assertThatThrownBy(() -> new ProfileId(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("ID 不可為 null");
        }

        @Test
        @DisplayName("建立 ProfileId 時，若 ID 為空白字串應拋出例外")
        void should_throw_when_id_is_blank() {
            assertThatThrownBy(() -> new ProfileId("   "))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("ID 不可為空白");
        }
    }

    @Nested
    @DisplayName("Email 值物件測試")
    class EmailTests {

        @Test
        @DisplayName("應能建立有效的 Email")
        void should_create_valid_email() {
            Email email = new Email("user@example.com");
            assertThat(email.value()).isEqualTo("user@example.com");
        }

        @Test
        @DisplayName("建立 Email 時，若為 null 應拋出例外")
        void should_throw_when_email_is_null() {
            assertThatThrownBy(() -> new Email(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("電子郵件不可為 null");
        }

        @Test
        @DisplayName("建立 Email 時，若格式無效應拋出例外")
        void should_throw_when_email_format_is_invalid() {
            assertThatThrownBy(() -> new Email("invalid-email"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("電子郵件格式無效");
        }
    }

    @Nested
    @DisplayName("Name 值物件測試")
    class NameTests {

        @Test
        @DisplayName("應能建立有效的 Name")
        void should_create_valid_name() {
            Name name = new Name("Alice");
            assertThat(name.value()).isEqualTo("Alice");
        }

        @Test
        @DisplayName("建立 Name 時，若為 null 應拋出例外")
        void should_throw_when_name_is_null() {
            assertThatThrownBy(() -> new Name(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("姓名不可為 null");
        }

        @Test
        @DisplayName("建立 Name 時，若為空白字串應拋出例外")
        void should_throw_when_name_is_blank() {
            assertThatThrownBy(() -> new Name("   "))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("姓名不可為空白");
        }
    }

    @Nested
    @DisplayName("Bio 值物件測試")
    class BioTests {

        @Test
        @DisplayName("應能建立有效的 Bio")
        void should_create_valid_bio() {
            Bio bio = new Bio("Hello World");
            assertThat(bio.value()).isEqualTo("Hello World");
        }

        @Test
        @DisplayName("建立 Bio 時，若為 null 應視為空字串")
        void should_treat_null_bio_as_empty_string() {
            Bio bio = new Bio(null);
            assertThat(bio.value()).isEmpty();
        }
    }

    @Nested
    @DisplayName("Profile 實體測試")
    class ProfileTests {

        @Test
        @DisplayName("應能建立有效的 Profile")
        void should_create_valid_profile() {
            ProfileId id = new ProfileId(UUID.randomUUID().toString());
            Email email = new Email("alice@example.com");
            Name name = new Name("Alice");
            Bio bio = new Bio("Developer");

            Profile profile = new Profile(id, email, name, bio);

            assertThat(profile.id()).isEqualTo(id);
            assertThat(profile.email()).isEqualTo(email);
            assertThat(profile.name()).isEqualTo(name);
            assertThat(profile.bio()).isEqualTo(bio);
        }

        @Test
        @DisplayName("Profile 應為不可變物件")
        void should_be_immutable() {
            ProfileId id = new ProfileId(UUID.randomUUID().toString());
            Email email = new Email("alice@example.com");
            Name name = new Name("Alice");
            Bio bio = new Bio("Developer");

            Profile profile = new Profile(id, email, name, bio);

            // 嘗試修改 bio (假設 bio 是 record，其 value 是 final)
            // 這裡主要測試 Profile record 本身不可變，且內部值物件也不應被意外修改
            // 由於 Java record 的欄位是 final，直接修改欄位會編譯錯誤，
            // 我們主要確保 update 回傳新實例，原實例不變
            Profile updatedProfile = profile.update(new Name("Bob"), new Bio("New Bio"));

            assertThat(updatedProfile.name().value()).isEqualTo("Bob");
            assertThat(updatedProfile.bio().value()).isEqualTo("New Bio");
            
            // 原 profile 不應改變
            assertThat(profile.name().value()).isEqualTo("Alice");
            assertThat(profile.bio().value()).isEqualTo("Developer");
        }

        @Test
        @DisplayName("更新 Profile 時，應回傳新的 Profile 實例")
        void should_return_new_profile_on_update() {
            ProfileId id = new ProfileId(UUID.randomUUID().toString());
            Email email = new Email("alice@example.com");
            Name name = new Name("Alice");
            Bio bio = new Bio("Developer");

            Profile profile = new Profile(id, email, name, bio);
            Profile updatedProfile = profile.update(new Name("Bob"), new Bio("New Bio"));

            assertThat(profile).isNotSameAs(updatedProfile);
            assertThat(profile.id()).isEqualTo(updatedProfile.id());
            assertThat(profile.email()).isEqualTo(updatedProfile.email());
        }
    }
}
