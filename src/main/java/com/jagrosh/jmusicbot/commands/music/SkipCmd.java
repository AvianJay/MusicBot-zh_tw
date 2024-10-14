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
package com.jagrosh.jmusicbot.commands.music;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jmusicbot.Bot;
import com.jagrosh.jmusicbot.audio.AudioHandler;
import com.jagrosh.jmusicbot.audio.RequestMetadata;
import com.jagrosh.jmusicbot.commands.MusicCommand;
import com.jagrosh.jmusicbot.utils.FormatUtil;

/**
 *
 * @author John Grosh <john.a.grosh@gmail.com>
 */
public class SkipCmd extends MusicCommand 
{
    public SkipCmd(Bot bot)
    {
        super(bot);
        this.name = "skip"; // 指令名稱
        this.help = "投票跳過當前歌曲"; // 指令說明
        this.aliases = bot.getConfig().getAliases(this.name); // 別名
        this.beListening = true; // 需要正在收聽
        this.bePlaying = true; // 需要正在播放音樂
    }

    @Override
    public void doCommand(CommandEvent event) 
    {
        AudioHandler handler = (AudioHandler)event.getGuild().getAudioManager().getSendingHandler(); // 獲取音訊處理器
        RequestMetadata rm = handler.getRequestMetadata(); // 獲取請求元數據
        double skipRatio = bot.getSettingsManager().getSettings(event.getGuild()).getSkipRatio(); // 獲取跳過比例
        if(skipRatio == -1) {
          skipRatio = bot.getConfig().getSkipRatio(); // 使用預設跳過比例
        }
        
        // 檢查是否為歌曲的擁有者或不需要投票
        if(event.getAuthor().getIdLong() == rm.getOwner() || skipRatio == 0)
        {
            event.reply(event.getClient().getSuccess() + " 跳過了 **" + handler.getPlayer().getPlayingTrack().getInfo().title + "**");
            handler.getPlayer().stopTrack(); // 停止播放當前歌曲
        }
        else
        {
            int listeners = (int)event.getSelfMember().getVoiceState().getChannel().getMembers().stream()
                    .filter(m -> !m.getUser().isBot() && !m.getVoiceState().isDeafened()).count(); // 獲取有效聽眾數量
            String msg;
            
            // 檢查用戶是否已經投票
            if(handler.getVotes().contains(event.getAuthor().getId()))
                msg = event.getClient().getWarning() + " 你已經投票跳過這首歌 `[";
            else
            {
                msg = event.getClient().getSuccess() + " 你投票跳過這首歌 `[";
                handler.getVotes().add(event.getAuthor().getId()); // 添加投票
            }
            
            // 獲取投票數和所需票數
            int skippers = (int)event.getSelfMember().getVoiceState().getChannel().getMembers().stream()
                    .filter(m -> handler.getVotes().contains(m.getUser().getId())).count();
            int required = (int)Math.ceil(listeners * skipRatio);
            msg += skippers + " 票，" + required + "/" + listeners + " 票所需]`";
            
            // 檢查是否達到跳過要求
            if(skippers >= required)
            {
                msg += "\n" + event.getClient().getSuccess() + " 跳過了 **" + handler.getPlayer().getPlayingTrack().getInfo().title
                    + "** " + (rm.getOwner() == 0L ? "(自動播放)" : "(由 **" + FormatUtil.formatUsername(rm.user) + "** 請求)");
                handler.getPlayer().stopTrack(); // 停止播放當前歌曲
            }
            event.reply(msg); // 回覆消息
        }
    }
    
}
