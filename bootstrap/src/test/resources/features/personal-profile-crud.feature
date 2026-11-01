# language: zh-TW

功能: 個人資料 CRUD 操作
    作為一名用戶
   我希望能夠管理我的個人資料
    
    場景: 成功建立新的個人資料
        當我發送 POST 請求至 "/api/profiles"，內容包含:
            """
            {
              "email": "user@example.com",
              "displayName": "測試用戶"
            }
            """
        那麼回應的狀態碼應該是 201
        
    場景: 嘗試建立重複電子郵件的個人資料失敗
        假設已存在一個電子郵件為 "duplicate@test.com" 的個人資料
        當我發送 POST 請求至 "/api/profiles"，內容包含:
            """
            {
              "email": "duplicate@test.com",
              "displayName": "另一個用戶"
            }
            """
        那麼回應的狀態碼應該是 409
        
    場景: 嘗試建立無效格式電子郵件的個人資料失敗
        當我發送 POST 請求至 "/api/profiles"，內容包含:
            """
            {
              "email": "invalid-email",
              "displayName": "壞資料"
            }
            """
        那麼回應的狀態碼應該是 400

    場景: 成功查詢存在的個人資料
        假設系統中已存在 ID 為 "550e8400-e29b-41d4-a716-446655440000" 的個人資料
        並且其電子郵件為 "query@example.com"
        當我發送 GET 請求至 "/api/profiles/550e8400-e29b-41d4-a716-446655440000"
        那麼回應的狀態碼應該是 200
        並且回應主體應包含正確的 email 與 displayName
        
    場景: 查詢不存在的個人資料
        當我發送 GET 請求至 "/api/profiles/non-existent-id"
        那麼回應的狀態碼應該是 404

    場景: 成功更新個人資料
        假設系統中已存在 ID 為 "update-id" 的個人資料
        並且目前顯示名稱為 "舊名稱"
        當我發送 PUT 請求至 "/api/profiles/update-id"，內容包含:
            """
            {
              "email": "updated@test.com",
              "displayName": "新名稱"
            }
            """
        那麼回應的狀態碼應該是 200
        並且再次查詢該資源時，顯示名稱應變更為 "新名稱"
        
    場景: 嘗試更新不存在的個人資料
        當我發送 PUT 請求至 "/api/profiles/missing-id"，內容包含:
            """
            {
              "email": "test@test.com",
              "displayName": "無效更新"
            }
            """
        那麼回應的狀態碼應該是 404

    場景: 成功刪除個人資料
        假設系統中已存在 ID 為 "delete-id" 的個人資料
        當我發送 DELETE 請求至 "/api/profiles/delete-id"
        那麼回應的狀態碼應該是 204
        並且隨後查詢該 ID 時應返回 404
        
    場景: 刪除不存在的個人資料
        當我發送 DELETE 請求至 "/api/profiles/non-existent-delete-id"
        那麼回應的狀態碼應該是 404
