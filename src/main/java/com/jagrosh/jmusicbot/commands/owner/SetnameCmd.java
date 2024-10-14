/*
 * Copyright 2018 John Grosh <john.a.grosh@gmail.com>.
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
import net.dv8tion.jda.api.exceptions.RateLimitedException;

/**
 *
 * @author John Grosh <john.a.grosh@gmail.com>
 */
public class SetnameCmd extends OwnerCommand
{
    public SetnameCmd(Bot bot)
    {
        this.name = "setname"; // 指令名稱
        this.help = "設置機器人的名稱"; // 指令說明
        this.arguments = "<name>"; // 參數
        this.aliases = bot.getConfig().getAliases(this.name); // 別名
        this.guildOnly = false; // 可以在私人頻道使用
    }
    
    @Override
    protected void execute(CommandEvent event) 
    {
        try 
        {
            String oldname = event.getSelfUser().getName(); // 獲取舊名稱
            event.getSelfUser().getManager().setName(event.getArgs()).complete(false); // 設置新名稱
            event.reply(event.getClient().getSuccess()+" 名稱已從 `"+oldname+"` 更改為 `"+event.getArgs()+"`");
        } 
        catch(RateLimitedException e) 
        {
            event.reply(event.getClient().getError()+" 名稱每小時只能更改兩次！");
        }
        catch(Exception e) 
        {
            event.reply(event.getClient().getError()+" 該名稱無效！");
        }
    }
}
