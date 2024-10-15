---
title: 播放清單
description: "JMusicBot 上的播放清單"
---

## 📃 Youtube 播放清單
要播放 YouTube 播放清單，你只需要使用 `play` 指令加上播放清單的鏈接或播放清單 ID。

範例：
```diff
# 完整的播放清單 URL
+ !play https://www.youtube.com/playlist?list=PLUib4KwT0DMJaPgg_nr1ia8FY5JcXksvb

# 播放清單 ID
+ !play PLUib4KwT0DMJaPgg_nr1ia8FY5JcXksvb

# 不是播放清單鏈接！(注意這裡有 `watch?v=`)
- !play https://www.youtube.com/watch?v=bd2B6SjMh_w&list=PLUib4KwT0DMJaPgg_nr1ia8FY5JcXksvb&index=4
```


## 📃 本地播放清單
本地播放清單是位於與運行機器人相同資料夾中 `Playlists` 資料夾內的 .txt 文件。文件的每一行都是一個新條目，條目可以是：
* YouTube 視頻、SoundCloud 曲目或在線文件的鏈接
* 本地文件的完整或相對路徑
* YouTube 或 SoundCloud 播放清單的鏈接
* 在線流媒體或廣播的鏈接
* 搜索結果，前綴為 `ytsearch:` 表示 YouTube 搜索，`scsearch:` 表示 SoundCloud 搜索

以 `#` 或 `//` 開頭的行會被忽略。如果希望播放清單在加載時自動隨機播放，請在播放清單中的任意位置單獨添加 `#shuffle` 或 `//shuffle`。

範例播放清單：
```
# 這是一個範例播放清單
# 你可以將此文件作為 example_playlist.txt 放入你的 Playlists 資料夾

# 以下行使播放清單隨機播放
# 如果不想隨機播放，請完全刪除此行
# shuffle

# YouTube 播放清單 ID：
PLUib4KwT0DMJaPgg_nr1ia8FY5JcXksvb

# 搜索
ytsearch:gorillaz dare audio
scsearch:lights metrognome

# 直接鏈接
https://www.youtube.com/watch?v=x7ogV49WGco
```
範例指令：
```
!play playlist example_playlist
```


## 📃 Soundcloud 播放清單
只需使用 `play` 指令，後面加上播放清單鏈接。

範例：
```
!play [鏈接即將發布]
```


## 📃 Spotify
很遺憾，Spotify 的服務條款禁止直接播放 Spotify 的音樂。請考慮使用像 [PlaylistConverter.net](http://www.playlist-converter.net/) 這樣的播放清單轉換器，將 Spotify 播放清單轉換為 YouTube 播放清單。
