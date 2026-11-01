package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;



import org.springframework.stereotype.Component;import com.github.chunpingwang.ollamaagentoutcome.application.port.out.DeleteProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;

/**
 * JPA 實作的 DeleteProfile Port，負責刪除資料。
 */
@Component
public class JpaDeleteProfilePort implements DeleteProfilePort {

    private final ProfileRepository repository;

    public JpaDeleteProfilePort(ProfileRepository repository) {
        this.repository = repository;
    }

    @Override
    public void deleteById(com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId id) {
        repository.deleteById(id.value());
    }
}
