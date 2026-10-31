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
 * 實作 Persistable 以支援手動產生的 UUID 識別碼，避免與 @GeneratedValue 衝突。
 */
@Entity
@Table(name = "profiles")
public class ProfileEntity implements Persistable<UUID> {

    @Id
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column
    private String note;

    @Transient
    private boolean isNew = true;

    protected ProfileEntity() {
        // JPA 需要無參數建構子
    }

    /**
     * 建立新的 Entity 實例。
     *
     * @param id    識別碼
     * @param name  姓名
     * @param email 電子郵件
     * @param note  備註
     */
    public ProfileEntity(UUID id, String name, String email, String note) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.note = note;
        this.isNew = true;
    }

    @Override
    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getNote() {
        return note;
    }

    @Override
    public boolean isNew() {
        return isNew;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setNote(String note) {
        this.note = note;
    }

    /**
     * 標記為已持久化。
     */
    @PostLoad
    @PostPersist
    void markAsNotNew() {
        this.isNew = false;
    }
}
