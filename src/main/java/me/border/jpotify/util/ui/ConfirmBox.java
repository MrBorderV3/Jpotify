package me.border.jpotify.util.ui;

import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import me.border.jpotify.util.ui.controllers.ConfirmBoxController;
import me.border.utilities.utils.URLUtils;

import java.io.IOException;

public class ConfirmBox {

    private ConfirmBox(){ }

    public static boolean showAlert(String alertMessage, String windowTitle) {
        try {
            Stage window = new Stage();
            window.setTitle(windowTitle);
            window.getIcons().add(new Image("/assets/icon.png"));
            window.initStyle(StageStyle.TRANSPARENT);
            window.setResizable(false);
            window.initModality(Modality.APPLICATION_MODAL);
            FXMLLoader loader = new FXMLLoader(URLUtils.getURL("/ui/ConfirmBox.fxml"));
            Parent root = loader.load();
            window.setScene(new Scene(root));
            ConfirmBoxController confirmBoxController = loader.getController();
            confirmBoxController.setLabel(alertMessage);
            window.setOnCloseRequest(Event::consume);
            window.showAndWait();

            return confirmBoxController.getBool();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
