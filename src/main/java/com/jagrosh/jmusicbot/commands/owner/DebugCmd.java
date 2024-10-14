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
import com.jagrosh.jdautilities.commons.JDAUtilitiesInfo;
import com.jagrosh.jmusicbot.Bot;
import com.jagrosh.jmusicbot.commands.OwnerCommand;
import com.jagrosh.jmusicbot.utils.OtherUtil;
import com.sedmelluq.discord.lavaplayer.tools.PlayerLibrary;
import net.dv8tion.jda.api.JDAInfo;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.ChannelType;

/**
 *
 * @author John Grosh (john.a.grosh@gmail.com)
 */
public class DebugCmd extends OwnerCommand 
{
    private final static String[] PROPERTIES = {"java.version", "java.vm.name", "java.vm.specification.version", 
        "java.runtime.name", "java.runtime.version", "java.specification.version",  "os.arch", "os.name"};
    
    private final Bot bot;
    
    public DebugCmd(Bot bot)
    {
        this.bot = bot;
        this.name = "debug"; // 指令名稱
        this.help = "顯示除錯資訊"; // 指令說明
        this.aliases = bot.getConfig().getAliases(this.name); // 別名
        this.guildOnly = false; // 可以在私人頻道使用
    }

    @Override
    protected void execute(CommandEvent event)
    {
        StringBuilder sb = new StringBuilder();
        sb.append("```\n系統屬性:");
        for(String key: PROPERTIES)
            sb.append("\n  ").append(key).append(" = ").append(System.getProperty(key)); // 列出系統屬性
        sb.append("\n\nJMusicBot 資訊:")
                .append("\n  版本 = ").append(OtherUtil.getCurrentVersion())
                .append("\n  擁有者 = ").append(bot.getConfig().getOwnerId())
                .append("\n  前綴 = ").append(bot.getConfig().getPrefix())
                .append("\n  替代前綴 = ").append(bot.getConfig().getAltPrefix())
                .append("\n  最大秒數 = ").append(bot.getConfig().getMaxSeconds())
                .append("\n  NPImages = ").append(bot.getConfig().useNPImages())
                .append("\n  SongInStatus = ").append(bot.getConfig().getSongInStatus())
                .append("\n  StayInChannel = ").append(bot.getConfig().getStay())
                .append("\n  UseEval = ").append(bot.getConfig().useEval())
                .append("\n  UpdateAlerts = ").append(bot.getConfig().useUpdateAlerts());
        sb.append("\n\n依賴資訊:")
                .append("\n  JDA 版本 = ").append(JDAInfo.VERSION)
                .append("\n  JDA-Utilities 版本 = ").append(JDAUtilitiesInfo.VERSION)
                .append("\n  Lavaplayer 版本 = ").append(PlayerLibrary.VERSION);
        long total = Runtime.getRuntime().totalMemory() / 1024 / 1024; // 總記憶體
        long used = total - (Runtime.getRuntime().freeMemory() / 1024 / 1024); // 已使用的記憶體
        sb.append("\n\n運行時資訊:")
                .append("\n  總記憶體 = ").append(total)
                .append("\n  已使用記憶體 = ").append(used);
        sb.append("\n\nDiscord 資訊:")
                .append("\n  ID = ").append(event.getJDA().getSelfUser().getId())
                .append("\n  伺服器數量 = ").append(event.getJDA().getGuildCache().size())
                .append("\n  用戶數量 = ").append(event.getJDA().getUserCache().size());
        sb.append("\n```");
        
        // 檢查是否在私人頻道或是否有權限上傳檔案
        if(event.isFromType(ChannelType.PRIVATE) 
                || event.getSelfMember().hasPermission(event.getTextChannel(), Permission.MESSAGE_ATTACH_FILES))
            event.getChannel().sendFile(sb.toString().getBytes(), "debug_information.txt").queue(); // 發送檔案
        else
            event.reply("除錯資訊: " + sb.toString()); // 直接回覆除錯資訊
    }
}
