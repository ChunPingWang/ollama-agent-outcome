package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;




import static org.assertj.core.api.Assertions.*;
import org.springframework.test.annotation.DirtiesContext;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.github.chunpingwang.ollamaagentoutcome.domain.DisplayName;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;
import jakarta.persistence.EntityManager;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;

// 測試使用 TestEntityManager（需要交易）：保留測試交易，以 flush()/clear() 控制一級快取；每個測試後重建 context
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ProfileEntityTest {

    @Autowired private EntityManager entityManager;

    @Test
    void should_persist_and_find_profile() {
        UUID id = UUID.randomUUID();
        String emailStr = "test@example.com";
        String displayNameStr = "測試用戶";

        Profile profile = new Profile(new ProfileId(id), new Email(emailStr), new DisplayName(displayNameStr));
        
        ProfileEntity entity = ProfileConverter.toEntity(profile);

        entityManager.persist(entity);
        entityManager.flush();
        entityManager.clear();

        ProfileEntity found = entityManager.find(ProfileEntity.class, id);
        assertThat(found).isNotNull();
        assertThat(found.getEmail()).isEqualTo(emailStr);
        assertThat(found.getDisplayName()).isEqualTo(displayNameStr);
    }

    @Test
    void should_reject_duplicate_email_on_unique_constraint() {
        String sharedEmail = "dup@example.com";
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();

        Profile p1 = new Profile(new ProfileId(id1), new Email(sharedEmail), new DisplayName("User 1"));
        Profile p2 = new Profile(new ProfileId(id2), new Email(sharedEmail), new DisplayName("User 2"));

        entityManager.persist(ProfileConverter.toEntity(p1));
        entityManager.flush();
        entityManager.clear();

        // when & then（必須為最後一行，避免交易中止後繼續執行）
        assertThatThrownBy(() -> {
            entityManager.persist(ProfileConverter.toEntity(p2));
            entityManager.flush();
        }).isInstanceOf(Exception.class);
    }
}
