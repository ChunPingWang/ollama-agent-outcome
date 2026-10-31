# Tasks 001 — Personal Profile CRUD

## T1 Domain Model & Exceptions
- 說明：建立領域層的核心 Value Objects、Entity 以及 Domain Exception。包含 `ProfileId` (UUID wrapper)、`Email` (含 RFC5322 基本格式驗證)、`DisplayName` (空白處理)，以及主 Entity `Profile` (含靜態工廠方法 `create` 與業務邏輯 `update`)。同時定義 `DuplicateEmailException`、`InvalidEmailFormatException`、`ProfileNotFoundException`。所有類別位於 `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/`。
- 測試：`domain/src/test/java/com/github/chunpingwang/ollamaagentoutcome/domain/ProfileTest.java` (驗證實體狀態變更與不變條件)、`domain/src/test/java/com/github/chunpingwang/ollamaagentoutcome/domain/ValueObjectValidationTest.java` (驗證 Email/DisplayName 的無效輸入拋出例外)。

## T2 Application Ports & Use Cases Implementation
- 說明：定義 Application Layer 的 Inbound Port (`CreateProfileUseCase`, `GetProfileUseCase`, `UpdateProfileUseCase`, `DeleteProfileUseCase`) 與 Outbound Port (`SaveProfilePort`, `LoadProfilePort`, `DeleteProfilePort`)，並實作對應的 Service (`CreateProfileService`, `GetProfileService`, `UpdateProfileService`, `DeleteProfileService`)。Service 負責協調 Port 呼叫與 Domain Exception 轉換。路徑為 `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/in/out/usecase/`。
- 測試：`application/src/test/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/CreateProfileServiceTest.java`、`.../usecase/GetProfileServiceTest.java`、`.../usecase/UpdateProfileServiceTest.java`、`.../usecase/DeleteProfileServiceTest.java`。使用 Mockito 模擬 Outbound Port，驗證業務邏輯流程（如：找不到時拋出例外、重複時拋出例外、成功時調用 save/delete）。

## T3 Persistence Adapter (Entity, Repository, Port Impl)
- 說明：建立 JPA Entity `ProfileEntity` (實作 `Persistable<String>`)、Spring Data Repository `ProfileRepository`，以及 Converter `ProfileConverter`。實作 Outbound Ports 的具體實現：`JpaSaveProfilePort` (處理 `saveAndFlush` 與唯一性約束異常)、`JpaLoadProfilePort`、`JpaDeleteProfilePort`。這些類別位於 `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/`。注意：Port 實現類需標註 Spring Bean 註解或依賴組件掃描發現。
- 測試：`adapter-persistence/src/test/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/JpaPersistenceIntegrationTest.java`。使用 `@DataJpaTest` + Testcontainers PostgreSQL，驗證 Entity 映射、Unique Constraint 觸發的 `DuplicateEmailException`、以及各 Port 實現類的 CRUD 行為正確性。

## T4 Bootstrap Configuration & Context Assembly
- 說明：在 `bootstrap/src/main/java/com/github/chunpingwang/ollamaagentoutcome/bootstrap/config/UseCaseConfiguration.java` 中，以 `@Bean` 方法註冊所有 Application Layer 的 Service (`CreateProfileService`, etc.)。確保 Spring Boot 能自動偵測並注入 Adapter 層的 Port 實現（假設它們已標記為 Component）。此任務僅修改配置，不新增新類別（骨架已有 Application.java）。
- 測試：利用骨架提供的 `bootstrap/src/test/java/com/github/chunpingwang/ollamaagentoutcome/bootstrap/ApplicationContextTest.java` 驗證上下文啟動成功。若無現有測試，則確認該測試通過即代表 Bean 組裝無誤。（根據規則，不再新增新的 Context 測試類）。

## T5 Web Adapter (Controller, DTOs, Exception Handler)
- 說明：建立 REST API 端點與錯誤處理。包含 DTO: `CreateProfileRequest`, `UpdateProfileRequest`, `GetProfileResponse`；Controller: `ProfileController` (映射 HTTP 動作為 Use Case Ports)；Error Handler: `GlobalExceptionHandler` (將 Domain Exception 轉為對應 HTTP Status Code)。路徑為 `adapter-web/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/`。
- 測試：`adapter-web/src/test/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/ProfileControllerTest.java`。使用 `@WebMvcTest` + MockMvc，模擬各 Use Case Port 的行為，驗證 HTTP 狀態碼 (201, 200, 204, 400, 404, 409)、JSON 序列化結果以及 Validation Error 的回應格式。

## T6 BDD Acceptance Tests (Cucumber)
- 說明：撰寫 Cucumber Feature 檔與 Step Definitions，進行端到端的整合測試。Feature 檔位於 `bootstrap/src/test/resources/features/personal-profile-crud.feature` (zh-TW)，Step Definitions 位於 `bootstrap/src/test/java/com/github/chunpingwang/ollamaagentoutcome/bdd/PersonalProfileSteps.java`。透過 `TestRestTemplate` 呼叫實際 API，驗證完整流程（建立、查詢、更新、刪除）及異常情境。
- 測試：執行 Cucumber Runner (`bootstrap/src/test/java/com/github/chunpingwang/ollamaagentoutcome/bootstrap/CucumberTest.java`)，確保所有 Scenario 通過。
