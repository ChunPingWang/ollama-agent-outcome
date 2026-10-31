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
    *   Note: Since the spec requires full replacement on PUT, we might just have a constructor that takes all fields or a builder. Let's use a Builder pattern for creation and explicit update methods if needed, but given "Full Replacement" in spec, passing all data to constructor is cleaner. However, email uniqueness is checked at persistence level usually, or domain level? Spec says "Email already exists" -> 409. This implies checking existence before save. So Domain doesn't check uniqueness against other instances, only format. Uniqueness is an infrastructure concern (Persistence).

### Domain Exceptions

1.  **`InvalidEmailException`** (`RuntimeException`)：當 Email VO 格式無效時拋出。
2.  **`InvalidDisplayNameException`** (`RuntimeException`)：當 DisplayName VO 長度或內容無效時拋出。
3.  **`DuplicateEmailException`** (`DomainException`)：當嘗試保存已存在的電子郵件時由 Port 實作層（或 Application Service 捕捉後）拋出，用於轉換為 HTTP 409。*註：嚴格來說，重複檢查是 Persistence 層的責任，但為了讓 Use Case 知道發生了什麼，Port 介面應宣告此例外，或由 Adapter 轉發。根據 SOLID，Application Layer 定義 Port，Port 可拋出自訂 Checked Exception 或 RuntimeException。這裡使用 `DuplicateEmailException extends RuntimeException`。*
4.  **`ProfileNotFoundException`** (`RuntimeException`)：當查詢不存在的 ID 時拋出。

## 2. Use Cases & Ports

所有類別位於 `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/...`

### Inbound Ports (Interfaces)

1.  **`CreateProfileUseCase`**
    *   `void execute(CreateProfileCommand command)`
    *   Command: record `{ String email, String displayName, String bio }`

2.  **`GetProfileUseCase`**
    *   `Optional<Profile> execute(UUID id)`

3.  **`UpdateProfileUseCase`**
    *   `void execute(UpdateProfileCommand command)`
    *   Command: record `{ UUID id, String email, String displayName, String bio }`
    *   Note: Spec says "Full Replacement" for PUT. So we update all mutable fields or replace the whole object state. Since Email is unique key and shouldn't change arbitrarily in a simple CRUD without migration logic, but spec allows changing it via PUT? The spec shows PUT body includes email. If email changes to an existing one, it should be 409. This implies complex validation. For simplicity in this plan, we assume PUT updates the profile identified by ID with new data. If the new email conflicts, throw DuplicateEmailException.

4.  **`DeleteProfileUseCase`**
    *   `void execute(UUID id)`

### Outbound Ports (Interfaces)

位於 `application/port/out/...`

1.  **`SaveProfilePort`**
    *   `Profile save(Profile profile)`
    *   Throws: `DuplicateEmailException` if constraint violated.

2.  **`LoadProfilePort`**
    *   `Optional<Profile> findById(UUID id)`

## 3. Web Adapter

套件：`com.github.chunpingwang.ollamaagentoutcome.adapter.web`

### Controller

*   **`ProfileController`** (`@RestController`)
    *   Base Path: `/api/profiles`
    *   Dependencies: Injected Inbound Ports (`CreateProfileUseCase`, etc.)

#### Endpoints & DTOs

1.  **POST /api/profiles** -> `createProfile(@Valid @RequestBody CreateProfileRequest request)`
    *   Request DTO: `CreateProfileRequest` (`record { String email, String displayName, String bio }`)
        *   Validation: `@NotBlank`, `@Email`, `@Size`.
    *   Response: HTTP 201 Created + Location Header. Body can be empty or return created ID. Spec implies standard REST. Let's return the created Profile JSON in body for consistency, or just headers. Spec says "Location header should point to resource path". Usually returns 201 with body. We will return the saved profile representation.
    *   Error Mapping:
        *   Invalid Input (Validation) -> 400 Bad Request (`MethodArgumentNotValidException`).
        *   Domain Logic Failures (if any at this layer, but mostly handled by ports) -> mapped below.

2.  **GET /api/profiles/{id}** -> `getProfile(@PathVariable("id") UUID id)`
    *   Response: HTTP 200 OK, Body: `GetProfileResponse` (`record { UUID id, String email, String displayName, String bio }`).
    *   Error Mapping:
        *   Not Found -> 404 Not Found (`ProfileNotFoundException`).

3.  **PUT /api/profiles/{id}** -> `updateProfile(@PathVariable("id") UUID id, @Valid @RequestBody UpdateProfileRequest request)`
    *   Request DTO: `UpdateProfileRequest` (`record { String email, String displayName, String bio }`)
        *   Validation: Same as Create.
    *   Response: HTTP 200 OK, Body: Updated Profile JSON.
    *   Error Mapping:
        *   Not Found -> 404 Not Found.
        *   Duplicate Email -> 409 Conflict (`DuplicateEmailException`).

4.  **DELETE /api/profiles/{id}** -> `deleteProfile(@PathVariable("id") UUID id)`
    *   Response: HTTP 204 No Content.
    *   Error Mapping:
        *   Not Found -> 404 Not Found.

### Exception Handler

*   **`GlobalExceptionHandler`** (`@RestControllerAdvice`)
    *   Handle `MethodArgumentNotValidException` -> 400.
    *   Handle `ProfileNotFoundException` -> 404.
    *   Handle `DuplicateEmailException` -> 409.
    *   Handle other specific domain exceptions if needed. Do NOT catch generic `Exception`.

## 4. Persistence Adapter

套件：`com.github.chunpingwang.ollamaagentoutcome.adapter.persistence`

### JPA Entity

*   **`ProfileEntity`** (`@Entity`)
    *   Implements `Persistable<UUID>`.
    *   Fields: `@Id UUID id`, `String email`, `String displayName`, `String bio`.
    *   Logic for `isNew()`: Uses a transient boolean flag, updated via lifecycle callbacks or constructor logic in the converter/service layer to ensure correct INSERT vs UPDATE behavior as per "Persistence Common Pitfalls". Specifically, when creating from Domain object (which has no ID initially? Or does it?), we need to handle ID generation.
    *   *Correction*: Spec says User provides Email/Name/Bio on Create. Who generates ID? Usually Server-side UUID is best practice for distributed systems. So `CreateProfileUseCase` should generate the UUID before calling Save Port, OR the Repository generates it. Let's have Application Service generate the UUID to keep Domain clean of infrastructure concerns about ID strategy, or let Domain Value Object `ProfileId` be generated by a factory. Let's assume `CreateProfileCommand` doesn't have an ID. The Use Case creates a new `Profile` with a new UUID. Then saves it.
    *   `ProfileEntity` needs `@GeneratedValue`? No, if we pass explicit UUID. But JPA usually handles IDs. If we use `Persistable`, we can manage state manually. However, simplest way compliant with pitfalls:
        1.  Generate UUID in Application Layer (`UUID.randomUUID()`).
        2.  Pass to Domain `Profile`.
        3.  Convert to Entity. Set `isNew = true`.
        4.  Call `repository.saveAndFlush(entity)`.
        5.  After save, set `isNew = false`.

### Spring Data Repository

*   **`JpaProfileRepository`** (`extends JpaRepository<ProfileEntity, UUID>`)
    *   Method: `Optional<ProfileEntity> findByEmail(String email)` (for duplicate check if needed at DB level, though Port throws exception on constraint violation). Actually, catching `DataIntegrityViolationException` is safer than pre-checking for race conditions. So just standard CRUD methods are enough, maybe custom query for optimization later, but not required now.

### Port Implementations

*   **`JpaSaveProfilePort`** (`implements SaveProfilePort`)
    *   Injects `JpaProfileRepository`.
    *   Logic: Converts Domain `Profile` -> `ProfileEntity`. Calls `saveAndFlush`. Catches `DataIntegrityViolationException` and re-throws as `DuplicateEmailException`. Handles the `Persistable` state management (setting `isNew`).

*   **`JpaLoadProfilePort`** (`implements LoadProfilePort`)
    *   Injects `JpaProfileRepository`.
    *   Logic: Finds by ID. If present, converts Entity to Domain `Profile`. Returns Optional.

### Converter/Assembler

*   **`ProfileConverter`** (Utility class or static methods)
    *   `toDomain(ProfileEntity entity)`
    *   `toEntity(Profile domain)`

## 5. Bootstrap

套件：`com.github.chunpingwang.ollamaagentoutcome.bootstrap`

*   **`UseCaseConfiguration`** (`@Configuration`)
    *   Defines Beans for all Inbound Ports implementations? No, defines beans for Application Services that implement Use Cases.
    *   Actually, in Hexagonal with Spring, we often have Interfaces in App Layer and Implementations also in App Layer (or separate). The prompt says "Inbound port interface... implementation ends with Service". And "Bootstrap @SpringBootTest must Import Testcontainers... use case bean only defined once in UseCaseConfiguration".
    *   So, `CreateProfileService`, `GetProfileService`, etc., are classes in `application/usecase/...` implementing the interfaces in `application/port/in/...`.
    *   `UseCaseConfiguration` will `@Bean` these services. It needs to inject the Outbound Ports (which are implemented in Persistence Adapter and exposed as beans there, or configured here?).
    *   Standard approach: Persistence Adapter provides beans for Port implementations. Bootstrap wires them together.
    *   Wait, System Prompt says: "adapter-persistence ... implements application's outbound port". This implies the Implementation class is in `adapter.persistence`.
    *   So `JpaSaveProfilePort` is a Bean in `adapter.persistence` configuration? Or just component scanned?
    *   To avoid ambiguity and ensure single definition:
        1.  `adapter.persistence` has `PersistenceConfig` (`@Configuration`) that defines `JpaSaveProfilePort` and `JpaLoadProfilePort` as Beans.
        2.  `bootstrap.UseCaseConfiguration` defines `CreateProfileService` etc., injecting the Port Interfaces.

*   **Cucumber Step Definitions**: Located in `bdd` package under bootstrap test resources/classes.

## 6. Testing Strategy

### Unit Tests (JUnit + Mockito)

*   **Domain Layer** (`domain/src/test/java/.../domain/`)
    *   Test Value Objects (`Email`, `DisplayName`) validation rules.
    *   Test Domain Entity business logic (if any).

*   **Application Layer** (`application/src/test/java/.../application/`)
    *   Test `CreateProfileService`: Verify it calls `savePort.save()` with correct domain object. Mock `SaveProfilePort`.
    *   Test `GetProfileService`: Verify return value from `loadPort.findById()`.
    *   Test `UpdateProfileService`: Verify update logic, handling of not found cases.
    *   Test `DeleteProfileService`: Verify delete call.
    *   Use AssertJ for assertions.

### Integration Tests (Spring Boot + Testcontainers)

*   **Persistence Adapter** (`adapter-persistence/src/test/java/.../persistence/`)
    *   `@DataJpaTest` + `@Import(JpaLoadProfilePort.class, JpaSaveProfilePort.class)`? No, Ports are interfaces. We need to import the implementations or use a specific config class that exposes them. The prompt says "Use @Import(...)". So we create a test configuration or directly import beans if they are in component scan path. Since `adapter.persistence` is separate module, its classes might not be auto-scanned by Web tests easily without explicit imports.
    *   Better: Create `PersistenceTestConfig` in adapter-persistence test scope that defines the Port beans using the real Repository and Converter. Then `@Import(PersistenceTestConfig.class)`.
    *   Test scenarios: Save valid profile, Duplicate email constraint violation, Load existing/non-existing.

*   **Web Adapter** (`adapter-web/src/test/java/.../web/`)
    *   `@WebMvcTest(ProfileController.class)`
    *   Mock all Inbound Ports (`CreateProfileUseCase`, etc.) using `@MockitoBean`.
    *   Verify HTTP status codes (201, 400, 409, 404, 204).
    *   Verify JSON body structure.
    *   Verify Location header on POST.

### BDD Tests (Cucumber)

*   Feature File: `bootstrap/src/test/resources/features/personal-profile-crud.feature`
*   Step Definitions: `com.github.chunpingwang.ollamaagentoutcome.bdd.ProfileSteps`
*   Runner: Uses `SpringBootTest` with full context (including DB via Testcontainers).
*   Scenarios map directly to Spec Gherkin steps.

## 7. Class List

| Category | Path (Relative to Root) | Responsibility |
| :--- | :--- | :--- |
| **Domain** | `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/valueobject/Email.java` | Value Object for Email validation and storage logic. |
| **Domain** | `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/valueobject/DisplayName.java` | Value Object for Display Name length/content rules. |
| **Domain** | `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/entity/Profile.java` | Domain Entity representing the core business object. |
| **Domain** | `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/exception/DuplicateEmailException.java` | Exception thrown when email uniqueness is violated at domain/port level. |
| **Domain** | `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/exception/InvalidEmailException.java` | Exception for invalid email format in VO. |
| **Application Port In** | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/in/CreateProfileUseCase.java` | Interface defining command to create a profile. |
| **Application Port In** | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/in/GetProfileUseCase.java` | Interface defining query to get a profile by ID. |
| **Application Port In** | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/in/UpdateProfileUseCase.java` | Interface defining command to update a profile. |
| **Application Port In** | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/in/DeleteProfileUseCase.java` | Interface defining command to delete a profile. |
| **Application Port Out** | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/out/SaveProfilePort.java` | Interface for saving domain profiles. |
| **Application Port Out** | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/out/LoadProfilePort.java` | Interface for loading domain profiles. |
| **Application Service** | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/CreateProfileService.java` | Implementation of CreateProfileUseCase, orchestrates creation logic. |
| **Application Service** | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/GetProfileService.java` | Implementation of GetProfileUseCase. |
| **Application Service** | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/UpdateProfileService.java` | Implementation of UpdateProfileUseCase. |
| **Application Service** | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/DeleteProfileService.java` | Implementation of DeleteProfileUseCase. |
| **Web Adapter Controller** | `adapter-web/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/ProfileController.java` | REST Controller handling HTTP requests/responses. |
| **Web Adapter DTOs** | `adapter-web/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/dto/CreateProfileRequest.java` | Request DTO for creating profile. |
| **Web Adapter DTOs** | `adapter-web/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/dto/UpdateProfileRequest.java` | Request DTO for updating profile. |
| **Web Adapter DTOs** | `adapter-web/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/dto/GetProfileResponse.java` | Response DTO for getting profile details. |
| **Web Adapter Exception** | `adapter-web/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/handler/GlobalExceptionHandler.java` | Handles exceptions and maps to HTTP status codes. |
| **Persistence Entity** | `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/ProfileEntity.java` | JPA Entity mapped to database table. |
| **Persistence Repository** | `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/JpaProfileRepository.java` | Spring Data JPA interface for Profile persistence. |
| **Persistence Port Impl** | `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/JpaSaveProfilePort.java` | Implements SaveProfilePort using JPA repository. |
| **Persistence Port Impl** | `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/JpaLoadProfilePort.java` | Implements LoadProfilePort using JPA repository. |
| **Bootstrap Config** | `bootstrap/src/main/java/com/github/chunpingwang/ollamaagentoutcome/bootstrap/UseCaseConfiguration.java` | Wires Application Services (Use Cases) with Ports. |
| **BDD Steps** | `bootstrap/src/test/java/com/github/chunpingwang/ollamaagentoutcome/bdd/ProfileSteps.java` | Cucumber step definitions for BDD tests. |
