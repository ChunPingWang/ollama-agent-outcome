# Tasks 001 — Personal Profile CRUD

## T1 Domain Model & Exceptions
- 說明：建立領域層核心模型與例外處理。包含 Value Objects (`ProfileId`, `Email`, `DisplayName`)、Domain Entity (`Profile`) 以及相關的 Domain Exceptions (`DuplicateEmailException`, `InvalidEmailFormatException`, `ProfileNotFoundException`)。所有類別位於 `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/`。
- 測試：`domain/src/test/java/com/github/chunpingwang/ollamaagentoutcome/domain/`
    - `EmailTest.java`: 驗證 Email VO 的建構邏輯（有效格式、空白檢查）。
    - `DisplayNameTest.java`: 驗證 DisplayName VO 的空值與空白處理。
    - `ProfileIdTest.java`: 驗證 UUID 格式的正確性。
    - `ProfileTest.java`: 驗證實體的建立、更新行為及狀態管理。

## T2 Application Ports (Inbound & Outbound)
- 說明：定義應用層的介面契約。建立 Inbound Ports (`CreateProfileUseCase`, `GetProfileUseCase`, `UpdateProfileUseCase`, `DeleteProfileUseCase`) 與 Outbound Ports (`SaveProfilePort`, `LoadProfilePort`, `DeleteProfilePort`)。這些介面僅定義方法簽章，不含業務邏輯。位置在 `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/in/` 與 `port/out/`。
- 測試：無獨立執行測試（純介面），但需確保編譯通過並作為後續 Use Case 實作的基礎。

## T3 Application Services Implementation
- 說明：實作 Use Case Service 類別，串接 Domain 與 Outbound Port。包含 `CreateProfileService`, `GetProfileService`, `UpdateProfileService`, `DeleteProfileService`。負責呼叫 Port 進行資料存取，並處理 Domain Exception 的傳播。位置在 `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/`。
- 測試：`application/src/test/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/`
    - `CreateProfileServiceTest.java`: 驗證成功建立、重複 Email 拋出例外。
    - `GetProfileServiceTest.java`: 驗證找到與找不到 (404) 的情境。
    - `UpdateProfileServiceTest.java`: 驗證更新流程、不存在 ID 拋出例外。
    - `DeleteProfileServiceTest.java`: 驗證刪除流程、不存在 ID 拋出例外。
    - *註解*：使用 Mockito 模擬 Outbound Ports，不啟動 Spring Context。

## T4 Persistence Adapter & Configuration
- 說明：建立 JPA Entity (`ProfileEntity`)、Spring Data Repository (`ProfileRepository`)、Port 實作類別 (`JpaSaveProfilePort`, `JpaLoadProfilePort`, `JpaDeleteProfilePort`) 以及 Converter (`ProfileConverter`)。同時修改 Bootstrap 層的 `UseCaseConfiguration`，將上述 Service Bean 註冊到 Spring Container。注意：此任務包含資料庫相關的 Port 實作測試。位置在 `adapter-persistence/src/main/java/...` 與 `bootstrap/src/main/java/...`。
- 測試：
    - `adapter-persistence/src/test/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/JpaSaveProfilePortTest.java`: 驗證資料持久化、唯一性約束觸發的例外轉換。
    - `adapter-persistence/src/test/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/JpaLoadProfilePortTest.java`: 驗證 findById 行為。
    - `adapter-persistence/src/test/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/JpaDeleteProfilePortTest.java`: 驗證 deleteById 行為。
    - *註解*：使用 `@DataJpaTest` + Testcontainers PostgreSQL。

## T5 Web Adapter (Controller & Exception Handler)
- 說明：建立 REST API 端點。包含 DTOs (`CreateProfileRequest`, `UpdateProfileRequest`, `GetProfileResponse`)、Controller (`ProfileController`) 以及全域異常處理器 (`GlobalExceptionHandler`)。負責 HTTP 請求解析、回應格式化及狀態碼映射。位置在 `adapter-web/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/`。
- 測試：`adapter-web/src/test/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/`
    - `ProfileControllerTest.java`: 使用 `@WebMvcTest`，模擬 Port 呼叫，驗證各 CRUD 動作的 HTTP Status Code (201, 200, 204, 400, 404, 409) 與 JSON 結構。
    - `GlobalExceptionHandlerTest.java`: 驗證特定 Domain Exception 轉換為正確的 HTTP 錯誤訊息與狀態碼。

## T6 BDD Acceptance Tests
- 說明：撰寫 Cucumber Feature 檔與 Step Definitions，進行端到端的整合測試。Feature 檔描述使用者故事，Step Definition 透過 `TestRestTemplate` 呼叫實際的 Web API（需啟動完整 Spring Context）。位置在 `bootstrap/src/test/resources/features/` 與 `bdd/`。
- 測試：
    - `bootstrap/src/test/resources/features/personal-profile-crud.feature`: 定義 Create, Read, Update, Delete 的情境。
    - `bootstrap/src/test/java/com/github/chunpingwang/ollamaagentoutcome/bdd/PersonalProfileSteps.java`: 實作 Gherkin 步驟，執行 HTTP 請求並斷言結果。
