package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PostLoad;
import jakarta.persistence.PostPersist;
import org.springframework.data.domain.Persistable;

/**
 * 個人資料的 JPA Entity。
 * 實作 Persistable 以支援 Domain 層自行產生 ID 的情境。
 */
@Entity
public class ProfileEntity implements Persistable<String> {

    @Id
    private String id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Integer age;

    @jakarta.persistence.Transient
    private boolean isNew = true;

    public ProfileEntity() {
    }

    public ProfileEntity(String id, String email, String name, Integer age) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.age = age;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public void setNew(boolean isNew) {
        this.isNew = isNew;
    }

    @PostPersist
    @PostLoad
    void markAsNotNew() {
        this.isNew = false;
    }
}
