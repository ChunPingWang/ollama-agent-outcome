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

    /**
     * 是否為新實體。
     */
    @jakarta.persistence.Transient
    private boolean isNew = true;

    protected ProfileJpaEntity() {
        // JPA 要求無參建構子
    }

    /**
     * 建立新的實體，標記為新增狀態。
     *
     * @param id          識別碼
     * @param email       電子郵件
     * @param displayName 顯示名稱
     */
    public ProfileJpaEntity(String id, String email, String displayName) {
        this.id = id;
        this.email = email.toLowerCase();
        this.displayName = (displayName != null && !displayName.isBlank()) ? displayName : "";
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
        this.email = email.toLowerCase();
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = (displayName != null && !displayName.isBlank()) ? displayName : "";
    }

    @jakarta.persistence.PostLoad
    @jakarta.persistence.PostPersist
    private void markNotNew() {
        this.isNew = false;
    }

    @Override
    public boolean isNew() {
        return isNew;
    }
}
