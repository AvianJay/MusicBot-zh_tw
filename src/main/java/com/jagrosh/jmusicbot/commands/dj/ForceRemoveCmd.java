/*
 * 版權所有 2019 John Grosh <john.a.grosh@gmail.com>.
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
package com.jagrosh.jmusicbot.commands.dj;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.commons.utils.FinderUtil;
import com.jagrosh.jdautilities.menu.OrderedMenu;
import com.jagrosh.jmusicbot.Bot;
import com.jagrosh.jmusicbot.audio.AudioHandler;
import com.jagrosh.jmusicbot.commands.DJCommand;
import com.jagrosh.jmusicbot.utils.FormatUtil;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author Michaili K.
 */
public class ForceRemoveCmd extends DJCommand
{
    public ForceRemoveCmd(Bot bot)
    {
        super(bot);
        this.name = "forceremove";
        this.help = "從佇列中刪除用戶的所有條目";
        this.arguments = "<user>";
        this.aliases = bot.getConfig().getAliases(this.name);
        this.beListening = false;
        this.bePlaying = true;
        this.botPermissions = new Permission[]{Permission.MESSAGE_EMBED_LINKS};
    }

    @Override
    public void doCommand(CommandEvent event)
    {
        if (event.getArgs().isEmpty())
        {
            event.replyError("你需要提及一位用戶！");
            return;
        }

        AudioHandler handler = (AudioHandler) event.getGuild().getAudioManager().getSendingHandler();
        if (handler.getQueue().isEmpty())
        {
            event.replyError("佇列中沒有任何內容！");
            return;
        }

        User target;
        List<Member> found = FinderUtil.findMembers(event.getArgs(), event.getGuild());

        if(found.isEmpty())
        {
            event.replyError("無法找到該用戶！");
            return;
        }
        else if(found.size() > 1)
        {
            OrderedMenu.Builder builder = new OrderedMenu.Builder();
            for(int i = 0; i < found.size() && i < 4; i++)
            {
                Member member = found.get(i);
                builder.addChoice("**" + member.getUser().getName() + "**#" + member.getUser().getDiscriminator());
            }

            builder
                .setSelection((msg, i) -> removeAllEntries(found.get(i - 1).getUser(), event))
                .setText("找到多位用戶：")
                .setColor(event.getSelfMember().getColor())
                .useNumbers()
                .setUsers(event.getAuthor())
                .useCancelButton(true)
                .setCancel((msg) -> {})
                .setEventWaiter(bot.getWaiter())
                .setTimeout(1, TimeUnit.MINUTES)
                .build().display(event.getChannel());

            return;
        }
        else
        {
            target = found.get(0).getUser();
        }

        removeAllEntries(target, event);
    }

    private void removeAllEntries(User target, CommandEvent event)
    {
        int count = ((AudioHandler) event.getGuild().getAudioManager().getSendingHandler()).getQueue().removeAll(target.getIdLong());
        if (count == 0)
        {
            event.replyWarning("**" + target.getName() + "** 在佇列中沒有任何歌曲！");
        }
        else
        {
            event.replySuccess("成功從 " + FormatUtil.formatUsername(target) + " 中刪除 `" + count + "` 條目。");
        }
    }
}
