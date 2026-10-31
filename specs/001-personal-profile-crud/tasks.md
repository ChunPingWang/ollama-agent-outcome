# Tasks 001 — 個人資料管理 (CRUD)

## T1 領域模型與例外定義
- 說明：建立純 Java 領域物件，包含 Value Objects、Entity 與 Domain Exceptions。此層級不依賴任何框架。
  - `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/ProfileId.java`
  - `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/Email.java`
  - `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/Profile.java`
  - `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/ProfileNotFoundException.java`
  - `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/DuplicateEmailException.java`
  - `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/InvalidProfileException.java`
- 測試：
  - `domain/src/test/java/com/github/chunpingwang/ollamaagentoutcome/domain/ProfileDomainTest.java`：驗證 `Profile` 的建構不變條件（姓名長度、Email 非空）、`update` 行為回傳新實例、以及例外類別的繼承關係。

## T2 Application Ports 與 Commands
- 說明：定義 Application 層的 Inbound/Outbound Ports 介面與 Command 物件。此層級僅依賴 `domain`。
  - `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/in/CreateProfileUseCase.java`
  - `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/in/GetProfileUseCase.java`
  - `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/in/UpdateProfileUseCase.java`
  - `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/in/DeleteProfileUseCase.java`
  - `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/out/SaveProfilePort.java`
  - `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/out/LoadProfilePort.java`
  - `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/out/ExistsByEmailPort.java`
  - `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/out/DeleteProfilePort.java`
  - `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/CreateProfileCommand.java`
  - `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/GetProfileCommand.java`
  - `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/UpdateProfileCommand.java`
  - `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/DeleteProfileCommand.java`
- 測試：
  - 此階段主要為介面定義，可透過編譯檢查或簡單的 Interface 結構測試驗證（若無具體邏輯，可暫緩至實作 Service 時一併測試，或僅確保類別存在且套件正確）。

## T3 Use Case 實作 (Service Layer)
- 說明：實作 Use Case 邏輯，依賴 Ports 介面。此層級僅依賴 `domain` 與 `application` 介面。
  - `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/CreateProfileService.java`
  - `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/GetProfileService.java`
  - `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/UpdateProfileService.java`
  - `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/DeleteProfileService.java`
- 測試：
  - `application/src/test/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/CreateProfileServiceTest.java`：模擬 `ExistsByEmailPort` 與 `SaveProfilePort`，測試成功建立、重複 Email 拋出例外、無效資料拋出例外。
  - `application/src/test/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/GetProfileServiceTest.java`：模擬 `LoadProfilePort`，測試找到與找不到 (404) 情境。
  - `application/src/test/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/UpdateProfileServiceTest.java`：模擬 `LoadProfilePort`, `ExistsByEmailPort`, `SaveProfilePort`，測試成功更新、找不到原資料、新 Email 重複。
  - `application/src/test/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/DeleteProfileServiceTest.java`：模擬 `LoadProfilePort`, `DeleteProfilePort`，測試成功刪除與找不到。

## T4 Persistence Adapter (JPA Entity, Repository, Ports)
- 說明：實作資料持久化層，包含 JPA Entity、Spring Data Repository 以及 Application Ports 的實作類別。此層級依賴 `domain` 與 `adapter-persistence` 內部。
  - `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/ProfileEntity.java`
  - `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/ProfileRepository.java`
  - `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/ProfileConverter.java`
  - `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/JpaSaveProfilePort.java`
  - `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/JpaLoadProfilePort.java`
  - `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/JpaExistsByEmailPort.java`
  - `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/JpaDeleteProfilePort.java`
- 測試：
  - `adapter-persistence/src/test/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/ProfileRepositoryTest.java`：使用 `@DataJpaTest` + Testcontainers，測試 Entity 儲存、查詢、唯一性約束 (Unique Constraint)。
  - `adapter-persistence/src/test/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/ProfileConverterTest.java`：測試 Domain `Profile` 與 JPA `ProfileEntity` 之間的轉換邏輯。
  - `adapter-persistence/src/test/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/JpaSaveProfilePortTest.java`：測試 Port 實作是否正確呼叫 Repository 並處理轉換。

## T5 Web Adapter (Controller, DTOs, Exception Handling)
- 說明：實作 REST API 端點、DTO 轉換與全域例外處理。此層級依賴 `adapter-web` 內部與 `application` 的 Inbound Ports。
  - `adapter-web/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/CreateProfileRequest.java`
  - `adapter-web/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/UpdateProfileRequest.java`
  - `adapter-web/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/ProfileResponse.java`
  - `adapter-web/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/ProfileController.java`
  - `adapter-web/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/ProfileControllerAdvice.java`
- 測試：
  - `adapter-web/src/test/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/ProfileControllerTest.java`：使用 `@WebMvcTest`，模擬 Use Case Ports，測試各 HTTP 方法 (POST/GET/PUT/DELETE) 的狀態碼、Header (Location)、回應 Body 格式，以及例外轉換為正確的 HTTP 狀態碼 (400/404/409)。

## T6 Bootstrap 組裝與啟動
- 說明：組裝 Spring Boot 應用程式，將 Application 層的 Service 與 Persistence 層的 Port 實作組裝為 Beans。
  - `bootstrap/src/main/java/com/github/chunpingwang/ollamaagentoutcome/bootstrap/Application.java`
  - `bootstrap/src/main/java/com/github/chunpingwang/ollamaagentoutcome/bootstrap/ApplicationConfig.java`
- 測試：
  - `bootstrap/src/test/java/com/github/chunpingwang/ollamaagentoutcome/bootstrap/ApplicationTest.java`：簡單的 Smoke Test，驗證應用程式上下文能否正常啟動 (`@SpringBootTest`)。

## T7 Cucumber BDD 驗收測試
- 說明：撰寫使用者視角的行為驅動測試，透過 HTTP 呼叫實際 API 進行驗收。
  - `bootstrap/src/test/resources/features/profile-crud.feature`
  - `bootstrap/src/test/java/com/github/chunpingwang/ollamaagentoutcome/bdd/ProfileCrudSteps.java`
  - `bootstrap/src/test/java/com/github/chunpingwang/ollamaagentoutcome/bdd/ProfileCrudTest.java`
- 測試：
  - 執行 `ProfileCrudTest`，驗證所有 Gherkin 場景通過，涵蓋建立、查詢、更新、刪除及錯誤情境。
