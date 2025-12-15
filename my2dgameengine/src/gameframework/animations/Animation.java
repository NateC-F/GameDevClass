package gameframework.animations;

import gameframework.GameThread;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Animation
{
    private String name;
    private BufferedImage[] frames;
    private int frameCount;
    private int curFrameIndex;
    private BufferedImage curFrame;
    private ArrayList<SpriteBorder> frameBorders;
    private int scaleWidth;
    private int scaleHeight;
    private int speed;
    private int speedCounter;
    private boolean paused;
    private boolean runOnlyOnce;
    private int timesToRun;
    private int runsCounter;
    private String soundEffect;

    public Animation(Spritesheet spritesheet, int scaleWidth, int scaleHeight)
    {
        name = spritesheet.getName();
        frames = spritesheet.convertToImageArray();
        frameCount = frames.length;
        curFrameIndex = 0;
        curFrame = frames[curFrameIndex];
        this.scaleHeight = scaleHeight;
        this.scaleWidth = scaleWidth;
        speed = 1;
        speedCounter = 0;
        paused = false;
        runOnlyOnce = false;
        runsCounter = 0;
        timesToRun = -1; //set by default to -1 which means run indefinitely
        soundEffect = "";
        initializeFrameBorders();
    }

    public Animation(String name, BufferedImage[] frames, int scaleWidth, int scaleHeight)
    {
        this.name = name;
        this.frames = frames;
        frameCount = frames.length;
        curFrameIndex = 0;
        curFrame = frames[curFrameIndex];
        this.scaleHeight = scaleHeight;
        this.scaleWidth = scaleWidth;
        speed = 1;
        speedCounter = 0;
        paused = false;
        runOnlyOnce = false;
        runsCounter = 0;
        timesToRun = -1; //set by default to -1 which means run indefinitely
        soundEffect = "";
        initializeFrameBorders();
    }

    public Animation(BufferedImage image, String name,  int scaleWidth, int scaleHeight)
    {
        this.name = name;
        frames = new BufferedImage[1];
        curFrame = frames[0] = image;
        frameCount = 1;
        curFrameIndex = 0;
        this.scaleHeight = scaleHeight;
        this.scaleWidth = scaleWidth;
        speed = 1;
        speedCounter = 0;
        paused = false;
        runOnlyOnce = false;
        runsCounter = 0;
        timesToRun = -1; //set by default to -1 which means run indefinitely
        soundEffect = "";
        initializeFrameBorders();
    }


    public void reset()
    {
        paused = false;
        curFrameIndex = 0;
        runsCounter = 0;
        speedCounter = 0;
    }

    public String getName()
    {
        return name;
    }

    public int getScaleWidth()
    {
        return scaleWidth;
    }

    public void setScaleWidth(int scaleWidth)
    {
        if (scaleWidth > 0)
            this.scaleWidth = scaleWidth;
    }

    public int getScaleHeight()
    {
        return scaleHeight;
    }

    public void setScaleHeight(int scaleHeight)
    {
        if (scaleHeight > 0)
            this.scaleHeight = scaleHeight;
    }

    /*
     * Link a sound effect that gets played whenever this animation starts.
     * If the effect is fully synchronized then it stops as soon as the animation
     * ends, otherwise it finishes playback after the animation has ended.
     */
    public void linkSoundEffect(String soundEffect, boolean fullySynchronized,
                                boolean allowPlayRestart)
    {
        if (!soundEffect.isEmpty())
        {
            this.soundEffect = soundEffect;
            GameThread.gameAudio.addSoundClip(soundEffect, fullySynchronized ? false : true,
                    allowPlayRestart);
        }
    }

    /*
     * This method is used to obtain and store the borders of each frame of the animation (the actual
     * sprite borders not including the transparent background), having the borders handy is very useful
     * for collision detection
     */
    private void initializeFrameBorders()
    {
        frameBorders = new ArrayList<SpriteBorder>();

        for ( int i = 0; i < frameCount; i++)
        {
            SpriteBorder s = SpriteBorder.getSpriteBorders(
                    frames[i], scaleWidth, scaleHeight);
            frameBorders.add(s);
        }
    }

    public SpriteBorder getCurrentFrameBorders(int posX, int posY, boolean reposition)
    {

        SpriteBorder curSpriteBorders = (SpriteBorder) frameBorders.get(curFrameIndex);

        //If requested (reposition is true) then adjusts all border point positions
        //to reflect the current sprite position
        if ( reposition )
            return curSpriteBorders.reposition(posX, posY);
        return curSpriteBorders;
    }

    public Rectangle getCurrentFrameBounds()
    {
        return frameBorders.get(curFrameIndex).getBordersRectangle();
    }

    public int getCurrentFrameIndex()
    {
        return curFrameIndex;
    }

    public void setCurrentFrame(int newFrameIndex)
    {
        if ( newFrameIndex >= 0 && newFrameIndex < frameCount)
            curFrameIndex = newFrameIndex;
    }

    /* These method can be used to convert the border point positions for every frame in the animation
     * from relative coords based on the top left corner of the bounds rectangle of the frame to actual
     * absolute coordinates in the game world. */
    public void adjustFrameBordersPosition(int posX, int posY)
    {
        for ( int i = 0; i < frameCount; i++)
        {
            SpriteBorder s = frameBorders.get(i);
            frameBorders.set(i, s.reposition(posX, posY));
        }
    }

    public void nextFrame()
    {
        if (isPaused())
            return;

        //If a sound effect is linked to the animation, play when it starts
        if (curFrameIndex == 0 && speedCounter == 0)
            playSoundEffect();

        speedCounter++;

        if (speedCounter == getSpeed())
        {
            curFrameIndex++;
            speedCounter = 0;
        }

        if ( curFrameIndex == frameCount )
        {
            curFrameIndex = 0;
            runsCounter++;

            if (runsCounter == timesToRun)
            {
                runsCounter = 0;
                curFrameIndex = frameCount - 1;
                pause();
            }
        }
        curFrame = frames[curFrameIndex];
    }

    public void drawFrame(Graphics g, int x, int y)
    {
        g.drawImage(curFrame, x, y, scaleWidth, scaleHeight, null );
    }

    public int getSpeed()
    {
        return speed;
    }

    public void setSpeed(int speed)
    {
        this.speed = speed;
    }

    public boolean isPaused()
    {
        return paused;
    }

    public void pause()
    {
        paused = true;
    }

    public boolean atLastFrame()
    {
        return curFrameIndex == frameCount - 1;
    }

    //reset and stop all side effects from the animation like any playing sound effects
    public void stop()
    {
        //reset();
        stopSoundEffect();
    }

    public void resume()
    {
        paused = false;
    }

    public void runOnlyOnce()
    {
        runOnlyOnce = true;
        setRunTimes(1);
    }

    public void setRunTimes(int timesToRun)
    {
        runOnlyOnce = false;
        this.timesToRun = timesToRun;
    }

    public Animation HorizontalFlip(){
        BufferedImage[] reverseFrames = new BufferedImage[frames.length];

        for(int i = 0; i < frames.length; i++){
            int w = frames[i].getWidth();
            int h = frames[i].getHeight();
            BufferedImage flipped = new BufferedImage(w,
                    h, frames[i].getType());
            Graphics2D g = flipped.createGraphics();

            AffineTransform tx = AffineTransform.getScaleInstance(-1,1);
            tx.translate(-w, 0);

            g.drawImage(frames[i], tx, null);
            g.dispose();

            reverseFrames[i] = flipped;
        }

        return new Animation(name, reverseFrames, scaleWidth, scaleHeight);
    }

    public Animation VerticalFlip(){
        BufferedImage[] reverseFrames = new BufferedImage[frames.length];

        for(int i = 0; i < frames.length; i++){
            int w = frames[i].getWidth();
            int h = frames[i].getHeight();
            BufferedImage flipped = new BufferedImage(w,
                    h, frames[i].getType());
            Graphics2D g = flipped.createGraphics();

            AffineTransform tx = AffineTransform.getScaleInstance(1,-1);
            tx.translate(0, -h);

            g.drawImage(frames[i], tx, null);
            g.dispose();

            reverseFrames[i] = flipped;
        }

        return new Animation(name, reverseFrames, scaleWidth, scaleHeight);
    }

    private void stopSoundEffect()
    {
        if (!soundEffect.isEmpty())
            GameThread.gameAudio.stopClip(soundEffect);
    }

    private void playSoundEffect()
    {
        if (!soundEffect.isEmpty())
            GameThread.gameAudio.playClip(soundEffect);
    }

}
