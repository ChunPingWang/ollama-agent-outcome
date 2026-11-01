# Tasks 001 — 個人資料管理 (Personal Profile CRUD)

## T1 Domain Model & Value Objects
- 說明：建立領域層核心型別與例外。包含 `ProfileId`、`Email`、`DisplayName` 三個 Value Object，以及 `Profile` Entity 和相關 Domain Exception (`DuplicateEmailException`, `ProfileNotFoundException`)。所有 VO 需在建構子中執行不變條件檢查。
- 測試：
    - `domain/src/test/java/com/github/chunpingwang/ollamaagentoutcome/domain/EmailTest.java`：驗證有效 Email 建構成功，無效格式拋出異常。
    - `domain/src/test/java/com/github/chunpingwang/ollamaagentoutcome/domain/DisplayNameTest.java`：驗證 null/blank 處理邏輯。
    - `domain/src/test/java/com/github/chunpingwang/ollamaagentoutcome/domain/ProfileEntityTest.java`：驗證 `changeDisplayName` 行為及狀態變更。

## T2 Application Ports & Use Cases (Create & Get)
- 說明：定義 Inbound Port (`CreateProfileUseCase`, `GetProfileUseCase`) 與 Outbound Port (`SaveProfilePort`, `LoadProfilePort`)。實作對應的 Service (`CreateProfileService`, `GetProfileService`)。`CreateProfileService` 負責呼叫 Port 並處理重複 Email 的業務規則；`GetProfileService` 負責載入資料或拋出找不到例外。
- 測試：
    - `application/src/test/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/CreateProfileServiceTest.java`：Mock `SaveProfilePort`，測試成功建立與重複 Email 異常轉發。
    - `application/src/test/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/GetProfileServiceTest.java`：Mock `LoadProfilePort`，測試找到資料回傳結果，找不到拋出 `ProfileNotFoundException`。

## T3 Application Ports & Use Cases (Update & Delete)
- 說明：定義 Inbound Port (`UpdateProfileUseCase`, `DeleteProfileUseCase`) 與 Outbound Port (`RemoveProfilePort`)。實作對應的 Service (`UpdateProfileService`, `DeleteProfileService`)。`UpdateProfileService` 需驗證實體存在後更新顯示名稱；`DeleteProfileService` 執行刪除操作。
- 測試：
    - `application/src/test/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/UpdateProfileServiceTest.java`：Mock `LoadProfilePort` 和 `SaveProfilePort`，測試更新邏輯及不存在時的處理。
    - `application/src/test/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/DeleteProfileServiceTest.java`：Mock `RemoveProfilePort`，測試刪除成功及不存在時的例外處理。

## T4 Persistence Adapter Implementation
- 說明：建立 JPA Entity (`ProfileEntity`)、Spring Data Repository (`ProfileJpaRepository`) 以及三個 Port 的實作類別 (`JpaSaveProfilePortImpl`, `JpaLoadProfilePortImpl`, `JpaRemoveProfilePortImpl`)。Entity 需實現 `Persistable<String>` 以支援客戶端生成的 UUID。Port 實作負責 Domain 與 JPA Entity 之間的轉換，並捕捉 DB 異常轉為 Domain Exception。
- 測試：
    - `adapter-persistence/src/test/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/JpaProfileRepositoryTest.java`：使用 Testcontainers PostgreSQL，驗證 Entity 持久化、唯一性約束觸發 (DataIntegrityViolationException) 及 Port 實作的正確性。

## T5 Bootstrap Configuration Assembly
- 說明：在 `bootstrap.UseCaseConfiguration` 中註冊所有 Application Layer 的 Service Bean (`createProfileUseCase`, `getProfileUseCase`, `updateProfileUseCase`, `deleteProfileUseCase`)。確保 Spring Context 能正確組裝 Ports 與 Services。**不新增**新的 Context 啟動測試，依賴骨架提供的 `ApplicationContextTest`。
- 測試：無新檔案（依賴現有 `bootstrap/src/test/java/.../ApplicationContextTest.java` 驗證 Context 啟動成功）。

## T6 Web Adapter & Error Handling
- 說明：建立 REST Controller (`ProfileRestController`)、Request/Response DTOs (`CreateRequestDto`, `UpdateRequestDto`, `ProfileResponseDto`) 以及全域例外處理器 (`GlobalExceptionHandler`)。Controller 負責路由請求、DTO 轉換至 Domain Request Object，並呼叫 Use Case。Handler 將 Domain Exception 映射為對應 HTTP 狀態碼 (404, 409)。
- 測試：
    - `adapter-web/src/test/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/ProfileRestControllerTest.java`：使用 `@WebMvcTest` + Mock Use Cases，驗證 CRUD 各端點的 HTTP 狀態碼、Header、JSON 序列化/反序列化及 Validation Error 處理。

## T7 Cucumber BDD Acceptance Tests
- 說明：撰寫 Cucumber Feature 檔 (`personal-profile-crud.feature`) 與 Step Definitions (`ProfileSteps`)。Step Definitions 位於 bootstrap 模組的 `bdd` 套件，使用 `TestRestTemplate` 呼叫實際 API 進行端到端驗收。Feature 檔需涵蓋所有 Spec 描述的使用者情境（創建、查詢、更新、刪除、錯誤場景）。
- 測試：
    - `bootstrap/src/test/resources/features/personal-profile-crud.feature`：定義繁體中文 Gherkin 步驟。
    - `bootstrap/src/test/java/com/github/chunpingwang/ollamaagentoutcome/bdd/ProfileSteps.java`：實作步驟邏輯，執行完整流程測試。
