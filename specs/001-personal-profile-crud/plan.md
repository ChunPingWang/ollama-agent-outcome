# Spec 001 — 個人資料管理 (Personal Profile CRUD) 技術設計 Plan

## 1. 領域模型 (Domain Model)

本層包含純 Java 程式碼，不依賴任何框架。

### Value Objects & Entities

*   **`ProfileId`** (`record`)
    *   類型：`java.util.UUID`
    *   描述：個人資料的唯一識別碼。
    *   不變條件：不可為 null。

*   **`Email`** (`record`)
    *   類型：`String`
    *   描述：使用者的電子郵件地址。
    *   不變條件：符合 RFC 5322 標準格式；長度限制在合理範圍內（如 1-254 chars）。
    *   驗證邏輯：建構子中檢查格式，若無效則拋出 `InvalidEmailException`。

*   **`DisplayName`** (`record`)
    *   類型：`String`
    *   描述：使用者顯示名稱。
    *   不變條件：允許空白或特殊字符，但長度需在 1~100 字元之間（含空白）。
    *   驗證邏輯：建構子中檢查長度與非空值，若無效則拋出 `InvalidDisplayNameException`。

*   **`Bio`** (`record`)
    *   類型：`String` (可為 null)
    *   描述：個人簡介。
    *   不變條件：若存在，長度需 <= 500 字元。

*   **`Profile`** (`class`, immutable state via builder or factory, but mutable for JPA proxy compatibility in domain logic context if needed, however per spec we use record-like structure with explicit update method)
    *   為了配合 Hexagonal Architecture 的純淨性與 Spring Data Proxy 的需求，Domain Entity 建議設計為不可變狀態物件，但在應用層透過 Factory/Builder 建立新實例，或在 Adapter 層處理轉換。
    *   然而，JPA Entity 需要 setter 或欄位訪問權限。因此，我們定義 Domain 層的 `Profile` 為一個包含業務規則的領域模型，而將持久化細節留在 Persistence Adapter。
    *   **修正策略**：根據 System Prompt "Persistence Common Pitfalls"，Entity 需實現 `Persistable<UUID>`。這通常意味著 JPA Entity 在 persistence adapter 中。Domain Layer 應定義抽象的 `Profile` 概念或 Value Object 組合。
    *   **最終決定**：
        *   `domain.Profile`: 代表業務實體的核心邏輯。由於 Java 17 record 不支援繼承且 JPA entity 通常需要無參建構子，我們將 `Profile` 定義為一個普通的 class (或使用 record + static factory)，並在 Application Service 中進行映射。但為了簡化 Port 介面，Port 操作的是 Domain 對象。
        *   考慮到 `adapter-persistence` 需要將 DB Row 轉為 Domain Object，我們讓 `domain.Profile` 成為一個純淨的領域模型（可視為 Aggregate Root）。它擁有驗證邏輯和狀態管理。

*   **`Profile` Class Details**:
    *   Fields: `id`, `email`, `displayName`, `bio`.
    *   Methods: `updateDisplayName(String)`, `updateBio(String)`, `validate()`.
    *   Note: Since the spec requires full replacement on PUT, we might just have a constructor that takes all fields and validates them.

### Domain Exceptions

*   `InvalidEmailException`: 當 Email 格式錯誤時拋出。
*   `InvalidDisplayNameException`: 當 Display Name 長度或內容不合規範時拋出。
*   `DuplicateEmailException`: 當嘗試建立具有重複 Email 的 Profile 時拋出（由 Persistence Layer 捕捉並轉換至此例外，或在 Domain 層檢查？通常唯一性約束在 DB 層，故由 Adapter 捕捉後重拋此 Domain Exception）。
*   `ProfileNotFoundException`: 當查詢不存在的 ID 時拋出。

## 2. Use Cases & Ports (Application Layer)

本層定義業務流程與介面。

### Inbound Ports (`port.in`)

1.  **`CreateProfileUseCase`**
    ```java
    void execute(CreateProfileCommand command);
    // Command class: record CreateProfileCommand(Email email, DisplayName displayName, Bio bio) {}
    ```

2.  **`LoadProfileUseCase`**
    ```java
    Optional<Profile> execute(UUID profileId);
    ```

3.  **`UpdateProfileUseCase`**
    *   Spec 要求 PUT 為全量替換。
    ```java
    void execute(UpdateProfileCommand command);
    // Command class: record UpdateProfileCommand(UUID id, Email email, DisplayName displayName, Bio bio) {}
    ```

4.  **`DeleteProfileUseCase`**
    ```java
    void execute(UUID profileId);
    ```

### Outbound Ports (`port.out`)

為了遵循 Interface Segregation Principle，我們將 Repository 拆分為專一目的的 Port：

1.  **`SaveProfilePort`**:
    ```java
    Profile save(Profile profile);
    ```
2.  **`FindProfileByIdPort`**:
    ```java
    Optional<Profile> findById(UUID id);
    boolean existsByEmail(Email email); // 用於檢查重複
    ```
3.  **`DeleteProfilePort`**:
    ```java
    void deleteById(UUID id);
    ```

*注意：雖然可以合併成一個 `ProfileRepository`，但為了符合 SOLID 中的 ISP 以及 System Prompt 中提到的 "小而專一"，這裡選擇拆分或至少提供明確的方法簽名。考慮到實現複雜度與維護性，若方法少，合併為單一 `ProfileRepositoryPort` 也是可接受的，只要它不違反 LSP。鑑於 CRUD 操作緊密相關，定義一個 `ProfileRepositoryPort` 介面可能更直觀，包含 `save`, `findById`, `existsByEmail`, `deleteById`。讓我們採用單一 Port 介面以減少样板代碼，除非有極強理由分離。在此案例中，分離必要性不高，故定義 `ProfileRepositoryPort`。*

**修正 Outbound Port:**
```java
public interface ProfileRepositoryPort {
    Profile save(Profile profile);
    Optional<Profile> findById(UUID id);
    boolean existsByEmail(Email email);
    void deleteById(UUID id);
}
```

### Use Case Implementations (`usecase`)

1.  **`CreateProfileService`**:
    *   Inject: `ProfileRepositoryPort`.
    *   Logic: Validate command -> Check `existsByEmail` -> Create Domain Object -> Save.
2.  **`LoadProfileService`**:
    *   Inject: `ProfileRepositoryPort`.
    *   Logic: Call `findById`. If empty, throw `ProfileNotFoundException`. Return result.
3.  **`UpdateProfileService`**:
    *   Inject: `ProfileRepositoryPort`.
    *   Logic: Find by ID (throw if not found) -> Check Email uniqueness against other IDs -> Update fields or create new instance with updated data -> Save.
4.  **`DeleteProfileService`**:
    *   Inject: `ProfileRepositoryPort`.
    *   Logic: Verify existence (optional, depends on DB constraint behavior, but spec says return 404 if not found). Call `deleteById`. Throw exception if not found before deletion to satisfy "return 404" requirement in app layer logic? Or let adapter handle it? Usually, checking existence first ensures we can provide a specific error message if needed, though DELETE often just returns success/nothing. Spec requires 404 if not found. So: `if (!port.exists(id)) throw ProfileNotFound; port.deleteById(id);`

## 3. Web Adapter (`adapter-web`)

### Controller

*   **`ProfileController`** (`@RestController`, `@RequestMapping("/api/profiles")`)

#### Endpoints & DTOs

1.  **POST `/api/profiles`**
    *   Request Body: `CreateProfileRequestDTO` (`record CreateProfileRequestDTO(String email, String displayName, String bio)`)
    *   Validation: Jakarta Validation annotations (`@Email`, `@NotBlank`, `@Size`).
    *   Response: HTTP 201 Created with `Location` header pointing to `/api/profiles/{id}`.
    *   Exception Mapping:
        *   Invalid Input -> 400 Bad Request (handled by `@RestControllerAdvice`).
        *   Duplicate Email -> 409 Conflict (`DuplicateEmailException`).

2.  **GET `/api/profiles/{id}`**
    *   Path Variable: `String id` (UUID string).
    *   Response: HTTP 200 OK with `ProfileResponseDTO`.
    *   Exception Mapping:
        *   Not Found -> 404 Not Found (`ProfileNotFoundException`).

3.  **PUT `/api/profiles/{id}`**
    *   Path Variable: `String id`.
    *   Request Body: `UpdateProfileRequestDTO` (same structure as Create, but ID is in path).
    *   Note: Spec says "Full Replacement". So the DTO contains all fields including email. We must ensure the email being updated doesn't conflict with other existing profiles.
    *   Response: HTTP 200 OK.
    *   Exception Mapping:
        *   Not Found -> 404 Not Found.
        *   Duplicate Email (conflict with another profile) -> 409 Conflict.

4.  **DELETE `/api/profiles/{id}`**
    *   Path Variable: `String id`.
    *   Response: HTTP 204 No Content.
    *   Exception Mapping:
        *   Not Found -> 404 Not Found.

### Error Handling

*   **`GlobalExceptionHandler`** (`@RestControllerAdvice`)
    *   Handle `MethodArgumentNotValidException`: Return 400.
    *   Handle `HttpMessageNotReadableException`: Return 400.
    *   Handle `DomainExceptions`:
        *   `InvalidEmailException`, `InvalidDisplayNameException`: Map to specific error messages in 400 response? Or generic validation error. Let's map domain validation errors to 400 Bad Request with descriptive message.
        *   `DuplicateEmailException`: Map to 409 Conflict.
        *   `ProfileNotFoundException`: Map to 404 Not Found.
    *   Do NOT catch general `Exception.class`.

## 4. Persistence Adapter (`adapter-persistence`)

### JPA Entity

*   **`ProfileEntity`** (`@Entity`)
    *   Implements `Persistable<UUID>`.
    *   Fields: `@Id UUID id`, `@Column(unique=true) String email`, `String displayName`, `String bio`.
    *   State Management: `@Transient boolean isNew = true;` + `@PostLoad/@PostPersist` logic to set `isNew = false`. This handles the "domain generates ID" scenario mentioned in pitfalls.
    *   No `@Version`.

### Repository Interface

*   **`ProfileJpaRepository`** extends `CrudRepository<ProfileEntity, UUID>`
    *   Custom method: `boolean existsByEmail(String email);` (Spring Data will implement this).

### Port Implementation

*   **`JpaProfileRepositoryPort`** implements `ProfileRepositoryPort`
    *   Inject: `ProfileJpaRepository`.
    *   Methods: Delegate calls to repository. Handle conversion between `ProfileEntity` and Domain `Profile`.
    *   Uniqueness Check: In `save()`, if it's a new entity with an existing email, catch `DataIntegrityViolationException` and throw `DuplicateEmailException`. Note: Since we check existence before save in UseCase, this is a safety net. However, for concurrent scenarios or strict adherence, checking inside port or relying on DB constraint is key. The plan says "catch DataIntegrityViolationException".

### Converter / Mapper

*   Internal helper methods within Port implementation or separate class to convert Entity <-> Domain Object.

## 5. Bootstrap (`bootstrap`)

*   **`UseCaseConfiguration`** (`@Configuration`)
    *   Defines Beans for all Application Layer Services (`CreateProfileService`, etc.).
    *   Dependencies are injected via constructor arguments defined in the bean definitions.
    *   Example:
        ```java
        @Bean
        CreateProfileUseCase createProfileUseCase(ProfileRepositoryPort profileRepo) {
            return new CreateProfileService(profileRepo);
        }
        // ... other beans
        ```
*   **No `@ComponentScan` overrides**: Let Spring Boot auto-configuration handle Web and Persistence adapters. Only explicitly define Application layer beans here as per instructions ("use case bean can only be defined once in UseCaseConfiguration").

## 6. Testing Strategy

### Unit Tests (JUnit + Mockito + AssertJ)

1.  **Domain Tests** (`domain/src/test/java/.../domain/`)
    *   Test Value Objects construction and validation rules (e.g., invalid email throws exception).
2.  **Application Tests** (`application/src/test/java/.../application/usecase/`)
    *   Mock `ProfileRepositoryPort`.
    *   Test each Service class:
        *   Happy path execution.
        *   Exception propagation (e.g., port throws DuplicateEmailException, service re-throws or wraps? Spec implies domain exceptions should bubble up to web adapter for HTTP mapping).
        *   Edge cases (null inputs if not handled by DTO validation).

### Integration Tests (Spring Boot + Testcontainers)

1.  **Web Adapter Tests** (`adapter-web/src/test/java/.../web/`)
    *   `@WebMvcTest(ProfileController.class)`
    *   `@MockitoBean` for all Inbound Ports (Use Cases).
    *   Verify HTTP status codes, response bodies, headers (Location), and error mappings using `MockMvcTester` or `MockMvc`.
2.  **Persistence Adapter Tests** (`adapter-persistence/src/test/java/.../persistence/`)
    *   `@DataJpaTest` with `@ServiceConnection(PostgreSQLContainer)`.
    *   Test `JpaProfileRepositoryPort`:
        *   Save and Load entities.
        *   Uniqueness constraint enforcement (catching duplicates).
        *   Delete operations.
3.  **BDD / Cucumber Tests** (`bootstrap/src/test/resources/features/`, steps in `bdd` package)
    *   Feature File: `personal-profile-crud.feature`
    *   Runner uses `CucumberSpringContextConfiguration` to load full Spring Context including Testcontainers DB.
    *   Steps use `TestRestTemplate` to call actual endpoints.
    *   Scenario Outline for various CRUD operations as defined in Spec.

## 7. Class List

| Category | Full Path (Relative to Project Root) | Responsibility |
| :--- | :--- | :--- |
| **Domain** | `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/valueobject/Email.java` | Value Object representing email address with validation. |
| **Domain** | `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/valueobject/DisplayName.java` | Value Object representing display name with length constraints. |
| **Domain** | `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/valueobject/Bio.java` | Value Object representing user bio text. |
| **Domain** | `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/entity/ProfileId.java` | Value Object wrapping UUID for Profile ID. |
| **Domain** | `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/model/Profile.java` | Domain Entity / Aggregate Root containing business logic and state. |
| **Domain** | `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/exception/DuplicateEmailException.java` | Domain Exception for duplicate email conflicts. |
| **Domain** | `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/exception/InvalidEmailException.java` | Domain Exception for invalid email format. |
| **Domain** | `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/exception/InvalidDisplayNameException.java` | Domain Exception for invalid display name. |
| **Domain** | `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/exception/ProfileNotFoundException.java` | Domain Exception when profile is not found. |
| **Application** | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/in/CreateProfileUseCase.java` | Inbound Port interface for creating a new profile. |
| **Application** | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/in/LoadProfileUseCase.java` | Inbound Port interface for loading a profile by ID. |
| **Application** | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/in/UpdateProfileUseCase.java` | Inbound Port interface for updating an existing profile. |
| **Application** | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/in/DeleteProfileUseCase.java` | Inbound Port interface for deleting a profile. |
| **Application** | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/command/CreateProfileCommand.java` | Command object carrying data for creation use case. |
| **Application** | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/command/UpdateProfileCommand.java` | Command object carrying data for update use case. |
| **Application** | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/out/ProfileRepositoryPort.java` | Outbound Port interface defining persistence operations. |
| **Application** | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/CreateProfileService.java` | Implementation of CreateProfileUseCase, orchestrates validation and saving. |
| **Application** | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/LoadProfileService.java` | Implementation of LoadProfileUseCase, retrieves profile from port. |
| **Application** | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/UpdateProfileService.java` | Implementation of UpdateProfileUseCase, handles full replacement logic. |
| **Application** | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/DeleteProfileService.java` | Implementation of DeleteProfileUseCase, removes profile by ID. |
| **Web Adapter** | `adapter-web/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/controller/ProfileController.java` | REST Controller exposing CRUD endpoints for profiles. |
| **Web Adapter** | `adapter-web/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/dto/CreateProfileRequestDTO.java` | DTO for incoming create requests with Jakarta Validation. |
| **Web Adapter** | `adapter-web/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/dto/UpdateProfileRequestDTO.java` | DTO for incoming update requests. |
| **Web Adapter** | `adapter-web/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/dto/ProfileResponseDTO.java` | DTO for outgoing profile data responses. |
| **Web Adapter** | `adapter-web/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/error/GlobalExceptionHandler.java` | Handles domain and validation exceptions, maps to HTTP status codes. |
| **Persistence Adapter**| `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/entity/ProfileEntity.java` | JPA Entity mapping Profile to database table, implements Persistable. |
| **Persistence Adapter**| `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/repository/ProfileJpaRepository.java` | Spring Data Repository interface for ProfileEntity. |
| **Persistence Adapter**| `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/port/JpaProfileRepositoryPort.java` | Implements ProfileRepositoryPort, handles entity conversion and DB interaction. |
| **Bootstrap** | `bootstrap/src/main/java/com/github/chunpingwang/ollamaagentoutcome/bootstrap/config/UseCaseConfiguration.java` | Configuration class defining Application Layer beans (Services). |
