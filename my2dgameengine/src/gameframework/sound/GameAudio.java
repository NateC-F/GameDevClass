package gameframework.sound;

import gameframework.GameThread;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;

/**
 * This class manages all audio for the game, including music
 * and all the different sound effects.
 */
public class GameAudio
{
    private GameClip mainTheme;

    public GameAudio()
    {
        mainTheme = null;
    }

    public void initMainTheme(String theme)
    {
        try
        {
            if (!theme.isEmpty())
            {
                mainTheme = getClip(theme, false, false);
//              mainTheme = GameThread.resourceManager.loadAudioResource(theme, GameThread.getCurrentLevel().getName(),
//                                                             false, false);
            }
        }
        catch (Exception e)
        {
            System.out.println("Failed to initialize main theme");
        }
    }

    public boolean setMainTheme(String theme)
    {
        boolean success = false;

        try
        {
            if (!theme.isEmpty())
            {
                //Make sure to stop any previous theme that might be playing
                if (mainTheme != null)
                    mainTheme.stop();
                initMainTheme(theme);
                success = true;
            }
        }
        catch (Exception e)
        {
            System.out.println("Failed to change main theme");
        }
        return success;
    }

    //Register a sound clip in the engine, we differentiate between sound clips that
    //end on a stop command and those that finish playback despite the stop.
    public void addSoundClip(String clipName, boolean keepPlayingAfterCommand,
                             boolean allowPlayRestart)
    {
        try
        {
            /* if the clip isn't registered already then store the clip in a map so that it can be
             * retrieved easily in the future using the clip's name */
              getClip(clipName, keepPlayingAfterCommand, allowPlayRestart);
//            GameThread.resourceManager.loadAudioResource(clipName, GameThread.getCurrentLevel().getName(),
//                                                         keepPlayingAfterCommand, allowPlayRestart);
        }
        catch (Exception e)
        {
            System.out.println("failed to register sound clip " + clipName);
        }

    }

    //Play the main theme, it is played in a loop
    public boolean playMainTheme(boolean playOnlyOnce)
    {
        boolean success = false;

        try
        {
            if (mainTheme != null)
            {
                //By default play only once
                int loopCount = 1;
                if (!playOnlyOnce)
                    loopCount = Clip.LOOP_CONTINUOUSLY;
                mainTheme.play(loopCount);
                success = true;
            }
        }
        catch (Exception e)
        {
            System.out.println("Failed to play main theme");
        }
        return success;
    }

    /*Now builds the GameClip object using the AudioData stored in the resource manager.
    Because only the audio data is stored in the ResourceManager a GameClip will need to be
    build and returned here so that other methods in the GameAudio class can use it*/
    private GameClip getClip(String clipName, boolean finishAfterStop, boolean allowInterrupt)
    {
        AudioData audioData = GameThread.resourceManager.loadAudioResource(clipName, GameThread.getCurrentLevel().getName());

        if (audioData != null)
        {
            Clip clip = null;
            try
            {
                clip = AudioSystem.getClip();
            }
            catch (LineUnavailableException e)
            {
                throw new RuntimeException(e);
            }
            try
            {
                clip.open(audioData.getFormat(), audioData.getAudioBytes(), 0, audioData.getAudioBytes().length);
                clip.start();
            }
            catch (LineUnavailableException e)
            {
                throw new RuntimeException(e);
            }
            return new GameClip(clip, finishAfterStop, allowInterrupt);
        }
        return null;
    }

    //This method plays the requested clip (must already be registered in the system)
    public void playClip(String clipName)
    {
        if (!GameThread.enableSoundEffects)
            return;

        GameClip clip = getClip(clipName, false, true);

        if (clip != null)
        {
            clip.play(1);
            //System.out.println("Clip " + clipName + " has started." );
        }
    }

    /* This version of play clip registers a sound clip in the engine if it isn't
     * already registered and then plays it, we differentiate between sound clips
     * that end when a command stops (A player releasing a key/button) and those
     * that finish playback despite the stop. */
    public void playClip(String clipName, boolean keepPlayingAfterCommand)
    {
        if (!GameThread.enableSoundEffects)
            return;

        addSoundClip(clipName, keepPlayingAfterCommand, false);
        playClip(clipName);
    }

    public void stopClip(String clipName)
    {
        GameClip clip = getClip(clipName, false, false);

        if (clip != null)
        {
            clip.stop();
            //System.out.println("Clip " + clipName + " has stopped." );
        }
    }

    public void resumeClip(String clipName)
    {
        if (!GameThread.enableSoundEffects)
            return;

        GameClip clip = getClip(clipName, false, false);

        if (clip != null)
        {
            clip.resume();
        }
    }

    public void stopMainTheme(boolean reset)
    {
        if (mainTheme == null)
            return;

        if (reset)
        {
            //rewind main theme before stopping
            mainTheme.reset();
        }
        mainTheme.stop();
    }

    public void resumeMainTheme()
    {
        mainTheme.resume();
    }
}
