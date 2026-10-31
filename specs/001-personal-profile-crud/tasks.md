# Tasks 001 — 個人資料管理 (Personal Profile CRUD)

## T1 建立領域模型與值物件
- 說明：定義核心領域實體、值物件及例外類別。包含 `ProfileId`, `Email`, `DisplayName`, `Bio` 等 Value Object 以確保不變條件；`Profile` Entity 負責狀態維護與基本業務規則（如 bio 長度檢查）；以及 `ProfileNotFoundException`, `DuplicateEmailException`, `InvalidEmailFormatException`, `BioTooLongException` 等 Domain Exception。
- 測試：
    - `domain/src/test/java/com/github/chunpingwang/ollamaagentoutcome/domain/valueobject/EmailTest.java`：驗證 Email 格式合法性與不可變性。
    - `domain/src/test/java/com/github/chunpingwang/ollamaagentoutcome/domain/valueobject/BioTest.java`：驗證 Bio 長度限制與不可變性。
    - `domain/src/test/java/com/github/chunpingwang/ollamaagentoutcome/domain/entity/ProfileTest.java`：驗證 Profile 的建立、更新邏輯，以及觸發 domain exception 的情境。

## T2 定義 Application Port 介面與 Command/Query 對象
- 說明：在 application 模組中定義 Inbound Ports (`CreateProfileUseCase`, `GetProfileUseCase`, `UpdateProfileUseCase`, `DeleteProfileUseCase`) 與 Outbound Ports (`SaveProfilePort`, `LoadProfilePort`, `DeleteProfilePort`)。同時建立對應的 Command (`CreateProfileCommand`, `UpdateProfileCommand`, `DeleteProfileCommand`) 與 Query (`GetProfileQuery`) 類別。此階段僅包含介面與資料結構，不含業務邏輯實作。
- 測試：無（純介面與 POJO，由後續 Use Case 實作涵蓋）。

## T3 實現 Use Cases (Application Layer)
- 說明：實現所有 Use Case Service 類別 (`CreateProfileService`, `GetProfileService`, `UpdateProfileService`, `DeleteProfileService`)。這些服務負責協調 Domain Entity 與 Outbound Ports，處理領域規則（如檢查重複 Email、Bio 長度），並轉換例外。
- 測試：
    - `application/src/test/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/CreateProfileServiceTest.java`：驗證成功創建與重複 Email 異常。
    - `application/src/test/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/GetProfileServiceTest.java`：驗證存在與不存在的情境。
    - `application/src/test/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/UpdateProfileServiceTest.java`：驗證更新邏輯與找不到實體的異常。
    - `application/src/test/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/DeleteProfileServiceTest.java`：驗證刪除邏輯與找不到實體的異常。

## T4 建立 Persistence Adapter (JPA Entities & Repositories)
- 說明：在 adapter-persistence 模組中定義 JPA Entity (`ProfileEntity`) 對應資料庫表結構，以及 Spring Data Repository (`ProfileJpaRepository`)。確保 Entity 正確映射欄位與唯一性約束 (Unique Constraint on email)。
- 測試：
    - `adapter-persistence/src/test/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/entity/ProfileEntityTest.java`：驗證 Entity 欄位映射、建構子邏輯及基本狀態管理（若需）。
    - *(註：完整的 DB 互動測試將放在 T5 的 Port 實作測試中)*

## T5 實現 Outbound Ports (Persistence Adapters)
- 說明：實現 Application Layer 定義的 Outbound Ports (`JpaSaveProfilePort`, `JpaLoadProfilePort`, `JpaDeleteProfilePort`)。負責 Domain Object 與 JPA Entity 之間的轉換，並處理底層數據庫例外（如將 `DataIntegrityViolationException` 轉為 `DuplicateEmailException`）。
- 測試：
    - `adapter-persistence/src/test/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/port/JpaSaveProfilePortTest.java`：使用 Testcontainers PostgreSQL 驗證儲存成功、更新成功、重複 Email 異常。
    - `adapter-persistence/src/test/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/port/JpaLoadProfilePortTest.java`：驗證依 ID 查詢功能。
    - `adapter-persistence/src/test/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/port/JpaDeleteProfilePortTest.java`：驗證刪除功能。

## T6 Bootstrap 組裝與上下文啟動驗證
- 說明：在 bootstrap 模組建立 `UseCaseConfiguration`，以 `@Bean` 方式註冊所有 Use Case Services 與 Port Implementations。確保 Spring Context 能正確載入所有依賴，並透過 `@SpringBootTest` 驗證應用程式可正常啟動且 Bean 注入無誤。此步驟必須在 Web Adapter 之前完成，以便後續 Controller 能找到所需的 Use Case Beans。
- 測試：
    - `bootstrap/src/test/java/com/github/chunpingwang/ollamaagentoutcome/bootstrap/config/UseCaseConfigurationIntegrationTest.java`：使用 `@SpringBootTest` + `@Import(TestcontainersConfiguration.class)` 驗證整個應用上下文（含資料庫連線）可順利啟動，且關鍵 Bean (如 `CreateProfileService`) 存在於容器中。

## T7 實現 Web Adapter (Controller & DTOs)
- 說明：在 adapter-web 模組中實現 REST API。包含 `ProfileController` (`POST`, `GET`, `PUT`, `DELETE`)、DTOs (`CreateProfileRequest`, `UpdateProfileRequest`, `GetProfileResponse`) 以及 `GlobalExceptionHandler`。處理 HTTP 請求解析、參數綁定、呼叫 Application Layer Ports，並將 Domain Exception 映射為適當的 HTTP 狀態碼 (400, 404, 409)。
- 測試：
    - `adapter-web/src/test/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/controller/ProfileControllerTest.java`：使用 `@WebMvcTest` 與 Mockito Mock 出 Use Case Port，驗證各端點的 HTTP 狀態碼、回應內容、Location Header 及錯誤處理邏輯。

## T8 BDD Cucumber 驗收測試
- 說明：撰寫 Cucumber Feature 檔與 Step Definitions，模擬真實使用者操作個人資料 CRUD 流程。Step Definitions 透過 `TestRestTemplate` 呼叫實際運行的 API（搭配 Testcontainers PostgreSQL），確保端到端的整合性。
- 測試：
    - `bootstrap/src/test/resources/features/personal-profile-crud.feature`：定義繁體中文 Gherkin 場景（創建、查詢、更新、刪除、異常情境）。
    - `bootstrap/src/test/java/com/github/chunpingwang/ollamaagentoutcome/bdd/PersonalProfileSteps.java`：實作對應的步驟定義，執行 HTTP 請求並斷言結果。
