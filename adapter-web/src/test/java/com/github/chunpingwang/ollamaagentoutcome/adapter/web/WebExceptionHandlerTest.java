package com.github.chunpingwang.ollamaagentoutcome.adapter.web;




import com.github.chunpingwang.ollamaagentoutcome.application.port.in.DeleteProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.GetProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.UpdateProfileDisplayNameUseCase;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import tools.jackson.databind.ObjectMapper;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.CreateProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.domain.DuplicateEmailException;
import com.github.chunpingwang.ollamaagentoutcome.domain.InvalidEmailException;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileNotFoundException;

@WebMvcTest(controllers = ProfileController.class)
class WebExceptionHandlerTest {
    @MockitoBean
    private GetProfileUseCase getProfileUseCase;

    @MockitoBean
    private UpdateProfileDisplayNameUseCase updateProfileDisplayNameUseCase;

    @MockitoBean
    private DeleteProfileUseCase deleteProfileUseCase;


    @MockitoBean
    private CreateProfileUseCase createProfileUseCase;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void should_handle_profile_not_found_with_404() throws Exception {
        UUID id = UUID.randomUUID();
        
        when(createProfileUseCase.execute(anyString(), anyString())).thenThrow(
            new ProfileNotFoundException(id));

        String requestBody = """
            {"email": "user@example.com", "displayName": "User"}
            """;

        mockMvc.perform(post("/api/profiles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.error").value("Profile not found"));
    }

    @Test
    void should_handle_duplicate_email_with_409() throws Exception {
        when(createProfileUseCase.execute(anyString(), anyString())).thenThrow(
            new DuplicateEmailException("Duplicate email address"));

        String requestBody = """
            {"email": "dup@example.com", "displayName": "Dup User"}
            """;

        mockMvc.perform(post("/api/profiles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.error").value("Duplicate email address"));
    }

    @Test
    void should_handle_invalid_email_format_with_400() throws Exception {
        when(createProfileUseCase.execute(anyString(), anyString())).thenThrow(
            new InvalidEmailException("Invalid format"));

        String requestBody = """
            {"email": "invalid-email", "displayName": "Bad Format"}
            """;

        mockMvc.perform(post("/api/profiles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error").exists());
    }

    @Test
    void should_handle_method_argument_not_valid_exception_with_400() throws Exception {
        var bindingResult = mock(org.springframework.validation.BindingResult.class);
        FieldError fieldError = new FieldError("CreateProfileRequest", "email", "must not be blank");
        
        when(bindingResult.getFieldErrors()).thenReturn(java.util.List.of(fieldError));

        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(null, bindingResult);

        String requestBody = """
            {"displayName": "User"}
            """; // Missing email

        mockMvc.perform(post("/api/profiles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.email").value("must not be blank"));
    }
}
