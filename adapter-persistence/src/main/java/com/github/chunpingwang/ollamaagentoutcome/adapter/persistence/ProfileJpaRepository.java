package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ProfileJpaRepository extends JpaRepository<ProfileEntity, String> {

    Optional<ProfileEntity> findByEmail(String email);

    boolean existsByEmail(String email);
}
