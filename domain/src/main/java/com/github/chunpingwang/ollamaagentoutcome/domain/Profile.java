package com.github.chunpingwang.ollamaagentoutcome.domain;

import java.util.Objects;
import java.util.Set;

/**
 * Profile aggregate root with business logic for updates and validation.
 */
public final class Profile {
    private final ProfileId id;
    private Email email;
    private DisplayName displayName;

    public Profile(ProfileId id, Email email, DisplayName displayName) {
        this.id = Objects.requireNonNull(id);
        this.email = Objects.requireNonNull(email);
        this.displayName = Objects.requireNonNull(displayName);
    }

    public ProfileId getId() {
        return id;
    }

    public Email getEmail() {
        return email;
    }

    public DisplayName getDisplayName() {
        return displayName;
    }

    /**
     * Updates the profile information after validating constraints.
     * Throws DuplicateEmailException if the new email is already taken by another profile.
     */
    public void update(Email newEmail, DisplayName newDisplayName, Set<Email> existingEmails) {
        // If there are other emails in DB (excluding self), check against them.
        // Note: The caller should pass all EXISTING emails from DB except this one's current email.
        // However, to keep it simple and robust based on common patterns:
        // We assume 'existingEmails' contains ALL emails currently stored in system OR 
        // specifically those that conflict with uniqueness constraint excluding self.
        
        // Standard DDD approach for value object uniqueness within aggregate context or external check:
        // Since we don't have ID here easily accessible for exclusion logic inside Entity without passing more args,
        // let's stick to a simpler contract: 
        // If newEmail equals current email -> allowed.
        // Else if newEmail exists in provided set of OTHERS -> throw duplicate.
        
        if (!this.email.equals(newEmail)) {
            if (existingEmails != null && existingEmails.contains(newEmail)) {
                throw new DuplicateEmailException("Email " + newEmail.getAddress() + " is already in use");
            }
        }

        this.email = newEmail;
        this.displayName = Objects.requireNonNull(newDisplayName);
    }
}
