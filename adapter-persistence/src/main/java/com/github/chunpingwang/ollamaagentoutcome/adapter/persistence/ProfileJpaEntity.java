package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.springframework.data.domain.Persistable;

/**
 * 個人資料的 JPA Entity。
 */
@Entity
@Table(name = "profiles")
public class ProfileJpaEntity implements Persistable<String> {

    @Id
    private String id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column
    private String displayName;

    // 用於 Persistable 介面，指示是否為新實體
    private boolean isNew = true;

    protected ProfileJpaEntity() {
        // JPA 要求無參建構子
    }

    /**
     * 從領域模型轉換而來的新實體建構子。
     */
    public ProfileJpaEntity(String id, String email, String displayName) {
        this.id = id;
        this.email = email;
        this.displayName = displayName;
        this.isNew = true;
    }

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
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

    /**
     * JPA 載入或持久化後更新isNew狀態。
     */
    @jakarta.persistence.PostLoad
    @jakarta.persistence.PostPersist
    private void markAsNotNew() {
        this.isNew = false;
    }
}
