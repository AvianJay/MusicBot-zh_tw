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
import net.dv8tion.jda.api.entities.Activity;

/**
 *
 * @author John Grosh <john.a.grosh@gmail.com>
 */
public class SetgameCmd extends OwnerCommand
{
    public SetgameCmd(Bot bot)
    {
        this.name = "setgame"; // 指令名稱
        this.help = "設置機器人正在玩的遊戲"; // 指令說明
        this.arguments = "[action] [game]"; // 參數
        this.aliases = bot.getConfig().getAliases(this.name); // 別名
        this.guildOnly = false; // 可以在私人頻道使用
        this.children = new OwnerCommand[]{
            new SetlistenCmd(),
            new SetstreamCmd(),
            new SetwatchCmd()
        };
    }
    
    @Override
    protected void execute(CommandEvent event) 
    {
        String title = event.getArgs().toLowerCase().startsWith("playing") ? event.getArgs().substring(7).trim() : event.getArgs();
        try
        {
            event.getJDA().getPresence().setActivity(title.isEmpty() ? null : Activity.playing(title));
            event.reply(event.getClient().getSuccess()+" **"+event.getSelfUser().getName()
                    +"** 現在 "+(title.isEmpty() ? "不再玩任何遊戲。" : "正在玩 `"+title+"`"));
        }
        catch(Exception e)
        {
            event.reply(event.getClient().getError()+" 遊戲無法設置！");
        }
    }
    
    private class SetstreamCmd extends OwnerCommand
    {
        private SetstreamCmd()
        {
            this.name = "stream"; // 指令名稱
            this.aliases = new String[]{"twitch","streaming"}; // 別名
            this.help = "設置機器人正在播放的遊戲為直播"; // 指令說明
            this.arguments = "<username> <game>"; // 參數
            this.guildOnly = false; // 可以在私人頻道使用
        }

        @Override
        protected void execute(CommandEvent event)
        {
            String[] parts = event.getArgs().split("\\s+", 2);
            if(parts.length<2)
            {
                event.replyError("請提供一個 Twitch 用戶名和要 '直播' 的遊戲名稱");
                return;
            }
            try
            {
                event.getJDA().getPresence().setActivity(Activity.streaming(parts[1], "https://twitch.tv/"+parts[0]));
                event.replySuccess("**"+event.getSelfUser().getName()
                        +"** 現在正在直播 `"+parts[1]+"`");
            }
            catch(Exception e)
            {
                event.reply(event.getClient().getError()+" 遊戲無法設置！");
            }
        }
    }
    
    private class SetlistenCmd extends OwnerCommand
    {
        private SetlistenCmd()
        {
            this.name = "listen"; // 指令名稱
            this.aliases = new String[]{"listening"}; // 別名
            this.help = "設置機器人正在聆聽的遊戲"; // 指令說明
            this.arguments = "<title>"; // 參數
            this.guildOnly = false; // 可以在私人頻道使用
        }

        @Override
        protected void execute(CommandEvent event)
        {
            if(event.getArgs().isEmpty())
            {
                event.replyError("請提供一個要聆聽的標題！");
                return;
            }
            String title = event.getArgs().toLowerCase().startsWith("to") ? event.getArgs().substring(2).trim() : event.getArgs();
            try
            {
                event.getJDA().getPresence().setActivity(Activity.listening(title));
                event.replySuccess("**"+event.getSelfUser().getName()+"** 現在正在聆聽 `"+title+"`");
            } catch(Exception e) {
                event.reply(event.getClient().getError()+" 遊戲無法設置！");
            }
        }
    }
    
    private class SetwatchCmd extends OwnerCommand
    {
        private SetwatchCmd()
        {
            this.name = "watch"; // 指令名稱
            this.aliases = new String[]{"watching"}; // 別名
            this.help = "設置機器人正在觀看的遊戲"; // 指令說明
            this.arguments = "<title>"; // 參數
            this.guildOnly = false; // 可以在私人頻道使用
        }

        @Override
        protected void execute(CommandEvent event)
        {
            if(event.getArgs().isEmpty())
            {
                event.replyError("請提供一個要觀看的標題！");
                return;
            }
            String title = event.getArgs();
            try
            {
                event.getJDA().getPresence().setActivity(Activity.watching(title));
                event.replySuccess("**"+event.getSelfUser().getName()+"** 現在正在觀看 `"+title+"`");
            } catch(Exception e) {
                event.reply(event.getClient().getError()+" 遊戲無法設置！");
            }
        }
    }
}
