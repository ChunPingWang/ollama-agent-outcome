# Tasks 001 — Personal Profile CRUD

## T1 Domain Model & Exceptions
- 說明：建立領域層的核心值物件、實體與例外類別。包含 `ProfileId` (UUID wrapper)、`Email` (含 RFC5322 基本格式驗證)、`Name` (長度與空白檢查)、`Profile` (不可變性設計，含 factory method 與 update 方法)。同時定義四個領域例外：`InvalidEmailException`、`InvalidNameException`、`DuplicateEmailException`、`ProfileNotFoundException`。這些類別必須是純 Java，不依賴任何框架。
- 測試：`domain/src/test/java/com/github/chunpingwang/ollamaagentoutcome/domain/ValueObjectValidationTest.java`
    - 驗證 `Email` 建構子對無效格式拋出 `InvalidEmailException`。
    - 驗證 `Name` 建構子對過長或空白字串拋出 `InvalidNameException`。
    - 驗證 `Profile.create()` 成功建立實體。
    - 驗證 `Profile.update()` 正確更新欄位並保持 ID 不變。

## T2 Application Ports & Services Implementation
- 說明：定義應用層的 Inbound Port (`CreateProfileUseCase`, `GetProfileUseCase`, `UpdateProfileUseCase`, `DeleteProfileUseCase`) 與 Outbound Port (`SaveProfilePort`, `LoadProfilePort`, `DeleteProfilePort`)。實作對應的 Service (`CreateProfileService`, `GetProfileService`, `UpdateProfileService`, `DeleteProfileService`)。Service 負責協調 Port，處理業務邏輯（如檢查存在性、轉換 DTO），但不包含 Spring 註解。
- 測試：`application/src/test/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/CreateProfileServiceTest.java`
    - 模擬 `SaveProfilePort.save()` 成功時，執行無異常且呼叫一次 save。
    - 模擬 `SaveProfilePort.save()` 拋出 `DuplicateEmailException` 時，服務層應透傳該例外。
    - (其他 Use Case 可合併於同一個測試類別或分開，重點是驗證 Service 對 Port 的調用邏輯)

## T3 Persistence Adapter: Entity, Repository & Converter
- 說明：建立 JPA Entity `ProfileEntity`，需實現 `Persistable<UUID>` 以支援 Domain 產生的 UUID。建立 Spring Data Repository `ProfileJpaRepository`。建立靜態工具類 `ProfileConverter` 負責 Domain `Profile` 與 JPA `ProfileEntity` 之間的雙向轉換。注意：Entity 不設 `@GeneratedValue`，而是由 Domain 產生 ID。
- 測試：`adapter-persistence/src/test/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/ProfileEntityConversionTest.java`
    - 使用純單元測試（無需 DB）驗證 `ProfileConverter.toDomain(entity)` 和 `toEntity(profile)` 的屬性映射正確性。
    - 驗證 `ProfileEntity.isNew()` 在實體建立後為 true，經 `@PostLoad` 或手動設定後變為 false。

## T4 Persistence Adapter: Port Implementation with Testcontainers
- 說明：實作 Outbound Ports (`JpaSaveProfilePort`, `JpaLoadProfilePort`, `JpaDeleteProfilePort`)。這些類別依賴 `ProfileJpaRepository` 並呼叫 `ProfileConverter`。關鍵點：`saveAndFlush` 必須捕捉 `DataIntegrityViolationException` 並轉譯為 `DuplicateEmailException`。使用 Testcontainers PostgreSQL 進行整合測試。
- 測試：`adapter-persistence/src/test/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/JpaSaveProfilePortIntegrationTest.java`
    - 啟動 Testcontainer PostgreSQL。
    - 測試正常儲存與讀取流程。
    - 測試插入重複 Email 時拋出 `DuplicateEmailException`。
    - 測試刪除不存在的 ID 時的行為（通常由 Repository 處理或 Service 層檢查）。

## T5 Bootstrap Configuration & Context Assembly
- 說明：修改 `bootstrap/config/UseCaseConfiguration.java`，以 `@Bean` 方法註冊所有 Application Layer 的 Service (Inbound Ports) 以及 Persistence Adapter 的 Port Implementations (Outbound Ports)。確保 Spring Boot 能正確掃描到 JPA Repositories 和這些 Bean。**不要**新增新的 Context 測試，僅配置 Bean。
- 測試：無新檔案。此任務通過骨架提供的 `bootstrap.ApplicationContextTest` 驗證，該測試會嘗試啟動整個應用上下文。若 Bean 定義錯誤或缺失依賴，測試將失敗。

## T6 Web Adapter: Controller, DTOs & Exception Handler
- 說明：建立 REST Controller `ProfileController`，映射 HTTP 請求至 Use Case。定義 Request/Response DTOs (`CreateProfileRequest`, `UpdateProfileRequest`, `ProfileResponse`) 並使用 Jakarta Validation。實作 `GlobalExceptionHandler` 將 Domain Exceptions 轉為對應的 HTTP Status Code (400, 404, 409)。
- 測試：`adapter-web/src/test/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/ProfileControllerMockMvcTest.java`
    - 使用 `@WebMvcTest` + `@MockitoBean` mock 所有的 Use Cases。
    - 驗證 POST /profiles 成功回傳 201 與 Location header。
    - 驗證 GET /{id} 存在時回傳 200，不存在時回傳 404。
    - 驗證 PUT /{id} 處理重複 Email 時回傳 409。
    - 驗證 DELETE /{id} 回傳 204。
    - 驗證輸入驗證錯誤（如無效 Email）回傳 400。

## T7 BDD Acceptance Tests with Cucumber & Testcontainers
- 說明：建立 Cucumber Feature 檔 `personal-profile-crud.feature` (zh-TW) 與 Step Definitions (`PersonalProfileSteps`)。Step Definitions 使用 `TestRestTemplate` 呼叫實際 API，並依賴 Bootstrap 組裝好的完整 Spring Context（包含 Testcontainer DB）。這是最後一層的端到端驗收測試。
- 測試：執行 Cucumber Runner。
    - 場景：Happy Path Create, Read, Update, Delete。
    - 場景：Validation Error (Invalid Input)。
    - 場景：Conflict (Duplicate Email)。
    - 場景：Not Found (Accessing non-existent ID)。
