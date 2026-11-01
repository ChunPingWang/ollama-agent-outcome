package com.github.chunpingwang.ollamaagentoutcome.bdd.steps;




import com.github.chunpingwang.ollamaagentoutcome.adapter.persistence.ProfileEntity;
import static org.assertj.core.api.Assertions.*;

import io.cucumber.java.zh_tw.假設;
import io.cucumber.java.zh_tw.當;
import io.cucumber.java.zh_tw.那麼;
import io.cucumber.java.zh_tw.並且;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import com.github.chunpingwang.ollamaagentoutcome.adapter.persistence.ProfileJpaRepository;

/**
 * Step definitions for Personal Profile CRUD BDD tests.
 */
public class PersonalProfileSteps {

    @Autowired private TestRestTemplate restTemplate;
    @Autowired private ProfileJpaRepository repository;
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    private ResponseEntity<String> lastResponse;
    private String lastTargetUrl;

    /**
     * Helper to send HTTP requests and parse JSON responses.
     */
    private void sendRequest(HttpMethod method, String path, String jsonBody) {
        this.lastTargetUrl = path;
        
        HttpEntity<String> entity = null;
        if (jsonBody != null && !jsonBody.isBlank()) {
            var headers = new org.springframework.http.HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            entity = new HttpEntity<>(jsonBody, headers);
        }
        
        lastResponse = restTemplate.exchange(path, method, entity, String.class);
    }

    // --- Create Scenarios ---

    @當("我發送 POST 請求至 {string}，內容包含:")
    public void postWithContent(String url, String content) {
        sendRequest(HttpMethod.POST, url, content);
    }

    @那麼("回應的狀態碼應為 {int}")
    public void thenStatusCodeIs(int expectedStatus) {
        assertThat(lastResponse).isNotNull();
        assertThat(lastResponse.getStatusCode().value()).isEqualTo(expectedStatus);
    }

    // --- Get Scenarios ---

    @當("我發送 GET 請求至 {string}")
    public void getProfile(String url) {
        this.lastTargetUrl = url;
        sendRequest(HttpMethod.GET, url, null);
    }

    @並且("回應主體中的 email 應為 {string}")
    public void andEmailMatches(String expectedEmail) {
        assertJsonFieldEquals("email", expectedEmail);
    }

    @並且("回應主體中的 displayName 應為 {string}")
    public void andDisplayNameMatches(String expectedName) {
        assertJsonFieldEquals("displayName", expectedName);
    }

    private void assertJsonFieldEquals(String field, String expectedValue) {
        assertThat(lastResponse).isNotNull();
        if (lastResponse.getBody() == null || lastResponse.getBody().isBlank()) {
             fail("Expected JSON body but was empty or null");
        }
        try {
            JsonNode node = objectMapper.readTree(lastResponse.getBody());
            assertThat(node.path(field).asText()).isEqualTo(expectedValue);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse JSON for assertion: " + field, e);
        }
    }

    // --- Update Scenarios ---

    @當("我發送 PUT 請求至 {string}，內容包含:")
    public void putWithContent(String url, String content) {
        sendRequest(HttpMethod.PUT, url, content);
    }

    @並且("再次查詢該資源時，displayName 應變更為 {string}")
    public void thenUpdatedDisplayNameIs(String expectedName) {
        String id = extractIdFromPath(this.lastTargetUrl != null ? this.lastTargetUrl : "/api/profiles/default-id");
        getProfile("/api/profiles/" + id);
        assertJsonFieldEquals("displayName", expectedName);
    }

    private String extractIdFromPath(String path) {
        return path.substring(path.lastIndexOf('/') + 1);
    }

    // --- Delete Scenarios ---

    @當("我發送 DELETE 請求至 {string}")
    public void deleteProfile(String url) {
        this.lastTargetUrl = url;
        sendRequest(HttpMethod.DELETE, url, null);
    }

    // --- Setup / Pre-conditions (Direct DB manipulation to match Gherkin fixed IDs reliably) ---
    
    /**
     * Creates a profile with the exact ID specified in Gherkin. 
     */
    @假設("系統中存在 ID 為 {string} 的個人資料")
    public void given_profile_exists_with_id(String idStr) {
        ensureProfileExists(idStr, "setup-" + System.nanoTime() + "@test.com", "Setup User");
    }

    @假設("系統中存在 ID 為 {string} 的個人資料，且其電子郵件為 {string}，顯示名稱為 {string}")
    public void given_profile_exists_with_id_and_email_and_name(String idStr, String email, String name) {
        ensureProfileExists(idStr, email, name);
    }

    @假設("系統中存在 ID 為 {string} 的個人資料，且目前顯示名稱為 {string}")
    public void given_profile_exists_with_id_and_displayname(String idStr, String displayName) {
        ensureProfileExists(idStr, "setup-dn-" + System.nanoTime() + "@test.com", displayName);
    }

    @假設("已執行過刪除操作於 ID 為 {string} 的資料")
    public void given_delete_has_been_executed_on_id(String idStr) {
        repository.deleteById(UUID.fromString(idStr));
        repository.flush();
    }

    private void ensureProfileExists(String idStr, String email, String name) {
        try {
            com.github.chunpingwang.ollamaagentoutcome.adapter.persistence.ProfileEntity entity = new com.github.chunpingwang.ollamaagentoutcome.adapter.persistence.ProfileEntity();
            entity.setId(UUID.fromString(idStr));
            entity.setEmail(email);
            entity.setDisplayName(name);
            
            // isNew defaults to true for boolean primitives in Java, so no need to explicitly set it here 
            // unless we want to be explicit about the state before persist.
            // However, since this is a fresh object, it's effectively 'new'.
            
            repository.saveAndFlush(entity);
        } catch (Exception e) {
            throw new RuntimeException("Failed to setup profile with ID " + idStr, e);
        }
    }

    private void fail(String message) {
        throw new AssertionError(message);
    }
}
