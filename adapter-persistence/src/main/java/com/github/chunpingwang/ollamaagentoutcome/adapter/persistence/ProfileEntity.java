package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;

import java.io.Serializable;

import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PostLoad;
import jakarta.persistence.PostPersist;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import org.springframework.data.domain.Persistable;

/**
 * 個人資料的 JPA Entity。
 * 負責將 Domain 物件與資料庫記錄進行轉換。
 */
@Entity
@Table(name = "profiles")
class ProfileEntity implements Persistable<String>, Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String name;

    @Transient
    private boolean isNew = true;

    /**
     * 預設建構子，供 JPA 使用。
     */
    protected ProfileEntity() {
    }

    /**
     * 從 Domain 物件建立 Entity。
     *
     * @param profile 個人資料 Domain 物件
     */
    ProfileEntity(Profile profile) {
        this.id = profile.getId().getValue();
        this.email = profile.getEmail().getValue();
        this.name = profile.getName();
        this.isNew = true;
    }

    /**
     * 直接指定 ID 與資料建立 Entity（用於測試或特定情境）。
     *
     * @param id    識別碼
     * @param name  姓名
     * @param email 電子郵件
     */
    ProfileEntity(String id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.isNew = true;
    }

    @Override
    public String getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
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

    /**
     * 將 Entity 轉換回 Domain 物件。
     *
     * @return 個人資料 Domain 物件
     */
    Profile toDomain() {
        return new Profile(new ProfileId(id), new Email(email), name);
    }
}
