package com.jagrosh.jmusicbot.commands.dj;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jmusicbot.Bot;
import com.jagrosh.jmusicbot.audio.AudioHandler;
import com.jagrosh.jmusicbot.audio.QueuedTrack;
import com.jagrosh.jmusicbot.commands.DJCommand;
import com.jagrosh.jmusicbot.queue.AbstractQueue;

/**
 * 提供用戶移動播放列表中曲目的能力的命令。
 */
public class MoveTrackCmd extends DJCommand
{
    public MoveTrackCmd(Bot bot)
    {
        super(bot);
        this.name = "movetrack";
        this.help = "將當前隊列中的曲目移動到不同位置";
        this.arguments = "<from> <to>";
        this.aliases = bot.getConfig().getAliases(this.name);
        this.bePlaying = true;
    }

    @Override
    public void doCommand(CommandEvent event)
    {
        int from;
        int to;

        String[] parts = event.getArgs().split("\\s+", 2);
        if(parts.length < 2)
        {
            event.replyError("請提供兩個有效的索引。");
            return;
        }

        try
        {
            // 驗證參數
            from = Integer.parseInt(parts[0]);
            to = Integer.parseInt(parts[1]);
        }
        catch (NumberFormatException e)
        {
            event.replyError("請提供兩個有效的索引。");
            return;
        }

        if (from == to)
        {
            event.replyError("無法將曲目移到相同位置。");
            return;
        }

        // 驗證 from 和 to 是否有效
        AudioHandler handler = (AudioHandler) event.getGuild().getAudioManager().getSendingHandler();
        AbstractQueue<QueuedTrack> queue = handler.getQueue();
        if (isUnavailablePosition(queue, from))
        {
            String reply = String.format("`%d` 不是隊列中的有效位置！", from);
            event.replyError(reply);
            return;
        }
        if (isUnavailablePosition(queue, to))
        {
            String reply = String.format("`%d` 不是隊列中的有效位置！", to);
            event.replyError(reply);
            return;
        }

        // 移動曲目
        QueuedTrack track = queue.moveItem(from - 1, to - 1);
        String trackTitle = track.getTrack().getInfo().title;
        String reply = String.format("已將 **%s** 從位置 `%d` 移動到 `%d`。", trackTitle, from, to);
        event.replySuccess(reply);
    }

    private static boolean isUnavailablePosition(AbstractQueue<QueuedTrack> queue, int position)
    {
        return (position < 1 || position > queue.size());
    }
}
