package com.github.chunpingwang.ollamaagentoutcome.domain.entity;

import com.github.chunpingwang.ollamaagentoutcome.domain.exception.InvalidEmailException;
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
        if (email == null) {
            throw new InvalidEmailException("Invalid email address: cannot be empty or blank");
        }
        this.id = id;
        this.email = email;
        this.displayName = displayName;
        this.bio = bio;
    }

    /**
     * 取得個人資料唯一識別碼。
     *
     * @return 識別碼值物件
     */
    public ProfileId getId() {
        return id;
    }

    /**
     * 取得電子郵件地址。
     *
     * @return 電子郵件值物件
     */
    public Email getEmail() {
        return email;
    }

    /**
     * 取得顯示名稱。
     *
     * @return 顯示名稱值物件
     */
    public DisplayName getDisplayName() {
        return displayName;
    }

    /**
     * 更新顯示名稱。
     *
     * @param displayName 新的顯示名稱值物件
     */
    public void setDisplayName(DisplayName displayName) {
        this.displayName = displayName;
    }

    /**
     * 取得個人簡介。
     *
     * @return 個人簡介值物件，若未設定則為 null
     */
    public Bio getBio() {
        return bio;
    }

    /**
     * 更新個人簡介。
     *
     * @param bio 新的個人簡介值物件，可為 null
     */
    public void setBio(Bio bio) {
        this.bio = bio;
    }
}
