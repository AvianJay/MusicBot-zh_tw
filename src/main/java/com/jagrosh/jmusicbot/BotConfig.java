/*
 * 版權所有 2018 John Grosh (jagrosh)
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

import com.jagrosh.jmusicbot.entities.Prompt;
import com.jagrosh.jmusicbot.utils.OtherUtil;
import com.jagrosh.jmusicbot.utils.TimeUtil;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.typesafe.config.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;

/**
 * 
 * 
 * @author John Grosh (jagrosh)
 */
public class BotConfig
{
    private final Prompt prompt;
    private final static String CONTEXT = "Config";
    private final static String START_TOKEN = "/// JMUSICBOT 設定開始 ///";
    private final static String END_TOKEN = "/// JMUSICBOT 設定結束 ///";
    
    private Path path = null;
    private String token, prefix, altprefix, helpWord, playlistsFolder, logLevel,
            successEmoji, warningEmoji, errorEmoji, loadingEmoji, searchingEmoji,
            evalEngine;
    private boolean stayInChannel, songInGame, npImages, updatealerts, useEval, dbots;
    private long owner, maxSeconds, aloneTimeUntilStop;
    private int maxYTPlaylistPages;
    private double skipratio;
    private OnlineStatus status;
    private Activity game;
    private Config aliases, transforms;

    private boolean valid = false;
    
    public BotConfig(Prompt prompt)
    {
        this.prompt = prompt;
    }
    
    public void load()
    {
        valid = false;
        
        // 從檔案中讀取設定
        try 
        {
            // 取得設定檔路徑，預設為 config.txt
            path = getConfigPath();
            
            // 載入設定檔，並加入預設值
            Config config = ConfigFactory.load();
            
            // 設定值
            token = config.getString("token");
            prefix = config.getString("prefix");
            altprefix = config.getString("altprefix");
            helpWord = config.getString("help");
            owner = config.getLong("owner");
            successEmoji = config.getString("success");
            warningEmoji = config.getString("warning");
            errorEmoji = config.getString("error");
            loadingEmoji = config.getString("loading");
            searchingEmoji = config.getString("searching");
            game = OtherUtil.parseGame(config.getString("game"));
            status = OtherUtil.parseStatus(config.getString("status"));
            stayInChannel = config.getBoolean("stayinchannel");
            songInGame = config.getBoolean("songinstatus");
            npImages = config.getBoolean("npimages");
            updatealerts = config.getBoolean("updatealerts");
            logLevel = config.getString("loglevel");
            useEval = config.getBoolean("eval");
            evalEngine = config.getString("evalengine");
            maxSeconds = config.getLong("maxtime");
            maxYTPlaylistPages = config.getInt("maxytplaylistpages");
            aloneTimeUntilStop = config.getLong("alonetimeuntilstop");
            playlistsFolder = config.getString("playlistsfolder");
            aliases = config.getConfig("aliases");
            transforms = config.getConfig("transforms");
            skipratio = config.getDouble("skipratio");
            dbots = owner == 113156185389092864L;
            
            // 可能需要寫入新的設定檔
            boolean write = false;

            // 驗證機器人代碼
            if(token==null || token.isEmpty() || token.equalsIgnoreCase("BOT_TOKEN_HERE"))
            {
                token = prompt.prompt("請提供一個機器人代碼。"
                        + "\n獲取代碼的說明可在此找到："
                        + "\nhttps://github.com/jagrosh/MusicBot/wiki/Getting-a-Bot-Token。"
                        + "\n機器人代碼: ");
                if(token==null)
                {
                    prompt.alert(Prompt.Level.WARNING, CONTEXT, "未提供代碼！程式結束。\n\n設定檔位置：" + path.toAbsolutePath().toString());
                    return;
                }
                else
                {
                    write = true;
                }
            }
            
            // 驗證擁有者 ID
            if(owner<=0)
            {
                try
                {
                    owner = Long.parseLong(prompt.prompt("擁有者 ID 缺失，或提供的擁有者 ID 無效。"
                        + "\n請提供機器人擁有者的用戶 ID。"
                        + "\n找到你的用戶 ID 的說明可在此找到："
                        + "\nhttps://github.com/jagrosh/MusicBot/wiki/Finding-Your-User-ID"
                        + "\n擁有者用戶 ID: "));
                }
                catch(NumberFormatException | NullPointerException ex)
                {
                    owner = 0;
                }
                if(owner<=0)
                {
                    prompt.alert(Prompt.Level.ERROR, CONTEXT, "無效的用戶 ID！程式結束。\n\n設定檔位置：" + path.toAbsolutePath().toString());
                    return;
                }
                else
                {
                    write = true;
                }
            }
            
            if(write)
                writeToFile();
            
            // 如果設定成功載入，則標記為有效
            valid = true;
        }
        catch (ConfigException ex)
        {
            prompt.alert(Prompt.Level.ERROR, CONTEXT, ex + ": " + ex.getMessage() + "\n\n設定檔位置：" + path.toAbsolutePath().toString());
        }
    }
    
    private void writeToFile()
    {
        byte[] bytes = loadDefaultConfig().replace("BOT_TOKEN_HERE", token)
                .replace("0 // OWNER ID", Long.toString(owner))
                .trim().getBytes();
        try 
        {
            Files.write(path, bytes);
        }
        catch(IOException ex) 
        {
            prompt.alert(Prompt.Level.WARNING, CONTEXT, "寫入新的設定選項至 config.txt 失敗: "+ex
                + "\n請確保檔案不在桌面或其他受限制的區域。\n\n設定檔位置：" 
                + path.toAbsolutePath().toString());
        }
    }
    
    private static String loadDefaultConfig()
    {
        String original = OtherUtil.loadResource(new JMusicBot(), "/reference.conf");
        return original==null 
                ? "token = BOT_TOKEN_HERE\r\nowner = 0 // OWNER ID" 
                : original.substring(original.indexOf(START_TOKEN)+START_TOKEN.length(), original.indexOf(END_TOKEN)).trim();
    }
    
    private static Path getConfigPath()
    {
        Path path = OtherUtil.getPath(System.getProperty("config.file", System.getProperty("config", "config.txt")));
        if(path.toFile().exists())
        {
            if(System.getProperty("config.file") == null)
                System.setProperty("config.file", System.getProperty("config", path.toAbsolutePath().toString()));
            ConfigFactory.invalidateCaches();
        }
        return path;
    }
    
    public static void writeDefaultConfig()
    {
        Prompt prompt = new Prompt(null, null, true, true);
        prompt.alert(Prompt.Level.INFO, "JMusicBot 設定", "生成預設設定檔");
        Path path = BotConfig.getConfigPath();
        byte[] bytes = BotConfig.loadDefaultConfig().getBytes();
        try
        {
            prompt.alert(Prompt.Level.INFO, "JMusicBot 設定", "寫入預設設定檔至 " + path.toAbsolutePath().toString());
            Files.write(path, bytes);
        }
        catch(Exception ex)
        {
            prompt.alert(Prompt.Level.ERROR, "JMusicBot 設定", "寫入預設設定檔時發生錯誤: " + ex.getMessage());
        }
    }
    
    public boolean isValid()
    {
        return valid;
    }
    
    public String getConfigLocation()
    {
        return path.toFile().getAbsolutePath();
    }
    
    public String getPrefix()
    {
        return prefix;
    }
    
    public String getAltPrefix()
    {
        return "NONE".equalsIgnoreCase(altprefix) ? null : altprefix;
    }
    
    public String getToken()
    {
        return token;
    }
    
    public double getSkipRatio()
    {
        return skipratio;
    }
    
    public long getOwnerId()
    {
        return owner;
    }
    
    public String getSuccess()
    {
        return successEmoji;
    }
    
    public String getWarning()
    {
        return warningEmoji;
    }
    
    public String getError()
    {
        return errorEmoji;
    }
    
    public String getLoading()
    {
        return loadingEmoji;
    }
    
    public String getSearching()
    {
        return searchingEmoji;
    }
    
    public Activity getGame()
    {
        return game;
    }
    
    public boolean isGameNone()
    {
        return game != null && game.getName().equalsIgnoreCase("none");
    }
    
    public OnlineStatus getStatus()
    {
        return status;
    }
    
    public String getHelp()
    {
        return helpWord;
    }
    
    public boolean getStay()
    {
        return stayInChannel;
    }
    
    public boolean getSongInStatus()
    {
        return songInGame;
    }
    
    public String getPlaylistsFolder()
    {
        return playlistsFolder;
    }
    
    public boolean getDBots()
    {
        return dbots;
    }
    
    public boolean useUpdateAlerts()
    {
        return updatealerts;
    }

    public String getLogLevel()
    {
        return logLevel;
    }

    public boolean useEval()
    {
        return useEval;
    }
    
    public String getEvalEngine()
    {
        return evalEngine;
    }
    
    public boolean useNPImages()
    {
        return npImages;
    }
    
    public long getMaxSeconds()
    {
        return maxSeconds;
    }
    
    public int getMaxYTPlaylistPages()
    {
        return maxYTPlaylistPages;
    }
    
    public String getMaxTime()
    {
        return TimeUtil.formatTime(maxSeconds * 1000);
    }

    public long getAloneTimeUntilStop()
    {
        return aloneTimeUntilStop;
    }
    
    public boolean isTooLong(AudioTrack track)
    {
        if(maxSeconds<=0)
            return false;
        return Math.round(track.getDuration()/1000.0) > maxSeconds;
    }

    public String[] getAliases(String command)
    {
        try
        {
            return aliases.getStringList(command).toArray(new String[0]);
        }
        catch(NullPointerException | ConfigException.Missing e)
        {
            return new String[0];
        }
    }
    
    public Config getTransforms()
    {
        return transforms;
    }
}
