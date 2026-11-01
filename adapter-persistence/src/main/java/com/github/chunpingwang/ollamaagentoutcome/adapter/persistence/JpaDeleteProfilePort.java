package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;

import com.github.chunpingwang.ollamaagentoutcome.application.port.out.DeleteProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;
import org.springframework.stereotype.Component;

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
        // Application Service 會先檢查存在性，這裡直接執行刪除即可。
        // 若需確保嚴格符合 "NotFound" 語義，可在此處查詢後刪除，但通常由 Caller 保證。
        repository.deleteById(id.value());
    }
}
