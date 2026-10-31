# Plan 001 — 個人資料管理 (Personal Profile CRUD)

本文件定義了「個人資料管理」功能的技術設計細節，遵循六角形架構（Hexagonal Architecture），確保領域邏輯獨立於基礎設施，並支援 TDD/BDD 開發流程。

## 1. 領域模型 (Domain Model)

### Value Objects & Entities

所有類別直接位於 `{base_package}.domain` 套件下，不使用子套件。

#### `ProfileId` (record)
- **類型**：Value Object / Identifier
- **欄位**：`UUID value`
- **不變條件**：值不得為 null。
- **用途**：作為 Domain Entity 的唯一識別碼，避免在業務邏輯中使用原始 String 或 UUID。

#### `Email` (record)
- **類型**：Value Object
- **欄位**：`String address`
- **不變條件**：
    - 格式需符合標準 Email 規範（基本結構檢查）。
    - 長度限制建議在 DTO 層處理，但此 VO 應保持純淨，僅儲存有效字串。
- **方法**：
    - `isValid()`: boolean，內部可包含簡單的 regex 驗證以確保語意正確性。

#### `Name` (record)
- **類型**：Value Object
- **欄位**：`String text`
- **不變條件**：非空、非空白、長度合理範圍內。

#### `Profile` (class, immutable except for ID generation strategy via constructor)
- **類型**：Entity
- **欄位**：
    - `private final ProfileId id;`
    - `private Name name;`
    - `private Email email;`
- **建構子**：
    - `Profile(ProfileId id, Name name, Email email)`：用於從 Repository 載入已存在資料時使用。
    - *注意*：根據 Persistence 陷阱規則，若 Domain 自行產生 ID（UUID），JPA Entity 需實作 `Persistable<UUID>`。但在 Domain 層，我們假設 ID 由外部（或 Application 層透過 UUID 生成）提供並傳入，或者 Domain 本身不負責持久化識別碼的生成策略，而是接受一個唯一的 ID。為了符合「系統自動產生」且避免 JPA 衝突，我們將 ID 視為輸入的一部分或由應用層決定。然而，標準做法是 Domain Entity 不應依賴 JPA。這裡採用：**Domain Entity 擁有不可變的 ID**。在建立新實體時，Application/Adapter 會先產生 UUID 再傳入建構子。
- **業務方法**：
    - `updateName(Name newName)`: 更新姓名。檢查新名稱是否有效。
    - `updateEmail(Email newEmail)`: 更新電子郵件。檢查新信箱格式及唯一性（唯一性由 Port 處理，此處僅做語意驗證）。
    - `getId()`, `getName()`, `getEmail()`

### Domain Exceptions

#### `InvalidProfileException` (class, extends RuntimeException)
- **用途**：當個人資料的狀態無效、欄位驗證失敗時拋出。
- **子類別**：
    - `DuplicateEmailException` (extends InvalidProfileException): 表示嘗試使用已存在的 Email。
    - `ProfileNotFoundException` (extends InvalidProfileException): 表示指定的 ID 不存在於儲存體中。

## 2. Use Cases 與 Ports

所有介面位於 `{base_package}.application.port.in` 和 `{base_package}.application.port.out`。實作類位於 `{base_package}.application.usecase`。

### Inbound Ports (Use Case Interfaces)

1.  **`CreateProfileUseCase`**
    ```java
    public interface CreateProfileUseCase {
        void execute(CreateProfileCommand command);
    }
    // Command: record CreateProfileCommand(Name name, Email email)
    ```

2.  **`LoadProfileUseCase`**
    ```java
    public interface LoadProfileUseCase {
        ProfileResult execute(ProfileId id);
    }
    // Result: record ProfileResult(Name name, Email email)
    ```

3.  **`UpdateProfileUseCase`**
    ```java
    public interface UpdateProfileUseCase {
        void execute(UpdateProfileCommand command);
    }
    // Command: record UpdateProfileCommand(ProfileId id, Name name, Email email)
    ```

4.  **`DeleteProfileUseCase`**
    ```java
    public interface DeleteProfileUseCase {
        void execute(ProfileId id);
    }
    ```

### Outbound Ports (Repository Interfaces)

這些介面定義了領域與持久化層之間的契約。

1.  **`SaveProfilePort`**
    ```java
    public interface SaveProfilePort {
        void save(Profile profile);
    }
    ```

2.  **`FindProfileByIdPort`**
    ```java
    public interface FindProfileByIdPort {
        Optional<Profile> findById(ProfileId id);
    }
    ```

### Use Case Implementations

位於 `{base_package}.application.usecase`：

-   `CreateProfileService`: 實現 `CreateProfileUseCase`。依賴 `SaveProfilePort`。負責呼叫 port.save()，若發生重複則由 Port 或 Domain 拋出異常。
-   `LoadProfileService`: 實現 `LoadProfileUseCase`。依賴 `FindProfileByIdPort`。若找不到則拋出 `ProfileNotFoundException`。
-   `UpdateProfileService`: 實現 `UpdateProfileUseCase`。依賴 `FindProfileByIdPort` 和 `SaveProfilePort`。先查詢實體，更新狀態，再儲存。若找不到則拋出例外。
-   `DeleteProfileService`: 實現 `DeleteProfileUseCase`。依賴 `FindProfileByIdPort` 和 `SaveProfilePort`（或直接刪除接口，但為保持一致性可保留 find+delete 或新增 delete 方法於 Port）。*優化*：為了效能與簡潔，Outbound Port 可增加 `void deleteById(ProfileId id)` 或讓 Service 層處理「查無資料」的邏輯後不執行任何操作？根據 Spec，刪除不存在應回 404，因此 Service 必須先檢查是否存在。故 `FindProfileByIdPort` 足夠，若 Optional.empty 則拋出 NotFoundException，否則調用一個假設存在的刪除機制？或者 Outbound Port 定義 `boolean exists(...)`? 
    *修正*：標準做法是 `DeleteProfilePort` 包含 `void delete(Profile profile)` 或 `void deleteById(ProfileId id)`。但在本架構中，我們希望 Domain Entity 被傳遞。然而，對於刪除，通常只需要 ID。讓我們定義 `DeleteProfilePort` 接收 Profile 實例（因為它需要知道要刪哪筆）或僅 ID。考慮到 SOLID 和單一職責，且 Find 已經由另一個 Port 負責，這裡我們可以簡單地在 `DeleteProfileService` 中先呼叫 `find`，如果存在，再呼叫 `save` (如果是軟刪除) 或直接刪除。
    
    *重新審視 Persistence Adapter 設計*：Spring Data JPA Repository 天生支持 `deleteById`。為了符合 Hexagonal，Outbound Port 應該抽象這個行為。
    新增 **`RemoveProfilePort`**:
    ```java
    public interface RemoveProfilePort {
        void removeById(ProfileId id);
    }
    ```
    這樣 `DeleteProfileService` 可以依賴 `FindProfileByIdPort` 來檢查是否存在，若存在則調用 `RemoveProfilePort.removeById(id)`。

## 3. Web Adapter

位於 `{base_package}.adapter.web`。

### Controller: `ProfileController`

-   `POST /profiles`: 
    -   方法：`create(@Valid @RequestBody CreateProfileRequest request)`
    -   回傳：201 Created, Header Location: `/profiles/{id}`
    -   DTO: `CreateProfileRequest` (record with name, email).
-   `GET /profiles/{id}`:
    -   方法：`get(@PathVariable("id") String idStr)` -> 轉換為 UUID/ProfileId
    -   回傳：200 OK, Body JSON
    -   DTO: `ProfileResponse` (record with id, name, email)
-   `PUT /profiles/{id}`:
    -   方法：`update(@PathVariable("id") String idStr, @Valid @RequestBody UpdateProfileRequest request)`
    -   回傳：200 OK
    -   DTO: `UpdateProfileRequest` (record with name, email)
-   `DELETE /profiles/{id}`:
    -   方法：`delete(@PathVariable("id") String idStr)`
    -   回傳：204 No Content

### Error Handling: `GlobalExceptionHandler` (`@RestControllerAdvice`)

-   `MethodArgumentNotValidException`: 轉為 **400 Bad Request**。
-   `HttpMessageNotReadableException`: 轉為 **400 Bad Request** (JSON 格式錯誤)。
-   `DuplicateEmailException` (Domain): 轉為 **409 Conflict**, body 包含錯誤訊息。
-   `ProfileNotFoundException` (Domain): 轉為 **404 Not Found**.
-   *注意*：不攔截 `Exception.class`，以免覆蓋 Spring Boot 預設的 404/5xx 行為。

## 4. Persistence Adapter

位於 `{base_package}.adapter.persistence`。

### JPA Entity: `ProfileEntity`

-   實作 `Persistable<UUID>`。
-   欄位：
    -   `@Id UUID id;`
    -   `String name;`
    -   `String email;`
    -   `@Transient boolean isNew = true;`
-   方法：
    -   `isNew()`: return this.isNew;
    -   `setNew(boolean new)`: this.isNew = new;
    -   `@PostLoad`, `@PostPersist`: 將 `isNew` 設為 false。
-   唯一性約束：`@Column(unique = true, nullable = false)` on `email`.

### Repository Interface: `ProfileRepository`

-   extends `JpaRepository<ProfileEntity, UUID>`, `JpaSpecificationExecutor<ProfileEntity>` (若需要複雜查詢，此處僅需基本 CRUD)。
-   額外定義：`Optional<ProfileEntity> findByEmail(String email);` (用於檢查重複，雖然 Port 層可能由 Service 處理邏輯，但 DB 層索引能加速)。*修正*：根據 Spec，重複檢查是業務規則。Port 實作中會呼叫 saveAndFlush，並捕捉 DataIntegrityViolationException。因此 Repository 不需要額外的 find by email，除非為了效能預先檢查。這裡選擇在 Save Profile Port 中直接嘗試保存並捕獲異常，以確保最終一致性與簡單的 Domain 模型依賴。

### Outbound Ports Implementation

1.  **`JpaSaveProfilePort`** implements `SaveProfilePort`
    -   注入 `ProfileRepository`。
    -   `save(Profile profile)`: 
        -   轉換 `Domain.Profile` -> `ProfileEntity`。
        -   調用 `repository.saveAndFlush(entity)`。
        -   捕捉 `DataIntegrityViolationException`，拋出 `DuplicateEmailException`。
    
2.  **`JpaFindProfileByIdPort`** implements `FindProfileByIdPort`
    -   注入 `ProfileRepository`。
    -   `findById(ProfileId id)`: 
        -   調用 `repository.findById(id.getValue())`。
        -   若存在，轉換為 `Domain.Profile`；否則回傳 `Optional.empty()`。

3.  **`JpaRemoveProfilePort`** implements `RemoveProfilePort`
    -   注入 `ProfileRepository`。
    -   `removeById(ProfileId id)`: 
        -   調用 `repository.deleteById(id.getValue())`。

### Converter: `ProfileConverter` (Utility Class or Component in Adapter)

-   靜態方法或無狀態 Bean，負責 Domain Object <-> JPA Entity 的雙向轉換。
-   `toEntity(Domain.Profile, boolean isNew)`
-   `fromEntity(ProfileEntity)`

## 5. Bootstrap

位於 `{base_package}.bootstrap`。

### Configuration: `UseCaseConfiguration` (`@Configuration`)

使用 `@Bean` 定義 Application Layer 的服務實例，並注入所需的 Outbound Ports（這些 Port 由 Spring 自動掃描並註冊為 Beans，因為它們是介面且其實作類在 classpath 中）。

```java
@Configuration
public class UseCaseConfiguration {

    @Bean
    CreateProfileService createProfileService(SaveProfilePort saveProfilePort) {
        return new CreateProfileService(saveProfilePort);
    }

    @Bean
    LoadProfileService loadProfileService(FindProfileByIdPort findProfileByIdPort) {
        return new LoadProfileService(findProfileByIdPort);
    }

    @Bean
    UpdateProfileService updateProfileService(FindProfileByIdPort findProfileByIdPort, SaveProfilePort saveProfilePort) {
        return new UpdateProfileService(findProfileByIdPort, saveProfilePort);
    }

    @Bean
    DeleteProfileService deleteProfileService(FindProfileByIdPort findProfileByIdPort, RemoveProfilePort removeProfilePort) {
        return new DeleteProfileService(findProfileByIdPort, removeProfilePort);
    }
}
```

*注意*：Adapter 的 Bean (如 `JpaSaveProfilePort`) 不需要在此處手動宣告，只要它們被標記為 `@Component` 或位於 Spring Boot 掃描路徑下即可。由於 `adapter-persistence` 模組中的 Port 實作類別通常會加上 `@Repository` 或直接由 Spring Data JPA 自動代理 Repository，而自定義 Port 實現類若需注入 Repository，應標註 `@Repository` 或讓 Bootstrap 組裝。根據規則，Application Layer 不依賴 Spring，所以 Port 介面在 Application，實現在 Persistence Adapter。Persistence Adapter 的實作類可以加 `@Repository` 以便 Spring 管理其生命週期並允許注入 JPA Repositories。Bootstrap 只需負責 Use Case Service 的組裝。

## 6. 測試策略

### Unit Tests (JUnit + Mockito + AssertJ)

-   **Domain**: 
    -   `ProfileTest`: 驗證建構子、更新方法的不變條件與狀態變更。
    -   `EmailTest`, `NameTest`: Value Object 驗證。
-   **Application**:
    -   `CreateProfileServiceTest`: Mock `SaveProfilePort`。測試成功保存、重複 Email 異常傳遞。
    -   `LoadProfileServiceTest`: Mock `FindProfileByIdPort`。測試找到資料、找不到資料拋出例外。
    -   `UpdateProfileServiceTest`: Mock Find/Save Ports。測試更新邏輯、404 情況。
    -   `DeleteProfileServiceTest`: Mock Find/Remove Ports。測試刪除前檢查存在性。

### Integration Tests (Spring Boot Testcontainers)

-   **Adapter-Persistence (`adapter-persistence/src/test/java...`)**:
    -   `JpaSaveProfilePortTest`: 使用 `@DataJpaTest` + Testcontainers PostgreSQL。測試 `saveAndFlush` 在唯一約束衝突時丟出 `DataIntegrityViolationException`，並確認轉換正確。
    -   `JpaFindProfileByIdPortTest`: 測試查詢功能。
    -   `JpaRemoveProfilePortTest`: 測試刪除功能。
    
-   **Adapter-Web (`adapter-web/src/test/java...`)**:
    -   `ProfileControllerTest`: 使用 `@WebMvcTest(ProfileController.class)` + `@MockitoBean` for Services。測試 HTTP 狀態碼、JSON 序列化、路徑變數解析、錯誤映射（400, 404, 409）。

### BDD Tests (Cucumber)

-   **Feature File**: `bootstrap/src/test/resources/features/personal-profile-crud.feature`
-   **Step Definitions**: `bootstrap/src/test/java/com/github/chunpingwang/ollamaagentoutcome/bdd/PersonalProfileSteps.java`
-   **Runner**: 利用骨架提供的 Cucumber Runner，確保啟動完整 Spring Context (`@SpringBootTest`) 與 Testcontainers DB。
-   **Scenario Flow**: 
    1.  Setup: Insert initial data via Repository or API if needed (usually clean state per scenario).
    2.  Execute: Call REST APIs using `TestRestTemplate`.
    3.  Verify: Check Status Code and Response Body/Header.

## 7. 類別清單

| 模組 | 套件 | 類別名稱 | 相對路徑 (src/main/java/...) | 職責描述 |
| :--- | :--- | :--- | :--- | :--- |
| Domain | domain | ProfileId | .../domain/ProfileId.java | 個人資料唯一識別碼 Value Object |
| Domain | domain | Email | .../domain/Email.java | 電子郵件地址 Value Object |
| Domain | domain | Name | .../domain/Name.java | 姓名 Value Object |
| Domain | domain | Profile | .../domain/Profile.java | 個人資料 Entity，包含業務邏輯 |
| Domain | domain | InvalidProfileException | .../domain/InvalidProfileException.java | 基礎領域例外 |
| Domain | domain | DuplicateEmailException | .../domain/DuplicateEmailException.java | 重複電子郵件例外 |
| Domain | domain | ProfileNotFoundException | .../domain/ProfileNotFoundException.java | 找不到資料例外 |
| Application | port.in | CreateProfileUseCase | .../application/port/in/CreateProfileUseCase.java | 新增資料輸入端口介面 |
| Application | port.in | LoadProfileUseCase | .../application/port/in/LoadProfileUseCase.java | 查詢資料輸入端口介面 |
| Application | port.in | UpdateProfileUseCase | .../application/port/in/UpdateProfileUseCase.java | 更新資料輸入端口介面 |
| Application | port.in | DeleteProfileUseCase | .../application/port/in/DeleteProfileUseCase.java | 刪除資料輸入端口介面 |
| Application | port.out | SaveProfilePort | .../application/port/out/SaveProfilePort.java | 儲存資料輸出端口介面 |
| Application | port.out | FindProfileByIdPort | .../application/port/out/FindProfileByIdPort.java | 依 ID 查找資料輸出端口介面 |
| Application | port.out | RemoveProfilePort | .../application/port/out/RemoveProfilePort.java | 移除資料輸出端口介面 |
| Application | usecase | CreateProfileService | .../application/usecase/CreateProfileService.java | 實作新增邏輯，協調 Port |
| Application | usecase | LoadProfileService | .../application/usecase/LoadProfileService.java | 實作查詢邏輯，協調 Port |
| Application | usecase | UpdateProfileService | .../application/usecase/UpdateProfileService.java | 實作更新邏輯，協調 Port |
| Application | usecase | DeleteProfileService | .../application/usecase/DeleteProfileService.java | 實作刪除邏輯，協調 Port |
| Adapter-Web | web | ProfileController | .../adapter/web/ProfileController.java | REST API 端點控制器 |
| Adapter-Web | web | GlobalExceptionHandler | .../adapter/web/GlobalExceptionHandler.java | 全域異常處理與 HTTP 狀態映射 |
| Adapter-Web | web (DTO) | CreateProfileRequest | .../adapter/web/CreateProfileRequest.java | 建立請求 DTO |
| Adapter-Web | web (DTO) | UpdateProfileRequest | .../adapter/web/UpdateProfileRequest.java | 更新請求 DTO |
| Adapter-Web | web (DTO) | ProfileResponse | .../adapter/web/ProfileResponse.java | 回應 DTO |
| Adapter-Persistence | persistence | ProfileEntity | .../adapter/persistence/ProfileEntity.java | JPA Entity，對應資料庫表 |
| Adapter-Persistence | persistence | ProfileRepository | .../adapter/persistence/ProfileRepository.java | Spring Data JPA Repository Interface |
| Adapter-Persistence | persistence | JpaSaveProfilePort | .../adapter/persistence/JpaSaveProfilePort.java | SaveProfilePort 的 JPA 實作 |
| Adapter-Persistence | persistence | JpaFindProfileByIdPort | .../adapter/persistence/JpaFindProfileByIdPort.java | FindProfileByIdPort 的 JPA 實作 |
| Adapter-Persistence | persistence | JpaRemoveProfilePort | .../adapter/persistence/JpaRemoveProfilePort.java | RemoveProfilePort 的 JPA 實作 |
| Bootstrap | bootstrap | UseCaseConfiguration | .../bootstrap/UseCaseConfiguration.java | Spring Bean 組裝配置類 |
