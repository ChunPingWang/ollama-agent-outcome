# Tasks 001 — Personal Profile CRUD (個人資料管理)

## T1 Domain Model & Exceptions
- 說明：建立領域層的核心值物件、實體與例外類別。包含 `ProfileId`、`Email`、`DisplayName` 的驗證邏輯，以及 `Profile` 實體的狀態管理方法 (`updateDisplayName`)。同時定義所有相關的 Domain Exception (`InvalidEmailException`, `InvalidDisplayNameException`, `DuplicateEmailException`, `ProfileNotFoundException`)。這些類別位於 `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/`。
- 測試：在 `domain/src/test/java/com/github/chunpingwang/ollamaagentoutcome/domain/` 下建立單元測試，驗證 Value Object 的不變條件（如 Email 格式、非空白檢查）以及 Entity 狀態更新的正確性。使用 JUnit + AssertJ，不依賴 Spring。

## T2 Application Ports & Use Case Interfaces
- 說明：定義應用層的 Inbound Port (`CreateProfileUseCase`, `GetProfileUseCase`, `UpdateProfileUseCase`, `DeleteProfileUseCase`) 及其對應的 Request/Response DTO records；並定義 Outbound Port (`SaveProfilePort`, `LoadProfilePort`, `ExistsByEmailPort`, `DeleteProfilePort`)。這些介面位於 `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/in/` 與 `port/out/`。
- 測試：此階段僅產生介面與 DTO，無需撰寫行為測試。確保套件結構符合規範，無子目錄混用。

## T3 Application Services (Business Logic)
- 說明：實作所有 Use Case Service (`CreateProfileService`, `GetProfileService`, `UpdateProfileService`, `DeleteProfileService`)。邏輯包含：呼叫 LoadPort 確認存在性、驗證業務規則（如更新時檢查 Email 唯一性）、轉換 Domain Entity、呼叫 Save/Delete Port。服務類別位於 `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/`。
- 測試：在 `application/src/test/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/` 下建立單元測試。使用 Mockito 模擬 Outbound Ports，驗證 Service 是否正確調用 Port、例外拋出時機（如找不到資料拋出 `ProfileNotFoundException`）以及業務規則的執行順序。

## T4 Persistence Adapter & Repository
- 說明：建立 JPA Entity (`ProfileEntity`) 實現 `Persistable<UUID>`，處理新增/更新狀態判斷；建立 Spring Data Repository (`ProfileRepository`) 並定義自訂查詢方法 `existsByEmail`。同時實作所有 Outbound Port 的 JPA 版本 (`JpaSaveProfilePort`, `JpaLoadProfilePort`, `JpaExistsByEmailPort`, `JpaDeleteProfilePort`)，負責 Domain Entity 與 JPA Entity 之間的轉換及異常捕捉（將 DB 約束衝突轉為 `DuplicateEmailException`）。這些類別位於 `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/`。
- 測試：在 `adapter-persistence/src/test/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/` 下建立整合測試。使用 `@DataJpaTest` + Testcontainers PostgreSQL。測試重點包括：Entity 映射正確性、`existsByEmail` 查詢結果、Port 實作中唯一性約束觸發時的例外包裝是否正確。

## T5 Bootstrap Configuration (Wiring)
- 說明：修改骨架提供的 `UseCaseConfiguration`，以 `@Bean` 方式組裝所有的 Port 實作與 Service Bean。確保依賴注入順序正確（先 Port，後 Service）。此步驟不新增新的 Context 測試類，僅配置 Bean。檔案位於 `bootstrap/src/main/java/com/github/chunpingwang/ollamaagentoutcome/bootstrap/UseCaseConfiguration.java`。
- 測試：執行現有的 `ApplicationContextTest` 驗證 Spring Context 能否成功啟動且無 Bean 定義衝突或找不到 Bean 的錯誤。確認所有 Use Case Service 均可被解析。

## T6 Web Adapter & Error Handling
- 說明：建立 REST Controller (`ProfileController`) 對應四個 HTTP 端點，處理 DTO 轉換並呼叫 Application Inbound Ports。同時建立 `GlobalExceptionHandler` 將 Domain Exception 映射為正確的 HTTP 狀態碼 (400, 404, 409)。這些類別位於 `adapter-web/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/`。
- 測試：在 `adapter-web/src/test/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/` 下建立 `@WebMvcTest` 單元測試。使用 `@MockitoBean` 模擬 Inbound Ports，驗證各端點的 HTTP 狀態碼、Response Body JSON 結構、Location Header（Create）以及例外處理邏輯是否正確。

## T7 Cucumber BDD Acceptance Tests
- 說明：撰寫 Cucumber Feature 檔與 Step Definitions，涵蓋個人資料 CRUD 的所有主要情境與邊界條件（包含成功流程、資料驗證失敗、資源不存在、重複 Email 等）。Feature 檔位於 `bootstrap/src/test/resources/features/personal-profile-crud.feature`，Step Definitions 位於 `bootstrap/src/test/java/com/github/chunpingwang/ollamaagentoutcome/bdd/ProfileStepDefinitions.java`。使用 `TestRestTemplate` 進行端到端呼叫。
- 測試：執行 Cucumber Runner，確保所有 Scenario 通過。這是最終的驗收測試，確認整個系統從 Web 層到資料庫層的完整運作。
