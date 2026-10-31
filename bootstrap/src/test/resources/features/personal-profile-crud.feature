# language: zh-TW

功能: 個人資料 CRUD 操作
  作為 系統管理員或使用者
  我想要 建立、查詢、更新與刪除個人資料
  以便 管理系統中的使用者基本資訊

  場景: 成功建立新的個人資料
    假設 電子郵件 "alice@example.com" 在系統中尚不存在
    當 我發送 POST 請求至 "/profiles"，內容包含:
      | 欄位     | 值                  |
      | email    | alice@example.com   |
      | name     | Alice               |
      | bio      | Hello World         |
    那麼 回應狀態碼應為 201 Created
    並且 回應標頭 "Location" 應包含新建立的個人資料 ID
    並且 回應主體應包含新建立的個人資料資訊

  場景: 建立個人資料時電子郵件重複
    假設 電子郵件 "bob@example.com" 已存在於系統中
    當 我發送 POST 請求至 "/profiles"，內容包含:
      | 欄位     | 值                |
      | email    | bob@example.com   |
      | name     | Bob               |
      | bio      | Test              |
    那麼 回應狀態碼應為 409 Conflict
    並且 回應主體應指出電子郵件已存在

  場景: 建立個人資料時電子郵件格式無效
    當 我發送 POST 請求至 "/profiles"，內容包含:
      | 欄位     | 值          |
      | email    | invalid     |
      | name     | Invalid     |
      | bio      | Test        |
    那麼 回應狀態碼應為 400 Bad Request
    並且 回應主體應指出電子郵件格式錯誤

  場景: 建立個人資料時姓名為空
    當 我發送 POST 請求至 "/profiles"，內容包含:
      | 欄位     | 值          |
      | email    | empty@test.com |
      | name     |             |
      | bio      | Test        |
    那麼 回應狀態碼應為 400 Bad Request
    並且 回應主體應指出姓名不可為空

  場景: 成功查詢存在的個人資料
    假設 系統中已存在 ID 為 "uuid-123" 的個人資料，其電子郵件為 "charlie@example.com"
    當 我發送 GET 請求至 "/profiles/uuid-123"
    那麼 回應狀態碼應為 200 OK
    並且 回應主體應包含該個人資料的正確資訊

  場景: 查詢不存在的個人資料
    當 我發送 GET 請求至 "/profiles/non-existent-id"
    那麼 回應狀態碼應為 404 Not Found
    並且 回應主體應指出找不到該個人資料

  場景: 成功更新個人資料
    假設 系統中已存在 ID 為 "uuid-456" 的個人資料，其姓名為 "Old Name"
    當 我發送 PUT 請求至 "/profiles/uuid-456"，內容包含:
      | 欄位     | 值            |
      | email    | old@test.com  |
      | name     | New Name      |
      | bio      | Updated Bio   |
    那麼 回應狀態碼應為 200 OK
    並且 回應主體應包含更新後的個人資料資訊，其中姓名應為 "New Name"

  場景: 更新個人資料時指定不存在的 ID
    當 我發送 PUT 請求至 "/profiles/non-existent-id"，內容包含:
      | 欄位     | 值            |
      | email    | test@test.com |
      | name     | Test          |
      | bio      | Test          |
    那麼 回應狀態碼應為 404 Not Found
    並且 回應主體應指出找不到該個人資料

  場景: 成功刪除存在的個人資料
    假設 系統中已存在 ID 為 "uuid-789" 的個人資料
    當 我發送 DELETE 請求至 "/profiles/uuid-789"
    那麼 回應狀態碼應為 204 No Content
    並且 後續查詢該 ID 應返回 404 Not Found

  場景: 刪除不存在的個人資料
    當 我發送 DELETE 請求至 "/profiles/non-existent-id"
    那麼 回應狀態碼應為 404 Not Found
    並且 回應主體應指出找不到該個人資料
