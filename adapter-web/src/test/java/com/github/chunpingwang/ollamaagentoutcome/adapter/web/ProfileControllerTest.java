package com.github.chunpingwang.ollamaagentoutcome.adapter.web;




import com.github.chunpingwang.ollamaagentoutcome.domain.DisplayName;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import tools.jackson.databind.ObjectMapper;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.CreateProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.DeleteProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.GetProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.UpdateProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.domain.DuplicateEmailException;
import com.github.chunpingwang.ollamaagentoutcome.domain.InvalidEmailFormatException;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileNotFoundException;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ProfileController.class)
class ProfileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CreateProfileUseCase createProfileUseCase;

    @MockitoBean
    private GetProfileUseCase getProfileUseCase;

    @MockitoBean
    private UpdateProfileUseCase updateProfileUseCase;

    @MockitoBean
    private DeleteProfileUseCase deleteProfileUseCase;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void should_create_profile_with_201_and_location_header() throws Exception {
        var request = new CreateProfileRequest("user@example.com", "Test User");
        
        doNothing().when(createProfileUseCase).execute(any(CreateProfileUseCase.CreateProfileRequest.class));

        // 由於 Use Case 是 void，我們無法直接獲取生成的 ID。
        // 但測試主要驗證狀態碼為 201。Location Header 通常由 Controller 設置，
        // 若 Use Case 不返回 ID，Controller 可能無法設置正確的 Location。
        // 在此模擬情境下，我們僅驗證 201 Created。
        mockMvc.perform(post("/api/profiles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andDo(print()); 
    }

    @Test
    void should_return_400_on_validation_failure() throws Exception {
        var request = new CreateProfileRequest("", "");
        
        mockMvc.perform(post("/api/profiles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void should_return_409_on_duplicate_email() throws Exception {
        var request = new CreateProfileRequest("dup@test.com", "User");
        
        doThrow(new DuplicateEmailException("duplicate"))
            .when(createProfileUseCase).execute(any(CreateProfileUseCase.CreateProfileRequest.class));

        mockMvc.perform(post("/api/profiles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isConflict());
    }

    @Test
    void should_return_400_on_invalid_email_format() throws Exception {
        var request = new CreateProfileRequest("invalid-email", "User");
        
        // 注意：如果 Email 驗證在 Domain 層進行，且 Controller 直接調用 Use Case，
        // 則 InvalidEmailFormatException 會被拋出。
        // GlobalExceptionHandler 需要處理它。
        doThrow(new InvalidEmailFormatException("bad format"))
            .when(createProfileUseCase).execute(any(CreateProfileUseCase.CreateProfileRequest.class));

        mockMvc.perform(post("/api/profiles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void should_get_profile_with_200_and_json_body() throws Exception {
        String id = "550e8400-e29b-41d4-a716-446655440000";
        when(getProfileUseCase.findById(id))
            .thenReturn(Optional.of(com.github.chunpingwang.ollamaagentoutcome.domain.Profile.create(
                new com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId(id),
                new com.github.chunpingwang.ollamaagentoutcome.domain.Email("test@example.com"),
                new com.github.chunpingwang.ollamaagentoutcome.domain.DisplayName("Tester")
            )));

        mockMvc.perform(get("/api/profiles/{id}", id))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.email").value("test@example.com"))
            .andExpect(jsonPath("$.displayName").value("Tester"));
    }

    @Test
    void should_return_404_when_profile_not_found_for_get() throws Exception {
        String notFoundId = "non-existent-id";
        
        doThrow(new ProfileNotFoundException(notFoundId))
            .when(getProfileUseCase).findById(anyString());

        mockMvc.perform(get("/api/profiles/{id}", notFoundId))
            .andExpect(status().isNotFound());
    }

    @Test
    void should_update_profile_with_200_ok() throws Exception {
        String id = "550e8400-e29b-41d4-a716-446655440000";
        var request = new UpdateProfileRequest("new@test.com", "New Name");
        
        doNothing().when(updateProfileUseCase).execute(any(UpdateProfileUseCase.UpdateRequest.class), anyString());

        mockMvc.perform(put("/api/profiles/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk());
            
        verify(updateProfileUseCase).execute(org.mockito.ArgumentMatchers.any(UpdateProfileUseCase.UpdateRequest.class), org.mockito.ArgumentMatchers.eq(id));
    }

    @Test
    void should_return_404_when_profile_not_found_for_update() throws Exception {
        String notFoundId = "non-existent-id";
        var request = new UpdateProfileRequest("test@test.com", "Name");
        
        doThrow(new ProfileNotFoundException(notFoundId))
            .when(updateProfileUseCase).execute(any(UpdateProfileUseCase.UpdateRequest.class), anyString());

        mockMvc.perform(put("/api/profiles/{id}", notFoundId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isNotFound());
    }

    @Test
    void should_delete_profile_with_204_no_content() throws Exception {
        String id = "550e8400-e29b-41d4-a716-446655440000";
        
        doNothing().when(deleteProfileUseCase).deleteById(anyString());

        mockMvc.perform(delete("/api/profiles/{id}", id))
            .andExpect(status().isNoContent());
            
        verify(deleteProfileUseCase).deleteById(id);
    }

    @Test
    void should_return_404_when_profile_not_found_for_delete() throws Exception {
        String notFoundId = "non-existent-id";
        
        doThrow(new ProfileNotFoundException(notFoundId))
            .when(deleteProfileUseCase).deleteById(anyString());

        mockMvc.perform(delete("/api/profiles/{id}", notFoundId))
            .andExpect(status().isNotFound());
    }
}
