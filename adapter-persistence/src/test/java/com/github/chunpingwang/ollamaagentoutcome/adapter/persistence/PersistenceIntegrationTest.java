package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;


import org.springframework.test.annotation.DirtiesContext;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.github.chunpingwang.ollamaagentoutcome.application.port.out.SaveProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.DisplayName;
import com.github.chunpingwang.ollamaagentoutcome.domain.DuplicateEmailException;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;

// 測試使用 TestEntityManager（需要交易）：保留測試交易，以 flush()/clear() 控制一級快取；每個測試後重建 context
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({ JpaSaveProfilePort.class, JpaLoadProfilePort.class, JpaDeleteProfilePort.class })
class PersistenceIntegrationTest {

    @Autowired private SaveProfilePort saveProfilePort;
    
    // Note: The prompt skeleton uses specific port autowiring. 
    // We will test the Ports directly as they wrap the Repository/EntityManager logic.
    
    @Autowired private JpaSaveProfilePort jpaSaveProfilePort;
    @Autowired private JpaLoadProfilePort jpaLoadProfilePort;
    @Autowired private JpaDeleteProfilePort jpaDeleteProfilePort;
    @Autowired private org.springframework.boot.jpa.test.autoconfigure.TestEntityManager entityManager;

    @Test
    void should_save_and_load_profile_successfully() {
        UUID id = UUID.randomUUID();
        Email email = new Email("test@example.com");
        DisplayName displayName = new DisplayName("Test User");
        
        Profile profile = new Profile(new ProfileId(id), email, displayName);
        
        Profile saved = jpaSaveProfilePort.save(profile);
        entityManager.flush();
        entityManager.clear();

        assertThat(saved.getId()).isEqualTo(new ProfileId(id));
        assertThat(saved.getEmail().getValue()).isEqualTo(email.getValue());
        assertThat(saved.getDisplayName().getValue()).isEqualTo(displayName.getValue());

        var loadedOpt = jpaLoadProfilePort.findById(new ProfileId(id));
        assertThat(loadedOpt).isPresent();
        assertThat(loadedOpt.get().getId()).isEqualTo(new ProfileId(id));
    }

    @Test
    void should_reject_duplicate_email_on_save() {
        UUID id1 = UUID.randomUUID();
        Email email = new Email("unique@test.com");
        DisplayName displayName = new DisplayName("User 1");
        
        Profile p1 = new Profile(new ProfileId(id1), email, displayName);
        jpaSaveProfilePort.save(p1);
        entityManager.flush(); // First save succeeds
        
        UUID id2 = UUID.randomUUID();
        Profile p2 = new Profile(new ProfileId(id2), email, displayName);
        
        // DuplicateEmailException is the domain exception thrown by JpaSaveProfilePort
        assertThatThrownBy(() -> jpaSaveProfilePort.save(p2))
                .isInstanceOf(DuplicateEmailException.class)
                .hasMessageContaining("Duplicate email");
                
        // Transaction aborted after constraint violation. 
        // We cannot verify "first still exists" in this same method due to transaction state.
    }

    @Test
    void should_delete_profile_by_id() {
        UUID id = UUID.randomUUID();
        Email email = new Email("delete@example.com");
        DisplayName displayName = new DisplayName("Delete Me");
        
        Profile saved = jpaSaveProfilePort.save(new Profile(new ProfileId(id), email, displayName));
        entityManager.flush();
        entityManager.clear();            // Clear first-level cache so delete takes effect for subsequent finds

        jpaDeleteProfilePort.deleteById(saved.getId());
        entityManager.flush();
        entityManager.clear();

        assertThat(jpaLoadProfilePort.findById(saved.getId())).isEmpty();
    }
    
    @Test
    void should_handle_null_display_name() {
        UUID id = UUID.randomUUID();
        Email email = new Email("nullname@test.com");
        // 建立包含 null value 的 Value Object，符合領域層設計慣例
        DisplayName nullNameVO = new DisplayName(null);
        
        Profile profile = new Profile(new ProfileId(id), email, nullNameVO);
        
        Profile saved = jpaSaveProfilePort.save(profile);
        entityManager.flush();
        entityManager.clear();

        var loadedOpt = jpaLoadProfilePort.findById(new ProfileId(id));
        assertThat(loadedOpt).isPresent();
        // 修正斷言：Value Object 本身不應為 null，應檢查其內部值是否為 null
        assertThat(loadedOpt.get().getDisplayName()).isNotNull()
                .satisfies(name -> assertThat(name.getValue()).isNull());
    }
}
