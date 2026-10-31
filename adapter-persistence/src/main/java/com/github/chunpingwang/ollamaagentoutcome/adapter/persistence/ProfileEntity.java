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
 * JPA Entity for Profile, implementing Persistable to handle domain-generated UUIDs.
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

    /**
     * Tracks whether the entity is new (transient).
     */
    private boolean isNew = true;

    protected ProfileEntity() {
        // Required by JPA
    }

    public ProfileEntity(UUID id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
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

    @Override
    public boolean isNew() {
        return isNew;
    }

    void setNew(boolean isNew) {
        this.isNew = isNew;
    }

    @PostPersist
    @PostLoad
    void markAsNotNew() {
        this.isNew = false;
    }
}
