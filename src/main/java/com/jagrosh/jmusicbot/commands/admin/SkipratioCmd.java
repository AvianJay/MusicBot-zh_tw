/*
 * 版權所有 2018 John Grosh <john.a.grosh@gmail.com>.
 *
 * 根據 Apache 許可證 2.0 版（"許可證"）授權；
 * 除非遵守許可證，否則你不能使用此檔案。
 * 你可以在以下網址獲得許可證副本：
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * 除非適用的法律要求或書面同意，
 * 根據許可證分發的軟體是在 "原樣" 基礎上提供的，
 * 不附帶任何形式的明示或默示擔保或條件。
 * 有關許可證下具體語言的權限和限制，請參見許可證。
 */
package com.jagrosh.jmusicbot.commands.admin;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jmusicbot.Bot;
import com.jagrosh.jmusicbot.commands.AdminCommand;
import com.jagrosh.jmusicbot.settings.Settings;

/**
 *
 * @author John Grosh (john.a.grosh@gmail.com)
 */
public class SkipratioCmd extends AdminCommand
{
    public SkipratioCmd(Bot bot)
    {
        this.name = "setskip";
        this.help = "設置特定伺服器的跳過百分比";
        this.arguments = "<0 - 100>";
        this.aliases = bot.getConfig().getAliases(this.name);
    }
    
    @Override
    protected void execute(CommandEvent event) 
    {
        try
        {
            int val = Integer.parseInt(event.getArgs().endsWith("%") ? event.getArgs().substring(0,event.getArgs().length()-1) : event.getArgs());
            if( val < 0 || val > 100)
            {
                event.replyError("提供的值必須在 0 到 100 之間！");
                return;
            }
            Settings s = event.getClient().getSettingsFor(event.getGuild());
            s.setSkipRatio(val / 100.0);
            event.replySuccess("跳過百分比已設置為 `" + val + "%`，適用於 *" + event.getGuild().getName() + "*");
        }
        catch(NumberFormatException ex)
        {
            event.replyError("請包含一個介於 0 和 100 之間的整數（預設值為 55）。此數字是必須投票的聆聽用戶百分比，以跳過歌曲。");
        }
    }
}
