package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.FileChooser;
import sample.Sound.AudioException;
import sample.Sound.AudioPlayer;
import sample.Sound.SoundRecorder;


import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;


public class Controller {
    @FXML
    public Label lblInfo;

    SoundRecorder recorder;
    AudioPlayer audioPlayer;
    boolean isStopped = false;
    boolean running = false;
    boolean isPlaying = false;
    String path;

    public void handleStartButtonAction(ActionEvent actionEvent) throws IOException {

        // if(setPathToFile()) {
        recorder = new SoundRecorder();
        lblInfo.setText("Recording...");
        running = true;

        final Thread stopper = new Thread(new Runnable() {
            @Override
            public void run() {
                while (running) {
                    try {
                        Thread.sleep(20L);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (isStopped) {
                        recorder.finish();
                        isStopped = false;
                        running = false;
                        break;
                    }
                }
            }


        });

        stopper.start();
        Thread starter = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    recorder.start();
                } catch (AudioException e) {
                    lblInfo.setText(e.toString());
                }
            }
        });
        starter.start();


    }


    public void handleStopButtonAction(ActionEvent actionEvent) {

        if(running) {
            isStopped = true;
            lblInfo.setText("Recording Finished!");
        }
        if(isPlaying){
            lblInfo.setText("Playing Stopped!");
            audioPlayer.stop();
            isPlaying = false;
        }
    }

    public boolean setPathToFile() {
        FileChooser fileChooser = new FileChooser();

        //Set extension filter
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("WAV files (*.wav)", "*wav");
        fileChooser.getExtensionFilters().add(extFilter);
        fileChooser.setInitialFileName("audioRecord");

        //Show save file dialog
        File file = fileChooser.showSaveDialog(null);

        if(file != null){
            path = file.getPath();
            if(!path.endsWith(".wav"))
                path+=".wav";
            return true;
        }
        return false;
    }

    public void handlePlayButtonAction(ActionEvent actionEvent) {
        try {
            audioPlayer = new AudioPlayer();
        } catch (UnsupportedAudioFileException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        }
        try {
            isPlaying = true;
            audioPlayer.play();
            lblInfo.setText("Playing...");
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public void handleSaveButtonAction(ActionEvent actionEvent) {
        if(setPathToFile()){
            try {
                recorder.save(path);
                lblInfo.setText("File saved to\r\n"+path);
            } catch (IOException e) {
                lblInfo.setText("Bad path.Please try again.");
                e.printStackTrace();
            }
        }

    }
}