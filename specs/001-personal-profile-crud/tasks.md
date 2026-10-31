# Tasks 001 — 個人資料管理 (Personal Profile CRUD)

## T1 建立領域模型與例外
- 說明：定義 Value Objects (`ProfileId`, `Email`, `DisplayName`, `Bio`)、Entity (`Profile`) 及 Domain Exceptions (`DuplicateEmailException`, `ProfileNotFoundException`, `InvalidEmailFormatException`, `BioTooLongException`)。確保 VO 的不可變性與基本不變條件檢查。
- 測試：`domain/src/test/java/com/github/chunpingwang/ollamaagentoutcome/domain/model/ProfileTest.java`（驗證 Entity 狀態與行為）、`domain/src/test/java/com/github/chunpingwang/ollamaagentoutcome/domain/model/ValueObjectsTest.java`（驗證 Email, Bio 等 VO 的建構規則與例外）。

## T2 定義 Application Ports 並實作 Create & Get Use Cases
- 說明：在 application module 中定義 Inbound Port (`CreateProfileUseCase`, `GetProfileUseCase`) 與 Outbound Port (`SaveProfilePort`, `LoadProfilePort`)，並實作對應 Service (`CreateProfileService`, `GetProfileService`)。此階段僅處理邏輯流程，不依賴真實資料庫。
- 測試：`application/src/test/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/CreateProfileServiceTest.java`（模擬 SavePort 成功與重複 email 例外）、`application/src/test/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/GetProfileServiceTest.java`（模擬 LoadPort 存在與不存在情境）。

## T3 實作 Update & Delete Use Cases
- 說明：定義剩餘 Inbound Port (`UpdateProfileUseCase`, `DeleteProfileUseCase`) 並實作 Service (`UpdateProfileService`, `DeleteProfileService`)。更新邏輯需包含載入實體、驗證狀態、呼叫 Domain 方法更新欄位後儲存；刪除邏輯為載入後刪除。
- 測試：`application/src/test/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/UpdateProfileServiceTest.java`（驗證更新流程與 404 例外）、`application/src/test/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/DeleteProfileServiceTest.java`（驗證刪除流程與 404 例外）。

## T4 建立 Persistence Adapter (JPA Entity, Repository, Ports)
- 說明：在 adapter-persistence module 中建立 JPA Entity (`ProfileEntity`)，實現 `Persistable<UUID>` 以處理 Assigned ID 策略。建立 Spring Data Repository (`ProfileJpaRepository`)。實作 Outbound Port (`JpaSaveProfilePort`, `JpaLoadProfilePort`)，負責 Domain 與 Entity 的轉換及異常映射（如將 `DataIntegrityViolationException` 轉為 `DuplicateEmailException`）。
- 測試：`adapter-persistence/src/test/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/port/JpaPersistencePortsIT.java`（使用 Testcontainers PostgreSQL 驗證 Entity 映射、唯一性約束觸發、以及 Port 層面的 CRUD 行為與例外轉換）。

## T5 Bootstrap 組裝與 Context 啟動驗證
- 說明：在 bootstrap module 中建立 `UseCaseConfiguration`，透過 `@Bean` 方法將 Application Layer 的 Service 與 Persistence Layer 的 Port 實作進行綁定。確保整個應用程式上下文可以正常啟動，且所有 Bean 依賴注入正確。
- 測試：`bootstrap/src/test/java/com/github/chunpingwang/ollamaagentoutcome/bootstrap/BootstrapContextTest.java`（使用 `@SpringBootTest` + `@Import(TestcontainersConfiguration.class)` 驗證應用程序可啟動，並檢查關鍵 Bean 是否存在於 ApplicationContext 中）。

## T6 建立 Web Adapter (Controller, DTOs, Exception Handler)
- 說明：在 adapter-web module 中建立 REST Controller (`ProfileRestController`)、Request/Response DTOs (`CreateProfileRequest`, `GetProfileResponse`, `UpdateProfileRequest`) 及全局異常處理器 (`GlobalExceptionHandler`)。實現 HTTP 狀態碼對應（201, 204, 400, 404, 409）與 Location Header 設定。
- 測試：`adapter-web/src/test/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/controller/ProfileRestControllerIT.java`（使用 `@WebMvcTest` + Mocked Ports 驗證各端點的請求解析、回應格式、HTTP 狀態碼及錯誤訊息內容）。

## T7 BDD Cucumber 驗收測試
- 說明：撰寫繁體中文 Gherkin Feature File (`personal-profile-crud.feature`) 涵蓋所有 Spec 中的 Acceptance Criteria。實作 Bootstrap 套件下的 Step Definitions，透過 `TestRestTemplate` 呼叫實際運行的 API（整合了真實資料庫的 Testcontainers），驗證端到端的業務行為。
- 測試：`bootstrap/src/test/resources/features/personal-profile-crud.feature` 搭配 `bootstrap/src/test/java/com/github/chunpingwang/ollamaagentoutcome/bdd/PersonalProfileSteps.java`。執行 Cucumber JUnit Platform Engine 以確保所有 Scenario 通過。
