package com.example.mp3player;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class PlayerController {
    private Media media;
    private MediaPlayer mediaPlayer;
    private FileChooser fileChooser;

    private DirectoryChooser dirChooser;
    private File musicDirectory;
    private File[] files;
    private ArrayList<File> songs;

    private int songNumber;

    private Timeline timeline;
    private boolean running;

    @FXML
    private ProgressBar progressBar;
    @FXML
    private Label musicTitleLabel;
    @FXML
    private Label musicDurationLabel;
    @FXML
    private Slider volumeSlider;

    @FXML
    private Button playBtn, pauseBtn, stopBtn, backBtn, nextBtn;

    @FXML
    protected void onOpenFile() throws IOException {
        String userHomeDir = System.getProperty("user.home");
        File defaultFile = new File(userHomeDir + "\\Music");
        fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extensionFilter = new FileChooser.ExtensionFilter("MP3 files (*.mp3)", "*.mp3");
        fileChooser.getExtensionFilters().add(extensionFilter);
        fileChooser.setInitialDirectory(defaultFile);
        Stage dialogeStage = new Stage();
        File mp3File = fileChooser.showOpenDialog(dialogeStage);
        if (mp3File != null) {
            media = new Media(mp3File.toURI().toString());
            if (mediaPlayer != null) {
                stopMedia(mediaPlayer);
                mediaPlayer = new MediaPlayer(media);
                changeVolume();
                playMedia(mediaPlayer);
            } else {
                mediaPlayer = new MediaPlayer(media);
                changeVolume();
                playMedia(mediaPlayer);
            }
            musicTitleLabel.setText(mp3File.getName());
            musicTitleLabel.setContentDisplay(ContentDisplay.CENTER);

        }
    }

    @FXML
    protected void onOpenFolder() throws IOException {
        String userHomeDir = System.getProperty("user.home");
        File defaultDirectory = new File(userHomeDir + "\\Music");
        dirChooser = new DirectoryChooser();
        dirChooser.setInitialDirectory(defaultDirectory);
        Stage dialogeStage = new Stage();
        File selectedDirectory = dirChooser.showDialog(dialogeStage);
        if (selectedDirectory != null) {
            musicDirectory = selectedDirectory;
            files = musicDirectory.listFiles();
            songs = new ArrayList<File>();
            if (files != null) {
                for (File file : files) {
                    songs.add(file);
                    System.out.println(file);
                }
            }
            musicTitleLabel.setText(songs.get(songNumber).getName().toString());
            media = new Media(songs.get(songNumber).toURI().toString());
            mediaPlayer = new MediaPlayer(media);
            playMedia(mediaPlayer);
        }
    }


    @FXML
    protected void onPlay() {
        if (mediaPlayer.getStatus().equals(MediaPlayer.Status.PLAYING)) {
            pauseMedia(mediaPlayer);
        } else {
            startTimer();
            playMedia(mediaPlayer);
        }
    }

    @FXML
    protected void onStop() {
        if (mediaPlayer.getStatus().equals(MediaPlayer.Status.PLAYING) || mediaPlayer.getStatus().equals(MediaPlayer.Status.STOPPED)) {
            mediaPlayer.stop();
        }
    }

    @FXML
    protected void onPause() {
        if (mediaPlayer.getStatus().equals(MediaPlayer.Status.PLAYING)) {
            pauseMedia(mediaPlayer);
        }
    }

    @FXML
    protected void onPrevious() {
        if(songNumber > 0 ){
            songNumber--;
            if(running == true){
                cancelTimer();
                stopMedia(mediaPlayer);
            }
            media = new Media(songs.get(songNumber).toURI().toString());
            mediaPlayer = new MediaPlayer(media);
            playMedia(mediaPlayer);
        }else{
            songNumber = songs.size()-1;
            System.out.println(songNumber);
            if(running == true){
                cancelTimer();
                stopMedia(mediaPlayer);
            }
            media = new Media(songs.get(songNumber).toURI().toString());
            mediaPlayer = new MediaPlayer(media);
            playMedia(mediaPlayer);
        }
    }

    @FXML
    protected void onNext() {
        if(songNumber < songs.size()-1 ){
            songNumber++;
            if(running == true){
                cancelTimer();
                stopMedia(mediaPlayer);
            }
            media = new Media(songs.get(songNumber).toURI().toString());
            mediaPlayer = new MediaPlayer(media);
            playMedia(mediaPlayer);
        }else{
            songNumber = 0;

            if(running == true){
                cancelTimer();
                stopMedia(mediaPlayer);
            }
            media = new Media(songs.get(songNumber).toURI().toString());
            mediaPlayer = new MediaPlayer(media);
            playMedia(mediaPlayer);
        }
    }

    @FXML
    protected void onClose() {
        Platform.exit();
    }

    public void playMedia(MediaPlayer md) {
        changeVolume();
        startTimer();
        md.play();

    }

    public void pauseMedia(MediaPlayer md) {
        md.pause();
    }

    public void stopMedia(MediaPlayer md) {
        cancelTimer();
        md.stop();
    }


    public void changeVolume() {
        volumeSlider.valueProperty().addListener(new ChangeListener<Number>() {

            @Override
            public void changed(ObservableValue<? extends Number> arg0, Number arg1, Number arg2) {

                mediaPlayer.setVolume(volumeSlider.getValue() * 0.01);
            }
        });
    }

    public void startTimer() {
        timeline = new Timeline();
        timeline.getKeyFrames().add(new KeyFrame(Duration.millis(100), e -> {
            double current = mediaPlayer.getCurrentTime().toSeconds();
            double end = mediaPlayer.getTotalDuration().toSeconds();
            progressBar.setProgress(current / end);
            int currentTime = (int) current;
            String time = getDuration(currentTime);
            musicDurationLabel.setText(time);
            if (current / end == 1) {
                timeline.stop();
            }
        }));
        running = true;
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

    public void cancelTimer() {
        timeline.stop();
    }

    public String getDuration(int seconds) {

        return getDuration(seconds / 60, seconds);
    }

    public String getDuration(int minutes, int seconds) {
        int hours = minutes / 60;
        int remainingMinutes = minutes % 60;
        int remainingSeconds = seconds % 60;
        String timeString = String.format("%02d:%02d", remainingMinutes, remainingSeconds);
        return  timeString;
    }
}
