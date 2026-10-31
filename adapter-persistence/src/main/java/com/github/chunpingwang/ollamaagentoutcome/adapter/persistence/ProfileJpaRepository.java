package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

/**
 * Spring Data Repository：負責 Profile 的持久化操作。
 */
@Repository
public interface ProfileJpaRepository extends CrudRepository<ProfileJpaEntity, UUID> {
}
