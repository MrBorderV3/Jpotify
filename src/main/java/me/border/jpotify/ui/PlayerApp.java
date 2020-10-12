package me.border.jpotify.ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import me.border.utilities.utils.URLUtils;

public class PlayerApp extends Application {

    public void start(String[] args){
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        stage.setTitle("Untitled");
        stage.setResizable(false);
        FXMLLoader loader = new FXMLLoader(URLUtils.getURL("/ui/playerapp.fxml"));
        Parent root = loader.load();
        stage.getIcons().add(new Image("/assets/icon.png"));
        stage.setScene(new Scene(root, 1280, 800));
        stage.show();
    }
}
