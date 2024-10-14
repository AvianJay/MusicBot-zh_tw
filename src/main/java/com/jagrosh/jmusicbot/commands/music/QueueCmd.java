/*
 * 版權所有 2018 John Grosh <john.a.grosh@gmail.com>.
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

import java.util.List;
import java.util.concurrent.TimeUnit;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.menu.Paginator;
import com.jagrosh.jmusicbot.Bot;
import com.jagrosh.jmusicbot.audio.AudioHandler;
import com.jagrosh.jmusicbot.audio.QueuedTrack;
import com.jagrosh.jmusicbot.commands.MusicCommand;
import com.jagrosh.jmusicbot.settings.QueueType;
import com.jagrosh.jmusicbot.settings.RepeatMode;
import com.jagrosh.jmusicbot.settings.Settings;
import com.jagrosh.jmusicbot.utils.FormatUtil;
import com.jagrosh.jmusicbot.utils.TimeUtil;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.exceptions.PermissionException;

/**
 *
 * @author John Grosh <john.a.grosh@gmail.com>
 */
public class QueueCmd extends MusicCommand 
{
    private final Paginator.Builder builder;
    
    public QueueCmd(Bot bot)
    {
        super(bot);
        this.name = "queue"; // 指令名稱為 queue
        this.help = "顯示當前的播放佇列"; // 指令說明
        this.arguments = "[頁碼]"; // 指令參數
        this.aliases = bot.getConfig().getAliases(this.name); // 指令的別名
        this.bePlaying = true; // 需要正在播放音樂
        this.botPermissions = new Permission[]{Permission.MESSAGE_ADD_REACTION, Permission.MESSAGE_EMBED_LINKS}; // 機器人所需權限
        builder = new Paginator.Builder()
                .setColumns(1) // 每頁顯示一列
                .setFinalAction(m -> {try{m.clearReactions().queue();}catch(PermissionException ignore){}}) // 最後移除反應
                .setItemsPerPage(10) // 每頁顯示10個項目
                .waitOnSinglePage(false) // 單頁結果時不等待
                .useNumberedItems(true) // 使用編號項目
                .showPageNumbers(true) // 顯示頁碼
                .wrapPageEnds(true) // 允許翻回第一頁
                .setEventWaiter(bot.getWaiter()) // 設定事件等待器
                .setTimeout(1, TimeUnit.MINUTES); // 設置超時時間為 1 分鐘
    }

    @Override
    public void doCommand(CommandEvent event)
    {
        int pagenum = 1;
        try
        {
            pagenum = Integer.parseInt(event.getArgs()); // 嘗試解析頁碼
        }
        catch(NumberFormatException ignore){} // 無效輸入則默認為第1頁
        AudioHandler ah = (AudioHandler)event.getGuild().getAudioManager().getSendingHandler(); // 獲取音訊管理器
        List<QueuedTrack> list = ah.getQueue().getList(); // 獲取播放佇列
        if(list.isEmpty()) // 如果播放佇列是空的
        {
            Message nowp = ah.getNowPlaying(event.getJDA()); // 獲取當前播放的音樂
            Message nonowp = ah.getNoMusicPlaying(event.getJDA()); // 沒有播放音樂的訊息
            Message built = new MessageBuilder()
                    .setContent(event.getClient().getWarning() + " 播放佇列中沒有任何音樂！")
                    .setEmbeds((nowp == null ? nonowp : nowp).getEmbeds().get(0)).build(); // 建立回應訊息
            event.reply(built, m -> 
            {
                if(nowp != null)
                    bot.getNowplayingHandler().setLastNPMessage(m); // 更新「正在播放」訊息
            });
            return;
        }
        String[] songs = new String[list.size()]; // 儲存播放佇列的歌曲
        long total = 0; // 總播放時間
        for(int i = 0; i < list.size(); i++)
        {
            total += list.get(i).getTrack().getDuration(); // 計算總時長
            songs[i] = list.get(i).toString(); // 儲存每首歌的資訊
        }
        Settings settings = event.getClient().getSettingsFor(event.getGuild()); // 獲取伺服器設置
        long fintotal = total;
        builder.setText((i1, i2) -> getQueueTitle(ah, event.getClient().getSuccess(), songs.length, fintotal, settings.getRepeatMode(), settings.getQueueType())) // 設置佇列標題
                .setItems(songs) // 設定歌曲列表
                .setUsers(event.getAuthor()) // 設定用戶
                .setColor(event.getSelfMember().getColor()); // 設定顏色
        builder.build().paginate(event.getChannel(), pagenum); // 顯示分頁
    }
    
    private String getQueueTitle(AudioHandler ah, String success, int songslength, long total, RepeatMode repeatmode, QueueType queueType)
    {
        StringBuilder sb = new StringBuilder();
        if(ah.getPlayer().getPlayingTrack() != null) // 如果有正在播放的歌曲
        {
            sb.append(ah.getStatusEmoji()).append(" **")
                    .append(ah.getPlayer().getPlayingTrack().getInfo().title).append("**\n"); // 顯示正在播放的歌曲
        }
        return FormatUtil.filter(sb.append(success).append(" 當前播放佇列 | ").append(songslength)
                .append(" 首歌曲 | `").append(TimeUtil.formatTime(total)).append("` ")
                .append("| ").append(queueType.getEmoji()).append(" `").append(queueType.getUserFriendlyName()).append('`')
                .append(repeatmode.getEmoji() != null ? " | "+repeatmode.getEmoji() : "").toString()); // 顯示佇列資訊
    }
}
