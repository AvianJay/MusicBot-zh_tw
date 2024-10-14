/*
 * Copyright 2020 John Grosh <john.a.grosh@gmail.com>.
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
import com.jagrosh.jmusicbot.commands.DJCommand;
import com.jagrosh.jmusicbot.commands.MusicCommand;
import com.jagrosh.jmusicbot.utils.TimeUtil;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author Whew., Inc.
 */
public class SeekCmd extends MusicCommand
{
    private final static Logger LOG = LoggerFactory.getLogger("Seeking");
    
    public SeekCmd(Bot bot)
    {
        super(bot);
        this.name = "seek"; // 指令名稱
        this.help = "調整當前播放歌曲的進度"; // 指令說明
        this.arguments = "[+ | -] <HH:MM:SS | MM:SS | SS>|<0h0m0s | 0m0s | 0s>"; // 參數格式
        this.aliases = bot.getConfig().getAliases(this.name); // 別名
        this.beListening = true; // 需要正在收聽
        this.bePlaying = true; // 需要正在播放音樂
    }

    @Override
    public void doCommand(CommandEvent event)
    {
        AudioHandler handler = (AudioHandler) event.getGuild().getAudioManager().getSendingHandler(); // 獲取音訊處理器
        AudioTrack playingTrack = handler.getPlayer().getPlayingTrack(); // 獲取正在播放的曲目
        if (!playingTrack.isSeekable()) // 檢查曲目是否可調整進度
        {
            event.replyError("此曲目無法調整進度。");
            return;
        }

        // 檢查用戶是否有權調整此曲目的進度
        if (!DJCommand.checkDJPermission(event) && playingTrack.getUserData(RequestMetadata.class).getOwner() != event.getAuthor().getIdLong())
        {
            event.replyError("你無法調整 **" + playingTrack.getInfo().title + "** 的進度，因為不是你添加的！");
            return;
        }

        String args = event.getArgs(); // 獲取參數
        TimeUtil.SeekTime seekTime = TimeUtil.parseTime(args); // 解析時間格式
        if (seekTime == null) // 如果格式無效
        {
            event.replyError("無效的調整進度！預期格式：" + arguments + "\n範例：`1:02:23` `+1:10` `-90`, `1h10m`, `+90s`");
            return;
        }

        long currentPosition = playingTrack.getPosition(); // 獲取當前播放位置
        long trackDuration = playingTrack.getDuration(); // 獲取曲目總時長

        long seekMilliseconds = seekTime.relative ? currentPosition + seekTime.milliseconds : seekTime.milliseconds; // 計算調整後的位置
        if (seekMilliseconds > trackDuration) // 檢查是否超過曲目時長
        {
            event.replyError("無法調整至 `" + TimeUtil.formatTime(seekMilliseconds) + "`，因為當前曲目的長度為 `" + TimeUtil.formatTime(trackDuration) + "`！");
            return;
        }
        
        try
        {
            playingTrack.setPosition(seekMilliseconds); // 調整曲目位置
        }
        catch (Exception e)
        {
            event.replyError("調整進度時發生錯誤：" + e.getMessage());
            LOG.warn("無法調整曲目 " + playingTrack.getIdentifier(), e);
            return;
        }
        event.replySuccess("成功調整至 `" + TimeUtil.formatTime(playingTrack.getPosition()) + "/" + TimeUtil.formatTime(playingTrack.getDuration()) + "`！");
    }
}
