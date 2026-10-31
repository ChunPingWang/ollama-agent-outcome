package com.github.chunpingwang.ollamaagentoutcome.adapter.web;

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

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.github.chunpingwang.ollamaagentoutcome.application.port.in.CreateProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.DeleteProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.GetProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.UpdateProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.domain.DuplicateEmailException;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileNotFoundException;

@WebMvcTest(ProfileController.class)
@AutoConfigureMockMvc
@Import(ProfileControllerTest.TestConfig.class)
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

    @TestConfiguration
    static class TestConfig {
        @Bean
        ProfileController profileController(CreateProfileUseCase createProfileUseCase,
                                            GetProfileUseCase getProfileUseCase,
                                            UpdateProfileUseCase updateProfileUseCase,
                                            DeleteProfileUseCase deleteProfileUseCase) {
            return new ProfileController(createProfileUseCase, getProfileUseCase, updateProfileUseCase, deleteProfileUseCase);
        }
    }

    @Test
    @DisplayName("應能成功建立個人資料並回傳 201")
    void should_create_profile_with_201() throws Exception {
        String email = "alice@example.com";
        String name = "Alice";
        String bio = "Developer";

        mockMvc.perform(post("/profiles")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "name": "%s",
                        "email": "%s",
                        "bio": "%s"
                    }
                    """.formatted(name, email, bio)))
            .andExpect(status().isCreated())
            .andExpect(header().exists("Location"));

        verify(createProfileUseCase).create(org.mockito.ArgumentMatchers.any());
    }

    @Test
    @DisplayName("建立個人資料時電子郵件重複應回傳 409")
    void should_return_409_on_duplicate_email() throws Exception {
        String email = "bob@example.com";
        String name = "Bob";
        String bio = "Test";

        doThrow(new DuplicateEmailException(email)).when(createProfileUseCase)
            .create(org.mockito.ArgumentMatchers.any());

        mockMvc.perform(post("/profiles")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "name": "%s",
                        "email": "%s",
                        "bio": "%s"
                    }
                    """.formatted(name, email, bio)))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.error").value("電子郵件已存在: " + email));
    }

    @Test
    @DisplayName("建立個人資料時輸入驗證失敗應回傳 400")
    void should_return_400_on_validation_error() throws Exception {
        mockMvc.perform(post("/profiles")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "name": "",
                        "email": "invalid-email",
                        "bio": "Test"
                    }
                    """))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error").exists());
    }

    @Test
    @DisplayName("應能成功查詢個人資料並回傳 200")
    void should_get_profile_with_200() throws Exception {
        String id = UUID.randomUUID().toString();
        String email = "charlie@example.com";
        String name = "Charlie";
        String bio = "Writer";

        GetProfileUseCase.ProfileResponse response = new GetProfileUseCase.ProfileResponse(id, email, name, bio);
        when(getProfileUseCase.get(id)).thenReturn(response);

        mockMvc.perform(get("/profiles/{id}", id))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(id))
            .andExpect(jsonPath("$.email").value(email))
            .andExpect(jsonPath("$.name").value(name))
            .andExpect(jsonPath("$.bio").value(bio));
    }

    @Test
    @DisplayName("查詢不存在的個人資料應回傳 404")
    void should_return_404_when_profile_not_found() throws Exception {
        String id = "non-existent-id";

        when(getProfileUseCase.get(id)).thenThrow(new ProfileNotFoundException(id));

        mockMvc.perform(get("/profiles/{id}", id))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.error").value("找不到個人資料: " + id));
    }

    @Test
    @DisplayName("應能成功更新個人資料並回傳 200")
    void should_update_profile_with_200() throws Exception {
        String id = UUID.randomUUID().toString();
        String email = "old@test.com";
        String newName = "New Name";
        String newBio = "Updated Bio";

        GetProfileUseCase.ProfileResponse response = new GetProfileUseCase.ProfileResponse(id, email, newName, newBio);
        when(updateProfileUseCase.update(org.mockito.ArgumentMatchers.eq(id), org.mockito.ArgumentMatchers.any()))
            .thenReturn(response);

        mockMvc.perform(put("/profiles/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "name": "%s",
                        "email": "%s",
                        "bio": "%s"
                    }
                    """.formatted(newName, email, newBio)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value(newName))
            .andExpect(jsonPath("$.bio").value(newBio));
    }

    @Test
    @DisplayName("更新不存在的個人資料應回傳 404")
    void should_return_404_when_update_not_found() throws Exception {
        String id = "non-existent-id";
        String email = "test@test.com";
        String name = "Test";
        String bio = "Test";

        when(updateProfileUseCase.update(org.mockito.ArgumentMatchers.eq(id), org.mockito.ArgumentMatchers.any()))
            .thenThrow(new ProfileNotFoundException(id));

        mockMvc.perform(put("/profiles/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "name": "%s",
                        "email": "%s",
                        "bio": "%s"
                    }
                    """.formatted(name, email, bio)))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.error").value("找不到個人資料: " + id));
    }

    @Test
    @DisplayName("應能成功刪除存在的個人資料並回傳 204")
    void should_delete_profile_with_204() throws Exception {
        String id = UUID.randomUUID().toString();

        mockMvc.perform(delete("/profiles/{id}", id))
            .andExpect(status().isNoContent());

        verify(deleteProfileUseCase).delete(id);
    }

    @Test
    @DisplayName("刪除不存在的個人資料應回傳 404")
    void should_return_404_when_delete_not_found() throws Exception {
        String id = "non-existent-id";

        doThrow(new ProfileNotFoundException(id)).when(deleteProfileUseCase).delete(id);

        mockMvc.perform(delete("/profiles/{id}", id))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.error").value("找不到個人資料: " + id));
    }
}
