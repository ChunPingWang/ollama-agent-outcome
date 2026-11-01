package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;

import java.io.Serializable;

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
 * JPA Entity：個人資料持久化模型。
 */
@Entity
@Table(name = "profiles", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"email"})
})
public class ProfileEntity implements Persistable<String>, Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column
    private String displayName;

    /**
     * 標記實體是否為新增狀態，用於決定 INSERT 或 UPDATE。
     */
    @Transient
    private boolean isNew = true;

    public ProfileEntity() {}

    public ProfileEntity(String id, String email, String displayName) {
        this.id = id;
        this.email = email.toLowerCase();
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
        this.email = email != null ? email.toLowerCase() : null;
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

    /**
     * 設定新增狀態（供 Port 層在更新時呼叫）。
     */
    void setNew(boolean newFlag) {
        this.isNew = newFlag;
    }
}
