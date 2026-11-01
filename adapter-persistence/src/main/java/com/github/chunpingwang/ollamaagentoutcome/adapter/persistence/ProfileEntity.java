package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;


import jakarta.persistence.Transient;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PostLoad;
import jakarta.persistence.PostPersist;
import jakarta.persistence.Table;
import org.springframework.data.domain.Persistable;

/**
 * 個人資料的 JPA Entity。
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

    protected ProfileEntity() {}

    public ProfileEntity(String id, String email, String displayName) {
        this.id = id;
        this.email = email;
        this.displayName = displayName;
    }

    @PostLoad
    @PostPersist
    void onLoad() {
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
