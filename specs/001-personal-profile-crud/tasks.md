# Tasks 001 — 個人資料管理 (Personal Profile CRUD)

## T1 領域模型與例外定義
- 說明：建立領域層的核心值物件、實體與例外類別。這些類別不依賴任何外部框架，僅包含業務規則與不變條件。
  - `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/ProfileId.java`
  - `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/Email.java`
  - `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/Name.java`
  - `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/Bio.java`
  - `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/Profile.java`
  - `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/ProfileNotFoundException.java`
  - `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/DuplicateEmailException.java`
  - `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/InvalidNameException.java`
- 測試：
  - `domain/src/test/java/com/github/chunpingwang/ollamaagentoutcome/domain/ProfileTest.java`：驗證 `Profile` 的建構、`update` 行為、值物件的不可變性與不變條件（如 Email 格式、Name 非空）。

## T2 應用層 Port 介面與 Use Case 實作
- 說明：定義應用層的 Inbound/Outbound Port 介面，並實作四個核心 Use Case Service。此階段僅處理業務邏輯流程，不依賴資料庫或 HTTP。
  - `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/in/CreateProfileUseCase.java`
  - `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/in/GetProfileUseCase.java`
  - `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/in/UpdateProfileUseCase.java`
  - `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/in/DeleteProfileUseCase.java`
  - `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/out/SaveProfilePort.java`
  - `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/out/LoadProfilePort.java`
  - `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/out/DeleteProfilePort.java`
  - `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/CreateProfileService.java`
  - `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/GetProfileService.java`
  - `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/UpdateProfileService.java`
  - `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/DeleteProfileService.java`
- 測試：
  - `application/src/test/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/CreateProfileServiceTest.java`：模擬 `SaveProfilePort`，驗證建立成功與重複 Email 例外。
  - `application/src/test/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/GetProfileServiceTest.java`：模擬 `LoadProfilePort`，驗證查詢成功與找不到資料例外。
  - `application/src/test/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/UpdateProfileServiceTest.java`：模擬 `LoadProfilePort` 與 `SaveProfilePort`，驗證更新邏輯與不存在資料例外。
  - `application/src/test/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/DeleteProfileServiceTest.java`：模擬 `LoadProfilePort` 與 `DeleteProfilePort`，驗證刪除邏輯與不存在資料例外。

## T3 持久化層實作 (JPA Entity, Repository, Port Implementations, Converter)
- 說明：建立 JPA Entity、Spring Data Repository、Port 介面實作以及 Domain 與 JPA 之間的轉換器。此階段需處理資料庫映射與事務邊界。
  - `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/ProfileEntity.java`
  - `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/ProfileRepository.java`
  - `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/ProfileConverter.java`
  - `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/JpaSaveProfilePort.java`
  - `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/JpaLoadProfilePort.java`
  - `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/JpaDeleteProfilePort.java`
- 測試：
  - `adapter-persistence/src/test/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/ProfileEntityTest.java`：驗證 JPA 映射與 `Persistable` 行為。
  - `adapter-persistence/src/test/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/ProfileConverterTest.java`：純 Java 測試，驗證 Domain 與 Entity 的雙向轉換。
  - `adapter-persistence/src/test/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/JpaSaveProfilePortTest.java`：使用 Testcontainers PostgreSQL，驗證 `saveAndFlush` 與唯一性約束觸發 `DuplicateEmailException`。
  - `adapter-persistence/src/test/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/JpaLoadProfilePortTest.java`：使用 Testcontainers PostgreSQL，驗證 `findById` 查詢邏輯。
  - `adapter-persistence/src/test/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/JpaDeleteProfilePortTest.java`：使用 Testcontainers PostgreSQL，驗證 `deleteById` 邏輯。

## T4 Bootstrap 組裝與上下文驗證
- 說明：建立 Spring Boot 組態類別，將 Application 層的 Use Case 與 Persistence 層的 Port 實作綁定為 Bean。此任務確保所有依賴可被 Spring 容器正確注入，並驗證應用程式上下文可成功啟動。
  - `bootstrap/src/main/java/com/github/chunpingwang/ollamaagentoutcome/bootstrap/UseCaseConfiguration.java`
- 測試：
  - `bootstrap/src/test/java/com/github/chunpingwang/ollamaagentoutcome/bootstrap/UseCaseConfigurationTest.java`：使用 `@SpringBootTest` 與 `@Import(TestcontainersConfiguration.class)` 驗證整個應用程式上下文（包含資料庫連線）可正常啟動，且所有 Bean 可被解析。

## T5 Web Adapter (Controller, DTO, Exception Handler)
- 說明：建立 REST Controller、Request/Response DTO 以及全域錯誤處理 Advice。此階段將 HTTP 請求映射到 Use Case，並處理驗證與例外轉換。
  - `adapter-web/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/ProfileController.java`
  - `adapter-web/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/CreateProfileRequest.java`
  - `adapter-web/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/UpdateProfileRequest.java`
  - `adapter-web/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/ProfileResponse.java`
  - `adapter-web/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/ProfileControllerAdvice.java`
- 測試：
  - `adapter-web/src/test/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/ProfileControllerTest.java`：使用 `@WebMvcTest` 與 `@MockitoBean` 模擬 Use Case，驗證 HTTP 狀態碼（201, 200, 204, 400, 404, 409）與回應內容。

## T6 Cucumber BDD 驗收測試
- 說明：建立端到端的行為驅動測試，透過 HTTP 呼叫實際 API（包含資料庫與組裝好的 Spring Context），驗證完整的業務流程。
  - `bootstrap/src/test/resources/features/personal-profile-crud.feature`
  - `bootstrap/src/test/java/com/github/chunpingwang/ollamaagentoutcome/bdd/ProfileSteps.java`
- 測試：
  - `bootstrap/src/test/java/com/github/chunpingwang/ollamaagentoutcome/bdd/ProfileSteps.java`：使用 `TestRestTemplate` 與 Testcontainers PostgreSQL，執行 Gherkin 步驟，驗證 CRUD 操作的完整行為與邊界條件。
