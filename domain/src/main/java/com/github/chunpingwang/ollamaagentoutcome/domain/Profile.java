package com.github.chunpingwang.ollamaagentoutcome.domain;

import java.util.Objects;

/**
 * 個人資料領域實體，採用不可變性設計。
 */
public class Profile {
    private final ProfileId id;
    private final Name name;
    private final Email email;

    /**
     * Factory method to create a new profile instance.
     */
    public static Profile create(ProfileId id, Name name, Email email) {
        return new Profile(id, name, email);
    }

    private Profile(ProfileId id, Name name, Email email) {
        Objects.requireNonNull(id, "id must not be null");
        Objects.requireNonNull(name, "name must not be null");
        Objects.requireNonNull(email, "email must not be null");
        
        this.id = id;
        this.name = name;
        this.email = email;
    }

    public ProfileId id() {
        return id;
    }

    public Name name() {
        return name;
    }

    public Email email() {
        return email;
    }

    /**
     * Returns a new Profile instance with updated fields, keeping the same ID.
     */
    public Profile update(Name newName, Email newEmail) {
        return new Profile(this.id, newName, newEmail);
    }
}
