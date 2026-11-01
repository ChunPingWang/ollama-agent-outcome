package com.github.chunpingwang.ollamaagentoutcome.domain;


import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.UUID;
import org.junit.jupiter.api.Test;

class ProfileIdTest {

    @Test
    void should_create_valid_profile_id() {
        UUID uuid = UUID.randomUUID();
        ProfileId id = new ProfileId(uuid.toString());
        assertThat(id.value()).isEqualTo(uuid.toString());
    }

    @Test
    void should_reject_null_value() {
        assertThatThrownBy(() -> new ProfileId(null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("must not be blank");
    }

    @Test
    void should_reject_blank_value() {
        assertThatThrownBy(() -> new ProfileId(""))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("must not be blank");
    }

    @Test
    void should_reject_invalid_uuid_format() {
        assertThatThrownBy(() -> new ProfileId("not-a-uuid"))
            .isInstanceOf(IllegalArgumentException.class); // 沿用現有例外或自訂，此處假設使用 InvalidProfileIdException 若存在，否則用 IllegalArgumentException。根據任務描述，需定義特定例外，但 T1 僅列出 Email/ProfileNotFound/Duplicate。為簡化，若無專屬例外，通常拋出 IllegalArgumentException。但在嚴格 TDD 下，應先寫測試。這裡假設我們需要一個通用的 ID 驗證失敗例外，或者直接使用 IllegalArgumentException。鑑於任務要求 "UUID 格式...驗證"，且未明確指定新例外類名，我們將依賴 `IllegalArgumentException` 或預期的 `InvalidEmailException` (如果命名混淆)。*修正*: 任務說定義 `InvalidEmailException`, `DuplicateEmailException`, `ProfileNotFoundException`。沒有提到 `InvalidProfileIdException`。因此 UUID 格式錯誤可能直接拋出 `IllegalArgumentException` 或在建構子中檢查。讓我們假設它應該拋出一個特定的異常以符合領域驅動設計精神，但如果必須從列表中選，則沒有合適的。通常 Value Object 驗證失敗會拋出 `IllegalArgumentException`。為了讓測試通過編譯（因為實作尚不存在），我們預期它拋出 `IllegalArgumentException`。
             assertThatThrownBy(() -> new ProfileId("invalid-uuid-string"))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void should_be_equal_for_same_uuid() {
        String uuidStr = UUID.randomUUID().toString();
        ProfileId id1 = new ProfileId(uuidStr);
        ProfileId id2 = new ProfileId(uuidStr);
        assertThat(id1).isEqualTo(id2);
    }
}
