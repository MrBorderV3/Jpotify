package me.border.jpotify.audio;

import javafx.application.Platform;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import me.border.jpotify.audio.util.SongQueue;
import me.border.jpotify.audio.util.Mode;
import me.border.jpotify.file.PlaylistCacheFile;
import me.border.jpotify.storage.PlaylistManager;
import me.border.jpotify.ui.PlayerApp;
import me.border.jpotify.ui.controllers.AppController;

import java.util.*;

public class Player {

    private Playlist playlist;
    private PlaylistCacheFile playlistCacheFile;
    private List<Song> songs;
    private Map<String, Integer> indexMap;

    private Random random = new Random();
    private Song currentSong;
    private SongQueue songQueue = new SongQueue();

    private boolean firstSong = true;
    private boolean playing = false;

    // TODO - Change it so playing on shuffle just shuffles the list of songs (maybe add shuffle function to Playlist and save an oglist and a shuffledlist) GOT TO KEEP COPY OF OG ORDER
    private Mode mode;

    private double volume = -1;

    public Player(){
        this.playlistCacheFile = new PlaylistCacheFile("pcache", PlaylistManager.dir, null);
        this.playlistCacheFile.setup();
        String cache = playlistCacheFile.getItem();
        if (cache != null){
            if (cache.contains("?:?")){
                String[] rCache = cache.split("\\?:\\?");
                String cachedPlaylist = rCache[0];
                String song = rCache[1];
                Playlist playlist = PlayerApp.playlistManager.getPlaylist(cachedPlaylist);
                setPlaylist(playlist);
                PlayerApp.controller.focusPlaylist(playlist.getName());
                if (playlist.hasSong(song)) {
                    Platform.runLater(() -> playSpecific(song));
                }
            } else {
                Playlist playlist = PlayerApp.playlistManager.getPlaylist(cache);
                setPlaylist(playlist);
                PlayerApp.controller.focusPlaylist(playlist.getName());
            }
        }
    }

    public void setPlaylist(Playlist playlist) {
        this.playlist = playlist;
        this.songs = playlist.getSongs();
        this.indexMap = playlist.getIndexMap();
        songQueue.clear();
        firstSong = true;
        String item = playlist.getName();
        if (currentSong != null && songs.contains(currentSong)){
            item = item + "?:?" + currentSong.getName();
        }
        this.playlistCacheFile.setItem(item);
        this.playlistCacheFile.save();
    }

    public Playlist getPlaylist(){
        return playlist;
    }

    public void playNormal(){
        if (this.mode != Mode.NORMAL){
            this.mode = Mode.NORMAL;
        }

        if (firstSong) {
            if (songs.isEmpty()){
                playing = false;
                return;
            }
            Song song = songs.get(0);
            playSong(song);
        } else {
            playNext();
        }
    }

    public void shufflePlay(){
        if (this.mode != Mode.SHUFFLE) {
            this.mode = Mode.SHUFFLE;
        }
        playSong(songs.get(random.nextInt(songs.size())));
    }

    // Function to play a song that is clicked
    public void playSpecific(String name){
        if (this.mode != Mode.NORMAL) {
            this.mode = Mode.NORMAL;
        }
        int index = indexMap.get(name);
        Song song = songs.get(index);

        playSong(song);
    }

    public void playNext(){
        if (currentSong == null) {
            return;
        }
        int index = songs.indexOf(currentSong);
        Song nextSong;
        try {
            nextSong = songs.get(index + 1);
        } catch (IndexOutOfBoundsException ex) {
            nextSong = songs.get(0);
        }

        playSong(nextSong);
    }

    public void playLastSong() {
        Song lastSong = songQueue.poll();
        if (lastSong == null) {
            if (currentSong == null) {
                return;
            }

            int index = songs.indexOf(currentSong);
            try {
                lastSong = songs.get(index - 1);
            } catch (IndexOutOfBoundsException ex) {
                lastSong = songs.get(0);
            }
        }

        playSong(lastSong);
    }

    public void pause() {
        if (!playing)
            return;
        playing = false;
        currentSong.getMedia().pause();
    }

    public void resume(){
        if (playing)
            return;
        if (firstSong){
            playNormal();
        } else {
            playing = true;
            currentSong.getMedia().play();
        }
    }

    private void playSong(Song song){
        if (playing) {
            pause();
        }
        if (!this.firstSong){
            songQueue.add(currentSong);
        } else {
            this.firstSong = false;
        }
        this.currentSong = song;

        MediaPlayer mediaPlayer = song.getMedia();
        mediaPlayer.seek(Duration.ZERO);

        if (this.volume == -1) {
            volume = mediaPlayer.getVolume();
        } else {
            mediaPlayer.setVolume(volume);
        }

        playing = true;
        controller().initTimeListener();
        mediaPlayer.play();

        String item = playlist.getName();
        if (currentSong != null){
            item = item + "?:?" + currentSong.getName();
        }
        this.playlistCacheFile.setItem(item);
        this.playlistCacheFile.save();
        mediaPlayer.setOnEndOfMedia(() -> {
            controller().resetSlider();
            switch (mode) {
                case SHUFFLE:
                    shufflePlay();
                    break;
                case NORMAL:
                    playNormal();
                    break;
            }
        });
    }

    public Song getCurrentSong(){
        return currentSong;
    }

    public double getVolume(){
        return volume;
    }

    public void setVolume(double volume){
        this.volume = volume;
        this.currentSong.getMedia().setVolume(volume);
    }

    public AppController controller(){
        return PlayerApp.controller;
    }
}