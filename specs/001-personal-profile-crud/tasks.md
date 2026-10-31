# Tasks 001 — 個人資料管理 (CRUD)

## T1 領域模型與例外定義
- 說明：建立純 Java 領域物件，包含 Value Objects (`ProfileId`, `Email`, `Name`, `Age`)、Entity (`Profile`) 與 Domain Exceptions (`ProfileNotFoundException`, `DuplicateEmailException`, `InvalidEmailException`, `InvalidAgeException`)。確保 Value Objects 在建構時執行不變條件驗證。
- 測試：
  - `domain/src/test/java/com/github/chunpingwang/ollamaagentoutcome/domain/ProfileIdTest.java`：驗證 ID 格式與非空檢查。
  - `domain/src/test/java/com/github/chunpingwang/ollamaagentoutcome/domain/EmailTest.java`：驗證 RFC 5322 格式檢查與無效格式拋出 `InvalidEmailException`。
  - `domain/src/test/java/com/github/chunpingwang/ollamaagentoutcome/domain/NameTest.java`：驗證非空、非空白檢查。
  - `domain/src/test/java/com/github/chunpingwang/ollamaagentoutcome/domain/AgeTest.java`：驗證正整數檢查與無效年齡拋出 `InvalidAgeException`。
  - `domain/src/test/java/com/github/chunpingwang/ollamaagentoutcome/domain/ProfileTest.java`：驗證 `update` 方法建立新實例且原實例不變，以及建構時的不變條件。

## T2 Application Ports 與 Use Case 實作
- 說明：定義 Application 層的 Inbound Ports (`CreateProfileUseCase`, `GetProfileUseCase`, `UpdateProfileUseCase`, `DeleteProfileUseCase`) 與 Outbound Ports (`SaveProfilePort`, `LoadProfilePort`, `DeleteProfilePort`, `ExistsByEmailPort`)。實作對應的 Service (`CreateProfileService`, `GetProfileService`, `UpdateProfileService`, `DeleteProfileService`)，處理業務邏輯與例外轉換。
- 測試：
  - `application/src/test/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/CreateProfileServiceTest.java`：驗證成功建立流程、重複 Email 拋出例外、Port 呼叫順序。
  - `application/src/test/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/GetProfileServiceTest.java`：驗證找到資料回傳 Profile、找不到拋出 `ProfileNotFoundException`。
  - `application/src/test/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/UpdateProfileServiceTest.java`：驗證更新流程、找不到資料拋出例外、重複 Email 拋出例外。
  - `application/src/test/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/DeleteProfileServiceTest.java`：驗證刪除流程、找不到資料拋出例外。

## T3 Persistence Adapter (JPA Entity, Repository & Port Implementations)
- 說明：建立 JPA Entity (`ProfileEntity`) 實作 `Persistable`，Spring Data Repository (`ProfileJpaRepository`)，以及 Outbound Ports 的實作類別 (`JpaSaveProfilePort`, `JpaLoadProfilePort`, `JpaDeleteProfilePort`, `JpaExistsByEmailPort`) 與轉換器 (`ProfileConverter`)。處理 Domain 與 JPA 物件的轉換及資料庫異常轉換。
- 測試：
  - `adapter-persistence/src/test/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/ProfileEntityTest.java`：驗證 Entity 與 Domain 物件的轉換邏輯、`isNew()` 行為。
  - `adapter-persistence/src/test/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/ProfileJpaRepositoryTest.java`：使用 Testcontainers 驗證基本 CRUD 與 Email 唯一性約束 (`DataIntegrityViolationException`)。
  - `adapter-persistence/src/test/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/JpaSaveProfilePortTest.java`：驗證 `save` 方法正確轉換並儲存，以及重複 Email 時轉換為 `DuplicateEmailException`。
  - `adapter-persistence/src/test/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/JpaLoadProfilePortTest.java`：驗證 `findById` 正確載入並轉換。
  - `adapter-persistence/src/test/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/JpaDeleteProfilePortTest.java`：驗證 `deleteById` 正確刪除。
  - `adapter-persistence/src/test/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/JpaExistsByEmailPortTest.java`：驗證 `existsByEmail` 正確檢查。

## T4 Bootstrap 組裝與 Context 驗證
- 說明：建立 `ApplicationConfig` (`@Configuration`)，使用 `@Bean` 方法組裝所有 Use Case Services 與 Port 實作。確保 Spring Context 可成功啟動，並驗證 Bean 依賴注入正確。此步驟在 Web Adapter 之前，確保 Controller 能找到所需的 Use Case Bean。
- 測試：
  - `bootstrap/src/test/java/com/github/chunpingwang/ollamaagentoutcome/bootstrap/ApplicationConfigTest.java`：使用 `@SpringBootTest` 啟動完整 Context，驗證所有 Use Case 與 Port 實作 Bean 存在且可注入。

## T5 Web Adapter (Controller, DTOs & Error Handling)
- 說明：建立 REST Controller (`ProfileController`)、Request/Response DTOs (`CreateProfileRequest`, `UpdateProfileRequest`, `ProfileResponse`) 與全域異常處理器 (`ProfileControllerAdvice`)。實現 HTTP 方法對應、輸入驗證 (Jakarta Validation) 與例外轉換為 HTTP 狀態碼。
- 測試：
  - `adapter-web/src/test/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/ProfileControllerTest.java`：使用 `@WebMvcTest` 與 `@MockitoBean` 模擬 Use Cases。驗證各 HTTP 方法 (POST, GET, PUT, DELETE) 的成功狀態碼、回應內容、Location Header，以及各種例外對應的 HTTP 狀態碼 (400, 404, 409)。

## T6 Cucumber BDD 驗收測試
- 說明：建立 Cucumber Feature 檔 (`profile-crud.feature`) 與步驟定義 (`ProfileSteps`)。使用 `TestRestTemplate` 發送真實 HTTP 請求，驗證端到端功能。確保 Feature 檔使用繁體中文 Gherkin 語法。
- 測試：
  - `bootstrap/src/test/resources/features/profile-crud.feature`：定義建立、查詢、更新、刪除個人資料的場景，包含成功與失敗情境。
  - `bootstrap/src/test/java/com/github/chunpingwang/ollamaagentoutcome/bdd/ProfileSteps.java`：實作步驟定義，使用 `TestRestTemplate` 呼叫 API 並驗證回應。
