package me.border.jpotify.ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import me.border.jpotify.audio.Player;
import me.border.jpotify.audio.Playlist;
import me.border.jpotify.storage.PlaylistManager;
import me.border.jpotify.ui.controllers.AppController;
import me.border.utilities.utils.URLUtils;

public class PlayerApp extends Application {

    private Player player = new Player();
    private PlaylistManager playlistManager = new PlaylistManager();

    public void start(String[] args){
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        stage.setTitle("Jpotify");
        stage.setResizable(false);
        FXMLLoader loader = new FXMLLoader(URLUtils.getURL("/ui/playerapp.fxml"));
        Parent root = loader.load();
        AppController controller = loader.getController();
        controller.playerRef.set(player);
        controller.initSlider();
        //stage.getIcons().add(new Image("/assets/icon.png"));
        stage.setScene(new Scene(root, 1280, 800));
        playlistManager.createPlaylist("myPlaylist");
        Playlist myPlaylist = playlistManager.getPlaylist("myPlaylist");
        player.setPlaylist(myPlaylist);
        player.playNormal();
        stage.show();

    }
}
