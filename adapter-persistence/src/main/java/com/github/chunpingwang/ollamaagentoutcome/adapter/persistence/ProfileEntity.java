package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PostLoad;
import jakarta.persistence.PostPersist;
import jakarta.persistence.Table;

import org.springframework.data.domain.Persistable;

/**
 * 個人資料的 JPA Entity，實作 Persistable 以支援自行產生的 UUID ID。
 */
@Entity
@Table(name = "profiles")
class ProfileEntity implements Persistable<UUID> {

    @Id
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(unique = true, nullable = false)
    private String email;

    /**
     * 標記實體是否為新建立的記錄。
     */
    @jakarta.persistence.Transient
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

    void setNew(boolean newStatus) {
        this.isNew = newStatus;
    }

    @PostPersist
    @PostLoad
    void markAsNotNew() {
        this.isNew = false;
    }
}
