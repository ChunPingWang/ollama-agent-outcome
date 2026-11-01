# Plan 001 — 個人資料管理 (Personal Profile CRUD)

本文件定義了「個人資料管理」功能的技術設計細節，遵循六角形架構（Ports and Adapters），確保領域邏輯獨立於框架依賴。

## 1. 領域模型 (Domain Model)

### Value Objects & Entities

由於識別碼由外部系統或 UUID 生成器產生，且需支援 JPA 持久化，我們採用以下結構：

*   **`ProfileId`** (`record`)
    *   類型：`UUID`
    *   用途：作為 Profile 的唯一標識符。
*   **`Email`** (`record`)
    *   欄位：`String value`
    *   不變條件：符合 RFC 5322 基本格式（非空、包含 @）。
    *   行為：提供靜態工廠方法 `of(String)` 進行驗證，失敗拋出 `InvalidEmailException`。
*   **`DisplayName`** (`record`)
    *   欄位：`String value`
    *   不變條件：不能為 null，不能僅含空白字符（`!isBlank()`）。
    *   行為：提供靜態工廠方法 `of(String)` 進行驗證，失敗拋出 `InvalidDisplayNameException`。
*   **`Profile`** (`class`, mutable for persistence updates, but logically immutable via factory methods in domain service if needed. For simplicity in this CRUD spec, we allow direct setters on the entity after validation or use a builder pattern within the UseCase).
    *   *修正策略*：為了簡化並符合 JPA 受管實體的需求，Domain Entity 將是可變的，但所有狀態變更必須通過 Domain Service 或明確的業務方法觸發，以確保不變條件。然而，在簡單的 CRUD 場景中，通常由 Application Layer 負責協調輸入驗證與領域邏輯。這裡我們將 `Profile` 定義為一個擁有 ID、Email、DisplayName 的聚合根。
    *   欄位：`id: ProfileId`, `email: Email`, `displayName: DisplayName`.
    *   構造函數/工廠：私有構造函數，公開靜態 `create(ProfileId id, String emailStr, String displayNameStr)`，內部調用 VO 的驗證邏輯。
    *   更新方法：`update(String newEmailStr, String newDisplayNameStr)`，內部重新驗證 VO 並更新狀態。

### Domain Exceptions

*   `ProfileNotFoundException`: 當查詢或刪除不存在的資料時拋出。
*   `DuplicateEmailException`: 當嘗試建立或更新已存在相同電子郵件的資料時拋出（通常在 Persistence Port 層捕捉 DB 約束衝突後轉為此例外）。
*   `InvalidEmailException`: 電子郵件格式無效。
*   `InvalidDisplayNameException`: 顯示名稱格式無效（如全空白）。

## 2. Use Cases & Ports (Application Layer)

所有 Inbound Ports 以 `UseCase` 結尾，Outbound Ports 以 `Port` 結尾。

### Inbound Ports (Interfaces)

1.  **`CreateProfileUseCase`**
    *   `void execute(CreateProfileCommand command);`
    *   Command DTO (`record`): `email`, `displayName`.
2.  **`GetProfileUseCase`**
    *   `ProfileDto execute(GetProfileQuery query);`
    *   Query DTO (`record`): `id: UUID`.
    *   Return: `ProfileDto` (包含 id, email, displayName).
3.  **`UpdateProfileUseCase`**
    *   `void execute(UpdateProfileCommand command);`
    *   Command DTO (`record`): `id: UUID`, `email`, `displayName`.
4.  **`DeleteProfileUseCase`**
    *   `void execute(DeleteProfileCommand command);`
    *   Command DTO (`record`): `id: UUID`.

### Outbound Ports (Interfaces)

1.  **`SaveProfilePort`**
    *   `Profile save(Profile profile);`
2.  **`FindProfileByIdPort`**
    *   `Optional<Profile> findById(UUID id);`
3.  **`ExistsByEmailPort`** (可選，或直接在 Save 時處理唯一性約束並捕捉異常。為清晰起見，我們依賴 DB 約束和 Port 層的轉換，因此只需一個通用的 Repository Port 即可，但為了符合 SOLID 介面分離，我們可以合併 Find/Exist 到一個 Port，或者讓 Save 負責檢查。鑑於 JPA 的特性，通常由 Repository 統一管理。)
    *   *調整*：為了簡化，Outbound Port 將直接映射到 Spring Data Repository 的能力。
    *   `interface ProfileRepositoryPort { Optional<Profile> findById(UUID id); boolean existsByEmail(String email); void deleteById(UUID id); }` -> 這違反了 Domain Entity 與 JPA Entity 的隔離嗎？不，Port 操作的是 Domain Entity。但是 `existsByEmail` 需要知道 Email 的值。
    *   *最佳實踐*：Outbound Port 應該只操作 Domain Entities。
        *   `save(Profile)`
        *   `findById(UUID) -> Optional<Profile>`
        *   `deleteById(UUID)`
    *   關於「重複電子郵件」的檢查：在 `SaveProfilePort` 實作中（Persistence Adapter），當呼叫 `repository.save()` 發生 `DataIntegrityViolationException` (Unique Constraint Violation) 時，捕獲並拋出 `DuplicateEmailException`。這樣就不需要在 Application Layer 預先查詢是否存在，減少一次 DB 往返，並避免 Race Condition。

### Use Case Implementations (Services)

1.  **`CreateProfileService`**: 實現 `CreateProfileUseCase`。驗證輸入 -> 調用 `SaveProfilePort`。
2.  **`GetProfileService`**: 實現 `GetProfileUseCase`。調用 `FindProfileByIdPort`，若不存在則拋出 `ProfileNotFoundException`。
3.  **`UpdateProfileService`**: 實現 `UpdateProfileUseCase`。先 `findById`，若存在則更新實體狀態，再 `save`。處理唯一性衝突異常。
4.  **`DeleteProfileService`**: 實現 `DeleteProfileUseCase`。先 `findById` 確認存在（可選，直接 delete 也可以，但為了返回明確錯誤或日誌，建議先查後刪或直接捕捉刪除失敗）。根據 Spec，刪除不存在的應回 404，所以需要先檢查存在性。

## 3. Web Adapter (REST API)

套件：`com.github.chunpingwang.ollamaagentoutcome.adapter.web`

*   **Controller**: `ProfileRestController`
    *   Base Path: `/api/profiles`
    *   `POST /`: 映射到 `CreateProfileUseCase.execute()`。回傳 201 + Location Header。
    *   `GET /{id}`: 映射到 `GetProfileUseCase.execute()`。回傳 200 JSON。
    *   `PUT /{id}`: 映射到 `UpdateProfileUseCase.execute()`。回傳 200 OK。
    *   `DELETE /{id}`: 映射到 `DeleteProfileUseCase.execute()`。回傳 204 No Content。

*   **DTOs** (`record`s):
    *   `CreateProfileRequest`: `email`, `displayName`.
    *   `UpdateProfileRequest`: `email`, `displayName`.
    *   `ProfileResponse`: `id`, `email`, `displayName`.

*   **Error Handling**: `GlobalExceptionHandler` (`@RestControllerAdvice`)
    *   `MethodArgumentNotValidException` -> 400 Bad Request.
    *   `InvalidEmailException`, `InvalidDisplayNameException` (若在 Controller 層驗證失敗) -> 400 Bad Request.
    *   `DuplicateEmailException` -> 409 Conflict.
    *   `ProfileNotFoundException` -> 404 Not Found.
    *   `HttpMessageNotReadableException` -> 400 Bad Request.

## 4. Persistence Adapter (JPA)

套件：`com.github.chunpingwang.ollamaagentoutcome.adapter.persistence`

*   **Entity**: `ProfileEntity`
    *   實作 `Persistable<UUID>` 以處理 Domain 生成的 UUID ID，避免 JPA 將其視為新記錄時的衝突或錯誤更新邏輯（雖然對於純新增沒問題，但為了符合 Prompt 中的陷阱提示，我們使用 `isNew()` 標誌）。
    *   欄位：`id` (@Id), `email` (@Column(unique=true)), `displayName`.
    *   轉換：由 `ProfileConverter` 負責將 `Domain.Profile` <-> `ProfileEntity`。

*   **Repository Interface**: `ProfileJpaRepository extends JpaRepository<ProfileEntity, UUID>, JpaSpecificationExecutor<ProfileEntity>`
    *   Spring Data 自動生成基本 CRUD。

*   **Port Implementation**: `JpaSaveProfilePort`, `JpaFindProfileByIdPort`, `JpaDeleteProfilePort` (或者合併為一個 `JpaProfileRepositoryAdapter`)。
    *   根據 SOLID 介面分離原則，我們可以建立多個小 Port 實現類，每個對應 Application Layer 的一個 Outbound Port 接口。
    *   `class JpaSaveProfilePort implements SaveProfilePort`: 依賴 `ProfileJpaRepository`。呼叫 `saveAndFlush`。捕捉 `DataIntegrityViolationException` 並拋出 `DuplicateEmailException`。
    *   `class JpaFindProfileByIdPort implements FindProfileByIdPort`: 依賴 `ProfileJpaRepository`。呼叫 `findById` 並轉換回 Domain Entity。
    *   `class JpaDeleteProfilePort implements DeleteProfilePort` (如果定義了這個 Port): 依賴 `ProfileJpaRepository`。先檢查存在性（可選），然後刪除。為了符合 Spec "404 if not found"，我們可以在 Service 層先查後刪，或者在 Port 層刪除前檢查。通常 Service 層處理業務邏輯更合適，所以 Port 只負責執行刪除操作。如果在 Service 層發現找不到就丟異常，那麼 Port 只需要 `deleteById`。

*   **Converter**: `ProfileEntityToDomainConverter`, `DomainToProfileEntityConverter` (或合併為一個 `ProfileMapper`)。由於沒有 MapStruct，我們將手動編寫靜態方法或在 Converter 類中實現。

## 5. Bootstrap

套件：`com.github.chunpingwang.ollamaagentoutcome.bootstrap`

*   **Configuration Class**: `UseCaseConfiguration` (`@Configuration`)
    *   使用 `@Bean` 註解註冊以下 Bean：
        *   `CreateProfileService`
        *   `GetProfileService`
        *   `UpdateProfileService`
        *   `DeleteProfileService`
        *   `JpaSaveProfilePort`
        *   `JpaFindProfileByIdPort`
        *   `JpaDeleteProfilePort` (如果需要)
        *   `ProfileRestController` (Spring MVC 掃描會自動找到它，但為了明確依賴注入，可以在這裡定義或直接讓 Spring Component Scan 處理。Prompt 說 "組裝 use case bean"，通常 Controller 由 Web Starter 管理，但 Use Case Services 需要 Application Context 來解析其依賴的 Ports。Ports 本身是簡單的 POJOs 依賴 Repository，Repository 由 Data JPA 提供。)
    *   *注意*：根據 Prompt，Bootstrap 負責組裝 Use Case Beans。Controller 可以通過 `@ComponentScan` 或直接在 Configuration 中定義為 Bean。為了清晰，我們在 `UseCaseConfiguration` 中定義所有 Service 和 Port 實現類作為 Bean。

## 6. 測試策略

### Unit Tests (JUnit + AssertJ + Mockito)

1.  **Domain Layer**:
    *   `EmailTest`: 驗證有效/無效 Email。
    *   `DisplayNameTest`: 驗證有效/無效 DisplayName。
    *   `ProfileTest`: 驗證 Profile 建立與更新的不變條件。
2.  **Application Layer**:
    *   `CreateProfileServiceTest`: Mock `SaveProfilePort`。測試成功、重複 Email 異常傳播。
    *   `GetProfileServiceTest`: Mock `FindProfileByIdPort`。測試找到/未找到。
    *   `UpdateProfileServiceTest`: Mock `Find...`, `Save...`。測試更新邏輯、不存在拋出異常、重複 Email 處理。
    *   `DeleteProfileServiceTest`: Mock `Find...`, `Delete...`。測試刪除存在/不存在的資料。

### Integration Tests (Spring Boot Testcontainers)

1.  **Persistence Adapter (`adapter-persistence/src/test/java`)**:
    *   `ProfileEntityTest`: JPA Entity 映射測試（使用 `@DataJpaTest`）。
    *   `JpaSaveProfilePortTest`: 測試 Port 實作，包括唯一性約束觸發的例外轉換。
    *   `JpaFindProfileByIdPortTest`: 測試查詢功能。
    *   `JpaDeleteProfilePortTest`: 測試刪除功能。
    *   使用 `@ServiceConnection` 連接 PostgreSQL Testcontainer。

2.  **Web Adapter (`adapter-web/src/test/java`)**:
    *   `ProfileRestControllerTest`: 使用 `@WebMvcTest(ProfileRestController.class)` + `@MockitoBean` for Use Cases。測試 HTTP 狀態碼、JSON 序列化/反序列化、錯誤映射。

3.  **BDD / E2E (`bootstrap/src/test/java/resources/features`)**:
    *   Feature File: `personal-profile-crud.feature`
    *   Step Definitions: `PersonalProfileSteps` (在 `bdd` 套件)。
    *   使用 `TestRestTemplate` 發送真實 HTTP 請求到嵌入式伺服器或 Testcontainers 運行的服務。
    *   覆蓋 Spec 中的所有場景：Create, Duplicate Email Error, Invalid Format Error, Get Success, Get Not Found, Update Success, Update Not Found, Delete Success, Confirm Deleted.

## 7. 類別清單

| 模組 | 完整路徑 (相對專案根目錄) | 職責描述 |
| :--- | :--- | :--- |
| Domain | `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/Email.java` | Value Object，封裝電子郵件地址及其驗證邏輯。 |
| Domain | `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/DisplayName.java` | Value Object，封裝顯示名稱及其驗證邏輯。 |
| Domain | `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/ProfileId.java` | Value Object，代表個人資料的唯一識別碼（UUID）。 |
| Domain | `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/Profile.java` | Entity/Aggregate Root，包含 ID、Email、Display Name 及基本業務規則。 |
| Domain | `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/exception/InvalidEmailException.java` | Domain Exception，表示 Email 格式無效。 |
| Domain | `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/exception/InvalidDisplayNameException.java` | Domain Exception，表示 Display Name 格式無效。 |
| Domain | `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/exception/DuplicateEmailException.java` | Domain Exception，表示嘗試使用已存在的 Email。 |
| Domain | `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/exception/ProfileNotFoundException.java` | Domain Exception，表示指定的 Profile ID 不存在。 |
| Application | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/in/CreateProfileUseCase.java` | Inbound Port Interface，定義建立個人資料的契約。 |
| Application | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/in/GetProfileUseCase.java` | Inbound Port Interface，定義查詢個人資料的契約。 |
| Application | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/in/UpdateProfileUseCase.java` | Inbound Port Interface，定義更新個人資料的契約。 |
| Application | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/in/DeleteProfileUseCase.java` | Inbound Port Interface，定義刪除個人資料的契約。 |
| Application | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/out/SaveProfilePort.java` | Outbound Port Interface，定義保存資料的契約。 |
| Application | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/out/FindProfileByIdPort.java` | Outbound Port Interface，定義根據 ID 查找資料的契約。 |
| Application | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/out/DeleteProfilePort.java` | Outbound Port Interface，定義刪除資料的契約。 |
| Application | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/CreateProfileService.java` | Use Case Implementation，協調建立流程並驗證業務規則。 |
| Application | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/GetProfileService.java` | Use Case Implementation，協調查詢流程。 |
| Application | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/UpdateProfileService.java` | Use Case Implementation，協調更新流程並處理衝突。 |
| Application | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/DeleteProfileService.java` | Use Case Implementation，協調刪除流程。 |
| Adapter-Web | `adapter-web/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/dto/CreateProfileRequest.java` | Request DTO，用於接收 POST 請求體。 |
| Adapter-Web | `adapter-web/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/dto/UpdateProfileRequest.java` | Request DTO，用於接收 PUT 請求體。 |
| Adapter-Web | `adapter-web/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/dto/ProfileResponse.java` | Response DTO，用於返回 JSON 回應。 |
| Adapter-Web | `adapter-web/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/ProfileRestController.java` | REST Controller，映射 HTTP 請求到 Use Cases。 |
| Adapter-Web | `adapter-web/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/GlobalExceptionHandler.java` | Exception Handler，將 Domain/Application 例外轉換為 HTTP 狀態碼。 |
| Adapter-Persistence | `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/entity/ProfileEntity.java` | JPA Entity，對應資料庫表結構。 |
| Adapter-Persistence | `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/repository/ProfileJpaRepository.java` | Spring Data Repository Interface，提供基礎數據訪問能力。 |
| Adapter-Persistence | `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/converter/ProfileConverter.java` | Converter，負責 Domain Profile <-> JPA Entity 的相互轉換。 |
| Adapter-Persistence | `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/port/JpaSaveProfilePort.java` | Port Implementation，實現 SaveProfilePort，處理 DB 約束衝突。 |
| Adapter-Persistence | `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/port/JpaFindProfileByIdPort.java` | Port Implementation，實現 FindProfileByIdPort。 |
| Adapter-Persistence | `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/port/JpaDeleteProfilePort.java` | Port Implementation，實現 DeleteProfilePort。 |
| Bootstrap | `bootstrap/src/main/java/com/github/chunpingwang/ollamaagentoutcome/bootstrap/Application.java` | Spring Boot Application Entry Point. |
| Bootstrap | `bootstrap/src/main/java/com/github/chunpingwang/ollamaagentoutcome/bootstrap/config/UseCaseConfiguration.java` | Configuration Class, defines beans for Use Cases and Ports. |
| BDD | `bootstrap/src/test/resources/features/personal-profile-crud.feature` | Cucumber Feature File describing user scenarios in Gherkin (zh-TW). |
| BDD | `bootstrap/src/test/java/com/github/chunpingwang/ollamaagentoutcome/bdd/steps/PersonalProfileSteps.java` | Step Definitions for executing HTTP requests against the API. |

*(註：測試類別如 `CreateProfileServiceTest`, `JpaSaveProfilePortTest` 等將位於各自模組的 `src/test/java` 對應路徑下，未在此列出以節省空間，但遵循相同的命名與結構規則。)*
