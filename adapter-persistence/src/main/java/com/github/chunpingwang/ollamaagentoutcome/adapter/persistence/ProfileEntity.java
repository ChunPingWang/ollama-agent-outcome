package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;

import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PostLoad;
import jakarta.persistence.PostPersist;
import jakarta.persistence.Table;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;

import java.util.Objects;
import java.util.UUID;

/**
 * 個人資料的 JPA Entity。
 * 實作 Persistable 以處理 Domain 層自行生成 ID 的情況。
 */
@Entity
@Table(name = "profiles")
public class ProfileEntity implements Persistable<UUID> {

    @Id
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(unique = true, nullable = false)
    private String email;

    /**
     * 標記實體是否為新建立。
     * 對於自行生成 ID 的 Entity，新建立的實體 isNew 為 true，
     * 從資料庫載入或持久化後設為 false。
     */
    @Transient
    private boolean isNew = true;

    public ProfileEntity() {
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

    public String getName() {return name; }

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

    public void setNew(boolean newEntity) {
        this.isNew = newEntity;
    }

    @PostPersist
    @PostLoad
    void markAsNotNew() {
        this.isNew = false;
    }

    /**
     * 將 Domain Profile 轉換為 Entity。
     */
    public static ProfileEntity fromDomain(Profile profile) {
        ProfileEntity entity = new ProfileEntity();
        entity.setId(profile.id().id());
        entity.setName(profile.name());
        entity.setEmail(profile.email().value());
        entity.setNew(true);
        return entity;
    }

    /**
     * 將 Entity 轉換為 Domain Profile。
     */
    public Profile toDomain() {
        return new Profile(
                new ProfileId(this.id),
                this.name,
                new Email(this.email)
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProfileEntity that = (ProfileEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
