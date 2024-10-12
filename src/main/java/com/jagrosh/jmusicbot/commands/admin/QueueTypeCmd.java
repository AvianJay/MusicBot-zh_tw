/*
 * 版權所有 2022 John Grosh <john.a.grosh@gmail.com>.
 *
 * 根據 Apache 許可證 2.0 版（"許可證"）授權；
 * 除非遵守許可證，否則你不能使用此檔案。
 * 你可以在以下網址獲得許可證副本：
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * 除非適用的法律要求或書面同意，
 * 根據許可證分發的軟體是在 "原樣" 基礎上提供的，
 * 不附帶任何形式的明示或默示擔保或條件。
 * 有關許可證下具體語言的權限和限制，請參見許可證。
 */
package com.jagrosh.jmusicbot.commands.admin;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jmusicbot.Bot;
import com.jagrosh.jmusicbot.audio.AudioHandler;
import com.jagrosh.jmusicbot.commands.AdminCommand;
import com.jagrosh.jmusicbot.settings.QueueType;
import com.jagrosh.jmusicbot.settings.Settings;

/**
 *
 * @author Wolfgang Schwendtbauer
 */
public class QueueTypeCmd extends AdminCommand
{
    public QueueTypeCmd(Bot bot)
    {
        super();
        this.name = "queuetype";
        this.help = "更改隊列類型";
        this.arguments = "[" + String.join("|", QueueType.getNames()) + "]";
        this.aliases = bot.getConfig().getAliases(this.name);
    }

    @Override
    protected void execute(CommandEvent event)
    {
        String args = event.getArgs();
        QueueType value;
        Settings settings = event.getClient().getSettingsFor(event.getGuild());

        if (args.isEmpty())
        {
            QueueType currentType = settings.getQueueType();
            event.reply(currentType.getEmoji() + " 當前的隊列類型是: `" + currentType.getUserFriendlyName() + "`.");
            return;
        }

        try
        {
            value = QueueType.valueOf(args.toUpperCase());
        }
        catch (IllegalArgumentException e)
        {
            event.replyError("無效的隊列類型。有效類型是: [" + String.join("|", QueueType.getNames()) + "]");
            return;
        }

        if (settings.getQueueType() != value)
        {
            settings.setQueueType(value);

            AudioHandler handler = (AudioHandler) event.getGuild().getAudioManager().getSendingHandler();
            if (handler != null)
                handler.setQueueType(value);
        }

        event.reply(value.getEmoji() + " 隊列類型已設置為 `" + value.getUserFriendlyName() + "`.");
    }
}
