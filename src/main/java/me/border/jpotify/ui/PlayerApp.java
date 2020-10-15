package me.border.jpotify.ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import me.border.jpotify.audio.Player;
import me.border.jpotify.storage.PlaylistManager;
import me.border.jpotify.ui.controllers.AppController;
import me.border.utilities.utils.URLUtils;

public class PlayerApp extends Application {
    public static Player player;
    public static AppController controller;

    public void start(String[] args){
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        stage.setTitle("Jpotify");
        stage.setResizable(false);
        stage.getIcons().add(new Image("/assets/icon.png"));

        FXMLLoader loader = new FXMLLoader(URLUtils.getURL("/ui/playerapp.fxml"));
        Parent root = loader.load();
        stage.setScene(new Scene(root, 1280, 800));

        controller = loader.getController();
        controller.initSliders();
        controller.initPlaylists(PlaylistManager.getInstance().getPlaylists());

        player = new Player();
        controller.initController(player);

        stage.show();
        controller.focus();
    }
}
