package com.github.chunpingwang.ollamaagentoutcome.domain;



import static org.assertj.core.api.Assertions.*;import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class ProfileDomainServiceTest {

    private final String idStr = UUID.randomUUID().toString();
    private final ProfileId profileId = new ProfileId(idStr);
    private final Email email1 = new Email("user@example.com");
    private final Email email2 = new Email("other@example.com");
    private final DisplayName displayName = new DisplayName("User Name");

    @Test
    void should_create_profile_successfully() {
        Profile profile = new Profile(profileId, email1, displayName);
        assertThat(profile.getId()).isEqualTo(profileId);
        assertThat(profile.getEmail()).isEqualTo(email1);
        assertThat(profile.getDisplayName()).isEqualTo(displayName);
    }

    @Test
    void should_update_display_name_successfully() {
        Profile profile = new Profile(profileId, email1, displayName);
        DisplayName newName = new DisplayName("New Name");
        
        // Pass a set that doesn't contain the current email to simulate uniqueness check logic 
        // In real domain service we might pass existing emails. Here we test update method signature behavior.
        // Since update checks against 'existingEmails', if it's null or empty and we change email, it throws Duplicate.
        // Let's test updating with same email (allowed) vs different email (needs check).
        
        // Update display name only: keep same email
        Set<Email> allowedEmails = Set.of(email1);
        profile.update(email1, newName, allowedEmails);
        
        assertThat(profile.getDisplayName().getName()).isEqualTo("New Name");
        assertThat(profile.getEmail().getAddress()).isEqualTo("user@example.com");
    }

    @Test
    void should_throw_duplicate_email_exception_when_updating_to_existing_email() {
        Profile profile = new Profile(profileId, email1, displayName);
        Email otherEmail = new Email("other@example.com");
        
        // Simulate scenario where 'otherEmail' is already taken by another record in DB
        Set<Email> existingEmailsInDb = Set.of(otherEmail); 
        
        // If we try to update TO an email that IS NOT in the allowed list (i.e., duplicate), it fails?
        // Wait, logic in Profile.java: 
        // if (existingEmails != null && !existingEmails.contains(newEmail)) throw ...
        // This means if I want to set my email to "new@test.com", and "new@test.com" is NOT in existingEmailsInDb, it throws.
        // That's wrong for uniqueness check usually. Usually you pass ALL emails except self.
        // Let's re-read Plan 001 T2/T3 description carefully or infer from standard DDD.
        // Standard: Check if NewEmail exists in Repository excluding SelfID.
        // The method signature `update(..., Set<Email> existingEmails)` implies passing the set of VALID/ALLOWED emails OR checking against duplicates.
        // Looking at the code I wrote in Profile.java:
        // if (!existingEmails.contains(newEmail)) -> Throw Duplicate.
        // This implies existingEmails should be the SET OF EMAILS THAT ARE ALREADY IN USE BY OTHERS.
        // So if newEmail is in 'others', throw.
        
        // Test Case: Trying to update to an email that IS taken by someone else.
        Email takenEmail = new Email("taken@example.com");
        Set<Email> othersEmails = Set.of(takenEmail);
        
        assertThatThrownBy(() -> profile.update(takenEmail, displayName, othersEmails))
                .isInstanceOf(DuplicateEmailException.class)
                .hasMessageContaining("already in use");
    }

    @Test
    void should_allow_update_to_same_email() {
        Profile profile = new Profile(profileId, email1, displayName);
        DisplayName newName = new DisplayName("Updated Name");
        
        // Even if we pass a list of other people's emails, updating to our OWN email (which isn't in 'other' list usually? 
        // No, typically you check uniqueness among ALL records. If New == Old, it's fine regardless of others.
        // My implementation above throws if NOT contains. So I need to ensure my own email is considered valid or handled separately.
        // Let's refine logic slightly for TDD compliance with standard behavior:
        // Actually, let's stick to the code generated in main source. 
        // Code: `if (!existingEmails.contains(newEmail)) throw ...`
        // This means existingEmails MUST contain the NEW email for it to be allowed? That sounds like an allowlist.
        // Or maybe existingEmails are "emails that are currently stored".
        // If I change from A to B, and B is not in DB, then !contains(B) is true -> Throw Exception.
        // This implies the method expects `existingEmails` to be the set of EMAILS THAT ARE ALREADY IN USE BY OTHER USERS.
        // And if New Email IS in that set, it means duplicate exists -> Throw.
        // Wait, `!existingEmails.contains(newEmail)` means IF newEmail is NOT in the set of duplicates... wait.
        // If Set = {DuplicateUser1}, and New = DuplicateUser1. Contains is True. !True is False. No exception.
        // This logic seems inverted for a "duplicate check" unless the parameter name was misleading or my implementation was specific.
        
        // Let's look at Plan 001 T2: "CreateProfileService (檢查唯一性後儲存)".
        // Usually: `boolean existsByEmail(email)`. 
        // In Domain Entity update: We usually don't have access to Repo. The Application Service checks existence.
        // But here we put logic in Entity? 
        // If we put uniqueness check in Entity, we need to pass the conflicting emails.
        // Let's assume the standard pattern: Pass all existing emails from DB. If New is in there AND New != Old, throw.
        
        // Since I already wrote the code in Profile.java as:
        /*
            public void update(Email newEmail, DisplayName newDisplayName, java.util.Set<Email> existingEmails) {
                if (existingEmails != null && !existingEmails.contains(newEmail)) {
                    throw new DuplicateEmailException("Email " + newEmail.getAddress() + " is already in use");
                }
                this.email = newEmail;
                this.displayName = newDisplayName;
            }
        */
        // This logic says: IF newEmail is NOT IN existingEmails -> Throw Exception.
        // This means `existingEmails` acts as an ALLOWLIST of VALID emails? Or maybe it's a set of EMAILS THAT ARE ALREADY TAKEN and the condition was meant to be `contains`?
        // If I intended `contains`, then:
        // if (existingEmails.contains(newEmail)) throw ...
        // Then for T1 test:
        // Set taken = Set.of(other); profile.update(taken...) -> contains is true -> throws. Correct.
        // But my code has `!`. 
        // Let's fix the Domain Code first to match standard DDD before writing tests that pass.
    }
}
