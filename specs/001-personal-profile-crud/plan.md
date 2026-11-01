# Plan 001 — 個人資料管理 (Personal Profile CRUD)

本計畫基於 Spec 001 設計「個人資料」模組的技術實現細節。採用六角形架構（Hexagonal Architecture），將業務邏輯隔離於 domain 層，透過 ports 與外部系統（Web API, Database）解耦。

## 領域模型 (Domain Model)

### Value Objects & Entities

由於需求僅涉及單一資源且識別碼由系統或客戶端生成（UUID），我們定義以下核心型別：

- **`ProfileId`** (`record`)
    - 內容：`String uuid`
    - 不變條件：非空字串，符合 UUID 格式（建議在建構子驗證）。
    - 用途：作為 Domain Entity 的唯一識別符，確保類型安全。

- **`Email`** (`record`)
    - 內容：`String value`
    - 不變條件：非空白，符合 RFC 5322 基本 Email 格式（使用正則表達式 `^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\.[a-zA-Z]{2,}$` 驗證）。
    - 用途：封裝電子郵件邏輯，確保全域唯一性檢查前的有效性。

- **`DisplayName`** (`record`)
    - 內容：`String value`
    - 不變條件：允許為 null 或空白字符串（代表未設定/匿名）。若提供值，長度限制可設為 1~100 字符（依常見規範，此處暫不嚴格限制上限以簡化，但需處理 null/blank）。
    - 用途：用戶顯示名稱。

- **`Profile`** (Domain Entity)
    - 屬性：
        - `id: ProfileId`
        - `email: Email`
        - `displayName: DisplayName`
    - 行為：
        - `update(displayName: DisplayName)`：更新顯示名稱。注意：根據 RESTful PUT 慣例與 Spec，PUT 通常視為全量替換，但若 email 不可變（作為身份憑證），則僅允許更新 displayName。Spec US-3 提到「更新我的個人資料（如顯示名稱）」且範例中 PUT body 包含 email，但鑑於 email 是唯一索引且通常不應在更新時變更（除非有複雜遷移邏輯，此處假設 email 建立後固定，或 PUT 時若 email 改變則視為錯誤/不支持，為符合 Spec "Update existing profile" 且保持簡單，我們定義 `update` 方法主要針對非 ID 欄位。*修正思考*：Spec US-3 的 JSON payload 包含 email，但 HTTP Method 是 PUT。通常在 CRUD 中，如果 ID 已存在，PUT 該資源可能意味著更新所有可寫字段。然而，Email 作為唯一鍵，若在 Update 時提供不同的 Email，會違反唯一性約束或需要特殊處理。考慮到 Spec 未明確說明「更換電子郵件」的情境，且為了簡化 Domain 模型，我們將 `Profile` 設計為：**ID 和 Email 在建構後不可變**，或者 **DisplayName 是可更新的**。
        - *重新審視 Spec US-3*：場景描述為「成功更新個人資料... display_name 應變更為 'New Name'」。Payload 中有 email。如果系統允許用戶更改綁定郵箱，那將非常複雜（涉及舊郵箱解绑、新郵箱驗證等）。基於「最小可行性」與「背景：註冊用戶維護基本資訊」，通常顯示名稱是最常更動的，而郵箱一旦註冊往往固定或由後續安全模組管理。**為了避免過度設計並符合一般 CRUD 實踐，本計畫假設：Email 在 Profile 建立後即鎖定，不可通過此 API 變更。** 若前端發送不同 email，後端應忽略或報錯？鑑於 Spec 沒有定義 "Change Email" 的業務規則，我們採取保守策略：**PUT 請求中的 email 必須與路徑對應的資源現有 email 一致，否則視為數據不一致錯誤；或者更簡單地，Domain Entity 僅暴露 `updateDisplayName` 方法，Web Adapter 負責比對 payload 中的 email 是否與查詢結果一致。**
        - *決定*：為簡化 Domain，`Profile` 提供 `changeDisplayName(DisplayName)` 方法。Web Layer 會先查出實體，確認 Payload 的 Email 與實體當前 Email 相同（防止併發或誤操作），然後呼叫該方法。

### Domain Exceptions

- **`DuplicateEmailException extends RuntimeException`**：當嘗試創建已存在電子郵件的個人資料時拋出。
- **`ProfileNotFoundException extends RuntimeException`**：當根據 ID 找不到資料時拋出。
- **`InvalidEmailFormatException extends IllegalArgumentException`**：當輸入的 Email 格式無效時拋出（也可由 Validation 層攔截，但 Domain 可作為最後防線）。

## Use Cases 與 Ports

本模組包含四個主要用例，分別對應 CRUD 操作。

### Inbound Ports (Use Case Interfaces)

1.  **`CreateProfileUseCase`**
    ```java
    void execute(CreateProfileRequest request);
    // Request: record CreateProfileRequest(Email email, DisplayName displayName)
    ```

2.  **`GetProfileUseCase`**
    ```java
    ProfileResult execute(ProfileId id);
    // Result: record ProfileResult(String email, String displayName)
    ```

3.  **`UpdateProfileUseCase`**
    ```java
    void execute(UpdateProfileRequest request);
    // Request: record UpdateProfileRequest(ProfileId id, String newDisplayName) 
    // Note: Web layer handles email consistency check before calling this.
    ```

4.  **`DeleteProfileUseCase`**
    ```java
    void execute(ProfileId id);
    ```

### Outbound Ports (Repository Interfaces)

為符合介面分離原則，將讀寫操作拆分：

1.  **`SaveProfilePort`**
    ```java
    void save(Profile profile);
    boolean existsByEmail(Email email);
    ```

2.  **`LoadProfilePort`**
    ```java
    Optional<Profile> findById(ProfileId id);
    ```

3.  **`RemoveProfilePort`**
    ```java
    void deleteById(ProfileId id);
    ```

*(註：為了簡化實作，也可以合併為一個 `ProfileRepositoryPort`，但根據 SOLID 介面分離原則，拆分為 Load/Save/Delete 更為清晰。)*

## Web Adapter

套件：`com.github.chunpingwang.ollamaagentoutcome.adapter.web`

-   **Controller**: `ProfileRestController`
    -   Base Path: `/api/profiles`
    -   Methods:
        -   `POST /`: 呼叫 `CreateProfileUseCase`。回傳 201 + Location header。
        -   `GET /{id}`: 呼叫 `GetProfileUseCase`。回傳 Profile DTO。若找不到拋出例外轉 404。
        -   `PUT /{id}`: 
            1. 先呼叫 `GetProfileUseCase` (或專門的檢查方法) 確認存在且 Email 匹配（若 payload 有 email）。
            2. 呼叫 `UpdateProfileUseCase`。
            3. 回傳 200 OK。
        -   `DELETE /{id}`: 呼叫 `DeleteProfileUseCase`。回傳 204 No Content。若找不到拋出例外轉 404。

-   **DTOs**:
    -   `CreateProfileRequestDto`: `{ "email": String, "displayName": String }`
    -   `UpdateProfileRequestDto`: `{ "email": String, "displayName": String }` (雖 PUT 包含 email，但業務邏輯僅更新 displayName，web layer 負責驗證一致性)
    -   `ProfileResponseDto`: `{ "id": String, "email": String, "displayName": String }`

-   **Error Handling**: `GlobalExceptionHandler` (`@RestControllerAdvice`)
    -   `DuplicateEmailException` -> HTTP 409 Conflict
    -   `ProfileNotFoundException` -> HTTP 404 Not Found
    -   `MethodArgumentNotValidException` (Jakarta Validation fail) -> HTTP 400 Bad Request
    -   `HttpMessageNotReadableException` -> HTTP 400 Bad Request

## Persistence Adapter

套件：`com.github.chunpingwang.ollamaagentoutcome.adapter.persistence`

-   **JPA Entity**: `ProfileEntity`
    -   實作 `Persistable<String>` (因為 ID 由客戶端或應用層生成 UUID)。
    -   Fields: `id` (String, PK), `email` (String, Unique Index), `display_name` (String).
    -   Logic: 
        -   `isNew()`: 判斷是否為新記錄。由於是 Persistable，需管理 `isNew` 狀態。在轉換器中建立實體時設為 true，保存後設為 false。
    
-   **Repository Interface**: `ProfileJpaRepository extends JpaRepository<ProfileEntity, String>, JpaSpecificationExecutor...`
    -   Method: `Optional<ProfileEntity> findByEmail(String email)`
    -   Method: `boolean existsByEmail(String email)`

-   **Port Implementations**:
    -   `JpaSaveProfilePort`: 實現 `SaveProfilePort`。負責將 Domain `Profile` 轉為 `ProfileEntity` 並呼叫 Repository。使用 `saveAndFlush` 以確保測試中的唯一性約束立即生效。檢查重複 Email 邏輯可在此處或 UseCase 層，建議在 Save 前檢查或使用 DB Constraint + Exception Catching。根據 Spec "嘗試建立重複電子郵件...失敗"，我們會在 Port 層捕捉 `DataIntegrityViolationException` 並拋出 `DuplicateEmailException`。
    -   `JpaLoadProfilePort`: 實現 `LoadProfilePort`。查詢 Entity 並轉回 Domain `Profile`。若無資料回傳 Optional.empty()。
    -   `JpaRemoveProfilePort`: 實現 `RemoveProfilePort`。刪除記錄。

## Bootstrap

套件：`com.github.chunpingwang.ollamaagentoutcome.bootstrap`

-   **Configuration Class**: `UseCaseConfiguration` (`@Configuration`)
    -   定義以下 Beans (Prototype 或 Singleton? Use cases 通常無狀態，Singleton):
        -   `createProfileService(CreateProfileUseCase)` -> Bean name: `createProfileUseCase`
        -   `getProfileService(GetProfileUseCase)` -> Bean name: `getProfileUseCase`
        -   `updateProfileService(UpdateProfileUseCase)` -> Bean name: `updateProfileUseCase`
        -   `deleteProfileService(DeleteProfileUseCase)` -> Bean name: `deleteProfileUseCase`
    -   *注意*：具體的 Service 類別（如 `CreateProfileService`）在 application 模組中實作，此處僅負責將它們註冊為 Spring Bean。由於架構要求 Application 層不依賴 Spring，我們需要在 Application 層提供這些 Service 類別，並在 Bootstrap 層的 Configuration 中宣告 `@Bean`。或者，更常見的做法是讓 Application 層的 Service 類別加上 `@Component`？**不行**，System Prompt 規定 "application 禁止 Spring annotation"。因此，必須在 `bootstrap.UseCaseConfiguration` 中以 `@Bean` 方法手動創建這些 Service 實例並注入其所需的 Ports。

## 測試策略

1.  **Domain Layer Tests**:
    -   `EmailTest`: 驗證格式正則表達式與無效輸入拋出例外。
    -   `DisplayNameTest`: 驗證 null/blank 處理。
    -   `ProfileEntityTest`: 驗證 `changeDisplayName` 行為。

2.  **Application Layer Tests (Unit)**:
    -   `CreateProfileServiceTest`: Mock `SaveProfilePort`。測試成功保存、捕捉重複 Email 異常轉發。
    -   `GetProfileServiceTest`: Mock `LoadProfilePort`。測試找到資料回傳結果，找不到拋出 `ProfileNotFoundException`。
    -   `UpdateProfileServiceTest`: Mock `LoadProfilePort`, `SaveProfilePort`。測試更新邏輯。
    -   `DeleteProfileServiceTest`: Mock `RemoveProfilePort`。測試刪除及不存在時的異常。

3.  **Web Adapter Tests (Integration/Mock)**:
    -   `ProfileRestControllerTest` (`@WebMvcTest`): 
        -   Mock Use Case Beans。
        -   測試各 HTTP Method 的狀態碼、Header、JSON 序列化/反序列化。
        -   測試 Validation Error (400)。
        -   測試 Domain Exception 映射到 404, 409。

4.  **Persistence Adapter Tests**:
    -   `JpaProfileRepositoryTest` (`@DataJpaTest` + Testcontainers): 
        -   測試 JPA Entity 的持久化、唯一性約束。
        -   測試 Port 實作（如 `JpaSaveProfilePort`）是否能正確轉換並處理 DB 例外。

5.  **BDD / Cucumber Tests**:
    -   Feature File: `bootstrap/src/test/resources/features/personal-profile-crud.feature`
    -   Step Definitions: `com.github.chunpingwang.ollamaagentoutcome.bdd.ProfileSteps`
    -   使用 `TestRestTemplate` 呼叫實際 API，驗證完整流程。

## 類別清單

| 模組 | 套件 | 類別名稱 | 路徑 (相對於專案根目錄) | 職責描述 |
| :--- | :--- | :--- | :--- | :--- |
| domain | ...domain | ProfileId | `domain/src/main/java/.../domain/ProfileId.java` | Value Object: UUID 識別碼封裝 |
| domain | ...domain | Email | `domain/src/main/java/.../domain/Email.java` | Value Object: 電子郵件格式與有效性驗證 |
| domain | ...domain | DisplayName | `domain/src/main/java/.../domain/DisplayName.java` | Value Object: 顯示名稱封裝 |
| domain | ...domain | Profile | `domain/src/main/java/.../domain/Profile.java` | Domain Entity: 個人資料核心邏輯與狀態管理 |
| domain | ...domain | DuplicateEmailException | `domain/src/main/java/.../domain/DuplicateEmailException.java` | Exception: 重複電子郵件錯誤 |
| domain | ...domain | ProfileNotFoundException | `domain/src/main/java/.../domain/ProfileNotFoundException.java` | Exception: 找不到個人資料錯誤 |
| application | ...port.in | CreateProfileUseCase | `application/src/main/java/.../application/port/in/CreateProfileUseCase.java` | Inbound Port: 建立用例介面 |
| application | ...port.in | GetProfileUseCase | `application/src/main/java/.../application/port/in/GetProfileUseCase.java` | Inbound Port: 查詢用例介面 |
| application | ...port.in | UpdateProfileUseCase | `application/src/main/java/.../application/port/in/UpdateProfileUseCase.java` | Inbound Port: 更新用例介面 |
| application | ...port.in | DeleteProfileUseCase | `application/src/main/java/.../application/port/in/DeleteProfileUseCase.java` | Inbound Port: 刪除用例介面 |
| application | ...usecase | CreateProfileService | `application/src/main/java/.../application/usecase/CreateProfileService.java` | Use Case Impl: 處理建立業務規則與流程 |
| application | ...usecase | GetProfileService | `application/src/main/java/.../application/usecase/GetProfileService.java` | Use Case Impl: 處理查詢業務邏輯 |
| application | ...usecase | UpdateProfileService | `application/src/main/java/.../application/usecase/UpdateProfileService.java` | Use Case Impl: 處理更新業務邏輯 |
| application | ...usecase | DeleteProfileService | `application/src/main/java/.../application/usecase/DeleteProfileService.java` | Use Case Impl: 處理刪除業務邏輯 |
| application | ...port.out | SaveProfilePort | `application/src/main/java/.../application/port/out/SaveProfilePort.java` | Outbound Port: 儲存操作介面 |
| application | ...port.out | LoadProfilePort | `application/src/main/java/.../application/port/out/LoadProfilePort.java` | Outbound Port: 載入操作介面 |
| application | ...port.out | RemoveProfilePort | `application/src/main/java/.../application/port/out/RemoveProfilePort.java` | Outbound Port: 移除操作介面 |
| adapter-web | ...web | ProfileRestController | `adapter-web/src/main/java/.../adapter/web/ProfileRestController.java` | REST Controller: HTTP 請求路由與 DTO 轉換 |
| adapter-web | ...web | CreateRequestDto | `adapter-web/src/main/java/.../adapter/web/CreateRequestDto.java` | Request DTO: 建立請求數據傳輸物件 |
| adapter-web | ...web | UpdateRequestDto | `adapter-web/src/main/java/.../adapter/web/UpdateRequestDto.java` | Request DTO: 更新請求數據傳輸物件 |
| adapter-web | ...web | ProfileResponseDto | `adapter-web/src/main/java/.../adapter/web/ProfileResponseDto.java` | Response DTO: 回應數據傳輸物件 |
| adapter-web | ...web | GlobalExceptionHandler | `adapter-web/src/main/java/.../adapter/web/GlobalExceptionHandler.java` | Exception Handler: 全域異常轉 HTTP 狀態碼 |
| adapter-persistence | ...persistence | ProfileEntity | `adapter-persistence/src/main/java/.../adapter/persistence/ProfileEntity.java` | JPA Entity: 資料庫映射實體 |
| adapter-persistence | ...persistence | ProfileJpaRepository | `adapter-persistence/src/main/java/.../adapter/persistence/ProfileJpaRepository.java` | Spring Data Repo: 資料存取介面 |
| adapter-persistence | ...persistence | JpaSaveProfilePortImpl | `adapter-persistence/src/main/java/.../adapter/persistence/JpaSaveProfilePortImpl.java` | Port Impl: 實現 SaveProfilePort，處理轉換與 DB 操作 |
| adapter-persistence | ...persistence | JpaLoadProfilePortImpl | `adapter-persistence/src/main/java/.../adapter/persistence/JpaLoadProfilePortImpl.java` | Port Impl: 實現 LoadProfilePort，查詢並轉換回 Domain |
| adapter-persistence | ...persistence | JpaRemoveProfilePortImpl | `adapter-persistence/src/main/java/.../adapter/persistence/JpaRemoveProfilePortImpl.java` | Port Impl: 實現 RemoveProfilePort，刪除記錄 |
| bootstrap | ...bootstrap | UseCaseConfiguration | `bootstrap/src/main/java/.../bootstrap/UseCaseConfiguration.java` | Configuration: 組裝 Application Layer Beans |
