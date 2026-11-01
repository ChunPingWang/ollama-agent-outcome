# Tasks 001 — Personal Profile CRUD

## T1 Domain Model & Exceptions
- 說明：建立領域層的核心值物件、實體與例外類別。包含 `ProfileId` (UUID 驗證)、`Email` (RFC 5322 基本格式驗證、忽略大小寫比較)、`DisplayName` (允許 null/空白)。定義 `Profile` 實體及其業務方法 `updateDisplayName`。定義 `InvalidEmailException`、`DuplicateEmailException`、`ProfileNotFoundException`。
- 測試：`domain/src/test/java/com/github/chunpingwang/ollamaagentoutcome/domain/ProfileIdTest.java` (驗證 UUID 格式與非空檢查)；`domain/src/test/java/com/github/chunpingwang/ollamaagentoutcome/domain/EmailTest.java` (驗證有效/無效 email、equals 忽略大小寫)；`domain/src/test/java/com/github/chunpingwang/ollamaagentoutcome/domain/DisplayNameTest.java` (驗證 null/空白處理)；`domain/src/test/java/com/github/chunpingwang/ollamaagentoutcome/domain/ProfileTest.java` (驗證更新顯示名稱的行為)。

## T2 Application Ports & Use Case Implementations
- 說明：建立應用層的 Inbound Port (`CreateProfileUseCase`, `GetProfileUseCase`, `UpdateProfileDisplayNameUseCase`, `DeleteProfileUseCase`) 與 Outbound Port (`SaveProfilePort`, `FindProfileByIdPort`, `DeleteProfileByIdPort`)。實作對應的 Service (`CreateProfileService`, `GetProfileService`, `UpdateProfileDisplayNameService`, `DeleteProfileService`)，包含業務邏輯與例外轉換（如捕捉重複錯誤）。
- 測試：`application/src/test/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/CreateProfileServiceTest.java` (模擬 SavePort 成功與拋出 DuplicateEmailException)；`application/src/test/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/GetProfileServiceTest.java` (模擬 FindPort 找到與未找到情況)；`application/src/test/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/UpdateProfileDisplayNameServiceTest.java` (模擬查詢、更新、儲存流程及NotFoundException)；`application/src/test/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/DeleteProfileServiceTest.java` (模擬檢查存在性並刪除)。

## T3 Persistence Adapter & Repository
- 說明：建立 JPA Entity `ProfileEntity` (實現 `Persistable<UUID>`)、Spring Data Repository `ProfileRepository` (含唯一約束檢查支援)、以及 Outbound Port 的 JPA 實作 (`JpaSaveProfilePort`, `JpaFindProfileByIdPort`, `JpaDeleteProfileByIdPort`)。處理 Domain 到 Entity 的轉換與異常映射。
- 測試：`adapter-persistence/src/test/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/JpaSaveProfilePortTest.java` (驗證資料存入、重複 Email 觸發 ConstraintViolation 轉為 DuplicateEmailException)；`adapter-persistence/src/test/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/JpaFindProfileByIdPortTest.java` (驗證依 ID 查詢與轉換)；`adapter-persistence/src/test/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/JpaDeleteProfileByIdPortTest.java` (驗證刪除邏輯，包含不存在時的行為)。

## T4 Bootstrap Configuration
- 說明：在 `bootstrap` 模組的 `UseCaseConfiguration` 中，以 `@Bean` 方法組裝所有 Application Layer 的 Use Case Bean（注入對應的 Port 實作）。確保 Spring Context 能正確啟動並解析依賴。不新增新的 Context 測試類別。
- 測試：利用骨架提供的 `bootstrap/src/test/java/com/github/chunpingwang/ollamaagentoutcome/bootstrap/ApplicationContextTest.java` 驗證整個應用程式上下文（含 JPA、Web、Use Cases）能否正常啟動且無 BeanDefinitionOverrideException。此任務主要確認配置檔案的正確性。

## T5 Web Adapter & Exception Handling
- 說明：建立 REST Controller `ProfileController` (`/api/profiles`)，處理 POST, GET, PUT, DELETE 請求。定義 DTO (`CreateProfileRequest`, `UpdateProfileRequest`, `ProfileResponse`) 並使用 Jakarta Validation。建立 `WebExceptionHandler` (`@RestControllerAdvice`) 將 Domain/Application 例外映射為正確的 HTTP 狀態碼 (201, 200, 204, 400, 404, 409)。
- 測試：`adapter-web/src/test/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/ProfileControllerTest.java` (使用 `@WebMvcTest` + `@MockitoBean` mock Use Case Ports，驗證各端點的 HTTP 狀態碼、JSON 結構與 Location Header)；`adapter-web/src/test/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/WebExceptionHandlerTest.java` (驗證各種例外對應的狀態碼)。

## T6 Cucumber BDD Acceptance Tests
- 說明：撰寫 Cucumber Feature 檔 (`personal-profile-crud.feature`) 描述個人資料 CRUD 的行為場景（包含成功路徑與錯誤路徑如重複 Email、找不到 ID）。實作步驟定義類別，使用 `TestRestTemplate` 呼叫實際 API 並斷言回應。確保在完整 Spring Context (含 Testcontainers PostgreSQL) 下運行通過。
- 測試：`bootstrap/src/test/resources/features/personal-profile-crud.feature` (Gherkin 語法)；`bootstrap/src/test/java/com/github/chunpingwang/ollamaagentoutcome/bdd/PersonalProfileSteps.java` (Cucumber Step Definitions)。執行 Cucumber Runner 驗證所有 Scenario 通過。
