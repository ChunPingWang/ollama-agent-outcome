# Spec 001 — 個人資料管理 (Personal Profile CRUD)

## 背景
系統需要為註冊用戶提供維護個人基本資訊的功能，包括建立新的個人資料記錄、查詢現有資料、更新個人資訊以及刪除不再需要的帳號資料。此功能是使用者帳戶管理的核心基礎。

## 名詞
- **個人資料 (Profile)**：代表單一使用者的基本資訊集合，包含唯一識別碼、電子郵件地址、顯示名稱與簡介。
- **電子郵件 (Email)**：用於識別使用者的唯一憑證，格式需符合標準 Email 規範。
- **顯示名稱 (Display Name)**：使用者希望公開顯示的名稱，允許空白或特殊字符，但長度有限制。
- **簡介 (Bio)**：關於使用者的簡短描述文字，可選填欄位。

## 使用者情境

### US-1 新增個人資料
身為一位新使用者，我想要建立我的個人資料，以便系統能夠識別我並儲存我的偏好設定。

### US-2 查詢個人資料
身為一位已登入的使用者，我想要根據 ID 查詢我的個人資料，以便確認目前的資料狀態。

### US-3 修改個人資料
身為一位使用者，我想要更新我的顯示名稱或簡介，以便保持資料的時效性。

### US-4 刪除個人資料
身為一位使用者，我想要永久刪除我的個人資料，以便行使被遺忘權或停止使用服務。

## 驗收條件

```gherkin
功能: 個人資料 CRUD 操作
    作為一名使用者
    我希望能夠管理我的個人資料
    
    場景: 成功建立新的個人資料
        當我發送 POST 請求至 "/api/profiles"，內容包含:
            """json
            {
              "email": "user@example.com",
              "displayName": "Test User",
              "bio": "Hello World"
            }
            """
        那麼回應的狀態碼應為 201 Created
        並且回應標頭 "Location" 應指向該資源的路徑
        
    場景: 建立失敗 - 電子郵件格式無效
        當我發送 POST 請求至 "/api/profiles"，內容包含:
            """json
            {
              "email": "invalid-email",
              "displayName": "Bad Email",
              "bio": ""
            }
            """
        那麼回應的狀態碼應為 400 Bad Request
        並且錯誤訊息應指出電子郵件格式不正確
        
    場景: 建立失敗 - 電子郵件已存在
        假設系統中已存在電子郵件為 "existing@test.com" 的資料
        當我發送 POST 請求至 "/api/profiles"，內容包含:
            """json
            {
              "email": "existing@test.com",
              "displayName": "Duplicate",
              "bio": ""
            }
            """
        那麼回應的狀態碼應為 409 Conflict
        並且錯誤訊息應指出電子郵件已被使用

    場景: 成功查詢個人資料
        假設系統中存在 ID 為 "550e8400-e29b-41d4-a716-446655440000" 的個人資料
        當我發送 GET 請求至 "/api/profiles/550e8400-e29b-41d4-a716-446655440000"
        那麼回應的狀態碼應為 200 OK
        並且回應內容中的 "id" 應為 "550e8400-e29b-41d4-a716-446655440000"
        並且回應內容中的 "email" 應與建立時一致
        
    場景: 查詢失敗 - 資料不存在
        當我發送 GET 請求至 "/api/profiles/non-existent-id"
        那麼回應的狀態碼應為 404 Not Found
        並且錯誤訊息應指出找不到該筆資料

    場景: 成功更新個人資料 (全量替換)
        假設系統中存在 ID 為 "550e8400-e29b-41d4-a716-446655440000" 且顯示名稱為 "Old Name" 的資料
        當我發送 PUT 請求至 "/api/profiles/550e8400-e29b-41d4-a716-446655440000"，內容包含:
            """json
            {
              "email": "user@example.com",
              "displayName": "New Name",
              "bio": "Updated Bio"
            }
            """
        那麼回應的狀態碼應為 200 OK
        並且再次查詢該資源時，其 "displayName" 應變更為 "New Name"
        
    場景: 更新失敗 - 資料不存在
        當我發送 PUT 請求至 "/api/profiles/non-existent-id"，內容包含:
            """json
            {
              "email": "test@test.com",
              "displayName": "Test",
              "bio": ""
            }
            """
        那麼回應的狀態碼應為 404 Not Found

    場景: 成功刪除個人資料
        假設系統中存在 ID 為 "550e8400-e29b-41d4-a716-446655440000" 的個人資料
        當我發送 DELETE 請求至 "/api/profiles/550e8400-e29b-41d4-a716-446655440000"
        那麼回應的狀態碼應為 204 No Content
        並且隨後發送 GET 請求查詢該 ID 時，應返回 404 Not Found
        
    場景: 刪除失敗 - 資料不存在
        當我發送 DELETE 請求至 "/api/profiles/non-existent-id"
        那麼回應的狀態碼應為 404 Not Found
```

## API 概要

| HTTP Method | Path                          | Success Status | Failure Status       | Description                  |
|-------------|-------------------------------|----------------|----------------------|------------------------------|
| POST        | `/api/profiles`               | 201 Created    | 400 Bad Request      | 建立新個人資料               |
|             |                               |                | 409 Conflict         | (電子郵件重複)               |
| GET         | `/api/profiles/{id}`          | 200 OK         | 404 Not Found        | 根據 ID 查詢個人資料         |
| PUT         | `/api/profiles/{id}`          | 200 OK         | 400 Bad Request      | 全量更新個人資料             |
|             |                               |                | 404 Not Found        | (資源不存在)                 |
| DELETE      | `/api/profiles/{id}`          | 204 No Content | 404 Not Found        | 刪除指定 ID 的個人資料       |

## 範圍外
- 使用者認證與授權（假設所有請求均來自已驗證的使用者，或本 Spec 僅關注資料結構本身）。
- 分頁、排序或搜尋功能。
- 檔案上傳或頭像處理。
- 歷史版本追蹤或軟刪除機制。

FEATURE_NAME: personal-profile-crud
