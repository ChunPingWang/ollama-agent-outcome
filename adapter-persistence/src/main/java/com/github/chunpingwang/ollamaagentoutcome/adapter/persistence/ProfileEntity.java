package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PostLoad;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import org.springframework.data.domain.Persistable;

/**
 * JPA Entity for Profile data storage.
 */
@Entity
@Table(name = "profiles", uniqueConstraints = {
    @jakarta.persistence.UniqueConstraint(columnNames = {"email"})
})
public class ProfileEntity implements Persistable<String>, Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "display_name")
    private String displayName;

    @Transient
    private boolean isNew = true;

    public ProfileEntity() {}

    /**
     * Create a new entity instance marked as new.
     */
    public ProfileEntity(String id, String email, String displayName) {
        this.id = id;
        this.email = email;
        this.displayName = displayName;
        this.isNew = true;
    }

    @PostLoad
    void onLoad() {
        this.isNew = false;
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

    protected void setIsNew(boolean isNew) {
        this.isNew = isNew;
    }
}
