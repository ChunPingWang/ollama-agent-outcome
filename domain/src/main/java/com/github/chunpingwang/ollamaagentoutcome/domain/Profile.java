package com.github.chunpingwang.ollamaagentoutcome.domain;

/**
 * 個人資料實體。
 */
public record Profile(
        ProfileId id,
        String name,
        String email,
        String note
) {

    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";

    /**
     * 建立新的個人資料。
     *
     * @param id    識別碼
     * @param name  姓名
     * @param email 電子郵件
     * @param note  備註
     * @return 新的 Profile 實例
     * @throws InvalidNameException  當姓名無效時
     * @throws InvalidEmailException 當電子郵件無效時
     */
    public static Profile create(ProfileId id, String name, String email, String note) {
        validateName(name);
        validateEmail(email);
        return new Profile(id, name, email, note);
    }

    /**
     * 更新個人資料。
     *
     * @param name  新姓名
     * @param email 新電子郵件
     * @param note  新備註
     * @return 新的 Profile 實例
     * @throws InvalidNameException  當姓名無效時
     * @throws InvalidEmailException 當電子郵件無效時
     */
    public Profile update(String name, String email, String note) {
        validateName(name);
        validateEmail(email);
        return new Profile(this.id, name, email, note);
    }

    private static void validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new InvalidNameException("name must not be blank");
        }
    }

    private static void validateEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new InvalidEmailException("email must not be blank");
        }
        if (!email.matches(EMAIL_REGEX)) {
            throw new InvalidEmailException("email format is invalid");
        }
    }
}
