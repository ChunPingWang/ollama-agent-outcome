# Spec 001 — 個人資料管理 (Personal Profile CRUD)

## 背景
系統需要為註冊用戶提供維護個人基本資訊的功能，包括建立新的個人資料記錄、查詢現有資料、更新已存在的資訊以及刪除不再需要的帳號或資料。這確保了數據的準確性與用戶對自身資料的控制權。

## 名詞
- **個人資料 (Profile)**：代表單一用戶的基本資訊集合，包含唯一的識別碼、電子郵件地址、顯示名稱等核心屬性。
- **電子郵件 (Email)**：用於唯一標識用戶的身份憑證，格式需符合標準 Email 規範且在全域中必須唯一。
- **顯示名稱 (Display Name)**：用戶希望公開顯示的名稱，允許為空或僅含空白字符時視為未設定。

## 使用者情境

### US-1 新增個人資料
身為**新用戶**，我想要**建立我的個人資料**，以便**開始使用系統服務**。

### US-2 查詢個人資料
身為**已登入用戶**，我想要**根據 ID 查詢我的個人資料**，以便**確認當前儲存的資訊是否正確**。

### US-3 修改個人資料
身為**已登入用戶**，我想要**更新我的個人資料（如顯示名稱）**，以便**保持資訊的最新狀態**。

### US-4 刪除個人資料
身為**已登入用戶**，我想要**永久刪除我的個人資料**，以便**行使被遺忘權或停止使用服務**。

## 驗收條件

```gherkin
功能: 個人資料 CRUD 操作
    作為一名用戶
    我希望能夠管理我的個人資料
    
    場景: 成功建立新的個人資料
        當我發送 POST 請求至 "/api/profiles"，內容包含:
            """json
            {
              "email": "user@example.com",
              "displayName": "Test User"
            }
            """
        那麼回應的狀態碼應該是 201 (Created)
        並且回應標頭 "Location" 應該存在且指向該資源
        
    場景: 嘗試建立重複電子郵件的個人資料失敗
        假設已經存在一個電子郵件為 "duplicate@test.com" 的個人資料
        當我發送 POST 請求至 "/api/profiles"，內容包含:
            """json
            {
              "email": "duplicate@test.com",
              "displayName": "Another User"
            }
            """
        那麼回應的狀態碼應該是 409 (Conflict)
        
    場景: 嘗試建立無效格式電子郵件的個人資料失敗
        當我發送 POST 請求至 "/api/profiles"，內容包含:
            """json
            {
              "email": "invalid-email",
              "displayName": "Bad Format"
            }
            """
        那麼回應的狀態碼應該是 400 (Bad Request)

    場景: 成功查詢存在的個人資料
        假設已有一個 ID 為 "550e8400-e29b-41d4-a716-446655440000" 的個人資料
        當我發送 GET 請求至 "/api/profiles/550e8400-e29b-41d4-a716-446655440000"
        那麼回應的狀態碼應該是 200 (OK)
        並且回應內容中的 "id" 應該等於 "550e8400-e29b-41d4-a716-446655440000"
        並且回應內容中的 "email" 應該等於該資料對應的電子郵件
        
    場景: 查詢不存在的個人資料失敗
        當我發送 GET 請求至 "/api/profiles/non-existent-id"
        那麼回應的狀態碼應該是 404 (Not Found)

    場景: 成功更新個人資料
        假設已有一個 ID 為 "550e8400-e29b-41d4-a716-446655440000" 且顯示名稱為 "Old Name" 的個人資料
        當我發送 PUT 請求至 "/api/profiles/550e8400-e29b-41d4-a716-446655440000"，內容包含:
            """json
            {
              "displayName": "New Name",
              "email": "user@example.com"
            }
            """
        那麼回應的狀態碼應該是 200 (OK)
        
    場景: 嘗試更新不存在的個人資料失敗
        當我發送 PUT 請求至 "/api/profiles/non-existent-id"，內容包含:
            """json
            {
              "displayName": "Ghost User",
              "email": "ghost@test.com"
            }
            """
        那麼回應的狀態碼應該是 404 (Not Found)

    場景: 成功刪除個人資料
        假設已有一個 ID 為 "550e8400-e29b-41d4-a716-446655440000" 的個人資料
        當我發送 DELETE 請求至 "/api/profiles/550e8400-e29b-41d4-a716-446655440000"
        那麼回應的狀態碼應該是 204 (No Content)
        
    場景: 刪除後再次查詢該資料應返回不存在
        假設已刪除一個 ID 為 "550e8400-e29b-41d4-a716-446655440000" 的個人資料
        當我發送 GET 請求至 "/api/profiles/550e8400-e29b-41d4-a716-446655440000"
        那麼回應的狀態碼應該是 404 (Not Found)
```

## API 概要

| HTTP Method | Path                          | Description              | Success Status | Error Status       |
|-------------|-------------------------------|--------------------------|----------------|--------------------|
| POST        | `/api/profiles`               | 建立新個人資料           | 201 Created    | 400 Bad Request, 409 Conflict |
| GET         | `/api/profiles/{id}`          | 根據 ID 查詢個人資料     | 200 OK         | 404 Not Found      |
| PUT         | `/api/profiles/{id}`          | 全量更新個人資料         | 200 OK         | 400 Bad Request, 404 Not Found |
| DELETE      | `/api/profiles/{id}`          | 刪除指定個人資料         | 204 No Content | 404 Not Found      |

## 範圍外
- 用戶認證與授權機制（假設請求已通過基礎驗證）。
- 個人資料的複雜關聯資料（如地址、電話等多對多關係），僅包含核心基本欄位。
- 分頁或列表查詢所有個人資料的功能。
- 軟刪除邏輯，採用物理刪除。

FEATURE_NAME: personal-profile-crud
