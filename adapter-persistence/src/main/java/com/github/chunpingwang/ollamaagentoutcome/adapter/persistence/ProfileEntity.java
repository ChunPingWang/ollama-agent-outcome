package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.springframework.data.domain.Persistable;

/**
 * 個人資料 JPA Entity，實現 Persistable 以支援 Domain 層生成的 UUID。
 */
@Entity
@Table(name = "profile")
class ProfileEntity implements Persistable<String> {

    @Id
    private String id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    /**
     * 標記是否為新實體（尚未持久化）。
     */
    @jakarta.persistence.Transient
    private boolean isNew = true;

    public ProfileEntity() {}

    public ProfileEntity(String id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.isNew = true;
    }

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
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

    @Override
    public boolean isNew() {
        return isNew;
    }

    // JPA Callbacks to manage isNew state after persistence operations
    @jakarta.persistence.PostLoad
    @jakarta.persistence.PostPersist
    @jakarta.persistence.PostUpdate
    protected void markNotNew() {
        this.isNew = false;
    }
}
