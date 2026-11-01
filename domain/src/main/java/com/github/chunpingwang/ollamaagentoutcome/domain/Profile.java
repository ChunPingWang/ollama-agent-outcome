package com.github.chunpingwang.ollamaagentoutcome.domain;

import java.util.Objects;

/**
 * Profile domain entity representing a user's personal information.
 */
public final class Profile {
    private final ProfileId id;
    private Name name;
    private Email email;

    public Profile(ProfileId id, Name name, Email email) {
        Objects.requireNonNull(id, "id must not be null");
        this.id = id;
        updateName(name);
        updateEmail(email);
    }

    /**
     * Updates the profile with new name and email after validation.
     * Returns a new instance to maintain immutability of identity.
     */
    public Profile update(Name newName, Email newEmail) {
        if (newName == null && newEmail == null) {
            return this;
        }
        
        Name updatedName = newName != null ? newName : this.name;
        Email updatedEmail = newEmail != null ? newEmail : this.email;
        
        // Re-validate via VO constructors implicitly by passing them in
        // However, since we already validated in constructor or previous steps, 
        // we just create a new immutable snapshot here as per DDD best practices for value objects/immutable entities logic
        // But strictly speaking, an Entity is mutable. To satisfy test `updated.id()`, returning new instance is safer for testing equality checks on ID.
        // Let's stick to the pattern: Update returns new instance OR updates self. 
        // The test expects: `Profile updated = profile.update(...)` then `updated.id()` works.
        // If we mutate self, `profile` reference would also change. Test uses `updated`.
        // So we return a new Profile instance with same ID but new values.
        
        return new Profile(this.id, updatedName, updatedEmail);
    }

    private void updateName(Name newName) {
        Objects.requireNonNull(newName, "name must not be null");
        this.name = newName;
    }

    private void updateEmail(Email newEmail) {
        Objects.requireNonNull(newEmail, "email must not be null");
        this.email = newEmail;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Profile other)) return false;
        return Objects.equals(id, other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
