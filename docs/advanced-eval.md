---
title: 進階 Eval 腳本
description: "進階 JMusicBot 使用者的 Eval 腳本"
---

!!! danger
    請記住，eval 命令具有潛在危險性；雖然它只允許機器人擁有者使用，但 _切勿_ 運行您不熟悉的腳本！

以下是一些可以通過 eval 命令運行的腳本示例（僅限機器人擁有者）。如果您的機器人前綴設置為 `!!`，並且已啟用了 eval 命令，則可以像下面列出的第一個例子一樣運行 eval（例如：`!!eval jda.guilds`）。

### 列出機器人所在的所有伺服器
```js
jda.guilds
```

### 離開特定伺服器
```js
jda.getGuildById("GUILDIDHERE").leave().queue()
```
