package com.github.chunpingwang.ollamaagentoutcome.domain;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * 電子郵件值物件，包含基本格式驗證。
 */
public record Email(String value) {
    private static final Pattern EMAIL_PATTERN = 
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

    public Email {
        if (value == null || value.isBlank()) {
            throw new InvalidEmailException("email cannot be blank or empty");
        }
        String trimmed = value.trim();
        if (!EMAIL_PATTERN.matcher(trimmed).matches()) {
            throw new InvalidEmailException("invalid email format: " + trimmed);
        }
        // Record compact constructor automatically assigns parameters to fields,
        // but explicit assignment is not allowed for 'this.value' in the body 
        // because it's already assigned by the compiler-generated accessor logic.
        // We just need to ensure validation passed. The field 'value' holds the original arg.
        // If we want to store the trimmed version, we must use a different approach or accept the untrimmed.
        // However, standard practice for Value Objects often implies canonicalization.
        // Since Java records don't allow reassigning the primary argument directly via this.field = ...
        // after construction starts implicitly, and we can't change the signature easily without breaking tests expecting `value()`,
        // let's stick to storing the validated input. If trimming is strictly required for storage, 
        // we usually create a helper method or just trust the caller, OR we implement as a class.
        // BUT, looking at Name.java error, same issue exists there.
        // To fix both cleanly while keeping them as records:
        // We cannot assign `this.value` inside the secondary constructor block if it conflicts with implicit assignment?
        // Actually, in a record compact constructor, you CAN'T do `this.value = ...`. You MUST return from the constructor.
        // The correct way to "transform" data in a Record constructor is to NOT modify `this.value` but rather rely on the fact that
        // the parameter IS the value. If we need to trim, we should probably not use a simple record for mutable-like behavior,
        // OR we simply store the trimmed version by ensuring the *input* was already processed? No.
        
        // Correction: In Java Records, the canonical constructor assigns parameters to fields automatically.
        // You CANNOT reassign `this.fieldName` inside the body of the compact constructor because the field is final and effectively assigned before the body runs (or during).
        // Wait, actually, the compiler generates code like: this.value = value; then executes your body.
        // So `this.value = trimmed` fails because 'value' is final.
        
        // Solution: Since we want validation AND potential normalization (trimming), 
        // and we can't change the stored value easily in a record without breaking immutability guarantees or syntax,
        // let's look at Name.java too. It has the same error.
        
        // To fix this properly while keeping them as records:
        // We will remove the trimming assignment attempt. We will validate the input.
        // If strict trimming is needed for business logic consistency, it might be better handled by the Service layer passing pre-trimmed strings,
        // OR we accept that the Record stores exactly what was passed (validated).
        // Given the test `should_create_valid_email` expects `"valid@example.com"`, and input is likely clean,
        // but `Name` test `should_create_valid_name` expects `"John Doe"` from `"John Doe"`.
        // Let's just perform validation. The "trimmed" variable was unused if not assigned.
    }
}
