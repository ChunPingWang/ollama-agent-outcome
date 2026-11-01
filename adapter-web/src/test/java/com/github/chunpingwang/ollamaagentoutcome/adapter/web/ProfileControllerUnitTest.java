package com.github.chunpingwang.ollamaagentoutcome.adapter.web;



import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import tools.jackson.databind.ObjectMapper;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.CreateProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.DeleteProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.GetProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.UpdateProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.domain.DuplicateEmailException;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.DisplayName;
import com.github.chunpingwang.ollamaagentoutcome.domain.InvalidEmailException;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileNotFoundException;

@WebMvcTest(ProfileRestController.class)
class ProfileControllerUnitTest {

    @Autowired private MockMvc mockMvc;
    
    @MockitoBean private CreateProfileUseCase createProfileUseCase;
    @MockitoBean private GetProfileUseCase getProfileUseCase;
    @MockitoBean private UpdateProfileUseCase updateProfileUseCase;
    @MockitoBean private DeleteProfileUseCase deleteProfileUseCase;

    @Test
    void should_create_profile_and_return_201_with_location() throws Exception {
        // Given
        String email = "new@example.com";
        String displayName = "New User";
        String id = UUID.randomUUID().toString();
        
        var requestJson = """
                {"email": "%s", "displayName": "%s"}
                """.formatted(email, displayName);

        Profile createdProfile = new Profile(id, new Email(email), new DisplayName(displayName));
        when(createProfileUseCase.execute(any())).thenReturn(createdProfile);

        // When & Then
        mockMvc.perform(post("/api/profiles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.email").value(email))
                .andExpect(jsonPath("$.displayName").value(displayName));
                
        verify(createProfileUseCase).execute(any());
    }

    @Test
    void should_reject_invalid_email_format_on_create() throws Exception {
        // 模擬 Use Case 因無效電子郵件格式拋出領域例外，確保測試不依賴 Jakarta Validation 的具體實作行為
        doThrow(new InvalidEmailException("無效的電子郵件格式"))
            .when(createProfileUseCase).execute(any());

        var requestJson = """
                {"email": "invalid-email", "displayName": "User"}
                """;

        // When & Then
        mockMvc.perform(post("/api/profiles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest());
                
        verify(createProfileUseCase).execute(any());
    }

    @Test
    void should_return_409_when_duplicate_email_during_create() throws Exception {
        // Given
        String email = "dup@example.com";
        String displayName = "Dup User";
        var requestJson = """
                {"email": "%s", "displayName": "%s"}
                """.formatted(email, displayName);

        doThrow(new DuplicateEmailException("電子郵件已存在")).when(createProfileUseCase).execute(any());

        // When & Then
        mockMvc.perform(post("/api/profiles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("電子郵件已存在"));
    }

    @Test
    void should_get_profile_by_id_and_return_200() throws Exception {
        // Given
        String id = UUID.randomUUID().toString();
        Profile profile = new Profile(id, new Email("test@test.com"), new DisplayName("Tester"));
        when(getProfileUseCase.execute(id)).thenReturn(Optional.of(profile));

        // When & Then
        mockMvc.perform(get("/api/profiles/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.email").value("test@test.com"))
                .andExpect(jsonPath("$.displayName").value("Tester"));
    }

    @Test
    void should_return_404_when_profile_not_found_on_get() throws Exception {
        // Given
        String id = "non-existent-id";
        when(getProfileUseCase.execute(id)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/api/profiles/" + id))
                .andExpect(status().isNotFound());
    }

    @Test
    void should_update_profile_and_return_200() throws Exception {
        // Given
        String id = UUID.randomUUID().toString();
        String email = "updated@example.com";
        String displayName = "Updated Name";
        
        var requestJson = """
                {"email": "%s", "displayName": "%s"}
                """.formatted(email, displayName);

        Profile updatedProfile = new Profile(id, new Email(email), new DisplayName(displayName));
        when(updateProfileUseCase.execute(any())).thenReturn(updatedProfile);

        // When & Then
        mockMvc.perform(put("/api/profiles/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.email").value(email))
                .andExpect(jsonPath("$.displayName").value(displayName));
                
        verify(updateProfileUseCase).execute(any());
    }

    @Test
    void should_reject_invalid_email_format_on_update() throws Exception {
        // 模擬 Use Case 因無效電子郵件格式拋出領域例外，確保測試不依賴 Jakarta Validation 的具體實作行為
        doThrow(new InvalidEmailException("無效的電子郵件格式"))
            .when(updateProfileUseCase).execute(any());

        String id = UUID.randomUUID().toString();
        var requestJson = """
                {"email": "bad-email", "displayName": "Name"}
                """;

        // When & Then
        mockMvc.perform(put("/api/profiles/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest());
                
        verify(updateProfileUseCase).execute(any());
    }

    @Test
    void should_return_404_when_profile_not_found_on_update() throws Exception {
        // Given
        String id = "non-existent-id";
        var requestJson = """
                {"email": "new@example.com", "displayName": "New Name"}
                """;
        
        doThrow(new ProfileNotFoundException("找不到識別碼為 " + id + " 的個人資料"))
            .when(updateProfileUseCase).execute(any());

        // When & Then
        mockMvc.perform(put("/api/profiles/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("找不到識別碼為 " + id + " 的個人資料"));
    }

    @Test
    void should_delete_profile_and_return_204() throws Exception {
        // Given
        String id = UUID.randomUUID().toString();
        doNothing().when(deleteProfileUseCase).execute(id);

        // When & Then
        mockMvc.perform(delete("/api/profiles/" + id))
                .andExpect(status().isNoContent());
                
        verify(deleteProfileUseCase).execute(id);
    }

    @Test
    void should_return_404_when_profile_not_found_on_delete() throws Exception {
        // Given
        String id = "non-existent-id";
        doThrow(new ProfileNotFoundException("找不到識別碼為 " + id + " 的個人資料"))
            .when(deleteProfileUseCase).execute(id);

        // When & Then
        mockMvc.perform(delete("/api/profiles/" + id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("找不到識別碼為 " + id + " 的個人資料"));
    }
}
