/*
 * Copyright 2017 John Grosh <john.a.grosh@gmail.com>.
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
package com.jagrosh.jmusicbot.commands.owner;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jmusicbot.Bot;
import com.jagrosh.jmusicbot.commands.OwnerCommand;

/**
 *
 * @author John Grosh <john.a.grosh@gmail.com>
 */
public class ShutdownCmd extends OwnerCommand
{
    private final Bot bot; // 機器人實例
    
    public ShutdownCmd(Bot bot)
    {
        this.bot = bot;
        this.name = "shutdown"; // 指令名稱
        this.help = "安全關閉"; // 指令說明
        this.aliases = bot.getConfig().getAliases(this.name); // 別名
        this.guildOnly = false; // 可以在私人頻道使用
    }
    
    @Override
    protected void execute(CommandEvent event)
    {
        event.replyWarning("正在關閉...");
        bot.shutdown(); // 關閉機器人
    }
}
