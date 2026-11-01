# Plan 001 — 個人資料管理 (Personal Profile CRUD)

本計畫基於 Spec 001 設計六角形架構的實現細節。所有程式碼將嚴格遵循 `com.github.chunpingwang.ollamaagentoutcome` 基底套件結構。

## 1. 領域模型 (Domain Model)

### Value Objects & Entities
由於需求未指定複雜的業務邏輯，且識別碼由外部生成或 UUID 產生，我們採用以下結構：

*   **ProfileId** (`record`)
    *   內容：`UUID id`
    *   不變條件：不可為 null。
*   **Email** (`record`)
    *   內容：`String value`
    *   不變條件：符合 RFC 5322 基本格式（使用正則表達式驗證），全域唯一（由 Port 層保證）。
*   **DisplayName** (`record`)
    *   內容：`String value`
    *   不變條件：允許空白字串或僅含空白字符（視為未設定/null），但不可為 null 引用本身。若輸入為空白，內部可存為空字串或 null，視實作方便性而定，此處建議統一轉為空字串 `""` 以簡化處理，或在 Domain 層定義 `Optional<String>`。為符合 Spec "允許為空"，我們將接受 `null` 或空白並轉換為 `""`。
*   **Profile** (`class`, immutable state via methods)
    *   屬性：`id: ProfileId`, `email: Email`, `displayName: DisplayName`
    *   行為：
        *   `update(email, displayName)`: 建立新的 Profile 實體或更新狀態（根據 SOLID，這裡選擇返回新實例或修改 mutable fields? 考慮到 JPA Entity 需要受管狀態，Domain Entity 通常設計為值物件風格或聚合根。鑑於這是單一實體且無複雜關聯，我們將其設計為具有明確生命週期的 Aggregate Root。為了配合 JPA 的 `saveAndFlush` 與樂觀鎖/併發控制需求，Domain 層保持純淨，不依賴 JPA。JPA Entity 在 Adapter 層映射）。

### Domain Exceptions
*   `DuplicateEmailException`: 當嘗試建立或更新已存在的電子郵件時拋出。
*   `ProfileNotFoundException`: 當查詢或刪除不存在的 ID 時拋出。
*   `InvalidEmailException`: 當輸入格式不符合規範時拋出。

## 2. Use Cases & Ports (Application Layer)

### Inbound Ports (Interfaces ending with `UseCase`)
1.  **CreateProfileUseCase**
    *   `void execute(CreateProfileCommand command)`
    *   Command DTO: `record CreateProfileCommand(String email, String displayName)`
2.  **GetProfileUseCase**
    *   `Optional<Profile> execute(GetProfileQuery query)`
    *   Query DTO: `record GetProfileQuery(ProfileId id)`
3.  **UpdateProfileUseCase**
    *   `void execute(UpdateProfileCommand command)`
    *   Command DTO: `record UpdateProfileCommand(ProfileId id, String email, String displayName)`
4.  **DeleteProfileUseCase**
    *   `void execute(DeleteProfileCommand command)`
    *   Command DTO: `record DeleteProfileCommand(ProfileId id)`

### Outbound Ports (Interfaces ending with `Port`)
1.  **SaveProfilePort**
    *   `Profile save(Profile profile)`
2.  **FindProfileByIdPort**
    *   `Optional<Profile> findById(ProfileId id)`
3.  **ExistsByEmailPort**
    *   `boolean existsByEmail(Email email)`
4.  **DeleteProfileByIdPort**
    *   `void deleteById(ProfileId id)`

### Use Case Implementations (Classes ending with `Service`)
*   `CreateProfileService`: 驗證 Email 格式 -> 檢查唯一性 (`existsByEmail`) -> 建立 Domain Entity -> 呼叫 `save`。
*   `GetProfileService`: 呼叫 `findById`，若無則拋出 `ProfileNotFoundException`（或回傳 Optional，由 Controller 處理）。為簡化錯誤流，建議在此層若找不到即拋出例外，讓 Controller 統一攔截轉為 404。
*   `UpdateProfileService`: 檢查存在性 -> 載入現有 Profile -> 執行領域更新邏輯 -> 儲存。注意：需檢查新 Email 是否與其他記錄衝突。
*   `DeleteProfileService`: 檢查存在性 -> 呼叫 `deleteById`。

## 3. Web Adapter (adapter-web)

### REST Controller: `ProfileController`
*   Base Path: `/api/profiles`
*   Endpoints:
    *   `POST /{id}`? No, POST is collection level. `POST /` or just path mapping. Spec says `/api/profiles`.
        *   `@PostMapping("/")` or default path if class has no path. Let's use `@RequestMapping("/api/profiles")`.
        *   `create(@Valid @RequestBody CreateProfileRequest req)` -> Returns 201 + Location header.
    *   `GET /{id}`
        *   `get(@PathVariable("id") UUID id)` -> Returns 200 + Response Body.
    *   `PUT /{id}`
        *   `update(@PathVariable("id") UUID id, @Valid @RequestBody UpdateProfileRequest req)` -> Returns 200.
    *   `DELETE /{id}`
        *   `delete(@PathVariable("id") UUID id)` -> Returns 204.

### DTOs
*   **CreateProfileRequest**: `email` (String), `displayName` (String). Validated with Jakarta Validation (`@Email`, etc.).
*   **UpdateProfileRequest**: Same as Create. Note: Email uniqueness check must happen in Domain/Application layer because the ID might be changing email to an existing one.
*   **ProfileResponse**: `id` (UUID), `email` (String), `displayName` (String).

### Error Handling: `GlobalExceptionHandler` (@RestControllerAdvice)
*   `MethodArgumentNotValidException` -> 400 Bad Request.
*   `HttpMessageNotReadableException` -> 400 Bad Request.
*   `DuplicateEmailException` -> 409 Conflict.
*   `ProfileNotFoundException` -> 404 Not Found.
*   Other Exceptions -> Do NOT catch generic Exception. Let Spring handle it or specific ones if needed. Spec says don't catch all exceptions.

## 4. Persistence Adapter (adapter-persistence)

### JPA Entity: `ProfileEntity`
*   Package: `com.github.chunpingwang.ollamaagentoutcome.adapter.persistence`
*   Fields: `id` (UUID, Primary Key), `email` (String, Unique Constraint), `display_name` (String).
*   Implements `Persistable<UUID>`?
    *   Since we are generating UUIDs for new records, and need to distinguish between insert and update without version fields causing issues on save, implementing `Persistable` is safer as per the "Persistence Common Pitfalls" rule.
    *   However, standard Spring Data JPA with `@GeneratedValue(strategy = GenerationType.UUID)` works fine in Boot 3/4 if configured correctly. But the prompt specifically warns about domain-generated IDs vs GeneratedValue. If Domain generates UUID, we should use `Persistable`.
    *   Decision: Use `Persistable<UUID>`. Field `isNew` managed by lifecycle callbacks (`@PostLoad`, `@PostPersist`).
*   Constraints: `@Column(unique=true)` on email.

### Repository Interface: `ProfileRepository` extends JpaRepository<ProfileEntity, UUID>, ProfilePortImplementationHelper?
*   Actually, Port implementations will be separate classes or interfaces implemented here.
*   Methods needed: `existsByEmail(String)`, `findById(UUID)`, `save(ProfileEntity)`, `deleteById(UUID)`.

### Port Implementations (Classes ending with `Port`)
*   **JpaSaveProfilePort**: Implements `SaveProfilePort`. Converts Domain -> Entity, calls repo.saveAndFlush(), converts back. Handles `DataIntegrityViolationException` for duplicate emails.
*   **JpaFindProfileByIdPort**: Implements `FindProfileByIdPort`. Calls repo.findById().
*   **JpaExistsByEmailPort**: Implements `ExistsByEmailPort`. Calls repo.existsByEmail().
*   **JpaDeleteProfileByIdPort**: Implements `DeleteProfileByIdPort`. Checks existence first (to throw specific exception if needed, though spec says 404 for delete non-existent), then deletes. Note: Spec requires 404 if deleting non-existent. So this port should check existence and throw `ProfileNotFoundException` if not found before calling repository delete.

## 5. Bootstrap

### Configuration: `UseCaseConfiguration` (@Configuration)
*   Defines beans for all Inbound Ports (`CreateProfileService`, etc.).
*   Injects Outbound Ports into Services via constructor.
*   No other `@SpringBootApplication` or component scanning overrides in bootstrap test context logic beyond what's provided.

## 6. Testing Strategy

### Unit Tests (JUnit + AssertJ + Mockito)
*   **Domain**: Test Value Object validation (Email format).
*   **Application**:
    *   `CreateProfileServiceTest`: Happy path, Duplicate email (mock throws/returns true), Invalid input.
    *   `GetProfileServiceTest`: Found, Not Found.
    *   `UpdateProfileServiceTest`: Update existing, Email conflict with another ID, Not Found update.
    *   `DeleteProfileServiceTest`: Delete existing, Not Found delete.
*   **Web Adapter** (`@WebMvcTest`):
    *   Mock the Use Cases. Verify HTTP status codes (201, 200, 204, 400, 404, 409) and headers (Location).
*   **Persistence Adapter** (`@DataJpaTest` + Testcontainers):
    *   Test Port implementations directly using `@Import`.
    *   Verify JPA mapping, unique constraint enforcement, and exception translation.

### Integration/BDD Tests (Cucumber)
*   Feature File: `bootstrap/src/test/resources/features/personal-profile-crud.feature`
*   Step Definitions: `com.github.chunpingwang.ollamaagentoutcome.bdd.ProfileSteps`
*   Uses `TestRestTemplate` to hit actual endpoints against a real PostgreSQL container.
*   Scenarios map directly to Spec Gherkin scenarios.

## 7. Class List

| Module | Path (Relative to Root) | Responsibility |
| :--- | :--- | :--- |
| domain | `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/ProfileId.java` | Value Object for Profile ID |
| domain | `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/Email.java` | Value Object with validation logic |
| domain | `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/DisplayName.java` | Value Object for display name |
| domain | `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/Profile.java` | Aggregate Root / Domain Entity |
| domain | `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/DuplicateEmailException.java` | Domain Exception for duplicate email |
| domain | `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/ProfileNotFoundException.java` | Domain Exception for missing profile |
| application | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/in/CreateProfileUseCase.java` | Inbound Port: Create Profile |
| application | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/in/GetProfileUseCase.java` | Inbound Port: Get Profile |
| application | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/in/UpdateProfileUseCase.java` | Inbound Port: Update Profile |
| application | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/in/DeleteProfileUseCase.java` | Inbound Port: Delete Profile |
| application | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/CreateProfileService.java` | Use Case Implementation: Create |
| application | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/GetProfileService.java` | Use Case Implementation: Read |
| application | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/UpdateProfileService.java` | Use Case Implementation: Update |
| application | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/DeleteProfileService.java` | Use Case Implementation: Delete |
| application | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/out/SaveProfilePort.java` | Outbound Port: Save |
| application | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/out/FindProfileByIdPort.java` | Outbound Port: Find By ID |
| application | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/out/ExistsByEmailPort.java` | Outbound Port: Exists By Email |
| application | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/out/DeleteProfileByIdPort.java` | Outbound Port: Delete By ID |
| adapter-web | `adapter-web/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/ProfileController.java` | REST Controller for Profile CRUD |
| adapter-web | `adapter-web/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/CreateProfileRequest.java` | Request DTO for Create |
| adapter-web | `adapter-web/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/UpdateProfileRequest.java` | Request DTO for Update |
| adapter-web | `adapter-web/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/ProfileResponse.java` | Response DTO |
| adapter-web | `adapter-web/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/GlobalExceptionHandler.java` | Handles HTTP errors and maps to status codes |
| adapter-persistence | `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/ProfileEntity.java` | JPA Entity mapping |
| adapter-persistence | `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/ProfileRepository.java` | Spring Data Repository interface |
| adapter-persistence | `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/JpaSaveProfilePort.java` | Implements SaveProfilePort using JPA |
| adapter-persistence | `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/JpaFindProfileByIdPort.java` | Implements FindProfileByIdPort using JPA |
| adapter-persistence | `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/JpaExistsByEmailPort.java` | Implements ExistsByEmailPort using JPA |
| adapter-persistence | `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/JpaDeleteProfileByIdPort.java` | Implements DeleteProfileByIdPort using JPA |
| bootstrap | `bootstrap/src/main/java/com/github/chunpingwang/ollamaagentoutcome/bootstrap/UseCaseConfiguration.java` | Configures Use Case beans |
| bootstrap | `bootstrap/src/test/resources/features/personal-profile-crud.feature` | Cucumber Feature file for BDD tests |
| bootstrap | `bootstrap/src/test/java/com/github/chunpingwang/ollamaagentoutcome/bdd/ProfileSteps.java` | Step definitions for Profile CRUD scenarios |
