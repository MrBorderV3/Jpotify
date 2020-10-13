package me.border.jpotify.ui.controllers;

import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.AnchorPane;
import me.border.jpotify.audio.util.AudioController;
import me.border.jpotify.audio.Player;
import me.border.jpotify.audio.Playlist;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class AppController {

    public AtomicReference<Player> playerRef = new AtomicReference<>();
    private AtomicReference<ToggleButton> toggledPlaylist = new AtomicReference<>();
    private Map<String, ToggleButton> playlistMap = new HashMap<>();

    public void initController(Player player){
        playerRef.set(player);
    }

    @FXML
    public Slider volSlider;
    @FXML
    public Slider timeSlider;
    @FXML
    public AnchorPane playlistPane;

    @FXML
    public void play(){
        playerRef.get().resume();
    }

    public void initTimeListener(){
        AudioController audioController = player().getCurrentSong().getController();
        audioController.init(timeSlider);
    }

    @FXML
    public void pause(){
        playerRef.get().pause();
    }

    @FXML
    public void clickable(){
        Scene scene = volSlider.getScene();
        scene.setCursor(Cursor.HAND);
    }

    @FXML
    public void unclickable(){
        Scene scene = volSlider.getScene();
        scene.setCursor(Cursor.DEFAULT);
    }

    public void resetSlider(){
        timeSlider.setValue(0);
    }

    public void initSliders() {
        volSlider.setValue(100);
        volSlider.valueProperty().addListener(observable -> player().setVolume(volSlider.getValue() / 100));
        timeSlider.setOnMousePressed(e -> {
            AudioController audioController = player().getCurrentSong().getController();
            audioController.sliderMoved();
        });
        timeSlider.valueProperty().addListener(observable -> {
            if (timeSlider.isValueChanging()) {
                AudioController audioController = player().getCurrentSong().getController();
                audioController.sliderMoved();
            }
        });
    }

    public void focusPlaylist(String name){
        if (toggledPlaylist.get() != null){
            toggledPlaylist.get().setSelected(false);
        }
        ToggleButton playlist = playlistMap.get(name);
        playlist.setSelected(true);
        toggledPlaylist.set(playlist);
    }

    public void initPlaylists(Collection<Playlist> playlists){
        double value = 0.0;
        for (Playlist playlist : playlists){
            ToggleButton toggleButton = new ToggleButton(playlist.getName());
            playlistMap.put(playlist.getName(), toggleButton);
            toggleButton.setOnMouseClicked(e -> {
                if (toggledPlaylist.get() != null){
                    toggledPlaylist.get().setSelected(false);
                }
                player().setPlaylist(playlist);
                toggledPlaylist.set(toggleButton);
            });
            AnchorPane.setTopAnchor(toggleButton, value);
            playlistPane.getChildren().add(toggleButton);
            value = value + 30.0;
        }
    }

    /*private void updateValues() {
        MediaPlayer mp = player().getCurrentSong().getMedia();
        Duration currentTime = mp.getCurrentTime();
        Duration duration = mp.getMedia().getDuration();
        timeSlider.setDisable(duration.isUnknown());
        if (!timeSlider.isDisabled() && duration.greaterThan(Duration.ZERO) && !timeSlider.isValueChanging()) {
            timeSlider.setValue(currentTime.divide(duration).toMillis() * 100.0);
        }
    }*/

    public Player player(){
        return playerRef.get();
    }
}
