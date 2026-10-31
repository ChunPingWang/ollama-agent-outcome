# Tasks 001 — Personal Profile CRUD

## T1 Domain Model & Value Objects
- 說明：建立領域層的核心模型與值物件。包含 `ProfileId`, `Email`, `DisplayName`, `Bio` 四個值物件（含建構子驗證邏輯），以及 `Profile` 實體類（包含業務規則如更新方法）。同時定義相關的 Domain Exception (`InvalidEmailException`, `InvalidDisplayNameException`, `DuplicateEmailException`, `ProfileNotFoundException`)。
- 測試：
    - `domain/src/test/java/com/github/chunpingwang/ollamaagentoutcome/domain/valueobject/EmailTest.java`: 驗證有效/無效 Email 格式的拋出行為。
    - `domain/src/test/java/com/github/chunpingwang/ollamaagentoutcome/domain/valueobject/DisplayNameTest.java`: 驗證長度限制 (1-100) 的拋出行為。
    - `domain/src/test/java/com/github/chunpingwang/ollamaagentoutcome/domain/model/ProfileTest.java`: 驗證 Profile 的建立、欄位存取，以及基本狀態管理。

## T2 Application Layer Ports & Commands
- 說明：建立應用層的 Inbound Port 介面 (`CreateProfileUseCase`, `LoadProfileUseCase`, `UpdateProfileUseCase`, `DeleteProfileUseCase`) 與 Outbound Port 介面 (`ProfileRepositoryPort`)。定義 Command 類別 (`CreateProfileCommand`, `UpdateProfileCommand`)。此階段僅定義介面與資料結構，不含實作邏輯。
- 測試：
    - `application/src/test/java/com/github/chunpingwang/ollamaagentoutcome/application/port/in/CreateProfileUseCaseTest.java` (Stub): 確認介面存在且可被呼叫（Mock）。
    - *(註：由於 Use Case 實作為下一任務，此處主要確保介面編譯通過並可被依賴。實際業務邏輯測試放在 T3)*

## T3 Application Services Implementation
- 說明：實作所有 Use Case Service (`CreateProfileService`, `LoadProfileService`, `UpdateProfileService`, `DeleteProfileService`)。這些服務注入 `ProfileRepositoryPort`，處理業務流程：檢查重複 Email、轉換 Command 到 Domain Object、調用 Port 儲存或查詢、拋出對應的 Domain Exception。
- 測試：
    - `application/src/test/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/CreateProfileServiceTest.java`: 驗證成功建立、Email 重複時拋出 `DuplicateEmailException`。
    - `application/src/test/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/LoadProfileServiceTest.java`: 驗證找到資料回傳 Optional，找不到時拋出 `ProfileNotFoundException`。
    - `application/src/test/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/UpdateProfileServiceTest.java`: 驗證全量更新邏輯、ID 不存在時拋出例外、新 Email 與他人衝突時拋出例外。
    - `application/src/test/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/DeleteProfileServiceTest.java`: 驗證刪除前檢查存在性，不存在時拋出例外。

## T4 Persistence Adapter (JPA & Port Implementation)
- 說明：在 `adapter-persistence` 模組中建立 JPA Entity (`ProfileEntity`)，實作 `Persistable<UUID>` 以處理 ID 生成狀態。建立 Spring Data Repository (`ProfileJpaRepository`)。實作 Outbound Port (`JpaProfileRepositoryPort`)，負責 Domain Object 與 JPA Entity 之間的轉換，並捕捉 `DataIntegrityViolationException` 轉為 `DuplicateEmailException`。使用 `saveAndFlush` 確保測試中的約束觸發。
- 測試：
    - `adapter-persistence/src/test/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/entity/ProfileEntityTest.java`: 驗證 Entity 的 Persistable 行為（isNew 狀態切換）。
    - `adapter-persistence/src/test/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/port/JpaProfileRepositoryPortTest.java`: 使用 Testcontainers PostgreSQL 進行整合測試。驗證 Save、FindById, ExistsByEmail, Delete 功能，以及重複 Email 時的異常處理。

## T5 Bootstrap Configuration & Context Assembly
- 說明：在 `bootstrap` 模組中建立 `UseCaseConfiguration`。在此配置類中以 `@Bean` 方法定義所有的 Inbound Port (Use Case) 和 Outbound Port (Persistence Adapter) 的 Bean。確保依賴注入正確，Spring Context 可正常啟動且包含所有組件。這是 Web Adapter 能運作的前提。
- 測試：
    - `bootstrap/src/test/java/com/github/chunpingwang/ollamaagentoutcome/bootstrap/config/UseCaseConfigurationIntegrationTest.java`: 使用 `@SpringBootTest` + `@Import(TestcontainersConfiguration.class)` 啟動完整上下文。驗證所有 Use Case 與 Port Bean 均可由 ApplicationContext 取得，無 `NoSuchBeanDefinitionException` 或 `BeanCreationException`。

## T6 Web Adapter (Controller, DTOs & Exception Handling)
- 說明：在 `adapter-web` 模組中建立 REST Controller (`ProfileController`)，對應 CRUD 端點。定義 Request/Response DTOs (`CreateProfileRequestDTO`, `UpdateProfileRequestDTO`, `ProfileResponseDTO`) 並加上 Jakarta Validation 註解。實作 `GlobalExceptionHandler` 將 Domain Exception 映射為正確的 HTTP 狀態碼 (400, 404, 409)。
- 測試：
    - `adapter-web/src/test/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/controller/ProfileControllerTest.java`: 使用 `@WebMvcTest` + `@MockitoBean` Mock 所有的 Use Case。驗證各端點的 HTTP Method、Status Code、Response Body 格式、Location Header (POST)，以及錯誤情境下的異常映射（如 409 Conflict）。

## T7 Cucumber BDD Acceptance Tests
- 說明：撰寫 Cucumber Feature 檔 (`personal-profile-crud.feature`) 與 Step Definitions。Step Definitions 位於 `bootstrap/bdd` 套件，使用 `TestRestTemplate` 呼叫實際的 API 端點（需啟動完整 Spring Context 與 Testcontainers DB）。涵蓋 Happy Path 與 Edge Cases（重複 Email、NotFound 等）。
- 測試：
    - `bootstrap/src/test/resources/features/personal-profile-crud.feature`: Gherkin 場景定義。
    - `bootstrap/src/test/java/com/github/chunpingwang/ollamaagentoutcome/bdd/PersonalProfileSteps.java`: 步驟實作。執行整個 Scenario Outline，確保端到端的資料流正確無誤。
