# Tasks 001 — Personal Profile CRUD

## T1 Domain Model & Exceptions
- 說明：建立領域層的核心 Value Objects、Entity 以及 Domain Exceptions。包含 `ProfileId`, `Email`, `Name` 三個 Value Object，`Profile` Entity 及其業務邏輯方法 (`updateName`, `updateEmail`)，以及 `InvalidProfileException`, `DuplicateEmailException`, `ProfileNotFoundException` 例外類別。所有類別位於 `domain` 套件下。
- 測試：`src/test/java/com/github/chunpingwang/ollamaagentoutcome/domain/ProfileTest.java` (驗證建構、更新邏輯)、`src/test/java/com/github/chunpingwang/ollamaagentoutcome/domain/EmailTest.java` (驗證格式)、`src/test/java/com/github/chunpingwang/ollamaagentoutcome/domain/NameTest.java` (驗證非空)。

## T2 Application Ports & Use Case Interfaces
- 說明：定義 Application Layer 的 Inbound Ports (Use Cases) 與 Outbound Ports (Repository interfaces)，並定義相關的 Command 和 Result records。包含 `CreateProfileUseCase`, `LoadProfileUseCase`, `UpdateProfileUseCase`, `DeleteProfileUseCase` 介面；`SaveProfilePort`, `FindProfileByIdPort`, `RemoveProfilePort` 介面；以及 `CreateProfileCommand`, `ProfileResult`, `UpdateProfileCommand` records。
- 測試：無獨立執行測試（僅介面與資料結構），但需確保編譯通過且符合命名規範（Inbound 以 UseCase 結尾，Outbound 以 Port 結尾）。

## T3 Use Case Implementations
- 說明：實作四個 Use Case Service，依賴 Outbound Ports。包含 `CreateProfileService` (呼叫 Save), `LoadProfileService` (呼叫 Find, 找不到拋出 NotFoundException), `UpdateProfileService` (先 Find 再更新欄位後 Save), `DeleteProfileService` (先 Find 確認存在，再呼叫 Remove)。所有類別位於 `application.usecase` 套件下。
- 測試：`src/test/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/CreateProfileServiceTest.java`、`.../LoadProfileServiceTest.java`、`.../UpdateProfileServiceTest.java`、`.../DeleteProfileServiceTest.java`。使用 Mockito Mock Outbound Ports，驗證業務流程與例外傳遞。

## T4 Persistence Adapter & Configuration
- 說明：建立 JPA Entity (`ProfileEntity`) 並實作 `Persistable<UUID>`；定義 Spring Data Repository (`ProfileRepository`)；實作三個 Outbound Ports 的 JPA 版本 (`JpaSaveProfilePort`, `JpaFindProfileByIdPort`, `JpaRemoveProfilePort`)；在 Bootstrap 模組的 `UseCaseConfiguration` 中註冊這三個 Port Bean 以及四個 Use Case Service Bean。注意：此任務僅涉及 adapter-persistence 的程式碼與 bootstrap 組態，不啟動完整 Web Context。
- 測試：`src/test/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/JpaSaveProfilePortTest.java` (@DataJpaTest + Testcontainers, 驗證 saveAndFlush 與唯一約束)、`.../JpaFindProfileByIdPortTest.java` (驗證查詢與轉換)、`.../JpaRemoveProfilePortTest.java` (驗證刪除)。確保這些測試能獨立透過資料庫連線執行。

## T5 Web Controller & Error Handling
- 說明：建立 REST API 端點與錯誤處理。包含 DTOs (`CreateProfileRequest`, `UpdateProfileRequest`, `ProfileResponse`)、`ProfileController` (處理 CRUD HTTP 請求) 與 `GlobalExceptionHandler` (將 Domain Exceptions 映射為對應 HTTP Status Code: 400, 404, 409)。所有類別位於 `adapter.web` 套件下。
- 測試：`src/test/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/ProfileControllerTest.java` (@WebMvcTest + @MockitoBean for Services, MockMvcTester 驗證狀態碼、JSON 結構、Location Header 及例外映射)。

## T6 BDD Acceptance Tests
- 說明：撰寫 Cucumber Feature 檔與 Step Definitions，進行端到端的整合測試。Feature 檔使用繁體中文 Gherkin，Step Definitions 使用 TestRestTemplate 呼叫實際的 REST API（需啟動完整 Spring Context 與 Testcontainers DB）。
- 測試：`bootstrap/src/test/resources/features/personal-profile-crud.feature` 與 `bootstrap/src/test/java/com/github/chunpingwang/ollamaagentoutcome/bdd/PersonalProfileSteps.java`。透過 Cucumber JUnit Platform Engine 執行，驗證整個系統從 Web 層到 Persistence 層的正確性。
