# Tasks 001 — Personal Profile CRUD

## T1 Domain Model & Exceptions
- 說明：建立領域層核心模型與例外類別。包含 Value Objects (`ProfileId`, `Email`, `DisplayName`)、Domain Entity (`Profile`) 及其靜態工廠方法，以及業務規則相關的例外 (`DuplicateEmailException`, `ProfileNotFoundException`)。所有類別位於 `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/`。
- 測試：`domain/src/test/java/com/github/chunpingwang/ollamaagentoutcome/domain/ProfileTest.java` (測試實體建立、更新邏輯)、`domain/src/test/java/com/github/chunpingwang/ollamaagentoutcome/domain/ValueObjectValidationTest.java` (測試 VO 的不變條件)。

## T2 Application Ports Definition
- 說明：定義應用層的 Inbound Port (Use Case Interfaces) 與 Outbound Port (Repository Interfaces)。Inbound ports 以 `UseCase` 結尾，Outbound ports 以 `Port` 結尾。Command/Query DTOs 併入此任務或隨 Use Case 實作一起定義於 application 模組中（建議放在 port.in 同包或獨立 record）。路徑：`application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/in/` 與 `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/out/`。
- 測試：無程式碼測試，僅確保介面簽名正確，為後續 Service 實作做準備。

## T3 Application Services Implementation
- 說明：實作所有 Use Case Services (`CreateProfileService`, `GetProfileService`, `UpdateProfileService`, `DeleteProfileService`)。這些類別位於 `application/usecase/` 套件，依賴 Outbound Ports 進行業務邏輯處理（如檢查重複 Email、轉換 Domain Entity 至 Response DTO）。不包含 Spring 註解。
- 測試：`application/src/test/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/CreateProfileServiceTest.java`、`.../GetProfileServiceTest.java`、`.../UpdateProfileServiceTest.java`、`.../DeleteProfileServiceTest.java` (使用 Mockito Mock Outbound Ports)。

## T4 Persistence Adapter & Port Implementations
- 說明：建立 JPA Entity (`ProfileEntity`)、Spring Data Repository (`ProfileRepository`) 以及 Outbound Ports 的具體實現 (`JpaSaveProfilePort`, `JpaLoadProfileByIdPort`, `JpaDeleteProfilePort`)。負責 Domain Model 與 JPA Entity 之間的轉換，並處理資料庫約束異常轉譯。路徑：`adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/`。
- 測試：`adapter-persistence/src/test/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/JpaSaveProfilePortTest.java`、`.../JpaLoadProfileByIdPortTest.java`、`.../JpaDeleteProfilePortTest.java` (使用 Testcontainers PostgreSQL + @DataJpaTest)。

## T5 Bootstrap Configuration Assembly
- 說明：在 `bootstrap.UseCaseConfiguration` 中註冊所有 Application Services Bean 與 Persistence Port Implementation Beans。確保 Spring Context 能正確組裝依賴鏈（Adapter -> Application）。**僅修改現有的 UseCaseConfiguration，不新增新的 @Configuration 類別或 Context 測試。**
- 測試：利用骨架提供的 `bootstrap.ApplicationContextTest` 驗證 Context 啟動成功；可額外撰寫簡單的 Integration Test 確認 Bean 注入無誤（若骨架未覆蓋此層面，則依賴後續 Web Adapter 的整合測試間接驗證）。

## T6 Web Adapter & Exception Handling
- 說明：建立 REST Controller (`ProfileController`)、Request/Response DTOs (`CreateProfileRequest`, etc.) 以及全域異常處理器 (`WebExceptionHandler`)。處理 HTTP 請求映射、輸入驗證 (@Valid) 並將 Domain Exceptions 轉換為對應的 HTTP Status Code (400, 404, 409, 201, 204)。路徑：`adapter-web/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/`。
- 測試：`adapter-web/src/test/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/ProfileControllerTest.java` (使用 @WebMvcTest + Mock Inbound Ports)、`.../WebExceptionHandlerTest.java`。

## T7 Cucumber BDD Acceptance Tests
- 說明：撰寫 Cucumber Feature 檔與 Step Definitions，透過真實的 HTTP 呼叫驗收 CRUD 功能。Feature 檔位於 `bootstrap/src/test/resources/features/`，Step Definitions 位於 `bootstrap/src/test/java/com/github/chunpingwang/ollamaagentoutcome/bdd/personalprofile/PersonalProfileSteps.java`。使用 TestRestTemplate 進行端到端測試。
- 測試：執行 Cucumber Runner，確保所有 Given/When/Then 步驟通過，涵蓋正常流程與錯誤場景（重複 Email、找不到 ID 等）。
