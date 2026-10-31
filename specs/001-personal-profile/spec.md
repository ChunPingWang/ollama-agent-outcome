# Spec 001 — 個人資料管理 (Personal Profile CRUD)

## 背景
系統需要為註冊用戶提供維護個人基本資訊的功能，包括建立新的個人資料記錄、查詢現有資料、更新個人資訊以及刪除不再需要的資料。這確保了數據的準確性與完整性，並符合隱私保護的基本要求。

## 名詞
- **個人資料 (Profile)**：代表單一用戶的基本資訊集合，包含唯一識別碼、電子郵件地址、顯示名稱和簡介。
- **電子郵件 (Email)**：用於標識用戶的唯一聯絡方式，格式需符合標準 Email 規範，且在系統中必須唯一。
- **顯示名稱 (Display Name)**：用戶希望對外展示的暱稱或真實姓名，允許為空或僅含空白字符（視為空值處理）。
- **簡介 (Bio)**：關於用戶的簡短介紹文字，可選填。

## 使用者情境

### US-1 新增個人資料
身為一名新用戶，我想要建立我的個人資料，以便在系統中擁有身份标识並開始使用服務。

### US-2 查詢個人資料
身為一名已登錄用戶，我想要根據 ID 查詢我的個人資料，以便確認當前儲存的資訊是否正確。

### US-3 修改個人資料
身為一名已登錄用戶，我想要更新我的個人資料（如暱稱或簡介），以便保持資訊的最新狀態。

### US-4 刪除個人資料
身為一名用戶，我想要刪除我的個人資料，以便徹底移除我在系統中的痕跡（例如銷戶流程的一部分）。

## 驗收條件

```gherkin
功能: 個人資料 CRUD 操作
    作為系統用戶
    我希望能夠對個人資料進行增刪改查
    以確保個人信息的準確性和安全性

場景: 成功建立新的個人資料
    當我發送 POST 請求至 "/api/profiles"，內容包含:
        """json
        {
          "email": "user@example.com",
          "displayName": "測試用戶",
          "bio": "這是一個測試簡介"
        }
        """
    那麼回應的狀態碼應該是 201 Created
    並且回應標頭 "Location" 應該存在且指向新建立的資源
    並且我可以透過該 Location 路徑 GET 到剛建立的資料

場景: 建立個人資料時電子郵件格式錯誤
    當我發送 POST 請求至 "/api/profiles"，內容包含:
        """json
        {
          "email": "invalid-email",
          "displayName": "測試用戶",
          "bio": "test"
        }
        """
    那麼回應的狀態碼應該是 400 Bad Request
    並且錯誤訊息應指出 Email 欄位格式不合法

場景: 建立個人資料時電子郵件已重複
    假設已經存在一個電子郵件為 "duplicate@test.com" 的個人資料
    當我發送 POST 請求至 "/api/profiles"，內容包含:
        """json
        {
          "email": "duplicate@test.com",
          "displayName": "另一個用戶",
          "bio": "test"
        }
        """
    那麼回應的狀態碼應該是 409 Conflict
    並且錯誤訊息應提示電子郵件已被使用

場景: 成功查詢存在的個人資料
    假設系統中已存在 ID 為 "123e4567-e89b-12d3-a456-426614174000" 的個人資料
    且其電子郵件為 "query@example.com"
    當我發送 GET 請求至 "/api/profiles/123e4567-e89b-12d3-a456-426614174000"
    那麼回應的狀態碼應該是 200 OK
    並且回應內容中的 email 欄位值應為 "query@example.com"

場景: 查詢不存在的個人資料
    當我發送 GET 請求至 "/api/profiles/non-existent-id"
    那麼回應的狀態碼應該是 404 Not Found
    並且錯誤訊息應提示找不到該筆資料

場景: 成功更新個人資料
    假設系統中已存在 ID 為 "update-id" 的個人資料，當前 displayName 為 "Old Name"
    當我發送 PUT 請求至 "/api/profiles/update-id"，內容包含:
        """json
        {
          "email": "old-email@test.com",
          "displayName": "New Name",
          "bio": "Updated Bio"
        }
        """
    那麼回應的狀態碼應該是 200 OK
    並且再次查詢該資源時，displayName 應變更為 "New Name"
    且 bio 應變更為 "Updated Bio"

場景: 嘗試更新不存在的個人資料
    當我發送 PUT 請求至 "/api/profiles/missing-id"，內容包含:
        """json
        {
          "email": "test@test.com",
          "displayName": "Name",
          "bio": ""
        }
        """
    那麼回應的狀態碼應該是 404 Not Found

場景: 成功刪除個人資料
    假設系統中已存在 ID 為 "delete-id" 的個人資料
    當我發送 DELETE 請求至 "/api/profiles/delete-id"
    那麼回應的狀態碼應該是 204 No Content
    並且隨後再 GET 該路徑時，會收到 404 Not Found

場景: 刪除不存在的個人資料
    當我發送 DELETE 請求至 "/api/profiles/non-existent-delete-id"
    那麼回應的狀態碼應該是 404 Not Found
```

## API 概要

| HTTP Method | Path                  | Description              | Success Status | Error Status       |
|-------------|-----------------------|--------------------------|----------------|--------------------|
| POST        | /api/profiles         | 建立新個人資料           | 201 Created    | 400, 409           |
| GET         | /api/profiles/{id}    | 查詢指定 ID 的資料       | 200 OK         | 404                |
| PUT         | /api/profiles/{id}    | 全量更新指定 ID 的資料   | 200 OK         | 400, 404           |
| DELETE      | /api/profiles/{id}    | 刪除指定 ID 的資料       | 204 No Content | 404                |

## 範圍外
- 用戶認證與授權（假設所有操作均由已驗證用戶發起，或本 Spec 僅關注數據層邏輯，權限控制由其他 Auth Service 處理）。
- 個人資料的分頁列表查詢。
- 檔案上傳（如頭像）。
- 歷史版本追蹤或軟刪除恢復功能。

FEATURE_NAME: personal-profile-crud
