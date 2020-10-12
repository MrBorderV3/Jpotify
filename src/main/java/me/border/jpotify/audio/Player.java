package me.border.jpotify.audio;

import javafx.scene.media.MediaPlayer;

import java.util.*;

public class Player {

    private Playlist playlist;
    private List<Song> songs;
    private Map<String, Integer> indexMap;

    private Random random = new Random();
    private Song currentSong;
    private Song lastSong;

    private boolean firstSong = true;
    private boolean playing = false;

    private Mode mode;

    private double volume = -1;

    public Player(){ }

    public Player(Playlist playlist){
        this.playlist = playlist;
        this.songs = playlist.getSongs();
        this.indexMap = playlist.getIndexMap();
    }

    public void setPlaylist(Playlist playlist){
        this.playlist = playlist;
        this.songs = playlist.getSongs();
        this.indexMap = playlist.getIndexMap();
    }

    public void playNormal(){
        if (this.mode != Mode.NORMAL){
            this.mode = Mode.NORMAL;
        }

        if (firstSong) {
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
        int index = songs.indexOf(currentSong);
        Song nextSong = songs.get(index+1);
        if (nextSong == null){
            nextSong = songs.get(0);
        }

        playSong(nextSong);
    }

    public void playLastSong(){
        Song lastSong = this.lastSong;

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
        playing = true;
        currentSong.getMedia().play();
    }

    private void playSong(Song song){
        if (!this.firstSong){
            this.lastSong = currentSong;
        } else {
            this.firstSong = false;
        }
        this.currentSong = song;

        MediaPlayer mediaPlayer = song.getMedia();

        if (this.volume != -1)
            mediaPlayer.setVolume(this.volume);

        mediaPlayer.play();
        mediaPlayer.setOnEndOfMedia(() -> {
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

    public void setVolume(double volume){
        this.volume = volume;
    }
}