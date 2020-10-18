package me.border.jpotify.ui.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import me.border.jpotify.audio.Song;
import me.border.jpotify.audio.util.AudioController;
import me.border.jpotify.audio.Player;
import me.border.jpotify.audio.Playlist;
import me.border.jpotify.audio.util.YTConverter;
import me.border.jpotify.storage.PlaylistManager;
import me.border.utilities.ui.javafx.fxml.AlertBox;
import me.border.utilities.ui.javafx.fxml.InputBox;
import me.border.utilities.utils.AsyncScheduler;
import me.border.utilities.utils.Response;
import me.border.utilities.utils.ref.MuteableWeakReference;
import me.border.utilities.utils.ref.StrongReference;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class AppController {

    private StrongReference<Player> playerRef = new StrongReference<>();
    private StrongReference<ToggleButton> toggledPlaylist = new StrongReference<>();
    private Map<String, ToggleButton> playlistMap = new LinkedHashMap<>();

    private MuteableWeakReference<Playlist> clipboard = new MuteableWeakReference<>();

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

    // Youtube link text field
    @FXML
    private TextField youtubeLink;
    @FXML
    private Button searchButton;

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
    private void search(){
        if (searchButton.isDisabled())
            return;

        String link = youtubeLink.getText();
        if (player().getPlaylist() == null){
            AlertBox.showAlert("Please choose a playlist first.", "Error");
        }
        if (link.isEmpty()){
            AlertBox.showAlert("Invalid Link", "Invalid Link");
        } else {
            searchButton.setDisable(true);
            searchButton.setText("Searching...");
            CompletableFuture<Response<Integer>> future = YTConverter.addSong(link, player().getPlaylist());
            future.whenComplete((b, throwable) -> {
                if (b.getAnswer()) {
                    Platform.runLater(() -> AlertBox.showAlert("Successfully downloaded requested song", "Success"));
                } else {
                    if (b.getContext() == 1) {
                        Platform.runLater(() -> AlertBox.showAlert("An error has occurred and the requested song wasn't found, please double check the search query to make sure it is valid.", "Failure"));
                    }
               }
                Platform.runLater(() -> {
                    searchButton.setText("Search");
                    searchButton.setDisable(false);
                });
            });
        }
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
        ToggleButton toggledPlaylist = this.toggledPlaylist.get();
        if (toggledPlaylist != null){
            toggledPlaylist.setSelected(false);
        }
        ToggleButton playlist = playlistMap.get(name);
        playlist.setSelected(true);
        this.toggledPlaylist.set(playlist);
    }

    public void initPlaylists(Collection<Playlist> playlists){
        ContextMenu menu = getPlaylistPaneMenu();
        playlistPane.setOnContextMenuRequested(e -> {
            Point point = MouseInfo.getPointerInfo().getLocation();
            double x = point.getX();
            double y = point.getY();
            menu.show(playlistPane, x, y);
        });
        double value = 0.0;
        for (Playlist playlist : playlists){
            ToggleButton toggleButton = createPlaylistButton(playlist);
            AnchorPane.setTopAnchor(toggleButton, value);
            playlistPane.getChildren().add(toggleButton);
            value = value + 30.0;
        }
    }

    private ToggleButton createPlaylistButton(Playlist playlist){
        ToggleButton toggleButton = new ToggleButton(playlist.getName());
        playlistMap.put(playlist.getName(), toggleButton);
        toggleButton.setContextMenu(getPlaylistMenu(playlist));
        toggleButton.setOnMouseClicked(e -> {
            MouseButton button = e.getButton();
            if (button == MouseButton.SECONDARY){
                return;
            }
            ToggleButton toggledPlaylist = this.toggledPlaylist.get();
            if (toggledPlaylist != null){
                if (toggledPlaylist.equals(toggleButton)){
                    toggledPlaylist.setSelected(true);
                    return;
                }
                toggledPlaylist.setSelected(false);
            }
            player().setPlaylist(playlist);
            this.toggledPlaylist.set(toggleButton);
        });

        return toggleButton;
    }

    private ContextMenu getPlaylistMenu(Playlist playlist){
        ContextMenu menu = new ContextMenu();
        MenuItem deleteItem = new MenuItem("Delete");
        MenuItem copyItem = new MenuItem("Copy");

        copyItem.setOnAction(e -> clipboard.set(playlist));

        deleteItem.setOnAction(e -> {
            PlaylistManager.getInstance().removePlaylist(playlist);
            ToggleButton playlistButton = playlistMap.remove(playlist.getName());
            playlistPane.getChildren().remove(playlistButton);
            rearrangePlaylists();
        });

        menu.getItems().addAll(copyItem, deleteItem);
        return menu;
    }

    private ContextMenu getPlaylistPaneMenu(){
        ContextMenu menu = new ContextMenu();
        MenuItem createItem = new MenuItem("New");
        MenuItem pasteItem = new MenuItem("Paste");

        createItem.setOnAction(e -> {
            //Create and receive input from an input box.
            String input = InputBox.showAlert("Choose a name for your new playlist.", "Input");
            if (input.isEmpty()){
                AlertBox.showAlert("Invalid Name! Please use a different name.", "Error");
                return;
            }
            PlaylistManager manager = PlaylistManager.getInstance();
            if (manager.contains(input)){
                AlertBox.showAlert("A playlist with that name already exists! Please use a different name.", "Error");
                return;
            }
            Playlist playlist = PlaylistManager.getInstance().createPlaylist(input);
            createPlaylistButton(playlist);
            rearrangePlaylists();
        });

        menu.setOnShowing(e -> {
            if (clipboard.isEmpty()){
                pasteItem.setDisable(true);
            } else {
                pasteItem.setDisable(false);
            }
        });

        pasteItem.setOnAction(e -> {
            if (pasteItem.isDisable())
                return;

            // Even though it checks this when the menu opens, the GC might run after showing but before the click so we check again.
            if (clipboard.isEmpty()){
                pasteItem.setDisable(true);
                return;
            }
            Playlist copy = clipboard.get();
            String name = InputBox.showAlert("Choose a name for your new playlist.", "Input");
            if (name.isEmpty()){
                AlertBox.showAlert("Invalid Name! Please use a different name.", "Error");
                return;
            }
            Playlist playlist = createPlaylist(name);
            if (playlist != null){
                AsyncScheduler.runTaskAsync(() -> {
                    for (Song song : copy.getSongs()) {
                        try {
                            File songDir = song.getDir();
                            Path songPath = Paths.get(songDir.toURI());
                            File newSongDir = new File(playlist.getDir(), songDir.getName());
                            Path newPath = Paths.get(newSongDir.toURI());
                            Files.copy(songPath, newPath, StandardCopyOption.REPLACE_EXISTING);
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }
                });
            }
        });

        menu.getItems().addAll(createItem, pasteItem);
        return menu;
    }

    private Playlist createPlaylist(String name){
        PlaylistManager manager = PlaylistManager.getInstance();
        if (manager.contains(name)){
            AlertBox.showAlert("A playlist with that name already exists! Please use a different name.", "Error");
            return null;
        }
        Playlist playlist = PlaylistManager.getInstance().createPlaylist(name);
        createPlaylistButton(playlist);
        rearrangePlaylists();

        return playlist;
    }

    private void rearrangePlaylists(){
        double value = 0;
        Collection<ToggleButton> toggleButtons = playlistMap.values();
        playlistPane.getChildren().removeAll(toggleButtons);
        for (ToggleButton toggleButton : toggleButtons){
            AnchorPane.setTopAnchor(toggleButton, value);
            playlistPane.getChildren().add(toggleButton);
            value = value + 30.0;
        }
    }

    private Player player(){
        return playerRef.get();
    }
}
