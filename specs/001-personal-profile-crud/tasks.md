# Tasks 001 — 個人資料管理 (CRUD)

## T1 領域模型與例外定義
- 說明：建立 `ProfileId`、`Profile` 值物件/實體，以及 `ProfileNotFoundException`、`DuplicateEmailException` 領域例外。`Profile` 需包含建構式驗證邏輯（Name 非空、Email 格式檢查）。
- 測試：`domain/src/test/java/com/github/chunpingwang/ollamaagentoutcome/domain/ProfileTest.java`
  - 驗證 `ProfileId` 的 UUID 生成與相等性。
  - 驗證 `Profile` 建構式在 Name 為空、Email 格式錯誤時拋出 `IllegalArgumentException`。
  - 驗證 `Profile` 的欄位存取與不可變性。

## T2 Application Ports 定義
- 說明：在 `application/port/in` 定義四個 Use Case 介面 (`CreateProfileUseCase`, `GetProfileUseCase`, `UpdateProfileUseCase`, `DeleteProfileUseCase`)；在 `application/port/out` 定義三個 Port 介面 (`SaveProfilePort`, `LoadProfilePort`, `DeleteProfilePort`)。
- 測試：無獨立測試類別（僅介面定義，由後續 Use Case 實作與整合測試覆蓋）。

## T3 Use Case 實作與 Application 層測試
- 說明：實作四個 Use Case Service (`CreateProfileService`, `GetProfileService`, `UpdateProfileService`, `DeleteProfileService`)。Service 需依賴對應的 Outbound Ports，並處理領域例外轉換。
- 測試：
  - `application/src/test/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/CreateProfileServiceTest.java`：驗證成功建立回傳 ID、重複 Email 拋出例外。
  - `application/src/test/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/GetProfileServiceTest.java`：驗證成功回傳 Profile、找不到拋出例外。
  - `application/src/test/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/UpdateProfileServiceTest.java`：驗證成功更新、找不到拋出例外、重複 Email 拋出例外。
  - `application/src/test/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/DeleteProfileServiceTest.java`：驗證成功刪除、找不到拋出例外。

## T4 Persistence Adapter 實作
- 說明：建立 `ProfileEntity` (實作 `Persistable<UUID>`)、`ProfileJpaRepository`，以及三個 Port 實作類別 (`JpaSaveProfilePort`, `JpaLoadProfilePort`, `JpaDeleteProfilePort`)。Port 實作需處理 Domain Entity 與 JPA Entity 的轉換，並捕捉 `DataIntegrityViolationException` 轉為 `DuplicateEmailException`。
- 測試：`adapter-persistence/src/test/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/JpaProfilePortTest.java`
  - 使用 `@DataJpaTest` + Testcontainers。
  - 驗證 `JpaSaveProfilePort` 儲存成功、重複 Email 拋出 `DuplicateEmailException`。
  - 驗證 `JpaLoadProfilePort` 查詢成功與找不到。
  - 驗證 `JpaDeleteProfilePort` 刪除成功。

## T5 Bootstrap 組裝與 Context 驗證
- 說明：建立 `UseCaseConfiguration`，以 `@Bean` 定義所有 Use Case Service 與 Port 實作類別。確保 Spring Context 可正常啟動並注入所有 Bean。
- 測試：`bootstrap/src/test/java/com/github/chunpingwang/ollamaagentoutcome/bootstrap/UseCaseConfigurationTest.java`
  - 使用 `@SpringBootTest` + `@Import(TestcontainersConfiguration.class)`。
  - 驗證所有 Use Case 與 Port Bean 可被成功解析與注入。

## T6 Web Adapter 實作
- 說明：建立 `ProfileController`、DTOs (`CreateProfileRequest`, `UpdateProfileRequest`, `ProfileResponse`) 與 `ProfileExceptionHandler`。Controller 呼叫 Use Case 並處理 HTTP 狀態碼與例外轉換。
- 測試：`adapter-web/src/test/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/ProfileControllerTest.java`
  - 使用 `@WebMvcTest` + `@MockitoBean`。
  - 驗證 POST /profiles 回 201 與 Location header。
  - 驗證 GET /profiles/{id} 回 200 或 404。
  - 驗證 PUT /profiles/{id} 回 200、404 或 409。
  - 驗證 DELETE /profiles/{id} 回 204 或 404。
  - 驗證輸入驗證失敗回 400。

## T7 Cucumber BDD 驗收測試
- 說明：建立 `profile-crud.feature` 與 `ProfileCrudSteps`。Steps 使用 `TestRestTemplate` 呼叫實際 API，驗證完整流程。
- 測試：
  - `bootstrap/src/test/resources/features/profile-crud.feature`：定義 Create, Read, Update, Delete 的 Gherkin 場景。
  - `bootstrap/src/test/java/com/github/chunpingwang/ollamaagentoutcome/bdd/ProfileCrudTest.java`：Cucumber Runner。
  - `bootstrap/src/test/java/com/github/chunpingwang/ollamaagentoutcome/bdd/ProfileCrudSteps.java`：步驟定義，驗證 HTTP 狀態碼與回應內容。
