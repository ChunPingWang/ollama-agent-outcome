package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence.converter;

import com.github.chunpingwang.ollamaagentoutcome.adapter.persistence.entity.ProfileEntity;
import com.github.chunpingwang.ollamaagentoutcome.domain.entity.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.valueobject.Age;
import com.github.chunpingwang.ollamaagentoutcome.domain.valueobject.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.valueobject.Name;
import com.github.chunpingwang.ollamaagentoutcome.domain.valueobject.ProfileId;

/**
 * Domain Profile 與 JPA Entity 之間的轉換器。
 */
public final class ProfileConverter {

	private ProfileConverter() {}

	public static Profile toDomain(ProfileEntity entity) {
		if (entity == null) return null;
		
		var id = new ProfileId(entity.getId());
		var name = new Name(entity.getName());
		var email = new Email(entity.getEmail());
		var age = new Age(entity.getAge());
		
		return Profile.create(id, name, email, age);
	}

	public static ProfileEntity toEntity(Profile profile) {
		if (profile == null) return null;
		
		ProfileEntity entity = new ProfileEntity();
		entity.setId(profile.id().id());
		entity.setName(profile.name().text());
		entity.setEmail(profile.email().address());
		entity.setAge(profile.age().years());
		entity.isNew = true; 
		
		return entity;
	}
	
	/**
	 * 更新現有實體的欄位，不替換整個物件。
	 */
	public static void updateEntityFromDomain(ProfileEntity entity, Profile updatedProfile) {
		entity.setName(updatedProfile.name().text());
		entity.setEmail(updatedProfile.email().address());
		entity.setAge(updatedProfile.age().years());
	}
}
