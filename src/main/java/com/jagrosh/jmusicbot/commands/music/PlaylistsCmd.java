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
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jmusicbot.Bot;
import com.jagrosh.jmusicbot.commands.MusicCommand;

/**
 *
 * @author John Grosh <john.a.grosh@gmail.com>
 */
public class PlaylistsCmd extends MusicCommand 
{
    public PlaylistsCmd(Bot bot)
    {
        super(bot);
        this.name = "playlists"; // 指令名稱為 playlists
        this.help = "顯示可用的播放清單"; // 指令說明
        this.aliases = bot.getConfig().getAliases(this.name); // 指令的別名
        this.guildOnly = true; // 只能在伺服器中使用
        this.beListening = false; // 不需要在語音頻道內
        this.bePlaying = false; // 不需要正在播放音樂
    }
    
    @Override
    public void doCommand(CommandEvent event) 
    {
        // 如果播放清單資料夾不存在，則嘗試創建它
        if(!bot.getPlaylistLoader().folderExists())
            bot.getPlaylistLoader().createFolder();
        
        // 如果資料夾仍然不存在，回傳錯誤訊息
        if(!bot.getPlaylistLoader().folderExists())
        {
            event.reply(event.getClient().getWarning()+" 播放清單資料夾不存在且無法創建！");
            return;
        }
        
        // 獲取播放清單名稱列表
        List<String> list = bot.getPlaylistLoader().getPlaylistNames();
        
        // 如果無法載入，回傳錯誤訊息
        if(list == null)
            event.reply(event.getClient().getError()+" 載入可用的播放清單失敗！");
        // 如果播放清單是空的，回傳警告訊息
        else if(list.isEmpty())
            event.reply(event.getClient().getWarning()+" 播放清單資料夾內沒有任何播放清單！");
        else
        {
            // 建立並回傳可用播放清單的訊息
            StringBuilder builder = new StringBuilder(event.getClient().getSuccess()+" 可用的播放清單:\n");
            list.forEach(str -> builder.append("`").append(str).append("` "));
            builder.append("\n輸入 `").append(event.getClient().getTextualPrefix()).append("play playlist <名稱>` 來播放播放清單");
            event.reply(builder.toString());
        }
    }
}
