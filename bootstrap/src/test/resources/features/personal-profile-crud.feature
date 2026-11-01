# language: zh-TW

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
        那麼回應的狀態碼應該是 201
        
    場景: 嘗試建立重複電子郵件的個人資料失敗
        假設已經存在一個電子郵件為 "duplicate@test.com" 的個人資料
        當我發送 POST 請求至 "/api/profiles"，內容包含:
            """json
            {
              "email": "duplicate@test.com",
              "displayName": "Another User"
            }
            """
        那麼回應的狀態碼應該是 409
        
    場景: 嘗試建立無效格式電子郵件的個人資料失敗
        當我發送 POST 請求至 "/api/profiles"，內容包含:
            """json
            {
              "email": "invalid-email",
              "displayName": "Bad Email"
            }
            """
        那麼回應的狀態碼應該是 400

    場景: 成功查詢存在的個人資料
        假設已有一個 ID 為 "550e8400-e29b-41d4-a716-446655440000" 的個人資料
        當我發送 GET 請求至 "/api/profiles/550e8400-e29b-41d4-a716-446655440000"
        那麼回應的狀態碼應該是 200
        並且回應內容中的 email 應該等於 "user@example.com"
        
    場景: 查詢不存在的個人資料
        當我發送 GET 請求至 "/api/profiles/non-existent-id"
        那麼回應的狀態碼應該是 404

    場景: 成功更新個人資料
        假設已有一個 ID 為 "550e8400-e29b-41d4-a716-446655440001" 且顯示名稱為 "Old Name" 的個人資料
        當我發送 PUT 請求至 "/api/profiles/550e8400-e29b-41d4-a716-446655440001"，內容包含:
            """json
            {
              "email": "user@example.com",
              "displayName": "New Name"
            }
            """
        那麼回應的狀態碼應該是 200
        
    場景: 嘗試更新不存在的個人資料
        當我發送 PUT 請求至 "/api/profiles/non-existent-id"，內容包含:
            """json
            {
              "email": "test@test.com",
              "displayName": "Test"
            }
            """
        那麼回應的狀態碼應該是 404

    場景: 成功刪除個人資料
        假設已有一個 ID 為 "550e8400-e29b-41d4-a716-446655440002" 的個人資料
        當我發送 DELETE 請求至 "/api/profiles/550e8400-e29b-41d4-a716-446655440002"
        那麼回應的狀態碼應該是 204
        
    場景: 嘗試刪除不存在的個人資料
        當我發送 DELETE 請求至 "/api/profiles/non-existent-id"
        那麼回應的狀態碼應該是 404
