package me.border.jpotify.audio;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.io.File;

public class Song {

    private MediaPlayer media;
    private String name;

    public Song(File mp3File){
        Media media = new Media(mp3File.toURI().toString());
        this.media = new MediaPlayer(media);
        this.name = stripExtension(mp3File.getName());
    }

    public String getName(){
        return name;
    }

    public MediaPlayer getMedia(){
        return media;
    }

    private static String stripExtension(String str){
        int pos = str.lastIndexOf('.');
        if (pos == -1)
            return str;

        return str.substring(0, pos);
    }
}
