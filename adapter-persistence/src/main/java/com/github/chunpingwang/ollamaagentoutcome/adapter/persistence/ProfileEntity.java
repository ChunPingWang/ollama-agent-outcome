package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;

import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PostLoad;
import jakarta.persistence.PostPersist;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.persistence.UniqueConstraint;
import org.springframework.data.domain.Persistable;

/**
 * JPA Entity for storing profile data in the database.
 */
@Entity
@Table(name = "profiles", uniqueConstraints = @UniqueConstraint(columnNames = "email"))
class ProfileEntity implements Persistable<String> {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(nullable = false, length = 36)
    private String id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true, length = 254)
    private String email;

    @Column(nullable = false)
    private Integer age;

    /**
     * Tracks whether this entity is newly created and not yet persisted to DB.
     */
    @Transient
    private boolean isNew = true;

    protected ProfileEntity() {
        // Required by JPA
    }

    public ProfileEntity(String id, String name, String email, int age) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("ID cannot be empty");
        }
        this.id = id;
        this.name = Objects.requireNonNull(name);
        this.email = Objects.requireNonNull(email).toLowerCase();
        if (age < 0) {
            throw new IllegalArgumentException("Age must not be negative");
        }
        this.age = age;
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
        this.email = email.toLowerCase();
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    @Override
    public boolean isNew() {
        return isNew;
    }

    /**
     * Marks the entity as persisted. Called by JPA after save/load operations.
     */
    private void markAsPersisted() {
        this.isNew = false;
    }

    @PostLoad
    @PostPersist
    private void onPostLoadOrPersist() {
        if (this.id != null && !this.id.isBlank()) {
            this.markAsPersisted();
        }
    }
}
