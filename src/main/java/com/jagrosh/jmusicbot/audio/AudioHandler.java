/*
 * 版權所有 2016 John Grosh <john.a.grosh@gmail.com>。
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

import com.jagrosh.jmusicbot.playlist.PlaylistLoader.Playlist;
import com.jagrosh.jmusicbot.queue.AbstractQueue;
import com.jagrosh.jmusicbot.settings.QueueType;
import com.jagrosh.jmusicbot.utils.TimeUtil;
import com.jagrosh.jmusicbot.settings.RepeatMode;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import com.sedmelluq.discord.lavaplayer.track.playback.AudioFrame;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import com.jagrosh.jmusicbot.settings.Settings;
import com.jagrosh.jmusicbot.utils.FormatUtil;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioTrack;
import java.nio.ByteBuffer;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.audio.AudioSendHandler;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author John Grosh <john.a.grosh@gmail.com>
 */
public class AudioHandler extends AudioEventAdapter implements AudioSendHandler 
{
    public final static String PLAY_EMOJI  = "\u25B6"; // ▶
    public final static String PAUSE_EMOJI = "\u23F8"; // ⏸
    public final static String STOP_EMOJI  = "\u23F9"; // ⏹

    private final static Logger LOGGER = LoggerFactory.getLogger(AudioHandler.class);

    private final List<AudioTrack> defaultQueue = new LinkedList<>();
    private final Set<String> votes = new HashSet<>();
    
    private final PlayerManager manager;
    private final AudioPlayer audioPlayer;
    private final long guildId;
    
    private AudioFrame lastFrame;
    private AbstractQueue<QueuedTrack> queue;

    protected AudioHandler(PlayerManager manager, Guild guild, AudioPlayer player)
    {
        this.manager = manager;
        this.audioPlayer = player;
        this.guildId = guild.getIdLong();

        this.setQueueType(manager.getBot().getSettingsManager().getSettings(guildId).getQueueType());
    }

    public void setQueueType(QueueType type)
    {
        queue = type.createInstance(queue);
    }

    public int addTrackToFront(QueuedTrack qtrack)
    {
        if(audioPlayer.getPlayingTrack() == null)
        {
            audioPlayer.playTrack(qtrack.getTrack());
            return -1;
        }
        else
        {
            queue.addAt(0, qtrack);
            return 0;
        }
    }
    
    public int addTrack(QueuedTrack qtrack)
    {
        if(audioPlayer.getPlayingTrack() == null)
        {
            audioPlayer.playTrack(qtrack.getTrack());
            return -1;
        }
        else
            return queue.add(qtrack);
    }
    
    public AbstractQueue<QueuedTrack> getQueue()
    {
        return queue;
    }
    
    public void stopAndClear()
    {
        queue.clear();
        defaultQueue.clear();
        audioPlayer.stopTrack();
        //current = null;
    }
    
    public boolean isMusicPlaying(JDA jda)
    {
        return guild(jda).getSelfMember().getVoiceState().inVoiceChannel() && audioPlayer.getPlayingTrack() != null;
    }
    
    public Set<String> getVotes()
    {
        return votes;
    }
    
    public AudioPlayer getPlayer()
    {
        return audioPlayer;
    }
    
    public RequestMetadata getRequestMetadata()
    {
        if(audioPlayer.getPlayingTrack() == null)
            return RequestMetadata.EMPTY;
        RequestMetadata rm = audioPlayer.getPlayingTrack().getUserData(RequestMetadata.class);
        return rm == null ? RequestMetadata.EMPTY : rm;
    }
    
    public boolean playFromDefault()
    {
        if(!defaultQueue.isEmpty())
        {
            audioPlayer.playTrack(defaultQueue.remove(0));
            return true;
        }
        Settings settings = manager.getBot().getSettingsManager().getSettings(guildId);
        if(settings == null || settings.getDefaultPlaylist() == null)
            return false;
        
        Playlist pl = manager.getBot().getPlaylistLoader().getPlaylist(settings.getDefaultPlaylist());
        if(pl == null || pl.getItems().isEmpty())
            return false;
        pl.loadTracks(manager, (at) -> 
        {
            if(audioPlayer.getPlayingTrack() == null)
                audioPlayer.playTrack(at);
            else
                defaultQueue.add(at);
        }, () -> 
        {
            if(pl.getTracks().isEmpty() && !manager.getBot().getConfig().getStay())
                manager.getBot().closeAudioConnection(guildId);
        });
        return true;
    }
    
    // 音頻事件
    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) 
    {
        RepeatMode repeatMode = manager.getBot().getSettingsManager().getSettings(guildId).getRepeatMode();
        // 如果曲目正常結束，且我們在重複模式中，則重新添加到隊列中
        if(endReason == AudioTrackEndReason.FINISHED && repeatMode != RepeatMode.OFF)
        {
            QueuedTrack clone = new QueuedTrack(track.makeClone(), track.getUserData(RequestMetadata.class));
            if(repeatMode == RepeatMode.ALL)
                queue.add(clone);
            else
                queue.addAt(0, clone);
        }
        
        if(queue.isEmpty())
        {
            if(!playFromDefault())
            {
                manager.getBot().getNowplayingHandler().onTrackUpdate(null);
                if(!manager.getBot().getConfig().getStay())
                    manager.getBot().closeAudioConnection(guildId);
                // 取消暫停，以防播放器被暫停並且曲目已被跳過。
                // 這是為了防止下次使用時播放器處於暫停狀態。
                player.setPaused(false);
            }
        }
        else
        {
            QueuedTrack qt = queue.pull();
            player.playTrack(qt.getTrack());
        }
    }

    @Override
    public void onTrackException(AudioPlayer player, AudioTrack track, FriendlyException exception)
    {
        if (
            exception.getMessage().equals("Sign in to confirm you're not a bot")
            || exception.getMessage().equals("Please sign in")
            || exception.getMessage().equals("This video requires login.")
        )
            LOGGER.error(
                "曲目 {} 無法播放：{}。"
                + "您需要登入 Google 以播放 YouTube 曲目。"
                + "詳細資訊：(還沒有等我做)",
                track.getIdentifier(),
                exception.getMessage()
            );
        else
            LOGGER.error("曲目 " + track.getIdentifier() + " 播放失敗", exception);
    }

    @Override
    public void onTrackStart(AudioPlayer player, AudioTrack track) 
    {
        votes.clear();
        manager.getBot().getNowplayingHandler().onTrackUpdate(track);
    }

    
    // 格式化
    public Message getNowPlaying(JDA jda)
    {
        if(isMusicPlaying(jda))
        {
            Guild guild = guild(jda);
            AudioTrack track = audioPlayer.getPlayingTrack();
            MessageBuilder mb = new MessageBuilder();
            mb.append(FormatUtil.filter(manager.getBot().getConfig().getSuccess()+" **現在正在播放 "+guild.getSelfMember().getVoiceState().getChannel().getAsMention()+"...**"));
            EmbedBuilder eb = new EmbedBuilder();
            eb.setColor(guild.getSelfMember().getColor());
            RequestMetadata rm = getRequestMetadata();
            if(rm.getOwner() != 0L)
            {
                User u = guild.getJDA().getUserById(rm.user.id);
                if(u == null)
                    eb.setAuthor(FormatUtil.formatUsername(rm.user), null, rm.user.avatar);
                else
                    eb.setAuthor(FormatUtil.formatUsername(u), null, u.getEffectiveAvatarUrl());
            }

            try 
            {
                eb.setTitle(track.getInfo().title, track.getInfo().uri);
            }
            catch(Exception e) 
            {
                eb.setTitle(track.getInfo().title);
            }

            if(track instanceof YoutubeAudioTrack && manager.getBot().getConfig().useNPImages())
            {
                eb.setThumbnail("https://img.youtube.com/vi/"+track.getIdentifier()+"/mqdefault.jpg");
            }
            
            if(track.getInfo().author != null && !track.getInfo().author.isEmpty())
                eb.setFooter("來源: " + track.getInfo().author, null);

            double progress = (double)audioPlayer.getPlayingTrack().getPosition()/track.getDuration();
            eb.setDescription(getStatusEmoji()
                    + " "+FormatUtil.progressBar(progress)
                    + " `[" + TimeUtil.formatTime(track.getPosition()) + "/" + TimeUtil.formatTime(track.getDuration()) + "]` "
                    + FormatUtil.volumeIcon(audioPlayer.getVolume()));
            
            return mb.setEmbeds(eb.build()).build();
        }
        else return null;
    }
    
    public Message getNoMusicPlaying(JDA jda)
    {
        Guild guild = guild(jda);
        return new MessageBuilder()
                .setContent(FormatUtil.filter(manager.getBot().getConfig().getSuccess()+" **現在正在播放...**"))
                .setEmbeds(new EmbedBuilder()
                .setTitle("沒有音樂正在播放")
                .setDescription(STOP_EMOJI+" "+FormatUtil.progressBar(-1)+" "+FormatUtil.volumeIcon(audioPlayer.getVolume()))
                .setColor(guild.getSelfMember().getColor())
                .build()).build();
    }

    public String getStatusEmoji()
    {
        return audioPlayer.isPaused() ? PAUSE_EMOJI : PLAY_EMOJI;
    }
    
    // 音頻發送處理程序方法
    /*@Override
    public boolean canProvide() 
    {
        if (lastFrame == null)
            lastFrame = audioPlayer.provide();

        return lastFrame != null;
    }

    @Override
    public byte[] provide20MsAudio() 
    {
        if (lastFrame == null) 
            lastFrame = audioPlayer.provide();

        byte[] data = lastFrame != null ? lastFrame.getData() : null;
        lastFrame = null;

        return data;
    }*/
    
    @Override
    public boolean canProvide() 
    {
        lastFrame = audioPlayer.provide();
        return lastFrame != null;
    }

    @Override
    public ByteBuffer provide20MsAudio() 
    {
        return ByteBuffer.wrap(lastFrame.getData());
    }

    @Override
    public boolean isOpus() 
    {
        return true;
    }
    
    
    // 私有方法
    private Guild guild(JDA jda)
    {
        return jda.getGuildById(guildId);
    }
}
