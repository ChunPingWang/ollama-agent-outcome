package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PostLoad;
import jakarta.persistence.PostPersist;
import jakarta.persistence.PostUpdate;
import jakarta.persistence.Table;

import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;

/**
 * 個人資料的 JPA Entity。
 * 實作 Persistable 以支援 Domain 層自行生成 UUID 作為主鍵。
 */
@Entity
@Table(name = "profiles")
class ProfileEntity implements Persistable<UUID> {

    @Id
    private UUID id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String name;

    /**
     * 標記此實體是否為新建立（尚未持久化）。
     * 預設為 true，在持久化後設為 false。
     */
    @Transient
    private boolean isNew = true;

    public ProfileEntity() {
        // 供 JPA 使用
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
    @PostUpdate
    @PostLoad
    void markNotNew() {
        this.isNew = false;
    }
}
