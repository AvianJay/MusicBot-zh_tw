package com.jagrosh.jmusicbot.utils;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.turbo.TurboFilter;
import ch.qos.logback.core.spi.FilterReply;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;

import java.nio.file.Files;

/**
 * 一個 logback turbo 篩選器，用來取得 YouTube OAuth2 刷新令牌，
 * 該令牌會在授權 YouTube 後被記錄下來。
 *
 * 作者：Michaili K. <git@michaili.dev>
 */
public class YoutubeOauth2TokenHandler extends TurboFilter {
    public final static org.slf4j.Logger LOGGER = LoggerFactory.getLogger(YoutubeOauth2TokenHandler.class);
    private Data data;


    public void init()
    {
        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
        context.addTurboFilter(this); // 將此篩選器加入到 LoggerContext 中
    }

    public Data getData()
    {
        return data; // 取得授權資料
    }

    @Override
    public FilterReply decide(Marker marker, Logger logger, Level level, String format, Object[] params, Throwable t)
    {
        if (!logger.getName().equals("dev.lavalink.youtube.http.YoutubeOauth2Handler"))
            return FilterReply.NEUTRAL; // 若 Logger 名稱不符，則中立回應

        if (format.equals("OAUTH INTEGRATION: 要讓 youtube-source 存取您的帳戶，請前往 {} 並輸入代碼 {}"))
        {
            this.data = new Data((String) params[0], (String) params[1]); // 設定授權網址和代碼
            return FilterReply.NEUTRAL;
        }
        if (format.equals("OAUTH INTEGRATION: 成功取得令牌。儲存您的刷新令牌以便重複使用。 ({})"))
        {
            LOGGER.info(
                "授權成功並取得令牌！將令牌儲存在 {}",
                OtherUtil.getPath("youtubetoken.txt").toAbsolutePath() // 確認儲存令牌的路徑
            );

            try
            {
                Files.write(OtherUtil.getPath("youtubetoken.txt"), params[0].toString().getBytes()); // 將令牌寫入檔案
            }
            catch (Exception e)
            {
                LOGGER.error(
                    "無法將 YouTube OAuth2 刷新令牌寫入儲存空間！您將需要在下次重啟時重新授權",
                    e
                );
            }
            return FilterReply.DENY; // 停止進一步處理此訊息
        }

        return FilterReply.NEUTRAL; // 若無匹配的訊息，則回應中立
    }

    public static class Data
    {
        private final String authorisationUrl;
        private final String code;

        private Data(String authorisationUrl, String code)
        {
            this.authorisationUrl = authorisationUrl;
            this.code = code;
        }

        public String getCode()
        {
            return code; // 取得授權碼
        }

        public String getAuthorisationUrl()
        {
            return authorisationUrl; // 取得授權網址
        }
    }

}
