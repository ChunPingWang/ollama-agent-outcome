# 技術設計：個人資料管理 (Spec 001)

## 1. 領域模型 (Domain Model)

本層級包含純 Java 類別，不依賴任何框架。

### 1.1 Value Objects

*   **`ProfileId`** (`record`)
    *   內容：`String id`
    *   不變條件：ID 為非空字串，格式為 UUID 或系統生成的唯一標識。
*   **`Email`** (`record`)
    *   內容：`String value`
    *   不變條件：符合 RFC 5322 標準的電子郵件格式。
    *   驗證邏輯：在建構子中檢查格式，若無效則拋出 `InvalidEmailException`。
*   **`Name`** (`record`)
    *   內容：`String value`
    *   不變條件：非空、非空白字串。
*   **`Age`** (`record`)
    *   內容：`int value`
    *   不變條件：必須為正整數 (`> 0`)。
    *   驗證邏輯：在建構子中檢查，若 `<= 0` 則拋出 `InvalidAgeException`。

### 1.2 Entity

*   **`Profile`** (`record`)
    *   內容：`ProfileId id`, `Email email`, `Name name`, `Age age`
    *   不變條件：
        *   電子郵件在系統中必須唯一（由 Domain Service 或 Application 層確保，或透過 DB 約束）。
        *   所有欄位不可為 null。
    *   行為：
        *   `update(Name newName, Age newAge)`: 建立並回傳一個新的 `Profile` 實例，反映更新後的狀態（Immutable Entity 模式）。

### 1.3 Domain Exceptions

*   **`ProfileNotFoundException`**：當查詢或更新不存在的 ID 時拋出。
*   **`DuplicateEmailException`**：當建立或更新時，電子郵件已存在於系統中時拋出。
*   **`InvalidEmailException`**：當電子郵件格式無效時拋出。
*   **`InvalidAgeException`**：當年齡非正整數時拋出。

## 2. Use Cases 與 Ports (Application Layer)

本層級定義業務邏輯流程，僅依賴 `domain`。

### 2.1 Inbound Ports (Use Cases)

1.  **`CreateProfileUseCase`**
    *   `void execute(CreateProfileRequest request)`
    *   邏輯：驗證輸入 → 檢查電子郵件唯一性 → 建立 Profile → 儲存。
2.  **`GetProfileUseCase`**
    *   `Profile execute(String profileId)`
    *   邏輯：根據 ID 查詢 → 若不存在拋出 `ProfileNotFoundException`。
3.  **`UpdateProfileUseCase`**
    *   `Profile execute(String profileId, UpdateProfileRequest request)`
    *   邏輯：查詢現有 Profile → 若不存在拋出 `ProfileNotFoundException` → 驗證新資料 → 執行更新邏輯 → 儲存。
4.  **`DeleteProfileUseCase`**
    *   `void execute(String profileId)`
    *   邏輯：查詢現有 Profile → 若不存在拋出 `ProfileNotFoundException` → 刪除。

### 2.2 Outbound Ports (Repositories)

1.  **`SaveProfilePort`**
    *   `Profile save(Profile profile)`
2.  **`LoadProfilePort`**
    *   `Optional<Profile> findById(String profileId)`
3.  **`DeleteProfilePort`**
    *   `void deleteById(String profileId)`
4.  **`ExistsByEmailPort`** (可選，或合併至 Save/Load 的異常處理，但為清晰起見建議獨立或透過 Save 時的唯一性約束異常處理。此處採用 Save 時若發生唯一性衝突則由 Adapter 層轉換為 Domain Exception 的方式，或 Application 層先檢查。考慮到效能與一致性，建議 Application 層先檢查 `exists`，若存在則拋出 `DuplicateEmailException`。)
    *   `boolean existsByEmail(String email)`

*註：為符合 SOLID 介面分離原則，我們將 Port 細分為 `SaveProfilePort`, `LoadProfilePort`, `DeleteProfilePort`, `ExistsByEmailPort`。*

### 2.3 Use Case Implementations (Services)

*   `CreateProfileService`：實作 `CreateProfileUseCase`。
*   `GetProfileService`：實作 `GetProfileUseCase`。
*   `UpdateProfileService`：實作 `UpdateProfileUseCase`。
*   `DeleteProfileService`：實作 `DeleteProfileUseCase`。

## 3. Web Adapter (REST API)

本層級處理 HTTP 請求/回應，依賴 `application`。

### 3.1 Controller

*   **`ProfileController`** (`@RestController`)
    *   `POST /profiles` → `createProfile(CreateProfileRequest)`
        *   呼叫 `CreateProfileUseCase`
        *   成功：回傳 201 Created，Location Header 指向新資源。
        *   失敗：
            *   `InvalidEmailException`, `InvalidAgeException` → 400 Bad Request
            *   `DuplicateEmailException` → 409 Conflict
    *   `GET /profiles/{id}` → `getProfile(@PathVariable String id)`
        *   呼叫 `GetProfileUseCase`
        *   成功：回傳 200 OK 與 Profile DTO。
        *   失敗：`ProfileNotFoundException` → 404 Not Found
    *   `PUT /profiles/{id}` → `updateProfile(@PathVariable String id, UpdateProfileRequest request)`
        *   呼叫 `UpdateProfileUseCase`
        *   成功：回傳 200 OK 與更新後的 Profile DTO。
        *   失敗：
            *   `ProfileNotFoundException` → 404 Not Found
            *   `InvalidEmailException`, `InvalidAgeException` → 400 Bad Request
            *   `DuplicateEmailException` → 409 Conflict
    *   `DELETE /profiles/{id}` → `deleteProfile(@PathVariable String id)`
        *   呼叫 `DeleteProfileUseCase`
        *   成功：回傳 204 No Content。
        *   失敗：`ProfileNotFoundException` → 404 Not Found

### 3.2 DTOs

*   **`CreateProfileRequest`**
    *   `String email` (驗證：@Email, not blank)
    *   `String name` (驗證：not blank)
    *   `Integer age` (驗證：@Min(1))
*   **`UpdateProfileRequest`**
    *   `String email` (驗證：@Email, not blank)
    *   `String name` (驗證：not blank)
    *   `Integer age` (驗證：@Min(1))
*   **`ProfileResponse`**
    *   `String id`
    *   `String email`
    *   `String name`
    *   `Integer age`

### 3.3 Error Handling

*   **`ProfileControllerAdvice`** (`@RestControllerAdvice`)
    *   處理 `MethodArgumentNotValidException` → 400
    *   處理 `ProfileNotFoundException` → 404
    *   處理 `DuplicateEmailException` → 409
    *   處理 `InvalidEmailException`, `InvalidAgeException` → 400
    *   *注意：不攔截 `Exception`，讓 Spring 處理其他未預期錯誤（如 404 路徑不存在）。*

## 4. Persistence Adapter (JPA)

本層級處理資料持久化，依賴 `application` (Ports) 與 `domain`。

### 4.1 JPA Entity

*   **`ProfileEntity`**
    *   `@Id String id` (對應 ProfileId)
    *   `@Column(unique = true) String email`
    *   `String name`
    *   `Integer age`
    *   實作 `Persistable<String>` 以處理 ID 生成邏輯（若 ID 由 Domain 產生，則 `isNew()` 需正確判斷）。
    *   *註：根據 Spec，ID 由系統產生或外部指定？Spec 提到 "回應內容應包含新建立的個人資料 ID"，且測試場景中假設 "ID 為 profile-123"。為簡化，假設 ID 由 Domain 層在建立時產生（例如 UUID），或由 Client 提供。若由 Client 提供，需確保唯一性。此處假設 ID 由 Domain 層產生（UUID），故 Entity 不需 `@GeneratedValue`，而是由 Domain 設定 ID。*
    *   修正：根據 Spec "假設 系統中已存在 ID 為 "profile-123" 的個人資料"，這暗示 ID 可能是預設的或測試用的。在實際實作中，建議使用 UUID。為符合 "Domain 自行產生識別碼" 的陷阱提示，我們將使用 `ProfileId` (UUID) 作為 ID，並在 Domain 層生成。

### 4.2 Repository

*   **`ProfileJpaRepository`** (`extends JpaRepository<ProfileEntity, String>`)
    *   `Optional<ProfileEntity> findByEmail(String email)`
    *   `boolean existsByEmail(String email)`

### 4.3 Port Implementations

*   **`JpaSaveProfilePort`**：實作 `SaveProfilePort`。
    *   將 `Profile` Domain 物件轉換為 `ProfileEntity`。
    *   呼叫 `ProfileJpaRepository.save()`。
    *   將 `ProfileEntity` 轉換回 `Profile`。
    *   處理 `DataIntegrityViolationException` 並轉換為 `DuplicateEmailException`。
*   **`JpaLoadProfilePort`**：實作 `LoadProfilePort`。
    *   呼叫 `ProfileJpaRepository.findById()`。
    *   若存在，轉換為 `Profile`；否則回傳 `Optional.empty()`。
*   **`JpaDeleteProfilePort`**：實作 `DeleteProfilePort`。
    *   呼叫 `ProfileJpaRepository.deleteById()`。
*   **`JpaExistsByEmailPort`**：實作 `ExistsByEmailPort`。
    *   呼叫 `ProfileJpaRepository.existsByEmail()`。

### 4.4 Converters

*   **`ProfileConverter`**：負責 `Profile` ↔ `ProfileEntity` 的轉換。

## 5. Bootstrap

*   **`ApplicationConfig`** (`@Configuration`)
    *   使用 `@Bean` 方法建立以下 Bean：
        *   `CreateProfileService`
        *   `GetProfileService`
        *   `UpdateProfileService`
        *   `DeleteProfileService`
    *   這些 Service 依賴於 Ports 介面，Spring 會自動注入實作類別（由 `adapter-persistence` 模組的 `@Configuration` 或 Component Scan 提供，或在此明確指定 `@Import`）。
    *   *註：根據架構，`adapter-persistence` 應提供 Ports 的實作 Bean。`bootstrap` 主要負責組裝 Use Case。*

## 6. 測試策略

### 6.1 Unit Tests (JUnit + AssertJ + Mockito)

*   **Domain Tests** (`domain/src/test/java/...`)
    *   `ProfileIdTest`, `EmailTest`, `NameTest`, `AgeTest`：驗證 Value Object 的不變條件與建構子驗證。
    *   `ProfileTest`：驗證 `update` 方法是否正確建立新實例。
*   **Application Tests** (`application/src/test/java/...`)
    *   `CreateProfileServiceTest`：
        *   成功：模擬 `ExistsByEmailPort.existsByEmail` 回傳 false，`SaveProfilePort.save` 回傳 Profile，驗證呼叫順序。
        *   失敗：模擬 `ExistsByEmailPort.existsByEmail` 回傳 true，驗證拋出 `DuplicateEmailException`。
        *   失敗：模擬 `SaveProfilePort.save` 拋出 `DuplicateEmailException`（若邏輯在 Save 層），驗證傳播。
    *   `GetProfileServiceTest`：
        *   成功：模擬 `LoadProfilePort.findById` 回傳 Optional 包含 Profile，驗證回傳值。
        *   失敗：模擬 `LoadProfilePort.findById` 回傳 Optional.empty，驗證拋出 `ProfileNotFoundException`。
    *   `UpdateProfileServiceTest`：
        *   成功：模擬 Load → Update → Save，驗證呼叫順序與回傳值。
        *   失敗：模擬 Load 回傳 empty，驗證拋出 `ProfileNotFoundException`。
    *   `DeleteProfileServiceTest`：
        *   成功：模擬 Load → Delete，驗證呼叫順序。
        *   失敗：模擬 Load 回傳 empty，驗證拋出 `ProfileNotFoundException`。

### 6.2 Integration Tests (Spring Boot Test)

*   **Web Adapter Tests** (`adapter-web/src/test/java/...`)
    *   `ProfileControllerTest` (`@WebMvcTest`)：
        *   使用 `@MockitoBean` 模擬所有 Use Case。
        *   測試各 HTTP 方法的成功與失敗情境（400, 404, 409）。
        *   驗證 HTTP 狀態碼、回應內容、Header。
*   **Persistence Adapter Tests** (`adapter-persistence/src/test/java/...`)
    *   `JpaProfileRepositoryTest` (`@DataJpaTest` + Testcontainers)：
        *   測試基本 CRUD 操作。
        *   測試唯一性約束（重複 Email 拋出 `DataIntegrityViolationException`）。
    *   `JpaSaveProfilePortTest` (`@DataJpaTest` + `@Import(JpaSaveProfilePort.class)`)：
        *   測試 Domain 物件與 Entity 的轉換。
        *   測試 `DuplicateEmailException` 的轉換。
    *   `JpaLoadProfilePortTest` (`@DataJpaTest` + `@Import(JpaLoadProfilePort.class)`)：
        *   測試查詢與轉換。
    *   `JpaDeleteProfilePortTest` (`@DataJpaTest` + `@Import(JpaDeleteProfilePort.class)`)：
        *   測試刪除操作。

### 6.3 BDD Tests (Cucumber)

*   **Feature File** (`bootstrap/src/test/resources/features/profile-crud.feature`)
    *   包含 Spec 中的所有場景。
*   **Step Definitions** (`bootstrap/src/test/java/.../bdd/`)
    *   `ProfileSteps`：
        *   使用 `TestRestTemplate` 發送 HTTP 請求。
        *   驗證回應狀態碼與內容。
        *   使用 `@SpringBootTest` 啟動完整應用上下文（含 Testcontainers）。

## 7. 類別清單

| 類別名稱 | 完整路徑 (相對專案根目錄) | 職責 |
| :--- | :--- | :--- |
| **Domain** | | |
| `ProfileId` | `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/ProfileId.java` | 個人資料唯一識別碼 Value Object |
| `Email` | `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/Email.java` | 電子郵件 Value Object，含格式驗證 |
| `Name` | `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/Name.java` | 姓名 Value Object，含非空驗證 |
| `Age` | `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/Age.java` | 年齡 Value Object，含正整數驗證 |
| `Profile` | `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/Profile.java` | 個人資料 Entity，含更新行為 |
| `ProfileNotFoundException` | `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/ProfileNotFoundException.java` | 找不到個人資料例外 |
| `DuplicateEmailException` | `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/DuplicateEmailException.java` | 電子郵件重複例外 |
| `InvalidEmailException` | `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/InvalidEmailException.java` | 電子郵件格式無效例外 |
| `InvalidAgeException` | `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/InvalidAgeException.java` | 年齡無效例外 |
| **Application** | | |
| `CreateProfileUseCase` | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/in/CreateProfileUseCase.java` | 建立個人資料 Use Case 介面 |
| `GetProfileUseCase` | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/in/GetProfileUseCase.java` | 查詢個人資料 Use Case 介面 |
| `UpdateProfileUseCase` | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/in/UpdateProfileUseCase.java` | 更新個人資料 Use Case 介面 |
| `DeleteProfileUseCase` | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/in/DeleteProfileUseCase.java` | 刪除個人資料 Use Case 介面 |
| `SaveProfilePort` | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/out/SaveProfilePort.java` | 儲存個人資料 Port 介面 |
| `LoadProfilePort` | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/out/LoadProfilePort.java` | 載入個人資料 Port 介面 |
| `DeleteProfilePort` | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/out/DeleteProfilePort.java` | 刪除個人資料 Port 介面 |
| `ExistsByEmailPort` | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/out/ExistsByEmailPort.java` | 檢查電子郵件是否存在 Port 介面 |
| `CreateProfileService` | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/CreateProfileService.java` | 建立個人資料 Use Case 實作 |
| `GetProfileService` | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/GetProfileService.java` | 查詢個人資料 Use Case 實作 |
| `UpdateProfileService` | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/UpdateProfileService.java` | 更新個人資料 Use Case 實作 |
| `DeleteProfileService` | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/DeleteProfileService.java` | 刪除個人資料 Use Case 實作 |
| **Web Adapter** | | |
| `ProfileController` | `adapter-web/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/ProfileController.java` | REST API Controller |
| `CreateProfileRequest` | `adapter-web/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/CreateProfileRequest.java` | 建立請求 DTO |
| `UpdateProfileRequest` | `adapter-web/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/UpdateProfileRequest.java` | 更新請求 DTO |
| `ProfileResponse` | `adapter-web/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/ProfileResponse.java` | 回應 DTO |
| `ProfileControllerAdvice` | `adapter-web/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/ProfileControllerAdvice.java` | 全域異常處理器 |
| **Persistence Adapter** | | |
| `ProfileEntity` | `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/ProfileEntity.java` | JPA Entity |
| `ProfileJpaRepository` | `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/ProfileJpaRepository.java` | Spring Data Repository |
| `JpaSaveProfilePort` | `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/JpaSaveProfilePort.java` | 儲存 Port 實作 |
| `JpaLoadProfilePort` | `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/JpaLoadProfilePort.java` | 載入 Port 實作 |
| `JpaDeleteProfilePort` | `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/JpaDeleteProfilePort.java` | 刪除 Port 實作 |
| `JpaExistsByEmailPort` | `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/JpaExistsByEmailPort.java` | 檢查存在性 Port 實作 |
| `ProfileConverter` | `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/ProfileConverter.java` | Domain 與 Entity 轉換器 |
| **Bootstrap** | | |
| `ApplicationConfig` | `bootstrap/src/main/java/com/github/chunpingwang/ollamaagentoutcome/bootstrap/ApplicationConfig.java` | Spring Bean 組態 |
| `ProfileSteps` | `bootstrap/src/test/java/com/github/chunpingwang/ollamaagentoutcome/bdd/ProfileSteps.java` | Cucumber 步驟定義 |
