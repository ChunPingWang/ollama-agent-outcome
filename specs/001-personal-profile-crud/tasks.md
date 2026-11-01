# Tasks 001 — Personal Profile CRUD

## T1 Domain Model & Exceptions
- 說明：建立領域層的核心值物件、聚合根與自訂例外。包含 `ProfileId` (UUID)、`Email` (含格式驗證)、`DisplayName`、`Profile` (Aggregate Root, 含更新邏輯) 以及三個 Domain Exception (`InvalidEmailException`, `DuplicateEmailException`, `ProfileNotFoundException`)。所有類別位於 `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/`。
- 測試：在 `domain/src/test/java/.../domain/` 下建立 `ProfileValueObjectTest.java` 驗證 Email 格式、ID 生成規則；`ProfileDomainServiceTest.java` 驗證 `update()` 方法中的業務規則（如重複 Email 拋出例外）及狀態變更。使用 JUnit + AssertJ，不依賴 Spring。

## T2 Application Ports & Use Cases (Create & Get)
- 說明：定義 Inbound Port (`CreateProfileUseCase`, `GetProfileUseCase`) 與 Outbound Port (`SaveProfilePort`, `LoadProfilePort`)。實作 `CreateProfileService` (檢查唯一性後儲存) 與 `GetProfileService` (查詢並處理未找到情況)。檔案位於 `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/in/out/usecase/`。
- 測試：在 `application/src/test/java/.../usecase/` 下建立 `CreateProfileServiceTest.java` (模擬 SavePort, 驗證重複檢查與呼叫流程) 與 `GetProfileServiceTest.java` (模擬 LoadPort, 驗證成功回傳 Optional 與失敗拋出例外)。使用 Mockito 模擬 Port。

## T3 Application Use Cases (Update & Delete)
- 說明：定義 Inbound Port (`UpdateProfileUseCase`, `DeleteProfileUseCase`) 與 Outbound Port (`DeleteProfilePort` - 可複用或獨立)。實作 `UpdateProfileService` (先載入、驗證自身以外無重複 Email、更新狀態) 與 `DeleteProfileService` (先確認存在再刪除)。檔案位於 `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/in/out/usecase/`。
- 測試：在 `application/src/test/java/.../usecase/` 下建立 `UpdateProfileServiceTest.java` (驗證 ID 不存在拋出例外、Email 衝突處理) 與 `DeleteProfileServiceTest.java` (驗證不存在時拋出例外、成功刪除後呼叫 deleteById)。使用 Mockito 模擬 Port。

## T4 Persistence Adapter (Entity, Repository & Ports)
- 說明：建立 JPA Entity `ProfileEntity` (實作 `Persistable<String>`)、Repository `ProfileRepository`，以及三個 Port 實作類別 (`JpaSaveProfilePort`, `JpaLoadProfilePort`, `JpaDeleteProfilePort`) 和轉換器 `ProfileConverter`。所有檔案位於 `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/`。同時建立 `PersistenceConfiguration.java` 註冊 Bean。
- 測試：在 `adapter-persistence/src/test/java/.../persistence/` 下建立 `JpaPortIntegrationTest.java` (使用 `@DataJpaTest` + Testcontainers PostgreSQL)，驗證 Entity 映射、唯一約束觸發的 `DuplicateEmailException`、以及 Port 方法的正確性（包含 `saveAndFlush` 的事務行為）。

## T5 Bootstrap Configuration
- 說明：修改 `bootstrap/src/main/java/com/github/chunpingwang/ollamaagentoutcome/bootstrap/UseCaseConfiguration.java`，以 `@Bean` 方法將 Application 層的 Use Case 與 Persistence 層的 Port 組裝起來。確保 Spring Context 能正確解析依賴。
- 測試：執行骨架提供的 `bootstrap.ApplicationContextTest.java` (或類似啟動測試) 來驗證整個應用程式上下文能否成功啟動且無 Bean 定義衝突。不需新增新的 Context 測試類別。

## T6 Web Adapter & Exception Handling
- 說明：建立 REST Controller `ProfileController` (`/api/profiles`)、DTOs (`CreateProfileRequest`, `UpdateProfileRequest`, `GetProfileResponse`) 以及 `GlobalExceptionHandler`。處理 Jakarta Validation 錯誤並映射 Domain Exception 至 HTTP Status Code (400, 404, 409)。檔案位於 `adapter-web/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/`。
- 測試：在 `adapter-web/src/test/java/.../web/` 下建立 `ProfileControllerTest.java` (使用 `@WebMvcTest` + `MockitoBean` Mock Inbound Ports)，驗證各端點的 HTTP Method、Status Code、JSON 序列化/反序列化及異常轉換邏輯。

## T7 Cucumber BDD Acceptance Tests
- 說明：撰寫 Gherkin Feature 檔與步驟定義，進行端到端驗收。Feature 檔涵蓋 CRUD 所有情境（含邊界條件如重複 Email）。步驟定義使用 `TestRestTemplate` 呼叫實際 API。檔案位於 `bootstrap/src/test/resources/features/personal-profile-crud.feature` 與 `bootstrap/src/test/java/com/github/chunpingwang/ollamaagentoutcome/bdd/PersonalProfileSteps.java`。
- 測試：執行 Cucumber Runner (`CucumberIT.java` 或骨架提供的 Runner) 確保所有 Scenario 通過。驗證真實資料庫操作與 REST API 的完整流程。
