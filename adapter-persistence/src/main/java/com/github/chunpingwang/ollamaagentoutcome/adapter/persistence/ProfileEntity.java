package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PostLoad;
import jakarta.persistence.PostPersist;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import org.springframework.data.domain.Persistable;

/**
 * JPA Entity for Profile, implementing Persistable to handle UUID primary keys correctly.
 */
@Entity
@Table(name = "profiles", uniqueConstraints = {
    @jakarta.persistence.UniqueConstraint(columnNames = {"email"})
})
public class ProfileEntity implements Persistable<String> {

    @Id
    private String id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column
    private String displayName;

    // 改為 package-private，讓同套件下的 Converter 與 Port 實作可直接設定 isNew 狀態
    boolean isNew = true;

    protected ProfileEntity() {}

    public ProfileEntity(String id, String email, String displayName) {
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

    @PostLoad
    @PostPersist
    public void onLoadOrPersist() {
        this.isNew = false;
    }

    @Override
    public boolean isNew() {
        return isNew;
    }
}
