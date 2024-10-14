---
title: 進階播放清單生成
description: "進階 JMusicBot 使用者的播放清單生成範例"
---

此頁面描述如何從文件夾生成播放清單（txt）文件。這些範例假設您的歌曲為 mp3 格式；如果您使用不同格式，請在所有提供的命令中將 `mp3` 替換為您的格式。

## Windows（桌面）
1. 在包含歌曲的文件夾中打開 Powershell（您可以按住 Shift 並右鍵點擊，然後從 Explorer 視窗中選擇在此處打開 Powershell 視窗）
2. 執行 **`Get-ChildItem . -Filter *.mp3 -Recurse | % { $_.FullName } | out-file -encoding ASCII songs.txt`**
3. 將 songs.txt 移動到您的 Playlists 文件夾，並將其重命名為您想要的播放清單名稱。
4. 如果您想添加其他歌曲或評論，可以編輯 songs.txt

## Linux（命令列）
1. 瀏覽到包含歌曲的文件夾 (`cd /path/to/the/folder`)
2. 執行 **`find "$(pwd)" | grep ".mp3" > songs.txt`**
3. 將 `songs.txt` 移動到您的 Playlists 文件夾並重命名 (`mv songs.txt /path/to/Playlists/playlistname.txt`)
