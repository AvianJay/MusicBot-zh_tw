---
title: 將您的機器人添加到您的伺服器
description: "如何將 Discord 機器人添加到您的 Discord 伺服器的指南"
---

!!! help "還沒有創建應用程式？"
    如果您還沒有創建機器人應用程式，請參考 [獲取機器人 Token](getting-a-bot-token.md) 來了解如何設置！

1. 前往 [應用程式頁面](https://discordapp.com/developers/applications) 並選擇您的其中一個機器人應用程式。  
![應用程式頁面](/assets/images/app-page.png)

2. 在設置列表中，選擇 **OAuth2**。  
![OAuth2](/assets/images/oauth.png)

3. 在 **Scopes** 區域，勾選 **Bot**。  
![Scopes](/assets/images/scopes.png)

4. 點擊 **複製** 按鈕將 OAuth2 URL 複製到剪貼簿。  
![複製](/assets/images/oauth-url.png)

5. 將鏈接貼到您的瀏覽器中。  
![貼上](/assets/images/browser.png)

6. 從下拉選單中選擇一個伺服器，然後點擊 **授權**。 **您必須擁有管理伺服器的權限才能將機器人添加到伺服器！** 如果未顯示任何伺服器，您可能需要 [登錄](https://discordapp.com/login)。  
![選擇伺服器](/assets/images/invite.png)


## 疑難排解
* 如果您收到 "需要授權碼 (Requires Code Grant)" 錯誤，請確保您的應用程式中該選項未被勾選：<br>![授權碼](/assets/images/code-grant.png)
* 如果您想手動生成鏈接，請將以下鏈接中的 CLIENTID 替換為您的機器人客戶端 ID：
```
https://discordapp.com/oauth2/authorize?client_id=CLIENTID&scope=bot
```
