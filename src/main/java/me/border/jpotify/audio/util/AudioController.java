package me.border.jpotify.audio.util;

import javafx.beans.InvalidationListener;
import javafx.scene.control.Slider;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;


public class AudioController {

    private InvalidationListener listener = observable -> updateValues();

    private MediaPlayer mp;
    private Slider timeSlider;

    public AudioController(MediaPlayer mp){
        this.mp = mp;
    }

    public void init(Slider timeSlider){
        this.timeSlider = timeSlider;
        mp.currentTimeProperty().removeListener(listener);
        mp.currentTimeProperty().addListener(listener);
    }

    public void sliderMoved(){
        mp.seek(mp.getMedia().getDuration().multiply(timeSlider.getValue() / 100.0));
        updateValues();
    }

    private void updateValues() {
        Duration currentTime = mp.getCurrentTime();
        Duration duration = mp.getMedia().getDuration();
        timeSlider.setDisable(duration.isUnknown());
        if (!timeSlider.isDisabled() && duration.greaterThan(Duration.ZERO) && !timeSlider.isValueChanging()) {
            timeSlider.setValue(currentTime.divide(duration).toMillis() * 100.0);
        }
    }
}
