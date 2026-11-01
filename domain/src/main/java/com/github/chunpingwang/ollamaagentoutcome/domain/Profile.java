package com.github.chunpingwang.ollamaagentoutcome.domain;

import java.util.Objects;

/**
 * Profile domain entity.
 */
public class Profile {

    private final ProfileId id;
    private Email email;
    private DisplayName displayName;

    /**
     * Private constructor for creating new instances with validation.
     */
    private Profile(ProfileId id, Email email, DisplayName displayName) {
        if (id == null) {
            throw new IllegalArgumentException("id must not be null");
        }
        if (email == null) {
            throw new IllegalArgumentException("email must not be null");
        }
        
        this.id = Objects.requireNonNull(id); // Redundant check but safe practice in Java
        this.email = Objects.requireNonNull(email);
        this.displayName = displayName;
    }

    /**
     * Static factory method to create a new profile.
     */
    public static Profile create(ProfileId id, Email email, DisplayName displayName) {
        return new Profile(id, email, displayName);
    }

    /**
     * Update the profile's email and display name.
     * Returns a new instance or updated state depending on immutability strategy. 
     * Here we update fields but for strict immutability in DDD, returning a new object is preferred if possible,
     * however standard JPA entities often mutate. Since this is Domain Entity (not JPA yet), let's make it immutable-like by returning new instance?
     * The test `should_fail_to_update_with_duplicate_email_logic_in_domain` expects:
     * 1. `updatedProfile` is NOT same as `profile`.
     * 2. `updatedProfile` has new values.
     * This implies an immutable pattern or copy-on-write. Let's implement immutable update.
     */
    public Profile update(Email newEmail, DisplayName newDisplayName) {
        // In a strictly immutable domain model, we would return a new instance.
        // However, typically Entities are mutable aggregates. But given the test assertion `isNotSameAs`,
        // we must return a NEW instance to satisfy the test requirement of "update" behavior simulation.
        
        Email effectiveEmail = Objects.requireNonNull(newEmail, "new email must not be null");
        DisplayName effectiveName = newDisplayName; // displayName can be null

        return Profile.create(this.id, effectiveEmail, effectiveName);
    }

    public ProfileId id() {
        return id;
    }

    public Email email() {
        return email;
    }

    public void setEmail(Email email) {
        this.email = Objects.requireNonNull(email, "email must not be null");
    }

    public DisplayName displayName() {
        return displayName;
    }

    public void setDisplayName(DisplayName displayName) {
        this.displayName = displayName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Profile profile)) return false;
        return Objects.equals(id, profile.id) && 
               Objects.equals(email, profile.email) && 
               Objects.equals(displayName, profile.displayName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, email, displayName);
    }
}
