package me.border.jpotify.ui.controllers;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Slider;
import javafx.stage.Stage;
import me.border.jpotify.audio.Player;

import java.util.concurrent.atomic.AtomicReference;

public class AppController {

    public AtomicReference<Player> playerRef = new AtomicReference<>();

    @FXML
    public Slider slider;

    @FXML
    public void play(){
        playerRef.get().resume();
    }

    @FXML
    public void pause(){
        playerRef.get().pause();
    }

    @FXML
    public void clickable(){
        Scene scene = slider.getScene();
        scene.setCursor(Cursor.HAND);
    }

    @FXML
    public void unclickable(){
        Scene scene = slider.getScene();
        scene.setCursor(Cursor.DEFAULT);
    }

    public void initSlider() {
        slider.setValue(100);
        slider.valueProperty().addListener(observable -> player().setVolume(slider.getValue() / 100));
    }

    public Player player(){
        return playerRef.get();
    }
}
