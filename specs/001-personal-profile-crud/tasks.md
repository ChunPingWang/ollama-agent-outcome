# Tasks 001 — 個人資料管理 (CRUD)

## T1 領域模型與例外定義
- 說明：建立 Domain 層的 Value Object 與 Entity，以及相關的 Domain Exception。
  - `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/ProfileId.java`
  - `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/Email.java`
  - `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/Profile.java`
  - `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/ProfileNotFoundException.java`
  - `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/DuplicateEmailException.java`
  - `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/InvalidEmailException.java`
  - `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/InvalidNameException.java`
- 測試：
  - `domain/src/test/java/com/github/chunpingwang/ollamaagentoutcome/domain/ProfileIdTest.java`：驗證 UUID 不可為 null。
  - `domain/src/test/java/com/github/chunpingwang/ollamaagentoutcome/domain/EmailTest.java`：驗證電子郵件格式與不可變性。
  - `domain/src/test/java/com/github/chunpingwang/ollamaagentoutcome/domain/ProfileTest.java`：驗證建立、更新姓名、更新電子郵件的業務規則與不變條件。

## T2 應用層 Port 介面定義
- 說明：定義 Application 層的 Inbound Port (Use Case) 與 Outbound Port (Repository) 介面。
  - `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/in/CreateProfileUseCase.java`
  - `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/in/GetProfileUseCase.java`
  - `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/in/UpdateProfileUseCase.java`
  - `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/in/DeleteProfileUseCase.java`
  - `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/out/SaveProfilePort.java`
  - `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/out/FindProfileByIdPort.java`
  - `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/out/FindProfileByEmailPort.java`
- 測試：無（僅介面定義，由後續任務實作與測試）。

## T3 應用層 Use Case 實作與測試
- 說明：實作所有 Use Case Service，並撰寫單元測試。測試需模擬 Outbound Port。
  - `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/CreateProfileService.java`
  - `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/GetProfileService.java`
  - `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/UpdateProfileService.java`
  - `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/DeleteProfileService.java`
- 測試：
  - `application/src/test/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/CreateProfileServiceTest.java`：驗證成功建立、電子郵件重複例外。
  - `application/src/test/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/GetProfileServiceTest.java`：驗證成功查詢、找不到資料例外。
  - `application/src/test/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/UpdateProfileServiceTest.java`：驗證成功更新、找不到資料例外、電子郵件重複例外。
  - `application/src/test/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/DeleteProfileServiceTest.java`：驗證成功刪除、找不到資料例外。

## T4 持久化層實作與測試
- 說明：建立 JPA Entity、Repository、Port 實作與 Converter，並撰寫整合測試。
  - `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/ProfileEntity.java`
  - `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/ProfileRepository.java`
  - `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/ProfileConverter.java`
  - `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/JpaSaveProfilePort.java`
  - `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/JpaFindProfileByIdPort.java`
  - `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/JpaFindProfileByEmailPort.java`
- 測試：
  - `adapter-persistence/src/test/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/ProfileConverterTest.java`：驗證 Domain 與 Entity 的雙向轉換。
  - `adapter-persistence/src/test/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/JpaSaveProfilePortTest.java`：驗證儲存成功、唯一性約束觸發例外。
  - `adapter-persistence/src/test/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/JpaFindProfileByIdPortTest.java`：驗證依 ID 查詢。
  - `adapter-persistence/src/test/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/JpaFindProfileByEmailPortTest.java`：驗證依 Email 查詢。

## T5 Bootstrap 組裝與上下文驗證
- 說明：在 Bootstrap 模組組裝所有 Bean，並驗證 Spring Context 可正常啟動。
  - `bootstrap/src/main/java/com/github/chunpingwang/ollamaagentoutcome/bootstrap/UseCaseConfiguration.java`
- 測試：
  - `bootstrap/src/test/java/com/github/chunpingwang/ollamaagentoutcome/bootstrap/UseCaseConfigurationTest.java`：使用 `@SpringBootTest` 與 `@Import(TestcontainersConfiguration.class)` 驗證 Context 啟動成功，且所有 Use Case 與 Port Bean 可被注入。

## T6 Web Adapter 實作與測試
- 說明：建立 REST Controller、DTO、錯誤處理，並撰寫單元測試。
  - `adapter-web/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/CreateProfileRequest.java`
  - `adapter-web/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/UpdateProfileRequest.java`
  - `adapter-web/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/ProfileResponse.java`
  - `adapter-web/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/ProfileController.java`
  - `adapter-web/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/ProfileControllerAdvice.java`
- 測試：
  - `adapter-web/src/test/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/ProfileControllerTest.java`：使用 `@WebMvcTest` 與 `@MockitoBean` 模擬 Use Case，驗證 HTTP 狀態碼、請求驗證與錯誤回應。

## T7 Cucumber BDD 驗收測試
- 說明：撰寫 Cucumber Feature 檔與步驟定義，進行端到端整合測試。
  - `bootstrap/src/test/resources/features/profile-crud.feature`
  - `bootstrap/src/test/java/com/github/chunpingwang/ollamaagentoutcome/bdd/ProfileSteps.java`
- 測試：
  - `bootstrap/src/test/java/com/github/chunpingwang/ollamaagentoutcome/bdd/ProfileSteps.java`：使用 `@SpringBootTest` 與 `@AutoConfigureTestRestTemplate` 呼叫真實 API，驗證 CRUD 所有場景。
