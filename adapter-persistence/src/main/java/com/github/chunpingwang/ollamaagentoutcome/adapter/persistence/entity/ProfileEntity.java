package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PostLoad;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;
import org.springframework.data.domain.Persistable;

/**
 * 個人資料的 JPA Entity，實作 Persistable 以支援客戶端生成的 UUID。
 */
@Entity
@Table(name = "profiles")
public class ProfileEntity implements Persistable<UUID> {

    @Id
    private UUID id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Instant createdAt;

    /**
     * 標記是否為新記錄，用於判斷 INSERT 或 UPDATE。
     */
    private boolean isNew = true;

    // JPA 需要無參建構子進行反射實體化
    public ProfileEntity() {}

    public ProfileEntity(UUID id, String name, String email, Instant createdAt) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.createdAt = createdAt;
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

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    @PrePersist
    protected void onCreate() {
        this.isNew = true;
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
