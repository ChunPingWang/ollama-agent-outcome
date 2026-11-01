package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;



import org.springframework.stereotype.Component;import com.github.chunpingwang.ollamaagentoutcome.application.port.out.RemoveProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;

/**
 * JPA 實作的移除個人資料 Port。
 */
@Component
public class JpaRemoveProfilePortImpl implements RemoveProfilePort {

    private final ProfileJpaRepository repository;

    JpaRemoveProfilePortImpl(ProfileJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public void deleteById(ProfileId id) {
        repository.deleteById(id.toString());
    }
}
