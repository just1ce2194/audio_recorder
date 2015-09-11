package sample.Sound;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 * An utility class for playing back audio files using Java Sound API.
 *
 */
public class AudioPlayer implements LineListener {


    /**
     * this flag indicates whether the playback completes or not.
     */
    private boolean playCompleted;

    /**
     * this flag indicates whether the playback is stopped or not.
     */
    private boolean isStopped;

    private boolean isPaused;

    private Clip audioClip;
    String audioFilePath = "temp.wav";
    /**
     * Load audio file before playing back
     *
     * @param audioFilePath
     *            Path of the audio file.
     * @throws IOException
     * @throws UnsupportedAudioFileException
     * @throws LineUnavailableException
     */
    public AudioPlayer()
            throws UnsupportedAudioFileException, IOException,
            LineUnavailableException {
        File audioFile = new File(audioFilePath);

        AudioInputStream audioStream = AudioSystem
                .getAudioInputStream(audioFile);

        AudioFormat format = audioStream.getFormat();

        DataLine.Info info = new DataLine.Info(Clip.class, format);

        audioClip = (Clip) AudioSystem.getLine(info);

        audioClip.addLineListener(this);

        audioClip.open(audioStream);
    }

    /**
     * Play a given audio file.
     *
     * @throws IOException
     * @throws UnsupportedAudioFileException
     * @throws LineUnavailableException
     */
     public void play() throws IOException {
         final Thread playThread = new Thread(new Runnable() {
             @Override
             public void run() {
                 audioClip.start();

                 playCompleted = false;
                 isStopped = false;

                 while (!playCompleted) {
                     if (isStopped) {
                         audioClip.stop();
                         break;
                     }
                     // wait for the playback completes
                     try {
                         Thread.sleep(20);
                     } catch (InterruptedException ex) {
                         ex.printStackTrace();

                         if (isPaused) {
                             audioClip.stop();
                         } else {
                             System.out.println("!!!!");
                             audioClip.start();
                         }
                     }
                 }

                 audioClip.close();
             }
         });
         playThread.start();



    }

    /**
     * Stop playing back.
     */
    public void stop() {
        isStopped = true;
    }


    /**
     * Listens to the audio line events to know when the playback completes.
     */
    @Override
    public void update(LineEvent event) {
        LineEvent.Type type = event.getType();
        if (type == LineEvent.Type.STOP) {
            if (isStopped || !isPaused) {
                playCompleted = true;
            }
        }
    }


}