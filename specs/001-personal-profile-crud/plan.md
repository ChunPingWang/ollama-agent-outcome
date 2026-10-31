# 技術設計：個人資料管理 (Spec 001)

## 1. 領域模型 (Domain Model)

本模組採用純 Java 實作，不依賴任何框架。

### 實體與值物件

*   **`ProfileId`** (`record`)
    *   類型：`UUID`
    *   職責：值物件，確保識別碼的不可變性與類型安全。
*   **`Profile`** (`record`)
    *   欄位：
        *   `id`: `ProfileId`
        *   `email`: `String` (不可為 null)
        *   `displayName`: `String` (可為 null 或空字串)
        *   `bio`: `String` (可為 null 或空字串)
    *   不變條件 (Invariants)：
        *   `email` 必須符合標準電子郵件格式。
        *   `email` 在系統中必須唯一（由 Domain Service 或 Repository 約束）。
    *   行為：
        *   `update(displayName, bio)`: 建立並回傳一個新的 `Profile` 實例，反映更新後的狀態（值物件不可變性）。

### 領域例外 (Domain Exceptions)

*   **`ProfileNotFoundException`**
    *   繼承自 `RuntimeException`。
    *   用途：當查詢或更新/刪除操作找不到指定 ID 的個人資料時拋出。
*   **`DuplicateEmailException`**
    *   繼承自 `RuntimeException`。
    *   用途：當建立或更新資料時，發現電子郵件已存在於系統中時拋出。
*   **`InvalidEmailException`**
    *   繼承自 `RuntimeException`。
    *   用途：當提供的電子郵件格式無效時拋出（通常在 Domain 層驗證，或由 Web 層攔截，此處定義於 Domain 以便 Use Case 統一處理）。

## 2. Use Cases 與 Ports

遵循六角形架構，Application 層定義介面 (Ports)，實作 Use Case 邏輯。

### Inbound Ports (Use Case Interfaces)

1.  **`CreateProfileUseCase`**
    *   `void execute(CreateProfileCommand command)`
    *   參數 `CreateProfileCommand`：包含 `email`, `displayName`, `bio`。
2.  **`GetProfileUseCase`**
    *   `Profile execute(GetProfileCommand command)`
    *   參數 `GetProfileCommand`：包含 `profileId`。
    *   回傳：找到的 `Profile`，若不存在則拋出 `ProfileNotFoundException`。
3.  **`UpdateProfileUseCase`**
    *   `Profile execute(UpdateProfileCommand command)`
    *   參數 `UpdateProfileCommand`：包含 `profileId`, `email`, `displayName`, `bio`。
    *   回傳：更新後的 `Profile`。若不存在拋出 `ProfileNotFoundException`；若 Email 重複拋出 `DuplicateEmailException`。
4.  **`DeleteProfileUseCase`**
    *   `void execute(DeleteProfileCommand command)`
    *   參數 `DeleteProfileCommand`：包含 `profileId`。
    *   若不存在拋出 `ProfileNotFoundException`。

### Outbound Ports (Repository Interfaces)

1.  **`SaveProfilePort`**
    *   `Profile save(Profile profile)`
    *   負責將 Domain Entity 持久化。
2.  **`LoadProfilePort`**
    *   `Optional<Profile> findById(ProfileId id)`
    *   負責根據 ID 載入 Domain Entity。
3.  **`ExistsByEmailPort`** (可選，或整合於 Save/Update 邏輯中)
    *   `boolean existsByEmail(String email)`
    *   用於檢查電子郵件唯一性。*註：為簡化設計，可考慮在 `SaveProfilePort` 或 `UpdateProfileUseCase` 中透過 Repository 檢查，或讓 Repository 在衝突時拋出異常。此處採用明確的 Port 以符合 SOLID 單一職責，讓 Use Case 決定何時檢查。*

### Use Case Implementations

*   `CreateProfileService`：實作 `CreateProfileUseCase`。驗證 Email 格式 -> 檢查 Email 是否已存在 -> 建立 Domain Entity -> 呼叫 `SaveProfilePort`。
*   `GetProfileService`：實作 `GetProfileUseCase`。呼叫 `LoadProfilePort` -> 若無結果拋出例外 -> 回傳 Entity。
*   `UpdateProfileService`：實作 `UpdateProfileUseCase`。呼叫 `LoadProfilePort` -> 若無結果拋出例外 -> 驗證 Email 格式 -> 檢查 Email 是否已存在 (排除自身) -> 呼叫 Domain `update` 方法 -> 呼叫 `SaveProfilePort`。
*   `DeleteProfileService`：實作 `DeleteProfileUseCase`。呼叫 `LoadProfilePort` -> 若無結果拋出例外 -> 呼叫 Repository 刪除方法 (或透過 SavePort 刪除，視 JPA 實作而定，通常直接呼叫 Repository 的 delete 或 save 已刪除狀態)。*註：為符合 Port 定義，可新增 `DeleteProfilePort` 或讓 `SaveProfilePort` 處理刪除邏輯，但通常 Repository 直接提供 delete 方法較直觀。在此架構下，我們定義 `DeleteProfilePort` 以保持一致性，或直接在 `DeleteProfileService` 中注入 `LoadProfilePort` 和一個假設存在的 `DeleteProfilePort`。為簡化，我們將刪除邏輯視為一種特殊的 Save 或獨立 Port。此處定義 `DeleteProfilePort`。*

    *修正*：為減少 Port 數量，`DeleteProfilePort` 可定義為 `void deleteById(ProfileId id)`。

## 3. Web Adapter

Spring MVC Controller，負責 HTTP 請求/回應的轉換與驗證。

### Controller

*   **`ProfileController`**
    *   `@RestController`
    *   `@RequestMapping("/profiles")`
    *   依賴注入：`CreateProfileUseCase`, `GetProfileUseCase`, `UpdateProfileUseCase`, `DeleteProfileUseCase`。

### Endpoints & DTOs

1.  **POST `/profiles`**
    *   Method: `createProfile(@Valid @RequestBody CreateProfileRequest request)`
    *   Request DTO: `CreateProfileRequest` (record)
        *   `email`: `String` (加 `@Email`, `@NotBlank`)
        *   `displayName`: `String` (可為 null)
        *   `bio`: `String` (可為 null)
    *   Response: 201 Created, Location header, Body: `ProfileResponse` (包含 id, email, displayName, bio)。
    *   Error: 400 (Validation Error), 409 (Duplicate Email)。

2.  **GET `/profiles/{id}`**
    *   Method: `getProfile(@PathVariable UUID id)`
    *   Response: 200 OK, Body: `ProfileResponse`。
    *   Error: 404 (Not Found)。

3.  **PUT `/profiles/{id}`**
    *   Method: `updateProfile(@PathVariable UUID id, @Valid @RequestBody UpdateProfileRequest request)`
    *   Request DTO: `UpdateProfileRequest` (record)
        *   `email`: `String` (加 `@Email`, `@NotBlank`)
        *   `displayName`: `String` (可為 null)
        *   `bio`: `String` (可為 null)
    *   Response: 200 OK, Body: `ProfileResponse`。
    *   Error: 400 (Validation Error), 404 (Not Found), 409 (Duplicate Email)。

4.  **DELETE `/profiles/{id}`**
    *   Method: `deleteProfile(@PathVariable UUID id)`
    *   Response: 204 No Content。
    *   Error: 404 (Not Found)。

### Error Handling

*   **`ProfileControllerAdvice`** (`@RestControllerAdvice`)
    *   `@ExceptionHandler(ProfileNotFoundException.class)` -> 404 Not Found。
    *   `@ExceptionHandler(DuplicateEmailException.class)` -> 409 Conflict。
    *   `@ExceptionHandler(MethodArgumentNotValidException.class)` -> 400 Bad Request (包含驗證錯誤細節)。
    *   *注意*：不攔截 `Exception.class`，以保留 Spring 預設的 404/500 行為。

## 4. Persistence Adapter

Spring Data JPA 實作，負責 Domain 與資料庫的映射。

### JPA Entity

*   **`ProfileEntity`**
    *   `@Entity`
    *   `@Table(uniqueConstraints = @UniqueConstraint(columnNames = "email"))`
    *   欄位：
        *   `id`: `UUID`, `@Id`, `@GeneratedValue` (或使用 UUID 生成策略，但根據 System Prompt，若 Domain 自行產生 ID，Entity 需實作 `Persistable`。此處假設 Domain 產生 UUID，故 Entity 實作 `Persistable<UUID>`，`@Transient boolean isNew = true`，`@PostLoad`/`@PostPersist` 設為 false)。
        *   `email`: `String`, `@Column(unique = true, nullable = false)`
        *   `displayName`: `String`, `@Column`
        *   `bio`: `String`, `@Column`
    *   實作 `Persistable<UUID>`。

### Repository

*   **`ProfileRepository`** (extends `JpaRepository<ProfileEntity, UUID>`)
    *   `Optional<ProfileEntity> findByEmail(String email)`
    *   `boolean existsByEmail(String email)`

### Port Implementations

*   **`JpaSaveProfilePort`**
    *   實作 `SaveProfilePort`。
    *   依賴：`ProfileRepository`, `ProfileConverter`。
    *   邏輯：將 Domain `Profile` 轉換為 `ProfileEntity` -> 呼叫 `repository.save()` -> 轉換回 Domain `Profile`。
*   **`JpaLoadProfilePort`**
    *   實作 `LoadProfilePort`。
    *   依賴：`ProfileRepository`, `ProfileConverter`。
    *   邏輯：`repository.findById(id)` -> 若存在則轉換為 Domain `Profile`，否則回傳 `Optional.empty()`。
*   **`JpaExistsByEmailPort`**
    *   實作 `ExistsByEmailPort`。
    *   依賴：`ProfileRepository`。
    *   邏輯：`repository.existsByEmail(email)`。
*   **`JpaDeleteProfilePort`**
    *   實作 `DeleteProfilePort`。
    *   依賴：`ProfileRepository`。
    *   邏輯：`repository.deleteById(id)`。

### Converter

*   **`ProfileConverter`**
    *   靜態工具類別或無狀態 Service。
    *   `toDomain(ProfileEntity entity)`
    *   `toEntity(Profile profile)`

## 5. Bootstrap

Spring Boot 組態，負責將 Ports 與 Implementations 綁定。

*   **`ApplicationConfig`** (`@Configuration`)
    *   `@Bean` `CreateProfileService` (注入 `SaveProfilePort`, `ExistsByEmailPort`)
    *   `@Bean` `GetProfileService` (注入 `LoadProfilePort`)
    *   `@Bean` `UpdateProfileService` (注入 `LoadProfilePort`, `SaveProfilePort`, `ExistsByEmailPort`)
    *   `@Bean` `DeleteProfileService` (注入 `LoadProfilePort`, `DeleteProfilePort`)
    *   `@Bean` `JpaSaveProfilePort` (注入 `ProfileRepository`, `ProfileConverter`)
    *   `@Bean` `JpaLoadProfilePort` (注入 `ProfileRepository`, `ProfileConverter`)
    *   `@Bean` `JpaExistsByEmailPort` (注入 `ProfileRepository`)
    *   `@Bean` `JpaDeleteProfilePort` (注入 `ProfileRepository`)
    *   `@Bean` `ProfileConverter`

## 6. 測試策略

### Unit Tests (JUnit + Mockito + AssertJ)

*   **Domain**:
    *   `ProfileTest`: 測試值物件行為、不變條件。
*   **Application**:
    *   `CreateProfileServiceTest`: 測試成功建立、Email 重複、Email 格式錯誤。
    *   `GetProfileServiceTest`: 測試成功查詢、找不到資料。
    *   `UpdateProfileServiceTest`: 測試成功更新、找不到資料、Email 重複、Email 格式錯誤。
    *   `DeleteProfileServiceTest`: 測試成功刪除、找不到資料。
    *   *Mock* `Port` 介面。

### Integration Tests (Spring Boot + Testcontainers)

*   **Web Adapter**:
    *   `ProfileControllerTest` (`@WebMvcTest`): 測試 HTTP 請求/回應、狀態碼、驗證錯誤。
    *   *Mock* Use Case Ports。
*   **Persistence Adapter**:
    *   `JpaSaveProfilePortTest` (`@DataJpaTest` + Testcontainers): 測試 Entity 映射、唯一性約束、CRUD 操作。
    *   `JpaLoadProfilePortTest` (`@DataJpaTest` + Testcontainers): 測試載入邏輯。
    *   `ProfileConverterTest`: 測試 Domain 與 Entity 轉換。

### BDD Tests (Cucumber)

*   **Feature File**: `bootstrap/src/test/resources/features/personal-profile-crud.feature`
*   **Step Definitions**: `bootstrap/src/test/java/com/github/chunpingwang/ollamaagentoutcome/bdd/ProfileSteps`
*   使用 `TestRestTemplate` 呼叫實際 API，驗證整體流程。

## 7. 類別清單

| 類別名稱 | 完整路徑 (相對專案根目錄) | 職責 |
| :--- | :--- | :--- |
| **Domain** | | |
| `ProfileId` | `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/ProfileId.java` | 個人資料識別碼值物件 |
| `Profile` | `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/Profile.java` | 個人資料領域實體 |
| `ProfileNotFoundException` | `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/ProfileNotFoundException.java` | 找不到個人資料例外 |
| `DuplicateEmailException` | `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/DuplicateEmailException.java` | 電子郵件重複例外 |
| `InvalidEmailException` | `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/InvalidEmailException.java` | 電子郵件格式無效例外 |
| **Application** | | |
| `CreateProfileUseCase` | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/in/CreateProfileUseCase.java` | 建立個人資料 Use Case 介面 |
| `GetProfileUseCase` | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/in/GetProfileUseCase.java` | 查詢個人資料 Use Case 介面 |
| `UpdateProfileUseCase` | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/in/UpdateProfileUseCase.java` | 更新個人資料 Use Case 介面 |
| `DeleteProfileUseCase` | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/in/DeleteProfileUseCase.java` | 刪除個人資料 Use Case 介面 |
| `SaveProfilePort` | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/out/SaveProfilePort.java` | 儲存個人資料 Port 介面 |
| `LoadProfilePort` | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/out/LoadProfilePort.java` | 載入個人資料 Port 介面 |
| `ExistsByEmailPort` | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/out/ExistsByEmailPort.java` | 檢查 Email 存在性 Port 介面 |
| `DeleteProfilePort` | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/out/DeleteProfilePort.java` | 刪除個人資料 Port 介面 |
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
| `ProfileRepository` | `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/ProfileRepository.java` | Spring Data Repository |
| `ProfileConverter` | `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/ProfileConverter.java` | Domain-Entity 轉換器 |
| `JpaSaveProfilePort` | `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/JpaSaveProfilePort.java` | 儲存 Port 實作 |
| `JpaLoadProfilePort` | `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/JpaLoadProfilePort.java` | 載入 Port 實作 |
| `JpaExistsByEmailPort` | `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/JpaExistsByEmailPort.java` | Email 存在性 Port 實作 |
| `JpaDeleteProfilePort` | `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/JpaDeleteProfilePort.java` | 刪除 Port 實作 |
| **Bootstrap** | | |
| `ApplicationConfig` | `bootstrap/src/main/java/com/github/chunpingwang/ollamaagentoutcome/bootstrap/ApplicationConfig.java` | Spring Bean 組態 |
| `ProfileSteps` | `bootstrap/src/test/java/com/github/chunpingwang/ollamaagentoutcome/bdd/ProfileSteps.java` | Cucumber 步驟定義 |
