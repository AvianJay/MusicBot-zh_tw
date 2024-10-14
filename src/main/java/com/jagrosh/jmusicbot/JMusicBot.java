/*
 * 版權所有 2016 John Grosh (jagrosh)
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
package com.jagrosh.jmusicbot;

import com.jagrosh.jdautilities.command.CommandClient;
import com.jagrosh.jdautilities.command.CommandClientBuilder;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.jagrosh.jdautilities.examples.command.*;
import com.jagrosh.jmusicbot.commands.admin.*;
import com.jagrosh.jmusicbot.commands.dj.*;
import com.jagrosh.jmusicbot.commands.general.*;
import com.jagrosh.jmusicbot.commands.music.*;
import com.jagrosh.jmusicbot.commands.owner.*;
import com.jagrosh.jmusicbot.entities.Prompt;
import com.jagrosh.jmusicbot.gui.GUI;
import com.jagrosh.jmusicbot.settings.SettingsManager;
import com.jagrosh.jmusicbot.utils.OtherUtil;
import java.awt.Color;
import java.util.Arrays;
import javax.security.auth.login.LoginException;
import net.dv8tion.jda.api.*;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ch.qos.logback.classic.Level;

/**
 *
 * @author John Grosh (jagrosh)
 */
public class JMusicBot 
{
    public final static Logger LOG = LoggerFactory.getLogger(JMusicBot.class);
    public final static Permission[] RECOMMENDED_PERMS = {Permission.MESSAGE_READ, Permission.MESSAGE_WRITE, Permission.MESSAGE_HISTORY, Permission.MESSAGE_ADD_REACTION,
                                Permission.MESSAGE_EMBED_LINKS, Permission.MESSAGE_ATTACH_FILES, Permission.MESSAGE_MANAGE, Permission.MESSAGE_EXT_EMOJI,
                                Permission.VOICE_CONNECT, Permission.VOICE_SPEAK, Permission.NICKNAME_CHANGE};
    public final static GatewayIntent[] INTENTS = {GatewayIntent.DIRECT_MESSAGES, GatewayIntent.GUILD_MESSAGES, GatewayIntent.GUILD_MESSAGE_REACTIONS, GatewayIntent.GUILD_VOICE_STATES};
    
    /**
     * @param args 命令行參數
     */
    public static void main(String[] args)
    {
        if(args.length > 0)
            switch(args[0].toLowerCase())
            {
                case "generate-config":
                    BotConfig.writeDefaultConfig();
                    return;
                default:
            }
        startBot();
    }
    
    private static void startBot()
    {
        // 創建提示以處理啟動
        Prompt prompt = new Prompt("JMusicBot");
        
        // 啟動檢查
        OtherUtil.checkVersion(prompt);
        OtherUtil.checkJavaVersion(prompt);
        
        // 載入配置
        BotConfig config = new BotConfig(prompt);
        config.load();
        if(!config.isValid())
            return;
        LOG.info("從 " + config.getConfigLocation() + " 載入配置");

        // 根據配置設置日誌級別
        ((ch.qos.logback.classic.Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME)).setLevel(
                Level.toLevel(config.getLogLevel(), Level.INFO));
        
        // 設置監聽器
        EventWaiter waiter = new EventWaiter();
        SettingsManager settings = new SettingsManager();
        Bot bot = new Bot(waiter, config, settings);
        CommandClient client = createCommandClient(config, settings, bot);
        
        
        if(!prompt.isNoGUI())
        {
            try 
            {
                GUI gui = new GUI(bot);
                bot.setGUI(gui);
                gui.init();

                LOG.info("從 " + config.getConfigLocation() + " 載入配置");
            }
            catch(Exception e)
            {
                LOG.error("無法啟動 GUI。如果您在伺服器上運行或在無法顯示窗口的地方，請使用 -Dnogui=true 標誌以無 GUI 模式運行。");
            }
        }
        
        // 嘗試登錄並啟動
        try
        {
            JDA jda = JDABuilder.create(config.getToken(), Arrays.asList(INTENTS))
                    .enableCache(CacheFlag.MEMBER_OVERRIDES, CacheFlag.VOICE_STATE)
                    .disableCache(CacheFlag.ACTIVITY, CacheFlag.CLIENT_STATUS, CacheFlag.EMOTE, CacheFlag.ONLINE_STATUS)
                    .setActivity(config.isGameNone() ? null : Activity.playing("載入中..."))
                    .setStatus(config.getStatus()==OnlineStatus.INVISIBLE || config.getStatus()==OnlineStatus.OFFLINE 
                            ? OnlineStatus.INVISIBLE : OnlineStatus.DO_NOT_DISTURB)
                    .addEventListeners(client, waiter, new Listener(bot))
                    .setBulkDeleteSplittingEnabled(true)
                    .build();
            bot.setJDA(jda);

            // No no no it is very bad because i stucked at turning off public bot
            // 檢查當前啟動是否不受支持
            /*String unsupportedReason = OtherUtil.getUnsupportedBotReason(jda);
            if (unsupportedReason != null)
            {
                prompt.alert(Prompt.Level.ERROR, "JMusicBot", "JMusicBot 無法在此 Discord 機器人上運行: " + unsupportedReason);
                try{ Thread.sleep(5000);}catch(InterruptedException ignored){} // 這很糟糕，但直到我們有更好的方法...
                jda.shutdown();
                System.exit(1);
            }*/
            
            // 其他檢查，現在只是警告，但將來可能會需要
            // 檢查用戶是否已更改前綴並提供有關
            // 消息內容意圖的信息
            if(!"@mention".equals(config.getPrefix()))
            {
                LOG.info("JMusicBot", "您當前已設置自定義前綴。"
                        + "如果您的前綴無效，請確保在 https://discord.com/developers/applications/" + jda.getSelfUser().getId() + "/bot 上啟用 '消息內容意圖'。");
            }
        }
        catch (LoginException ex)
        {
            prompt.alert(Prompt.Level.ERROR, "JMusicBot", ex + "\n請確保您正在編輯正確的 config.txt 文件，並且您使用了"
                    + "正確的令牌（而不是 'secret'！）\n配置位置: " + config.getConfigLocation());
            System.exit(1);
        }
        catch(IllegalArgumentException ex)
        {
            prompt.alert(Prompt.Level.ERROR, "JMusicBot", "配置的某個方面無效: "
                    + ex + "\n配置位置: " + config.getConfigLocation());
            System.exit(1);
        }
        catch(ErrorResponseException ex)
        {
            prompt.alert(Prompt.Level.ERROR, "JMusicBot", ex + "\n嘗試連接時返回無效響應，請確保您已連接到互聯網");
            System.exit(1);
        }
    }
    
    private static CommandClient createCommandClient(BotConfig config, SettingsManager settings, Bot bot)
    {
        // 實例化 about 命令
        AboutCommand aboutCommand = new AboutCommand(Color.BLUE.brighter(),
                                "一個 [易於自行托管的音樂機器人！](https://github.com/AvianJay/MusicBot-zh_tw/) (v" + OtherUtil.getCurrentVersion() + ")\n翻譯版本由AvianJay製作 (Powered by ChatGPT)\n原專案位置：[jagrosh/MusicBot](https://github.com/jagrosh/MusicBot)",
                                new String[]{"高品質音樂播放", "公平排隊技術", "易於自行托管"},
                                RECOMMENDED_PERMS);
        aboutCommand.setIsAuthor(false);
        aboutCommand.setReplacementCharacter("\uD83C\uDFB6"); // 🎶
        
        // 設置命令客戶端
        CommandClientBuilder cb = new CommandClientBuilder()
                .setPrefix(config.getPrefix())
                .setAlternativePrefix(config.getAltPrefix())
                .setOwnerId(Long.toString(config.getOwnerId()))
                .setEmojis(config.getSuccess(), config.getWarning(), config.getError())
                .setHelpWord(config.getHelp())
                .setLinkedCacheSize(200)
                .setGuildSettingsManager(settings)
                .addCommands(aboutCommand,
                        new PingCommand(),
                        new SettingsCmd(bot),
                        
                        new LyricsCmd(bot),
                        new NowplayingCmd(bot),
                        new PlayCmd(bot),
                        new PlaylistsCmd(bot),
                        new QueueCmd(bot),
                        new RemoveCmd(bot),
                        new SearchCmd(bot),
                        new SCSearchCmd(bot),
                        new SeekCmd(bot),
                        new ShuffleCmd(bot),
                        new SkipCmd(bot),

                        new ForceRemoveCmd(bot),
                        new ForceskipCmd(bot),
                        new MoveTrackCmd(bot),
                        new PauseCmd(bot),
                        new PlaynextCmd(bot),
                        new RepeatCmd(bot),
                        new SkiptoCmd(bot),
                        new StopCmd(bot),
                        new VolumeCmd(bot),
                        
                        new PrefixCmd(bot),
                        new QueueTypeCmd(bot),
                        new SetdjCmd(bot),
                        new SkipratioCmd(bot),
                        new SettcCmd(bot),
                        new SetvcCmd(bot),

                        new AutoplaylistCmd(bot),
                        new DebugCmd(bot),
                        new PlaylistCmd(bot),
                        new SetavatarCmd(bot),
                        new SetgameCmd(bot),
                        new SetnameCmd(bot),
                        new SetstatusCmd(bot),
                        new ShutdownCmd(bot)
                );
        
        // 如果適用，啟用評估
        if(config.useEval())
            cb.addCommand(new EvalCmd(bot));
        
        // 如果在配置中設置狀態
        if(config.getStatus() != OnlineStatus.UNKNOWN)
            cb.setStatus(config.getStatus());
        
        // 設置遊戲
        if(config.getGame() == null)
            cb.useDefaultGame();
        else if(config.isGameNone())
            cb.setActivity(null);
        else
            cb.setActivity(config.getGame());
        
        return cb.build();
    }
}
