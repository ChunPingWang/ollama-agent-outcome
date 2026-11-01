# Spec 001 — 個人資料管理 (Personal Profile CRUD)

## 背景
系統需要為註冊用戶提供維護個人基本資訊的功能，包括建立新的個人資料記錄、查詢現有資料、更新已存在的資訊以及刪除不再需要的帳號或資料。此功能是使用者中心的核心模組，確保資料的完整性與一致性。

## 名詞
- **個人資料 (Profile)**：代表單一使用者的基本資訊集合，包含唯一識別碼、電子郵件地址、顯示名稱等欄位。
- **電子郵件 (Email)**：用於識別使用者的唯一憑證，格式需符合標準 Email 規範，且在全域中必須唯一。
- **顯示名稱 (Display Name)**：使用者希望對外展示的暱稱或真實姓名，允許空白但不得僅含空白字符。

## 使用者情境

### US-1 新增個人資料
身為**新使用者**，我想要**建立我的個人資料**，以便**後續使用系統功能**。

### US-2 查詢個人資料
身為**已登入的使用者**，我想要**根據 ID 查詢我的個人資料**，以便**確認我的當前設定**。

### US-3 修改個人資料
身為**擁有帳戶的使用者**，我想要**更新我的個人資料（如顯示名稱）**，以便**保持資訊的最新狀態**。

### US-4 刪除個人資料
身為**決定退出服務的使用者**，我想要**永久刪除我的個人資料**，以便**停止所有相關服務並保護隱私**。

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
              "displayName": "測試用戶"
            }
            """
        那麼回應的狀態碼應為 201 Created
        並且回應標頭 "Location" 應存在且指向該資源
        
    場景: 嘗試建立重複電子郵件的個人資料失敗
        假設已存在電子郵件為 "duplicate@test.com" 的個人資料
        當我發送 POST 請求至 "/api/profiles"，內容包含:
            """json
            {
              "email": "duplicate@test.com",
              "displayName": "另一個用戶"
            }
            """
        那麼回應的狀態碼應為 409 Conflict
        並且錯誤訊息指出電子郵件已存在
        
    場景: 嘗試使用無效格式電子郵件建立資料失敗
        當我發送 POST 請求至 "/api/profiles"，內容包含:
            """json
            {
              "email": "invalid-email",
              "displayName": "壞數據"
            }
            """
        那麼回應的狀態碼應為 400 Bad Request
        並且驗證錯誤指出 Email 格式不正確

    場景: 成功查詢存在的個人資料
        假設系統中存在 ID 為 "550e8400-e29b-41d4-a716-446655440000" 的個人資料
        且其電子郵件為 "query@example.com"，顯示名稱為 "查詢者"
        當我發送 GET 請求至 "/api/profiles/550e8400-e29b-41d4-a716-446655440000"
        那麼回應的狀態碼應為 200 OK
        並且回應主體中的 email 應為 "query@example.com"
        並且回應主體中的 displayName 應為 "查詢者"

    場景: 查詢不存在的個人資料失敗
        當我發送 GET 請求至 "/api/profiles/non-existent-id"
        那麼回應的狀態碼應為 404 Not Found
        
    場景: 成功更新個人資料
        假設系統中存在 ID 為 "update-id" 的個人資料
        且目前顯示名稱為 "舊名稱"
        當我發送 PUT 請求至 "/api/profiles/update-id"，內容包含:
            """json
            {
              "email": "updated@test.com",
              "displayName": "新名稱"
            }
            """
        那麼回應的狀態碼應為 200 OK
        並且再次查詢該資源時，displayName 應變更為 "新名稱"
        
    場景: 嘗試更新不存在的資料失敗
        當我發送 PUT 請求至 "/api/profiles/missing-id"，內容包含:
            """json
            {
              "email": "test@test.com",
              "displayName": "測試"
            }
            """
        那麼回應的狀態碼應為 404 Not Found

    場景: 成功刪除個人資料
        假設系統中存在 ID 為 "delete-id" 的個人資料
        當我發送 DELETE 請求至 "/api/profiles/delete-id"
        那麼回應的狀態碼應為 204 No Content
        
    場景: 確認刪除後無法再查詢
        假設已執行過刪除操作於 ID 為 "deleted-id" 的資料
        當我發送 GET 請求至 "/api/profiles/deleted-id"
        那麼回應的狀態碼應為 404 Not Found
```

## API 概要

| HTTP Method | Path                  | Description          | Success Status | Error Status       |
|-------------|-----------------------|----------------------|----------------|--------------------|
| POST        | /api/profiles         | 建立新個人資料       | 201 Created    | 400, 409           |
| GET         | /api/profiles/{id}    | 查詢單一個人資料     | 200 OK         | 404                |
| PUT         | /api/profiles/{id}    | 更新完整個人資料     | 200 OK         | 400, 404, 409      |
| DELETE      | /api/profiles/{id}    | 刪除個人資料         | 204 No Content | 404                |

## 範圍外
- 密碼管理與驗證（僅限基本資訊 CRUD）。
- 檔案上傳或頭像處理。
- 分頁、搜尋或過濾列表功能（本 Spec 僅涵蓋單筆操作）。
- 軟刪除機制（採用硬刪除）。

FEATURE_NAME: personal-profile-crud
