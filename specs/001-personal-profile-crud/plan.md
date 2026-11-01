# Plan 001 — 個人資料管理 (Personal Profile CRUD)

本計畫基於 Spec 001 設計，採用六角形架構（Hexagonal Architecture），確保領域邏輯獨立於框架與基礎設施。

## 1. 領域模型 (Domain Model)

### Value Objects & Entities
- **ProfileId** (`record`)
    - 屬性：`String value` (UUID String)。
    - 不變條件：非空、符合 UUID 格式。
- **Email** (`record`)
    - 屬性：`String address`。
    - 不變條件：非空白、符合 RFC 5322 基本格式（使用正則表達式驗證）。
    - 行為：`isValid()` 檢查格式；`equals` 比較時忽略大小寫以支援唯一性約束。
- **DisplayName** (`record`)
    - 屬性：`String name`。
    - 不變條件：允許為 null 或僅含空白字符（視為未設定）。
- **Profile** (`class`, Domain Entity)
    - 屬性：`id: ProfileId`, `email: Email`, `displayName: DisplayName`。
    - 不變條件：電子郵件在全域必須唯一（由 Port/Repository 層強制，但 Domain 需能識別重複概念）。
    - 行為：
        - `updateDisplayName(newName: DisplayName)`：更新顯示名稱。
        - `delete()`：標記刪除狀態（或直接由 Repository 物理刪除，視實現而定，此處採用物理刪除模式以簡化 CUD 邏輯，Domain 提供 `isDeleted` 標誌或僅作為業務規則觸發點，但在本 Spec 中 "永久刪除" 暗示物理移除，故 Domain 主要負責數據完整性與狀態轉換，實際 DB 操作在 Adapter）。*修正策略*：為了符合 JPA 簡單實踐且避免複雜的軟刪除邏輯（Spec 未要求），我們將使用硬刪除。Domain 實體本身不包含持久化註解，僅包含業務邏輯。

### Domain Exceptions
- **InvalidEmailException**：當提供的電子郵件格式無效時拋出。
- **DuplicateEmailException**：當嘗試建立具有已存在電子郵件的新個人資料時拋出（通常由 Persistence Layer 捕捉並轉為此例外，或在 SavePort 檢查後拋出）。
- **ProfileNotFoundException**：當查詢或更新不存在的 ID 時拋出。

## 2. Use Cases & Ports (Application Layer)

### Inbound Ports (Interfaces ending with `UseCase`)
1. **CreateProfileUseCase**
    - `execute(CreateProfileRequest request): ProfileId`
2. **GetProfileUseCase**
    - `execute(ProfileId id): Profile`
3. **UpdateProfileDisplayNameUseCase**
    - `execute(ProfileId id, DisplayName newDisplayName)`
4. **DeleteProfileUseCase**
    - `execute(ProfileId id)`

### Outbound Ports (Interfaces ending with `Port`)
1. **SaveProfilePort**
    - `save(Profile profile): void`
    - *注意*：需處理唯一性約束衝突。
2. **FindProfileByIdPort**
    - `findById(ProfileId id): Optional<Profile>`
3. **DeleteProfileByIdPort**
    - `deleteById(ProfileId id): void`

### Use Case Implementations (Classes ending with `Service`)
- `CreateProfileService`：實現 `CreateProfileUseCase`。驗證輸入（可委託 Domain），呼叫 `SaveProfilePort`，若發生重複則捕捉並重拋或轉換為 Domain Exception。
- `GetProfileService`：實現 `GetProfileUseCase`。呼叫 `FindProfileByIdPort`，若不存在拋出 `ProfileNotFoundException`。
- `UpdateProfileDisplayNameService`：實現 `UpdateProfileDisplayNameUseCase`。先查詢實體，更新顯示名稱，再儲存。
- `DeleteProfileService`：實現 `DeleteProfileUseCase`。先檢查是否存在，存在則刪除。

## 3. Web Adapter (adapter-web)

### Controller
- **ProfileController** (`@RestController`, path: `/api/profiles`)
    - `POST /{id}` -> No, POST is collection level.
    - `POST /` -> `create(@Valid @RequestBody CreateProfileRequest request)`
        - Returns: 201 Created + Location header.
    - `GET /{id}` -> `get(@PathVariable("id") String id)`
        - Returns: ProfileResponse DTO or 404.
    - `PUT /{id}` -> `update(@PathVariable("id") String id, @Valid @RequestBody UpdateProfileRequest request)`
        - Returns: 200 OK.
    - `DELETE /{id}` -> `delete(@PathVariable("id") String id)`
        - Returns: 204 No Content.

### Request/Response DTOs
- **CreateProfileRequest**: `email` (String), `displayName` (String). Jakarta Validation annotations for email format and not blank.
- **UpdateProfileRequest**: `displayName` (String). Optional validation if displayName must be provided on update? Spec says "update... such as display name", implies it's the field to change. Let's assume it can be null/empty to clear it, but usually PUT requires full state or specific fields. Given spec simplicity, we allow updating just displayName. If null, set to empty/null in domain.
- **ProfileResponse**: `id` (String), `email` (String), `displayName` (String).

### Error Handling (`@RestControllerAdvice`)
- `InvalidEmailException` -> 400 Bad Request.
- `DuplicateEmailException` -> 409 Conflict.
- `ProfileNotFoundException` -> 404 Not Found.
- `MethodArgumentNotValidException` -> 400 Bad Request (Standard Spring behavior).

## 4. Persistence Adapter (adapter-persistence)

### JPA Entity
- **ProfileEntity** (`@Entity`, implements `Persistable<UUID>`)
    - Fields: `uuid id` (Primary Key), `email address`, `display_name`.
    - Strategy: Since Domain generates UUIDs, use `@GeneratedValue` is NOT used for ID generation logic if we pass UUID from client/domain, BUT standard practice often lets DB generate or app generate. Spec doesn't specify who generates ID. Usually, Client sends nothing on POST, Server generates ID. However, REST best practices often return the created resource with its ID. Let's assume Application/Domain generates the UUID upon creation request processing, or Repository generates it? 
    - *Correction*: In Hexagonal, Domain usually owns business IDs. If external system assigns ID, it's different. Here, "Create new profile" implies server creates it. We will let the Application layer create a `ProfileId` (UUID) before calling SavePort, OR let JPA handle it via `GenerationType.UUID` but mapped to a specific generator. To keep Domain pure and simple, let's have the `CreateProfileService` generate the UUID using `java.util.UUID.randomUUID()` and wrap in `ProfileId`. The Entity will map this UUID directly without auto-generation. Thus, Entity needs `Persistable` interface as per System Prompt rules for custom IDs.

### Spring Data Repository
- **ProfileRepository** (`extends JpaRepository<ProfileEntity, UUID>`)
    - Method: `Optional<ProfileEntity> findByEmail(String email)` (for duplicate check).
    - Note: Uniqueness constraint should be defined at DB level (`@Column(unique=true)`). Port implementation catches `DataIntegrityViolationException`.

### Port Implementations
- **JpaSaveProfilePort**: Implements `SaveProfilePort`. Converts Domain Profile -> Entity. Calls `repository.saveAndFlush(entity)`. Catches `DataIntegrityViolationException` on unique constraint violation and throws `DuplicateEmailException`.
- **JpaFindProfileByIdPort**: Implements `FindProfileByIdPort`. Calls `repository.findById(id)`, converts Entity -> Domain Profile.
- **JpaDeleteProfileByIdPort**: Implements `DeleteProfileByIdPort`. Finds by ID, if present deletes, else does nothing or throws? Spec says 404 on delete non-existent. So it must find first to verify existence before deleting, or catch exception from delete. Finding first is safer for returning correct HTTP status in Adapter layer logic flow, but Port can just throw if not found. Let's have the Service check existence via FindPort then call DeletePort, or DeletePort checks and throws. Given separation, let's make `DeleteProfileByIdPort` simply execute deletion. If entity doesn't exist, JPA might not throw immediately depending on flush strategy. Better: `deleteById` returns void. We need to ensure 404. The `DeleteProfileService` will likely use `FindProfileByIdPort` to check existence (or rely on a method that verifies), then call `DeleteProfileByIdPort`. Or simpler: `DeleteProfileByIdPort` finds and deletes, throwing `EntityNotFoundException` if missing.

### Converter/Translator
- Internal helper methods within Ports or separate converter classes to map between `Profile` <-> `ProfileEntity`. Since no MapStruct/Lombok, manual mapping in Ports or small static utility class in persistence module.

## 5. Bootstrap

- **UseCaseConfiguration** (`@Configuration`)
    - Defines beans for all Inbound Use Cases (`CreateProfileUseCase`, etc.) by instantiating their Service implementations with required Outbound Ports injected.
    - Example: `@Bean CreateProfileUseCase createProfileUseCase(SaveProfilePort savePort) { return new CreateProfileService(savePort); }`
- No other configurations needed for this feature specifically. Standard Spring Boot auto-config handles Web, DataJpa, Validation.

## 6. Testing Strategy

- **Domain Tests**: Pure JUnit + AssertJ. Test Value Object validation (Email format, ProfileId validity). Test Domain Entity business rules (e.g., updating display name).
- **Application Tests**: Pure JUnit + Mockito. Mock Outbound Ports. Verify correct port calls and exception handling (e.g., DuplicateEmailException propagation).
- **Persistence Adapter Tests**: `@DataJpaTest` + Testcontainers PostgreSQL. Test Repository constraints (unique email). Test Port implementations (conversion logic, duplicate detection via constraint violation).
- **Web Adapter Tests**: `@WebMvcTest`. Mock the Application Layer Use Cases (Inbound Ports). Verify HTTP status codes, request/response JSON structure, and error mapping.
- **BDD/Cucumber Tests**: Full integration tests using `@SpringBootTest` + Testcontainers. Feature file: `bootstrap/src/test/resources/features/personal-profile-crud.feature`. Steps use `TestRestTemplate` to hit actual endpoints.

## 7. Class List

| Module | Path (Relative to Root) | Responsibility |
| :--- | :--- | :--- |
| domain | `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/ProfileId.java` | Value object for unique profile identifier. |
| domain | `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/Email.java` | Value object with validation for email address. |
| domain | `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/DisplayName.java` | Value object for user's display name. |
| domain | `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/Profile.java` | Domain entity representing a personal profile. |
| domain | `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/InvalidEmailException.java` | Exception for invalid email format. |
| domain | `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/DuplicateEmailException.java` | Exception for duplicate email constraint violation. |
| domain | `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/ProfileNotFoundException.java` | Exception when profile is not found by ID. |
| application | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/in/CreateProfileUseCase.java` | Inbound port interface for creating profiles. |
| application | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/in/GetProfileUseCase.java` | Inbound port interface for retrieving profiles. |
| application | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/in/UpdateProfileDisplayNameUseCase.java` | Inbound port interface for updating display names. |
| application | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/in/DeleteProfileUseCase.java` | Inbound port interface for deleting profiles. |
| application | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/out/SaveProfilePort.java` | Outbound port for saving profile data. |
| application | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/out/FindProfileByIdPort.java` | Outbound port for finding a profile by ID. |
| application | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/out/DeleteProfileByIdPort.java` | Outbound port for deleting a profile by ID. |
| application | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/CreateProfileService.java` | Implementation of CreateProfileUseCase. |
| application | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/GetProfileService.java` | Implementation of GetProfileUseCase. |
| application | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/UpdateProfileDisplayNameService.java` | Implementation of UpdateProfileDisplayNameUseCase. |
| application | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/DeleteProfileService.java` | Implementation of DeleteProfileUseCase. |
| adapter-web | `adapter-web/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/ProfileController.java` | REST Controller handling HTTP requests/responses. |
| adapter-web | `adapter-web/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/dto/CreateProfileRequest.java` | DTO for creating new profiles. |
| adapter-web | `adapter-web/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/dto/UpdateProfileRequest.java` | DTO for updating profile display name. |
| adapter-web | `adapter-web/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/dto/ProfileResponse.java` | DTO for returning profile data. |
| adapter-web | `adapter-web/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/WebExceptionHandler.java` | Global exception handler mapping exceptions to HTTP status codes. |
| adapter-persistence | `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/ProfileEntity.java` | JPA Entity mapped to database table. |
| adapter-persistence | `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/ProfileRepository.java` | Spring Data Repository interface. |
| adapter-persistence | `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/JpaSaveProfilePort.java` | Implementation of SaveProfilePort using JPA. |
| adapter-persistence | `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/JpaFindProfileByIdPort.java` | Implementation of FindProfileByIdPort using JPA. |
| adapter-persistence | `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/JpaDeleteProfileByIdPort.java` | Implementation of DeleteProfileByIdPort using JPA. |
| bootstrap | `bootstrap/src/main/java/com/github/chunpingwang/ollamaagentoutcome/bootstrap/UseCaseConfiguration.java` | Configuration class wiring Application layer beans. |
