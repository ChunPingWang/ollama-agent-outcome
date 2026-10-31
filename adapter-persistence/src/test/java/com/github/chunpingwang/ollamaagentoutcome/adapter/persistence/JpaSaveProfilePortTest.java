package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;

import com.github.chunpingwang.ollamaagentoutcome.domain.DuplicateEmailException;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.Name;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(JpaSaveProfilePort.class)
class JpaSaveProfilePortTest {

    @Autowired
    private JpaSaveProfilePort saveProfilePort;

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private org.springframework.boot.jpa.test.autoconfigure.TestEntityManager entityManager;

    @Test
    void should_save_profile_and_return_id() {
        // given
        Name name = new Name("Tester");
        Email email = new Email("test@example.com");

        // when
        ProfileId id = saveProfilePort.saveAndReturnId(name, email);

        // then
        assertThat(id).isNotNull();
        
        // flush and clear to verify persistence in a separate context
        entityManager.flush();
        entityManager.clear();

        var entity = profileRepository.findById(id.value());
        assertThat(entity).isPresent();
        assertThat(entity.get().getName()).isEqualTo("Tester");
        assertThat(entity.get().getEmail()).isEqualTo("test@example.com");
    }

    @Test
    void should_throw_duplicate_email_exception_on_violation() {
        // given
        Name name1 = new Name("User 1");
        Email email = new Email("unique@test.com");
        Name name2 = new Name("User 2");

        // first save succeeds
        saveProfilePort.saveAndReturnId(name1, email);
        
        // expect second save with same email fails immediately within the same transaction block
        assertThatThrownBy(() -> saveProfilePort.saveAndReturnId(name2, email))
            .isInstanceOf(DuplicateEmailException.class)
            .hasCauseInstanceOf(DataIntegrityViolationException.class);
            
        // Note: We do not query or count after this point because the transaction is aborted by PostgreSQL.
        // The test passes if the exception was thrown as expected.
    }
}
