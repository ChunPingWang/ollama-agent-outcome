package com.github.chunpingwang.ollamaagentoutcome.domain.entity;

import com.github.chunpingwang.ollamaagentoutcome.domain.valueobject.Bio;
import com.github.chunpingwang.ollamaagentoutcome.domain.valueobject.DisplayName;
import com.github.chunpingwang.ollamaagentoutcome.domain.valueobject.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.valueobject.ProfileId;

/**
 * 個人資料領域實體，負責狀態維護與基本業務規則。
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

    /**
     * 更新顯示名稱與簡介。
     */
    public void update(DisplayName newDisplayName, Bio newBio) {
        this.displayName = newDisplayName;
        this.bio = newBio;
    }

    public ProfileId id() {
        return id;
    }

    public Email email() {
        return email;
    }

    public DisplayName displayName() {
        return displayName;
    }

    public Bio bio() {
        return bio;
    }
}
