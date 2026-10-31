package com.github.chunpingwang.ollamaagentoutcome.bdd;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import io.cucumber.java.zh_tw.假設;
import io.cucumber.java.zh_tw.當;
import io.cucumber.java.zh_tw.那麼;
import io.cucumber.java.zh_tw.並且;

/**
 * 個人資料 CRUD 的 Cucumber 步驟定義。
 * 注意：此類別不標註 @CucumberContextConfiguration，該註解由 CucumberSpringConfiguration 提供。
 */
@Component
public class ProfileSteps {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private ResponseEntity<String> response;
    private String createdProfileId;

    @假設("電子郵件 \"([^\"]*)\" 在系統中尚不存在")
    public void emailDoesNotExist(String email) {
        jdbcTemplate.update("DELETE FROM profiles WHERE email = ?", email);
    }

    @假設("電子郵件 \"([^\"]*)\" 已存在於系統中")
    public void emailAlreadyExists(String email) {
        String id = "existing-" + email.hashCode();
        jdbcTemplate.update(
            "INSERT INTO profiles (id, email, name, bio) VALUES (?, ?, ?, ?)",
            id, email, "Existing User", "Existing Bio"
        );
    }

    @假設("系統中已存在 ID 為 \"([^\"]*)\" 的個人資料，其電子郵件為 \"([^\"]*)\"")
    public void profileExists(String id, String email) {
        jdbcTemplate.update(
            "INSERT INTO profiles (id, email, name, bio) VALUES (?, ?, ?, ?)",
            id, email, "Test User", "Test Bio"
        );
    }

    @假設("系統中已存在 ID 為 \"([^\"]*)\" 的個人資料，其姓名為 \"([^\"]*)\"")
    public void profileExistsWithName(String id, String name) {
        jdbcTemplate.update(
            "INSERT INTO profiles (id, email, name, bio) VALUES (?, ?, ?, ?)",
            id, "update@test.com", name, "Old Bio"
        );
    }

    @假設("系統中已存在 ID 為 \"([^\"]*)\" 的個人資料")
    public void profileExistsById(String id) {
        jdbcTemplate.update(
            "INSERT INTO profiles (id, email, name, bio) VALUES (?, ?, ?, ?)",
            id, "delete@test.com", "Delete User", "Delete Bio"
        );
    }

    @當("我發送 POST 請求至 \"/profiles\"，內容包含:")
    public void createProfile(Map<String, String> data) {
        String name = data.get("name");
        String email = data.get("email");
        String bio = data.getOrDefault("bio", "");

        String body = String.format(
            "{\"name\":\"%s\",\"email\":\"%s\",\"bio\":\"%s\"}",
            name, email, bio
        );

        response = restTemplate.postForEntity("/profiles", body, String.class);
    }

    @當("我發送 GET 請求至 \"/profiles/([^\"]*)\"")
    public void getProfile(String id) {
        response = restTemplate.getForEntity("/profiles/{id}", String.class, id);
    }

    @當("我發送 PUT 請求至 \"/profiles/([^\"]*)\"，內容包含:")
    public void updateProfile(String id, Map<String, String> data) {
        String name = data.get("name");
        String email = data.get("email");
        String bio = data.getOrDefault("bio", "");

        String body = String.format(
            "{\"name\":\"%s\",\"email\":\"%s\",\"bio\":\"%s\"}",
            name, email, bio
        );

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        
        response = restTemplate.exchange(
            "/profiles/{id}", 
            HttpMethod.PUT, 
            new HttpEntity<>(body, headers), 
            String.class, 
            id
        );
    }

    @當("我發送 DELETE 請求至 \"/profiles/([^\"]*)\"")
    public void deleteProfile(String id) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        
        response = restTemplate.exchange(
            "/profiles/{id}", 
            HttpMethod.DELETE, 
            new HttpEntity<>(headers), 
            String.class, 
            id
        );
    }

    @那麼("回應狀態碼應為 ([0-9]+) ([^\"]*)")
    public void verifyStatusCode(int expectedStatus, String expectedReason) {
        assertThat(response.getStatusCode().value()).isEqualTo(expectedStatus);
    }

    @那麼("回應標頭 \"Location\" 應包含新建立的個人資料 ID")
    public void verifyLocationHeader() {
        String location = response.getHeaders().getLocation().toString();
        assertThat(location).contains("/profiles/");
        createdProfileId = location.substring(location.lastIndexOf('/') + 1);
    }

    @那麼("回應主體應包含新建立的個人資料資訊")
    public void verifyResponseBodyContainsProfileInfo() {
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).contains("id");
        assertThat(response.getBody()).contains("email");
        assertThat(response.getBody()).contains("name");
    }

    @那麼("回應主體應指出電子郵件已存在")
    public void verifyDuplicateEmailError() {
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).contains("電子郵件已存在");
    }

    @那麼("回應主體應指出電子郵件格式錯誤")
    public void verifyInvalidEmailError() {
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).contains("電子郵件格式無效");
    }

    @那麼("回應主體應指出姓名不可為空")
    public void verifyEmptyNameError() {
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).contains("姓名不可為空");
    }

    @那麼("回應主體應包含該個人資料的正確資訊")
    public void verifyProfileInfo() {
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).contains("charlie@example.com");
        assertThat(response.getBody()).contains("uuid-123");
    }

    @那麼("回應主體應指出找不到該個人資料")
    public void verifyNotFound() {
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).contains("找不到個人資料");
    }

    @那麼("回應主體應包含更新後的個人資料資訊，其中姓名應為 \"([^\"]*)\"")
    public void verifyUpdatedName(String newName) {
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).contains(newName);
    }

    @那麼("後續查詢該 ID 應返回 404 Not Found")
    public void verifyDeletedProfileNotFound() {
        ResponseEntity<String> getResponse = restTemplate.getForEntity("/profiles/{id}", String.class, createdProfileId);
        assertThat(getResponse.getStatusCode().value()).isEqualTo(404);
    }
}
