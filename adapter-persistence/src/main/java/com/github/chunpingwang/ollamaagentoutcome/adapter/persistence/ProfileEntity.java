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
 * 個人資料 JPA Entity，實作 Persistable 以處理 isNew 狀態。
 */
@Entity
@Table(name = "profiles")
public class ProfileEntity implements Persistable<UUID> {

    @Id
    private UUID id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "display_name")
    private String displayName;

    /**
     * 標記是否為新實體（尚未持久化）。
     */
    @jakarta.persistence.Transient
    private boolean isNew = true;

    protected ProfileEntity() {
        // JPA 要求無參數建構子
    }

    /**
     * 建立新的實體。
     *
     * @param id         識別碼
     * @param email      電子郵件
     * @param displayName 顯示名稱
     */
    public static ProfileEntity create(UUID id, String email, String displayName) {
        ProfileEntity entity = new ProfileEntity();
        entity.id = id;
        entity.email = email;
        entity.displayName = displayName;
        entity.isNew = true;
        return entity;
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

    @PostLoad
    void onLoad() {
        this.isNew = false;
    }

    @PostPersist
    void onPersist() {
        this.isNew = false;
    }

    @Override
    public boolean isNew() {
        return isNew;
    }
}
