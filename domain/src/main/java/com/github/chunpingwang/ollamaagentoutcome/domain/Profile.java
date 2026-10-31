package com.github.chunpingwang.ollamaagentoutcome.domain;

import java.util.Objects;

/**
 * Profile entity representing a user's personal information.
 * Immutable fields are updated via the update method which validates inputs.
 */
public final class Profile {
    
    private final ProfileId id;
    private String name;
    private Email email;
    private Age age;

    public Profile(ProfileId id, String name, Email email, Age age) {
        if (id == null || name == null || name.isBlank()) {
            throw new IllegalArgumentException("ID and Name cannot be null or empty");
        }
        this.id = id;
        this.name = name;
        this.email = Objects.requireNonNull(email);
        this.age = Objects.requireNonNull(age);
    }

    public ProfileId id() {
        return id;
    }

    public String name() {
        return name;
    }

    public void setName(String newName) {
        if (newName == null || newName.isBlank()) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        this.name = newName;
    }

    public Email email() {
        return email;
    }

    public void setEmail(Email newEmail) {
        this.email = Objects.requireNonNull(newEmail);
    }

    public Age age() {
        return age;
    }

    public void setAge(Age newAge) {
        this.age = Objects.requireNonNull(newAge);
    }

    /**
     * Updates the profile with new values. Validates inputs before applying changes.
     */
    public void update(String newName, String newEmailStr, int newAge) {
        // Validate and create VOs first to ensure business rules are met
        new Email(newEmailStr); // Throws InvalidEmailFormatException if invalid
        new Age(newAge);        // Throws InvalidAgeException if negative
        
        this.setName(newName);
        this.setEmail(new Email(newEmailStr));
        this.setAge(new Age(newAge));
    }
}
