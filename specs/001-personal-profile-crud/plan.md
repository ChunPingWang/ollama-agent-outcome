# Plan 001 — 個人資料管理 (Personal Profile CRUD)

本計畫基於 Spec 001 設計，採用六角形架構（Ports and Adapters），嚴格遵循 SOLID 原則與 Spring Boot 4 / Java 17 技術棧規範。

## 領域模型 (Domain Model)

### Value Objects & Entities

所有實體直接位於 `{base_package}.domain` 套件下，不使用子套件。

- **ProfileId** (`record`)
    - 欄位：`String value`
    - 不變條件：非空字串，符合 UUID 格式（建議）。
    - 用途：作為 Domain Entity 的唯一識別碼。

- **Email** (`record`)
    - 欄位：`String address`
    - 不變條件：非空、符合標準 Email 正規表示式。
    - 用途：確保電子郵件格式的正確性。

- **Age** (`record`)
    - 欄位：`int years`
    - 不變條件：`years >= 0`。
    - 用途：封裝年齡邏輯，防止負數。

- **Profile** (`class`, immutable fields, mutable via methods or factory if needed, but here we use standard class with setters for update flow as per persistence adapter requirement to manage entity state)
    *修正*：為了配合 JPA 的受管實體更新流程（先 findById 再修改），以及 Domain Service 的純淨性，我們定義 `Profile` 為一個具有狀態的類別，但業務規則由 Domain Exception 強制執行。或者更優雅的方式是：Domain Entity 是不可變的，但在 Update Use Case 中，Port 實作負責載入現有記錄並應用變更。然而，Spec 要求「全量更新」，通常意味著 DTO 中的字段覆蓋現有字段。
    
    讓我們重新審視 Spec US-3：PUT `/profiles/{id}` 包含 name, email, age。這暗示了全量替換或合併。鑑於 Email 的唯一性約束，更新時若改變 Email，需檢查新 Email 是否與其他 ID 衝突。
    
    **決定**：`Profile` 是一個標準 Java Class (非 record)，因為它需要被 JPA 管理且可能需要 setter 或由 Port 實作進行屬性複製。但為了符合 SOLID 和領域驅動設計的精神，我們將核心邏輯放在 Value Objects 中，Entity 主要作為數據容器與行為入口。
    
    - 欄位：`ProfileId id`, `String name`, `Email email`, `Age age`
    - 方法：
        - `update(String newName, String newEmailStr, int newAge)`: 內部驗證並建立新的 Value Object 實例，更新自身狀態（或返回新實體，取決於實現細節）。為簡化 JPA 整合，這裡採用直接修改欄位的方式，但在修改前調用 VO 的靜態工廠方法進行驗證。

### Domain Exceptions

位於 `{base_package}.domain` 套件下。

- **ProfileNotFoundException**: 當查詢不存在的 Profile 時拋出。
- **DuplicateEmailException**: 當嘗試使用已存在的 Email 創建或更新時拋出。
- **InvalidAgeException**: 當年齡小於 0 時拋出。
- **InvalidEmailFormatException**: 當 Email 格式無效時拋出。

## Use Cases & Ports

所有 Inbound/Outbound ports 及 Usecase 實作位於 `application` 模組對應子套件。

### US-1: Create Profile (新增個人資料)

- **Inbound Port (`CreateProfileUseCase`)**:
    ```java
    void create(CreateProfileCommand command);
    // Command class in application.usecase package or just use record/dto? 
    // Better to define a simple DTO-like class for input.
    ```
    *修正*：根據規範，inbound port 介面以 `UseCase` 結尾。輸入參數建議定義在 `usecase` 套件或直接作為方法參數。為清晰起見，我們定義一個 `CreateProfileRequest` record 在 `usecase` 套件供 Use Case 接收。

- **Outbound Port (`SaveProfilePort`)**:
    ```java
    ProfileId save(Profile profile);
    ```

- **Implementation (`CreateProfileService`)**:
    - 驗證輸入（可在此或 Domain 層）。
    - 建立 `Profile` 實體。
    - 調用 `saveProfilePort.save(profile)`。
    - 處理可能的 `DuplicateEmailException`（由 Port 拋出）並重新拋出或轉換。

### US-2: Get Profile (查詢個人資料)

- **Inbound Port (`GetProfileUseCase`)**:
    ```java
    Optional<Profile> getById(String id);
    ```

- **Outbound Port (`LoadProfilePort`)**:
    ```java
    Optional<Profile> findById(String id);
    ```

- **Implementation (`GetProfileService`)**:
    - 調用 `loadProfilePort.findById(id)`。
    - 若不存在，選擇返回空 Optional 或由 Controller 判斷拋出異常。根據 Spec，404 是 HTTP 錯誤，故 Service 應返回 Optional，Controller 決定是否拋出 `ProfileNotFoundException`。

### US-3: Update Profile (更新個人資料)

- **Inbound Port (`UpdateProfileUseCase`)**:
    ```java
    void update(UpdateProfileCommand command);
    // Command contains id, name, email, age.
    ```

- **Outbound Ports**:
    1. `LoadProfilePort`: `Optional<Profile> findById(String id);` (重用)
    2. `SaveProfilePort`: `void save(Profile profile);` (重用，但需注意唯一性檢查邏輯可能在這裡或 Domain)
    
    *注意*：由於更新時可能改變 Email，且需確保新 Email 不與其他記錄衝突（除了當前 ID），這需要更複雜的查詢。為簡化，我們可以在 `SaveProfilePort` 實作中處理唯一性約束，或者在 Use Case 層面先載入所有相關數據？不，這樣效率低。
    
    更好的設計：`SaveProfilePort` 負責持久化。如果更新涉及唯一性變更，通常由資料庫約束 + 應用層預檢完成。
    讓我們定義一個專門用於更新的 Port 行為，或者直接複用 Save。鑑於 Spec 要求「全量更新」，我們可以將 DTO 映射到 Entity 後保存。若發生重複 Email 錯誤，Persistence Adapter 會捕捉 DB 異常並拋出 Domain Exception。

### US-4: Delete Profile (刪除個人資料)

- **Inbound Port (`DeleteProfileUseCase`)**:
    ```java
    void deleteById(String id);
    ```

- **Outbound Ports**:
    1. `LoadProfilePort`: `Optional<Profile> findById(String id);` (用於確認存在性)
    2. `RemoveProfilePort`: `void remove(Profile profile);`

- **Implementation (`DeleteProfileService`)**:
    - 調用 `loadProfilePort.findById(id)`。
    - 若不存在，拋出 `ProfileNotFoundException`（或返回空，讓 Controller 處理）。
    - 若存在，調用 `removeProfilePort.remove(profile)`。

## Web Adapter

位於 `{base_package}.adapter.web` 套件。

### Controllers

- **ProfileController** (`@RestController`)
    - `POST /profiles`: 呼叫 `CreateProfileUseCase.create()`。回傳 201 + Location Header。
    - `GET /profiles/{id}`: 呼叫 `GetProfileUseCase.getById()`。若無資料回傳 404；若有則回傳 200 + JSON Body。
    - `PUT /profiles/{id}`: 呼叫 `UpdateProfileUseCase.update()`。若 ID 不存在回傳 404；若驗證失敗回傳 400；若成功回傳 200 + JSON Body。
    - `DELETE /profiles/{id}`: 呼叫 `DeleteProfileUseCase.deleteById()`。若 ID 不存在回傳 404；若成功回傳 204 No Content。

### DTOs (Request/Response)

位於 `{base_package}.adapter.web.dto`? **不**，根據規則，Adapter 模組不得有子套件（除了 web/persistence 本身）。所以 DTO 直接放在 `{base_package}.adapter.web`？或者通常 DTO 會跟 Controller 在一起。規則說「adapter-web ... 的所有類別直接放在 {base_package}.adapter.web」。這意味著沒有 `dto` 子目錄。我們將 Request/Response classes 直接定義在該套件下，命名如 `CreateProfileRequest`, `ProfileResponse` 等。

- **CreateProfileRequest**: `name` (String, @NotBlank), `email` (String, @Email, @NotNull), `age` (Integer, @NotNull).
- **UpdateProfileRequest**: 同 CreateProfileRequest.
- **ProfileResponse**: `id` (String), `name` (String), `email` (String), `age` (int).

### Error Handling

- **GlobalExceptionHandler** (`@RestControllerAdvice`)
    - `MethodArgumentNotValidException`: 轉為 HTTP 400 Bad Request，返回詳細錯誤訊息。
    - `InvalidAgeException`, `InvalidEmailFormatException`: 轉為 HTTP 400 Bad Request。
    - `DuplicateEmailException`: 轉為 HTTP 409 Conflict。
    - `ProfileNotFoundException`: 轉為 HTTP 404 Not Found。
    - *禁止* 捕獲通用 `Exception.class`。

## Persistence Adapter

位於 `{base_package}.adapter.persistence` 套件。

### JPA Entity

- **ProfileEntity** (`@Entity`)
    - 實作 `Persistable<String>`? Spec 中 ID 是 UUID/String。若由 Domain 生成 ID（如 US-1），則需處理 isNew 狀態。
    - 欄位：`id` (String, PK), `name` (String), `email` (String, Unique), `age` (Integer)。
    - 註解：`@Table(uniqueConstraints = @UniqueConstraint(columnNames = "email"))`。
    - 由於 Domain 可能自行產生 ID（例如在 Create Use Case 中生成 UUID），我們需要讓 Entity 支持 Persistable 以區分 Insert/Update，或者依賴 Spring Data JPA 的自動檢測。為了穩健性，遵循 Prompt 建議：實作 `Persistable<String>`，使用 `isNew` flag。

### Repository

- **ProfileRepository** (`extends JpaRepository<ProfileEntity, String>`, `JpaSpecificationExecutor`?)
    - 不需要自訂方法，除非有複雜查詢。這裡只需要標準 CRUD。但為了檢查 Email 唯一性或刪除前的存在性，可能需要 `findByEmail(String email)`。這有助於在 Update 時進行應用層的唯一性預檢（雖然 DB 約束是最後的防線）。讓我們加上 `Optional<ProfileEntity> findByEmailAndIdNotIn(String email, List<String> idsToExclude);`? 不，太複雜。簡單點：`Optional<ProfileEntity> findByEmail(String email);`。

### Port Implementations

- **SaveProfilePortImpl**: 實現 `application.port.out.SaveProfilePort`。將 Domain `Profile` 轉換為 `ProfileEntity`，調用 `repository.save(entity)` (注意 flush)。
- **LoadProfilePortImpl**: 實現 `application.port.out.LoadProfilePort`。將 `ProfileEntity` 轉換回 Domain `Profile`。
- **RemoveProfilePortImpl**: 實現 `application.port.out.RemoveProfilePort`。先 `findById` 獲取受管實體，再 `delete`。若不存在則拋出異常或返回空？通常 Remove 會假設已驗證存在。

### Converter/Mapper

- **ProfileConverter** (`@Component`? No, pure logic. Static methods or separate class). 
    - `toDomain(ProfileEntity entity)`: Profile -> Domain.Profile
    - `toEntity(Profile profile)`: Domain.Profile -> ProfileEntity
    - *注意*：由於 Entity 需要處理 `isNew`，在 `toEntity` 時需設置該標誌。

## Bootstrap

位於 `{base_package}.bootstrap` 套件。

- **UseCaseConfiguration** (`@Configuration`)
    - 定義所有 Application Layer Ports 的 Bean（如果需要代理）或直接注入 Use Case Services。
    - 主要任務是組裝 `CreateProfileService`, `GetProfileService`, `UpdateProfileService`, `DeleteProfileService` 為 Spring Beans，供 Web Adapter 依賴注入。
    - 確保 Port 實作類也被註冊為 Beans（如果它們有狀態或需要測試替換）。

## 測試策略

1.  **Unit Tests (JUnit + Mockito)**:
    -   **Domain**: 測試 Value Objects 的不變條件與業務規則（如年齡負數檢查、Email 格式）。測試 `Profile` 實體的狀態變更邏輯。
    -   **Application**: 測試各 Use Case Service。模擬 Outbound Ports，驗證是否調用了正確的 Port 方法，以及異常傳播行為。例如：當 `SavePort` 拋出 `DuplicateEmailException` 時，`CreateService` 應正確傳遞此異常。

2.  **Integration Tests (Spring Boot Testcontainers)**:
    -   **Persistence Adapter**: 使用 `@DataJpaTest` + Testcontainers PostgreSQL。測試 Repository 操作及 Port 實作的轉換邏輯。特別關注唯一性約束觸發時的例外處理 (`DataIntegrityViolationException` -> Domain Exception)。
    -   **Web Adapter**: 使用 `@WebMvcTest` + Mock MVC。模擬 Controller 呼叫 Use Case Bean（Mocked），驗證 HTTP 狀態碼、回應體結構及錯誤訊息格式。

3.  **BDD / E2E Tests (Cucumber)**:
    -   Feature File: `bootstrap/src/test/resources/features/personal-profile-crud.feature`
    -   Step Definitions: `bootstrap/src/test/java/.../bdd/PersonalProfileSteps.java`
    -   使用 `TestRestTemplate` 發送真實 HTTP 請求至內嵌伺服器。
    -   場景涵蓋 Spec 中的所有驗收條件：成功 CRUD、各種失敗情況（400, 404, 409）。

## 類別清單

| 模組 | 完整路徑 (相對專案根目錄) | 職責描述 |
| :--- | :--- | :--- |
| **Domain** | `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/ProfileId.java` | Value Object，封裝 Profile ID 邏輯與驗證。 |
| **Domain** | `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/Email.java` | Value Object，封裝 Email 地址及其格式驗證。 |
| **Domain** | `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/Age.java` | Value Object，封裝年齡數值及非負整數約束。 |
| **Domain** | `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/Profile.java` | Domain Entity，代表個人資料的核心業務對象。 |
| **Domain** | `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/exception/ProfileNotFoundException.java` | 自訂例外，表示找不到指定的個人資料。 |
| **Domain** | `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/exception/DuplicateEmailException.java` | 自訂例外，表示電子郵件已重複。 |
| **Domain** | `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/exception/InvalidAgeException.java` | 自訂例外，表示年齡無效（如負數）。 |
| **Domain** | `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/exception/InvalidEmailFormatException.java` | 自訂例外，表示 Email 格式不正確。 |
| **Application** | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/in/CreateProfileUseCase.java` | Inbound Port 介面，定義建立資料的契約。 |
| **Application** | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/in/GetProfileUseCase.java` | Inbound Port 介面，定義查詢資料的契約。 |
| **Application** | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/in/UpdateProfileUseCase.java` | Inbound Port 介面，定義更新資料的契約。 |
| **Application** | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/in/DeleteProfileUseCase.java` | Inbound Port 介面，定義刪除資料的契約。 |
| **Application** | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/out/SaveProfilePort.java` | Outbound Port 介面，定義保存資料的契約。 |
| **Application** | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/out/LoadProfilePort.java` | Outbound Port 介面，定義載入資料的契約。 |
| **Application** | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/out/RemoveProfilePort.java` | Outbound Port 介面，定義移除資料的契約。 |
| **Application** | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/CreateProfileRequest.java` | Use Case 輸入 DTO (Record)。 |
| **Application** | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/UpdateProfileCommand.java` | Use Case 輸入 Command (Class with fields). |
| **Application** | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/CreateProfileService.java` | Use Case 實作，處理建立業務邏輯。 |
| **Application** | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/GetProfileService.java` | Use Case 實作，處理查詢業務邏輯。 |
| **Application** | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/UpdateProfileService.java` | Use Case 實作，處理更新業務邏輯。 |
| **Application** | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/DeleteProfileService.java` | Use Case 實作，處理刪除業務邏輯。 |
| **Adapter-Web** | `adapter-web/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/ProfileController.java` | REST Controller，處理 HTTP 請求與回應映射。 |
| **Adapter-Web** | `adapter-web/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/CreateProfileRequestDto.java` | Web Layer Request DTO (with Jakarta Validation). |
| **Adapter-Web** | `adapter-web/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/UpdateProfileRequestDto.java` | Web Layer Request DTO. |
| **Adapter-Web** | `adapter-web/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/ProfileResponseDto.java` | Web Layer Response DTO. |
| **Adapter-Web** | `adapter-web/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/GlobalExceptionHandler.java` | 全域異常處理器，轉換 Domain Exception 為 HTTP Status。 |
| **Adapter-Persistence** | `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/ProfileEntity.java` | JPA Entity，對應資料庫表結構。 |
| **Adapter-Persistence** | `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/ProfileRepository.java` | Spring Data Repository 介面。 |
| **Adapter-Persistence** | `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/SaveProfilePortImpl.java` | Outbound Port 實作，負責 Domain -> Entity 轉換與保存。 |
| **Adapter-Persistence** | `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/LoadProfilePortImpl.java` | Outbound Port 實作，負責 Entity -> Domain 轉換與載入。 |
| **Adapter-Persistence** | `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/RemoveProfilePortImpl.java` | Outbound Port 實作，負責刪除操作。 |
| **Bootstrap** | `bootstrap/src/main/java/com/github/chunpingwang/ollamaagentoutcome/bootstrap/Application.java` | Spring Boot 啟動入口。 |
| **Bootstrap** | `bootstrap/src/main/java/com/github/chunpingwang/ollamaagentoutcome/bootstrap/UseCaseConfiguration.java` | 組裝 Application Layer Beans (Services) 的 Configuration。 |
