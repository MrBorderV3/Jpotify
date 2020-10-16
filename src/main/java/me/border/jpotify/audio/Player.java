package me.border.jpotify.audio;

import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import me.border.jpotify.audio.util.SaveStatus;
import me.border.jpotify.audio.util.SongQueue;
import me.border.jpotify.audio.util.Mode;
import me.border.jpotify.file.CacheFile;
import me.border.jpotify.storage.PlaylistManager;
import me.border.jpotify.ui.PlayerApp;
import me.border.jpotify.ui.controllers.AppController;

import java.util.*;

public class Player {

    private PlaylistManager playlistManager;

    private Playlist playlist;
    private List<Song> songs;
    private Map<String, Integer> indexMap;

    private Random random = new Random();
    private SongQueue songQueue = new SongQueue();
    private Song currentSong;

    private CacheFile cacheFile = new CacheFile("cache", PlaylistManager.dir, null);
    private Object[] cache;
    private SaveStatus saveStatus = SaveStatus.CLOSED;

    // TODO - Make it so shuffle doesn't play similar songs one after the other (By saving indices on the files)
    // TODO - Change it so playing on shuffle just shuffles the list of songs (maybe add shuffle function to Playlist and save an oglist and a shuffledlist) GOT TO KEEP COPY OF OG ORDER
    private Mode mode;

    private double volume = -1;

    private boolean firstSong = true;
    private boolean playing = false;

    public Player(){
        this.cacheFile.setup();
        this.cache = cacheFile.getItem();
        controller().adjustButton(false);
        this.playlistManager = PlaylistManager.getInstance();
        if (cache != null) {
            String playlistCache = (String) cache[0];
            if (playlistCache != null) {
                if (playlistCache.contains("?:?")) {
                    String[] stringCache = playlistCache.split("\\?:\\?");
                    String cachedPlaylist = stringCache[0];
                    String song = stringCache[1];
                    Playlist playlist = playlistManager.getPlaylist(cachedPlaylist);
                    lazySetPlaylist(playlist);
                    PlayerApp.controller.focusPlaylist(playlist.getName());
                    if (playlist.hasSong(song)) {
                        this.firstSong = false;
                        currentSong = songs.get(indexMap.get(song));
                        controller().changeText(currentSong.getName());
                    }
                } else {
                    Playlist playlist = playlistManager.getPlaylist(playlistCache);
                    setPlaylist(playlist);
                    PlayerApp.controller.focusPlaylist(playlist.getName());
                }
            }
            Double volumeCache = (Double) cache[1];
            if (volumeCache != null){
                controller().adjustVolume(volumeCache);
                setVolume(volumeCache);
            }
        } else {
            this.cache = new Object[2];
        }
    }

    private void lazySetPlaylist(Playlist playlist){
        this.playlist = playlist;
        this.songs = playlist.getSongs();
        this.indexMap = playlist.getIndexMap();
    }

    public void setPlaylist(Playlist playlist) {
        lazySetPlaylist(playlist);
        songQueue.clear();
        firstSong = true;
        String item = playlist.getName();
        if (currentSong != null && songs.contains(currentSong)){
            item = item + "?:?" + currentSong.getName();
        }
        cache[0] = item;
        this.cacheFile.setItem(cache);
        this.cacheFile.save();
    }

    public Playlist getPlaylist(){
        return playlist;
    }

    public void playNormal(){
        if (this.mode != Mode.NORMAL){
            this.mode = Mode.NORMAL;
        }

        if (firstSong) {
            if (songs == null){
                controller().adjustButton(false);
                return;
            }
            if (songs.isEmpty()){
                playing = false;
                return;
            }
            Song song = songs.get(0);
            playSong(song, false);
        } else {
            playNext();
        }
    }

    public void shufflePlay(){
        if (this.mode != Mode.SHUFFLE) {
            this.mode = Mode.SHUFFLE;
        }

        Song randomSong = songs.get(random.nextInt(songs.size()));
        while (randomSong == currentSong){
            randomSong = songs.get(random.nextInt(songs.size()));;
        }
        playSong(randomSong, false);
    }

    // Function to play a song that is clicked
    public void playSpecific(String name){
        if (this.mode != Mode.NORMAL) {
            this.mode = Mode.NORMAL;
        }
        int index = indexMap.get(name);
        Song song = songs.get(index);

        playSong(song, false);
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

        playSong(nextSong, false);
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

        playSong(lastSong, true);
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
            controller().changeText(currentSong.getName());
            currentSong.getMedia().play();
        }
    }

    private void playSong(Song song, boolean prev){
        if (playing) {
            pause();
        }
        if (!this.firstSong && !prev){
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
        controller().changeText(song.getName());
        mediaPlayer.play();

        String item = playlist.getName();
        if (currentSong != null){
            item = item + "?:?" + currentSong.getName();
        }
        cache[0] = item;
        saveCache();
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

    public void setVolume(double volume) {
        this.volume = volume;
        this.cache[1] = volume;
        if (this.currentSong != null) {
            this.currentSong.getMedia().setVolume(volume);
        }
        saveCache();
    }

    private void saveCache(){
        if (saveStatus == SaveStatus.WAITING){
            return;
        }
        if (saveStatus == SaveStatus.READY) {
            this.cacheFile.setItem(cache);
            this.cacheFile.save();
            saveStatus = SaveStatus.CLOSED;
        } else {
            saveStatus = SaveStatus.WAITING;
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    saveStatus = SaveStatus.READY;
                    cacheFile.setItem(cache);
                    cacheFile.save();
                }
            }, 2000);
        }
    }

    public boolean isPlaying(){
        return playing;
    }

    public AppController controller(){
        return PlayerApp.controller;
    }
}