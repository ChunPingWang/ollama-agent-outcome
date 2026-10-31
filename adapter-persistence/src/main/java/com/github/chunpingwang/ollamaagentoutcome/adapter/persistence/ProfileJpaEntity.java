package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.springframework.data.domain.Persistable;

/**
 * JPA Entity：對應個人資料資料表。
 */
@Entity
@Table(name = "profiles")
public class ProfileJpaEntity implements Persistable<java.util.UUID> {

    @Id
    private java.util.UUID id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column
    private String displayName;

    /**
     * 標記實體是否為新建立（尚未持久化）。
     */
    @jakarta.persistence.Transient
    private boolean isNew = true;

    protected ProfileJpaEntity() {
        // For JPA mapping only
    }

    public ProfileJpaEntity(java.util.UUID id, String email, String displayName) {
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

    @jakarta.persistence.PostLoad
    @jakarta.persistence.PostPersist
    void markAsLoadedOrPersisted() {
        this.isNew = false;
    }
}
