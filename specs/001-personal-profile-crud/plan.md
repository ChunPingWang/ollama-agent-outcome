# Plan 001 — 個人資料管理 (Personal Profile CRUD)

本計畫基於 Spec 001 設計，採用六角形架構（Ports and Adapters），確保領域邏輯獨立於框架細節。

## 1. 領域模型 (Domain Model)

### Value Objects & Entities
- **ProfileId** (`record`)
    - 內容：`String value`
    - 不變條件：非空字串，符合 UUID 格式或系統定義之唯一識別符規範。
- **Email** (`record`)
    - 內容：`String address`
    - 不變條件：非空白，符合 RFC 5322 基本語法（使用正則表達式驗證）。
    - 行為：提供 `isValid()` 方法供 Domain Service 呼叫。
- **Name** (`record`)
    - 內容：`String text`
    - 不變條件：長度介於 1 到 50 之間，非空白。
- **Profile** (`class`, immutable state via builder or factory, but mutable for JPA proxy compatibility in domain context if necessary, though we prefer pure domain objects converted by adapter)
    - *修正策略*：為符合 Hexagonal Architecture 與 JPA 需求，Domain Layer 的 `Profile` 應視為純業務實體（Pure Business Entity），不依賴 JPA annotation。其狀態由建構子確定，但為了支援更新操作，需提供修改方法或透過 Factory/Builder 建立新實例。考慮到 Spec 要求「全量更新」且需保留未變更欄位（如 email），我們設計 `Profile` 為包含所有必要資訊的聚合根。
    - 屬性：`id: ProfileId`, `name: Name`, `email: Email`。
    - 不變條件：Email 在全域唯一（此約束由 Port 層強制，Domain 僅負責資料結構完整性）。

### Domain Exceptions
- `InvalidEmailException`: 當 Email 格式無效時拋出。
- `InvalidNameException`: 當 Name 長度或內容不合規時拋出。
- `DuplicateEmailException`: 當嘗試儲存已存在的 Email 時拋出（由 Persistence Adapter 捕捉 DB constraint violation 後轉換至此例外，或在 SavePort 中檢查）。
- `ProfileNotFoundException`: 當查詢指定 ID 卻找不到資料時拋出。

## 2. Use Cases & Ports

### Inbound Ports (Interfaces)
1. **CreateProfileUseCase**
   - `void execute(CreateProfileRequest request)`
     - 參數：包含 name, email 的 DTO 或 Value Objects。
     - 行為：驗證輸入 → 建立 Profile → 呼叫 save port。
2. **GetProfileUseCase**
   - `ProfileResult execute(String profileId)`
     - 參數：字串形式的 ID。
     - 行為：呼叫 load port → 若不存在拋出異常 → 回傳結果 DTO/VO。
3. **UpdateProfileUseCase**
   - `void execute(UpdateProfileRequest request)`
     - 參數：id, new name, new email。
     - 行為：載入現有 Profile → 更新欄位 → 呼叫 save port。
4. **DeleteProfileUseCase**
   - `void execute(String profileId)`
     - 參數：ID。
     - 行為：確認存在（可選，或直接刪除並處理例外）→ 呼叫 delete port。

### Outbound Ports (Interfaces)
1. **SaveProfilePort**
   - `void save(Profile profile)`
     - 負責將 Domain Entity 持久化。需處理唯一性約束衝突。
2. **LoadProfilePort**
   - `Optional<Profile> findById(String id)`
     - 根據 ID 查詢資料。
3. **DeleteProfilePort**
   - `void deleteById(String id)`
     - 根據 ID 刪除資料。

### Use Case Implementations
- `CreateProfileService`
- `GetProfileService`
- `UpdateProfileService`
- `DeleteProfileService`

## 3. Web Adapter

### Controller
- **ProfileController** (`@RestController`)
    - Base Path: `/profiles`

### Endpoints & DTOs
1. **POST /profiles**
    - Request Body: `CreateProfileRequestDTO` (`name`, `email`)
    - Validation: Jakarta Validation (`NotBlank`, `Email`).
    - Success: 201 Created, Header `Location`.
    - Errors:
        - 400 Bad Request: 輸入驗證失敗（由 `@Valid` + `@RestControllerAdvice` 處理）。
        - 409 Conflict: Email 重複（對應 `DuplicateEmailException`）。
2. **GET /profiles/{id}**
    - Path Variable: `id` (String).
    - Response Body: `ProfileResponseDTO` (`id`, `name`, `email`).
    - Success: 200 OK.
    - Errors:
        - 404 Not Found: 找不到資料（對應 `ProfileNotFoundException`）。
3. **PUT /profiles/{id}**
    - Path Variable: `id`.
    - Request Body: `UpdateProfileRequestDTO` (`name`, `email`).
    - Success: 200 OK.
    - Errors:
        - 400 Bad Request: 輸入驗證失敗。
        - 404 Not Found: 找不到資料。
        - 409 Conflict: Email 與現有其他記錄衝突。
4. **DELETE /profiles/{id}**
    - Path Variable: `id`.
    - Success: 204 No Content.
    - Errors:
        - 404 Not Found: 找不到資料。

### Error Handling
- `@RestControllerAdvice` 將 Domain Exceptions 映射為 HTTP Status：
    - `InvalidEmailException`, `InvalidNameException` → 400 (Bad Request)
    - `DuplicateEmailException` → 409 (Conflict)
    - `ProfileNotFoundException` → 404 (Not Found)
    - `MethodArgumentNotValidException` → 400 (Bad Request, standard Spring behavior)

## 4. Persistence Adapter

### JPA Entity
- **ProfileEntity** (`jakarta.persistence.Entity`)
    - Table: `profile`
    - Fields: `id` (String/UUID), `name` (String), `email` (String).
    - Constraints: Unique index on `email`.
    - Implementation details: Implements `Persistable<String>` to handle ID generation strategy if needed, but since we use UUIDs generated in domain or application layer, we might just set the ID directly. *Decision*: Let Application Layer generate UUID for ProfileId to keep Domain pure and simple, or let DB generate? Spec says "System automatically generates unique identifier". Usually implies UUID v4 in app logic for distributed systems. We will generate UUID in `CreateProfileService` before saving. Thus, Entity does not need `@GeneratedValue`. It needs a constructor that accepts all fields including ID.

### Repository
- **ProfileRepository** (`org.springframework.data.jpa.repository.JpaRepository<ProfileEntity, String>`)
    - Method: `Optional<ProfileEntity> findByEmail(String email)` (for duplicate check if done at repo level, though Port implementation is preferred for separation of concerns). Actually, SavePort can catch constraint violation. But finding by email helps in Update scenario to ensure no other record has this email. So having `findByEmail` is useful.

### Port Implementations
- **JpaSaveProfilePort**:
    - Inject `ProfileRepository`.
    - `save(Profile profile)`: Convert Domain -> JPA Entity. Call `repository.saveAndFlush(entity)`. Catch `DataIntegrityViolationException`, wrap as `DuplicateEmailException`.
- **JpaLoadProfilePort**:
    - Inject `ProfileRepository`.
    - `findById(String id)`: Find entity, convert to Domain Profile. Return Optional.
- **JpaDeleteProfilePort**:
    - Inject `ProfileRepository`.
    - `deleteById(String id)`: Check existence first? Or just delete and let exception propagate? Spec says 404 on delete non-existent. So we should verify existence or catch specific exception. Best practice: Load then Delete, or use `existsById` then `deleteById`. To avoid race conditions in simple CRUD, loading first ensures we know it exists. However, standard REST DELETE often returns 204 even if already deleted (idempotent), but spec explicitly asks for 404 if not found. We will check existence via `load` logic inside the port or service. Let's put existence check in `DeleteProfileService` using `LoadProfilePort` result, or have `DeleteProfilePort` throw if not found. Given "Delete" usually implies removal, checking existence is safer for the 404 requirement.

### Converter/Assembler
- Internal helper methods within Ports or separate `ProfileConverter` class to handle mapping between `Profile` (Domain) and `ProfileEntity` (Persistence). Since no MapStruct/Lombok, manual mapping is required.

## 5. Bootstrap

- **UseCaseConfiguration** (`@Configuration`)
    - Defines beans for all Inbound Port Implementations (`CreateProfileService`, etc.).
    - Injects Outbound Ports into Services.
    - No other `@SpringBootApplication` classes needed; existing skeleton handles startup.
    - Ensure `@Import(TestcontainersConfiguration.class)` is NOT added here directly as a bean definition source, but test configurations use it. The main app context loads these beans automatically due to component scanning or explicit configuration? Spec says: "bootstrap 組裝任務不要新增 context 測試... Use Case Bean 只能在 UseCaseConfiguration 以 @Bean 定義一次". So we explicitly define them in `UseCaseConfiguration`.

## 6. Testing Strategy

| Layer | Test Type | Framework | Key Scenarios |
| :--- | :--- | :--- | :--- |
| Domain | Unit Tests | JUnit 5 + AssertJ | Value Object validation rules, Profile business logic invariants. |
| Application | Unit Tests | JUnit 5 + Mockito | Use case orchestration, exception translation, port interaction verification. |
| Adapter (Web) | Integration/Mock | `@WebMvcTest` + MockMvc | HTTP request/response mapping, status codes, error handling, DTO validation. |
| Adapter (Persistence)| Integration/Testcontainer | `@DataJpaTest` + PostgreSQL Container | Repository queries, Entity constraints, Port implementations (Save/Load/Delete), transaction behavior. |
| BDD / E2E | Feature Tests | Cucumber 8 + Spring Boot | Full flow via REST API using `TestRestTemplate`, verifying acceptance criteria from spec.md. |

- **Cucumber Features**: `src/test/resources/features/personal-profile-crud.feature`
    - Language: `zh-TW`
    - Steps use Cucumber Expressions (`{string}`, `{int}`).

## 7. Class List

| Category | Path (Relative to Root) | Responsibility |
| :--- | :--- | :--- |
| **Domain** | `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/ProfileId.java` | Value Object for unique identifier. |
| **Domain** | `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/Email.java` | Value Object with email format validation. |
| **Domain** | `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/Name.java` | Value Object with length/content validation. |
| **Domain** | `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/Profile.java` | Aggregate Root holding ID, Name, Email. |
| **Domain** | `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/InvalidEmailException.java` | Domain Exception for invalid email input. |
| **Domain** | `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/InvalidNameException.java` | Domain Exception for invalid name input. |
| **Domain** | `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/DuplicateEmailException.java` | Domain Exception indicating duplicate email constraint violation. |
| **Domain** | `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/ProfileNotFoundException.java` | Domain Exception when profile is not found. |
| **Application Port In** | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/in/CreateProfileUseCase.java` | Interface for creating a new profile. |
| **Application Port In** | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/in/GetProfileUseCase.java` | Interface for retrieving a profile by ID. |
| **Application Port In** | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/in/UpdateProfileUseCase.java` | Interface for updating an existing profile. |
| **Application Port In** | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/in/DeleteProfileUseCase.java` | Interface for deleting a profile. |
| **Application Port Out** | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/out/SaveProfilePort.java` | Interface for saving profile data. |
| **Application Port Out** | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/out/LoadProfilePort.java` | Interface for loading profile data. |
| **Application Port Out** | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/out/DeleteProfilePort.java` | Interface for deleting profile data. |
| **Application Service** | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/CreateProfileService.java` | Implements CreateProfileUseCase, handles validation and orchestration. |
| **Application Service** | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/GetProfileService.java` | Implements GetProfileUseCase, loads and returns profile. |
| **Application Service** | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/UpdateProfileService.java` | Implements UpdateProfileUseCase, updates existing record. |
| **Application Service** | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/DeleteProfileService.java` | Implements DeleteProfileUseCase, removes record after verification. |
| **Web Adapter Controller**| `adapter-web/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/ProfileController.java` | REST endpoints mapping to Use Cases. |
| **Web Adapter DTO** | `adapter-web/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/CreateProfileRequestDTO.java` | Request body for POST /profiles. |
| **Web Adapter DTO** | `adapter-web/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/UpdateProfileRequestDTO.java` | Request body for PUT /profiles/{id}. |
| **Web Adapter DTO** | `adapter-web/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/ProfileResponseDTO.java` | Response body for GET /profiles/{id}. |
| **Web Adapter Error Handler**| `adapter-web/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/WebExceptionHandler.java` | Global exception handler converting exceptions to HTTP responses. |
| **Persistence Entity** | `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/ProfileEntity.java` | JPA Entity mapped to 'profile' table. |
| **Persistence Repository**| `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/ProfileRepository.java` | Spring Data JPA interface for ProfileEntity. |
| **Persistence Port Impl** | `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/JpaSaveProfilePort.java` | Implements SaveProfilePort using JPA. |
| **Persistence Port Impl** | `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/JpaLoadProfilePort.java` | Implements LoadProfilePort using JPA. |
| **Persistence Port Impl** | `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/JpaDeleteProfilePort.java` | Implements DeleteProfilePort using JPA. |
| **Bootstrap Config** | `bootstrap/src/main/java/com/github/chunpingwang/ollamaagentoutcome/bootstrap/UseCaseConfiguration.java` | Spring Configuration defining Use Case beans and wiring Ports. |
