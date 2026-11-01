package com.github.chunpingwang.ollamaagentoutcome.adapter.web;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import tools.jackson.databind.ObjectMapper;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.CreateProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.DeleteProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.GetProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.UpdateProfileDisplayNameUseCase;
import com.github.chunpingwang.ollamaagentoutcome.domain.DisplayName;
import com.github.chunpingwang.ollamaagentoutcome.domain.DuplicateEmailException;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.InvalidEmailException;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileNotFoundException;

@WebMvcTest(ProfileController.class)
class ProfileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CreateProfileUseCase createProfileUseCase;

    @MockitoBean
    private GetProfileUseCase getProfileUseCase;

    @MockitoBean
    private UpdateProfileDisplayNameUseCase updateProfileDisplayNameUseCase;

    @MockitoBean
    private DeleteProfileUseCase deleteProfileUseCase;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void should_create_profile_and_return_201_with_location() throws Exception {
        UUID id = UUID.randomUUID();
        String email = "user@example.com";
        String displayName = "John Doe";
        
        when(createProfileUseCase.execute(email, displayName)).thenReturn(new ProfileId(id.toString()));

        String requestBody = """
            {"email": "%s", "displayName": "%s"}
            """.formatted(email, displayName);

        mockMvc.perform(post("/api/profiles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
            .andExpect(status().isCreated())
            .andExpect(header().exists("Location"));
            
        verify(createProfileUseCase).execute(eq(email), eq(displayName));
    }

    @Test
    void should_get_profile_by_id_and_return_200() throws Exception {
        UUID id = UUID.randomUUID();
        Email email = new Email("user@example.com");
        DisplayName dn = new DisplayName("John Doe");
        Profile profile = new Profile(id, email, dn);
        
        when(getProfileUseCase.execute(new ProfileId(id.toString()))).thenReturn(profile);

        mockMvc.perform(get("/api/profiles/" + id.toString())
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(id.toString()))
            .andExpect(jsonPath("$.email").value(email.address()))
            .andExpect(jsonPath("$.displayName").value(dn.name()));
    }

    @Test
    void should_update_display_name_and_return_200() throws Exception {
        UUID id = UUID.randomUUID();
        String newName = "New Name";
        
        doNothing().when(updateProfileDisplayNameUseCase).execute(any(ProfileId.class), any(DisplayName.class));

        String requestBody = """
            {"displayName": "%s"}
            """.formatted(newName);

        mockMvc.perform(put("/api/profiles/" + id.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
            .andExpect(status().isOk());
            
        verify(updateProfileDisplayNameUseCase).execute(eq(new ProfileId(id.toString())), argThat(arg -> 
            arg != null && arg.name().equals(newName)));
    }

    @Test
    void should_delete_profile_and_return_204() throws Exception {
        UUID id = UUID.randomUUID();
        
        doNothing().when(deleteProfileUseCase).execute(any(ProfileId.class));

        mockMvc.perform(delete("/api/profiles/" + id.toString()))
            .andExpect(status().isNoContent());
            
        verify(deleteProfileUseCase).execute(new ProfileId(id.toString()));
    }

    @Test
    void should_reject_invalid_email_format_with_400() throws Exception {
        String invalidEmail = "not-an-email";
        String displayName = "User";
        
        when(createProfileUseCase.execute(invalidEmail, displayName)).thenThrow(new InvalidEmailException("Invalid format"));

        String requestBody = """
            {"email": "%s", "displayName": "%s"}
            """.formatted(invalidEmail, displayName);

        mockMvc.perform(post("/api/profiles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error").exists());
    }

    @Test
    void should_reject_duplicate_email_with_409() throws Exception {
        UUID id = UUID.randomUUID();
        String email = "dup@example.com";
        String displayName = "Dup User";
        
        when(createProfileUseCase.execute(email, displayName)).thenThrow(new DuplicateEmailException("Duplicate"));

        String requestBody = """
            {"email": "%s", "displayName": "%s"}
            """.formatted(email, displayName);

        mockMvc.perform(post("/api/profiles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.error").value("Duplicate email address"));
    }

    @Test
    void should_return_404_when_profile_not_found_on_get() throws Exception {
        UUID id = UUID.randomUUID();
        
        when(getProfileUseCase.execute(any(ProfileId.class))).thenThrow(
            new ProfileNotFoundException(id));

        mockMvc.perform(get("/api/profiles/" + id.toString()))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.error").value("Profile not found"));
    }

    @Test
    void should_return_404_when_deleting_non_existent_profile() throws Exception {
        UUID id = UUID.randomUUID();
        
        doThrow(new ProfileNotFoundException(id)).when(deleteProfileUseCase).execute(any(ProfileId.class));

        mockMvc.perform(delete("/api/profiles/" + id.toString()))
            .andExpect(status().isNotFound());
    }
    
    @Test
    void should_reject_blank_email_with_validation_error() throws Exception {
        String requestBody = """
            {"email": "", "displayName": "User"}
            """;

        mockMvc.perform(post("/api/profiles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.email").exists()); // 驗證錯誤訊息包含欄位名稱
    }
}
