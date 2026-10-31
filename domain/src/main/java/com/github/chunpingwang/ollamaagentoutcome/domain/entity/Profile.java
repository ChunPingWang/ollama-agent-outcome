package com.github.chunpingwang.ollamaagentoutcome.domain.entity;

import com.github.chunpingwang.ollamaagentoutcome.domain.valueobject.Bio;
import com.github.chunpingwang.ollamaagentoutcome.domain.valueobject.DisplayName;
import com.github.chunpingwang.ollamaagentoutcome.domain.valueobject.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.valueobject.ProfileId;

/**
 * 個人資料領域實體。
 */
public class Profile {
    private final ProfileId id;
    private final Email email;
    private DisplayName displayName;
    private Bio bio;

    public Profile(ProfileId id, Email email, DisplayName displayName, Bio bio) {
        this.id = id;
        this.email = email;
        this.displayName = displayName;
        this.bio = bio;
    }

    public ProfileId getId() { return id; }
    public Email getEmail() { return email; }
    public DisplayName getDisplayName() { return displayName; }
    public void setDisplayName(DisplayName displayName) { this.displayName = displayName; }
    
    public Bio getBio() { return bio; }
    public void setBio(Bio bio) { this.bio = bio; }
}
