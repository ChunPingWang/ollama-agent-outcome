# 技術設計：個人資料管理 (Spec 001)

## 1. 領域模型 (Domain Model)

本層級包含純 Java 類別，不依賴任何框架。

### Value Objects & Entities

*   **`ProfileId`** (`record`)
    *   類型：`java.util.UUID`
    *   職責：封裝個人資料的唯一識別碼。
    *   不變條件：不可為 `null`。

*   **`Email`** (`record`)
    *   類型：`String`
    *   職責：封裝電子郵件地址。
    *   不變條件：符合 RFC 5322 基本格式（在 Domain Service 或 Factory 中驗證，或於 DTO 層驗證後傳入）。在此設計中，為簡化 Domain 層複雜度，假設傳入的 Email 字串已通過基本格式檢查，但需確保唯一性由 Repository 層或 Domain Service 檢查。

*   **`Profile`** (`record`)
    *   欄位：
        *   `id: ProfileId`
        *   `name: String` (姓名，不可為空)
        *   `email: Email` (電子郵件，不可為空)
    *   不變條件：
        *   `name` 長度介於 1 到 100 之間。
        *   `email` 格式有效。
        *   一旦建立，`id` 不可變。
    *   行為：
        *   `update(name: String, email: Email): Profile`：回傳新的 `Profile` 實例，包含更新後的資料。若新 email 與現有 email 相同（且為同一筆資料），則允許；若不同，需由外部檢查唯一性。

### Domain Exceptions

*   **`ProfileNotFoundException`**
    *   繼承自 `RuntimeException`。
    *   用途：當查詢或更新/刪除不存在的 Profile 時拋出。
*   **`DuplicateEmailException`**
    *   繼承自 `RuntimeException`。
    *   用途：當建立或更新時，發現電子郵件已存在於系統中時拋出。
*   **`InvalidProfileException`**
    *   繼承自 `RuntimeException`。
    *   用途：當資料不符合業務規則（如姓名為空）時拋出。

## 2. Use Cases 與 Ports

Application 層定義介面（Ports）並實作 Use Case。

### Inbound Ports (Use Cases)

1.  **`CreateProfileUseCase`**
    *   方法：`void execute(CreateProfileCommand command)`
    *   參數 `CreateProfileCommand`：包含 `name` 和 `email`。
2.  **`GetProfileUseCase`**
    *   方法：`Profile execute(GetProfileCommand command)`
    *   參數 `GetProfileCommand`：包含 `profileId`。
3.  **`UpdateProfileUseCase`**
    *   方法：`void execute(UpdateProfileCommand command)`
    *   參數 `UpdateProfileCommand`：包含 `profileId`, `name`, `email`。
4.  **`DeleteProfileUseCase`**
    *   方法：`void execute(DeleteProfileCommand command)`
    *   參數 `DeleteProfileCommand`：包含 `profileId`。

### Outbound Ports (Repository Interfaces)

1.  **`SaveProfilePort`**
    *   方法：`Profile save(Profile profile)`
    *   職責：儲存或更新 Profile。
2.  **`LoadProfilePort`**
    *   方法：`Optional<Profile> findById(ProfileId id)`
    *   職責：根據 ID 載入 Profile。
3.  **`ExistsByEmailPort`**
    *   方法：`boolean existsByEmail(Email email)`
    *   職責：檢查電子郵件是否已存在。
4.  **`DeleteProfilePort`**
    *   方法：`void deleteById(ProfileId id)`
    *   職責：根據 ID 刪除 Profile。

### Use Case Implementations

*   **`CreateProfileService`**：實作 `CreateProfileUseCase`。
    *   邏輯：檢查 email 唯一性 (`ExistsByEmailPort`) -> 建立 `Profile` -> 儲存 (`SaveProfilePort`)。
*   **`GetProfileService`**：實作 `GetProfileUseCase`。
    *   邏輯：載入 Profile (`LoadProfilePort`) -> 若不存在拋出 `ProfileNotFoundException` -> 回傳 Profile。
*   **`UpdateProfileService`**：實作 `UpdateProfileUseCase`。
    *   邏輯：載入 Profile (`LoadProfilePort`) -> 若不存在拋出 `ProfileNotFoundException` -> 檢查新 email 是否與其他現有 email 重複 (`ExistsByEmailPort`) -> 若重複拋出 `DuplicateEmailException` -> 更新 Profile -> 儲存 (`SaveProfilePort`)。
*   **`DeleteProfileService`**：實作 `DeleteProfileUseCase`。
    *   邏輯：載入 Profile (`LoadProfilePort`) -> 若不存在拋出 `ProfileNotFoundException` -> 刪除 (`DeleteProfilePort`)。

## 3. Web Adapter

Spring MVC 層，負責 HTTP 請求/回應轉換。

### Controller

*   **`ProfileController`**
    *   `@RestController`
    *   `@RequestMapping("/api/profiles")`
    *   依賴注入：`CreateProfileUseCase`, `GetProfileUseCase`, `UpdateProfileUseCase`, `DeleteProfileUseCase`。

### Endpoints

1.  **POST `/api/profiles`**
    *   方法：`createProfile(@Valid @RequestBody CreateProfileRequest request)`
    *   回應：`201 Created`，Header `Location` 指向新資源。
    *   DTO：`CreateProfileRequest` (fields: `name`, `email`)。
2.  **GET `/api/profiles/{id}`**
    *   方法：`getProfile(@PathVariable UUID id)`
    *   回應：`200 OK`，Body 為 `ProfileResponse`。
    *   DTO：`ProfileResponse` (fields: `id`, `name`, `email`)。
3.  **PUT `/api/profiles/{id}`**
    *   方法：`updateProfile(@PathVariable UUID id, @Valid @RequestBody UpdateProfileRequest request)`
    *   回應：`200 OK`，Body 為 `ProfileResponse`。
    *   DTO：`UpdateProfileRequest` (fields: `name`, `email`)。
4.  **DELETE `/api/profiles/{id}`**
    *   方法：`deleteProfile(@PathVariable UUID id)`
    *   回應：`204 No Content`。

### Error Handling

*   **`ProfileControllerAdvice`** (`@RestControllerAdvice`)
    *   `@ExceptionHandler(ProfileNotFoundException.class)` -> `404 Not Found`
    *   `@ExceptionHandler(DuplicateEmailException.class)` -> `409 Conflict`
    *   `@ExceptionHandler(InvalidProfileException.class)` -> `400 Bad Request`
    *   `@ExceptionHandler(MethodArgumentNotValidException.class)` -> `400 Bad Request` (Jakarta Validation 失敗)
    *   **注意**：不攔截 `Exception.class`，以避免覆蓋 Spring 的 404 或其他框架錯誤。

## 4. Persistence Adapter

JPA 與 Spring Data 層，負責資料持久化。

### JPA Entity

*   **`ProfileEntity`**
    *   實作 `Persistable<UUID>` (以處理 ID 生成邏輯，避免 `@GeneratedValue` 與 Domain 生成 ID 的衝突)。
    *   欄位：
        *   `@Id UUID id`
        *   `@Column(unique = true) String email`
        *   `String name`
        *   `@Transient boolean isNew = true`
    *   方法：
        *   `isNew()`：回傳 `isNew` 狀態。
        *   `@PostLoad`, `@PostPersist`：將 `isNew` 設為 `false`。

### Repository

*   **`ProfileRepository`** (extends `JpaRepository<ProfileEntity, UUID>`)
    *   方法：`Optional<ProfileEntity> findByEmail(String email)`
    *   方法：`boolean existsByEmail(String email)`

### Port Implementations

*   **`JpaSaveProfilePort`**：實作 `SaveProfilePort`。
    *   轉換 `Profile` (Domain) <-> `ProfileEntity` (JPA)。
    *   呼叫 `profileRepository.save(entity)`。
*   **`JpaLoadProfilePort`**：實作 `LoadProfilePort`。
    *   呼叫 `profileRepository.findById(id)` 並轉換為 `Profile`。
*   **`JpaExistsByEmailPort`**：實作 `ExistsByEmailPort`。
    *   呼叫 `profileRepository.existsByEmail(email)`。
*   **`JpaDeleteProfilePort`**：實作 `DeleteProfilePort`。
    *   呼叫 `profileRepository.deleteById(id)`。

### Converter

*   **`ProfileConverter`** (Stateless Utility Class)
    *   `toEntity(Profile profile)`
    *   `toDomain(ProfileEntity entity)`

## 5. Bootstrap

Spring Boot 組態層。

*   **`Application`** (`@SpringBootApplication`)
    *   主程式入口。
*   **`ApplicationConfig`** (`@Configuration`)
    *   使用 `@Bean` 方法建立 Application 層的 Use Case 實例。
    *   由於 Use Case 實作類別依賴 Ports 介面，而 Ports 實作類別由 Persistence Adapter 提供，Spring 會自動透過建構子注入找到對應的 Bean（若 Port 實作類別也標註為 `@Component` 或在此 Config 中定義為 Bean）。
    *   建議：將所有 Port 實作類別 (`Jpa*Port`) 標註為 `@Component`，或在 `ApplicationConfig` 中明確 `@Import` 它們。為符合六角形架構，通常由 Bootstrap 組裝 Use Case，並依賴 Spring 的自動偵測或明確組裝 Port 實作。
    *   在此設計中，`ApplicationConfig` 將建立 `CreateProfileService`, `GetProfileService`, `UpdateProfileService`, `DeleteProfileService` 的 Bean。這些 Service 的建構子參數（Ports）將由 Spring 自動注入（假設 Port 實作類別也是 Spring Beans）。

## 6. 測試策略

### Unit Tests (Domain & Application)

*   **`ProfileDomainTest`**：測試 `Profile` 的不變條件與行為。
*   **`CreateProfileServiceTest`**：使用 Mockito 模擬 `ExistsByEmailPort` 和 `SaveProfilePort`。測試成功建立、重複 Email、無效資料等情境。
*   **`GetProfileServiceTest`**：模擬 `LoadProfilePort`。測試找到與找不到。
*   **`UpdateProfileServiceTest`**：模擬 `LoadProfilePort`, `ExistsByEmailPort`, `SaveProfilePort`。測試成功更新、找不到、重複 Email、找不到原資料。
*   **`DeleteProfileServiceTest`**：模擬 `LoadProfilePort`, `DeleteProfilePort`。測試成功刪除、找不到。

### Integration Tests (Persistence Adapter)

*   **`ProfileRepositoryTest`**：使用 `@DataJpaTest` + Testcontainers PostgreSQL。測試 JPA Entity 的儲存、查詢、唯一性約束。
*   **`JpaSaveProfilePortTest`**：測試 Port 實作與 Converter 的正確性。

### Web Tests (Web Adapter)

*   **`ProfileControllerTest`**：使用 `@WebMvcTest`。測試 Controller 的路徑、參數綁定、例外轉換為 HTTP 狀態碼。

### BDD Tests (Cucumber)

*   **`ProfileCrudSteps`**：位於 `bootstrap/src/test/java/.../bdd/`。
    *   使用 `TestRestTemplate` 呼叫實際的 REST API。
    *   Feature 檔：`bootstrap/src/test/resources/features/profile-crud.feature`。
    *   涵蓋所有 Spec 中的場景。

## 7. 類別清單

| 類別名稱 | 完整路徑 (相對專案根目錄) | 職責 |
| :--- | :--- | :--- |
| `ProfileId` | `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/ProfileId.java` | Value Object: 個人資料 ID |
| `Email` | `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/Email.java` | Value Object: 電子郵件 |
| `Profile` | `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/Profile.java` | Entity: 個人資料領域模型 |
| `ProfileNotFoundException` | `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/ProfileNotFoundException.java` | Domain Exception: 找不到資料 |
| `DuplicateEmailException` | `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/DuplicateEmailException.java` | Domain Exception: 電子郵件重複 |
| `InvalidProfileException` | `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/InvalidProfileException.java` | Domain Exception: 無效資料 |
| `CreateProfileUseCase` | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/in/CreateProfileUseCase.java` | Inbound Port: 建立個人資料 |
| `GetProfileUseCase` | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/in/GetProfileUseCase.java` | Inbound Port: 查詢個人資料 |
| `UpdateProfileUseCase` | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/in/UpdateProfileUseCase.java` | Inbound Port: 更新個人資料 |
| `DeleteProfileUseCase` | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/in/DeleteProfileUseCase.java` | Inbound Port: 刪除個人資料 |
| `SaveProfilePort` | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/out/SaveProfilePort.java` | Outbound Port: 儲存資料 |
| `LoadProfilePort` | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/out/LoadProfilePort.java` | Outbound Port: 載入資料 |
| `ExistsByEmailPort` | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/out/ExistsByEmailPort.java` | Outbound Port: 檢查 Email 存在性 |
| `DeleteProfilePort` | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/out/DeleteProfilePort.java` | Outbound Port: 刪除資料 |
| `CreateProfileCommand` | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/CreateProfileCommand.java` | Command Object: 建立請求參數 |
| `GetProfileCommand` | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/GetProfileCommand.java` | Command Object: 查詢請求參數 |
| `UpdateProfileCommand` | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/UpdateProfileCommand.java` | Command Object: 更新請求參數 |
| `DeleteProfileCommand` | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/DeleteProfileCommand.java` | Command Object: 刪除請求參數 |
| `CreateProfileService` | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/CreateProfileService.java` | Use Case Impl: 建立邏輯 |
| `GetProfileService` | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/GetProfileService.java` | Use Case Impl: 查詢邏輯 |
| `UpdateProfileService` | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/UpdateProfileService.java` | Use Case Impl: 更新邏輯 |
| `DeleteProfileService` | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/DeleteProfileService.java` | Use Case Impl: 刪除邏輯 |
| `ProfileController` | `adapter-web/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/ProfileController.java` | REST Controller: HTTP 端點 |
| `ProfileControllerAdvice` | `adapter-web/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/ProfileControllerAdvice.java` | Exception Handler: 錯誤轉換 |
| `CreateProfileRequest` | `adapter-web/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/CreateProfileRequest.java` | DTO: 建立請求 |
| `UpdateProfileRequest` | `adapter-web/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/UpdateProfileRequest.java` | DTO: 更新請求 |
| `ProfileResponse` | `adapter-web/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/ProfileResponse.java` | DTO: 回應 |
| `ProfileEntity` | `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/ProfileEntity.java` | JPA Entity: 資料庫映射 |
| `ProfileRepository` | `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/ProfileRepository.java` | Spring Data Repository |
| `JpaSaveProfilePort` | `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/JpaSaveProfilePort.java` | Port Impl: 儲存 |
| `JpaLoadProfilePort` | `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/JpaLoadProfilePort.java` | Port Impl: 載入 |
| `JpaExistsByEmailPort` | `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/JpaExistsByEmailPort.java` | Port Impl: 檢查 Email |
| `JpaDeleteProfilePort` | `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/JpaDeleteProfilePort.java` | Port Impl: 刪除 |
| `ProfileConverter` | `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/ProfileConverter.java` | Converter: Domain <-> Entity |
| `Application` | `bootstrap/src/main/java/com/github/chunpingwang/ollamaagentoutcome/bootstrap/Application.java` | Spring Boot Main Class |
| `ApplicationConfig` | `bootstrap/src/main/java/com/github/chunpingwang/ollamaagentoutcome/bootstrap/ApplicationConfig.java` | Spring Config: Bean 組裝 |
| `ProfileCrudSteps` | `bootstrap/src/test/java/com/github/chunpingwang/ollamaagentoutcome/bdd/ProfileCrudSteps.java` | Cucumber Steps: BDD 測試 |
| `ProfileCrudTest` | `bootstrap/src/test/java/com/github/chunpingwang/ollamaagentoutcome/bdd/ProfileCrudTest.java` | Cucumber Runner: 執行 Feature |
