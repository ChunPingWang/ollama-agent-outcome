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
 * 個人資料的 JPA Entity，支援由 Domain 層產生的 UUID 識別碼。
 */
@Entity
@Table(name = "profiles")
class ProfileEntity implements Persistable<UUID> {

    @Id
    private UUID id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(length = 100)
    private String displayName;

    /**
     * 標記實體是否為新建立的物件。
     */
    @jakarta.persistence.Transient
    private boolean isNew = true;

    protected ProfileEntity() {
        // For JPA
    }

    public ProfileEntity(UUID id, String email, String displayName) {
        this.id = id;
        this.email = email;
        this.displayName = displayName;
        this.isNew = true;
    }

    @Override
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

    @Override
    public boolean isNew() {
        return isNew;
    }

    @PostPersist
    @PostLoad
    void markAsNotNew() {
        this.isNew = false;
    }
}
