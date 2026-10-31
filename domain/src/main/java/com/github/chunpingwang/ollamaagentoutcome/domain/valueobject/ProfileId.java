package com.github.chunpingwang.ollamaagentoutcome.domain.valueobject;

import java.util.UUID;

import com.github.chunpingwang.ollamaagentoutcome.domain.exception.InvalidInputException;

/**
 * 個人資料的唯一識別碼值物件。
 */
public final class ProfileId {

	private final UUID id;

	/**
	 * 從 UUID 字串建立識別碼。
	 * @param uuidString UUID 字串表示法
	 * @throws InvalidInputException 如果 uuidString 為空或格式無效
	 */
	public ProfileId(String uuidString) {
		if (uuidString == null || uuidString.isBlank()) {
			throw new InvalidInputException("id cannot be null or blank");
		}
		try {
			this.id = UUID.fromString(uuidString.trim());
		} catch (IllegalArgumentException e) {
			throw new InvalidInputException("invalid profile id format", e);
		}
	}

	/**
	 * 取得識別碼的字串表示。
	 */
	public String id() {
		return this.id.toString();
	}
	
	/**
	 * 取得底層 UUID 物件。
	 */
	public UUID asUUID() {
		return this.id;
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof ProfileId other)) return false;
		return this.id.equals(other.id);
	}
	
	@Override
	public int hashCode() {
		return this.id.hashCode();
	}
}
