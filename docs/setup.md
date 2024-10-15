---
title: 設置
description: "設置 JMusicBot"
---

## 1️⃣ 安裝 Java
* JMusicBot 需要 Java 11
* 有關如何在您的系統上安裝 Java 的說明請參閱：[安裝 Java](installing-java.md)

## 2️⃣ 下載 JMusicBot
* 從 [發佈頁面](https://github.com/jagrosh/MusicBot/releases/latest) 下載最新的 **JMusicBot-X.Y.Z.jar** （還可以選擇下載示例 **config.txt** 文件）（或者，從發佈頁面獲取 URL，然後使用 wget 或類似的命令行工具進行下載）。
* 您的文件夾應該類似於這樣（在桌面上）：  
![View](/assets/images/folder-view.png)
!!! note
    上面的圖像是 Windows，但在所有平台上應該看起來相似  
!!! warning
    不要將其放入 *下載* 或 *桌面* 文件夾。請使用 *文檔* 內的文件夾

## 3️⃣ 配置機器人
* 創建一個機器人帳號，並在 Discord 開發者頁面上進行配置
  * 請參閱 [獲取機器人 Token](getting-a-bot-token.md) 以獲取逐步說明
  * 確保取消勾選「公開機器人」，並勾選「訊息內容意圖」和「伺服器成員意圖」
* 創建配置文件
  * 在沒有配置文件的情況下運行機器人將提示您提供機器人 Token 和用戶 ID。提供這些信息後，系統會為您生成一個配置文件。
  * 可以在 [示例配置](config.md) 中找到 `config.txt` 文件的示例。您可以在與機器人相同的文件夾中創建一個 `config.txt` 文件，將示例配置文件的內容粘貼到該文件中，並修改其中的值。
  * 如果需要幫助查找配置中的一些值，請參閱 [獲取機器人 Token](getting-a-bot-token.md) 和 [查找您的用戶 ID](finding-your-user-id.md)。

!!! warning
    每次編輯 `config.txt` 文件後，您都必須重新啟動機器人。建議在編輯文件之前完全關閉機器人。

## 4️⃣ 運行 JMusicBot
* 運行 jar 文件（選擇以下其中一種方式）：
  * 雙擊 jar 文件（在桌面環境中），或
  * 從命令行運行 `java -Dnogui=true -jar JMusicBot-X.Y.Z.jar`（將 X、Y 和 Z 替換為發佈號）
* 如果提示，請提供所需的信息。
* 等待 "Finished Loading" 消息。

!!! tip
    如果您希望讓機器人在後台運行，請參閱 [作為服務運行](running-as-a-service.md)

## 5️⃣ 將機器人添加到您的伺服器
* 當機器人啟動時，如果它尚未添加到任何伺服器，則會在控制台中提供一個鏈接。
* 或者，請按照這些說明（附圖）：[將您的機器人添加到伺服器](adding-your-bot.md)

!!! tip
    如果您遇到問題，請務必查看 [故障排除](troubleshooting.md) 頁面！
