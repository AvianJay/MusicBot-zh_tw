/*
 * Copyright 2018 John Grosh (jagrosh)
 *
 * 根據 Apache License 2.0 版（以下簡稱「許可證」）授權使用本文件；
 * 除非遵守許可證，否則您不得使用本文件。
 * 您可以在以下網址獲取許可證副本：
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * 除非適用法律要求或書面同意，根據許可證分發的軟體按「現狀」提供，
 * 不附帶任何明示或默示的保證或條件。
 * 請參閱許可證以瞭解具體的許可權和限制。
 */
package com.jagrosh.jmusicbot.entities;

import java.util.Scanner;
import javax.swing.JOptionPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author John Grosh (john.a.grosh@gmail.com)
 */
public class Prompt
{
    private final String title; // 標題
    private final String noguiMessage; // 無 GUI 模式訊息
    
    private boolean nogui; // 是否為無 GUI 模式
    private boolean noprompt; // 是否不顯示提示
    private Scanner scanner; // 用於讀取命令行輸入的掃描器
    
    public Prompt(String title)
    {
        this(title, null); // 使用只有標題的構造函數
    }
    
    public Prompt(String title, String noguiMessage)
    {
        this(title, noguiMessage, "true".equalsIgnoreCase(System.getProperty("nogui")), "true".equalsIgnoreCase(System.getProperty("noprompt")));
    }
    
    public Prompt(String title, String noguiMessage, boolean nogui, boolean noprompt)
    {
        this.title = title;
        this.noguiMessage = noguiMessage == null ? "切換到無 GUI 模式。您可以通過包括 -Dnogui=true 標誌手動啟動無 GUI 模式。" : noguiMessage;
        this.nogui = nogui;
        this.noprompt = noprompt;
    }
    
    public boolean isNoGUI()
    {
        return nogui; // 返回是否為無 GUI 模式
    }
    
    public void alert(Level level, String context, String message)
    {
        if(nogui) // 如果是無 GUI 模式
        {
            Logger log = LoggerFactory.getLogger(context);
            switch(level) // 根據不同的級別記錄訊息
            {
                case INFO: 
                    log.info(message); 
                    break;
                case WARNING: 
                    log.warn(message); 
                    break;
                case ERROR: 
                    log.error(message); 
                    break;
                default: 
                    log.info(message); 
                    break;
            }
        }
        else // 如果不是無 GUI 模式
        {
            try 
            {
                int option = 0;
                switch(level)
                {
                    case INFO: 
                        option = JOptionPane.INFORMATION_MESSAGE; 
                        break;
                    case WARNING: 
                        option = JOptionPane.WARNING_MESSAGE; 
                        break;
                    case ERROR: 
                        option = JOptionPane.ERROR_MESSAGE; 
                        break;
                    default:
                        option = JOptionPane.PLAIN_MESSAGE; // 默認訊息類型
                        break;
                }
                JOptionPane.showMessageDialog(null, "<html><body><p style='width: 400px;'>"+message, title, option);
            }
            catch(Exception e) 
            {
                nogui = true; // 切換到無 GUI 模式
                alert(Level.WARNING, context, noguiMessage); // 顯示無 GUI 模式訊息
                alert(level, context, message); // 再次顯示原訊息
            }
        }
    }
    
    public String prompt(String content)
    {
        if(noprompt) // 如果不顯示提示
            return null;
        if(nogui) // 如果是無 GUI 模式
        {
            if(scanner==null)
                scanner = new Scanner(System.in); // 初始化掃描器
            try
            {
                System.out.println(content); // 顯示提示內容
                if(scanner.hasNextLine())
                    return scanner.nextLine(); // 讀取用戶輸入
                return null;
            }
            catch(Exception e)
            {
                alert(Level.ERROR, title, "無法從命令行讀取輸入。"); // 顯示錯誤訊息
                e.printStackTrace(); // 輸出堆棧跟蹤
                return null;
            }
        }
        else // 如果不是無 GUI 模式
        {
            try 
            {
                return JOptionPane.showInputDialog(null, content, title, JOptionPane.QUESTION_MESSAGE); // 顯示輸入對話框
            }
            catch(Exception e) 
            {
                nogui = true; // 切換到無 GUI 模式
                alert(Level.WARNING, title, noguiMessage); // 顯示無 GUI 模式訊息
                return prompt(content); // 再次呼叫提示
            }
        }
    }
    
    public static enum Level
    {
        INFO, WARNING, ERROR; // 提示級別
    }
}
