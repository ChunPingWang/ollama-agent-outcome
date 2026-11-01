package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;

import com.github.chunpingwang.ollamaagentoutcome.domain.DisplayName;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;

/**
 * Converter between Domain Model and JPA Entity.
 */
public final class ProfileConverter {

    private ProfileConverter() {}

    /**
     * Converts a Domain Profile to a JPA Entity.
     */
    public static ProfileEntity toEntity(Profile profile) {
        if (profile == null) {
            return null;
        }
        
        // If the domain object was just created or loaded from DB, we need to determine isNew state.
        // However, typically in SavePort, we receive a fresh domain object or one modified by service.
        // To support updates correctly via Persistable logic, we assume that if it's being saved 
        // for the first time in this context without prior load, it might be new. 
        // But standard pattern: Service loads -> modifies -> saves. The entity should reflect 'not new'.
        // Since we don't have an ID mapping back easily here without tracking, we rely on the fact that
        // usually converters are used bidirectionally. For save, we create a NEW entity instance.
        // Wait, if we update, we shouldn't create a new entity with isNew=true unless it IS new.
        // Let's look at how Update works: Load Port returns Domain -> Service calls .update() -> Save Port receives same Domain.
        // How does Save Port know it's not new? It doesn't directly from Domain alone unless Domain carries state.
        // Standard approach: Converter.toEntity(Profile) creates a NEW Entity with isNew = true.
        // BUT, for Updates, we usually want to MERGE/UPDATE existing record.
        // If we use saveAndFlush on a NEW entity (isNew=true), JPA treats it as INSERT.
        // This causes issues if the ID already exists in DB (Duplicate Key).
        
        // Correction based on "Persistence Common Pitfalls":
        // "Update flow: First findById to get managed Entity, then update fields, do NOT overwrite with new Entity."
        // So, JpaSaveProfilePort should probably handle this logic or expect the converter to behave differently?
        // Actually, simpler pattern for UUID/Persistable:
        // 1. New Profile: isNew=true -> INSERT.
        // 2. Existing Profile (loaded): The domain object is just data. 
        //    To distinguish, often an adapter-specific flag or loading mechanism is used.
        //    
        // Let's stick to the simplest interpretation that works with `saveAndFlush`:
        // If we pass a profile that was JUST loaded by LoadPort, how does SavePort know?
        // It doesn't, unless we load the ENTITY first inside SavePort? No, that defeats purpose of Port abstraction slightly but ensures correctness.
        // OR, we assume that `toEntity` sets isNew=false if the ID matches an existing one? No, repository access needed.
        
        // Alternative: The Service layer handles the distinction? No, Port implementation details are hidden from Service.
        // Best Practice for Hexagonal + JPA Persistable:
        // When saving, check if entity exists in DB? Expensive.
        // Instead, let's look at the test requirements.
        // Test `should_update_existing_profile` creates initialProfile, saves it. Then tries to save another WITH SAME EMAIL.
        // This implies "Update" via SavePort isn't about updating fields on same record, but rather ensuring uniqueness constraint triggers error if you try to INSERT duplicate email.
        // Wait, the test name is `should_update_existing_profile` but the code inside checks for DuplicateEmailException!
        // Ah, looking closely at `JpaSaveProfilePortTest.should_update_existing_profile()`:
        // It saves p1. Then tries to save p2 with DIFFERENT ID but SAME EMAIL. 
        // And expects DuplicateEmailException.
        // So this test is actually testing DUPLICATE DETECTION, not field update logic of a single row.
        // The method name is misleading or I am misinterpreting "update".
        // Let's re-read carefully: "Simulate an update scenario... A simpler update test: Create one, then try to create another with SAME email should fail."
        // Okay, so the Port doesn't need to support "UPDATE existing row by merging fields" directly from Domain object without loading first?
        // Actually, standard JPA `save()` handles both insert and update based on identifier existence/isNew flag.
        
        // If we want true Update capability (changing email/name of existing profile):
        // We MUST load the entity first in SavePort? Or pass it as managed?
        // Since Service calls LoadPort -> gets Domain -> modifies Domain -> calls SavePort(Domain).
        // SavePort receives Domain. It needs to convert to Entity.
        // If Converter creates NEW Entity(isNew=true), JPA will INSERT. This fails if PK exists.
        // Therefore, for Updates, we typically do NOT use save() on a new detached entity unless we know it's new.
        // OR, we implement `toEntity` such that it checks DB? No.
        
        // Correct Pattern for this specific setup (Persistable + UUID):
        // 1. New Profile: isNew = true.
        // 2. Existing Profile: The domain object itself doesn't carry "managed" status easily across layers without overhead.
        //    However, many implementations simply assume that if you are calling Save after Load+Modify, 
        //    you should have loaded the ENTITY directly or passed an ID to check existence.
        
        // Let's look at `JpaSaveProfilePort` implementation strategy:
        // Option A: Always treat incoming Domain as NEW. Use `saveAndFlush`. If conflict, catch exception.
        //          This works for Create and Duplicate detection. For Update, it would fail with Unique Constraint Violation if Email changed but is unique? No, if email changes to something else, it inserts new row -> Error because old row still has old email? No, old row stays. New row inserted. But wait, if I change name only, email same -> Insert fails duplicate key. THIS IS BAD FOR UPDATES.
        
        // So, simple `isNew=true` converter DOES NOT SUPPORT UPDATE BY MERGE correctly if we just insert.
        // We need to support Updates.
        // How does Spring Data JPA handle updates on detached entities? It checks identifier.
        // With Persistable, it uses `isNew()`.
        // If we want to update, `isNew()` must be false.
        // Who sets `isNew=false` before save?
        // The Service layer could load the Entity via a different port? No, Port returns Domain.
        
        // Solution: In SavePort, check if entity exists in DB by ID first? 
        // Or, simpler: LoadPort returns Domain. SavePort receives Domain.
        // If we assume that "Update" flow involves loading the profile first (which we do), 
        // maybe we can pass an extra flag or use a specific method signature? 
        // But Ports are fixed.
        
        // Let's look at the test again for Update logic.
        // There is NO test in T4 that tests "Change Name of existing Profile".
        // Test `should_update_existing_profile` actually tests Duplicate Email detection!
        // "A simpler update test: Create one, then try to create another with SAME email should fail."
        // So, strictly speaking, for THIS task, we might not need full merge-update support in SavePort IF no test requires changing fields of an existing record without creating a duplicate key error scenario on insert.
        // BUT, T6 Web Adapter will likely call UpdateService which calls Load -> Modify -> Save.
        // If Save always inserts as NEW, updating a name will cause INSERT -> Unique Constraint Violation (if email unchanged) OR Insert New Row (if email changed). Both wrong.
        
        // Therefore, SavePort MUST handle Updates correctly.
        // To do this, it needs to know if the domain object represents an EXISTING entity.
        // Since Domain doesn't have `isNew`, and Service just passes the modified Domain...
        // We must check existence in DB inside SavePort or assume ID exists.
        // Checking existence by ID is safer.
        
        // Revised Strategy for JpaSaveProfilePort:
        // 1. Convert Domain to Entity. Set isNew = true initially? No.
        //    Let's set isNew based on whether we find the ID in DB? 
        //    Or better: Use `saveAndFlush`. If it throws DataIntegrityViolationException due to unique constraint, convert to DuplicateEmailException.
        //    But what about PK conflict? If I try to update Name, Email stays same. 
        //    If Converter creates new Entity(isNew=true), JPA tries INSERT. 
        //    INSERT fails because Email already exists. Exception thrown. Converted to DuplicateEmailException? NO! It should be Update.
        
        // This implies my "Converter always sets isNew=true" approach is FLAWED for updates.
        
        // Correct Approach:
        // The Port implementation should probably load the existing entity first if updating?
        // Or, use a different method signature? No, port interface is fixed (`void save(Profile)`).
        
        // Alternative: Don't use Persistable logic for Updates. Just do FindById -> Merge fields -> Flush.
        // In SavePort.save(profile):
        //   ProfileEntity entity = profileRepository.findById(profile.id().value()).orElse(null);
        //   if (entity == null) {
        //       // New Profile
        //       entity = converter.toEntity(profile); // isNew=true
        //   } else {
        //       // Existing Profile - Update fields
        //       entity.setEmail(profile.email().address());
        //       entity.setDisplayName(profile.displayName().name());
        //   }
        //   repository.saveAndFlush(entity);
        
        // This handles both Create and Update correctly without relying on Domain `isNew` state or complex converters.
        // And it naturally triggers Unique Constraint Violation only if we try to INSERT a NEW email that exists.
        // If we UPDATE an existing record with SAME email, no violation occurs because it's an UPDATE statement, not INSERT.
        
        // Let's implement this strategy in JpaSaveProfilePort. The Converter just needs basic mapping.

        return new ProfileEntity(
            profile.id().value(),
            profile.email().address(),
            profile.displayName() != null ? profile.displayName().name() : ""
        );
    }

    /**
     * Converts a JPA Entity to a Domain Profile.
     */
    public static Profile toDomain(ProfileEntity entity) {
        if (entity == null) {
            return null;
        }
        
        return Profile.create(
            new ProfileId(entity.getId()),
            new Email(entity.getEmail()),
            new DisplayName(entity.getDisplayName())
        );
    }
}
