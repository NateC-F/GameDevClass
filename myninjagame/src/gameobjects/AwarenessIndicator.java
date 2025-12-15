package gameobjects;

import gameframework.animations.Animation;
import gameframework.animations.Spritesheet;
import gameframework.gameobjects.GameObject;
import gameframework.gameobjects.GameObjects;
import gameframework.gameobjects.InanimateObject;

// Small indicator of an object or characters state... can show a "?" or a "!" depending on its settings
public class AwarenessIndicator extends InanimateObject {
    private final int HIDE_BUFFER = 75;
    private GameObject objectToWatch;
    private int bufferX;
    private int bufferY;
    private int alertDisplayTime = 4000;
    private long lastRecordedTime = System.currentTimeMillis();
    private Animation empty;
    private Animation suspicion;
    private Animation alert;

    public AwarenessIndicator(String name, int x, int y, int z, int scaleWidth, int scaleHeight, GameObject object, int offsetX, int offsetY) {
        super(name, x, y, z, scaleWidth, scaleHeight);
        requiresUpdating = true;
        objectToWatch = object;
        bufferX = offsetX;
        bufferY = offsetY;
        initializeAnimations();
        disableCollision = true;
        //System.out.println("The awareness indicator was created for " + object);
    }

    // Sets the object for this tool to watch (appear above)
//    public void setObjectToWatch(GameObject object){
//        objectToWatch = object;
//    }

    public void initializeAnimations(){
        Spritesheet emptySpritesheet = new Spritesheet("almost_empty_2.png",
                1, 1, 1, false);
        empty = new Animation(emptySpritesheet, scaleWidth, scaleHeight);
        empty.setSpeed(0);
        curAnimation = empty;

        Spritesheet suspicionSpritesheet = new Spritesheet("suspicion_indicator.png",
                1, 1, 1, false);
        suspicion = new Animation(suspicionSpritesheet, scaleWidth, scaleHeight);
        suspicion.setSpeed(0);

        Spritesheet alertSpritesheet = new Spritesheet("alert_indicator.png",
                1, 1, 1, false);
        alert = new Animation(alertSpritesheet, scaleWidth, scaleHeight);
        alert.setSpeed(0);
    };

    @Override
    public boolean handleObjectCollision(GameObject object) {return false;}

    @Override
    public boolean isUnmovable() {return false;}

//    @Override
//    public boolean isInanimate() {return false;}

    // Sets the indicator to "?"
    public void setSuspicious(){
        curAnimation = suspicion;
    }

    // Sets the indicator to "!" but also initiates a timer so that the "!" is not on forever
    public void setAlert(){
        if (curAnimation != alert) {        // only reset timer if we weren't already in alert
            lastRecordedTime = System.currentTimeMillis();
        }
        curAnimation = alert;
    }

    // Turns indicator off
    public void setOff(){
        curAnimation = empty;
    }

    @Override
    public void update(GameObjects objects) {
        super.update(objects);
        // If object we are supposed to watch actually exists... set the position to the object's
        // positon, plus any buffers
        if (!(objectToWatch == null))
            if (curAnimation == empty) // If we are on the empty animation, set the alert to be inside the object (hidden)
                setPosition((objectToWatch.getX() + bufferX), (objectToWatch.getY() + bufferY) + HIDE_BUFFER);
            else // Otherwise display it above the character
                setPosition((objectToWatch.getX() + bufferX), (objectToWatch.getY() + bufferY));

        // Check if the current animation is alert, if yes then we need to track how much time has passed
        if (curAnimation == alert){
            long currentTime = System.currentTimeMillis(); // get the current time
            // if the current time less the time recorded when the alert animation was set is greater than the displayTime
            if (currentTime - lastRecordedTime > alertDisplayTime)
                setOff(); // then set the indicator off
        }
    }
}
