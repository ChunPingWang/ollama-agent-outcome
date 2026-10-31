package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PostLoad;
import jakarta.persistence.PostPersist;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import org.springframework.data.domain.Persistable;

/**
 * JPA Entity，對應個人資料資料表。
 * 實作 Persistable<UUID> 以支援 Domain 層自行生成的 UUID 識別碼。
 */
@Entity
@Table(name = "profiles")
class ProfileEntity implements Persistable<UUID> {

    @Id
    private UUID id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String displayName;

    /**
     * 標記實體是否為新增狀態。JPA 需要此資訊來決定是 INSERT 還是 UPDATE。
     */
    @Transient
    private boolean isNew = true;

    public ProfileEntity() {
        this.isNew = true;
    }

    public ProfileEntity(UUID id, String email, String displayName) {
        this.id = id;
        this.email = email;
        this.displayName = displayName;
        this.isNew = true;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    @PostPersist
    @PostLoad
    void markNotNew() {
        this.isNew = false;
    }

    @Override
    public boolean isNew() {
        return isNew;
    }
}
