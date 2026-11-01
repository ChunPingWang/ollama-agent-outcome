# Tasks 001 — Personal Profile CRUD

## T1 Domain Model & Exceptions
- 說明：建立領域層核心模型與例外類別。包含 Value Objects (`ProfileId`, `Email`, `DisplayName`)、Domain Entity (`Profile`) 以及相關的業務規則驗證邏輯與自訂例外 (`DuplicateEmailException`, `InvalidEmailFormatException`, `ProfileNotFoundException`)。所有類別位於 `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/`。
- 測試：`domain/src/test/java/com/github/chunpingwang/ollamaagentoutcome/domain/ProfileTest.java` (驗證實體狀態轉換)、`domain/src/test/java/com/github/chunpingwang/ollamaagentoutcome/domain/ValueObjectValidationTest.java` (驗證 Email/ID 格式限制)。

## T2 Application Ports & Use Cases Implementation
- 說明：定義應用層的 Inbound Port (`CreateProfileUseCase`, `GetProfileUseCase`, `UpdateProfileUseCase`, `DeleteProfileUseCase`) 與 Outbound Port (`SaveProfilePort`, `LoadProfilePort`, `DeleteProfilePort`)，並實作對應的 Service (`CreateProfileService`, `GetProfileService`, `UpdateProfileService`, `DeleteProfileService`)。Service 負責協調 Port 呼叫與業務流程。檔案位於 `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/in/out/usecase/`。
- 測試：`application/src/test/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/CreateProfileServiceTest.java`、`.../usecase/GetProfileServiceTest.java`、`.../usecase/UpdateProfileServiceTest.java`、`.../usecase/DeleteProfileServiceTest.java`。使用 Mockito 模擬 Outbound Port，驗證服務邏輯正確性（如找不到 ID 拋出例外、重複 Email 處理等）。

## T3 Persistence Adapter (JPA Entity, Repository & Port Impl)
- 說明：建立 JPA Entity (`ProfileEntity`) 實現 `Persistable<String>`，Spring Data Repository (`ProfileRepository`)，以及 Converter (`ProfileConverter`)。實作 Outbound Ports 的 JPA 版本 (`JpaSaveProfilePort`, `JpaLoadProfilePort`, `JpaDeleteProfilePort`)，處理資料庫操作與 Domain Exception 轉換。檔案位於 `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/`。
- 測試：`adapter-persistence/src/test/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/JpaPortIntegrationTest.java`。使用 `@DataJpaTest` + Testcontainers PostgreSQL，驗證 Entity 映射、唯一約束觸發的 `DuplicateEmailException`、以及 Port 方法的實際資料庫行為。

## T4 Bootstrap Configuration Assembly
- 說明：在 `bootstrap/src/main/java/com/github/chunpingwang/ollamaagentoutcome/bootstrap/config/UseCaseConfiguration.java` 中註冊所有 Application Layer Service Bean。確保 Spring Context 能正確組裝 Domain -> Application -> Persistence 依賴鏈。**不新增新的 Context 啟動測試**（骨架已提供）。
- 測試：無新測試類別。依賴骨架提供的 `bootstrap/src/test/java/.../ApplicationContextTest.java` 來驗證整個應用上下文能否成功啟動且無 Bean 衝突或找不到定義的問題。

## T5 Web Adapter (Controller, DTOs & Error Handling)
- 說明：建立 REST Controller (`ProfileController`) 對應四個 HTTP 端點，定義 Request/Response DTOs (`CreateProfileRequest`, `UpdateProfileRequest`, `GetProfileResponse`)，並實作 `GlobalExceptionHandler` 將 Domain Exception 與 Validation Exception 轉換為正確的 HTTP 狀態碼 (201, 200, 204, 400, 404, 409)。檔案位於 `adapter-web/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/`。
- 測試：`adapter-web/src/test/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/ProfileControllerTest.java`。使用 `@WebMvcTest` + MockMvc，模擬 Port 行為，驗證各端點的 HTTP 狀態碼、回應 JSON 結構及錯誤處理邏輯。

## T6 Cucumber BDD Acceptance Tests
- 說明：撰寫 Cucumber Feature 檔 (`personal-profile-crud.feature`) 與 Step Definitions (`PersonalProfileSteps.java`)，以繁體中文描述使用者情境，並透過 `TestRestTemplate` 呼叫實際的 API 端點進行驗收。確保覆蓋 CRUD 所有正常路徑與異常情況（重複 Email、找不到 ID）。
- 測試：執行 Cucumber Runner。驗證整個系統從 HTTP 請求到資料庫存取的端到端流程符合 Spec 需求。
