package me.border.jpotify.storage;

import me.border.jpotify.audio.Playlist;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class PlaylistManager {

    private static final File dir = new File(System.getProperty("user.home") + File.separator + "Jpotify");

    private static Map<String, Playlist> playlists = new HashMap<>();

    public static Playlist getPlaylist(String name) {
        handleDir();
        return playlists.get(name);
    }

    public static void createPlaylist(String name) {
        handleDir();
        File playlistDir = new File(dir + File.separator + name);
        if (!playlistDir.exists()){
            playlistDir.mkdirs();
        }

        Playlist playlist = new Playlist(playlistDir);
        playlists.put(name, playlist);
    }

    public static void removePlaylist(Playlist playlist){
        playlists.remove(playlist.getName());
    }

    private static void handleDir() {
        if (!dir.exists())
            dir.mkdirs();
    }

}
