package com.github.chunpingwang.ollamaagentoutcome.domain.entity;

import java.util.Objects;

import com.github.chunpingwang.ollamaagentoutcome.domain.exception.InvalidInputException;
import com.github.chunpingwang.ollamaagentoutcome.domain.valueobject.Age;
import com.github.chunpingwang.ollamaagentoutcome.domain.valueobject.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.valueobject.Name;
import com.github.chunpingwang.ollamaagentoutcome.domain.valueobject.ProfileId;

/**
 * 個人資料領域實體。
 * 
 * 採用不可變設計，更新時回傳新實例以確保狀態一致性。
 */
public final class Profile {

	private final ProfileId id;
	private Name name;
	private Email email;
	private Age age;

	private Profile(ProfileId id, Name name, Email email, Age age) {
		this.id = Objects.requireNonNull(id);
		this.name = Objects.requireNonNull(name);
		this.email = Objects.requireNonNull(email);
		this.age = Objects.requireNonNull(age);
	}

	/**
	 * 建立新的個人資料。
	 * @param id 識別碼
	 * @param name 姓名
	 * @param email 電子郵件
	 * @param age 年齡
	 * @return 新的 Profile 實例
	 */
	public static Profile create(ProfileId id, Name name, Email email, Age age) {
		return new Profile(id, name, email, age);
	}

	/**
	 * 取得識別碼。
	 */
	public ProfileId id() {
		return this.id;
	}

	/**
	 * 取得姓名。
	 */
	public Name name() {
		return this.name;
	}

	/**
	 * 取得電子郵件。
	 */
	public Email email() {
		return this.email;
	}

	/**
	 * 取得年齡。
	 */
	public Age age() {
		return this.age;
	}

	/**
	 * 更新個人資料的部分欄位。
	 * <p>
	 * 若傳入 null 則保留原值；否則驗證新值並回傳新的 Profile 實例。
	 * </p>
	 * 
	 * @param newName 新姓名（可為 null）
	 * @param newEmail 新電子郵件（可為 null）
	 * @param newAge 新年齡（可為 null）
	 * @return 更新後的 Profile 實例
	 * @throws InvalidInputException 如果所有參數皆為 null，或新值無效
	 */
	public Profile update(Name newName, Email newEmail, Age newAge) {
		if (newName == null && newEmail == null && newAge == null) {
			throw new InvalidInputException("name cannot be null");
		}

		Name resolvedName = (newName != null) ? newName : this.name;
		Email resolvedEmail = (newEmail != null) ? newEmail : this.email;
		Age resolvedAge = (newAge != null) ? newAge : this.age;

		return create(this.id, resolvedName, resolvedEmail, resolvedAge);
	}
}
