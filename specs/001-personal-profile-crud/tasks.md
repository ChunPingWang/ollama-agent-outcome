# Tasks 001 — 個人資料管理 (CRUD)

## T1 領域模型與值物件驗證
- 說明：建立 Domain 層的純 Java 類別，定義值物件與實體，並實作不變條件驗證。
  - `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/ProfileId.java`
  - `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/Email.java`
  - `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/Name.java`
  - `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/Age.java`
  - `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/Profile.java`
  - `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/ProfileNotFoundException.java`
  - `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/DuplicateEmailException.java`
  - `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/InvalidEmailException.java`
  - `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/InvalidNameException.java`
  - `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/InvalidAgeException.java`
- 測試：
  - `domain/src/test/java/com/github/chunpingwang/ollamaagentoutcome/domain/ProfileIdTest.java`：驗證 UUID 不可為 null。
  - `domain/src/test/java/com/github/chunpingwang/ollamaagentoutcome/domain/EmailTest.java`：驗證有效/無效格式、null/空白檢查。
  - `domain/src/test/java/com/github/chunpingwang/ollamaagentoutcome/domain/NameTest.java`：驗證非空、非空白檢查。
  - `domain/src/test/java/com/github/chunpingwang/ollamaagentoutcome/domain/AgeTest.java`：驗證正整數 (>0) 檢查。
  - `domain/src/test/java/com/github/chunpingwang/ollamaagentoutcome/domain/ProfileTest.java`：驗證 Profile 建立與 `update` 方法返回新實例的行為。

## T2 Application 層 Port 介面與 Use Case 實作
- 說明：建立 Application 層的 Inbound/Outbound Port 介面，以及 Use Case 的 Service 實作。此階段僅依賴 Domain，使用 Mockito 模擬 Outbound Port。
  - `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/in/CreateProfileUseCase.java`
  - `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/in/GetProfileUseCase.java`
  - `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/in/UpdateProfileUseCase.java`
  - `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/in/DeleteProfileUseCase.java`
  - `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/out/SaveProfilePort.java`
  - `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/out/FindProfileByIdPort.java`
  - `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/out/FindProfileByEmailPort.java`
  - `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/out/DeleteProfileByIdPort.java`
  - `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/CreateProfileService.java`
  - `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/GetProfileService.java`
  - `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/UpdateProfileService.java`
  - `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/DeleteProfileService.java`
- 測試：
  - `application/src/test/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/CreateProfileServiceTest.java`：驗證成功建立、重複 Email 拋出例外、Domain 驗證失敗拋出例外。
  - `application/src/test/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/GetProfileServiceTest.java`：驗證找到資料回傳 Profile，找不到拋出 `ProfileNotFoundException`。
  - `application/src/test/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/UpdateProfileServiceTest.java`：驗證更新成功、資料不存在拋出例外、重複 Email 拋出例外。
  - `application/src/test/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/DeleteProfileServiceTest.java`：驗證刪除成功、資料不存在拋出例外。

## T3 Persistence 層 JPA Entity、Repository 與 Port 實作
- 說明：建立 Persistence 層的 JPA Entity、Spring Data Repository，以及實作 Application 層的 Outbound Port。此階段需處理 Domain 與 JPA Entity 的轉換。
  - `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/ProfileEntity.java`
  - `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/ProfileRepository.java`
  - `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/ProfileEntityConverter.java`
  - `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/JpaSaveProfilePort.java`
  - `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/JpaFindProfileByIdPort.java`
  - `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/JpaFindProfileByEmailPort.java`
  - `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/JpaDeleteProfileByIdPort.java`
- 測試：
  - `adapter-persistence/src/test/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/ProfileEntityTest.java`：驗證 Entity 的 `isNew` 狀態管理、`@PostLoad`/`@PostPersist` 行為。
  - `adapter-persistence/src/test/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/ProfileRepositoryTest.java`：使用 `@DataJpaTest` + Testcontainers，驗證 CRUD 操作、唯一性約束 (`DataIntegrityViolationException`)。
  - `adapter-persistence/src/test/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/ProfileEntityConverterTest.java`：驗證 Domain `Profile` 與 JPA `ProfileEntity` 的雙向轉換正確性。
  - `adapter-persistence/src/test/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/JpaSaveProfilePortTest.java`：驗證 Port 實作呼叫 Repository 並正確轉換回 Domain 物件。
  - `adapter-persistence/src/test/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/JpaFindProfileByIdPortTest.java`：驗證依 ID 查詢邏輯。
  - `adapter-persistence/src/test/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/JpaFindProfileByEmailPortTest.java`：驗證依 Email 查詢邏輯。
  - `adapter-persistence/src/test/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/JpaDeleteProfileByIdPortTest.java`：驗證刪除邏輯。

## T4 Web Adapter Controller、DTO 與錯誤處理
- 說明：建立 Web 層的 Controller、Request/Response DTO 以及全域異常處理器。此階段使用 `@WebMvcTest` 進行單元測試，模擬 Use Case Port。
  - `adapter-web/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/ProfileController.java`
  - `adapter-web/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/CreateProfileRequest.java`
  - `adapter-web/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/UpdateProfileRequest.java`
  - `adapter-web/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/ProfileResponse.java`
  - `adapter-web/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/ProfileExceptionHandler.java`
- 測試：
  - `adapter-web/src/test/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/ProfileControllerTest.java`：
    - 驗證 `POST /profiles` 成功回 201 與 Location header。
    - 驗證 `GET /profiles/{id}` 成功回 200。
    - 驗證 `PUT /profiles/{id}` 成功回 200。
    - 驗證 `DELETE /profiles/{id}` 成功回 204。
    - 驗證 DTO 驗證失敗 (400)。
    - 驗證 Use Case 拋出 `ProfileNotFoundException` 回 404。
    - 驗證 Use Case 拋出 `DuplicateEmailException` 回 409。
    - 驗證 Use Case 拋出其他 Domain 例外回 400。

## T5 Bootstrap 組態與 Cucumber 驗收測試
- 說明：建立 Bootstrap 層的 Spring Configuration 組裝 Bean，並撰寫 Cucumber Feature 檔與步驟定義，進行端到端整合測試。
  - `bootstrap/src/main/java/com/github/chunpingwang/ollamaagentoutcome/bootstrap/ProfileConfiguration.java`
  - `bootstrap/src/test/resources/features/profile-crud.feature`
  - `bootstrap/src/test/java/com/github/chunpingwang/ollamaagentoutcome/bdd/ProfileStepDefinitions.java`
- 測試：
  - `bootstrap/src/test/java/com/github/chunpingwang/ollamaagentoutcome/bdd/ProfileStepDefinitions.java`：透過 `TestRestTemplate` 呼叫實際 API，驗證所有 Cucumber 場景（成功 CRUD、各種錯誤情境）通過。
