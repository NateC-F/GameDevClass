package gameframework.sound;

import javax.sound.sampled.Clip;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;

/**
 * This is a wrapper for a java sound clip that allows us to treat
 * clips different, for example there are some game clips that we
 * want the sound to stop as soon as the stop command is issued, but
 * there are other where we want the sound to finish playing after
 * the stop is signaled. Normally we would extend the Clip class, but
 * since Clip is an interface we use containment/composition to
 * extend the functionality instead.
 *
 */
public class GameClip
{
    private Clip clip;

    /* The meaning of these flags is the following:
     * isPlaying              --> true if the clip is currently playing
     * allowPlayInterruption  --> if true we are allowed to interrupt this clip
     *                            and restart it even while its playing, otherwise
     *                            (if false) we cannot play it again until done.
     * finishPlayingAfterStop --> calling the stop method on this clip won't
     *                            stop sound playback if true.
     */
    private boolean isPlaying;
    private boolean finishPlayingAfterStop;
    private boolean allowPlayInterruption;

    public GameClip(Clip clip, boolean finishPlayingAfterStop, boolean allowPlayInterruption)
    {
        this.clip = clip;
        this.finishPlayingAfterStop = finishPlayingAfterStop;
        this.allowPlayInterruption = allowPlayInterruption;

        clip.addLineListener(new LineListener()
        {
            public void update(LineEvent le)
            {
                if (le.getType() == LineEvent.Type.START)
                        isPlaying = true;
                else if (le.getType() == LineEvent.Type.STOP)
                        isPlaying = false;
            }
        });
        isPlaying = false;

    }

    public void reset()
    {
        //Rewinds clip to the start (these three lines of code have that effect)
        clip.stop();
        clip.flush();
        clip.setFramePosition(0);
    }

    //This method plays a clip a number of times, use the value
    //Clip.LOOP_CONTINUOUSLY to play indefinitely
    public void play(int count)
    {
        if (!isPlaying || allowPlayInterruption)
        {
            //reset the clip so it can be replayed
            reset();
            //set looping if required (the loop method expects the number of
            //repetitions rather than the number of times, so decrease by 1.
            if (count != Clip.LOOP_CONTINUOUSLY)
                count--;
            if (count != 0)
                clip.loop(count);
            //start playing the clip
            clip.start();
        }
    }

    public void resume()
    {
        //continue playing the clip from where we left
        clip.start();
    }

    //this method stops and rewinds a clip (if the clip has a setting that requires
    //playback to finish then it won't be stopped).
    public void stop()
    {
        //depending on this setting we stop the sound right away or finish playback
        if (!finishPlayingAfterStop)
        {
            reset();
        }
    }

    public boolean isPlaying()
    {
        return isPlaying;
    }
    public boolean isFinishPlayingAfterStop()
    {
        return finishPlayingAfterStop;
    }

    public void setFinishPlayingAfterStop(boolean finishPlayingAfterStop)
    {
        this.finishPlayingAfterStop = finishPlayingAfterStop;
    }

    public boolean isAllowPlayInterruption()
    {
        return allowPlayInterruption;
    }

    public void setAllowPlayInterruption(boolean allowPlayInterruption)
    {
        this.allowPlayInterruption = allowPlayInterruption;
    }

    public Clip getClip()
    {
        return clip;
    }
}
