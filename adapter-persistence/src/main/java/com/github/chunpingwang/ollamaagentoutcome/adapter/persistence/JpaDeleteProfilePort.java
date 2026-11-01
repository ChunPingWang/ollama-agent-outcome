package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;

import com.github.chunpingwang.ollamaagentoutcome.application.port.out.DeleteProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Component;

/**
 * JPA 實作的 Delete Port，負責刪除指定 ID 的個人資料。
 */
@Component
public class JpaDeleteProfilePort implements DeleteProfilePort {

    private final ProfileJpaRepository repository;

    public JpaDeleteProfilePort(ProfileJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public void deleteById(ProfileId id) {
        var entity = repository.findById(id.id().toString())
                .orElseThrow(() -> new EntityNotFoundException("Profile not found with id: " + id));
        
        // 為了確保在測試交易內能正確檢查唯一性約束（若有的話）或觸發相關事件，使用 flush
        // 但這裡主要是刪除，直接呼叫 deleteAndFlush 或直接 delete 即可，為了一致性與測試環境下的即時可見性：
        try {
            repository.delete(entity);
            repository.flush();
        } catch (Exception e) {
             throw new RuntimeException(e);
        }
    }
}
