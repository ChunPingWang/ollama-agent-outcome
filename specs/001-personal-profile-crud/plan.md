# Plan 001 — 個人資料管理 (Personal Profile CRUD)

本文件定義了 Spec 001 的技術實現細節，遵循六角形架構（Hexagonal Architecture），確保領域邏輯獨立於基礎設施，並透過明確的 Port 進行通訊。

## 1. 領域模型 (Domain Model)

### Value Objects & Entities

- **ProfileId** (`record`)
    - 內容：`UUID value`
    - 不變條件：不可為 null。
    - 用途：作為 Domain Entity 的唯一識別符，也用於 JPA Entity 的主鍵映射。

- **Email** (`record`)
    - 內容：`String address`
    - 不變條件：
        - 不可為 null 或空白。
        - 必須符合標準 Email 格式（使用正則表達式驗證）。
    - 用途：封裝電子郵件的有效性檢查，確保領域內所有 Email 均有效。

- **DisplayName** (`record`)
    - 內容：`String name`
    - 不變條件：
        - 可為 null 或空字串。
        - 若僅含空白字符，視為空值處理（null）。
        - 長度限制：最大 50 個字元。
    - 用途：封裝顯示名稱的清洗與驗證邏輯。

- **Profile** (`class`, immutable fields via constructor, mutable state encapsulated in methods)
    - 欄位：
        - `id: ProfileId` (唯一識別碼)
        - `email: Email` (電子郵件)
        - `displayName: DisplayName` (顯示名稱)
    - 業務規則：
        - 建立時需驗證 Email 和 Display Name 的有效性。
        - 更新時，允許修改 Display Name；Email 在更新情境下通常由 Controller/UseCase 決定是否允許變更（根據 Spec US-3 暗示全量更新包含 email，但為了數據一致性，通常建議 email 不可變或嚴格控制。本設計採用：**Update 操作接受新的 Email DTO，但在 Domain 層面，我們將其視為「替換」整個實體狀態，或者更精確地說，Port 介面將傳遞完整的 Profile 資料進行覆寫**。*修正思考*：Spec US-3 提到 "update full profile info"，且 PUT 請求包含 email。這意味著 Update 是**全量替換**。因此，Domain Entity 提供一個 `withUpdatedInfo(...)` 方法或直接透過建構子重建新實例（若採用不可變模式）。為簡化 JPA 管理，我們將 Profile 設為可變的 Value Object 集合體，或由 Service 層負責組裝。
        - **最終決策**：Profile 是一個簡單的 POJO/Record 組合。由於需要與 JPA Entity 映射，我們在 Adapter 層處理轉換。Domain Layer 主要負責驗證邏輯。

### Domain Exceptions

- **InvalidEmailException**: 當 Email 格式不正確時拋出。
- **DuplicateEmailException**: 當嘗試建立或更新導致電子郵件重複時拋出（由 Port 層捕捉並轉發，或在 Application 層檢查後拋出？通常唯一性約束在 DB 層，故由 Persistence Adapter 捕捉 DataIntegrityViolationException 轉為此例外）。
- **ProfileNotFoundException**: 當查詢或刪除的 ID 不存在時拋出。

## 2. Use Cases & Ports (Application Layer)

所有 Inbound Port 介面以 `UseCase` 結尾，Outbound Port 介面以 `Port` 結尾。

### US-1: CreateProfileUseCase
- **Inbound Port Interface**: `CreateProfileUseCase`
    - Method: `void execute(CreateProfileRequest request)`
- **Outbound Port Interface**: `SaveProfilePort`
    - Method: `void save(Profile profile)`
- **Implementation Class**: `CreateProfileService`
    - 職責：接收 DTO，驗證輸入（可在此或 Validator），呼叫 `save`。若發生 `DataIntegrityViolation` 對應的 domain exception，則向上拋出。

### US-2: GetProfileUseCase
- **Inbound Port Interface**: `GetProfileUseCase`
    - Method: `Optional<Profile> findById(ProfileId id)`
- **Outbound Port Interface**: `LoadProfilePort`
    - Method: `Optional<Profile> loadById(ProfileId id)`
- **Implementation Class**: `GetProfileService`
    - 職責：呼叫 `loadById`，若為空則拋出 `ProfileNotFoundException`（由 Controller 處理為 404）。

### US-3: UpdateProfileUseCase
- **Inbound Port Interface**: `UpdateProfileUseCase`
    - Method: `void update(UpdateProfileRequest request)`
- **Outbound Ports**:
    - `LoadProfilePort`: `Optional<Profile> loadById(ProfileId id)` (用於檢查存在性)
    - `SaveProfilePort`: `void save(Profile profile)`
- **Implementation Class**: `UpdateProfileService`
    - 職責：先 `loadById` 確認存在，若不存在拋出 `ProfileNotFoundException`；然後組裝新的 Profile 實體並呼叫 `save`。注意：此處為全量更新，故直接替換狀態。

### US-4: DeleteProfileUseCase
- **Inbound Port Interface**: `DeleteProfileUseCase`
    - Method: `void delete(ProfileId id)`
- **Outbound Ports**:
    - `LoadProfilePort`: `Optional<Profile> loadById(ProfileId id)` (用於檢查存在性)
    - `DeleteProfilePort`: `void deleteById(ProfileId id)`
- **Implementation Class**: `DeleteProfileService`
    - 職責：先 `loadById` 確認存在，若不存在拋出 `ProfileNotFoundException`；然後呼叫 `deleteById`。

## 3. Web Adapter (REST API)

套件：`com.github.chunpingwang.ollamaagentoutcome.adapter.web`

### Controller: `ProfileController`
- Base Path: `/api/profiles`
- Dependencies: Injected Inbound Ports (`CreateProfileUseCase`, `GetProfileUseCase`, etc.) via Constructor.

#### Endpoints & DTOs

1.  **POST /api/profiles**
    -   Request Body: `CreateProfileRequest` (record)
        ```java
        record CreateProfileRequest(@Email String email, @Size(max = 50) String displayName) {}
        ```
    -   Response: HTTP 201 Created + Location Header.
    -   Error Handling:
        -   Validation Fail -> 400 Bad Request.
        -   Duplicate Email -> 409 Conflict.

2.  **GET /api/profiles/{id}**
    -   Path Variable: `@PathVariable("id") UUID id`
    -   Response Body: `ProfileResponse` (record)
        ```java
        record ProfileResponse(UUID id, String email, String displayName) {}
        ```
    -   Status: 200 OK.
    -   Error Handling: Not Found -> 404 Not Found.

3.  **PUT /api/profiles/{id}**
    -   Path Variable: `@PathVariable("id") UUID id`
    -   Request Body: `UpdateProfileRequest` (record)
        ```java
        // Note: Spec says "full update", so we include all fields or allow null for optional ones? 
        // Spec US-3 example shows sending both email and displayName.
        // We will require both to ensure full state replacement as per spec implication of "full profile info".
        record UpdateProfileRequest(@Email String email, @Size(max = 50) String displayName) {}
        ```
    -   Response: HTTP 200 OK (Empty body or updated entity? REST convention often returns 200 with updated resource or just 200. Spec doesn't specify response body content for PUT success, but GET follows up. Let's return 200 OK).
    -   Error Handling:
        -   Validation Fail -> 400 Bad Request.
        -   Not Found -> 404 Not Found.
        -   Duplicate Email -> 409 Conflict.

4.  **DELETE /api/profiles/{id}**
    -   Path Variable: `@PathVariable("id") UUID id`
    -   Response: HTTP 204 No Content.
    -   Error Handling: Not Found -> 404 Not Found.

### Exception Handler: `GlobalExceptionHandler`
-   `@RestControllerAdvice`
-   Maps Domain Exceptions to HTTP Status Codes:
    -   `InvalidEmailException` -> 400
    -   `DuplicateEmailException` -> 409
    -   `ProfileNotFoundException` -> 404
    -   `MethodArgumentNotValidException` -> 400
    -   `HttpMessageNotReadableException` -> 400

## 4. Persistence Adapter (JPA)

套件：`com.github.chunpingwang.ollamaagentoutcome.adapter.persistence`

### JPA Entity: `ProfileEntity`
-   Table: `profiles`
-   Fields:
    -   `id`: `UUID`, Primary Key, Generated by application (UUID). Implements `Persistable<UUID>`? 
        -   *Decision*: Since we generate UUID in Java (`UUID.randomUUID()`), we do NOT use `@GeneratedValue`. We implement `Persistable<UUID>` with a transient `isNew` flag to help Spring Data know if it's an insert or update, although for simple CRUD where ID is always present and unique, standard save works if we handle the "new" state correctly. However, Spec mentions avoiding optimistic locking issues. The safest way for pre-generated IDs in Spring Data JPA without `@GeneratedValue` is often just using `save()`. If `saveAndFlush` is used as per persistence traps note, we need to ensure the entity isn't treated as detached incorrectly. 
        -   Actually, if we pass a new object with a generated ID that doesn't exist in DB, `save()` will perform an INSERT. This is correct behavior. No special `Persistable` implementation needed unless we are mixing managed/detached states confusingly. But to be safe against `ObjectOptimisticLockingFailureException` mentioned in tips, we can stick to standard flow: New Object -> Save (Insert). Existing Object -> Load then Update fields OR Replace Entity.
    -   `email`: `String`, Unique Constraint, Not Null.
    -   `display_name`: `String`, Nullable.

### Repository Interface: `ProfileRepository`
-   Extends `JpaRepository<ProfileEntity, UUID>`
-   Custom Method: `Optional<ProfileEntity> findByEmail(String email)` (for duplicate check if needed at repo level, though Port handles logic).

### Port Implementations

1.  **SaveProfilePortImpl** (`implements SaveProfilePort`)
    -   Injects `ProfileRepository`.
    -   Converts Domain `Profile` to JPA `ProfileEntity`.
    -   Calls `repository.saveAndFlush(entity)`.
    -   Catches `DataIntegrityViolationException` and re-throws as `DuplicateEmailException`.

2.  **LoadProfilePortImpl** (`implements LoadProfilePort`)
    -   Injects `ProfileRepository`.
    -   Converts JPA `ProfileEntity` to Domain `Profile`.
    -   Returns `Optional.empty()` if not found.

3.  **DeleteProfilePortImpl** (`implements DeleteProfilePort`)
    -   Injects `ProfileRepository`.
    -   Calls `repository.deleteById(id)`.

## 5. Bootstrap Configuration

套件：`com.github.chunpingwang.ollamaagentoutcome.bootstrap`

Class: `UseCaseConfiguration` (`@Configuration`)
-   Defines all Application Layer Services as Beans.
-   Example:
    ```java
    @Bean
    CreateProfileService createProfileService(SaveProfilePort savePort) { ... }
    
    @Bean
    GetProfileService getProfileService(LoadProfilePort loadPort) { ... }
    
    // ... other services
    ```
-   Note: Ports are implemented in the persistence adapter module, which is a dependency of bootstrap (via application). Spring Boot component scanning will find them if they are annotated with `@Component`, OR we explicitly define beans for Port implementations here too? 
    -   *Correction*: The prompt says "application layer... cannot use Spring annotations". So Port Implementations MUST be in `adapter-persistence`. They should be marked with `@Component` or defined in a configuration class within that module. Since `bootstrap` imports configurations from dependencies, it's cleaner to have an `AdapterPersistenceAutoConfiguration` in `adapter-persistence` or just rely on Component Scanning if `@SpringBootApplication` scans base packages correctly. Given the strict modular structure, defining beans in `UseCaseConfiguration` usually refers to Use Cases. For Adapters, since they depend on Infrastructure (DB), they might need their own config or simply be picked up by component scan if placed under the main package. To avoid ambiguity and ensure separation, I will assume standard Component Scan covers all modules under `{base_package}`. Thus, Port Implementations will have `@Repository` annotation.

## 6. Testing Strategy

### Unit Tests (JUnit + Mockito)
-   **Domain**: Test Value Object validation rules (e.g., invalid email format).
-   **Application**: 
    -   `CreateProfileServiceTest`: Verify port calls, exception mapping.
    -   `GetProfileServiceTest`: Verify existence check and exception throwing.
    -   Mock Outbound Ports using Mockito. No Spring context.

### Integration Tests (Web Adapter)
-   **Class**: `ProfileControllerIT` (`@WebMvcTest(ProfileController.class)`)
-   Uses `MockitoBean` for Inbound Ports.
-   Verifies HTTP status codes, request/response bodies, and headers.
-   Does NOT touch database.

### Persistence Tests
-   **Class**: `JpaPersistenceIntegrationTest` (`@DataJpaTest` + Testcontainers PostgreSQL)
-   Tests `SaveProfilePortImpl`, `LoadProfilePortImpl`.
-   Verifies DB constraints (Unique Email), CRUD operations, and entity conversion.
-   Uses `@AutoConfigureTestDatabase(replace = NONE)` to use Testcontainers.

### BDD / Cucumber Tests
-   Feature File: `bootstrap/src/test/resources/features/personal-profile-crud.feature`
-   Step Definitions: `com.github.chunpingwang.ollamaagentoutcome.bdd.ProfileSteps`
-   Runs against a real embedded API (using `TestRestTemplate`).
-   Scenario 1: Create Profile Success -> Check 201 & Location.
-   Scenario 2: Duplicate Email -> Check 409.
-   Scenario 3: Invalid Email Format -> Check 400.
-   Scenario 4: Get Existing Profile -> Check 200 & Data.
-   Scenario 5: Get Non-existent Profile -> Check 404.
-   Scenario 6: Update Profile -> Check 200 & Updated Data via subsequent GET.
-   Scenario 7: Delete Existing Profile -> Check 204 & Subsequent 404.
-   Scenario 8: Delete Non-existent Profile -> Check 404.

## 7. Class List

| Module | Path (Relative) | Responsibility |
| :--- | :--- | :--- |
| **Domain** | `domain/.../domain/valueobject/ProfileId.java` | Value Object for UUID ID. |
| **Domain** | `domain/.../domain/valueobject/Email.java` | Value Object with email validation. |
| **Domain** | `domain/.../domain/valueobject/DisplayName.java` | Value Object with display name cleaning/validation. |
| **Domain** | `domain/.../domain/entity/Profile.java` | Domain Entity holding state and business rules. |
| **Domain** | `domain/.../domain/exception/InvalidEmailException.java` | Exception for invalid email format. |
| **Domain** | `domain/.../domain/exception/DuplicateEmailException.java` | Exception for duplicate email constraint violation. |
| **Domain** | `domain/.../domain/exception/ProfileNotFoundException.java` | Exception when profile is not found. |
| **Application** | `application/.../port/in/CreateProfileUseCase.java` | Inbound Port interface for creating profiles. |
| **Application** | `application/.../usecase/CreateProfileService.java` | Implementation of Create Profile logic. |
| **Application** | `application/.../port/out/SaveProfilePort.java` | Outbound Port interface for saving profiles. |
| **Application** | `application/.../port/in/GetProfileUseCase.java` | Inbound Port interface for getting a profile. |
| **Application** | `application/.../usecase/GetProfileService.java` | Implementation of Get Profile logic. |
| **Application** | `application/.../port/out/LoadProfilePort.java` | Outbound Port interface for loading profiles. |
| **Application** | `application/.../port/in/UpdateProfileUseCase.java` | Inbound Port interface for updating profiles. |
| **Application** | `application/.../usecase/UpdateProfileService.java` | Implementation of Update Profile logic. |
| **Application** | `application/.../port/in/DeleteProfileUseCase.java` | Inbound Port interface for deleting profiles. |
| **Application** | `application/.../usecase/DeleteProfileService.java` | Implementation of Delete Profile logic. |
| **Application** | `application/.../port/out/DeleteProfilePort.java` | Outbound Port interface for deleting profiles. |
| **Web Adapter** | `adapter-web/.../web/controller/ProfileController.java` | REST Controller handling HTTP requests/responses. |
| **Web Adapter** | `adapter-web/.../web/dto/CreateProfileRequest.java` | DTO for Create request body. |
| **Web Adapter** | `adapter-web/.../web/dto/UpdateProfileRequest.java` | DTO for Update request body. |
| **Web Adapter** | `adapter-web/.../web/dto/ProfileResponse.java` | DTO for API response bodies. |
| **Web Adapter** | `adapter-web/.../web/handler/GlobalExceptionHandler.java` | Handles exceptions and maps to HTTP status codes. |
| **Persistence** | `adapter-persistence/.../persistence/entity/ProfileEntity.java` | JPA Entity mapped to 'profiles' table. |
| **Persistence** | `adapter-persistence/.../persistence/repository/ProfileRepository.java` | Spring Data Repository interface. |
| **Persistence** | `adapter-persistence/.../persistence/port/SaveProfilePortImpl.java` | Implements SaveProfilePort, handles DB save & constraint checks. |
| **Persistence** | `adapter-persistence/.../persistence/port/LoadProfilePortImpl.java` | Implements LoadProfilePort, handles DB load & conversion. |
| **Persistence** | `adapter-persistence/.../persistence/port/DeleteProfilePortImpl.java` | Implements DeleteProfilePort, handles DB delete. |
| **Bootstrap** | `bootstrap/.../bootstrap/config/UseCaseConfiguration.java` | Configures Application Layer Services as Beans. |
