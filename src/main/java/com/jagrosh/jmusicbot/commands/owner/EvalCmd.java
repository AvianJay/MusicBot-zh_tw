/*
 * Copyright 2016 John Grosh (jagrosh).
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

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jmusicbot.Bot;
import com.jagrosh.jmusicbot.commands.OwnerCommand;
import net.dv8tion.jda.api.entities.ChannelType;

/**
 *
 * @author John Grosh (jagrosh)
 */
public class EvalCmd extends OwnerCommand 
{
    private final Bot bot;
    private final String engine;
    
    public EvalCmd(Bot bot)
    {
        this.bot = bot;
        this.name = "eval"; // 指令名稱
        this.help = "評估 nashorn 代碼"; // 指令說明
        this.aliases = bot.getConfig().getAliases(this.name); // 別名
        this.engine = bot.getConfig().getEvalEngine(); // 獲取評估引擎
        this.guildOnly = false; // 可以在私人頻道使用
    }
    
    @Override
    protected void execute(CommandEvent event) 
    {
        ScriptEngine se = new ScriptEngineManager().getEngineByName(engine); // 獲取腳本引擎
        if(se == null)
        {
            event.replyError("配置中提供的 eval 引擎 (`"+engine+"`) 不存在。這可能是由於引擎名稱無效，或引擎在您的 Java 版本中不存在 (`"+System.getProperty("java.version")+"`)。");
            return;
        }
        se.put("bot", bot); // 將 bot 放入引擎上下文
        se.put("event", event); // 將事件放入引擎上下文
        se.put("jda", event.getJDA()); // 將 JDA 放入引擎上下文
        if (event.getChannelType() != ChannelType.PRIVATE) {
            se.put("guild", event.getGuild()); // 將伺服器放入引擎上下文
            se.put("channel", event.getChannel()); // 將頻道放入引擎上下文
        }
        try
        {
            event.reply(event.getClient().getSuccess()+" 成功評估:\n```\n"+se.eval(event.getArgs())+" ```");
        } 
        catch(Exception e)
        {
            event.reply(event.getClient().getError()+" 發生異常:\n```\n"+e+" ```");
        }
    }
    
}
