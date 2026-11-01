# Plan 001 — 個人資料管理 (Personal Profile CRUD)

本計畫基於 Spec 001 設計，採用六角形架構（Hexagonal Architecture），確保業務邏輯獨立於框架與基礎設施。

## 領域模型 (Domain Model)

### Value Objects & Entities

由於識別碼由系統自動產生且需支援 JPA 持久化，我們定義以下核心型別：

- **ProfileId** (`record`)
    - 內容：`UUID id`
    - 不變條件：不可為 null。
    - 用途：作為 Domain Entity 的唯一標識，並封裝 UUID 操作。

- **Email** (`record`)
    - 內容：`String value`
    - 不變條件：符合 RFC 5322 基本格式（非空、包含 @）。在 Domain 層進行語法驗證。
    - 用途：確保電子郵件格式的正確性。

- **Name** (`record`)
    - 內容：`String value`
    - 不變條件：長度介於 1 到 50 之間，不能僅含空白。
    - 用途：確保姓名欄位的完整性。

- **Profile** (`class`, Immutable after creation except for updates via methods)
    - 屬性：
        - `id: ProfileId` (只讀，建立後不可變)
        - `name: Name`
        - `email: Email`
    - 行為：
        - `update(name: Name, email: Email): Profile`：回傳新的 Profile 實例或更新內部狀態（視實現而定，建議使用 Builder 模式或 Factory 來處理不可变性，但為了簡化 JPA 映射與應用服務邏輯，此處採用可變的 Entity 概念但在 Domain Service 中控制變更流程，或者更純粹地，Domain Entity 本身是可變的以反映業務狀態變化，但識別碼固定）。*修正策略*：為符合六角形架構常見實踐及 JPA 需求，Domain Entity 將包含業務規則方法。由於 Spec 要求「全量更新」，我們允許修改 name/email，但不允許修改 id。
    - 不變條件：Email 在系統內必須唯一（由 Application/Adapter 層保證，Domain 層不檢查全域唯一性，僅檢查格式）。

### Domain Exceptions

- **InvalidNameException**：當姓名不符合規範時拋出。
- **InvalidEmailException**：當電子郵件格式錯誤時拋出。
- **ProfileNotFoundException**：當查詢指定 ID 的個人資料不存在時拋出。
- **DuplicateEmailException**：當嘗試建立或更新已存在的電子郵件時拋出。

## Use Cases & Ports

每個使用者情境對應一個 Inbound Port (Use Case) 及其實作 Service。Outbound Ports 定義資料存取契約。

### US-1: 新增個人資料 (`CreateProfile`)

- **Inbound Port**: `CreateProfileUseCase`
    ```java
    void execute(CreateProfileCommand command);
    // CreateProfileCommand contains: Name, Email
    ```
- **Outbound Port**: `SaveProfilePort`
    ```java
    ProfileId save(Profile profile);
    ```
- **Service Implementation**: `CreateProfileService`
    - 驗證輸入參數。
    - 呼叫 `SaveProfilePort.save()`。
    - 若發生重複異常，轉為業務邏輯處理（通常由 Adapter 捕捉並轉換例外，或使用 Repository 回傳結果判斷）。*注意*：根據 Spec，409 Conflict 來自於唯一性約束違反。這通常在 Persistence 層檢測到後向上拋出 Domain Exception，再由 Web Layer 轉換為 HTTP 狀態碼。因此 `SaveProfilePort` 應在衝突時拋出 `DuplicateEmailException`。

### US-2: 查詢個人資料 (`GetProfile`)

- **Inbound Port**: `GetProfileUseCase`
    ```java
    GetProfileResult execute(GetProfileQuery query);
    // GetProfileQuery contains: ProfileId id
    // GetProfileResult wraps the found Profile or throws exception
    ```
- **Outbound Port**: `LoadProfilePort`
    ```java
    Optional<Profile> findById(ProfileId id);
    ```
- **Service Implementation**: `GetProfileService`
    - 呼叫 `LoadProfilePort.findById(id)`。
    - 若不存在，拋出 `ProfileNotFoundException`。
    - 將 Domain Entity 映射至 Result DTO (或直接在 Service 中返回 Entity，由 Controller 負責映射)。建議 Application 層返回純 Java 物件，不依賴 Spring/DTO。

### US-3: 修改個人資料 (`UpdateProfile`)

- **Inbound Port**: `UpdateProfileUseCase`
    ```java
    void execute(UpdateProfileCommand command);
    // UpdateProfileCommand contains: ProfileId id, Name name, Email email
    ```
- **Outbound Ports**:
    - `LoadProfilePort`: `Optional<Profile> findById(ProfileId id);`
    - `SaveProfilePort`: `void save(Profile profile);`
- **Service Implementation**: `UpdateProfileService`
    - 使用 `LoadProfilePort` 檢查記錄是否存在。若不存在，拋出 `ProfileNotFoundException`。
    - 驗證新的 `name` 和 `email` 格式（可委託給 Value Object 建構子）。
    - 呼叫實體的更新方法並透過 `SaveProfilePort.save()` 持久化。
    - *注意*：唯一性檢查應在 `SaveProfilePort` 實作中處理，或在 Save 前查詢確認。為簡化，讓 Persistence Adapter 捕捉 DB 約束違反並轉為 `DuplicateEmailException` 向上拋出。

### US-4: 刪除個人資料 (`DeleteProfile`)

- **Inbound Port**: `DeleteProfileUseCase`
    ```java
    void execute(DeleteProfileCommand command);
    // DeleteProfileCommand contains: ProfileId id
    ```
- **Outbound Ports**:
    - `LoadProfilePort`: `Optional<Profile> findById(ProfileId id);` (用於檢查存在性)
    - `RemoveProfilePort`: `void deleteById(ProfileId id);`
- **Service Implementation**: `DeleteProfileService`
    - 先檢查資料是否存在（可選，直接調用 delete 並在失敗時捕獲異常也是一種策略，但明確檢查可提供更清晰的錯誤訊息或避免不必要的寫入操作）。這裡選擇先 Load 再 Delete，若 Load 返回空則拋出 `ProfileNotFoundException`。
    - 呼叫 `RemoveProfilePort.deleteById(id)`。

## Web Adapter

Controller 負責接收 HTTP 請求、驗證輸入格式（Jakarta Validation）、呼叫 Application Use Case，並將結果轉換為 HTTP 回應。

- **Controller**: `ProfileRestController`
    - `POST /profiles` -> `createProfile(CreateProfileRequest request)`
        - 狀態碼：201 Created + Location Header。
        - DTO: `CreateProfileRequest { name, email }`
    - `GET /profiles/{id}` -> `getProfile(@PathVariable("id") UUID id)`
        - 狀態碼：200 OK。
        - DTO: `GetProfileResponse { id, name, email }`
    - `PUT /profiles/{id}` -> `updateProfile(@PathVariable("id") UUID id, UpdateProfileRequest request)`
        - 狀態碼：200 OK。
        - DTO: `UpdateProfileRequest { name, email }` (與 Create 結構相同)
    - `DELETE /profiles/{id}` -> `deleteProfile(@PathVariable("id") UUID id)`
        - 狀態碼：204 No Content。

- **Error Handling**: `GlobalExceptionHandler` (`@RestControllerAdvice`)
    - `InvalidInputException` (由 Jakarta Validation 觸發或自定義): 400 Bad Request.
    - `DuplicateEmailException`: 409 Conflict.
    - `ProfileNotFoundException`: 404 Not Found.
    - *注意*：不攔截通用 Exception，避免覆蓋 Spring Boot 的預設錯誤處理（如 404 for unknown paths）。

## Persistence Adapter

負責 Domain Entity 與 JPA Entity 之間的轉換，以及資料存取操作。

- **JPA Entity**: `ProfileEntity`
    - 欄位：`id` (UUID, Primary Key), `name`, `email`.
    - 實現 `Persistable<UUID>` 以支援手動 ID 生成（因為識別碼在 Domain 層產生或由 Controller 接收並傳遞下來，而非 DB Auto Increment）。
    - 唯一約束：`UNIQUE(email)`。
- **Repository Interface**: `ProfileRepository extends JpaRepository<ProfileEntity, UUID>, JpaSpecificationExecutor<...>` (若需複雜查詢，此處僅需基本 CRUD)
    - 繼承標準方法即可滿足需求。
- **Port Implementations**:
    - `JpaSaveProfilePort implements SaveProfilePort`
        - 將 Domain `Profile` 轉為 `ProfileEntity`。
        - 呼叫 `repository.saveAndFlush(entity)`。
        - 捕捉 `DataIntegrityViolationException` 並拋出 `DuplicateEmailException`。
    - `JpaLoadProfilePort implements LoadProfilePort`
        - 呼叫 `repository.findById(id)`。
        - 將 `Optional<Entity>` 轉回 `Optional<Domain Profile>`。
    - `JpaRemoveProfilePort implements RemoveProfilePort`
        - 呼叫 `repository.deleteById(id)`。

## Bootstrap

負責組裝 Bean，特別是 Application 層的 Use Case Service。

- **Configuration Class**: `UseCaseConfiguration` (`@Configuration`)
    - 定義以下 `@Bean`：
        - `createProfileService(CreateProfileUseCase useCase)` -> 實際上應注入 Port 依賴。正確做法是 Service 建構子接收 Ports。
        - `getProfileService(...)`
        - `updateProfileService(...)`
        - `deleteProfileService(...)`
    - *注意*：Ports (Repository/Adapter) 由 Spring Data JPA 自動掃描生成代理物件或手動定義為 Bean（若使用自訂實作）。在此架構中，Persistence Adapter 的 Port 實作類別也需註冊為 Bean，或者利用 Spring Data Repository 介面直接作為 Outbound Port？
    - *修正策略*：根據 System Prompt，Application 層定義 Outbound Port 介面。Adapter 層實作這些介面並註冊為 Bean。因此 `UseCaseConfiguration` 需要將這些 Port 實作類註冊為 Bean，或者直接讓它們被 Component Scan 發現（如果加上 `@Component`）。但 System Prompt 強調「application 只能依賴 domain」且「bootstrap 以 @Configuration 組裝」。為了嚴格控制依賴，建議在 `adapter-persistence` 模組中也提供一個配置類，或在 `bootstrap` 的配置中明確引用。考慮到模塊分離，最乾淨的方式是在 `adapter-persistence` 中定義一個 `PersistencePortConfiguration` (`@Configuration`) 來註冊 Port 實現，然後在 `bootstrap` 的 `UseCaseConfiguration` 中通過 `@Import(PersistencePortConfiguration.class)` 引入，或者簡單地讓所有帶有 `@Service`/`@Repository` 註解的類被自動掃描。
    - *System Prompt 限制檢查*："bootstrap 的 UseCaseConfiguration ... use case bean 只能在 UseCaseConfiguration 以 @Bean 定義一次"。這暗示我們可能需要手動組裝 Application Services。對於 Persistence Ports，由於它們是 Adapter 的一部分，通常由 Spring Data JPA 或自訂 Bean 管理。若使用自訂 Port 實作類別（如 `JpaSaveProfilePort`），應將其標記為 `@Service` 或由 Configuration 暴露為 Bean。鑑於架構清晰度，我們將 `Jpa...Port` 類別標記為 `@Component` (在 adapter-persistence 模組)，並在 bootstrap 中確保它們可被掃描，或者在 `UseCaseConfiguration` 中手動注入這些 Port 實例並傳入 Service 建構子。
    - *最佳實踐調整*：為了符合「依賴反轉」和「測試隔離」，Application Services 應該透過介面接收 Ports。Ports 的具體實現由 Bootstrap 決定。因此，`UseCaseConfiguration` 將負責創建 Application Services 的 Bean，並將所需的 Port 實現作為參數傳遞。

## 測試策略

1. **Domain Layer Tests** (`domain/src/test/java/...`)
   - 純 JUnit + AssertJ。
   - 驗證 Value Objects 的不變條件與格式檢查。
   - 驗證 Domain Entity 的行為邏輯。

2. **Application Layer Tests** (`application/src/test/java/...`)
   - Mock Outbound Ports (`LoadProfilePort`, `SaveProfilePort`, etc.) using Mockito。
   - 驗證 Use Case 的服務邏輯（如：找不到 ID 時拋出例外、重複 Email 時的處理）。
   - 不啟動 Spring Context。

3. **Web Adapter Tests** (`adapter-web/src/test/java/...`)
   - `@WebMvcTest(ProfileRestController.class)`
   - Mock Application Use Cases (Inbound Ports)。
   - 驗證 HTTP 狀態碼、Request/Response JSON 結構、錯誤訊息映射。

4. **Persistence Adapter Tests** (`adapter-persistence/src/test/java/...`)
   - `@DataJpaTest` + Testcontainers PostgreSQL。
   - 測試 Repository 操作及 Port 實作類（轉換器邏輯、唯一性約束捕捉）。
   - 使用 `@ServiceConnection` 連接 Testcontainer。

5. **BDD / Integration Tests** (`bootstrap/src/test/resources/features/`)
   - Cucumber Feature: `personal-profile-crud.feature`
   - 步驟定義在 `bdd` 套件，使用 `TestRestTemplate` 呼叫實際 API。
   - 完整 Spring Context 啟動，包含資料庫。

## 類別清單

| 模組 | 路徑 (相對專案根目錄) | 職責描述 |
| :--- | :--- | :--- |
| domain | `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/ProfileId.java` | Value Object, Profile UUID wrapper |
| domain | `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/Email.java` | Value Object, Email validation & storage |
| domain | `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/Name.java` | Value Object, Name validation & storage |
| domain | `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/entity/Profile.java` | Domain Entity, Business logic for profile data |
| domain | `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/exception/DuplicateEmailException.java` | Domain Exception, Duplicate email error |
| domain | `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/exception/InvalidInputException.java` | Domain Exception, Invalid input format |
| domain | `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/exception/ProfileNotFoundException.java` | Domain Exception, Record not found |
| application | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/in/CreateProfileUseCase.java` | Inbound Port Interface, Create operation contract |
| application | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/in/GetProfileUseCase.java` | Inbound Port Interface, Get operation contract |
| application | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/in/UpdateProfileUseCase.java` | Inbound Port Interface, Update operation contract |
| application | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/in/DeleteProfileUseCase.java` | Inbound Port Interface, Delete operation contract |
| application | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/out/SaveProfilePort.java` | Outbound Port Interface, Save profile data |
| application | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/out/LoadProfilePort.java` | Outbound Port Interface, Load profile by ID |
| application | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/out/RemoveProfilePort.java` | Outbound Port Interface, Remove profile by ID |
| application | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/CreateProfileService.java` | Use Case Implementation, Create logic orchestration |
| application | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/GetProfileService.java` | Use Case Implementation, Get logic orchestration |
| application | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/UpdateProfileService.java` | Use Case Implementation, Update logic orchestration |
| application | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/DeleteProfileService.java` | Use Case Implementation, Delete logic orchestration |
| adapter-web | `adapter-web/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/controller/ProfileRestController.java` | REST Controller, HTTP request handling |
| adapter-web | `adapter-web/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/dto/CreateProfileRequest.java` | Request DTO for creation/update |
| adapter-web | `adapter-web/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/dto/GetProfileResponse.java` | Response DTO for retrieval |
| adapter-web | `adapter-web/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/handler/GlobalExceptionHandler.java` | Exception Handler, Maps exceptions to HTTP status codes |
| adapter-persistence | `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/entity/ProfileEntity.java` | JPA Entity, Database mapping |
| adapter-persistence | `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/repository/ProfileRepository.java` | Spring Data Repository Interface |
| adapter-persistence | `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/port/JpaSaveProfilePort.java` | Port Implementation, Save logic with constraint check |
| adapter-persistence | `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/port/JpaLoadProfilePort.java` | Port Implementation, Load logic |
| adapter-persistence | `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/port/JpaRemoveProfilePort.java` | Port Implementation, Remove logic |
| bootstrap | `bootstrap/src/main/java/com/github/chunpingwang/ollamaagentoutcome/bootstrap/config/UseCaseConfiguration.java` | Configuration, Wires Application Services and Ports |
| bdd | `bootstrap/src/test/resources/features/personal-profile-crud.feature` | Cucumber Feature File, BDD Scenarios |
| bdd | `bootstrap/src/test/java/com/github/chunpingwang/ollamaagentoutcome/bdd/steps/PersonalProfileSteps.java` | Step Definitions, HTTP interactions via TestRestTemplate |
