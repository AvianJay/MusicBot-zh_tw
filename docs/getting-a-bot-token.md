---
title: 獲取機器人 Token
description: "如何創建並獲取 Discord 機器人 token 的說明"
---

此頁面將詳細描述如何為您的音樂機器人獲取 token。

1. 前往您的 [應用程序頁面](https://discordapp.com/login?redirect_to=/developers/applications)（您可能需要先登錄）

2. 點擊*創建應用程序*按鈕  
![新應用程序](../assets/images/create-application.png)

3. 在 **General Information** 標籤中，設置一個名稱以識別您的應用程序（這不是機器人的名稱）  
![創建應用程序](../assets/images/general-info.png)

4. 前往 **Bot** 標籤並選擇 **Add Bot**  
![添加機器人](../assets/images/add-bot.png)

5. 點擊*Yes, do it!*  
![Yes](../assets/images/yes-do-it.png)

6. 設置名稱和頭像（可選）  
![設置](../assets/images/customize-bot.png)

7. 取消勾選 **Public Bot**（可選），並勾選 **Message Content Intent** 和 **Server Members Intents**  
![消息內容意圖](../assets/images/oauth-and-intents.png)

    !!! warning
        JMusicBot 並非設計為公共音樂機器人，建議別對外開放。

8. 保存您的設置  
![保存](../assets/images/save-changes.png)

9. 在 token 部分選擇 **Copy** 按鈕，將機器人的 token 複製到剪貼板。  
![複製 token](../assets/images/copy-token.png)
