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
package com.jagrosh.jmusicbot.commands.music;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jmusicbot.Bot;
import com.jagrosh.jmusicbot.audio.AudioHandler;
import com.jagrosh.jmusicbot.commands.MusicCommand;

/**
 *
 * @author John Grosh <john.a.grosh@gmail.com>
 */
public class ShuffleCmd extends MusicCommand 
{
    public ShuffleCmd(Bot bot)
    {
        super(bot);
        this.name = "shuffle"; // 指令名稱
        this.help = "隨機播放你添加的歌曲"; // 指令說明
        this.aliases = bot.getConfig().getAliases(this.name); // 別名
        this.beListening = true; // 需要正在收聽
        this.bePlaying = true; // 需要正在播放音樂
    }

    @Override
    public void doCommand(CommandEvent event) 
    {
        AudioHandler handler = (AudioHandler)event.getGuild().getAudioManager().getSendingHandler(); // 獲取音訊處理器
        int s = handler.getQueue().shuffle(event.getAuthor().getIdLong()); // 隨機播放隊列中的歌曲
        switch (s) 
        {
            case 0:
                event.replyError("你沒有任何音樂在隊列中以進行隨機播放！");
                break;
            case 1:
                event.replyWarning("你在隊列中只有一首歌！");
                break;
            default:
                event.replySuccess("你成功隨機播放了你的 " + s + " 首歌曲。");
                break;
        }
    }
}
