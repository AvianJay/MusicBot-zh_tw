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

import com.jagrosh.jmusicbot.audio.RequestMetadata;
import com.jagrosh.jmusicbot.utils.TimeUtil;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException.Severity;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import java.util.concurrent.TimeUnit;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.menu.OrderedMenu;
import com.jagrosh.jmusicbot.Bot;
import com.jagrosh.jmusicbot.audio.AudioHandler;
import com.jagrosh.jmusicbot.audio.QueuedTrack;
import com.jagrosh.jmusicbot.commands.MusicCommand;
import com.jagrosh.jmusicbot.utils.FormatUtil;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.exceptions.PermissionException;

/**
 *
 * @author John Grosh <john.a.grosh@gmail.com>
 */
public class SearchCmd extends MusicCommand 
{
    protected String searchPrefix = "ytsearch:"; // 搜尋前綴
    private final OrderedMenu.Builder builder;
    private final String searchingEmoji;
    
    public SearchCmd(Bot bot)
    {
        super(bot);
        this.searchingEmoji = bot.getConfig().getSearching(); // 獲取搜尋中的表情符號
        this.name = "search"; // 指令名稱為 search
        this.aliases = bot.getConfig().getAliases(this.name); // 指令的別名
        this.arguments = "<查詢>"; // 指令參數
        this.help = "搜尋 YouTube 上提供的查詢"; // 指令說明
        this.beListening = true; // 需要正在收聽
        this.bePlaying = false; // 不需要正在播放音樂
        this.botPermissions = new Permission[]{Permission.MESSAGE_EMBED_LINKS}; // 機器人所需權限
        builder = new OrderedMenu.Builder()
                .allowTextInput(true) // 允許文字輸入
                .useNumbers() // 使用編號選項
                .useCancelButton(true) // 使用取消按鈕
                .setEventWaiter(bot.getWaiter()) // 設定事件等待器
                .setTimeout(1, TimeUnit.MINUTES); // 設定超時時間為1分鐘
    }
    
    @Override
    public void doCommand(CommandEvent event) 
    {
        if(event.getArgs().isEmpty()) // 如果沒有提供查詢
        {
            event.replyError("請包含一個查詢。"); // 回覆錯誤訊息
            return;
        }
        // 回覆搜尋中訊息，並開始載入項目
        event.reply(searchingEmoji+" 搜尋中... `["+event.getArgs()+"]`", 
                m -> bot.getPlayerManager().loadItemOrdered(event.getGuild(), searchPrefix + event.getArgs(), new ResultHandler(m,event)));
    }
    
    private class ResultHandler implements AudioLoadResultHandler 
    {
        private final Message m;
        private final CommandEvent event;
        
        private ResultHandler(Message m, CommandEvent event)
        {
            this.m = m;
            this.event = event;
        }
        
        @Override
        public void trackLoaded(AudioTrack track)
        {
            if(bot.getConfig().isTooLong(track)) // 如果曲目太長
            {
                m.editMessage(FormatUtil.filter(event.getClient().getWarning()+" 此曲目 (**"+track.getInfo().title+"**) 超過允許的最大長度：`"
                        + TimeUtil.formatTime(track.getDuration())+"` > `"+TimeUtil.formatTime(bot.getConfig().getMaxTime())+"`")).queue();
                return;
            }
            AudioHandler handler = (AudioHandler)event.getGuild().getAudioManager().getSendingHandler(); // 獲取音訊處理器
            int pos = handler.addTrack(new QueuedTrack(track, RequestMetadata.fromResultHandler(track, event)))+1; // 添加曲目到佇列
            m.editMessage(FormatUtil.filter(event.getClient().getSuccess()+" 已添加 **"+track.getInfo().title
                    +"** (`"+ TimeUtil.formatTime(track.getDuration())+"`) "+(pos==0 ? "開始播放"
                        : " 到佇列中的位置 "+pos))).queue(); // 回覆成功訊息
        }

        @Override
        public void playlistLoaded(AudioPlaylist playlist)
        {
            builder.setColor(event.getSelfMember().getColor()) // 設定顏色
                    .setText(FormatUtil.filter(event.getClient().getSuccess()+" 查詢結果 `"+event.getArgs()+"`：")) // 設定文本
                    .setChoices(new String[0]) // 清除選項
                    .setSelection((msg,i) -> 
                    {
                        AudioTrack track = playlist.getTracks().get(i-1); // 獲取選擇的曲目
                        if(bot.getConfig().isTooLong(track)) // 如果曲目太長
                        {
                            event.replyWarning("此曲目 (**"+track.getInfo().title+"**) 超過允許的最大長度：`"
                                    + TimeUtil.formatTime(track.getDuration())+"` > `"+bot.getConfig().getMaxTime()+"`");
                            return;
                        }
                        AudioHandler handler = (AudioHandler)event.getGuild().getAudioManager().getSendingHandler(); // 獲取音訊處理器
                        int pos = handler.addTrack(new QueuedTrack(track, RequestMetadata.fromResultHandler(track, event)))+1; // 添加曲目到佇列
                        event.replySuccess("已添加 **" + FormatUtil.filter(track.getInfo().title)
                                + "** (`" + TimeUtil.formatTime(track.getDuration()) + "`) " + (pos==0 ? "開始播放" 
                                    : " 到佇列中的位置 "+pos)); // 回覆成功訊息
                    })
                    .setCancel((msg) -> {}) // 設定取消動作
                    .setUsers(event.getAuthor()); // 設定用戶
            for(int i=0; i<4 && i<playlist.getTracks().size(); i++) // 迭代前4個曲目
            {
                AudioTrack track = playlist.getTracks().get(i);
                builder.addChoices("`["+ TimeUtil.formatTime(track.getDuration())+"]` [**"+track.getInfo().title+"**]("+track.getInfo().uri+")"); // 添加選項
            }
            builder.build().display(m); // 顯示選單
        }

        @Override
        public void noMatches() 
        {
            m.editMessage(FormatUtil.filter(event.getClient().getWarning()+" 找不到 `"+event.getArgs()+"` 的結果。")).queue(); // 回覆沒有結果的訊息
        }

        @Override
        public void loadFailed(FriendlyException throwable) 
        {
            if(throwable.severity == Severity.COMMON)
                m.editMessage(event.getClient().getError()+" 載入錯誤："+throwable.getMessage()).queue(); // 回覆載入錯誤訊息
            else
                m.editMessage(event.getClient().getError()+" 載入曲目時發生錯誤。").queue(); // 回覆通用載入錯誤訊息
        }
    }
}
