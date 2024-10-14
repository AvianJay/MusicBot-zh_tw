/*
 * 版權所有 2016 John Grosh <john.a.grosh@gmail.com>.
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
import com.jagrosh.jmusicbot.audio.QueuedTrack;
import com.jagrosh.jmusicbot.commands.MusicCommand;
import com.jagrosh.jmusicbot.settings.Settings;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.User;

/**
 *
 * @author John Grosh <john.a.grosh@gmail.com>
 */
public class RemoveCmd extends MusicCommand 
{
    public RemoveCmd(Bot bot)
    {
        super(bot);
        this.name = "remove"; // 指令名稱為 remove
        this.help = "移除播放佇列中的歌曲"; // 指令說明
        this.arguments = "<位置|全部>"; // 指令參數
        this.aliases = bot.getConfig().getAliases(this.name); // 指令的別名
        this.beListening = true; // 需要正在收聽
        this.bePlaying = true; // 需要正在播放音樂
    }

    @Override
    public void doCommand(CommandEvent event) 
    {
        AudioHandler handler = (AudioHandler)event.getGuild().getAudioManager().getSendingHandler(); // 獲取音訊處理器
        if(handler.getQueue().isEmpty()) // 如果播放佇列是空的
        {
            event.replyError("播放佇列中沒有任何音樂！"); // 提示用戶播放佇列是空的
            return;
        }
        if(event.getArgs().equalsIgnoreCase("all")) // 如果參數為 "all"
        {
            int count = handler.getQueue().removeAll(event.getAuthor().getIdLong()); // 移除所有該用戶添加的歌曲
            if(count == 0)
                event.replyWarning("您沒有任何歌曲在播放佇列中！"); // 如果沒有移除任何歌曲，發送警告
            else
                event.replySuccess("成功移除了您的 "+count+" 首歌曲。"); // 成功移除歌曲
            return;
        }
        int pos;
        try {
            pos = Integer.parseInt(event.getArgs()); // 嘗試將參數解析為整數
        } catch(NumberFormatException e) {
            pos = 0; // 如果解析失敗，設為 0
        }
        if(pos < 1 || pos > handler.getQueue().size()) // 如果位置無效
        {
            event.replyError("位置必須是 1 到 "+handler.getQueue().size()+" 之間的有效整數！"); // 提示用戶位置無效
            return;
        }
        Settings settings = event.getClient().getSettingsFor(event.getGuild()); // 獲取伺服器設置
        boolean isDJ = event.getMember().hasPermission(Permission.MANAGE_SERVER); // 檢查用戶是否有管理伺服器權限
        if(!isDJ)
            isDJ = event.getMember().getRoles().contains(settings.getRole(event.getGuild())); // 檢查用戶是否具有DJ角色
        QueuedTrack qt = handler.getQueue().get(pos - 1); // 獲取指定位置的歌曲
        if(qt.getIdentifier() == event.getAuthor().getIdLong()) // 如果是用戶自己添加的歌曲
        {
            handler.getQueue().remove(pos - 1); // 移除該歌曲
            event.replySuccess("已從佇列中移除 **" + qt.getTrack().getInfo().title + "**"); // 提示用戶成功移除歌曲
        }
        else if(isDJ) // 如果用戶是DJ
        {
            handler.getQueue().remove(pos - 1); // 移除該歌曲
            User u;
            try {
                u = event.getJDA().getUserById(qt.getIdentifier()); // 獲取添加該歌曲的用戶
            } catch(Exception e) {
                u = null; // 如果無法獲取，用 null 表示
            }
            event.replySuccess("已從佇列中移除 **" + qt.getTrack().getInfo().title
                    + "** (由 "+(u == null ? "某人" : "**" + u.getName() + "**") + " 添加)"); // 提示DJ成功移除歌曲並顯示添加者
        }
        else // 如果用戶無權移除
        {
            event.replyError("您無法移除 **" + qt.getTrack().getInfo().title + "**，因為這不是您添加的！"); // 提示用戶無權移除該歌曲
        }
    }
}
