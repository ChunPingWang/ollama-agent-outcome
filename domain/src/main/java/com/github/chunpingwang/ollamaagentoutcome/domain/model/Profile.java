package com.github.chunpingwang.ollamaagentoutcome.domain.model;

import java.time.Instant;

/**
 * 個人資料領域實體。
 */
public class Profile {
    private final ProfileId id;
    private Name name;
    private Email email;
    private final Instant createdAt;

    public Profile(ProfileId id, Name name, Email email, Instant createdAt) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.createdAt = createdAt;
    }

    public ProfileId getId() {
        return id;
    }

    public Name getName() {
        return name;
    }

    public void updateName(Name newName) {
        this.name = newName;
    }

    public Email getEmail() {
        return email;
    }

    public void updateEmail(Email newEmail) {
        this.email = newEmail;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
