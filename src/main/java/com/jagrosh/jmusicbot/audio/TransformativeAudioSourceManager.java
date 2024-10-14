/*
 * 版權所有 2021 John Grosh <john.a.grosh@gmail.com>。
 *
 * 根據 Apache 許可證 版本 2.0（以下簡稱“許可證”）授權；
 * 除非遵守該許可證，否則您不得使用此檔案。
 * 您可以在以下網址獲取許可證的副本：
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * 除非法律要求或書面同意，否則根據許可證分發的軟件是以“原樣”基礎提供的，
 * 不附有任何明示或隱含的保證或條件。
 * 有關授權的具體語言以及對於許可證的限制，請參見許可證。
 */
package com.jagrosh.jmusicbot.audio;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.track.AudioItem;
import com.sedmelluq.discord.lavaplayer.track.AudioReference;
import com.typesafe.config.Config;
import dev.lavalink.youtube.YoutubeAudioSourceManager;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.regex.PatternSyntaxException;
import java.util.stream.Collectors;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author John Grosh (john.a.grosh@gmail.com)
 */
public class TransformativeAudioSourceManager extends YoutubeAudioSourceManager
{
    private final static Logger log = LoggerFactory.getLogger(TransformativeAudioSourceManager.class);
    private final String name, regex, replacement, selector, format;
    
    public TransformativeAudioSourceManager(String name, Config object)
    {
        this(name, object.getString("regex"), object.getString("replacement"), object.getString("selector"), object.getString("format"));
    }
    
    public TransformativeAudioSourceManager(String name, String regex, String replacement, String selector, String format)
    {
        this.name = name;
        this.regex = regex;
        this.replacement = replacement;
        this.selector = selector;
        this.format = format;
    }

    @Override
    public String getSourceName()
    {
        return name;
    }

    @Override
    public AudioItem loadItem(AudioPlayerManager apm, AudioReference ar)
    {
        if(ar.identifier == null || !ar.identifier.matches(regex))
            return null;
        try
        {
            String url = ar.identifier.replaceAll(regex, replacement);
            Document doc = Jsoup.connect(url).get();
            String value = doc.selectFirst(selector).ownText();
            String formattedValue = String.format(format, value);
            return super.loadItem(apm, new AudioReference(formattedValue, null));
        }
        catch (PatternSyntaxException ex)
        {
            log.info(String.format("來源 '%s' 中的模式語法 '%s' 無效", name, regex));
        }
        catch (IOException ex)
        {
            log.warn(String.format("在來源 '%s' 中解析 URL 失敗：", name), ex);
        }
        catch (Exception ex)
        {
            log.warn(String.format("在來源 '%s' 中發生異常", name), ex);
        }
        return null;
    }
    
    public static List<TransformativeAudioSourceManager> createTransforms(Config transforms)
    {
        try
        {
            return transforms.root().entrySet().stream()
                    .map(e -> new TransformativeAudioSourceManager(e.getKey(), transforms.getConfig(e.getKey())))
                    .collect(Collectors.toList());
        }
        catch (Exception ex)
        {
            log.warn("無效的轉換 ", ex);
            return Collections.emptyList();
        }
    }
}
