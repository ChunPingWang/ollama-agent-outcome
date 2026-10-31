# Tasks 001 — 個人資料管理 (Personal Profile CRUD)

## T1 建立領域模型與例外
- 說明：實作純 Java 領域物件與例外類別，包含 `ProfileId`、`Email`、`Profile` 以及相關的 Domain Exceptions。
  - `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/ProfileId.java`
  - `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/Email.java`
  - `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/Profile.java`
  - `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/ProfileNotFoundException.java`
  - `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/DuplicateEmailException.java`
  - `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/InvalidEmailException.java`
- 測試：
  - `domain/src/test/java/com/github/chunpingwang/ollamaagentoutcome/domain/ProfileIdTest.java`：驗證建構、不可變性、null/空白檢查。
  - `domain/src/test/java/com/github/chunpingwang/ollamaagentoutcome/domain/EmailTest.java`：驗證格式檢查、不可變性。
  - `domain/src/test/java/com/github/chunpingwang/ollamaagentoutcome/domain/ProfileTest.java`：驗證建構、`updateName` 行為、不可變性。

## T2 定義 Application Ports 與 Use Case 介面
- 說明：定義 inbound ports (Use Cases) 與 outbound ports (Repository Interfaces)。此階段僅定義介面，不含實作。
  - `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/in/CreateProfileUseCase.java`
  - `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/in/LoadProfileUseCase.java`
  - `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/in/UpdateProfileUseCase.java`
  - `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/in/DeleteProfileUseCase.java`
  - `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/out/SaveProfilePort.java`
  - `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/out/LoadProfilePort.java`
  - `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/out/DeleteProfilePort.java`
- 測試：無（僅介面定義，由後續任務實作後驗證）。

## T3 實作 Use Cases 與 Application 層測試
- 說明：實作所有 Use Case 服務類別，並撰寫單元測試。測試需使用 Mockito 模擬 outbound ports。
  - `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/CreateProfileService.java`
  - `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/LoadProfileService.java`
  - `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/UpdateProfileService.java`
  - `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/DeleteProfileService.java`
- 測試：
  - `application/src/test/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/CreateProfileServiceTest.java`：驗證成功建立、重複 Email 例外、無效 Email 例外。
  - `application/src/test/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/LoadProfileServiceTest.java`：驗證存在與不存在的情況。
  - `application/src/test/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/UpdateProfileServiceTest.java`：驗證成功更新、不存在例外、無效姓名例外。
  - `application/src/test/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/DeleteProfileServiceTest.java`：驗證成功刪除、不存在例外。

## T4 實作 Persistence Adapter 與資料庫測試
- 說明：實作 JPA Entity、Repository 以及 Outbound Ports 的 JPA 實作。使用 Testcontainers 進行整合測試。
  - `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/ProfileEntity.java`
  - `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/ProfileRepository.java`
  - `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/JpaSaveProfilePort.java`
  - `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/JpaLoadProfilePort.java`
  - `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/JpaDeleteProfilePort.java`
- 測試：
  - `adapter-persistence/src/test/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/JpaSaveProfilePortTest.java`：驗證儲存、重複 Email 檢查、轉換邏輯。
  - `adapter-persistence/src/test/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/JpaLoadProfilePortTest.java`：驗證查詢。
  - `adapter-persistence/src/test/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/JpaDeleteProfilePortTest.java`：驗證刪除。

## T5 組裝 Bootstrap 與驗證 Context 啟動
- 說明：在 `UseCaseConfiguration` 中組裝所有 Use Case 與 Port 實作為 Spring Beans，並驗證 Spring Context 可成功啟動。
  - `bootstrap/src/main/java/com/github/chunpingwang/ollamaagentoutcome/bootstrap/UseCaseConfiguration.java`
- 測試：
  - `bootstrap/src/test/java/com/github/chunpingwang/ollamaagentoutcome/bootstrap/UseCaseConfigurationTest.java`：使用 `@SpringBootTest` 與 `@Import(TestcontainersConfiguration.class)` 驗證 Context 啟動與 Bean 注入。

## T6 實作 Web Adapter 與 Controller 測試
- 說明：實作 REST Controller、DTOs 與 Error Handler。使用 `@WebMvcTest` 進行單元測試。
  - `adapter-web/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/ProfileController.java`
  - `adapter-web/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/ProfileControllerAdvice.java`
  - `adapter-web/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/CreateProfileRequest.java`
  - `adapter-web/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/UpdateProfileRequest.java`
  - `adapter-web/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/ProfileResponse.java`
- 測試：
  - `adapter-web/src/test/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/ProfileControllerTest.java`：驗證所有 CRUD 端點的 HTTP 狀態碼、請求/回應格式、錯誤處理。

## T7 實作 Cucumber BDD 驗收測試
- 說明：撰寫 Cucumber Feature 檔與步驟定義，進行端到端整合測試。
  - `bootstrap/src/test/resources/features/personal-profile-crud.feature`
  - `bootstrap/src/test/java/com/github/chunpingwang/ollamaagentoutcome/bdd/ProfileSteps.java`
- 測試：
  - `bootstrap/src/test/java/com/github/chunpingwang/ollamaagentoutcome/bdd/ProfileStepsTest.java`（或透過 Cucumber Runner）：驗證個人資料 CRUD 的完整業務流程。
