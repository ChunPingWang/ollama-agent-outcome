# Plan 001 — 個人資料管理 (Personal Profile CRUD)

本文件定義了「個人資料管理」功能的技術設計細節，遵循六角形架構（Ports and Adapters），確保領域邏輯與基礎設施分離。

## 1. 領域模型 (Domain Model)

### Value Objects & Entities

由於識別碼由 Domain 層自行生成（UUID），Entity 需實現 `Persistable<UUID>` 以處理 JPA 的新增/更新判斷。

*   **`ProfileId`** (`record`)
    *   內容：`String value` (UUID String)。
    *   不變條件：必須符合 UUID 格式。
*   **`Email`** (`record`)
    *   內容：`String address`。
    *   不變條件：非空白、符合 RFC 5322 基本格式（使用正則表達式驗證）。全域唯一性由 Repository Port 保證。
*   **`DisplayName`** (`record`)
    *   內容：`String name`。
    *   不變條件：非空白字符串（`!name.isBlank()`）。
*   **`Profile`** (`class`, mutable for persistence, immutable logic via methods)
    *   欄位：`id: ProfileId`, `email: Email`, `displayName: DisplayName`。
    *   方法：
        *   `updateDisplayName(DisplayName newName)`：檢查新名稱有效性，更新內部狀態。
        *   `toDto()`: 轉換為應用層 DTO（可選，或直接在 Adapter 層轉換）。
    *   注意：Domain Entity 不依賴 JPA Annotation。

### Domain Exceptions

*   **`InvalidEmailException`**：當電子郵件格式無效時拋出。
*   **`InvalidDisplayNameException`**：當顯示名稱為空或無效時拋出。
*   **`DuplicateEmailException`**：當嘗試建立或更新已存在的電子郵件時拋出（由 Persistence Layer 捕捉並轉發至此例外，或在 Application Layer 預查後拋出，視實現策略而定；此處建議在 Port 實作層捕捉 DB 約束異常並轉為此例外，Application Layer 直接處理此例外）。
*   **`ProfileNotFoundException`**：當查詢的 ID 不存在時拋出。

## 2. Use Cases & Ports (Application Layer)

所有 Inbound Port 以 `UseCase` 結尾，Outbound Port 以 `Port` 結尾。Service 類別負責協調邏輯。

### Inbound Ports (`port.in`)

1.  **`CreateProfileUseCase`**
    *   `void create(CreateProfileRequest request)`
        *   參數 DTO: `record CreateProfileRequest(String email, String displayName)`
2.  **`GetProfileUseCase`**
    *   `GetProfileResponse get(UUID id)`
        *   回傳 DTO: `record GetProfileResponse(UUID id, String email, String displayName)`
3.  **`UpdateProfileUseCase`**
    *   `void update(UpdateProfileRequest request)`
        *   參數 DTO: `record UpdateProfileRequest(UUID id, String email, String displayName)`
4.  **`DeleteProfileUseCase`**
    *   `void delete(UUID id)`

### Outbound Ports (`port.out`)

1.  **`SaveProfilePort`**
    *   `Profile save(Profile profile)`
2.  **`LoadProfilePort`**
    *   `Optional<Profile> findById(UUID id)`
3.  **`ExistsByEmailPort`** (可選，或合併在 Save/Find 中處理)
    *   `boolean existsByEmail(String email)`
    *   *註：為簡化並符合單一職責，建議在 `SaveProfilePort` 實作時檢查唯一性約束，若衝突則拋出特定異常或在 Application Layer 使用 `existsByEmail`。此處採用明確的查詢方式以確保業務規則清晰：*
    *   `boolean existsByEmail(String email)`

### Use Case Implementations (`usecase`)

*   **`CreateProfileService`**: 實現 `CreateProfileUseCase`。驗證輸入 -> 建立 Domain Entity -> 呼叫 `saveProfilePort.save()`。
*   **`GetProfileService`**: 實現 `GetProfileUseCase`。呼叫 `loadProfilePort.findById(id)` -> 若無則拋出 `ProfileNotFoundException` -> 轉換為 Response DTO。
*   **`UpdateProfileService`**: 實現 `UpdateProfileUseCase`。先 `findById` 確認存在 -> 驗證新數據有效性（特別是 Email 是否與其他記錄重複）-> 調用 Entity 的更新方法 -> 呼叫 `saveProfilePort.save()`。
*   **`DeleteProfileService`**: 實現 `DeleteProfileUseCase`。先 `findById` 確認存在 -> 呼叫 `repository.deleteById(id)` (或透過 Port)。

## 3. Web Adapter (REST API)

套件：`com.github.chunpingwang.ollamaagentoutcome.adapter.web`

### Controller: `ProfileController`

*   `@RestController`
*   `@RequestMapping("/api/profiles")`
*   Endpoints:
    *   `POST /`: `create(@Valid @RequestBody CreateProfileRequest request)` -> 201 Created + Location Header.
    *   `GET /{id}`: `get(@PathVariable("id") UUID id)` -> 200 OK.
    *   `PUT /{id}`: `update(@PathVariable("id") UUID id, @Valid @RequestBody UpdateProfileRequest request)` -> 200 OK.
        *   *注意：Spec 中 PUT body 包含 email，即使路徑已有 ID。這意味著全量替換。需確保 Body 中的 ID 與 Path Variable 一致（可選檢查），或直接忽略 Body 中的 ID 以 Path Variable 為準，但通常 RESTful 設計建議 Body 不包含資源標識符，或者若包含則必須匹配。此處依 Spec "內容包含...email"，我們接受 Body 數據並用 Path Variable 的 ID 作為目標資源。*
    *   `DELETE /{id}`: `delete(@PathVariable("id") UUID id)` -> 204 No Content.

### DTOs (Data Transfer Objects)

*   `CreateProfileRequest` (`record`)
    *   Fields: `String email`, `String displayName`.
    *   Validation: Jakarta Validation annotations (`@Email`, `@NotBlank`).
*   `UpdateProfileRequest` (`record`)
    *   Fields: `String email`, `String displayName`.
    *   Validation: Jakarta Validation annotations (`@Email`, `@NotBlank`).
*   `GetProfileResponse` (`record`)
    *   Fields: `UUID id`, `String email`, `String displayName`.

### Error Handling: `GlobalExceptionHandler`

*   `@RestControllerAdvice`
*   Mappings:
    *   `MethodArgumentNotValidException` -> 400 Bad Request.
    *   `HttpMessageNotReadableException` -> 400 Bad Request.
    *   `DuplicateEmailException` -> 409 Conflict.
    *   `ProfileNotFoundException` -> 404 Not Found.
    *   *(Avoid generic Exception handler to prevent masking framework errors like 404 for wrong paths)*

## 4. Persistence Adapter (JPA)

套件：`com.github.chunpingwang.ollamaagentoutcome.adapter.persistence`

### Entity: `ProfileEntity`

*   Implements `Persistable<UUID>`.
*   Fields:
    *   `id`: UUID, Primary Key.
    *   `email`: String, Unique Constraint, Not Null.
    *   `displayName`: String, Not Null.
    *   `isNew`: boolean, Transient. Set to true on creation, false after persist/load via lifecycle callbacks or constructor logic.
        *   Implementation detail: Use a default constructor and setters for JPA proxying if necessary, but prefer immutable-like structure with explicit update methods in Domain. For JPA compatibility, standard fields are fine. The key is the `isNew()` implementation.
        ```java
        @Transient private boolean isNew = true;
        
        public ProfileEntity() { this.isNew = true; }
        
        // After save or load
        @PostLoad
        @PostPersist
        void markNotNew() { this.isNew = false; }
        
        @Override
        public boolean isNew() { return isNew; }
        ```

### Repository Interface: `ProfileRepository`

*   Extends `JpaRepository<ProfileEntity, UUID>` (or `CrudRepository`).
*   Custom method: `boolean existsByEmail(String email)`.

### Port Implementations (`port.out`)

1.  **`JpaSaveProfilePort`** implements `SaveProfilePort`
    *   Injects `ProfileRepository`.
    *   Converts Domain `Profile` -> `ProfileEntity`.
    *   Calls `repository.saveAndFlush(entity)` (flush to trigger unique constraint check immediately).
    *   Catches `DataIntegrityViolationException`, wraps as `DuplicateEmailException`.
2.  **`JpaLoadProfilePort`** implements `LoadProfilePort`
    *   Injects `ProfileRepository`.
    *   Finds by ID, converts Entity -> Domain `Profile`. Returns Optional.
3.  **`JpaExistsByEmailPort`** implements `ExistsByEmailPort`
    *   Injects `ProfileRepository`.
    *   Delegates to `repository.existsByEmail(email)`.

## 5. Bootstrap Configuration

套件：`com.github.chunpingwang.ollamaagentoutcome.bootstrap`

Class: `UseCaseConfiguration` (@Configuration)

Beans to define:
*   `CreateProfileService` (depends on SaveProfilePort)
*   `GetProfileService` (depends on LoadProfilePort)
*   `UpdateProfileService` (depends on LoadProfilePort, ExistsByEmailPort, SaveProfilePort)
*   `DeleteProfileService` (depends on LoadProfilePort, Repository/SavePort for delete logic - typically just needs a way to verify existence and then delete. Can reuse SavePort or add DeletePort. For simplicity, let's assume we can check existence via LoadPort and then call repository directly in the service if needed, OR better, have a `DeleteProfilePort`. Let's stick to existing ports: Use `LoadProfilePort` to find, then use a new `DeleteProfilePort`? Or simply inject `ProfileRepository` into the Service? No, Services should only talk to Ports. So, add **`DeleteProfilePort`** interface with `void delete(UUID id)`.)

*Correction*: To strictly follow Port pattern, I will add `DeleteProfilePort` to Application Layer.
*   New Inbound: `DeleteProfileUseCase.delete(id)`
*   New Outbound: `DeleteProfilePort.delete(id)`
*   Implementation: `JpaDeleteProfilePort` implements `DeleteProfilePort`, calls `repository.deleteById(id)`.

Beans:
1.  `saveProfilePort()` -> `new JpaSaveProfilePort(profileRepository)`
2.  `loadProfilePort()` -> `new JpaLoadProfilePort(profileRepository)`
3.  `existsByEmailPort()` -> `new JpaExistsByEmailPort(profileRepository)`
4.  `deleteProfilePort()` -> `new JpaDeleteProfilePort(profileRepository)`
5.  `createProfileService(SaveProfilePort port)`
6.  `getProfileService(LoadProfilePort port)`
7.  `updateProfileService(LoadProfilePort loadPort, ExistsByEmailPort existsPort, SaveProfilePort savePort)`
8.  `deleteProfileService(DeleteProfilePort port)` (Note: Need to check existence first? Usually Delete throws if not found. So Service checks LoadPort, then calls DeletePort).

## 6. Testing Strategy

### Unit Tests (Domain & Application)
*   **Location**: `domain/src/test/java/...` and `application/src/test/java/...`
*   **Tools**: JUnit Jupiter, AssertJ, Mockito. No Spring Context.
*   **Focus**:
    *   Domain Value Object validation logic.
    *   Use Case business rules (e.g., preventing duplicate emails in Update scenario).
    *   Mocking Outbound Ports (`@MockBean` or constructor injection with mocks).

### Integration Tests (Persistence Adapter)
*   **Location**: `adapter-persistence/src/test/java/...`
*   **Tools**: `@DataJpaTest`, Testcontainers PostgreSQL, `@AutoConfigureTestDatabase(replace = NONE)`.
*   **Focus**:
    *   Entity mapping correctness.
    *   Repository queries (`existsByEmail`).
    *   Port implementations (`JpaSaveProfilePort`) verifying constraint violations are caught and wrapped correctly.

### Web Controller Tests
*   **Location**: `adapter-web/src/test/java/...`
*   **Tools**: `@WebMvcTest(ProfileController.class)`, `@MockitoBean` for all Inbound Ports.
*   **Focus**:
    *   HTTP Status codes (201, 400, 404, 409, 204).
    *   Request/Response JSON structure.
    *   Validation error messages format.
    *   Location header presence on creation.

### BDD / Cucumber Tests
*   **Location**: `bootstrap/src/test/resources/features/personal-profile-crud.feature`
*   **Steps**: `com.github.chunpingwang.ollamaagentoutcome.bdd.ProfileStepDefinitions`
*   **Tools**: Spring Boot Test Context with Testcontainers, `TestRestTemplate`.
*   **Focus**: End-to-end flow of all scenarios defined in Spec.

## 7. Class List

| Module | Package | Class Name | Responsibility |
| :--- | :--- | :--- | :--- |
| Domain | ...domain | `ProfileId` | Value Object: Profile UUID identifier. |
| Domain | ...domain | `Email` | Value Object: Validated email address string. |
| Domain | ...domain | `DisplayName` | Value Object: Non-blank display name string. |
| Domain | ...domain | `Profile` | Entity: Core business object holding profile data and logic. |
| Domain | ...domain.exception | `InvalidEmailException` | Domain Exception: Invalid email format. |
| Domain | ...domain.exception | `InvalidDisplayNameException` | Domain Exception: Invalid display name (blank). |
| Domain | ...domain.exception | `DuplicateEmailException` | Domain Exception: Email already exists globally. |
| Domain | ...domain.exception | `ProfileNotFoundException` | Domain Exception: Profile ID not found. |
| Application | ...port.in | `CreateProfileUseCase` | Inbound Port Interface: Create new profile request. |
| Application | ...port.in | `GetProfileUseCase` | Inbound Port Interface: Get profile by ID. |
| Application | ...port.in | `UpdateProfileUseCase` | Inbound Port Interface: Update existing profile. |
| Application | ...port.in | `DeleteProfileUseCase` | Inbound Port Interface: Delete profile by ID. |
| Application | ...port.out | `SaveProfilePort` | Outbound Port Interface: Save/Update a profile entity. |
| Application | ...port.out | `LoadProfilePort` | Outbound Port Interface: Load a profile by ID. |
| Application | ...port.out | `ExistsByEmailPort` | Outbound Port Interface: Check if email is taken. |
| Application | ...port.out | `DeleteProfilePort` | Outbound Port Interface: Delete a profile by ID. |
| Application | ...usecase | `CreateProfileService` | Use Case Impl: Orchestrates creation logic. |
| Application | ...usecase | `GetProfileService` | Use Case Impl: Orchestrates retrieval logic. |
| Application | ...usecase | `UpdateProfileService` | Use Case Impl: Orchestrates update logic (validation + save). |
| Application | ...usecase | `DeleteProfileService` | Use Case Impl: Orchestrates deletion logic (check existence + delete). |
| Adapter-Web | ...adapter.web | `ProfileController` | REST Controller: Exposes HTTP endpoints for Profile CRUD. |
| Adapter-Web | ...adapter.web | `GlobalExceptionHandler` | Exception Handler: Maps Domain Exceptions to HTTP Responses. |
| Adapter-Persistence | ...adapter.persistence | `ProfileEntity` | JPA Entity: Database mapping for Profile, implements Persistable. |
| Adapter-Persistence | ...adapter.persistence | `ProfileRepository` | Spring Data Repository: Accesses ProfileEntity from DB. |
| Adapter-Persistence | ...adapter.persistence | `JpaSaveProfilePort` | Persistence Impl: Saves entity and handles constraint violations. |
| Adapter-Persistence | ...adapter.persistence | `JpaLoadProfilePort` | Persistence Impl: Loads entity by ID. |
| Adapter-Persistence | ...adapter.persistence | `JpaExistsByEmailPort` | Persistence Impl: Checks email uniqueness. |
| Adapter-Persistence | ...adapter.persistence | `JpaDeleteProfilePort` | Persistence Impl: Deletes entity by ID. |
| Bootstrap | ...bootstrap | `UseCaseConfiguration` | Configuration: Wires Application Services with Port Implementations. |
