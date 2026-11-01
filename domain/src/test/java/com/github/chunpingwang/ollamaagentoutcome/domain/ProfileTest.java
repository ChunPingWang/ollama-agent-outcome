package com.github.chunpingwang.ollamaagentoutcome.domain;



import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.UUID;

import org.junit.jupiter.api.Test;

class ProfileTest {

    private final UUID testId = UUID.randomUUID();
    private final ProfileId profileId = new ProfileId(testId);
    private final Email email = new Email("test@example.com");
    private final Name name = new Name("John Doe");

    @Test
    void should_create_profile_with_valid_data() {
        Profile profile = new Profile(profileId, name, email);
        
        assertThat(profile.id()).isEqualTo(profileId);
        assertThat(profile.name()).isEqualTo(name);
        assertThat(profile.email()).isEqualTo(email);
    }

    @Test
    void id_should_be_immutable_after_creation() {
        Profile profile = new Profile(profileId, name, email);
        
        Profile updated = profile.update(new Name("Jane Doe"), email);
        
        assertThat(updated.id()).isEqualTo(profileId);
        assertThat(updated.name().value()).isEqualTo("Jane Doe");
        assertThat(updated.email()).isEqualTo(email);
    }

    @Test
    void update_should_validate_new_name() {
        Profile profile = new Profile(profileId, name, email);
        
        assertThatThrownBy(() -> profile.update(new Name(""), email))
            .isInstanceOf(InvalidNameException.class);
            
        assertThatThrownBy(() -> profile.update(new Name("   "), email))
            .isInstanceOf(InvalidNameException.class);
    }

    @Test
    void update_should_validate_new_email_format() {
        Profile profile = new Profile(profileId, name, email);
        
        assertThatThrownBy(() -> profile.update(name, new Email("invalid")))
            .isInstanceOf(InvalidEmailException.class);
    }
    
    @Test
    void equals_and_hashCode_based_on_id() {
        // 使用相同的 ID，但不同的內容
        UUID sameId = UUID.randomUUID();
        Profile p1 = new Profile(new ProfileId(sameId), new Name("John Doe"), new Email("test@example.com"));
        Profile p2 = new Profile(new ProfileId(sameId), new Name("Other"), new Email("other@test.com"));
        
        // Record 的 equals/hashCode 預設基於所有欄位。若業務規則要求「僅以 ID 判斷相等」，需自訂 equals/hashCode。
        // 根據錯誤訊息與常見領域驅動設計實踐，Domain Entity 通常以 Identity (ID) 作為相等性依據。
        // 這裡我們假設需求是基於 ID 相等即視為同一個實體（Identity-based equality）。
        // 由於 record 自動生成的 equals 比較所有欄位，導致上述測試失敗。
        // 為了符合 "equals_and_hashCode_based_on_id" 的測試名稱與預期行為，Profile 必須覆寫 equals/hashCode。
        assertThat(p1).isEqualTo(p2);
        assertThat(p1.hashCode()).isEqualTo(p2.hashCode());
    }
}
