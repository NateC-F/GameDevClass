package gameobjects;

import gamecharacters.NinjaPlayer;
import gameframework.GameData;
import gameframework.GameThread;
import gameframework.animations.Animation;
import gameframework.animations.Spritesheet;
import gameframework.gameobjects.GameObject;
import gameframework.gameobjects.GameObjects;
import gameframework.gameobjects.InanimateObject;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

import static java.lang.Math.sqrt;

public class SkeletonBone extends InanimateObject {

    private NinjaPlayer ninja;
    private final int TARGET_Y_BUFFER = 75;
    private final int SPAWN_Y_BUFFER = 50;
    private boolean atTarget = false;
    private boolean atNinja = false;
    private boolean hone = false;
    private boolean honePeriodExhausted = false;
    Point targetPosition;
    Point startPosition;
    private long lastRecordedTime = 0;
    private long honeDelay = 1000;
    private long ttlStartTime = 0;
    private int ttlDelay;
    private double vx;
    private double vy;
    private int speed = 10;
    private Animation disappear;
    private Animation staticView;
    //Added By Logan
    private final int DAMAGE = 10;
    //

    private Random rand = new Random();
    public SkeletonBone(String name,
                        int x, int y, int z,
                        int scaleWidth, int scaleHeight, Point targetPosition, int TTL){

        super(name, x, y, z, scaleWidth, scaleHeight);
        setType(NinjaGameObjectType.SKELETON_BONE);
        this.targetPosition = targetPosition;
        this.startPosition = new Point(x , y); // Adds a buffer amount so that it can more accurately advance the target
        requiresUpdating = true;
        this.ttlDelay = TTL;
        this.ttlStartTime = System.currentTimeMillis();

        this.targetPosition.y = this.targetPosition.y - 75;




        setGravity(0);
        //Added By Logan
        setDamage(DAMAGE);
        //

        initVelocityVectors();
        initializeAnimations();

    }


    public void initializeAnimations(){


        Spritesheet emptySpritesheet = new Spritesheet("bone_static.png",
                1, 1, 1, false);
        Animation empty = new Animation(emptySpritesheet, scaleWidth, scaleHeight);
        empty.setSpeed(0);
        disappear = empty;

        Spritesheet staticSpritesheet = new Spritesheet("skeleton_bone_throw.png",
                4, 1, 4, false);
        Animation staticImage = new Animation(staticSpritesheet, scaleWidth, scaleHeight);
        empty.setSpeed(2);
        staticView = staticImage;
        curAnimation = staticImage;

    }

    @Override
    public boolean handleObjectCollision(GameObject collidingObject){
        if (!collidingObject.isDisableCollision()) {
            GameThread.data.removeObjectWhenSafe(this);
            return true;
        }
        else
            return true;
    }

    @Override
    public boolean shouldIgnoreCollisionWith(GameObject other){
        boolean shouldIgnore = false;

        switch(other.getType()){

            case NinjaGameObjectType.SKELETON:
                shouldIgnore = true;
                break;

            case NinjaGameObjectType.ELF_PLAYER:
                shouldIgnore = true;
                break;

        }
        return shouldIgnore;
    }

    @Override
    public boolean isUnmovable(){ return false;}

    @Override
    public void update(GameObjects objects) {
        super.update(objects);

        if(ninja == null){

            for(GameObject objs : objects){

                if(objs instanceof NinjaPlayer){

                    ninja = (NinjaPlayer) objs;
                    break;
                }
            }

            if(ninja == null);
            return;
        }

        // If the projectile is at the target position then it is time to break the projectile motion loop
        if (getPosition() == targetPosition){
            atTarget = true;
            curAnimation = disappear;
        }

        // get the current system time
        long currentTime = System.currentTimeMillis();
        // If the initial delay has passed, then hone for a random amount of time
        if (currentTime - lastRecordedTime > honeDelay){ // Check if we have passed the initial delay
            if (!honePeriodExhausted) { // Check if the shuriken hone period has already been used
                // if it has not been used yet, then it's time to use it...
                hone = true;
                lastRecordedTime = currentTime;
                // RESET the delay so that the hone period has a different delay number than the original
                honeDelay = rand.nextInt(1000, 2000);
                honePeriodExhausted = true; // then set the hone period used flag to true so that we cannot use it again
            }
            else { // If we get here it means it's time to shutoff the honing, because the random hone period has ended
                hone = false;
            }
        }

        if (atNinja){
            setPosition(startPosition.x, startPosition.y);
            atNinja = false;
        }

        if (!atTarget){

            int currentX = getX();
            int currentY = getY();

            if (hone){ // Hone or travel towards the target every update when hone is true
                // Calculate the direction vector by getting the length to the target in X and length to the target in Y
                int dx = targetPosition.x - startPosition.x;
                int dy = targetPosition.y - startPosition.y;


                // Use the pythagorean theorem to determine the length of the hypotenuse (the distance between the start and the end point)
                double len = (sqrt(dx*dx + dy*dy));

//        // Normalize each point... Converts the vector into a unit that has a length of exactly one
                // NORMALIZATION NOT WORKING
                double nx = dx / len;
                double ny = dy / len;

                // Multiply a speed and get a projectile that moves in a constant way
                vx = nx * speed;
                vy = ny * speed;
                setPosition(getX() + (int)vx, getY() + (int)vy);
            }
            else{ // Regular projectile action (move in a straight line)
                // Apply the changes to the shuriken projectile in an iterative way

                setPosition(currentX + (int)vx, currentY + (int)vy);
            }
        }
        else {
            curAnimation = disappear;
        }

        // Check if the TTL (Time To Live) has run out
        if (currentTime > ttlStartTime + ttlDelay || collidesWith(ninja)) {
            // If it has then, it is time to delete the object
            GameThread.data.removeObjectWhenSafe(this);
            ninja.setCurHealth(ninja.getCurHealth() - 10);
            System.out.println(ninja.getCurHealth());

            return;
        }
    }



    private void initVelocityVectors() {
        // Calculate the direction vector by getting the length to the target in X and length to the target in Y
        int dx = targetPosition.x - startPosition.x;
        int dy = targetPosition.y - startPosition.y;


        // Use the pythagorean theorem to determine the length of the hypotenuse (the distance between the start and the end point)
        double len = (sqrt(dx*dx + dy*dy));

//        // Normalize each point... Converts the vector into a unit that has a length of exactly one
        // NORMALIZATION NOT WORKING
        double nx = dx / len;
        double ny = dy / len;

        // Multiply a speed and get a projectile that moves in a constant way
        vx = nx * speed;
        vy = ny * speed;
    };
}
