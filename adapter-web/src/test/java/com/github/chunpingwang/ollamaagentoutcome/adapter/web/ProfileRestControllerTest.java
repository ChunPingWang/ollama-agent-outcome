package com.github.chunpingwang.ollamaagentoutcome.adapter.web;




import com.github.chunpingwang.ollamaagentoutcome.application.usecase.GetProfileQuery;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import tools.jackson.databind.ObjectMapper;

import com.github.chunpingwang.ollamaagentoutcome.application.port.in.CreateProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.DeleteProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.GetProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.UpdateProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.domain.DuplicateEmailException;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileNotFoundException;
import com.github.chunpingwang.ollamaagentoutcome.domain.DisplayName;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;
import com.github.chunpingwang.ollamaagentoutcome.application.usecase.DeleteProfileCommand;

@WebMvcTest(ProfileRestController.class)
@AutoConfigureMockMvc
class ProfileRestControllerTest {

    @Autowired private ObjectMapper objectMapper;
    @Autowired private org.springframework.test.web.servlet.MockMvc mockMvc;

    @MockitoBean private CreateProfileUseCase createProfileUseCase;
    @MockitoBean private GetProfileUseCase getProfileUseCase;
    @MockitoBean private UpdateProfileUseCase updateProfileUseCase;
    @MockitoBean private DeleteProfileUseCase deleteProfileUseCase;

    @Test
    void should_create_profile_and_return_201_with_location() throws Exception {
        // given
        String email = "new@example.com";
        String displayName = "New User";
        
        String jsonRequest = """
            {"email": "%s", "displayName": "%s"}
            """.formatted(email, displayName);

        // when & then
        mockMvc.perform(post("/api/profiles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
               .andExpect(status().isCreated())
               .andExpect(header().exists("Location")); 
        
        verify(createProfileUseCase).execute(any());
    }

    @Test
    void should_reject_invalid_email_format_on_create() throws Exception {
        // given
        String invalidJson = "{\"email\": \"invalid-email\", \"displayName\": \"User\"}";

        // when & then
        mockMvc.perform(post("/api/profiles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidJson))
               .andExpect(status().isBadRequest());
               
        verifyNoInteractions(createProfileUseCase);
    }

    @Test
    void should_return_409_when_duplicate_email_during_create() throws Exception {
        // given
        doThrow(new DuplicateEmailException("duplicate"))
            .when(createProfileUseCase).execute(any());
            
        String validJson = "{\"email\": \"dup@test.com\", " + "\"displayName\": \"Dup User\"}";

        // when & then
        mockMvc.perform(post("/api/profiles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(validJson))
               .andExpect(status().isConflict());
    }

    @Test
    void should_get_profile_by_id_and_return_200() throws Exception {
        // given
        UUID id = UUID.randomUUID();
        var profile = new Profile(
            new ProfileId(id),
            new Email("get@example.com"),
            new DisplayName("Get User")
        );
        
        when(getProfileUseCase.execute(any())).thenReturn(profile);

        // when & then
        mockMvc.perform(get("/api/profiles/{id}", id))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.id").value(id.toString()))
               .andExpect(jsonPath("$.email").value("get@example.com"))
               .andExpect(jsonPath("$.displayName").value("Get User"));
               
        verify(getProfileUseCase).execute(new com.github.chunpingwang.ollamaagentoutcome.application.usecase.GetProfileQuery(id));
    }

    @Test
    void should_return_404_when_profile_not_found_on_get() throws Exception {
        // given
        UUID nonExistentId = UUID.randomUUID();
        doThrow(new ProfileNotFoundException(nonExistentId))
            .when(getProfileUseCase).execute(any());

        // when & then
        mockMvc.perform(get("/api/profiles/{id}", nonExistentId))
               .andExpect(status().isNotFound());
    }

    @Test
    void should_update_profile_and_return_200() throws Exception {
        // given
        UUID id = UUID.randomUUID();
        String email = "updated@test.com";
        String displayName = "Updated Name";
        
        String jsonRequest = """
            {"email": "%s", "displayName": "%s"}
            """.formatted(email, displayName);

        // when & then
        mockMvc.perform(put("/api/profiles/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
               .andExpect(status().isOk());
               
        verify(updateProfileUseCase).execute(any());
    }

    @Test
    void should_delete_profile_and_return_204() throws Exception {
        // given
        UUID id = UUID.randomUUID();

        // when & then
        mockMvc.perform(delete("/api/profiles/{id}", id))
               .andExpect(status().isNoContent());
               
        verify(deleteProfileUseCase).execute(new DeleteProfileCommand(id));
    }
    
    private static void verifyNoInteractions(Object... mocks) {
        for (Object mock : mocks) {
            org.mockito.Mockito.verifyNoInteractions(mock);
        }
    }
}
