package chip8;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

/**
 *Based on michaelarnauts Sound.java class from its chip8-java project.
 *https://github.com/michaelarnauts/chip8-java/blob/master/Source/src/be/khleuven/arnautsmichael/chip8/Sound.java
 *
 * Class that manages the sound.
 */
public class Sound  {

    private boolean isEnabled;
    private boolean isPlaying;
    private AudioFormat af;
    private SourceDataLine sdl;
    
    private Thread playThread;
    
    private byte[] buf = new byte[256];
    
    /** Creates a new instance of Sound */
    public Sound(boolean isEnabled) {
        try {
            af = new AudioFormat(44100f, 8, 1, true, false);
            sdl = AudioSystem.getSourceDataLine(af);
            sdl.open(af);
            this.isEnabled = isEnabled;
            isPlaying = false;
            
            // generate the sound to play
            for (int i=0; i<buf.length; i++)
                buf[i] = 121;

            for (int i=buf.length/3; i<2*buf.length/3; i++)
                buf[i] = (byte)255-121;
        
        } catch (LineUnavailableException ex) {
            ex.printStackTrace();
            isEnabled = false;
        }
    }
        
    public void startSound() {
        if (isPlaying || !isEnabled) return;
        isPlaying = true;
        playThread = new PlayThread();
        playThread.setPriority(Thread.MAX_PRIORITY);
        playThread.start();
    }
    
    public void stopSound() {
        isPlaying = false;
    }

    public void setEnabled(boolean isEnabled) {
        this.isEnabled = isEnabled;
    }
    
    public boolean isEnabled() {
        return isEnabled;
    }
    
    class PlayThread extends Thread {
        
        public void run(){
            try {
                sdl.start();
                do {
                    sdl.write(buf, 0, buf.length);
                } while (isPlaying);
                sdl.stop();
                sdl.flush();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
    
}