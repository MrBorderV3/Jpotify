package me.border.jpotify.audio;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import me.border.jpotify.util.Utils;

import java.io.File;

public class Song {

    private MediaPlayer media;
    private String name;

    public Song(File mp3File){
        Media media = new Media(mp3File.toURI().toString());
        this.media = new MediaPlayer(media);
        this.name = Utils.stripExtension(mp3File.getName());
    }

    public String getName(){
        return name;
    }

    public MediaPlayer getMedia(){
        return media;
    }
}
