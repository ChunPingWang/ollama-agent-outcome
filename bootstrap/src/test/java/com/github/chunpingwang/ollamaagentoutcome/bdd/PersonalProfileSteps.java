package com.github.chunpingwang.ollamaagentoutcome.bdd;


import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import java.util.UUID;

import io.cucumber.datatable.DataTable;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import com.github.chunpingwang.ollamaagentoutcome.application.port.out.SaveProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.DisplayName;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;

import io.cucumber.java.zh_tw.當;
import io.cucumber.java.zh_tw.那麼;
import io.cucumber.java.zh_tw.假設;
import io.cucumber.java.zh_tw.並且;

/**
 * Cucumber BDD 步驟定義，負責透過 HTTP API 驗證個人資料 CRUD 功能。
 */
public class PersonalProfileSteps {

    private final TestRestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Autowired(required = false)
    private SaveProfilePort saveProfilePort;

    // 用於在場景間傳遞狀態的欄位
    private ResponseEntity<String> lastResponse;
    private String createdId;
    private JsonNode responseBody;
    private String lastWrittenEmail;

    public PersonalProfileSteps(TestRestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    @當("我發送 POST 請求至 {string}，內容包含:")
    public void 我發送_post_請求至(String path, String docString) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
        
        // 解析 JSON 以提取 email 供後續比對使用
        try {
            JsonNode node = objectMapper.readTree(docString);
            if (node.has("email")) {
                this.lastWrittenEmail = node.get("email").asText();
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse request body", e);
        }

        HttpEntity<String> entity = new HttpEntity<>(docString, headers);
        
        lastResponse = restTemplate.postForEntity(path, entity, String.class);
    }

    @當("我發送 GET 請求至 {string}")
    public void 我發送_get_請求至(String path) {
        lastResponse = restTemplate.getForEntity(path, String.class);
    }

    @當("我發送 PUT 請求至 {string}，內容包含:")
    public void 我發送_put_請求至(String path, String docString) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
        
        // 解析 JSON 以提取 email 供後續比對使用
        try {
            JsonNode node = objectMapper.readTree(docString);
            if (node.has("email")) {
                this.lastWrittenEmail = node.get("email").asText();
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse request body", e);
        }

        HttpEntity<String> entity = new HttpEntity<>(docString, headers);
        
        lastResponse = restTemplate.exchange(path, HttpMethod.PUT, entity, String.class);
    }

    @當("我發送 DELETE 請求至 {string}")
    public void 我發送_delete_請求至(String path) {
        // exchange 才能取得 ResponseEntity<Void> 的完整狀態碼資訊（雖然 delete 通常回 204）
        lastResponse = restTemplate.exchange(path, HttpMethod.DELETE, null, String.class);
    }

    @那麼("回應的狀態碼應該是 {int}")
    public void 回應的狀態碼應該是(int expectedStatus) {
        assertThat(lastResponse.getStatusCode().value()).isEqualTo(expectedStatus);
    }

    @那麼("回應內容中的 {string} 應該等於 {string}")
    public void 回應內容中的_應該等於(String field, String expectedValue) {
        parseResponseBody();
        JsonNode node = responseBody.get(field);
        if (node == null || node.isNull()) {
            throw new AssertionError("Field '" + field + "' is missing or null in response");
        }
        assertThat(node.asText()).isEqualTo(expectedValue);
    }

    @並且("回應內容中的 {string} 應該等於該資料對應的電子郵件")
    public void 並且回應內容中的_應該等於該資料對應的電子郵件(String field) {
        parseResponseBody();
        
        // 如果是指 email，則與最後一次寫入的 email 比對
        if ("email".equals(field)) {
             assertThat(responseBody.get(field).asText()).isEqualTo(this.lastWrittenEmail);
             return;
        }
        
        // 其他欄位（如 id）通常由特定步驟處理，若在此處呼叫且非 email，視為錯誤或忽略
        // 但根據 feature，這步主要用於驗證 email。若 field 是 "id"，應使用另一個通用步驟或此處的邏輯擴展。
        // 為簡化並符合當前 feature 需求：此步驟專門處理 email 驗證。
        // 若未來需要支援其他欄位的「動態預期值」，需進一步設計狀態追蹤。
        // 目前假設只有 email 會用到這個特殊語義。
        throw new UnsupportedOperationException("Only 'email' field is supported for this step currently.");
    }

    private void parseResponseBody() {
        try {
            String body = lastResponse.getBody();
            if (body == null && lastResponse.getStatusCode().is2xxSuccessful()) {
                // 對於 204 No Content 等無 Body 回應，parse 可能失敗，但此類場景不應調用檢查內容的步驟
                responseBody = objectMapper.createObjectNode(); 
                return;
            }
            this.responseBody = objectMapper.readTree(body);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse response body", e);
        }
    }

    @假設("已存在一個電子郵件為 {string} 的個人資料")
    public void 已存在一個電子郵件為_的個人資料(String email) {
        this.lastWrittenEmail = email;
        UUID id = UUID.randomUUID();
        
        Profile profile = Profile.create(
            new ProfileId(id),
            new Email(email),
            new DisplayName("Pre-existing User")
        );
        
        saveProfilePort.save(profile);
        createdId = id.toString();
    }
    
    @假設("已有一個 ID 為 {string} 的個人資料")
    public void 已有一個_id_為_的個人資料(String idStr) {
        createdId = idStr;
        UUID id = UUID.fromString(idStr);
        
        // 確保該 ID 在 DB 中存在。若不存在則建立（避免重複執行測試時衝突）
        try {
             String email = "preexisting_" + System.nanoTime() + "@example.com";
             
             Profile profile = Profile.create(
                 new ProfileId(id),
                 new Email(email),
                 new DisplayName("Existing User")
             );
             saveProfilePort.save(profile);
             this.lastWrittenEmail = email;
        } catch (Exception e) {
            // If it already exists, ignore. Or throw if strict.
            // For BDD, usually we assume setup is done. But to be safe against duplicate keys in shared DB:
            // We rely on the fact that previous scenarios might have used this specific ID? 
            // No, IDs are random per run unless specified. The feature specifies a static ID "550e...".
            // So we MUST ensure that specific ID exists.
            // If save throws DuplicateKeyException (unlikely as email is unique), we catch and proceed.
            if (!(e.getCause() instanceof org.springframework.dao.DataIntegrityViolationException)) {
                throw new RuntimeException(e);
            }
        }
    }

    @假設("已有一個 ID 為 {string} 且顯示名稱為 {string} 的個人資料")
    public void 已有一個_id_為_且顯示名稱為_的個人資料(String idStr, String displayName) {
        createdId = idStr;
        UUID id = UUID.fromString(idStr);
        
        String email = "update_" + System.nanoTime() + "@example.com";
        
        Profile profile = Profile.create(
            new ProfileId(id),
            new Email(email),
            new DisplayName(displayName)
        );
        
        try {
            saveProfilePort.save(profile);
        } catch (Exception e) {
             if (!(e.getCause() instanceof org.springframework.dao.DataIntegrityViolationException)) {
                 throw new RuntimeException(e);
             }
        }
        this.lastWrittenEmail = email;
    }

    @假設("已刪除一個 ID 為 {string} 的個人資料")
    public void 已刪除一個_id_為_的個人資料(String idStr) {
         createdId = idStr;
         // 此步驟在 Feature 中用於「刪除後再次查詢」場景的前置條件？
         // 不，Feature 寫的是：
         /*
             場景: 刪除後再次查詢該資料應返回不存在
                 假設已刪除一個 ID 為 ... 
                 當我發送 GET ...
          */
         // 這意味著我們需要先執行 DELETE。但 Cucumber 步驟是順序執行的。
         // 如果這個 Scenario 只有這兩步，那麼「假設已刪除」必須實際執行刪除動作。
         
         try {
             restTemplate.delete("/api/profiles/" + idStr);
         } catch (Exception e) {
             // 忽略錯誤，因為可能已經被刪除了或本來就不存在（取決於測試數據準備）
             // 但如果真的失敗了且不是 404/410，可能是其他問題。這裡先寬鬆處理以確保流程繼續。
             if (!e.getMessage().contains("404")) {
                  throw new RuntimeException(e);
             }
         }
    }
    
    /**
     * 輔助方法：從路徑中提取最後一段作為 ID（若需要）。
     */
    private String extractLastPathSegment(String path) {
        return path.substring(path.lastIndexOf('/') + 1);
    }
}
