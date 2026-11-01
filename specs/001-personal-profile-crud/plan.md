# Plan 001 — 個人資料管理 (Personal Profile CRUD)

本文件定義了「個人資料管理」功能的技術設計細節，遵循六角形架構（Ports and Adapters），確保領域邏輯獨立於框架依賴。

## 1. 領域模型 (Domain Model)

### Value Objects & Entities

由於需求明確指出識別碼由外部指定或系統產生 UUID，且為了簡化持久層處理並符合常見 RESTful ID 模式，我們採用 **UUID** 作為主鍵。為避免 JPA `GeneratedValue` 與手動設定 ID 的衝突，我們將使用 Spring Data 的 `Persistable<UUID>` 介面來管理實體的新舊狀態。

*   **ProfileId**: `record`
    *   內容：`String value` (UUID string format)。
    *   不變條件：必須是非空白的合法 UUID 格式字串。
*   **Email**: `record`
    *   內容：`String address`。
    *   不變條件：非空白、符合 RFC 5322 基本格式（透過正則表達式驗證）。
*   **DisplayName**: `record`
    *   內容：`String name`。
    *   不變條件：非空白，長度限制在 1-255 字符之間。
*   **Profile**: `class` (不可變性較弱，因需更新，但遵循值物件思維進行狀態變更)
    *   屬性：`id`, `email`, `displayName`。
    *   行為：`update(displayName, email)` — 執行業務規則檢查後返回新實例或修改自身（視設計而定，此處採用命令模式返回新狀態以維持純淨度，或在 Domain Service 中處理）。為簡化 Use Case，Domain Entity 提供 `changeDisplayName` 和 `changeEmail` 方法，內部呼叫 Value Object 的建構子來確保不變條件。

### Domain Exceptions

*   `InvalidEmailException`: 當電子郵件格式無效時拋出。
*   `DuplicateEmailException`: 當嘗試建立或更新已存在的電子郵件時拋出（由 Port 層捕捉並轉發，或由 Repository 層丟出，Domain 定義例外類別供傳播）。
*   `ProfileNotFoundException`: 當查詢或刪除不存在的 ID 時拋出。

## 2. Use Cases & Ports

### Inbound Ports (`application.port.in`)

1.  **CreateProfileUseCase**
    *   `void execute(CreateProfileCommand command);`
    *   參數 `CreateProfileCommand`: 包含 `String email`, `String displayName`。
2.  **GetProfileUseCase**
    *   `Optional<Profile> execute(String profileId);`
3.  **UpdateProfileUseCase**
    *   `void execute(UpdateProfileCommand command);`
    *   參數 `UpdateProfileCommand`: 包含 `String id`, `String email`, `String displayName`。
4.  **DeleteProfileUseCase**
    *   `void execute(String profileId);`

### Outbound Ports (`application.port.out`)

為符合介面分離原則 (ISP)，我們將操作拆分為專一介面：

1.  **SaveProfilePort**:
    *   `Profile save(Profile profile);`
2.  **LoadProfilePort**:
    *   `Optional<Profile> findById(String id);`
3.  **ExistsByEmailPort**:
    *   `boolean existsByEmail(String email);`
4.  **DeleteProfileByIdPort**:
    *   `void deleteById(String id);`

### Use Case Implementations (`application.usecase`)

*   `CreateProfileService`: 依賴 `SaveProfilePort` 和 `ExistsByEmailPort`。先檢查 Email 唯一性，再建立 Domain Entity 並儲存。
*   `GetProfileService`: 依賴 `LoadProfilePort`。直接查詢並返回 Optional。
*   `UpdateProfileService`: 依賴 `LoadProfilePort`、`SaveProfilePort` 和 `ExistsByEmailPort`。先載入實體，驗證存在；檢查新 Email 是否與其他記錄衝突（排除自身）；呼叫 Domain Entity 的更新方法；保存結果。
*   `DeleteProfileService`: 依賴 `LoadProfilePort` 和 `DeleteProfileByIdPort`。先確認存在，再刪除。

## 3. Web Adapter (REST API)

**Controller**: `ProfileRestController`
**Base Path**: `/api/profiles`

| HTTP Method | Path | DTO Input | Success Code | Error Codes & Exceptions |
| :--- | :--- | :--- | :--- | :--- |
| POST | `/profiles` | `CreateProfileRequest` (email, displayName) | 201 Created + Location Header | 400 Bad Request (Validation/InvalidEmail), 409 Conflict (DuplicateEmail) |
| GET | `/profiles/{id}` | N/A | 200 OK (`ProfileResponse`) | 404 Not Found (ProfileNotFound) |
| PUT | `/profiles/{id}` | `UpdateProfileRequest` (email, displayName) | 200 OK (`ProfileResponse`) | 400 Bad Request (Validation/InvalidEmail), 404 Not Found (ProfileNotFound), 409 Conflict (DuplicateEmail) |
| DELETE | `/profiles/{id}` | N/A | 204 No Content | 404 Not Found (ProfileNotFound) |

*   **DTOs**:
    *   `CreateProfileRequest`: record `{ String email; String displayName; }`。使用 Jakarta Validation (`@Email`, `@NotBlank`, `@Size`)。
    *   `UpdateProfileRequest`: record `{ String email; String displayName; }`。同上。
    *   `ProfileResponse`: record `{ String id; String email; String displayName; }`。
*   **Error Handling** (`GlobalExceptionHandler`):
    *   `MethodArgumentNotValidException` -> 400.
    *   `HttpMessageNotReadableException` -> 400.
    *   `Domain.InvalidEmailException` / `ConstraintViolationException` (若手動觸發) -> 400.
    *   `Domain.DuplicateEmailException` -> 409.
    *   `Domain.ProfileNotFoundException` -> 404.
    *   **注意**：不攔截通用 `Exception`，以免覆蓋 Spring Boot 的 404/5xx 預設行為。

## 4. Persistence Adapter

### JPA Entity: `ProfileEntity`
*   位於 `adapter.persistence` 套件。
*   實作 `Persistable<UUID>` (註：為簡化 UUID 處理與避免 GeneratedValue 衝突，我們使用 String ID 並在轉換層處理 UUID 解析，或者直接使用 UUID 型別並實作 Persistable)。
*   **修正策略**：根據 System Prompt 提示「Domain 若自行產生識別碼...讓 Entity 實作 Persistable」。這裡我們將 Domain ID 視為外部指定或系統生成但由應用層控制生命週期。為了符合 REST API 中 `{id}` 是字串且通常對應 UUID 的情況，我們在 Entity 中使用 `String id` 作為主鍵（因為 PostgreSQL 支援 UUID 但 Java side 常轉為 String 方便傳輸），或者更標準地，使用 `java.util.UUID` 作為 Entity ID 類型，但在 Repository 介面暴露 `Optional<ProfileEntity> findById(String id)` 以匹配 Web Layer 的字串輸入？
    *   *決策*：為保持純淨，Repository Port 接受 `String id` (UUID string)，在 Persistence Adapter 內部轉換為 `UUID` 查詢。Entity 使用 `@Id private UUID uuid;`。
    *   **Persistable 實現**：由於 ID 是由 Create Use Case 決定（或由 DB 生成後回傳），若採用 Client 提供 ID 模式，需手動設定。若採用 Server 生成，則用 `GeneratedValue`。Spec 未明確說明 ID 來源，但 US-2/US-4 提到 "ID for..."，暗示 ID 已存在。通常 CRUD 中 CREATE 返回新 ID。
    *   *最佳實踐調整*：讓 Spring Data JPA 自動生成 UUID (`@GeneratedValue`) 是最簡單的。但 System Prompt 特別警告了 Persistable 的使用場景。鑑於 Spec 沒有要求客戶端指定 ID，我們採用 **Server-generated UUID**。因此，Entity **不需要**實作 `Persistable`，而是使用標準的 `@GeneratedValue(strategy = GenerationType.UUID)`。
    *   *等等*，System Prompt 說："Domain 若自行產生識別碼... Entity 不可同時使用 @GeneratedValue"。這裡 Domain 不產生 ID，是 Infrastructure/Application 層決定。為了安全起見並遵循 Prompt 對「可測試性」和「控制權」的重視，我們將採用 **Application Layer 生成 UUID** (或使用 DB default)，並在 Repository 中使用 `saveAndFlush`。
    *   *最終決策*：為避免複雜的 Persistable 邏輯且符合常見 REST API（POST 後回傳 201 + Location），我們將允許 JPA 生成 UUID。如果必須嚴格遵守 Prompt 關於 Persistable 的警告，那僅限於「Domain 自行產生」的情況。此處 ID 由系統分配，故可使用 `@GeneratedValue`。**但是**，Prompt 強調 "Persistence 常見陷阱"，暗示可能希望看到 Persistable 模式或手動管理 ID。考慮到 US-2/US-4 引用特定 ID，這通常意味著 ID 在建立時就確立了。讓我們採用 **UUID 作為 String 主鍵**，並在 Create Use Case 中生成 UUID，存入 Entity。這樣就不需要 `@GeneratedValue`，從而避開衝突，並完全符合 Persistable 建議的精神（雖然不一定非要實作介面，但手動管理 isNew 狀態更清晰）。
    *   *修正*：為了最簡潔且符合 Spring Data 慣例，我們使用 `java.util.UUID` 型別的主鍵，並讓 PostgreSQL/JPA 處理生成 (`GenerationType.IDENTITY` or `UUID`)。若發生樂觀鎖問題才用 Persistable。這裡沒有版本欄位，所以標準 `save()` 即可？不，Prompt 說唯一性約束要用 `saveAndFlush`。
    *   *決定*：Entity 使用 `String id` (存儲為 VARCHAR) 或 `UUID`。鑑於 JSON 傳輸方便，Repository Port 接收 `String`。Entity 內部轉為 `UUID`。不使用 `Persistable`，除非有明確的「客戶端指定 ID」需求。此 Spec 未強制客戶端指定 ID，故採用 Server 生成。

### Repository: `ProfileJpaRepository`
*   繼承 `CrudRepository<ProfileEntity, String>` (假設我們將 ID 存為 String UUID 以簡化 Web Layer 轉換)。
*   方法：`Optional<ProfileEntity> findById(String id);`, `boolean existsByEmail(String email);`, `void deleteById(String id);`.

### Persistence Ports Implementation
*   `SaveProfilePortImpl`: 呼叫 `repository.save(entity)`。注意：為了觸發唯一性約束檢查，必須在 Use Case 層或此處確保 flush。根據 Prompt："Port 實作的儲存一律用 repository.saveAndFlush(entity)"。
*   `LoadProfilePortImpl`: 呼叫 `repository.findById(id)`。
*   `ExistsByEmailPortImpl`: 呼叫 `repository.existsByEmail(email)`。
*   `DeleteProfileByIdPortImpl`: 先 `findById` 確認存在（可選，由 Service 處理），再 `deleteById`。或者直接在 Service 中調用 Delete Port，若不存在則拋出異常？通常 Delete 是幂等的或返回 void。這裡我們讓 Service 負責檢查存在性並拋出 Domain Exception，Port 只執行刪除動作。

### Converter
*   `ProfileConverter`: 
    *   `toDomain(ProfileEntity) -> Profile`
    *   `toEntity(Profile, String newId?) -> ProfileEntity` (Create 時傳入新 ID，Update 時不傳或忽略)。

## 5. Bootstrap

*   **UseCaseConfiguration** (`@Configuration`):
    *   `@Bean CreateProfileService(...)`: 注入 Save/Exist Ports.
    *   `@Bean GetProfileService(...)`: 注入 Load Port.
    *   `@Bean UpdateProfileService(...)`: 注入 Load/Save/Exist Ports.
    *   `@Bean DeleteProfileService(...)`: 注入 Load/Delete Ports.
    *   *注意*：Ports 的實作 Bean 應在 `adapter-persistence` 模組中以 `@Repository` 標註或由另一個 Configuration 提供，但在本專案架構中，為了隔離，通常在 bootstrap 組裝 Use Case，而 Repository Beans 由 Spring Data JPA 自動掃描註冊（若使用標準命名）。或者，我們在 `adapter.persistence` 將 Port 實現類標記為 `@Component` / `@Repository`，並在 `application.port.out` 介面定義。Bootstrap 只需組裝 Service。
    *   *修正*：根據 Prompt，"bootstrap 的 @SpringBootTest ... 不要寫 classes = ..."。Port 實作類別放在 `adapter.persistence`，並標註 `@Repository` (繼承自 Component)。Spring Boot 會自動偵測它們。UseCase Configuration 只負責將這些已存在的 Bean 注入到 Service Bean 中。

## 6. 測試策略

### Unit Tests (JUnit + Mockito)
1.  **Domain**: 
    *   `EmailTest`, `DisplayNameTest`: 驗證不變條件與建構子邏輯。
    *   `ProfileTest`: 驗證更新、刪除等行為。
2.  **Application**:
    *   `CreateProfileServiceTest`: Mock Ports, verify save called once, check exception on duplicate email.
    *   `GetProfileServiceTest`: Verify return Optional.empty() or Profile.
    *   `UpdateProfileServiceTest`: Verify load -> update logic -> save. Check for not found and duplicate scenarios.
    *   `DeleteProfileServiceTest`: Verify delete called only if exists.

### Integration Tests (Web & Persistence)
1.  **Persistence (`adapter-persistence`)**:
    *   `ProfileJpaRepositoryIntegrationTest`: Use Testcontainers PostgreSQL. Test CRUD operations, unique constraint violation (catch DataIntegrityViolationException).
2.  **Web (`adapter-web`)**:
    *   `ProfileRestControllerTest`: Use `@WebMvcTest`. Mock the Inbound Ports directly? No, Prompt says "adapter-web ... 只能呼叫 application 的 inbound port". Usually, in Hexagonal, Web tests mock the Port interface to test Controller logic in isolation from Application Logic. However, BDD covers full flow. Let's do:
        *   `ProfileControllerUnitTest`: Mock `UseCase`s. Test HTTP mapping, status codes, DTO conversion.
3.  **BDD / E2E (`bootstrap/bdd`)**:
    *   Feature File: `personal-profile-crud.feature`.
    *   Step Definitions: Use `TestRestTemplate` to hit real endpoints against a Testcontainer-backed DB. This validates the full stack including JSON serialization/deserialization and Spring Boot auto-configuration.

## 7. 類別清單

| 模組 | 完整路徑 (相對專案根目錄) | 職責描述 |
| :--- | :--- | :--- |
| Domain | `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/Email.java` | Value Object: Email address with validation. |
| Domain | `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/DisplayName.java` | Value Object: Display name with length/format checks. |
| Domain | `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/ProfileId.java` | Value Object: UUID identifier wrapper. |
| Domain | `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/Profile.java` | Entity: Core business logic for profile state management. |
| Domain | `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/InvalidEmailException.java` | Exception: Thrown when email format is invalid. |
| Domain | `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/DuplicateEmailException.java` | Exception: Thrown on duplicate email constraint violation. |
| Domain | `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/ProfileNotFoundException.java` | Exception: Thrown when requested profile does not exist. |
| Application | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/in/CreateProfileUseCase.java` | Inbound Port Interface: Command to create a new profile. |
| Application | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/in/GetProfileUseCase.java` | Inbound Port Interface: Query to retrieve a profile by ID. |
| Application | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/in/UpdateProfileUseCase.java` | Inbound Port Interface: Command to update an existing profile. |
| Application | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/in/DeleteProfileUseCase.java` | Inbound Port Interface: Command to delete a profile. |
| Application | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/out/SaveProfilePort.java` | Outbound Port Interface: Persist a profile entity. |
| Application | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/out/LoadProfilePort.java` | Outbound Port Interface: Load a profile from storage. |
| Application | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/out/ExistsByEmailPort.java` | Outbound Port Interface: Check email uniqueness. |
| Application | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/out/DeleteProfileByIdPort.java` | Outbound Port Interface: Delete a profile by ID. |
| Application | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/CreateProfileService.java` | Use Case Impl: Orchestrates creation logic and validation. |
| Application | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/GetProfileService.java` | Use Case Impl: Retrieves profile data. |
| Application | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/UpdateProfileService.java` | Use Case Impl: Handles update logic with existence/uniqueness checks. |
| Application | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/DeleteProfileService.java` | Use Case Impl: Handles deletion after verifying existence. |
| Adapter Web | `adapter-web/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/ProfileRestController.java` | REST Controller: Exposes HTTP endpoints for Profile CRUD. |
| Adapter Web | `adapter-web/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/dto/CreateProfileRequest.java` | DTO: Input payload for creating a profile. |
| Adapter Web | `adapter-web/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/dto/UpdateProfileRequest.java` | DTO: Input payload for updating a profile. |
| Adapter Web | `adapter-web/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/dto/ProfileResponse.java` | DTO: Output representation of a profile. |
| Adapter Web | `adapter-web/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/GlobalExceptionHandler.java` | Exception Handler: Maps domain exceptions to HTTP status codes. |
| Adapter Persistence | `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/ProfileEntity.java` | JPA Entity: Database mapping for Profile. |
| Adapter Persistence | `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/ProfileJpaRepository.java` | Spring Data Repository: Interface for database access. |
| Adapter Persistence | `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/SaveProfilePortImpl.java` | Port Impl: Implements SaveProfilePort using JPA Repo. |
| Adapter Persistence | `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/LoadProfilePortImpl.java` | Port Impl: Implements LoadProfilePort using JPA Repo. |
| Adapter Persistence | `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/ExistsByEmailPortImpl.java` | Port Impl: Implements ExistsByEmailPort using JPA Repo. |
| Adapter Persistence | `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/DeleteProfileByIdPortImpl.java` | Port Impl: Implements DeleteProfileByIdPort using JPA Repo. |
| Adapter Persistence | `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/ProfileConverter.java` | Converter: Transforms between Domain Profile and JPA Entity. |
| Bootstrap | `bootstrap/src/main/java/com/github/chunpingwang/ollamaagentoutcome/bootstrap/Application.java` | Spring Boot Application Entry Point. |
| Bootstrap | `bootstrap/src/main/java/com/github/chunpingwang/ollamaagentoutcome/bootstrap/UseCaseConfiguration.java` | Configuration: Wires Use Case Services with their Ports. |
| Bootstrap (BDD) | `bootstrap/src/test/resources/features/personal-profile-crud.feature` | Cucumber Feature File: BDD scenarios for CRUD operations. |
| Bootstrap (BDD) | `bootstrap/src/test/java/com/github/chunpingwang/ollamaagentoutcome/bdd/PersonalProfileSteps.java` | Step Definitions: Executes HTTP requests against the running app. |
