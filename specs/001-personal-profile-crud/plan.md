# Plan 001 — 個人資料管理 (Personal Profile CRUD)

本計畫基於 Spec 001 設計，採用六角形架構（Hexagonal Architecture），確保業務邏輯獨立於框架與基礎設施。

## 領域模型 (Domain Model)

### Value Objects & Entities

- **ProfileId** (`record`)
    - 內容：`UUID id`
    - 不變條件：不可為 null。
    - 用途：作為 Domain Entity 的唯一識別符，並用於 JPA Entity 的主鍵映射。

- **Email** (`record`)
    - 內容：`String value`
    - 不變條件：符合 RFC 5322 基本格式（使用正則表達式驗證）、不為空白、全域唯一（由 Port 層保證）。
    - 用途：封裝電子郵件邏輯，避免在多個地方重複驗證字串。

- **Name** (`record`)
    - 內容：`String value`
    - 不變條件：長度介於 1 到 50 之間，不為空白或僅含空白字符。
    - 用途：封裝姓名邏輯。

- **Profile** (`class`, Immutable after creation except for updates via factory/methods)
    - 欄位：`ProfileId id`, `Name name`, `Email email`
    - 行為：
        - `create(Name, Email)`: Factory method 建立新實體。
        - `update(Name newName, Email newEmail)`: 返回新的 Profile 實例或更新內部狀態（視設計而定，此處採用不可變模式，返回新實例或修改後實例，考慮到 JPA 管理，Domain 層保持純淨，實際持久化由 Adapter 處理）。*註：為了配合 JPA 的受管實體特性，Domain Entity 通常會保留 setter 或由 Service 層調用方法來改變狀態，但本設計傾向於讓 Application Service 決定何時呼叫 update，並傳入新的 Value Objects。*
    - 不變條件：ID 一旦設定不可更改；Email 必須唯一（在 Domain 層無法檢查全域性，故僅做格式與非空驗證，唯一性由 DB Constraint + Port 層捕捉異常保證）。

### Domain Exceptions

- **DuplicateEmailException**: 當嘗試建立或更新已存在的電子郵件時拋出。
- **InvalidEmailException**: 當提供的電子郵件格式無效時拋出。
- **InvalidNameException**: 當姓名不符合規範時拋出。
- **ProfileNotFoundException**: 當查詢或刪除不存在的 ID 時拋出。

## Use Cases & Ports

### Inbound Ports (Interfaces ending with `UseCase`)

1.  **`CreateProfileUseCase`**
    -   `void execute(CreateProfileCommand command)`: 接收包含 name 和 email 的命令，執行建立邏輯。
2.  **`GetProfileUseCase`**
    -   `ProfileDto execute(GetProfileQuery query)`: 根據 ID 查詢資料，返回 DTO。
3.  **`UpdateProfileUseCase`**
    -   `void execute(UpdateProfileCommand command)`: 根據 ID 更新名稱與電子郵件。
4.  **`DeleteProfileUseCase`**
    -   `void execute(DeleteProfileCommand command)`: 根據 ID 刪除資料。

*註：為簡化傳輸，Application Layer 可定義簡單的 Command/Record 作為輸入參數，或直接使用 Value Objects。考慮到 Spring Boot 4 的 Jackson 3 支援 record 序列化，DTO 可直接用 record。但在 Application Port 層，為了保持純 Java，建議使用简单的 POJO 或 Record 來承載數據。*

### Outbound Ports (Interfaces ending with `Port`)

1.  **`SaveProfilePort`**
    -   `Profile save(Profile profile)`: 儲存或更新 Profile。需處理唯一性約束衝突並拋出 Domain Exception。
2.  **`LoadProfilePort`**
    -   `Optional<Profile> findById(ProfileId id)`: 依 ID 加載 Profile。
3.  **`ExistsByEmailPort`** (可選，若 Save 內部不檢查則不需要，但通常 DB 層檢查更高效或在 Save 時捕捉異常)
    -   *決策*：將唯一性檢查下放至 Persistence Adapter 的 `saveAndFlush` 捕捉 `DataIntegrityViolationException`，因此 Outbound Port 只需提供基本的 CRUD 操作即可。若需預先檢查以避免競爭條件，可增加此 Port，但為簡化，本設計依賴 DB Constraint + Transactional Atomicity。

## Web Adapter

-   **Controller**: `ProfileRestController` (`@RestController`, `@RequestMapping("/profiles")`)
-   **DTOs**:
    -   `CreateProfileRequest` (record): `String name`, `String email`. 使用 Jakarta Validation (`@NotBlank`, `@Email`).
    -   `UpdateProfileRequest` (record): `String name`, `String email`. 同上。
    -   `ProfileResponse` (record): `UUID id`, `String name`, `String email`.
    -   `ErrorResponse` (record): `String message`, `String code` (optional).
-   **Endpoints & Mappings**:
    -   `POST /`: 映射到 `CreateProfileUseCase.execute()`. 回傳 201 Created, Header Location.
    -   `GET /{id}`: 映射到 `GetProfileUseCase.execute()`. 回傳 200 OK 或 404 Not Found.
    -   `PUT /{id}`: 映射到 `UpdateProfileUseCase.execute()`. 回傳 200 OK 或 404/409.
    -   `DELETE /{id}`: 映射到 `DeleteProfileUseCase.execute()`. 回傳 204 No Content 或 404 Not Found.
-   **Error Handling**: `GlobalExceptionHandler` (`@RestControllerAdvice`)
    -   `MethodArgumentNotValidException` -> 400 Bad Request.
    -   `DuplicateEmailException` -> 409 Conflict.
    -   `ProfileNotFoundException` -> 404 Not Found.
    -   `InvalidNameException`, `InvalidEmailException` -> 400 Bad Request.

## Persistence Adapter

-   **JPA Entity**: `ProfileEntity`
    -   欄位：`UUID id` (Primary Key), `String name`, `String email`.
    -   約束：`email` column unique constraint.
    -   實作 `Persistable<UUID>` 以支援 Domain 產生的 UUID。
-   **Repository Interface**: `ProfileJpaRepository extends JpaRepository<ProfileEntity, UUID>, JpaSpecificationExecutor<ProfileEntity>`
    -   方法：`Optional<ProfileEntity> findById(UUID id)`, `boolean existsByEmail(String email)`, `void deleteById(UUID id)` 等。
-   **Port Implementation**: `JpaSaveProfilePort implements SaveProfilePort`, `JpaLoadProfilePort implements LoadProfilePort`
    -   負責將 `ProfileEntity` 與 Domain `Profile` 互相轉換（Converter）。
    -   `saveAndFlush` 捕捉 `DataIntegrityViolationException` 並轉為 `DuplicateEmailException`。
-   **Converter**: `ProfileConverter` (Static utility class or separate component in adapter-persistence)
    -   `toDomain(ProfileEntity entity)`
    -   `toEntity(Profile profile)`

## Bootstrap

-   **Configuration Class**: `UseCaseConfiguration` (`@Configuration`)
    -   `@Bean CreateProfileService`: Injects `CreateProfileUseCase`.
    -   `@Bean GetProfileService`: ...
    -   `@Bean UpdateProfileService`: ...
    -   `@Bean DeleteProfileService`: ...
    -   *注意*：所有 Service 實作類別需標記為 `@Component` 或在 Configuration 中明確定義 Bean，但根據規則，**use case bean 只能在 UseCaseConfiguration 以 @Bean 定義一次**。因此，Application 層的 Service 實現類不應加 `@Service`/`@Component`，而是由這裡注入 Port 介面並返回 Service 實例。
    -   Repository Beans: Spring Data JPA 會自動掃描並註冊 `adapter.persistence` 下的 Repository 接口。Port 的實作類（如 `JpaSaveProfilePort`）也需要作為 Bean 被創建，以便注入到 Service 中。這些也可以放在此配置類中，或者如果它們是簡單的無狀態轉換器+Repository調用，可以考慮讓 Spring 管理，但為了嚴格控制依賴，建議在此處顯式聲明或確保它們能被組件掃描找到（若不加註解則必須在此聲明）。*修正策略*：為了符合「application 層禁止 Spring annotation」且「bootstrap 負責組裝」，Adapter 層的 Port 實現類應加上 `@Component` 或由 Bootstrap 配置類提供 `@Bean`。鑑於 Adapter 層可能有多個，且在 `adapter-persistence` 模組，通常我們會在那裡做 `@EnableJpaRepositories` (自動) 和將 Port 實現類標記為 `@Component`。但是 System Prompt 說 "use case bean 只能在 UseCaseConfiguration 以 @Bean 定義一次"，這主要指 Application 層的 Inbound/Outbound 橋接點。讓我們釐清：
        -   Domain: Pure Java.
        -   Application: Interfaces + Implementations (Services). No Spring Annotations on Services? Rule says: "不要建立其他 @Configuration... use case bean 只能在 UseCaseConfiguration 以 @Bean 定義一次". This implies the Service implementations should be instantiated here or via component scanning if they are in a scanned package. However, to avoid ambiguity and strict control, it's safer to define them as Beans in `UseCaseConfiguration`. But wait, `adapter-persistence` is a separate module. If we put `@Component` on JPA Ports, they will be auto-scanned by Spring Boot. The rule about `UseCaseConfiguration` specifically targets **Application Layer** beans (Inbound/Outbound ports bridge). Let's assume standard practice: Repository interfaces are auto-configured. Adapter Port implementations can be `@Component` in their own module, OR defined in Bootstrap. Given the strictness, I will define the **Application Service** beans in `UseCaseConfiguration`, and let the **Adapter Port** beans be discovered via Component Scanning (since they reside in sub-packages of the main application package) or explicitly imported. To be safest against `BeanDefinitionOverrideException` and ensure clean separation, I will make sure only one configuration class exists for Use Cases.
        -   *Refined Strategy*: 
            1. Domain classes: Plain Java.
            2. Application Interfaces & Services: No annotations. Defined as `@Bean` in `bootstrap.UseCaseConfiguration`.
            3. Persistence Adapters (Ports): Since they depend on Spring Data Repositories, they must be managed by Spring. They can be annotated with `@Repository` or `@Service`? No, better to keep them simple. I'll add `@Component` to the Port Implementations in `adapter-persistence` so they are picked up automatically, assuming the base package is scanned. If not, we might need a specific import. However, usually `@SpringBootApplication` scans its own package and subpackages. `adapter.persistence` is a sibling/subpackage depending on structure. The prompt says `{base_package}.adapter.persistence`. So it IS under the base package. Thus, standard component scanning works.

## Testing Strategy

-   **Domain Tests**: JUnit + AssertJ. Test Value Object validation rules and Entity business logic invariants. No Spring context.
-   **Application Tests**: JUnit + Mockito. Mock Outbound Ports (`SaveProfilePort`, etc.). Verify that Service calls correct ports and handles exceptions correctly.
-   **Web Adapter Tests**: `@WebMvcTest(ProfileRestController.class)`. Mock Inbound Ports (`CreateProfileUseCase`, etc.). Use `MockMvcTester` for assertions. Verify HTTP status codes and JSON payloads.
-   **Persistence Adapter Tests**: `@DataJpaTest` + Testcontainers (PostgreSQL). Test Repository methods and Port implementations (`JpaSaveProfilePort`). Verify unique constraint enforcement and exception translation.
-   **BDD/Cucumber**: Feature file `src/test/resources/features/personal-profile-crud.feature`. Steps use `TestRestTemplate` to hit real endpoints against an embedded/Testcontainer DB.

## 類別清單

| 模組 | 完整路徑 (相對專案根目錄) | 職責描述 |
| :--- | :--- | :--- |
| Domain | `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/ProfileId.java` | 個人資料唯一識別碼值物件。 |
| Domain | `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/Email.java` | 電子郵件值物件，含格式驗證與不可變性。 |
| Domain | `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/Name.java` | 姓名值物件，含長度與空白檢查。 |
| Domain | `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/Profile.java` | 個人資料領域實體，包含業務規則與狀態管理。 |
| Domain | `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/DuplicateEmailException.java` | 電子郵件重複時拋出的領域例外。 |
| Domain | `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/InvalidEmailException.java` | 無效電子郵件格式時拋出的領域例外。 |
| Domain | `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/InvalidNameException.java` | 無效姓名格式時拋出的領域例外。 |
| Domain | `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/ProfileNotFoundException.java` | 找不到指定 ID 的個人資料時拋出的領域例外。 |
| Application | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/in/CreateProfileUseCase.java` | 新增個人資料的使用案例介面 (Inbound Port)。 |
| Application | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/in/GetProfileUseCase.java` | 查詢個人資料的使用案例介面 (Inbound Port)。 |
| Application | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/in/UpdateProfileUseCase.java` | 更新個人資料的使用案例介面 (Inbound Port)。 |
| Application | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/in/DeleteProfileUseCase.java` | 刪除個人資料的使用案例介面 (Inbound Port)。 |
| Application | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/out/SaveProfilePort.java` | 儲存個人資料的外向端口介面 (Outbound Port)。 |
| Application | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/out/LoadProfilePort.java` | 加載個人資料的外向端口介面 (Outbound Port)。 |
| Application | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/CreateProfileService.java` | 實現新增邏輯，驗證輸入並調用 SavePort。 |
| Application | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/GetProfileService.java` | 實現查詢邏輯，調用 LoadPort 並轉換 DTO。 |
| Application | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/UpdateProfileService.java` | 實現更新邏輯，檢查存在性、唯一性後調用 SavePort。 |
| Application | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/DeleteProfileService.java` | 實現刪除邏輯，先檢查存在性再調用 Delete (via SavePort or specific delete port if needed, but usually Load then logic). *Note: Spec doesn't have a dedicated DeletePort, so we might need one or reuse. Let's add `DeleteProfilePort` to Outbound Ports for clarity.* -> **Correction**: I will add `DeleteProfilePort` interface in application/port/out and its implementation in persistence adapter. |
| Web Adapter | `adapter-web/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/dto/CreateProfileRequest.java` | POST /profiles 的請求 DTO。 |
| Web Adapter | `adapter-web/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/dto/UpdateProfileRequest.java` | PUT /profiles/{id} 的請求 DTO。 |
| Web Adapter | `adapter-web/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/dto/ProfileResponse.java` | GET /profiles/{id} 的回應 DTO。 |
| Web Adapter | `adapter-web/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/dto/ErrorDetail.java` | 錯誤訊息結構化 DTO。 |
| Web Adapter | `adapter-web/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/ProfileController.java` | RESTful API 端點控制器，映射 HTTP 請求到 Use Cases。 |
| Web Adapter | `adapter-web/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/GlobalExceptionHandler.java` | 全域異常處理器，將 Domain Exception 轉為對應 HTTP 狀態碼。 |
| Persistence Adapter | `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/ProfileEntity.java` | JPA Entity，映射至資料庫表。 |
| Persistence Adapter | `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/ProfileJpaRepository.java` | Spring Data JPA Repository 介面。 |
| Persistence Adapter | `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/converter/ProfileConverter.java` | 負責 Profile (Domain) 與 ProfileEntity (JPA) 之間的轉換。 |
| Persistence Adapter | `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/JpaSaveProfilePort.java` | 實現 SaveProfilePort，處理持久化邏輯與唯一性衝突捕捉。 |
| Persistence Adapter | `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/JpaLoadProfilePort.java` | 實現 LoadProfilePort，從 DB 加載資料並轉換回 Domain。 |
| Persistence Adapter | `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/JpaDeleteProfilePort.java` | 實現 DeleteProfilePort，執行刪除操作。 *(Added to Outbound Ports)* |
| Bootstrap | `bootstrap/src/main/java/com/github/chunpingwang/ollamaagentoutcome/bootstrap/Application.java` | Spring Boot 啟動類。 |
| Bootstrap | `bootstrap/src/main/java/com/github/chunpingwang/ollamaagentoutcome/bootstrap/config/UseCaseConfiguration.java` | 組裝 Application Layer Use Case Services Bean 的配置類。 |
| BDD | `bootstrap/src/test/resources/features/personal-profile-crud.feature` | Cucumber Feature 檔，定義個人資料 CRUD 的驗收測試場景。 |
| BDD | `bootstrap/src/test/java/com/github/chunpingwang/ollamaagentoutcome/bdd/steps/PersonalProfileSteps.java` | Cucumber Step Definitions，使用 TestRestTemplate 呼叫 API。 |
