# Spec 001 — 個人資料管理 (Personal Profile CRUD)

## 背景
系統需要為註冊用戶提供個人基本資訊的管理能力，包括建立新的個人資料、查詢現有資料、更新既有內容以及刪除不再需要的記錄。這確保了數據的準確性與完整性，並符合隱私保護的基本需求。

## 名詞
- **個人資料 (Profile)**：代表單一用戶的核心身份資訊集合，包含唯一識別碼、姓名、電子郵件地址等屬性。
- **電子郵件 (Email)**：用於標識用戶的唯一聯絡方式，格式需符合標準 RFC 規範，且在系統中必須保持唯一性。
- **識別碼 (ID)**：系統內部生成的全局唯一標識符，通常採用 UUID 格式，不可由使用者自訂或修改。

## 使用者情境

### US-1 新增個人資料
身為**新用戶**，我想要**提交我的基本資訊來建立個人資料**，以便**後續使用系統服務**。

### US-2 查詢個人資料
身為**已登入用戶**，我想要**透過 ID 查看我的詳細資料**，以便**確認當前儲存的資訊是否正確**。

### US-3 更新個人資料
身為**已登入用戶**，我想要**修改我的部分或全部基本資訊**，以便**反映最新的生活狀態或聯絡方式**。

### US-4 刪除個人資料
身為**希望退出服務的用戶**，我想要**永久移除我的個人資料**，以便**徹底銷毀我的數位足跡**。

## 驗收條件

```gherkin
功能: 個人資料 CRUD 操作

場景: 成功建立新的個人資料
    當我發送 POST 請求至 "/api/profiles"，內容包含:
        """json
        {
          "name": "王小明",
          "email": "xiaoming@example.com"
        }
        """
    那麼回應的狀態碼應該是 201 (Created)
    並且回應標頭 "Location" 應指向剛建立的資源路徑
    並且回應主體中的 "id" 欄位不應為空
    並且回應主體中的 "name" 應為 "王小明"
    並且回應主體中的 "email" 應為 "xiaoming@example.com"

場景: 嘗試建立重複電子郵件的資料時失敗
    假設系統中已存在電子郵件為 "duplicate@test.com" 的個人資料
    當我發送 POST 請求至 "/api/profiles"，內容包含:
        """json
        {
          "name": "李大明",
          "email": "duplicate@test.com"
        }
        """
    那麼回應的狀態碼應該是 409 (Conflict)
    並且錯誤訊息應指出電子郵件已被佔用

場景: 查詢不存在的個人資料
    當我發送 GET 請求至 "/api/profiles/550e8400-e29b-41d4-a716-446655440000"
    那麼回應的狀態碼應該是 404 (Not Found)
    並且錯誤訊息應說明該識別碼找不到對應資料

場景: 成功更新現有個人資料
    假設系統中存在 ID 為 "existing-id" 且姓名為 "舊名字"、Email 為 "old@test.com" 的資料
    當我發送 PUT 請求至 "/api/profiles/existing-id"，內容包含:
        """json
        {
          "name": "新名字",
          "email": "new@test.com"
        }
        """
    那麼回應的狀態碼應該是 200 (OK)
    並且回應主體中的 "name" 應為 "新名字"
    並且回應主體中的 "email" 應為 "new@test.com"

場景: 嘗試更新無效格式的電子郵件時失敗
    當我發送 POST 請求至 "/api/profiles"，內容包含:
        """json
        {
          "name": "測試用戶",
          "email": "invalid-email-format"
        }
        """
    那麼回應的狀態碼應該是 400 (Bad Request)
    並且錯誤訊息應指出 Email 格式不合法

場景: 成功刪除個人資料
    假設系統中存在 ID 為 "delete-me-id" 的個人資料
    當我發送 DELETE 請求至 "/api/profiles/delete-me-id"
    那麼回應的狀態碼應該是 204 (No Content)
    且隨後再次查詢該 ID 時應返回 404 Not Found
```

## API 概要

| HTTP 方法 | 路徑 | 說明 | 成功狀態碼 | 常見失敗狀態碼 |
| :--- | :--- | :--- | :--- | :--- |
| `POST` | `/api/profiles` | 建立新的個人資料 | 201 Created | 400 Bad Request, 409 Conflict |
| `GET` | `/api/profiles/{id}` | 根據 ID 查詢資料 | 200 OK | 404 Not Found |
| `PUT` | `/api/profiles/{id}` | 全量更新指定 ID 的資料 | 200 OK | 400 Bad Request, 404 Not Found |
| `DELETE` | `/api/profiles/{id}` | 永久刪除指定 ID 的資料 | 204 No Content | 404 Not Found |

## 範圍外
- 用戶認證與授權機制（假設請求已通過基礎驗證）。
- 個人資料的分頁、搜尋或過濾功能。
- 檔案上傳（如頭像）功能。
- 歷史版本追蹤或軟刪除邏輯。

FEATURE_NAME: personal-profile-crud
