# Tasks 001 — Personal Profile CRUD

## T1 Domain Layer: Value Objects, Entity & Exceptions
- 說明：建立領域層核心型別。包含 `ProfileId`、`Email`、`Name` 三個 Value Object (record)，以及 `Profile` Domain Entity。定義業務規則驗證邏輯於 VO 建構子中。同時建立四個 Domain Exception (`InvalidNameException`, `InvalidEmailException`, `ProfileNotFoundException`, `DuplicateEmailException`)。所有類別位於 `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/`。
- 測試：`domain/src/test/java/com/github/chunpingwang/ollamaagentoutcome/domain/ValueObjectTest.java`（驗證 Email/Name 格式與非法輸入拋出例外）、`domain/src/test/java/com/github/chunpingwang/ollamaagentoutcome/domain/entity/ProfileEntityTest.java`（驗證 Profile 建立與更新行為）。

## T2 Application Layer: Ports and Use Cases for Create & Get
- 說明：建立應用層的 Inbound Port 介面 (`CreateProfileUseCase`, `GetProfileUseCase`) 與 Outbound Port 介面 (`SaveProfilePort`, `LoadProfilePort`)。實作對應的 Service (`CreateProfileService`, `GetProfileService`)，處理流程控制並呼叫 Port。Command/Result DTO 隨 Service 或 Use Case 介面一同定義在 application 模組內。路徑為 `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/in/out/` 及 `usecase/`。
- 測試：`application/src/test/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/CreateProfileServiceTest.java`（Mock SavePort，驗證成功保存與重複 Email 異常傳遞）、`application/src/test/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/GetProfileServiceTest.java`（Mock LoadPort，驗證找到資料回傳結果、找不到拋出 NotFoundException）。

## T3 Application Layer: Ports and Use Cases for Update & Delete
- 說明：建立剩餘的 Inbound Port 介面 (`UpdateProfileUseCase`, `DeleteProfileUseCase`) 與 Outbound Port 介面 (`RemoveProfilePort`)。實作對應的 Service (`UpdateProfileService`, `DeleteProfileService`)。Update 需先 Load 再 Save；Delete 需先確認存在性 (Load) 再 Remove。路徑同上。
- 測試：`application/src/test/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/UpdateProfileServiceTest.java`（Mock Load/SavePort，驗證更新邏輯與不存在時例外）、`application/src/test/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/DeleteProfileServiceTest.java`（Mock Load/RemovePort，驗證刪除邏輯與不存在時例外）。

## T4 Persistence Adapter: Entity, Repository & Port Implementations
- 說明：建立 JPA Entity (`ProfileEntity`) 實現 `Persistable<UUID>`，Repository (`ProfileRepository`)，以及三個 Port 實作類別 (`JpaSaveProfilePort`, `JpaLoadProfilePort`, `JpaRemoveProfilePort`)。處理 Domain Object 與 JPA Entity 之間的轉換，並在 Save 層捕捉 `DataIntegrityViolationException` 轉為 `DuplicateEmailException`。所有檔案位於 `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/`。
- 測試：`adapter-persistence/src/test/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/port/JpaPortIntegrationTest.java`（使用 `@DataJpaTest` + Testcontainers，測試 Port 實作的 CRUD 行為、唯一性約束觸發的異常、以及 Entity 轉換正確性）。

## T5 Bootstrap Configuration: Wire Up Beans
- 說明：修改 `bootstrap/src/main/java/com/github/chunpingwang/ollamaagentoutcome/bootstrap/config/UseCaseConfiguration.java`。在此配置類中定義所有 Application Service Bean (Create/Get/Update/Delete)，並注入對應的 Outbound Port Bean (由 Persistence Adapter 提供)。確保依賴注入鏈完整。**不新增**新的 Context 測試，僅組裝 Bean。
- 測試：利用骨架提供的 `bootstrap/src/test/java/com/github/chunpingwang/ollamaagentoutcome/bootstrap/ApplicationContextTest.java` 驗證 Spring Context 能否成功啟動且無 BeanNotFound 錯誤。（此任務主要確認組裝邏輯正確，無需額外寫新測試類別，但需確保現有 Context 測試通過）。

## T6 Web Adapter: Controller, DTOs & Exception Handler
- 說明：建立 REST Controller (`ProfileRestController`) 處理 `/profiles` 路徑的 POST/GET/PUT/DELETE。定義 Request/Response DTO (`CreateProfileRequest`, `GetProfileResponse`, `UpdateProfileRequest`)。建立 `GlobalExceptionHandler` 將 Domain Exceptions 映射為 HTTP 狀態碼 (400, 404, 409)。所有檔案位於 `adapter-web/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/`。
- 測試：`adapter-web/src/test/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/controller/ProfileControllerTest.java`（使用 `@WebMvcTest` + Mock UseCase Ports，驗證各端點的路由、HTTP 狀態碼、JSON 序列化與錯誤訊息格式）。

## T7 BDD Acceptance Tests: Feature & Step Definitions
- 說明：撰寫 Cucumber Feature 檔 (`personal-profile-crud.feature`) 描述 CRUD 情境。實作步驟定義類 (`PersonalProfileSteps`) 在 bootstrap 模組的 `bdd` 套件中，使用 `TestRestTemplate` 呼叫實際 API 並斷言結果。確保 Feature 檔第一行為 `# language: zh-TW`。
- 測試：執行 Cucumber Runner (`bootstrap/src/test/java/com/github/chunpingwang/ollamaagentoutcome/bdd/CucumberRunnerIT.java` 或骨架提供的 runner)，驗證所有 Gherkin 場景通過。
