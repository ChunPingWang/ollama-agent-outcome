package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence.entity;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PostLoad;
import jakarta.persistence.PostPersist;
import jakarta.persistence.Table;

import org.springframework.data.domain.Persistable;

/**
 * Profile JPA Entity。
 */
@Entity
@Table(name = "profiles")
public class ProfileEntity implements Persistable<UUID> {

    @Id
    private UUID id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column
    private String displayName;

    @Column(length = 500)
    private String bio;

    /**
     * 用於判斷是新增還是更新。
     */
    @jakarta.persistence.Transient
    private boolean isNew = true;

    protected ProfileEntity() {
        // JPA 需要無參數建構子
    }

    public ProfileEntity(UUID id, String email, String displayName, String bio) {
        this.id = id;
        this.email = email;
        this.displayName = displayName;
        this.bio = bio;
        this.isNew = true;
    }

    @PostPersist
    @PostLoad
    void markNotNew() {
        this.isNew = false;
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

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    @Override
    public boolean isNew() {
        return isNew;
    }
}
