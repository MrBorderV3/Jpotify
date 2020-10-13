package me.border.jpotify.audio;

import javafx.embed.swing.JFXPanel;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import me.border.jpotify.audio.util.AudioController;
import me.border.jpotify.util.Utils;

import java.io.File;

public class Song {

    private MediaPlayer media;
    private AudioController controller;
    private String name;

    public Song(File mp3File){
        // JavaFX is dumb and wants you to initiate JavaFxRuntime for this to work, so you create a dummy panel for the runtime to initiate.
        new JFXPanel();
        Media media = new Media(mp3File.toURI().toString());
        this.media = new MediaPlayer(media);
        this.name = Utils.stripExtension(mp3File.getName());
        this.controller = new AudioController(this.media);
    }

    public String getName(){
        return name;
    }

    public AudioController getController(){
        return controller;
    }

    public MediaPlayer getMedia(){
        return media;
    }

    @Override
    public String toString() {
        return "Song{" + "name='" + name + '\'' + '}';
    }
}
