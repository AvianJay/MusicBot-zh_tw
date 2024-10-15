---
title: 安裝 Java
description: "在你的系統上安裝 Java"
---

# JMusicBot 需要 Java 11
一些較新的版本可能也可以運行，但可能會遇到相容性問題。對於大多數平台，安裝 Java 最簡單的方法是下載安裝程式（如下面所列）。Linux 用戶可以選擇使用套件管理器通過命令行安裝。

## 下載安裝程式（適用於任何平台）
1. 前往 [Adoptium](https://adoptium.net/temurin/releases/?version=11&package=jre) 或 [Oracle](https://www.oracle.com/java/technologies/javase/jdk11-archive-downloads.html)
2. 下載與你的系統相符的安裝程式
3. 執行安裝程式或手動安裝該套件（取決於你的選擇）

## 通過命令行安裝（僅限 Linux）
!!! 提示
    如果你安裝了多個 Java 版本，可以使用 `sudo update-alternatives --config java` 來選擇預設使用哪一個版本。你可以執行 `java -version` 來查看當前的預設版本。
### Ubuntu
1. 執行：`sudo apt-get update && sudo apt-get install openjdk-11-jre -y`
### Debian 
1. 如果尚未安裝 `sudo`，請執行安裝（`apt update && apt upgrade && apt install sudo`）
2. 執行：`sudo apt-get install default-jre`
### Raspbian (Raspberry Pi)
1. 執行：`sudo apt-get install oracle-java11-jdk`
