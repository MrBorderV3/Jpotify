package me.border.jpotify.audio.youtube;

import com.github.kiulian.downloader.YoutubeDownloader;
import com.github.kiulian.downloader.YoutubeException;
import com.github.kiulian.downloader.model.YoutubeVideo;
import me.border.jpotify.audio.Playlist;

import java.io.IOException;

public class YTConverter {

    public static void addSong(String id, Playlist playlist){
        try {
            YoutubeDownloader downloader = new YoutubeDownloader();
            YoutubeVideo video = downloader.getVideo(id);
            video.download(video.audioFormats().get(0), playlist.getDir());
        } catch (YoutubeException | IOException e){
            e.printStackTrace();
        }
    }
}