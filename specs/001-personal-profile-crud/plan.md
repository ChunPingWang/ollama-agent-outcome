# Plan 001 — 個人資料管理 (Personal Profile CRUD)

本計畫基於 Spec 001 設計六角形架構的實現細節。所有程式碼將嚴格遵循 `com.github.chunpingwang.ollamaagentoutcome` 基底套件結構。

## 1. 領域模型 (Domain Model)

### Value Objects & Entities
由於識別碼由外部系統或 UUID 生成邏輯決定，且需配合 JPA 的 `Persistable` 接口以處理自動生成的 ID 狀態，我們定義以下核心型別：

*   **`ProfileId`** (`record`)
    *   屬性：`String id` (UUID String)。
    *   不變條件：不可為 null、不可為空白。
*   **`Email`** (`record`)
    *   屬性：`String value`。
    *   不變條件：符合 RFC 5322 基本格式（使用正則表達式驗證），全域唯一性由 Repository Port 保證，但 VO 層負責格式檢查。
*   **`DisplayName`** (`record`)
    *   屬性：`String value`。
    *   不變條件：允許為 null 或空字串（表示未設定）。若輸入僅含空白字符，視為 null/empty。
*   **`Profile`** (`class`, Domain Entity)
    *   屬性：`id: ProfileId`, `email: Email`, `displayName: DisplayName`。
    *   行為：
        *   `create(id, email, displayName)`：靜態工廠方法，建立新實體。
        *   `update(email, displayName)`：更新顯示名稱與電子郵件。需檢查新的 Email 是否與當前不同且有效。
        *   `delete()`：標記為刪除狀態（或直接移除，視持久化策略而定，此處採用物理刪除概念，但在領域層僅作為業務動作觸發點）。

### Domain Exceptions
*   **`InvalidEmailException`**：當 Email 格式無效時拋出 (對應 HTTP 400)。
*   **`DuplicateEmailException`**：當 Email 已存在於系統中時由 Port 實作轉入或 Domain 驗證失敗時拋出 (對應 HTTP 409)。
*   **`ProfileNotFoundException`**：當查詢的 ID 不存在時拋出 (對應 HTTP 404)。

## 2. Use Cases & Ports

### Inbound Ports (Interfaces in `application.port.in`)
1.  **`CreateProfileUseCase`**
    *   `void execute(CreateProfileRequest request)`: 接收 DTO，轉換為 Domain Object，呼叫 Outbound Port。
2.  **`GetProfileUseCase`**
    *   `Optional<Profile> execute(String profileId)`: 根據 ID 查詢。
3.  **`UpdateProfileUseCase`**
    *   `void execute(UpdateProfileRequest request)`: 先查詢是否存在，再執行更新邏輯。
4.  **`DeleteProfileUseCase`**
    *   `void execute(String profileId)`: 先檢查是否存在，若存在則刪除。

### Outbound Ports (Interfaces in `application.port.out`)
1.  **`SaveProfilePort`**
    *   `Profile save(Profile profile)`: 儲存或更新 Profile。需處理唯一性約束衝突並轉發例外。
2.  **`LoadProfilePort`**
    *   `Optional<Profile> findById(String id)`: 依 ID 載入。
3.  **`ExistsByEmailPort`** (可選，或合併於 Save/Find)
    *   為了簡化，我們將唯一性檢查放在 `SaveProfilePort` 的實作層（Repository 層），因為 JPA Repository 最適合處理此類約束。因此 Application Layer 不需要額外的 Exists Port，除非有複雜業務規則。在此設計中，我們依賴 `SaveProfilePort` 拋出 `DuplicateEmailException`。

### Use Case Implementations (in `application.usecase`)
*   `CreateProfileService`：實現 `CreateProfileUseCase`。驗證輸入 DTO -> 建立 Domain Entity -> 呼叫 `saveProfilePort.save()`。
*   `GetProfileService`：實現 `GetProfileUseCase`。呼叫 `loadProfilePort.findById()` -> 若為空則拋出 `ProfileNotFoundException`。
*   `UpdateProfileService`：實現 `UpdateProfileUseCase`。先調用 `loadProfilePort.findById(id)`，若不存在拋出異常；否則調用 domain entity 的 `update()` 方法，再調用 `saveProfilePort.save()`。
*   `DeleteProfileService`：實現 `DeleteProfileUseCase`。先調用 `loadProfilePort.findById(id)`，若存在則調用 repository delete 或標記刪除（本 spec 暗示物理刪除或邏輯上移除查詢結果）。由於 Spring Data JPA 支持 `deleteById`，我們將實作一個簡單的 `delete` 操作在 Port 或直接使用 Repository。為了符合 Ports and Adapters，定義 `DeleteProfilePort`? 不，通常 Save/Load 已足夠，Delete 可以視為一種特殊的 Save 或使用現有的 Repository interface。但為了清晰，我們可以在 `SaveProfilePort` 中增加 `void delete(String id)` 或者新增 `RemoveProfilePort`。考慮到 SOLID 單一職責，且 Delete 與 Update/Create 行為差異大，建議在 Application Layer 直接依賴 `LoadProfilePort` 和一個通用的 `Repository` 接口？不，嚴格來說應該有對應的 Outbound Port。讓我們簡化：將 CRUD 操作分散到不同的 Port 介面，或者只保留 Load/Save，讓 Delete 成為 Save 的一種特殊狀態？不，最乾淨的方式是：
    *   `FindProfilePort`: `Optional<Profile> findById(...)`
    *   `SaveProfilePort`: `Profile save(Profile profile)`
    *   `DeleteProfilePort`: `void deleteById(String id)` (如果 repository 支援) 或者我們在 Service 層處理刪除邏輯時，調用一個專門的 port。
    *   **修正**：根據 Spec-Driven Development 慣例，每個 Use Case 最好有明確的 Port。
        *   `CreateProfileUseCase` -> `SaveProfilePort.save()`
        *   `GetProfileUseCase` -> `FindProfilePort.findById()`
        *   `UpdateProfileUseCase` -> `FindProfilePort.findById()` + `SaveProfilePort.save()`
        *   `DeleteProfileUseCase` -> `FindProfilePort.findById()` + `RemoveProfilePort.deleteById()`

## 3. Web Adapter (`adapter.web`)

### Controller
*   **`ProfileController`**: `@RestController`, `@RequestMapping("/api/profiles")`
    *   `POST /{id}`? No, POST `/profiles`.
    *   `GET /{id}`: 映射到 `GetProfileUseCase.execute(id)`。
    *   `PUT /{id}`: 映射到 `UpdateProfileUseCase.execute(request)`。
    *   `DELETE /{id}`: 映射到 `DeleteProfileUseCase.execute(id)`。

### DTOs (Request/Response)
*   **`CreateProfileRequest`**: `email` (String), `displayName` (String). Jakarta Validation: `@Email`, `@NotBlank` (optional for displayName if required, but spec says empty allowed, so maybe just format check). Spec says "empty or whitespace only视为未設定", so validation should allow null/blank for display name, but email must be valid and present.
*   **`UpdateProfileRequest`**: Same as Create. Note: PUT is full replacement. If user sends blank display name, it becomes null/empty in DB.
*   **`ProfileResponse`**: `id` (String), `email` (String), `displayName` (String).

### Error Handling (`GlobalExceptionHandler`)
*   `MethodArgumentNotValidException`: Return 400 Bad Request.
*   `InvalidEmailException` (Domain): Return 400 Bad Request.
*   `DuplicateEmailException` (Domain/Application): Return 409 Conflict.
*   `ProfileNotFoundException` (Application): Return 404 Not Found.
*   `HttpMessageNotReadableException`: Return 400 Bad Request.
*   *Note*: Do not catch generic `Exception`.

## 4. Persistence Adapter (`adapter.persistence`)

### JPA Entity
*   **`ProfileEntity`**: Implements `Persistable<String>`.
    *   Fields: `@Id String id`, `@Column(unique=true) String email`, `String displayName`.
    *   State: `boolean isNew = true;` managed by lifecycle callbacks or constructor logic during conversion.
    *   Methods: `isNew()`, `setNew(boolean)`.

### Repository
*   **`JpaProfileRepository`** extends `CrudRepository<ProfileEntity, String>` and `CustomProfilePort`? No, it implements the Outbound Port interface defined in Application layer? No, Adapter implements Application's Port. So `JpaProfileRepository` should implement `SaveProfilePort`, `FindProfilePort`, etc., OR we create a separate class that adapts the Spring Data Repo to these Ports.
    *   Best Practice: Create `JpaSaveProfilePort` implementing `SaveProfilePort`. Inject `JpaProfileRepository`.
    *   Create `JpaLoadProfilePort` implementing `FindProfilePort`.
    *   Create `JpaDeleteProfilePort` implementing `RemoveProfilePort`.

### Converter/Assembler
*   **`ProfileConverter`**: Static utility methods to convert between `Profile` (Domain), `CreateProfileRequest` (DTO), and `ProfileEntity` (Persistence).
    *   `toDomain(dto)` -> `Profile`
    *   `toDto(domain)` -> `ProfileResponse`
    *   `toEntity(domain)` -> `ProfileEntity`
    *   `fromEntity(entity)` -> `Profile`

## 5. Bootstrap (`bootstrap`)

*   **`UseCaseConfiguration`**: `@Configuration` class.
    *   Defines `@Bean`s for all Use Case Services (`CreateProfileService`, `GetProfileService`, etc.).
    *   Since they depend on Ports, and Ports are implemented by Adapters which are Spring Beans (or registered via Component Scan in Adapter modules? No, Adapters should be scanned or explicitly imported).
    *   Actually, the Skeleton usually handles component scanning for adapters if configured correctly, OR we need to import them here. Given "Bootstrap assembles use case beans", and Adapters are separate modules, we assume standard Spring Boot auto-configuration scans `adapter.*` packages. If not, `UseCaseConfiguration` might need `@Import` for adapter configurations. However, typically in Hexagonal with Spring Boot, each module's components are discovered if their base package is included in the main application scan path. The prompt says "Base Package: com.github.chunpingwang.ollamaagentoutcome". So `@SpringBootApplication` at root will scan everything. Thus, Port Implementations (in `adapter.persistence`) annotated with `@Component`/`@Repository` will be available.
    *   We only define the Application Layer Services as `@Bean` in `UseCaseConfiguration`.

## 6. Testing Strategy

*   **Domain Tests** (`domain/src/test/java`):
    *   Test Value Object immutability and validation rules (e.g., invalid email format throws exception).
    *   Test Domain Entity business logic (e.g., updating display name).
*   **Application Tests** (`application/src/test/java`):
    *   Use JUnit + Mockito.
    *   Mock Outbound Ports (`SaveProfilePort`, etc.).
    *   Verify that correct ports are called with correct arguments.
    *   Verify exceptions are thrown correctly (e.g., `DuplicateEmailException`).
*   **Web Adapter Tests** (`adapter-web/src/test/java`):
    *   `@WebMvcTest(ProfileController.class)`.
    *   Mock Inbound Ports (Use Cases).
    *   Assert HTTP Status Codes (201, 400, 409, 404, 200, 204).
    *   Assert Response Body content for GET requests.
*   **Persistence Adapter Tests** (`adapter-persistence/src/test/java`):
    *   `@DataJpaTest` + Testcontainers PostgreSQL.
    *   Test `ProfileConverter` mapping correctness.
    *   Test Port Implementations (`JpaLoadProfilePort`, `JpaSaveProfilePort`) using real DB via Testcontainers.
    *   Verify Unique Constraint violation handling (catching exception and re-throwing domain exception).
*   **BDD/Cucumber Tests** (`bootstrap/src/test/resources/features` & `bdd/`):
    *   File: `personal-profile-crud.feature`.
    *   Language: `zh-TW`.
    *   Steps use `TestRestTemplate` to hit the actual API running on a random port with Testcontainers DB.

## 7. Class List

| Module | Path (Relative to Root) | Responsibility |
| :--- | :--- | :--- |
| Domain | `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/ProfileId.java` | Value Object for Profile ID |
| Domain | `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/Email.java` | Value Object for Email validation |
| Domain | `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/DisplayName.java` | Value Object for Display Name |
| Domain | `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/Profile.java` | Domain Entity, core business logic |
| Domain | `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/InvalidEmailException.java` | Exception for invalid email format |
| Domain | `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/DuplicateEmailException.java` | Exception for duplicate email constraint violation |
| Domain | `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/ProfileNotFoundException.java` | Exception when profile is not found |
| Application Port In | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/in/CreateProfileUseCase.java` | Interface for creating a new profile |
| Application Port In | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/in/GetProfileUseCase.java` | Interface for retrieving a profile by ID |
| Application Port In | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/in/UpdateProfileUseCase.java` | Interface for updating an existing profile |
| Application Port In | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/in/DeleteProfileUseCase.java` | Interface for deleting a profile |
| Application Port Out | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/out/SaveProfilePort.java` | Interface for saving/persisting profiles |
| Application Port Out | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/out/FindProfilePort.java` | Interface for finding profiles by ID |
| Application Port Out | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/out/RemoveProfilePort.java` | Interface for removing profiles by ID |
| Application Use Case | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/CreateProfileService.java` | Implementation of CreateProfileUseCase |
| Application Use Case | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/GetProfileService.java` | Implementation of GetProfileUseCase |
| Application Use Case | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/UpdateProfileService.java` | Implementation of UpdateProfileUseCase |
| Application Use Case | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/DeleteProfileService.java` | Implementation of DeleteProfileUseCase |
| Web Adapter DTO | `adapter-web/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/CreateProfileRequest.java` | Request DTO for creation |
| Web Adapter DTO | `adapter-web/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/UpdateProfileRequest.java` | Request DTO for update |
| Web Adapter DTO | `adapter-web/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/ProfileResponse.java` | Response DTO for profile data |
| Web Adapter Controller | `adapter-web/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/ProfileController.java` | REST Controller handling HTTP requests |
| Web Adapter Error | `adapter-web/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/GlobalExceptionHandler.java` | Handles exceptions and maps to HTTP status codes |
| Persistence Entity | `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/ProfileEntity.java` | JPA Entity mapping to database table |
| Persistence Repo Interface | `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/JpaProfileRepository.java` | Spring Data JPA Repository interface |
| Persistence Port Impl | `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/JpaSaveProfilePort.java` | Implements SaveProfilePort using JPA Repo |
| Persistence Port Impl | `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/JpaFindProfilePort.java` | Implements FindProfilePort using JPA Repo |
| Persistence Port Impl | `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/JpaRemoveProfilePort.java` | Implements RemoveProfilePort using JPA Repo |
| Persistence Converter | `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/ProfileConverter.java` | Converts between Domain, DTO, and Entity models |
| Bootstrap Config | `bootstrap/src/main/java/com/github/chunpingwang/ollamaagentoutcome/bootstrap/UseCaseConfiguration.java` | Assembles Use Case beans |
