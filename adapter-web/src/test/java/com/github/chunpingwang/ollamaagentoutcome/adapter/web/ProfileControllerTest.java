package com.github.chunpingwang.ollamaagentoutcome.adapter.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
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
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureWebMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import tools.jackson.databind.ObjectMapper;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.CreateProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.usecase.CreateProfileCommand;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.GetProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.usecase.GetProfileQuery;
import com.github.chunpingwang.ollamaagentoutcome.application.usecase.ProfileResult;
import com.github.chunpingwang.ollamaagentoutcome.domain.DuplicateEmailException;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileNotFoundException;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.UpdateProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.usecase.UpdateProfileCommand;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.DeleteProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.usecase.DeleteProfileCommand;

@WebMvcTest(ProfileController.class)
@AutoConfigureWebMvc
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
    void should_create_profile_and_return_201_with_location() throws Exception {
        String name = "John Doe";
        String email = "john@example.com";
        String bio = "Developer";

        doNothing().when(createProfileUseCase).execute(any(CreateProfileCommand.class));

        String requestBody = """
            {
              "name": "%s",
              "email": "%s",
              "bio": "%s"
            }
            """.formatted(name, email, bio);

        mockMvc.perform(post("/profiles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"));
        
        verify(createProfileUseCase).execute(new CreateProfileCommand(name, email, bio));
    }

    @Test
    void should_reject_invalid_request_body_with_400() throws Exception {
        // Missing required fields or wrong type triggers validation error
        String invalidJson = "{ \"name\": null }"; 
        
        mockMvc.perform(post("/profiles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void should_return_profile_when_found() throws Exception {
        UUID id = UUID.randomUUID();
        ProfileResult result = new ProfileResult(id, "John Doe", "john@example.com", "Developer");

        when(getProfileUseCase.execute(any(GetProfileQuery.class))).thenReturn(result);

        mockMvc.perform(get("/profiles/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.name").value("John Doe"))
                .andExpect(jsonPath("$.email").value("john@example.com"))
                .andExpect(jsonPath("$.bio").value("Developer"));
                
        verify(getProfileUseCase).execute(new GetProfileQuery(id));
    }

    @Test
    void should_return_404_when_profile_not_found() throws Exception {
        UUID id = UUID.randomUUID();

        doThrow(new ProfileNotFoundException(id)).when(getProfileUseCase).execute(any(GetProfileQuery.class));

        mockMvc.perform(get("/profiles/{id}", id))
                .andExpect(status().isNotFound());
    }

    @Test
    void should_update_profile_and_return_200() throws Exception {
        UUID id = UUID.randomUUID();
        String name = "New Name";
        String email = "new@example.com";
        String bio = "Updated Bio";

        doNothing().when(updateProfileUseCase).execute(any(UpdateProfileCommand.class));

        // The request body must include the ID to match UpdateProfileRequest structure if it expects it, 
        // but typically PUT /{id} uses path var for identity. However, our DTO has 'id' field.
        // To be safe and consistent with previous test logic which passed verification:
        String requestBody = """
            {
              "name": "%s",
              "email": "%s",
              "bio": "%s"
            }
            """.formatted(name, email, bio);

        mockMvc.perform(put("/profiles/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isOk());
        
        verify(updateProfileUseCase).execute(new UpdateProfileCommand(id, name, email, bio));
    }

    @Test
    void should_reject_duplicate_email_with_409_on_create() throws Exception {
        String email = "duplicate@test.com";
        String requestBody = """
            {
              "name": "John Doe",
              "email": "%s",
              "bio": "Bio"
            }
            """.formatted(email);

        doThrow(new DuplicateEmailException()).when(createProfileUseCase).execute(any(CreateProfileCommand.class));

        mockMvc.perform(post("/profiles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isConflict());
    }

    @Test
    void should_delete_profile_and_return_204() throws Exception {
        UUID id = UUID.randomUUID();

        doNothing().when(deleteProfileUseCase).execute(any(DeleteProfileCommand.class));

        mockMvc.perform(delete("/profiles/{id}", id))
                .andExpect(status().isNoContent());
                
        verify(deleteProfileUseCase).execute(new DeleteProfileCommand(id));
    }

    @Test
    void should_return_404_when_deleting_not_found() throws Exception {
        UUID id = UUID.randomUUID();

        doThrow(new ProfileNotFoundException(id)).when(deleteProfileUseCase).execute(any(DeleteProfileCommand.class));

        mockMvc.perform(delete("/profiles/{id}", id))
                .andExpect(status().isNotFound());
    }
}
