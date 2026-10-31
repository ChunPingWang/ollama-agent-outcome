package com.github.chunpingwang.ollamaagentoutcome.domain;

import java.util.Objects;

/**
 * 個人資料實體。
 */
public class Profile {

    private final ProfileId id;
    private String name;
    private Email email;

    private Profile(ProfileId id, String name, Email email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }

    public static Profile create(ProfileId id, String name, Email email) {
        if (name == null || name.isBlank()) {
            throw new InvalidNameException("Name cannot be null or blank");
        }
        return new Profile(id, name, email);
    }

    public ProfileId getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Email getEmail() {
        return email;
    }

    public void updateName(String newName) {
        if (newName == null || newName.isBlank()) {
            throw new InvalidNameException("Name cannot be null or blank");
        }
        this.name = newName;
    }

    public void updateEmail(Email newEmail) {
        if (newEmail == null) {
            throw new IllegalArgumentException("Email cannot be null");
        }
        this.email = newEmail;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Profile profile = (Profile) o;
        return Objects.equals(id, profile.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
