package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;

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
 * JPA Entity for Profile, implements Persistable to handle UUID primary key and isNew state.
 */
@Entity
@Table(name = "profiles", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"email"})
})
public class ProfileEntity implements Persistable<String> {

    @Id
    private String id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column
    private String displayName;

    @Transient
    private boolean isNew = true;

    public ProfileEntity() {
        // Default constructor required by JPA
    }

    /**
     * Create a new entity instance marked as new.
     */
    public static ProfileEntity createNew(String id, String email, String displayName) {
        ProfileEntity entity = new ProfileEntity();
        entity.id = id;
        entity.email = email;
        entity.displayName = displayName;
        entity.isNew = true;
        return entity;
    }

    @PostLoad
    @PostPersist
    void onLoadOrPersist() {
        this.isNew = false;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public boolean isNew() {
        return isNew;
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
}
