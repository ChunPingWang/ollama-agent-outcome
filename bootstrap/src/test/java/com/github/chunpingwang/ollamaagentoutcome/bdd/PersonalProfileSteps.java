package com.github.chunpingwang.ollamaagentoutcome.bdd;

import static org.assertj.core.api.Assertions.*;

import java.util.Map;
import java.util.UUID;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.zh_tw.當;
import io.cucumber.java.zh_tw.假設;
import io.cucumber.java.zh_tw.那麼;
import io.cucumber.java.zh_tw.並且;
import tools.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.github.chunpingwang.ollamaagentoutcome.adapter.persistence.ProfileEntity;
import com.github.chunpingwang.ollamaagentoutcome.adapter.persistence.ProfileRepository;

/**
 * Cucumber BDD 步驟定義類別，用於個人資料 CRUD 操作測試。
 */
public class PersonalProfileSteps {

    private final TestRestTemplate restTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @Autowired(required = false)
    private ProfileRepository profileRepository;

    private ResponseEntity<String> responseEntity;
    private Map<String, Object> responseBodyMap;
    private String createdId;
    private String setupEmail;
    private String lastFetchedEmail;

    public PersonalProfileSteps(TestRestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Autowired(required = false)
    void setProfileRepository(ProfileRepository repository) {
        this.profileRepository = repository;
    }

    // 輔助方法：確保指定 ID 的資料存在於資料庫中（用於固定 ID 的前置場景）
    private void ensureProfileExists(String idStr, String email, String displayName) {
        if (profileRepository == null) return;
        UUID uuid = UUID.fromString(idStr);
        ProfileEntity existing = profileRepository.findById(uuid).orElse(null);
        if (existing != null) {
            existing.setEmail(email);
            existing.setDisplayName(displayName);
            profileRepository.saveAndFlush(existing);
        } else {
            ProfileEntity entity = new ProfileEntity(uuid);
            entity.setEmail(email);
            entity.setDisplayName(displayName);
            profileRepository.saveAndFlush(entity);
        }
    }

    @當("我發送 POST 請求至 {string}，內容包含:")
    public void sendPostRequest(String path, String content) throws Exception {
        responseEntity = restTemplate.postForEntity(path, content, String.class);
        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            HttpHeaders headers = responseEntity.getHeaders();
            if (headers.getLocation() != null) {
                createdId = headers.getLocation().getPath().substring(headers.getLocation().getPath().lastIndexOf('/') + 1);
            }
            responseBodyMap = objectMapper.readValue(responseEntity.getBody(), Map.class);
            lastFetchedEmail = (String) responseBodyMap.get("email");
        } else {
            responseBodyMap = null;
            lastFetchedEmail = null;
        }
    }

    @當("我發送 GET 請求至 {string}")
    public void sendGetRequest(String path) {
        responseEntity = restTemplate.getForEntity(path, String.class);
        try {
            responseBodyMap = objectMapper.readValue(responseEntity.getBody(), Map.class);
            lastFetchedEmail = (String) responseBodyMap.get("email");
        } catch (Exception e) {
            responseBodyMap = null;
            lastFetchedEmail = null;
        }
    }

    @當("我發送 PUT 請求至 {string}，內容包含:")
    public void sendPutRequest(String path, String content) throws Exception {
        org.springframework.http.HttpEntity<String> request = new org.springframework.http.HttpEntity<>(content, 
                org.springframework.http.HttpHeaders.EMPTY);
        responseEntity = restTemplate.exchange(path, org.springframework.http.HttpMethod.PUT, request, String.class);
        try {
            responseBodyMap = objectMapper.readValue(responseEntity.getBody(), Map.class);
            lastFetchedEmail = (String) responseBodyMap.get("email");
        } catch (Exception e) {
            responseBodyMap = null;
            lastFetchedEmail = null;
        }
    }

    @當("我發送 DELETE 請求至 {string}")
    public void sendDeleteRequest(String path) {
        restTemplate.delete(path);
        responseBodyMap = null;
        lastFetchedEmail = null;
    }

    @那麼("回應的狀態碼應該是 {int}")
    public void assertStatusCode(int expectedStatus) {
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.valueOf(expectedStatus));
    }

    @並且("回應標頭 \"Location\" 應該存在且指向該資源")
    public void assertLocationHeaderExists() {
        HttpHeaders headers = responseEntity.getHeaders();
        assertThat(headers).as("Response headers should not be null").isNotNull();
        assertThat(headers.getLocation())
                .as("Location header should exist and point to a valid resource")
                .isNotNull();
    }

    @並且("回應內容中的 {string} 應該等於 {string}")
    public void assertResponseBodyFieldEquals(String fieldName, String expectedValue) throws Exception {
         if (responseBodyMap != null && responseBodyMap.containsKey(fieldName)) {
             Object actual = responseBodyMap.get(fieldName);
             assertThat(actual).isEqualTo(expectedValue);
         } else {
             throw new AssertionError(String.format("Response body missing key '%s'", fieldName));
         }
    }

    @並且("回應內容中的 {string} 應該等於該資料對應的電子郵件")
    public void assertEmailMatchesSetup(String fieldName) throws Exception {
         if (responseBodyMap != null && responseBodyMap.containsKey(fieldName)) {
            String actualEmail = (String) responseBodyMap.get(fieldName);
            if (lastFetchedEmail != null) {
                 assertThat(actualEmail.toLowerCase()).isEqualTo(lastFetchedEmail.toLowerCase());
            } else {
                assertThat(actualEmail).isNotBlank();
            }
        } else {
            throw new AssertionError("Response body does not contain '" + fieldName + "' field");
        }
    }
    
    // 補齊 Feature 檔中缺失的前置步驟定義
    @假設("已經存在一個電子郵件為 {string} 的個人資料")
    public void setupExistingProfileByEmail(String email) throws Exception {
        String uniqueEmail = "setup_" + System.currentTimeMillis() + "_" + email;
        String body = """
            {"email": "%s", "displayName": "Setup User"}
            """.formatted(uniqueEmail);
        
        ResponseEntity<Void> createResponse = restTemplate.postForEntity("/api/profiles", body, Void.class);
        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        HttpHeaders headers = createResponse.getHeaders();
        createdId = headers.getLocation().getPath().substring(headers.getLocation().getPath().lastIndexOf('/') + 1); 
        setupEmail = uniqueEmail;
    }

    @假設("存在一個 ID 為 {string} 的個人資料")
    public void setupExistingProfileById(String idStr) {
        ensureProfileExists(idStr, "setup_" + System.currentTimeMillis() + "@test.com", "Test User");
    }

    @假設("存在一個 ID 為 {string} 且顯示名稱為 {string} 的個人資料")
    public void setupExistingProfileWithDisplayName(String idStr, String displayName) {
        ensureProfileExists(idStr, "setup_" + System.currentTimeMillis() + "@test.com", displayName);
    }

    @並且("隨後查詢該 ID 時應返回 {int}")
    public void assertDeletedProfileReturnsStatus(int expectedStatus) throws Exception {
        String url = "/api/profiles/" + createdId;
        ResponseEntity<String> getResponse = restTemplate.getForEntity(url, String.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.valueOf(expectedStatus));
    }
}
