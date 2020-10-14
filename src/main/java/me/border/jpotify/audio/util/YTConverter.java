package me.border.jpotify.audio.util;

import com.github.kiulian.downloader.YoutubeDownloader;
import com.github.kiulian.downloader.YoutubeException;
import com.github.kiulian.downloader.model.YoutubeVideo;
import me.border.jpotify.audio.Playlist;
import me.border.utilities.utils.AsyncScheduler;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class YTConverter {

    public static void addSong(String url, Playlist playlist){
        AsyncScheduler.runTaskAsync(() -> {
            try {
                String id = getYoutubeVideoId(url);
                YoutubeDownloader downloader = new YoutubeDownloader();
                YoutubeVideo video = downloader.getVideo(id);
                video.download(video.audioFormats().get(0), playlist.getDir());
            } catch (YoutubeException | IOException e){
                e.printStackTrace();
            }
        });
    }

    private static String getYoutubeVideoId(String youtubeUrl) {
        String video_id = "";
        if (youtubeUrl != null && youtubeUrl.trim().length() > 0 && youtubeUrl.startsWith("http")) {
            String expression = "^.*((youtu.be" + "\\/)" + "|(v\\/)|(\\/u\\/w\\/)|(embed\\/)|(watch\\?))\\??v?=?([^#\\&\\?]*).*";
            CharSequence input = youtubeUrl;
            Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(input);
            if (matcher.matches()) {
                String groupIndex1 = matcher.group(7);
                if (groupIndex1 != null && groupIndex1.length() == 11)
                    video_id = groupIndex1;
            }
        }
        return video_id;
    }

}