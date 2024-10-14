---
title: 進階配置選項
description: "進階 JMusicBot 使用者的配置設置"
---

## 指定不同的配置文件
可以使用 `-Dconfig=/path/to/config.file` 命令行選項來設置要用作配置文件的不同文件。例如：
```bash
java -Dconfig=alternate_config.txt -jar JMusicBot.jar
```
這將運行機器人，從 `alternate_config.txt` 加載，而不是 `config.txt`。

## 從命令行指定配置選項
類似於 `-Dconfig` 選項，配置文件中的任何設置也可以從命令行設置。例如，要從命令行設置前綴（而不是從配置中），您可以使用 `-Dprefix="!!"`（如果值包含空格或某些特殊字符，則需要用引號括起來）。例如：
```bash
java -Dprefix="!" -jar JMusicBot.jar
```

## 從環境變量指定配置選項
要使用環境變量作為配置，有兩種選擇。以下例子假設前綴已設置為環境變量 `CUSTOM_PREFIX`。
### 從命令行
要從命令行使用環境變量，使用上面的相同系統，但替換為解析的變量名稱。例如：
```bash
java -Dprefix="$CUSTOM_PREFIX" -jar JMusicBot.jar
```
### 在配置文件中
要在配置文件中使用環境變量，請按如下方式指定：
```hocon
// 這是在配置文件中
prefix = ${CUSTOM_PREFIX}
```
