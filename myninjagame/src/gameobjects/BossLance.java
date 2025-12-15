package gameobjects;

import gameframework.GameThread;
import gameframework.animations.Animation;
import gameframework.animations.Spritesheet;
import gameframework.gameobjects.GameObject;
import gameframework.gameobjects.InanimateObject;
import gameframework.gameobjects.GameObjects;

import java.awt.*;
import java.util.Random;

import static java.lang.Math.sqrt;


public class BossLance extends InanimateObject {
    private final int TARGET_Y_BUFFER = 75;
    private final int TARGET_X_BUFFER = 100;
    private final int SPAWN_X_BUFFER = 50;
    private final int SPAWN_Y_BUFFER = 25;
    Point targetPositon;
    Point startPosition;
    private boolean atBoss = true;
    private boolean atTarget = false;
    private boolean hone = false;
    private boolean honePeriodExhausted = false;
    private int speed = 8;
    private long lastRecordedTime = 0;
    private long honeDelay = 2000;
    private long ttlStartTime = 0;
    private int ttlDelay;
    private double vx;
    private double vy;
    private Animation disappear;
    private Animation staticView;
    private Random rand = new Random();

    public BossLance(String name, int x, int y, int z, int scaleWidth, int scaleHeight, Point targetPosition, int TTL) {
        super(name, x, y, z, scaleWidth, scaleHeight);
        this.targetPositon = targetPosition;
        this.startPosition = new Point(x, y + SPAWN_Y_BUFFER); // Adds a buffer amount so that it can more accurately advance the target
        requiresUpdating = true;
        this.ttlDelay = TTL;
        this.ttlStartTime = System.currentTimeMillis();

        // Make buffer adjustments to the target and spawn points
//        this.targetPositon.x =- TARGET_X_BUFFER;
        this.targetPositon.y = this.targetPositon.y + TARGET_Y_BUFFER;


        initVelocityVectors();
        initializeAnimations();
    }

    public void initializeAnimations(){
        //Add lance pngs
        Spritesheet emptySpritesheet = new Spritesheet("shuriken_static.png",
                1, 1, 1, false);
        Animation empty = new Animation(emptySpritesheet, scaleWidth, scaleHeight);
        empty.setSpeed(0);
        disappear = empty;

        //Add lance pngs
        Spritesheet staticSpritesheet = new Spritesheet("ninja_star_normal_throwing.png",
                2, 1, 2, false);
        Animation staticImage = new Animation(staticSpritesheet, scaleWidth, scaleHeight);
        empty.setSpeed(1);
        staticView = staticImage;
        curAnimation = staticImage;
    };

    @Override
    public boolean handleObjectCollision(GameObject object) {
        return false;
    }

    @Override
    public boolean isUnmovable() {
        return false;
    }

    @Override
    public void update(GameObjects objects)
    {
        super.update(objects);

        // If the projectile is at the target position then it is time to break the projectile motion loop
        if (getPosition() == targetPositon){
            atTarget = true;
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

        if (atBoss){
            setPosition(startPosition.x, startPosition.y);
            atBoss = false;
        }

        if (!atTarget){

            int currentX = getX();
            int currentY = getY();

            if (hone){ // Hone or travel towards the target every update when hone is true
                // Calculate the direction vector by getting the length to the target in X and length to the target in Y
                int dx = targetPositon.x - startPosition.x;
                int dy = targetPositon.y - startPosition.y;


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
        if (currentTime > ttlStartTime + ttlDelay) {
            // If it has then, it is time to delete the object
            GameThread.data.removeObjectWhenSafe(this);
            return;
        }
    }


//    private void initVelocityVectors() {
//        int dx = targetPositon.x - startPosition.x;
//        int dy = targetPositon.y - startPosition.y;
//
//        // --- TEMPORARY FIX FOR STRAIGHT MOVEMENT ---
//        // vx is set to 'speed' if we need to go right, or '-speed' if we need to go left.
//        if (dx > 0) {
//            vx = speed; // Move right by 2 pixels per frame
//        } else if (dx < 0) {
//            vx = -speed; // Move left by 2 pixels per frame
//        } else {
//            vx = 0;
//        }
//
//        // vy is set to 'speed' if we need to go down, or '-speed' if we need to go up.
//        if (dy > 0) {
//            vy = speed; // Move down by 2 pixels per frame
//        } else if (dy < 0) {
//            vy = -speed; // Move up by 2 pixels per frame
//        } else {
//            vy = 0;
//        }
//
//        // Note: The total distance traveled will be slightly faster diagonally (hypotenuse)
//        // but this verifies the movement system.
//    };

    private void initVelocityVectors() {
        // Calculate the direction vector by getting the length to the target in X and length to the target in Y
        int dx = targetPositon.x - startPosition.x;
        int dy = targetPositon.y - startPosition.y;


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