---
title: 作為服務運行
description: "如何將 JMusicBot 作為服務運行，以便它可以在後台運行而無需手動啟動。"
---

## 作為服務運行
將 JMusicBot 作為服務運行可以使其在後台運行，而無需手動啟動。這對於在伺服器上運行機器人或希望能夠關閉終端而不停止機器人時非常有用。

### 使用 systemd 的 Linux

!!! warning
    此方法假設您已為機器人創建了一個用戶。如果尚未創建，請參閱 [此指南](https://www.digitalocean.com/community/tutorials/how-to-create-a-sudo-user-on-ubuntu-quickstart) 以獲取說明。

!!! note
    將 jar 文件複製到機器人運行用戶的主目錄，或更改服務文件中的 `WorkingDirectory` 和 `ExecStart` 行以指向正確位置。

1. 打開終端並運行以下命令以創建新的服務文件：

```bash
sudo nano /etc/systemd/system/JMusicBot.service
```

2. 將以下文本複製到文件中並保存：

```ini
[Unit]
Description=JMusicBot
Requires=network.target
After=network.target

[Service]
WorkingDirectory=/home/<username>
User=<username>
Group=<username>
Type=simple
ExecStart=/usr/bin/env java -Dnogui=true -jar JMusicBot.jar
Restart=always
RestartSec=10

[Install]
WantedBy=multi-user.target
```

!!! note
    將 `<username>` 替換為機器人運行的用戶名。

4. 運行以下命令以啟動機器人：

```bash
sudo systemctl start JMusicBot
```

5. 運行以下命令以停止機器人：

```bash
sudo systemctl stop JMusicBot
```

6. 運行以下命令以重啟機器人：

```bash
sudo systemctl restart JMusicBot
```

7. 運行以下命令以啟用機器人在啟動時自動啟動：

```bash
sudo systemctl enable JMusicBot
```


### 使用 screen 的 Linux

!!! warning
    此方法不建議用於生產環境，請參見 [systemd](#linux-using-systemd)。

1. 安裝 [screen](https://www.howtoforge.com/linux_screen) 工具（如果尚未安裝）。
2. 運行以下命令以啟動機器人：

```bash
screen -dmS JMusicBot java -jar JMusicBot.jar
```

3. 運行以下命令以停止機器人：

```bash
screen -S JMusicBot -X quit
```

4. 運行以下命令以重啟機器人：

```bash
screen -S JMusicBot -X quit
screen -dmS JMusicBot java -jar JMusicBot.jar
```

### Windows

1. 下載 [NSSM](https://nssm.cc/download) 可執行文件，並將其放在與 JMusicBot jar 文件相同的目錄中。
2. 在與 JMusicBot jar 文件相同的目錄中打開命令提示符，運行以下命令：

```bat
nssm install JMusicBot java -jar JMusicBot.jar
```

3. 運行以下命令以啟動服務：

```bat
nssm start JMusicBot
```

4. 運行以下命令以停止服務：

```bat
nssm stop JMusicBot
```

5. 運行以下命令以刪除服務：

```bat
nssm remove JMusicBot
```
