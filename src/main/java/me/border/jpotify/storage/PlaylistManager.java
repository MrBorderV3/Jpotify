package me.border.jpotify.storage;

import me.border.jpotify.audio.Playlist;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class PlaylistManager {

    public static final File dir = new File(System.getProperty("user.home") + File.separator + "Jpotify");

    private Map<String, Playlist> playlists = new HashMap<>();

    private PlaylistManager(){
        if (!dir.exists())
            dir.mkdirs();
        loadPlaylists();
    }

    private static PlaylistManager instance = new PlaylistManager();

    public static PlaylistManager getInstance(){
        return instance;
    }

    private void loadPlaylists(){
        for (File playlistDir : dir.listFiles()){
            if (playlistDir != null){
                if (playlistDir.isDirectory()){
                    Playlist playlist = new Playlist(playlistDir);
                    playlists.put(playlist.getName(), playlist);
                }
            }
        }
    }

    public Playlist getPlaylist(String name) {
        return playlists.get(name);
    }

    public boolean contains(String name){
        return playlists.containsKey(name);
    }

    public Collection<Playlist> getPlaylists(){
        return playlists.values();
    }

    public Playlist createPlaylist(String name) {
        if (playlists.containsKey(name)){
            return null;
        }
        File playlistDir = new File(dir + File.separator + name);
        if (!playlistDir.exists()){
            playlistDir.mkdirs();
        }

        Playlist playlist = new Playlist(playlistDir);
        playlists.put(name, playlist);
        return playlist;
    }

    public void removePlaylist(Playlist playlist){
        playlists.remove(playlist.getName());
        playlist.delete();
    }
}
