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
package com.jagrosh.jmusicbot.commands.owner;

import java.io.IOException;
import java.util.List;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jmusicbot.Bot;
import com.jagrosh.jmusicbot.commands.OwnerCommand;
import com.jagrosh.jmusicbot.playlist.PlaylistLoader.Playlist;

/**
 *
 * @author John Grosh <john.a.grosh@gmail.com>
 */
public class PlaylistCmd extends OwnerCommand 
{
    private final Bot bot;
    
    public PlaylistCmd(Bot bot)
    {
        this.bot = bot;
        this.guildOnly = false; // 可以在私人頻道使用
        this.name = "playlist"; // 指令名稱
        this.arguments = "<append|delete|make|setdefault>"; // 參數
        this.help = "播放列表管理"; // 指令說明
        this.aliases = bot.getConfig().getAliases(this.name); // 別名
        this.children = new OwnerCommand[]{
            new ListCmd(),
            new AppendlistCmd(),
            new DeletelistCmd(),
            new MakelistCmd(),
            new DefaultlistCmd(bot)
        };
    }

    @Override
    public void execute(CommandEvent event) 
    {
        StringBuilder builder = new StringBuilder(event.getClient().getWarning()+" 播放列表管理指令：\n");
        for(Command cmd: this.children)
            builder.append("\n`").append(event.getClient().getPrefix()).append(name).append(" ").append(cmd.getName())
                    .append(" ").append(cmd.getArguments()==null ? "" : cmd.getArguments()).append("` - ").append(cmd.getHelp());
        event.reply(builder.toString());
    }
    
    public class MakelistCmd extends OwnerCommand 
    {
        public MakelistCmd()
        {
            this.name = "make"; // 指令名稱
            this.aliases = new String[]{"create"}; // 別名
            this.help = "創建新的播放列表"; // 指令說明
            this.arguments = "<name>"; // 參數
            this.guildOnly = false; // 可以在私人頻道使用
        }

        @Override
        protected void execute(CommandEvent event) 
        {
            String pname = event.getArgs().replaceAll("\\s+", "_");
            pname = pname.replaceAll("[*?|\\/\":<>]", ""); // 移除無效字符
            if(pname == null || pname.isEmpty()) 
            {
                event.replyError("請提供播放列表名稱！");
            } 
            else if(bot.getPlaylistLoader().getPlaylist(pname) == null)
            {
                try
                {
                    bot.getPlaylistLoader().createPlaylist(pname); // 創建播放列表
                    event.reply(event.getClient().getSuccess()+" 成功創建播放列表 `"+pname+"`！");
                }
                catch(IOException e)
                {
                    event.reply(event.getClient().getError()+" 我無法創建播放列表: "+e.getLocalizedMessage());
                }
            }
            else
                event.reply(event.getClient().getError()+" 播放列表 `"+pname+"` 已存在！");
        }
    }
    
    public class DeletelistCmd extends OwnerCommand 
    {
        public DeletelistCmd()
        {
            this.name = "delete"; // 指令名稱
            this.aliases = new String[]{"remove"}; // 別名
            this.help = "刪除現有的播放列表"; // 指令說明
            this.arguments = "<name>"; // 參數
            this.guildOnly = false; // 可以在私人頻道使用
        }

        @Override
        protected void execute(CommandEvent event) 
        {
            String pname = event.getArgs().replaceAll("\\s+", "_");
            if(bot.getPlaylistLoader().getPlaylist(pname)==null)
                event.reply(event.getClient().getError()+" 播放列表 `"+pname+"` 不存在！");
            else
            {
                try
                {
                    bot.getPlaylistLoader().deletePlaylist(pname); // 刪除播放列表
                    event.reply(event.getClient().getSuccess()+" 成功刪除播放列表 `"+pname+"`！");
                }
                catch(IOException e)
                {
                    event.reply(event.getClient().getError()+" 我無法刪除播放列表: "+e.getLocalizedMessage());
                }
            }
        }
    }
    
    public class AppendlistCmd extends OwnerCommand 
    {
        public AppendlistCmd()
        {
            this.name = "append"; // 指令名稱
            this.aliases = new String[]{"add"}; // 別名
            this.help = "向現有播放列表添加歌曲"; // 指令說明
            this.arguments = "<name> <URL> | <URL> | ..."; // 參數
            this.guildOnly = false; // 可以在私人頻道使用
        }

        @Override
        protected void execute(CommandEvent event) 
        {
            String[] parts = event.getArgs().split("\\s+", 2);
            if(parts.length<2)
            {
                event.reply(event.getClient().getError()+" 請包含播放列表名稱和要添加的 URL！");
                return;
            }
            String pname = parts[0];
            Playlist playlist = bot.getPlaylistLoader().getPlaylist(pname);
            if(playlist==null)
                event.reply(event.getClient().getError()+" 播放列表 `"+pname+"` 不存在！");
            else
            {
                StringBuilder builder = new StringBuilder();
                playlist.getItems().forEach(item -> builder.append("\r\n").append(item));
                String[] urls = parts[1].split("\\|");
                for(String url: urls)
                {
                    String u = url.trim();
                    if(u.startsWith("<") && u.endsWith(">"))
                        u = u.substring(1, u.length()-1); // 移除尖括號
                    builder.append("\r\n").append(u);
                }
                try
                {
                    bot.getPlaylistLoader().writePlaylist(pname, builder.toString()); // 將 URL 添加到播放列表
                    event.reply(event.getClient().getSuccess()+" 成功添加 "+urls.length+" 項到播放列表 `"+pname+"`！");
                }
                catch(IOException e)
                {
                    event.reply(event.getClient().getError()+" 我無法追加到播放列表: "+e.getLocalizedMessage());
                }
            }
        }
    }
    
    public class DefaultlistCmd extends AutoplaylistCmd 
    {
        public DefaultlistCmd(Bot bot)
        {
            super(bot);
            this.name = "setdefault"; // 指令名稱
            this.aliases = new String[]{"default"}; // 別名
            this.arguments = "<playlistname|NONE>"; // 參數
            this.guildOnly = true; // 僅限伺服器
        }
    }
    
    public class ListCmd extends OwnerCommand 
    {
        public ListCmd()
        {
            this.name = "all"; // 指令名稱
            this.aliases = new String[]{"available","list"}; // 別名
            this.help = "列出所有可用的播放列表"; // 指令說明
            this.guildOnly = true; // 僅限伺服器
        }

        @Override
        protected void execute(CommandEvent event) 
        {
            if(!bot.getPlaylistLoader().folderExists())
                bot.getPlaylistLoader().createFolder(); // 創建播放列表文件夾
            if(!bot.getPlaylistLoader().folderExists())
            {
                event.reply(event.getClient().getWarning()+" 播放列表文件夾不存在且無法創建！");
                return;
            }
            List<String> list = bot.getPlaylistLoader().getPlaylistNames(); // 獲取播放列表名稱
            if(list==null)
                event.reply(event.getClient().getError()+" 無法加載可用的播放列表！");
            else if(list.isEmpty())
                event.reply(event.getClient().getWarning()+" 播放列表文件夾中沒有播放列表！");
            else
            {
                StringBuilder builder = new StringBuilder(event.getClient().getSuccess()+" 可用的播放列表：\n");
                list.forEach(str -> builder.append("`").append(str).append("` "));
                event.reply(builder.toString());
            }
        }
    }
}
