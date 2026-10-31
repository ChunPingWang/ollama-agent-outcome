package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;

import java.util.UUID;

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
 * 實作 Persistable 以支援 Domain 層自行生成 UUID 的情況。
 */
@Entity
@Table(name = "profiles")
class ProfileEntity implements Persistable<UUID> {

    @Id
    private UUID id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Integer age;

    @Transient
    private boolean isNew = true;

    protected ProfileEntity() {
    }

    /**
     * 建立新的 Entity 實例。
     */
    ProfileEntity(UUID id, String email, String name, Integer age) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.age = age;
        this.isNew = true;
    }

    @Override
    public UUID getId() {
        return id;
    }

    void setId(UUID id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    void setAge(Integer age) {
        this.age = age;
    }

    @Override
    public boolean isNew() {
        return isNew;
    }

    void setNew(boolean isNew) {
        this.isNew = isNew;
    }

    /**
     * JPA 載入後，標記為非新狀態。
     */
    @PostLoad
    void onLoad() {
        this.isNew = false;
    }

    /**
     * JPA 持久化後，標記為非新狀態。
     */
    @PostPersist
    void onPersist() {
        this.isNew = false;
    }
}
