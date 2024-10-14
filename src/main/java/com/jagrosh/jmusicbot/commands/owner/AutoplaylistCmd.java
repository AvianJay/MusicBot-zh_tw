/*
 * Copyright 2016 John Grosh <john.a.grosh@gmail.com>.
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
import com.jagrosh.jmusicbot.settings.Settings;

/**
 *
 * @author John Grosh <john.a.grosh@gmail.com>
 */
public class AutoplaylistCmd extends OwnerCommand
{
    private final Bot bot;
    
    public AutoplaylistCmd(Bot bot)
    {
        this.bot = bot;
        this.guildOnly = true; // 只能在伺服器中使用
        this.name = "autoplaylist"; // 指令名稱
        this.arguments = "<name|NONE>"; // 指令參數
        this.help = "設置伺服器的預設播放清單"; // 指令說明
        this.aliases = bot.getConfig().getAliases(this.name); // 別名
    }

    @Override
    public void execute(CommandEvent event) 
    {
        if(event.getArgs().isEmpty())
        {
            event.reply(event.getClient().getError() + " 請提供播放清單名稱或 NONE");
            return; // 如果沒有參數，則提示用戶
        }
        if(event.getArgs().equalsIgnoreCase("none"))
        {
            Settings settings = event.getClient().getSettingsFor(event.getGuild());
            settings.setDefaultPlaylist(null); // 清除預設播放清單
            event.reply(event.getClient().getSuccess() + " 已清除 **" + event.getGuild().getName() + "** 的預設播放清單");
            return;
        }
        String pname = event.getArgs().replaceAll("\\s+", "_"); // 將空格替換為底線
        if(bot.getPlaylistLoader().getPlaylist(pname) == null)
        {
            event.reply(event.getClient().getError() + " 找不到 `" + pname + ".txt`！");
        }
        else
        {
            Settings settings = event.getClient().getSettingsFor(event.getGuild());
            settings.setDefaultPlaylist(pname); // 設置新的預設播放清單
            event.reply(event.getClient().getSuccess() + " **" + event.getGuild().getName() + "** 的預設播放清單現在是 `" + pname + "`");
        }
    }
}
