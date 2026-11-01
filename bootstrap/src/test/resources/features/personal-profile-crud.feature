# language: zh-TW

功能: 個人資料 CRUD 操作
    作為一名用戶
    我希望能夠管理我的個人資料
    
    場景: 成功建立新的個人資料
        當我發送 POST 請求至 "/api/profiles"，內容包含:
            """json
            {
              "email": "user_{{timestamp}}@example.com",
              "displayName": "Test User"
            }
            """
        那麼回應的狀態碼應該是 201
        
    場景: 嘗試建立重複電子郵件的個人資料失敗
        # 先建立第一筆資料 (使用唯一 email)
        當我發送 POST 請求至 "/api/profiles"，內容包含:
            """json
            {
              "email": "dup_{{timestamp}}@test.com",
              "displayName": "First User"
            }
            """
        那麼回應的狀態碼應該是 201
        
        # 再嘗試建立第二筆相同 Email 的資料
        當我發送 POST 請求至 "/api/profiles"，內容包含:
            """json
            {
              "email": "dup_{{timestamp}}@test.com",
              "displayName": "Second User"
            }
            """
        那麼回應的狀態碼應該是 409
        
    場景: 嘗試建立無效格式電子郵件的個人資料失敗
        當我發送 POST 請求至 "/api/profiles"，內容包含:
            """json
            {
              "email": "invalid-email-{{timestamp}}",
              "displayName": "Bad Email"
            }
            """
        那麼回應的狀態碼應該是 400

    場景: 成功查詢存在的個人資料
        # 先創建一條記錄以獲取 ID (使用唯一 email)
        當我發送 POST 請求至 "/api/profiles"，內容包含:
            """json
            {
              "email": "query_{{timestamp}}@example.com",
              "displayName": "Query User"
            }
            """
        那麼回應的狀態碼應該是 201
        
        # 使用最後創建的資源進行查詢
        當我查詢最後建立的個人資料時
        那麼回應的狀態碼應該是 200
        並且回應內容中的 email 應該等於 "{{last_email}}"
        
    場景: 查詢不存在的個人資料
        當我發送 GET 請求至 "/api/profiles/00000000-0000-0000-0000-000000000001"
        那麼回應的狀態碼應該是 404

    場景: 成功更新個人資料
        # 先創建一條記錄 (使用唯一 email)
        當我發送 POST 請求至 "/api/profiles"，內容包含:
            """json
            {
              "email": "update_{{timestamp}}@example.com",
              "displayName": "Old Name"
            }
            """
        那麼回應的狀態碼應該是 201
        
        # 更新該資料
        當我更新最後建立的個人資料，內容包含:
            """json
            {
              "email": "update_{{timestamp}}@example.com",
              "displayName": "New Name"
            }
            """
        那麼回應的狀態碼應該是 200
        
        # 驗證更新結果
        當我查詢最後建立的個人資料時
        並且回應的內容中的 displayName 應該等於 "New Name"
        
    場景: 嘗試更新不存在的個人資料
        當我發送 PUT 請求至 "/api/profiles/00000000-0000-0000-0000-000000000002"，內容包含:
            """json
            {
              "email": "test_{{timestamp}}@test.com",
              "displayName": "Test"
            }
            """
        那麼回應的狀態碼應該是 404

    場景: 成功刪除個人資料
        # 先創建一條記錄 (使用唯一 email)
        當我發送 POST 請求至 "/api/profiles"，內容包含:
            """json
            {
              "email": "delete_{{timestamp}}@example.com",
              "displayName": "To Be Deleted"
            }
            """
        那麼回應的狀態碼應該是 201
        
        # 刪除該資料
        當我刪除最後建立的個人資料時
        那麼回應的狀態碼應該是 204
        
        # 驗證刪除後無法再查詢
        當我查詢最後建立的個人資料時
        那麼回應的狀態碼應該是 404
        
    場景: 嘗試刪除不存在的個人資料
        當我發送 DELETE 請求至 "/api/profiles/00000000-0000-0000-0000-000000000003"
        那麼回應的狀態碼應該是 404
