package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PostLoad;
import jakarta.persistence.PostPersist;
import jakarta.persistence.Table;
import org.springframework.data.domain.Persistable;

/**
 * 個人資料的 JPA Entity，實現 Persistable 以支援 Domain 產生的 UUID。
 */
@Entity
@Table(name = "profiles")
public class ProfileEntity implements Persistable<java.util.UUID> {

    @Id
    private java.util.UUID id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column
    private String displayName;

    /**
     * 標記實體是否為新建立的物件。
     */
    @jakarta.persistence.Transient
    private boolean isNew = true;

    protected ProfileEntity() {
        // JPA 需要無參數建構子
    }

    public ProfileEntity(java.util.UUID id, String email, String displayName) {
        this.id = id;
        this.email = email;
        this.displayName = displayName;
        this.isNew = true;
    }

    @Override
    public java.util.UUID getId() {
        return id;
    }

    public void setId(java.util.UUID id) {
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
    void markNotNew() {
        this.isNew = false;
    }
}
