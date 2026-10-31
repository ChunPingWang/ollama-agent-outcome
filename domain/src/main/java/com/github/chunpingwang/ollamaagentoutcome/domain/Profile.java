package com.github.chunpingwang.ollamaagentoutcome.domain;

/**
 * 個人資料 Domain Entity。
 */
public class Profile {

    private final ProfileId id;
    private Name name;
    private Email email;
    private Bio bio;

    public Profile(ProfileId id, Name name, Email email, Bio bio) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.bio = bio;
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

    public Bio bio() {
        return bio;
    }

    /**
     * 更新個人資料。若傳入 null 則保留原有值。
     */
    public void update(Name newName, Email newEmail, Bio newBio) {
        if (newName != null) {
            this.name = newName;
        }
        if (newEmail != null) {
            this.email = newEmail;
        }
        if (newBio != null) {
            this.bio = newBio;
        }
    }
}
