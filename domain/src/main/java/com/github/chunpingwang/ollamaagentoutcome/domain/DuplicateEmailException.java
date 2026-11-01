package com.github.chunpingwang.ollamaagentoutcome.domain;

import java.util.Objects;

/**
 * 當電子郵件已存在於系統中時拋出的例外。
 */
public class DuplicateEmailException extends RuntimeException {
    private final ProfileId profileId;
    private final Email email;

    public DuplicateEmailException(ProfileId profileId, Email email) {
        super(email.value() + " already exists");
        this.profileId = Objects.requireNonNull(profileId);
        this.email = Objects.requireNonNull(email);
    }

    public ProfileId getProfileId() {
        return profileId;
    }

    public Email getEmail() {
        return email;
    }
}
