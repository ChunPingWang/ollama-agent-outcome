# 技術設計：個人資料管理 (Spec 001)

## 1. 領域模型 (Domain)

### 實體與值物件

*   **`ProfileId`** (`record`)
    *   類型：`java.util.UUID`
    *   職責：封裝個人資料的唯一識別碼。
*   **`Profile`** (`record`)
    *   欄位：
        *   `id`: `ProfileId`
        *   `name`: `String` (非空，長度 1-100)
        *   `email`: `String` (非空，符合 RFC 5322 格式)
        *   `note`: `String` (可為空，長度 0-500)
    *   不變條件 (Invariants)：
        *   `name` 不能為空白。
        *   `email` 必須是合法的電子郵件格式。
        *   `email` 在系統中必須唯一（由 Domain Service 或 Port 層級確保，但 Domain 層級需定義此規則的例外）。
*   **`ProfileNotFoundException`** (`class extends RuntimeException`)
    *   觸發時機：查詢或更新、刪除時，找不到對應 ID 的資料。
*   **`DuplicateEmailException`** (`class extends RuntimeException`)
    *   觸發時機：建立或更新時，電子郵件已存在於系統中。

## 2. Use Cases 與 Ports

### Inbound Ports (Use Cases)
*   `CreateProfileUseCase`
    *   `execute(CreateProfileRequest request) : ProfileId`
*   `GetProfileUseCase`
    *   `execute(ProfileId id) : Profile`
*   `UpdateProfileUseCase`
    *   `execute(ProfileId id, UpdateProfileRequest request) : Profile`
*   `DeleteProfileUseCase`
    *   `execute(ProfileId id) : void`

### Outbound Ports (Repository Interfaces)
*   `SaveProfilePort`
    *   `save(Profile profile) : Profile`
*   `LoadProfilePort`
    *   `findById(ProfileId id) : Optional<Profile>`
*   `DeleteProfilePort`
    *   `deleteById(ProfileId id) : void`
    *   *(註：為簡化依賴，可合併為 `ProfileRepositoryPort`，但為符合介面分離原則，若更新邏輯需先查詢再儲存，建議分開或讓 Service 層組合。此處為簡潔，將 Load 和 Save 分開，Delete 單獨或合併。考慮到更新需要先 Load 再 Save，若 Port 是純 CRUD，則需組合。為符合 SOLID 介面分離，我們將定義為：)*
    *   `ProfileRepositoryPort`
        *   `save(Profile profile) : Profile`
        *   `findById(ProfileId id) : Optional<Profile>`
        *   `deleteById(ProfileId id) : void`
        *   *(註：若嚴格遵循單一職責，可拆分為 `SaveProfilePort`, `LoadProfilePort`, `DeleteProfilePort`。此處為避免過度設計，且更新場景需同時用到 Load 和 Save，建議在 Application 層組合多個 Port 或定義一個綜合 Port。根據 System Prompt 的 "介面分離" 規則，建議拆分為小介面，讓 Use Case 依賴它需要的最小介面。)*
        *   **修正策略**：
            *   `SaveProfilePort`: `save(Profile)`
            *   `LoadProfilePort`: `findById(ProfileId)`
            *   `DeleteProfilePort`: `deleteById(ProfileId)`
            *   `CreateProfileService` 依賴 `SaveProfilePort`。
            *   `GetProfileService` 依賴 `LoadProfilePort`。
            *   `UpdateProfileService` 依賴 `LoadProfilePort` 和 `SaveProfilePort`。
            *   `DeleteProfileService` 依賴 `LoadProfilePort` 和 `DeleteProfilePort`。

### Use Case Implementations (Services)
*   `CreateProfileService`
    *   依賴：`SaveProfilePort`
    *   邏輯：驗證輸入 -> 呼叫 Port 儲存 -> 回傳 ID。
*   `GetProfileService`
    *   依賴：`LoadProfilePort`
    *   邏輯：呼叫 Port 查詢 -> 若無則拋出 `ProfileNotFoundException` -> 回傳 Profile。
*   `UpdateProfileService`
    *   依賴：`LoadProfilePort`, `SaveProfilePort`
    *   邏輯：呼叫 Port 查詢 -> 若無則拋出 `ProfileNotFoundException` -> 驗證新資料 (含唯一性檢查，若唯一性檢查在 Domain 層，則需 Port 提供查詢功能或 Domain Service 介入；此處將唯一性檢查放在 Domain 層，故需 Port 提供 `findByEmail` 或讓 Domain 層處理。為簡化，假設 `SaveProfilePort` 在儲存時會檢查唯一性並拋出 `DuplicateEmailException`，或者在 Update 前由 Service 層檢查。根據 Spec，409 是重複錯誤，通常在儲存時觸發。為確保一致性，`UpdateProfileService` 將先 Load，驗證資料，然後呼叫 Save。若 Save 內部檢查唯一性失敗，則拋出例外。)
*   `DeleteProfileService`
    *   依賴：`LoadProfilePort`, `DeleteProfilePort`
    *   邏輯：呼叫 Port 查詢 -> 若無則拋出 `ProfileNotFoundException` -> 呼叫 Port 刪除。

## 3. Web Adapter

### Controller
*   `ProfileController`
    *   `@RestController`
    *   `@RequestMapping("/profiles")`
    *   依賴：`CreateProfileUseCase`, `GetProfileUseCase`, `UpdateProfileUseCase`, `DeleteProfileUseCase`

### DTOs
*   `CreateProfileRequest`
    *   `name`: `String` (NotBlank)
    *   `email`: `String` (Email, NotBlank)
    *   `note`: `String` (Size <= 500)
*   `UpdateProfileRequest`
    *   `name`: `String` (NotBlank)
    *   `email`: `String` (Email, NotBlank)
    *   `note`: `String` (Size <= 500)
*   `ProfileResponse`
    *   `id`: `String` (UUID format)
    *   `name`: `String`
    *   `email`: `String`
    *   `note`: `String`

### 錯誤處理 (`@RestControllerAdvice`)
*   `ProfileNotFoundException` -> 404 Not Found
*   `DuplicateEmailException` -> 409 Conflict
*   `MethodArgumentNotValidException` -> 400 Bad Request
*   `HttpMessageNotReadableException` -> 400 Bad Request

## 4. Persistence Adapter

### JPA Entity
*   `ProfileEntity`
    *   實作 `Persistable<UUID>`
    *   欄位：`id` (UUID, PK), `name`, `email`, `note`
    *   `isNew()`: 判斷是否為新記錄，用於避免 `@GeneratedValue` 衝突。

### Repository
*   `ProfileJpaRepository` (`extends JpaRepository<ProfileEntity, UUID>`)
    *   `findByEmail(String email) : Optional<ProfileEntity>` (用於唯一性檢查)

### Port Implementations
*   `JpaSaveProfilePort`
    *   依賴：`ProfileJpaRepository`
    *   實作 `SaveProfilePort`
    *   邏輯：Domain Entity 轉 Entity -> `saveAndFlush` -> 轉回 Domain Entity。捕捉 `DataIntegrityViolationException` 轉為 `DuplicateEmailException`。
*   `JpaLoadProfilePort`
    *   依賴：`ProfileJpaRepository`
    *   實作 `LoadProfilePort`
    *   邏輯：`findById` -> 轉回 Domain Entity。
*   `JpaDeleteProfilePort`
    *   依賴：`ProfileJpaRepository`
    *   實作 `DeleteProfilePort`
    *   邏輯：`deleteById`。

## 5. Bootstrap

### Configuration
*   `UseCaseConfiguration` (`@Configuration`)
    *   `@Bean` `createProfileUseCase`
    *   `@Bean` `getProfileUseCase`
    *   `@Bean` `updateProfileUseCase`
    *   `@Bean` `deleteProfileUseCase`
    *   *(註：Port 實作由 Spring Data JPA 自動掃描註冊，或在此明確定義 Bean。為確保依賴注入清晰，Port 實作類別若無 Spring 註解，需在 Configuration 中定義 Bean，或讓 Spring Data JPA 的 Repository 自動注入。由於 Port 是介面，其實作類別需被 Spring 管理。建議在 `UseCaseConfiguration` 中定義 Port 實作的 Bean，或使用 `@Component` 但需確保套件掃描範圍正確。根據 System Prompt，application 層禁止 Spring 註解，故 Port 實作在 adapter 層，可加 `@Repository` 或在此 Configuration 中定義。為簡化，假設 `adapter-persistence` 模組的 Port 實作類別加 `@Repository`，並由 `bootstrap` 的套件掃描包含 `adapter.persistence`。若不行，則在 `UseCaseConfiguration` 中 `@Bean` 定義 Port 實作。)*
    *   **修正**：System Prompt 指出 "application 的 outbound port 介面，adapter 實作"。Adapter 層可以加 Spring 註解。故 `JpaSaveProfilePort` 等類別可加 `@Repository`。`UseCaseConfiguration` 只需定義 Use Case (Service) 的 Bean。

## 6. 測試策略

*   **Domain 層**：
    *   `ProfileTest`：測試值物件不變條件（如 Email 格式、Name 非空）。
*   **Application 層**：
    *   `CreateProfileServiceTest`：測試成功建立、重複 Email 例外。
    *   `GetProfileServiceTest`：測試成功查詢、找不到例外。
    *   `UpdateProfileServiceTest`：測試成功更新、找不到例外、重複 Email 例外。
    *   `DeleteProfileServiceTest`：測試成功刪除、找不到例外。
    *   使用 Mockito 模擬 Outbound Ports。
*   **Web Adapter 層**：
    *   `ProfileControllerTest` (`@WebMvcTest`)：測試各 API 端點，模擬 Use Case 行為。
*   **Persistence Adapter 層**：
    *   `JpaProfilePortTest` (`@DataJpaTest` + Testcontainers)：測試 Port 實作與 Entity 轉換、唯一性約束。
*   **BDD (Cucumber)**：
    *   `src/test/resources/features/profile-crud.feature`
    *   步驟定義在 `bootstrap/src/test/java/com/github/chunpingwang/ollamaagentoutcome/bdd/`
    *   使用 `TestRestTemplate` 呼叫實際 API。

## 7. 類別清單

| 類別名稱 | 完整路徑 (相對專案根目錄) | 職責 |
| :--- | :--- | :--- |
| `ProfileId` | `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/ProfileId.java` | 值物件：個人資料識別碼 |
| `Profile` | `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/Profile.java` | 實體：個人資料資料模型 |
| `ProfileNotFoundException` | `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/ProfileNotFoundException.java` | 領域例外：找不到資料 |
| `DuplicateEmailException` | `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/DuplicateEmailException.java` | 領域例外：電子郵件重複 |
| `CreateProfileUseCase` | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/in/CreateProfileUseCase.java` | Inbound Port：建立個人資料 |
| `GetProfileUseCase` | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/in/GetProfileUseCase.java` | Inbound Port：查詢個人資料 |
| `UpdateProfileUseCase` | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/in/UpdateProfileUseCase.java` | Inbound Port：更新個人資料 |
| `DeleteProfileUseCase` | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/in/DeleteProfileUseCase.java` | Inbound Port：刪除個人資料 |
| `SaveProfilePort` | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/out/SaveProfilePort.java` | Outbound Port：儲存個人資料 |
| `LoadProfilePort` | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/out/LoadProfilePort.java` | Outbound Port：載入個人資料 |
| `DeleteProfilePort` | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/out/DeleteProfilePort.java` | Outbound Port：刪除個人資料 |
| `CreateProfileService` | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/CreateProfileService.java` | Use Case 實作：建立 |
| `GetProfileService` | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/GetProfileService.java` | Use Case 實作：查詢 |
| `UpdateProfileService` | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/UpdateProfileService.java` | Use Case 實作：更新 |
| `DeleteProfileService` | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/DeleteProfileService.java` | Use Case 實作：刪除 |
| `ProfileController` | `adapter-web/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/ProfileController.java` | REST Controller：處理 HTTP 請求 |
| `CreateProfileRequest` | `adapter-web/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/CreateProfileRequest.java` | Request DTO：建立請求 |
| `UpdateProfileRequest` | `adapter-web/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/UpdateProfileRequest.java` | Request DTO：更新請求 |
| `ProfileResponse` | `adapter-web/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/ProfileResponse.java` | Response DTO：回應資料 |
| `ProfileExceptionHandler` | `adapter-web/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/ProfileExceptionHandler.java` | 錯誤處理：轉換例外為 HTTP 狀態碼 |
| `ProfileEntity` | `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/ProfileEntity.java` | JPA Entity：資料庫映射 |
| `ProfileJpaRepository` | `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/ProfileJpaRepository.java` | Spring Data Repository：資料庫存取 |
| `JpaSaveProfilePort` | `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/JpaSaveProfilePort.java` | Port 實作：儲存 |
| `JpaLoadProfilePort` | `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/JpaLoadProfilePort.java` | Port 實作：載入 |
| `JpaDeleteProfilePort` | `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/JpaDeleteProfilePort.java` | Port 實作：刪除 |
| `UseCaseConfiguration` | `bootstrap/src/main/java/com/github/chunpingwang/ollamaagentoutcome/bootstrap/UseCaseConfiguration.java` | 組態：定義 Use Case Bean |
| `ProfileCrudSteps` | `bootstrap/src/test/java/com/github/chunpingwang/ollamaagentoutcome/bdd/ProfileCrudSteps.java` | BDD 步驟定義 |
| `ProfileCrudTest` | `bootstrap/src/test/java/com/github/chunpingwang/ollamaagentoutcome/bdd/ProfileCrudTest.java` | Cucumber Runner |
