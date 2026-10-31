# Tasks 001 — 個人資料管理 (Personal Profile CRUD)

## T1 Domain Model & Exceptions
- 說明：建立領域層的核心值物件、聚合根與例外類別。包含 `ProfileId`、`Email`、`Name` 的不可變實作與驗證邏輯；`Profile` 聚合根；以及 `InvalidEmailException`、`InvalidNameException`、`DuplicateEmailException`、`ProfileNotFoundException`。所有驗證規則需在建構子或 factory method 中強制執行。
- 測試：`domain/src/test/java/com/github/chunpingwang/ollamaagentoutcome/domain/ValueObjectTest.java`（驗證 Email/Name/ID 的正負案例）、`domain/src/test/java/com/github/chunpingwang/ollamaagentoutcome/domain/ProfileTest.java`（驗證 Profile 的建立與不變條件）。

## T2 Application Ports & Use Cases Implementation
- 說明：定義應用層的 Inbound Port (`CreateProfileUseCase`, `GetProfileUseCase`, `UpdateProfileUseCase`, `DeleteProfileUseCase`) 與 Outbound Port (`SaveProfilePort`, `LoadProfilePort`, `DeleteProfilePort`)，並實作對應的 Service (`CreateProfileService`, `GetProfileService`, `UpdateProfileService`, `DeleteProfileService`)。Service 負責協調 Port 呼叫與業務流程，不包含具體持久化細節。
- 測試：`application/src/test/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/CreateProfileServiceTest.java`、`.../usecase/GetProfileServiceTest.java`、`.../usecase/UpdateProfileServiceTest.java`、`.../usecase/DeleteProfileServiceTest.java`。使用 Mockito 模擬 Outbound Port，驗證服務邏輯正確性與例外處理。

## T3 Persistence Adapter (Entity, Repository, Port Impl)
- 說明：建立 JPA Entity (`ProfileEntity`)、Spring Data Repository (`ProfileRepository`) 以及 Outbound Port 的實作類別 (`JpaSaveProfilePort`, `JpaLoadProfilePort`, `JpaDeleteProfilePort`)。注意 `ProfileEntity` 需實現 `Persistable<String>` 以支援 Domain 產生的 UUID，並在 SavePort 中捕捉 `DataIntegrityViolationException` 轉換為 `DuplicateEmailException`。
- 測試：`adapter-persistence/src/test/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/JpaPortIntegrationTest.java`（結合 Testcontainers PostgreSQL，測試 Port 實作的 CRUD 行為、唯一性約束衝突處理）。

## T4 Bootstrap Configuration & Context Assembly
- 說明：在 `bootstrap` 模組的 `UseCaseConfiguration` 中，以 `@Bean` 方法宣告並組裝所有 Application Service Bean。確保 Spring Context 能正確解析依賴關係。**不新增**新的 Context 啟動測試，僅修改配置類。
- 測試：無新檔案。依賴骨架提供的 `bootstrap/src/test/java/com/github/chunpingwang/ollamaagentoutcome/bootstrap/ApplicationContextTest.java` 驗證 Context 是否成功啟動且 Bean 可注入。若此階段導致 Context 啟動失敗，則視為任務未通過。

## T5 Web Adapter (Controller, DTOs, Exception Handler)
- 說明：建立 REST Controller (`ProfileController`)、Request/Response DTOs (`CreateProfileRequestDTO`, `UpdateProfileRequestDTO`, `ProfileResponseDTO`) 以及全域異常處理器 (`WebExceptionHandler`)。Controller 呼叫 Inbound Port，Handler 將 Domain Exception 映射為對應 HTTP Status Code (201, 200, 204, 400, 404, 409)。
- 測試：`adapter-web/src/test/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/ProfileControllerMockMvcTest.java`（使用 `@WebMvcTest` + MockMvc，模擬 Use Case 行為，驗證 HTTP 狀態碼、回應內容與錯誤處理）。

## T6 Cucumber BDD Acceptance Tests
- 說明：撰寫 Cucumber Feature 檔 (`personal-profile-crud.feature`) 與步驟定義 (`StepDefinitions`)。Feature 以繁體中文描述 CRUD 情境。步驟定義透過 `TestRestTemplate` 發送真實 HTTP 請求至 Spring Boot 應用，並斷言回應結果。確保涵蓋成功路徑與各種錯誤場景（重複 Email、找不到 ID 等）。
- 測試：`bootstrap/src/test/resources/features/personal-profile-crud.feature` 與 `bootstrap/src/test/java/com/github/chunpingwang/ollamaagentoutcome/bdd/PersonalProfileSteps.java`。執行 Cucumber Runner 驗證所有 Scenario 通過。
