package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PostLoad;
import jakarta.persistence.PostPersist;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import org.springframework.data.domain.Persistable;

/**
 * JPA Entity for Profile，實作 Persistable 以管理 UUID 識別碼與 isNew 狀態。
 */
@Entity
@Table(name = "profiles")
class ProfileEntity implements Persistable<UUID> {

    @Id
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column
    private String bio;

    // 用於 Persistable 支援，預設為新實體
    @jakarta.persistence.Transient
    private boolean isNew = true;

    public ProfileEntity() {}

    public ProfileEntity(UUID id, String name, String email, String bio) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.bio = bio;
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
        return name;    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    @PrePersist
    protected void onCreate() {
        this.isNew = true;
    }

    @PostPersist
    protected void onPersist() {
        this.isNew = false;
    }

    @PostLoad
    protected void onLoad() {
        this.isNew = false;
    }

    @Override
    public boolean isNew() {
        return isNew;
    }
}
