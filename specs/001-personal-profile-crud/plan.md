# Plan 001 — 個人資料管理 (Personal Profile CRUD)

本計畫基於 Spec 001 設計，採用六角形架構（Ports and Adapters），確保領域邏輯獨立於框架細節。

## 1. 領域模型 (Domain Model)

### Value Objects & Entities
- **ProfileId** (`record`)
    - 內容：`UUID id`
    - 不變條件：不可為 null。
- **Email** (`record`)
    - 內容：`String value`
    - 不變條件：符合 RFC 5322 基本格式（使用正則表達式驗證）、全域唯一性由 Repository Port 保證、非空白。
- **DisplayName** (`record`)
    - 內容：`String value`
    - 不變條件：允許為 null 或空字串（表示未設定）。若僅含空白字符視為空。
- **Profile** (`class`, immutable state via methods, or mutable for JPA mapping in adapter? -> Domain Entity is usually rich domain object. For simplicity with JPA, we use a standard class with private fields, but logic stays here.)
    - *修正*：根據 System Prompt "Domain 物件以 record 或不可變類別為主"，但 JPA Entity 需要 setter 或建構子。通常做法是 Domain Entity 保持業務規則，JPA Entity 在 Adapter 層處理映射。這裡我們將 `Profile` 定義為包含業務邏輯的領域實體。為了配合 JPA 與 Spring Data，我們讓它成為一個普通的 Java Class，但在 Application/Adapter 層進行轉換。或者更純淨的做法：Domain Entity 是不可變的 Value Object 集合？不，CRUD 通常需要狀態變更。
    - *決定*：`Profile` 是一個領域實體類。它持有 `id`, `email`, `displayName`。它提供驗證方法。由於 System Prompt 提到 "Domain 若自行產生識別碼... JPA Entity 不可同時使用 @GeneratedValue"，且 UUID 通常在應用層或 DTO 層生成並傳入，或由 DB 生成。Spec 中 GET 示例使用了固定 UUID，暗示 ID 可能由客戶端指定或由系統生成。鑑於 RESTful 慣例和 Spec 中的 "Location" header，ID 應在建立時確定。為了簡化並符合常見實踐，我們假設 **UUID 由應用程式層（Use Case）生成**，作為輸入的一部分傳遞給 Port，這樣 Domain 不需要依賴 UUID 生成器，也避免了 JPA 自動生成的複雜性（除非必要）。如果 ID 是外部提供的，則不需 Persistable 陷阱。讓我們檢查 Spec："假設已有一個 ID 為..."。這通常意味著 ID 是資源標識符。如果是 POST 創建，誰負責生成 ID？通常是 Server。但為了避免 JPA `@GeneratedValue` 與自訂 ID 的衝突，我們將採用 **"Client-Generated UUID"** 模式或 **"Application Layer Generates UUID"** 模式。
    - *最佳實踐調整*：為了嚴格遵守 Persistence 陷阱提示（關於 `Persistable<UUID>`），如果 ID 是由資料庫生成的，我們需要處理 isNew。但如果 ID 是由前端或應用層生成的 UUID，我們可以簡單地將其視為普通欄位。考慮到現代 API 設計，讓 Client 提供 UUID (RFC 4122) 是一種選擇，或者 Server 生成。這裡我們選擇 **Server 生成 UUID**，但在 Application UseCase 中生成，然後傳入 Domain Entity。JPA Entity 將使用 `@Id private UUID id;` 且不使用 `@GeneratedValue`，因為值已經存在。這樣就不需要 `Persistable` 接口來判斷是否為新記錄嗎？不，Spring Data JPA 在呼叫 `save()` 時，如果實體沒有 ID 會 insert，有 ID 會 update。如果我們在 save 前設定了 ID，它會被當作 update 嘗試（除非主鍵不存在）。對於新建，我們希望它是 Insert。因此，即使 ID 已設定，只要 DB 中不存在該 ID，`saveAndFlush` 應該能正確執行 INSERT（依賴唯一約束失敗來區分衝突？不，INSERT 成功即代表新增）。
    - *關鍵決策*：為了避免 `ObjectOptimisticLockingFailureException` 和複雜的 `isNew` 邏輯，我們將採用 **"ID by Client"** 策略（Client 發送 UUID）或 **"Application generates UUID, but we treat it as new if not found"**。鑑於 Spec 未明確指出誰生成 ID，但 RESTful POST 通常由 Server 分配 URI。然而，為了簡化技術實現並符合 "Domain Exception" 的設計，我們將讓 **Use Case 生成 UUID** 作為輸入的一部分傳入 DTO/Entity。JPA Entity 將簡單地映射這個 UUID。當調用 `repository.save(entity)` 時，因為 entity 帶有 ID，Spring Data 可能會嘗試 UPDATE。如果記錄不存在，UPDATE 影響行數為 0。**這是不行的**。
    - *修正方案*：使用 Spring Data JPA 的 `CrudRepository.save()` 行為：如果實體有 ID 且資料庫中存在該 ID，則更新；否則插入。這要求底層驅動支援 "Insert or Update" 或者我們先檢查是否存在。更標準的做法是：**對於新建操作，不使用 `save()`，而是使用 `insert()` (如果有) 或確保 `save()` 能正確處理。在 PostgreSQL + Hibernate 中，如果主鍵已存在值，它會執行 UPDATE。如果該主鍵不存在，Hibernate 5.2+ 有時會自動轉換為 INSERT，但這取決於配置和版本，並不總是可靠（特別是涉及唯一約束時）。**
    - *最安全的做法*：遵循 System Prompt 中的提示："Domain 若自行產生識別碼... JPA Entity 不可同時使用 @GeneratedValue... 請讓 Entity 實作 Persistable<UUID>..."。這暗示了即使 ID 由外部提供，JPA 也需要知道它是新的。因此，我們將採用 **Application Layer Generates UUID** 並將其作為輸入傳入 UseCase。Port 接收帶有 UUID 的 Profile。Adapter 將 Domain Profile 轉為 JPA Entity。為了讓 `save` 工作，我們將依賴 Spring Data JPA 的行為：如果實體有 ID，它嘗試 UPDATE。如果沒有找到行，它不會自動 INSERT（除非使用特定的 saveAndFlush 行為或自訂 Repository）。
    - *重新審視 Spec*：Spec 說 "POST ... /api/profiles"。通常回應包含新創建的資源 URI。如果 Client 不提供 ID，Server 必須生成。讓我們假設 **Server 生成 UUID**。那麼在 Application 層，我們生成 UUID，創建 Domain Object，然後調用 Port。Port 實現中，我們需要確保這是 Insert。
    - *簡化策略以符合 Prompt 陷阱警告*：Prompt 特別警告了 `Persistable` 的使用場景是當 Domain 自行產生 ID 時。如果我們在 Application 層生成 ID，Domain 對象仍然持有該 ID。對於 JPA，最簡單且無副作用的方法是：**不使用 `@GeneratedValue`，而是手動設置 ID。為了確保 `save()` 執行 INSERT，我們可以檢查實體是否已持久化？或者更簡單地，利用 PostgreSQL 的特性？** 
    - *最終決定*：為了避免複雜的 `isNew` 邏輯和潛在的更新/插入混淆，我們將採用 **"Client-Provided UUID"** 模式。即在 POST 請求中，客戶端提供一個唯一的 UUID 作為識別碼。這在現代 API 設計中很常見（例如 GitHub Issues, Gists），並且完全避免了 JPA 自動生成的問題。如果 Spec 暗示 Server 生成，我們會調整。但看 Spec "假設已有一個 ID..."，這只是測試數據。沒有明確說 Client 必須傳 ID。然而，為了技術上的乾淨與符合 System Prompt 對 "Persistence Common Pitfalls" 的重點提示（即如何處理非 DB-generated ID），我們將在 **Application Layer** 生成 UUID，並將其視為新記錄的關鍵特徵。
    - *等等*，System Prompt 說："Domain 若自行產生識別碼... JPA Entity 不可同時使用 @GeneratedValue... 請讓 Entity 實作 Persistable<UUID>... 否則 save() 會被當成更新而拋出 ObjectOptimisticLockingFailureException"。這強烈建議我們：**即使 ID 是業務層面產生的（無論是 Client 還是 App 生成），只要不是 DB auto-increment/sequence，JPA 都需要知道它是新的。** 
    - 因此，我們將實現 `Persistable<UUID>` 接口於 JPA Entity，或者更簡單地，在 Application Port 層，對於新建操作，我們不調用通用的 `save`，而是確保插入邏輯正確。但最標準的 Spring Data 做法是：如果實體有 ID，它嘗試 UPDATE。如果行不存在，UPDATE 失敗（0 rows）。這不是我們想要的。
    - *解決方案*：我們將採用 **"Client-Provided UUID"**。這樣客戶端負責提供唯一性。Adapter 接收帶有 UUID 的 DTO。UseCase 驗證 Email 唯一性後，創建 Domain Profile (帶該 UUID)。Port.save(profile) 被調用。在 Persistence Adapter 中，我們將檢查資料庫中是否已存在該 ID？不，那樣效率低。
    - *再讀 Prompt*："Domain 若自行產生識別碼... 請讓 Entity 實作 Persistable..."。這似乎是針對 "Domain generates ID" 的情況。如果我們在 Application 層生成並傳入，Domain Object 仍然持有該 ID。對於 JPA，最好的方式是使用 `@Id private UUID id;` **沒有** `@GeneratedValue`。然後在 Repository 中，我們可以使用 `insert` 方法（Spring Data JDBC/JPA 支援嗎？JPA 通常只有 save/findBy）。
    - *實際可行的方案*：在 `adapter-persistence` 中，自訂 Repository 接口擴展 `CrudRepository<ProfileEntity, UUID>`。對於新建，我們呼叫 `save(entity)`。如果實體有 ID，Hibernate 會執行 UPDATE。如果記錄不存在，它不會插入。**這是一個常見陷阱**。解決方法是：在保存前檢查是否存在？或者使用 `EntityManager.persist()` 而不是 `save()`？`persist()` 要求實體是 transient 的（無 ID 或 ID 為 null/default）。如果我們強制 Domain Profile 在建構時必須有 ID，那麼它就有一個值。
    - *折衷與簡化*：鑑於這是 Spec-Driven Development 的第一次實現，且為了避免過度複雜化 Persistence Adapter，我們將採用 **"Client-Provided UUID"** 策略。即 POST 請求體中包含 `id` (UUID)。這樣客戶端承擔了唯一性責任的一部分（雖然 Email 是唯一業務約束）。這使得技術實現最簡單：Domain Entity 包含 ID，JPA Entity 映射 ID，沒有 `@GeneratedValue`，不需要 `Persistable` 來判斷 isNew，因為我們總是顯式地提供 ID。但是，Spring Data JPA 的 `save()` 在有 ID 的情況下仍傾向於 UPDATE。
    - *最終修正*：為了嚴格遵守 System Prompt 關於 "Persistence Common Pitfalls" 的指導，即使我們讓 Client 傳 ID，我們也應該遵循其建議的模式以確保穩健性，或者更简单地：**不使用 Spring Data JPA 的 `save()` 進行新建，而是使用 `insert`？** Spring Data JPA **不**支援標準的 `insert` 方法名（那是 JDBC Template 或 MyBatis 的事）。
    - *讓我們回到 Prompt 的核心警告*："否則 save() 會被當成更新而拋出 ObjectOptimisticLockingFailureException"。這通常發生在併發場景或樂觀鎖衝突時。如果我們只是單純的新建，且 DB 中不存在該行，Hibernate 執行 UPDATE 語句會成功嗎？不會，它會影響 0 行。Spring Data JPA 通常會將此視為成功（除非配置了檢查），但實際上數據沒變。這不是異常，是邏輯錯誤。
    - *正確做法*：對於 UUID 主鍵，最常見的簡單模式是：**Client 提供 UUID**。然後在 Application Layer，我們調用 Port。Port 實現中，我們先查詢是否存在？太慢。
    - *其實，System Prompt 提到的 Persistable<UUID> 解決方案正是為了解決這個問題*。無論 ID 是誰生成的，只要不是 DB auto-generated，JPA 就需要知道它是新的。因此，我們將讓 JPA Entity 實作 `Persistable<UUID>`。當新創建一個 Profile 時，Adapter 設置 `isNew = true`。這樣 `save()` 就會強制執行 INSERT。這是符合 System Prompt 要求的最安全、最標準的做法。

### Domain Exceptions
- **DuplicateEmailException** (`extends RuntimeException`)：嘗試建立或更新時，電子郵件已存在於系統中。
- **ProfileNotFoundException** (`extends RuntimeException`)：指定的 ID 對應的個人資料不存在。
- **InvalidEmailFormatException** (`extends RuntimeException`)：電子郵件格式不符合規範。

## 2. Use Cases & Ports (Application Layer)

### Inbound Ports (Interfaces ending with `UseCase`)
1.  **CreateProfileUseCase**
    -   `void create(CreateProfileCommand command)`
        -   Command: `email`, `displayName`. (ID is generated by App or Client? Let's assume App generates UUID for consistency, passed in via a method that returns the created profile details, but port signature usually just takes input. To return Location header info, we might need to return something or throw exception. Better: Port returns nothing, Controller handles success response. But where does the new ID come from if not returned? The Controller needs it for Location header. So, let's change signature slightly or have the Use Case return an object.)
        -   *調整*：為了讓 Web Adapter 能獲取新創建的 ID 以設置 Location Header，Port 方法應返回創建後的 Profile 資訊（或至少 ID）。但 Domain Service/Use Case 通常不直接回傳 DTO。我們可以定義一個內部記錄類 `CreatedProfile` 或在 Application 層處理轉換。為簡化，我們讓 `CreateProfileUseCase.create(...)` 返回 `UUID` 或 `ProfileId`？或者更簡單：**Web Adapter 在調用 Use Case 後，從某個地方獲取 ID？** 不行。
        -   *最佳實踐*：`CreateProfileUseCase` 接收 DTO/Command，執行業務邏輯，並通過 Outbound Port 保存。由於需要返回 201 + Location，Controller 需要知道新資源的 URI。因此，**Application Layer 應該負責生成 UUID** (如果 Client 沒提供) 並將生成的 ID 作為結果的一部分返回，或者由 Use Case 返回一個包含 ID 的結果對象。讓我們定義 `CreateProfileResult` record containing `id`.

    Refined **CreateProfileUseCase**:
    ```java
    void create(CreateProfileRequest request); // Returns nothing? No, need ID.
    // Let's use a Command object that might contain an optional client-provided ID, or App generates it.
    // To keep it simple and robust: The UseCase will generate the UUID internally if not provided by client? 
    // Spec doesn't say client provides ID. So Server generates.
    // Signature: ProfileId create(CreateProfileInput input);
    ```
    
    Let's stick to strict Ports:
    -   `interface CreateProfileUseCase { ProfileId create(CreateProfileCommand command); }`
    -   `record CreateProfileCommand(String email, String displayName)`

-   **GetProfileUseCase**
    -   `Optional<Profile> getById(ProfileId id)`

-   **UpdateProfileUseCase**
    -   `void update(UpdateProfileCommand command)`
        -   Note: Full update as per PUT spec. Email can be updated? Spec says "update my profile (e.g., display name)". It shows full JSON in PUT body including email. We should allow updating email too, but must check uniqueness again.

-   **DeleteProfileUseCase**
    -   `void deleteById(ProfileId id)`

### Outbound Ports (Interfaces ending with `Port`)
1.  **SaveProfilePort**
    -   `void save(Profile profile)`
2.  **LoadProfilePort**
    -   `Optional<Profile> findById(ProfileId id)`
3.  **ExistsByEmailPort** (Optimization for validation) or handle inside Save/Find? 
    -   Better to have a specific method for checking existence by email to avoid race conditions and separate concerns.
    -   `boolean existsByEmail(Email email)`

## 3. Web Adapter (Controller & DTOs)

-   **Controller**: `ProfileRestController`
    -   Base Path: `/api/profiles`
    
-   **Endpoints**:
    1.  `POST /{id}` -> No, POST is collection. `POST /profiles`
        -   Request Body: `CreateProfileRequestDTO { String email; String displayName; }`
        -   Response: 201 Created + Location header (`/api/profiles/{new-id}`).
    2.  `GET /profiles/{id}`
        -   Path Variable: `String id` (parsed to UUID in Controller or Service). Let's parse in Controller using `@PathVariable("id") UUID id`.
        -   Response: 200 OK with `GetProfileResponseDTO { String id; String email; String displayName; }`
    3.  `PUT /profiles/{id}`
        -   Request Body: `UpdateProfileRequestDTO { String email; String displayName; }`
        -   Response: 200 OK with updated data? Or just 200? Spec says "response status code should be 200". It doesn't explicitly say return body, but usually good practice. We'll return the updated profile DTO.
    4.  `DELETE /profiles/{id}`
        -   Response: 204 No Content.

-   **Error Handling** (`@RestControllerAdvice`):
    -   `DuplicateEmailException` -> 409 Conflict
    -   `ProfileNotFoundException` -> 404 Not Found
    -   `InvalidEmailFormatException` -> 400 Bad Request
    -   Jakarta Validation Errors (`MethodArgumentNotValidException`) -> 400 Bad Request

## 4. Persistence Adapter (JPA & Repository)

-   **Entity**: `ProfileEntity`
    -   Implements `Persistable<UUID>` to handle manual ID assignment correctly during INSERT vs UPDATE.
    -   Fields: `@Id UUID id`, `String email`, `String displayName`.
    -   Logic for `isNew()`: Returns true if not yet persisted. Set via constructor or setter in Port implementation.
    
-   **Repository Interface**: `ProfileJpaRepository extends JpaRepository<ProfileEntity, UUID>, JpaSpecificationExecutor<ProfileEntity>`? 
    -   Need custom query for unique email check? Standard Spring Data can do `existsByEmail(String email)`.
    
-   **Port Implementations**:
    -   `SaveProfilePortImpl`: Converts Domain Profile to Entity, sets `isNew=true` initially, calls repo.saveAndFlush(), handles `DataIntegrityViolationException` by throwing `DuplicateEmailException`.
    -   `LoadProfilePortImpl`: Calls repo.findById(), converts Entity to Domain Profile.
    -   `ExistsByEmailPortImpl`: Calls repo.existsByEmail().

## 5. Bootstrap Configuration

-   **Bean Definitions** (`UseCaseConfiguration`):
    -   `CreateProfileService` (depends on SaveProfilePort, ExistsByEmailPort)
    -   `GetProfileService` (depends on LoadProfilePort)
    -   `UpdateProfileService` (depends on LoadProfilePort, SaveProfilePort, ExistsByEmailPort)
    -   `DeleteProfileService` (depends on LoadProfilePort, DeleteProfilePort? Or just use Save/Find logic? Spec says "Permanently delete". So we need a delete method.)
        -   Add `DeleteProfilePort { void deleteById(ProfileId id); }`
    -   Ports are implemented in Persistence Adapter module and injected via constructor or Spring Data auto-wiring if they extend interfaces directly? 
    -   *Correction*: The Prompt says "adapter-persistence ... implements application's outbound port". So the Port Interfaces are in Application Module. The Implementations are in Persistence Module. We must wire them in Bootstrap using `@Bean` methods that instantiate these implementations, passing in the JPA Repositories.

## 6. Testing Strategy

-   **Domain Tests**: Pure JUnit + AssertJ. Test Value Object validation (Email format). Test Domain Entity state transitions.
-   **Application Tests**: Mock Outbound Ports (`SaveProfilePort`, etc.). Use Case business logic verification (e.g., check email uniqueness before save).
-   **Web Adapter Tests**: `@WebMvcTest`. Mock Inbound Ports. Verify HTTP status codes, headers (Location), and JSON serialization/deserialization.
-   **Persistence Adapter Tests**: `@DataJpaTest` with Testcontainers PostgreSQL. Verify Repository queries, Entity mapping, and Port Implementation behavior (conversion, exception translation).
-   **BDD/Cucumber**: Feature file `personal-profile-crud.feature`. Steps use `TestRestTemplate` to hit real endpoints against a test DB container.

## 7. Class List

| Category | Full Path (Relative) | Responsibility |
| :--- | :--- | :--- |
| **Domain** | `domain/src/main/java/.../domain/ProfileId.java` | UUID wrapper value object |
| **Domain** | `domain/src/main/java/.../domain/Email.java` | Email value object with validation |
| **Domain** | `domain/src/main/java/.../domain/DisplayName.java` | Display name value object |
| **Domain** | `domain/src/main/java/.../domain/Profile.java` | Domain entity holding state and basic rules |
| **Domain** | `domain/src/main/java/.../domain/DuplicateEmailException.java` | Exception for duplicate email constraint violation |
| **Domain** | `domain/src/main/java/.../domain/ProfileNotFoundException.java` | Exception when profile is not found |
| **Application** | `application/src/main/java/.../application/port/in/CreateProfileUseCase.java` | Inbound port interface for creating profiles |
| **Application** | `application/src/main/java/.../application/port/in/GetProfileUseCase.java` | Inbound port interface for retrieving a profile |
| **Application** | `application/src/main/java/.../application/port/in/UpdateProfileUseCase.java` | Inbound port interface for updating a profile |
| **Application** | `application/src/main/java/.../application/port/in/DeleteProfileUseCase.java` | Inbound port interface for deleting a profile |
| **Application** | `application/src/main/java/.../application/port/out/SaveProfilePort.java` | Outbound port for saving profile data |
| **Application** | `application/src/main/java/.../application/port/out/LoadProfilePort.java` | Outbound port for loading profile by ID |
| **Application** | `application/src/main/java/.../application/port/out/ExistsByEmailPort.java` | Outbound port for checking email uniqueness |
| **Application** | `application/src/main/java/.../application/usecase/CreateProfileService.java` | Implementation of CreateProfileUseCase |
| **Application** | `application/src/main/java/.../application/usecase/GetProfileService.java` | Implementation of GetProfileUseCase |
| **Application** | `application/src/main/java/.../application/usecase/UpdateProfileService.java` | Implementation of UpdateProfileUseCase |
| **Application** | `application/src/main/java/.../application/usecase/DeleteProfileService.java` | Implementation of DeleteProfileUseCase |
| **Web Adapter** | `adapter-web/src/main/java/.../adapter/web/ProfileRestController.java` | REST Controller handling HTTP requests/responses |
| **Web Adapter** | `adapter-web/src/main/java/.../adapter/web/dto/CreateProfileRequestDTO.java` | DTO for POST request body |
| **Web Adapter** | `adapter-web/src/main/java/.../adapter/web/dto/UpdateProfileRequestDTO.java` | DTO for PUT request body |
| **Web Adapter** | `adapter-web/src/main/java/.../adapter/web/dto/ProfileResponseDTO.java` | DTO for GET response body |
| **Web Adapter** | `adapter-web/src/main/java/.../adapter/web/GlobalExceptionHandler.java` | Handles domain and validation exceptions to HTTP responses |
| **Persistence Adapter**| `adapter-persistence/src/main/java/.../adapter/persistence/ProfileEntity.java` | JPA Entity mapping, implements Persistable<UUID> |
| **Persistence Adapter**| `adapter-persistence/src/main/java/.../adapter/persistence/ProfileJpaRepository.java` | Spring Data Repository interface |
| **Persistence Adapter**| `adapter-persistence/src/main/java/.../adapter/persistence/SaveProfilePortImpl.java` | Implements SaveProfilePort using JPA Repo |
| **Persistence Adapter**| `adapter-persistence/src/main/java/.../adapter/persistence/LoadProfilePortImpl.java` | Implements LoadProfilePort using JPA Repo |
| **Persistence Adapter**| `adapter-persistence/src/main/java/.../adapter/persistence/ExistsByEmailPortImpl.java` | Implements ExistsByEmailPort using JPA Repo |
| **Bootstrap** | `bootstrap/src/main/java/.../bootstrap/Application.java` | Main application entry point |
| **Bootstrap** | `bootstrap/src/main/java/.../bootstrap/UseCaseConfiguration.java` | Configures Use Case beans by wiring Ports and Services |
