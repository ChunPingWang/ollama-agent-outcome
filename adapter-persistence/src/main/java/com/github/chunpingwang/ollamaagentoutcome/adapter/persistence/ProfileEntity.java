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
 * 個人資料 JPA 實體。
 */
@Entity
@Table(name = "profile")
public class ProfileEntity implements Persistable<UUID> {

    @Id
    private UUID id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column
    private String displayName;

    @Transient
    private boolean isNew = true;

    protected ProfileEntity() {}

    public ProfileEntity(UUID id) {
        this.id = id;
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

    public void setEmail(String email) {this.email = email;}

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

    void markNotNew() {
        this.isNew = false;
    }

    // 用於 JPA 載入後自動標記為非新資料
    @PostLoad
    void onPostLoad() {
        this.isNew = false;
    }

    @PostPersist
    void onPostPersist() {
        this.isNew = false;
    }
}
