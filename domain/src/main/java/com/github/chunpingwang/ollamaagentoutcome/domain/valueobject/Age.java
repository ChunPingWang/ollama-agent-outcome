package com.github.chunpingwang.ollamaagentoutcome.domain.valueobject;

import com.github.chunpingwang.ollamaagentoutcome.domain.exception.InvalidInputException;

/**
 * 年齡值物件。
 */
public record Age(int years) {

	/**
	 * 建立新的 Age。
	 * @param years 年齡數值
	 * @throws InvalidInputException 如果 years 超出合理範圍
	 */
	public Age {
		if (years < 0 || years > 150) {
			throw new InvalidInputException("age must be between 0 and 150");
		}
	}
}
