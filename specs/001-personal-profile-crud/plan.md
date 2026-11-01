# Plan 001 — 個人資料管理 (Personal Profile CRUD)

本計畫基於 Spec 001 設計六角形架構的實現細節。所有程式碼將嚴格遵循 Hexagonal Architecture，依賴方向由外向內（Adapter -> Application -> Domain），並使用 JDK 17+ 特性。

## 領域模型 (Domain Model)

### Value Objects & Entities

由於系統要求「硬刪除」且識別碼由外部指定或 UUID 生成，我們採用 **UUID** 作為主鍵。為符合 JPA 最佳實踐與 Spec 中的 ID 格式，我們將識別碼封裝在 Value Object 中，但為了簡化持久層映射，Entity 直接使用 `String` 或 `UUID` 類型儲存。考慮到 Spec 示例中使用的是標準 UUID 字串，這裡定義一個不可變的 `ProfileId` record 用於 Domain 邏輯，但在 Persistence Adapter 中直接處理 String/UUID。

*   **`ProfileId`** (`record`)
    *   屬性：`String value`
    *   不變條件：非空、符合 UUID 格式（可選，若需嚴謹驗證）。
*   **`Email`** (`record`)
    *   屬性：`String address`
    *   不變條件：非空白、符合 RFC 5322 基本格式（透過 Jakarta Validation @Email 確保輸入合法性，Domain 層可做二次檢查或僅視為純資料容器）。在此設計中，Domain 層主要負責業務規則（唯一性），格式驗證主要由 Web Layer (DTO) 承擔以快速失敗。
*   **`DisplayName`** (`record`)
    *   屬性：`String name`
    *   不變條件：允許為 null 或空白字符串（視為空值）。長度限制依一般實務設為 1-255 字符。
*   **`Profile`** (`class`, Entity in Domain sense, not JPA yet)
    *   屬性：`id: ProfileId`, `email: Email`, `displayName: DisplayName`
    *   行為：
        *   `create(id, email, displayName)`：靜態工廠方法，建立新實例。
        *   `update(email, displayName)`：更新顯示名稱與電子郵件。注意：根據 Spec US-3 "Update full profile"，PUT 請求包含完整資訊，因此這裡接受新的 Email 和 DisplayName。需檢查 Email 是否變更且與其他現有記錄衝突（此邏輯在 Application/Port 層處理較佳，因為需要查詢資料庫確認唯一性，或者由 Port 回傳結果）。
        *   `delete()`：標記刪除狀態？不，Spec 要求硬刪除，所以 Domain Entity 本身不需要軟刪除標誌，直接從 DB 移除即可。

### Domain Exceptions

*   **`DuplicateEmailException`**：當嘗試建立或更新資料時，發現該 Email 已存在於系統中。
*   **`ProfileNotFoundException`**：當查詢、更新或删除的 ID 不存在時拋出。
*   **`InvalidInputException`**：通用輸入驗證失敗例外（可選，若 Web Layer 已攔截格式錯誤，Domain 層主要關注業務規則如重複 Email）。建議將格式錯誤留在 DTO 驗證階段，Domain 只處理業務邏輯異常。

## Use Cases & Ports

遵循命名規範：Inbound Port 以 `UseCase` 結尾，Implementation 以 `Service` 結尾；Outbound Port 以 `Port` 結尾。

### Inbound Ports (Interfaces)

1.  **`CreateProfileUseCase`**
    ```java
    void execute(CreateProfileCommand command);
    // Command: record CreateProfileCommand(String id, String email, String displayName)
    ```
2.  **`GetProfileUseCase`**
    ```java
    ProfileDto execute(GetProfileQuery query);
    // Query: record GetProfileQuery(String profileId)
    // Return: ProfileDto (record with id, email, displayName)
    ```
3.  **`UpdateProfileUseCase`**
    ```java
    void execute(UpdateProfileCommand command);
    // Command: record UpdateProfileCommand(String profileId, String email, String displayName)
    ```
4.  **`DeleteProfileUseCase`**
    ```java
    void execute(DeleteProfileCommand command);
    // Command: record DeleteProfileCommand(String profileId)
    ```

### Outbound Ports (Interfaces)

定義在 `application.port.out` 套件中：

1.  **`SaveProfilePort`**
    ```java
    interface SaveProfilePort {
        void save(Profile domainEntity);
        boolean existsByEmail(String email); // 用於檢查唯一性，避免先查後存的 Race Condition 或減少查詢次數
    }
    ```
    *註：為了簡化並符合單一職責，也可以將 findByEmail 分開，但考慮到 Create/Update 都需要檢查 Email 唯一性，合併在此 Port 或新增一個 `FindProfileBy...Port`。根據 SOLID 介面分離原則，若 Find 和 Save 使用場景不同，建議分開。*
    
    修正為更精細的 Port：
    -   `LoadProfileByIdPort`: `Optional<Profile> findById(String id)`
    -   `SaveProfilePort`: `void save(Profile entity)`
    -   `ExistsByEmailPort`: `boolean existsByEmail(String email, String excludeId)` (excludeId 用於更新時排除自身)

2.  **`DeleteProfilePort`**
    ```java
    interface DeleteProfilePort {
        void deleteById(String profileId);
        boolean existsById(String profileId); // 輔助判斷是否刪除成功（404 vs 204）
    }
    ```

### Use Case Implementations (Services)

位於 `application.usecase` 套件：

1.  **`CreateProfileService`**: 
    -   驗證輸入非空。
    -   呼叫 `existsByEmailPort.existsByEmail(cmd.email())`。若存在拋出 `DuplicateEmailException`。
    -   建立 Domain Entity (`Profile.create(...)`)。
    -   呼叫 `saveProfilePort.save(entity)`。
2.  **`GetProfileService`**:
    -   呼叫 `loadProfileByIdPort.findById(id)`。
    -   若不存在，拋出 `ProfileNotFoundException`。
    -   轉換為 DTO 並回傳。
3.  **`UpdateProfileService`**:
    -   呼叫 `loadProfileByIdPort.findById(id)`。
    -   若不存在，拋出 `ProfileNotFoundException`。
    -   檢查新 Email 是否與其他記錄衝突 (`existsByEmailPort.existsByEmail(newEmail, id)`)。若是，拋出 `DuplicateEmailException`。
    -   呼叫 Domain Entity 的 `update()` 方法更新狀態。
    -   呼叫 `saveProfilePort.save(updatedEntity)`。
4.  **`DeleteProfileService`**:
    -   呼叫 `deleteProfilePort.existsById(id)` (或先 load)。
    -   若不存在，拋出 `ProfileNotFoundException`。
    -   呼叫 `deleteProfilePort.deleteById(id)`。

## Web Adapter

位於 `adapter.web` 套件：

### Controller: `ProfileController`

-   `POST /api/profiles`: 
    -   Method: `create(@Valid @RequestBody CreateProfileRequest request)`
    -   Response: 201 Created + Location Header.
    -   Error Mapping:
        -   Validation Errors -> 400 Bad Request (由 `@RestControllerAdvice` 處理).
        -   `DuplicateEmailException` -> 409 Conflict.
-   `GET /api/profiles/{id}`:
    -   Method: `get(@PathVariable("id") String id)`
    -   Response: 200 OK with JSON body.
    -   Error Mapping:
        -   `ProfileNotFoundException` -> 404 Not Found.
-   `PUT /api/profiles/{id}`:
    -   Method: `update(@PathVariable("id") String id, @Valid @RequestBody UpdateProfileRequest request)`
    -   Response: 200 OK.
    -   Error Mapping:
        -   Validation Errors -> 400 Bad Request.
        -   `ProfileNotFoundException` -> 404 Not Found.
        -   `DuplicateEmailException` -> 409 Conflict.
-   `DELETE /api/profiles/{id}`:
    -   Method: `delete(@PathVariable("id") String id)`
    -   Response: 204 No Content.
    -   Error Mapping:
        -   `ProfileNotFoundException` -> 404 Not Found.

### DTOs (Data Transfer Objects)

使用 Java Record：

1.  **`CreateProfileRequest`**: `record CreateProfileRequest(String email, String displayName)`
    -   Constraints: `@NotBlank`, `@Email`.
2.  **`UpdateProfileRequest`**: `record UpdateProfileRequest(String email, String displayName)`
    -   Constraints: `@NotBlank`, `@Email`.
3.  **`ProfileResponse`** (for GET): `record ProfileResponse(String id, String email, String displayName)`

## Persistence Adapter

位於 `adapter.persistence` 套件：

### JPA Entity: `ProfileEntity`

```java
@Entity
@Table(name = "profiles", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"email"})
})
public class ProfileEntity implements Persistable<String> { // 或 UUID，視 ID 類型而定。Spec 示例為 UUID string.
    
    @Id
    private String id;
    
    @Column(nullable = false, unique = true)
    private String email;
    
    @Column(name = "display_name")
    private String displayName;
    
    @Transient
    private boolean isNew = true;

    // Constructors, Getters, Setters
    
    @PostLoad
    public void onLoad() {
        this.isNew = false;
    }
    
    @Override
    public String getId() { return id; }
    
    @Override
    public boolean isNew() { return isNew; }
}
```
*註：使用 `Persistable` 介面來處理新舊實體的判斷，避免 `save()` 時因 ID 已存在而被誤判為更新導致衝突或錯誤行為（雖然通常 saveAndFlush 會根據 ID 決定 insert/update，但明確實現 Persistable 更清晰）。由於 Domain ID 是 Value Object，這裡直接映射到 String/UUID。*

### Repository: `ProfileRepository`

繼承 `JpaRepository<ProfileEntity, String>` (或 `UUID`)。

### Port Implementations

1.  **`JpaSaveProfilePort`** (`implements SaveProfilePort`, `ExistsByEmailPort`):
    -   Inject `ProfileRepository`.
    -   `save(Profile)`: Convert Domain Entity to JPA Entity -> `repository.saveAndFlush(entity)`。
    -   `existsByEmail(String email, String excludeId)`: Use custom query or method name `existsByEmailAndIdNot(email, excludeId)`。若無 excludeId 則用 `existsByEmail(email)`。
2.  **`JpaLoadProfileByIdPort`**:
    -   Inject `ProfileRepository`.
    -   `findById(String id)`: Return `Optional.ofNullable(...).map(this::toDomain)`.
3.  **`JpaDeleteProfilePort`**:
    -   Inject `ProfileRepository`.
    -   `deleteById(String id)`: Call `repository.deleteById(id)`.
    -   `existsById(String id)`: Call `repository.existsById(id)`.

### Converter / Mapper

由於不使用 MapStruct，手動撰寫靜態轉換方法或在 Port 實作中內聯：
-   `toEntity(Profile domain)`: 建立新的 `ProfileEntity`，設定 ID, Email, DisplayName, isNew=true。
-   `toDomain(ProfileEntity entity)`: 建立新的 `Profile` (或 Value Objects)，從 Entity 讀取值。

## Bootstrap

位於 `bootstrap` 套件：

### Configuration: `UseCaseConfiguration`

```java
@Configuration
public class UseCaseConfiguration {

    @Bean
    CreateProfileService createProfileService(SaveProfilePort savePort, ExistsByEmailPort existsByEmailPort) {
        return new CreateProfileService(savePort, existsByEmailPort);
    }

    // ... other beans for Get, Update, Delete services
    
    @Bean
    JpaSaveProfilePort jpaSaveProfilePort(ProfileRepository repository) {
        return new JpaSaveProfilePort(repository);
    }
    
    // ... other port implementations if they need Spring Beans like Repository
}
```
*注意：Ports 的實作類別（如 JpaSaveProfilePort）依賴於 Spring Data Repositories，這些 Repository Bean 由 Spring Boot 自動掃描產生。因此，在 `UseCaseConfiguration` 中定義 Service Bean 時，需注入 Port Interface，而 Port Implementation 本身也應作為 Bean 註冊，或者直接在 Service 建構子中注入具體實現（若只有一個實現）。為符合依賴反轉，建議將 Port Implementation 也註冊為 Bean，或在 Application Context 啟動時自動偵測並綁定。最簡單的方式是讓 Port Implementation 成為 `@Component` (但在 adapter-persistence 模組)，然後在 Bootstrap 組裝 Service。但根據規則，Application 層不能依賴 Adapter。所以正確做法是：**Adapter 提供 Port 實作的 Bean**，Bootstrap 僅負責將 Use Case Services 組裝起來？不，通常 Hexagonal 架構中，Infrastructure/Adapter 層的 Bean 會自動被 Component Scan 發現。如果 `adapter-persistence` 中的 Port 實作類標記了 `@Repository` 或 `@Service`，它們會被 Spring 管理。然而，為了嚴格控制依賴，我們可以在 `bootstrap.UseCaseConfiguration` 中明確宣告所有 Service Beans，並在參數中指定所需的 Port Bean 名稱。*

修正策略：
1.  `adapter.persistence` 中的 Port 實作類別 (`JpaSaveProfilePort`, etc.) 加上 `@Repository` 註解，使其成為 Spring Bean。
2.  `application.usecase` 中的 Service 類別 **不加** `@Service` / `@Component`。
3.  `bootstrap.UseCaseConfiguration` 中使用 `@Bean` 方法手動創建這些 Service，並通過參數注入對應的 Port Bean（Spring 會自動匹配型別）。

## 測試策略

### Unit Tests (JUnit + Mockito)

-   **Domain**: 
    -   `ProfileTest`: 測試 Value Objects 的不變條件、Entity 的建立與更新邏輯。
-   **Application**:
    -   `CreateProfileServiceTest`: Mock `ExistsByEmailPort` 和 `SaveProfilePort`。驗證成功時呼叫 save，重複 Email 拋出例外。
    -   `GetProfileServiceTest`: Mock `LoadProfileByIdPort`。驗證找到資料回傳 DTO，找不到拋出例外。
    -   `UpdateProfileServiceTest`: Mock Load, Exists, Save。驗證更新流程及唯一性檢查。
    -   `DeleteProfileServiceTest`: Mock Delete/Exists。驗證刪除前存在性檢查。

### Integration Tests (Web & Persistence)

-   **Adapter Web (`adapter-web`)**:
    -   `ProfileControllerIT` or `ProfileControllerTest` (@WebMvcTest):
        -   Mock Use Case Ports? No, @WebMvcTest typically mocks the service layer if using Spring Boot Test. But since we are testing the Controller's mapping and error handling, we can mock the Inbound Port interfaces directly in the test class using `@MockBean`.
        -   Verify HTTP Status codes (201, 409, 400, 200, 404).
        -   Verify JSON structure of responses.
-   **Adapter Persistence (`adapter-persistence`)**:
    -   `JpaSaveProfilePortTest`, `JpaLoadProfileByIdPortTest`, etc. (@DataJpaTest + Testcontainers PostgreSQL):
        -   Verify that saving a profile with duplicate email throws exception (due to DB constraint handled by port catching DataIntegrityViolationException).
        -   Verify CRUD operations work correctly with real database.

### BDD / Cucumber Tests (Bootstrap/BDD)

-   Feature File: `src/test/resources/features/personal-profile-crud.feature`
-   Step Definitions: `PersonalProfileSteps.java`
    -   Uses `TestRestTemplate` to call actual endpoints.
    -   Steps will trigger Create, Get, Update, Delete scenarios defined in Spec.
    -   Assertions on Response Body and Headers.

## 類別清單

| 模組 | 完整路徑 (Relative to Project Root) | 職責描述 |
| :--- | :--- | :--- |
| Domain | `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/ProfileId.java` | Value Object for Profile ID |
| Domain | `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/Email.java` | Value Object for Email address |
| Domain | `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/DisplayName.java` | Value Object for Display Name |
| Domain | `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/Profile.java` | Domain Entity representing a User's Profile |
| Domain | `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/DuplicateEmailException.java` | Exception thrown when email already exists |
| Domain | `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/ProfileNotFoundException.java` | Exception thrown when profile is not found |
| Application Port In | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/in/CreateProfileUseCase.java` | Interface for creating profiles |
| Application Port In | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/in/GetProfileUseCase.java` | Interface for retrieving profiles |
| Application Port In | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/in/UpdateProfileUseCase.java` | Interface for updating profiles |
| Application Port In | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/in/DeleteProfileUseCase.java` | Interface for deleting profiles |
| Application Port Out | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/out/SaveProfilePort.java` | Interface for saving profiles |
| Application Port Out | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/out/LoadProfileByIdPort.java` | Interface for loading profiles by ID |
| Application Port Out | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/out/ExistsByEmailPort.java` | Interface for checking email uniqueness |
| Application Port Out | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/out/DeleteProfilePort.java` | Interface for deleting profiles |
| Application Use Case | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/CreateProfileService.java` | Implementation of CreateProfileUseCase |
| Application Use Case | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/GetProfileService.java` | Implementation of GetProfileUseCase |
| Application Use Case | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/UpdateProfileService.java` | Implementation of UpdateProfileUseCase |
| Application Use Case | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/DeleteProfileService.java` | Implementation of DeleteProfileUseCase |
| Adapter Web | `adapter-web/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/ProfileController.java` | REST Controller handling HTTP requests |
| Adapter Web | `adapter-web/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/dto/CreateProfileRequest.java` | DTO for creating profile input |
| Adapter Web | `adapter-web/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/dto/UpdateProfileRequest.java` | DTO for updating profile input |
| Adapter Web | `adapter-web/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/dto/ProfileResponse.java` | DTO for returning profile data |
| Adapter Web | `adapter-web/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/WebExceptionHandler.java` | Global exception handler for web layer |
| Adapter Persistence | `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/ProfileEntity.java` | JPA Entity mapped to database table |
| Adapter Persistence | `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/ProfileRepository.java` | Spring Data Repository interface |
| Adapter Persistence | `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/JpaSaveProfilePort.java` | Implementation of Save and ExistsByEmail ports |
| Adapter Persistence | `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/JpaLoadProfileByIdPort.java` | Implementation of LoadProfileById port |
| Adapter Persistence | `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/JpaDeleteProfilePort.java` | Implementation of DeleteProfile port |
| Bootstrap Config | `bootstrap/src/main/java/com/github/chunpingwang/ollamaagentoutcome/bootstrap/UseCaseConfiguration.java` | Configuration class wiring Use Cases and Ports |
| BDD Steps | `bootstrap/src/test/java/com/github/chunpingwang/ollamaagentoutcome/bdd/personalprofile/PersonalProfileSteps.java` | Cucumber step definitions for CRUD operations |
