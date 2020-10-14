package me.border.jpotify.ui.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import me.border.jpotify.audio.util.AudioController;
import me.border.jpotify.audio.Player;
import me.border.jpotify.audio.Playlist;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class AppController {

    private AtomicReference<Player> playerRef = new AtomicReference<>();
    private AtomicReference<ToggleButton> toggledPlaylist = new AtomicReference<>();
    private Map<String, ToggleButton> playlistMap = new HashMap<>();

    public void initController(Player player){
        playerRef.set(player);
    }

    // SONG VOLUME SLIDER
    @FXML
    private Slider volSlider;
    // SONG TIME SLIDER
    @FXML
    private Slider timeSlider;

    // PANE WHERE ALL THE PLAYLISTS ARE AT
    @FXML
    private AnchorPane playlistPane;
    // MAIN PANE
    @FXML
    private AnchorPane mainPane;

    // PAUSE AND PLAY BUTTONS
    @FXML
    private ImageView pauseImage;
    @FXML
    private ImageView playImage;

    // CURRENT SONG TEXT
    @FXML
    private Text currentSong;

    public void initTimeListener(){
        AudioController audioController = player().getCurrentSong().getController();
        audioController.init(timeSlider);
    }

    @FXML
    private void ppButton(){
        boolean playing = player().isPlaying();
        if (playing){
            pause();
        } else {
            play();
        }
    }

    public void changeText(String songName){
        currentSong.setText(songName);
    }

    private void play(){
        adjustButton(true);
        playerRef.get().resume();
    }

    private void pause(){
        adjustButton(false);
        player().pause();
    }

    public void adjustButton(boolean playing){
        if (playing){
            playImage.setOpacity(0);
            pauseImage.setOpacity(100);
        } else {
            pauseImage.setOpacity(0);
            playImage.setOpacity(100);
        }
    }

    public void adjustVolume(double volume){
        Platform.runLater(() -> volSlider.setValue(volume * 100));
    }

    @FXML
    private void prev(){
        adjustButton(true);
        player().playLastSong();
    }

    @FXML
    private void next(){
        adjustButton(true);
        player().playNext();
    }

    @FXML
    private void clickable(){
        Scene scene = volSlider.getScene();
        scene.setCursor(Cursor.HAND);
    }

    @FXML
    private void unclickable(){
        Scene scene = volSlider.getScene();
        scene.setCursor(Cursor.DEFAULT);
    }

    @FXML
    public void focus(){
        mainPane.requestFocus();
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
                MouseButton button = e.getButton();
                if (button == MouseButton.SECONDARY){
                    // OPEN A MENU WITH OPTIONS LIKE : DELETE, COPY,
                }
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


    private Player player(){
        return playerRef.get();
    }
}
