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
身為一位新使用者，我想要建立我的個人資料以便在系統中擁有身份並開始使用服務。

### US-2 查詢個人資料
身為一位已登入的使用者，我想要根據 ID 查詢我的個人資料以確認當前狀態。

### US-3 更新個人資料
身為一位已登入的使用者，我想要修改我的顯示名稱或簡介以便保持資訊的準確性。

### US-4 刪除個人資料
身為一位使用者，我想要刪除我的個人資料以便徹底移除我在系統中的存在（例如銷戶）。

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
        
    場景: 建立資料時電子郵件格式錯誤
        當我發送 POST 請求至 "/api/profiles"，內容包含:
            """json
            {
              "email": "invalid-email",
              "displayName": "Bad Email",
              "bio": ""
            }
            """
        那麼回應的狀態碼應為 400 Bad Request
        並且錯誤訊息應指出電子郵件格式無效
        
    場景: 建立資料時顯示名稱過長
        當我發送 POST 請求至 "/api/profiles"，內容包含:
            """json
            {
              "email": "longname@example.com",
              "displayName": "This is a very long display name that exceeds the maximum allowed length limit for testing purposes",
              "bio": ""
            }
            """
        那麼回應的狀態碼應為 400 Bad Request
        並且錯誤訊息應指出顯示名稱長度超出限制
        
    場景: 查詢不存在的個人資料
        假設系統中沒有 ID 為 "non-existent-id" 的個人資料
        當我發送 GET 請求至 "/api/profiles/non-existent-id"
        那麼回應的狀態碼應為 404 Not Found

    場景: 成功更新個人資料
        假設已存在一個 ID 為 "profile-123"、電子郵件為 "old@test.com"、顯示名稱為 "Old Name" 的個人資料
        當我發送 PUT 請求至 "/api/profiles/profile-123"，內容包含:
            """json
            {
              "email": "new@test.com",
              "displayName": "New Name",
              "bio": "Updated Bio"
            }
            """
        那麼回應的狀態碼應為 200 OK
        並且該資源的顯示名稱應變更為 "New Name"
        
    場景: 嘗試刪除不存在的資料
        假設系統中沒有 ID 為 "missing-profile" 的個人資料
        當我發送 DELETE 請求至 "/api/profiles/missing-profile"
        那麼回應的狀態碼應為 404 Not Found
        
    場景: 成功刪除個人資料
        假設已存在一個 ID 為 "delete-me" 的個人資料
        當我發送 DELETE 請求至 "/api/profiles/delete-me"
        那麼回應的狀態碼應為 204 No Content
```

## API 概要

| HTTP Method | Path                  | Description          | Success Status | Failure Status      |
|-------------|-----------------------|----------------------|----------------|---------------------|
| POST        | /api/profiles         | Create new profile   | 201 Created    | 400 Bad Request     |
| GET         | /api/profiles/{id}    | Get profile by ID    | 200 OK         | 404 Not Found       |
| PUT         | /api/profiles/{id}    | Update full profile  | 200 OK         | 400, 404            |
| DELETE      | /api/profiles/{id}    | Delete profile       | 204 No Content | 404 Not Found       |

## 範圍外
- 密碼管理與驗證（此 Spec 僅處理公開資料）。
- 檔案上傳或頭像功能。
- 分頁查詢所有使用者列表（僅限單筆查詢）。
- 軟刪除邏輯（本需求為硬刪除，不可恢復）。

FEATURE_NAME: personal-profile-crud
