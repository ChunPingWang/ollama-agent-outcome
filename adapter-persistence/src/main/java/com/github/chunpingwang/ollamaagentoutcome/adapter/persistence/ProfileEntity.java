package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PostLoad;
import jakarta.persistence.PostPersist;
import jakarta.persistence.Table;
import org.springframework.data.domain.Persistable;

import java.util.UUID;

/**
 * 個人資料的 JPA Entity。
 * 實作 Persistable 以支援 Domain 層自行生成 UUID 識別碼。
 */
@Entity
@Table(name = "profiles")
class ProfileEntity implements Persistable<UUID> {

    @Id
    private UUID id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, unique = true, length = 255)
    private String email;

    /**
     * 標記此 Entity 是否為新建立（尚未持久化）。
     * 預設為 true，在 @PostPersist 或 @PostLoad 後設為 false。
     */
    @jakarta.persistence.Transient
    private boolean isNew = true;

    protected ProfileEntity() {
        // JPA 要求無參數建構子
    }

    public ProfileEntity(UUID id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.isNew = true;
    }

    @Override
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public boolean isNew() {
        return isNew;
    }

    public void setNew(boolean isNew) {
        this.isNew = isNew;
    }

    @PostPersist
    @PostLoad
    void markNotNew() {
        this.isNew = false;
    }
}
