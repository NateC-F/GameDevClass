package gameobjects;

import gameframework.GameThread;
import gameframework.animations.Animation;
import gameframework.animations.Spritesheet;
import gameframework.collision.CollisionHandler;
import gameframework.gameobjects.GameObject;
import gameframework.gameobjects.InanimateObject;
import gameframework.gameobjects.GameObjects;

import java.awt.*;
import java.util.Random;
import static java.lang.Math.sqrt;

public class NinjaShuriken extends InanimateObject {
    private final int TARGET_Y_BUFFER = 75;
    private final int TARGET_X_BUFFER = 100;
    private final int SPAWN_X_BUFFER = 50;
    private final int SPAWN_Y_BUFFER = 25;
    GameObject currentPlayer;
    Point targetPositon;
    Point startPosition;
    private boolean atNinja = true;
    private boolean atTarget = false;
    private boolean hone = false;
    private boolean honePeriodExhausted = false;
    private int speed = 8;
    private long lastRecordedTime = System.currentTimeMillis();
    private long honeDelay = 1500;
    private long ttlStartTime = 0;
    private int ttlDelay;
    private double vx;
    private double vy;
    private Animation disappear;
    private Animation spin1;
    private Animation spin2;
    private Random rand = new Random();
    //Added by Logan (DO NOT REMOVE IF YOU ARE GONNA USE THIS, IF YOU DO MAKE SURE TO ADD THE DAMAGE)
    private final int DAMAGE = 10;
    //

    public NinjaShuriken(String name, int x, int y, int z, int scaleWidth, int scaleHeight, GameObject player, int TTL) {
        super(name, x, y, z, scaleWidth, scaleHeight);
        setType(NinjaGameObjectType.NINJA_SHURIKEN);

        this.currentPlayer = player;
        this.targetPositon = new Point(currentPlayer.getX(), currentPlayer.getY());
        this.startPosition = new Point(x, y + SPAWN_Y_BUFFER); // Adds a buffer amount so that it can more accurately advance the target
        requiresUpdating = true;
        this.ttlDelay = TTL;
        this.ttlStartTime = System.currentTimeMillis();

        setGravity(0);

        //Added By Logan
        setDamage(DAMAGE);
        //

        // Make buffer adjustments to the target and spawn points
//        this.targetPositon.x =- TARGET_X_BUFFER;
        this.targetPositon.y = this.targetPositon.y + TARGET_Y_BUFFER;

        GameThread.gameAudio.playClip("NinjaThrow2.wav");

        initVelocityVectors();
        initializeAnimations();
    }

    public void initializeAnimations(){
        Spritesheet emptySpritesheet = new Spritesheet("shuriken_static.png",
                1, 1, 1, false);
        Animation empty = new Animation(emptySpritesheet, scaleWidth, scaleHeight);
        empty.setSpeed(0);
        disappear = empty;

        Spritesheet regularSpinSpritesheet = new Spritesheet("ninja_star_normal_throwing.png",
                2, 1, 2, false);
        Animation normal = new Animation(regularSpinSpritesheet, scaleWidth, scaleHeight);
        normal.setSpeed(1);
        spin1 = normal;

        Spritesheet regularSpinSpritesheet2 = new Spritesheet("ninja_star_speicial_throwing.png",
                2, 1, 2, false);
        Animation normal2 = new Animation(regularSpinSpritesheet2, scaleWidth - 5, scaleHeight - 5);
        normal2.setSpeed(1);
        spin2 = normal2;

        curAnimation = spin1;
    };

//    @Override
//    public boolean handleObjectCollision(GameObject object) {
//        return false;
//    }

    @Override
    public boolean isUnmovable() {
        return false;
    }

    @Override
        public void update(GameObjects objects)
        {
            super.update(objects);

        // If the projectile is at the target position then it is time to break the projectile motion loop
//        if (getPosition().equals(targetPositon)) {
//            atTarget = true;
//        }

            // get the current system time
        long currentTime = System.currentTimeMillis();
        // If the initial delay has passed, then hone for a random amount of time
        if (currentTime - lastRecordedTime > honeDelay){ // Check if we have passed the initial delay
            if (!honePeriodExhausted) { // Check if the shuriken hone period has already been used
                // if it has not been used yet, then it's time to use it...
                GameThread.gameAudio.playClip("NinjaHone1.wav");
                hone = true;
                lastRecordedTime = currentTime;
                // RESET the delay so that the hone period has a different delay number than the original
                honeDelay = 500;
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
                int dx = currentPlayer.getX() - getX(); // For honing the (X, Y) are relative to its current distance
                int dy = currentPlayer.getY() - getY();

                // Set animation to special spinning animation and set speed higher to catch the target
                curAnimation = spin2;
                speed = 8;

                // Use the pythagorean theorem to determine the length of the hypotenuse (the distance between the start and the end point)
                double len = (sqrt(dx*dx + dy*dy));

                // Normalize each point... Converts the vector into a unit that has a length of exactly one
                double targetXDir = dx / len;
                double targetYDir = dy / len;

                // Get the current angle radian values, needed for homing calculation
                double currentAngle = Math.atan2(vy, vx);
                double targetAngle = Math.atan2(targetYDir, targetXDir);

                // Compute the difference between our current angle and the ideal angle calculated above
                double angleDiff = targetAngle - currentAngle;

                // Normalize the angle difference (prevents angle wrap-around)
                if (angleDiff > Math.PI) angleDiff -= 2 * Math.PI;
                if (angleDiff < -Math.PI) angleDiff += 2 * Math.PI;

                // Create a clamp value and convert it to radians
                double maxTurn = Math.toRadians(5);
                // If the angle difference is larger than the max or smaller than the minimum: clamp it within the set maxTurn value
                angleDiff = Math.max(-maxTurn, Math.min(maxTurn, angleDiff));

                // Creates the new angle with the calculated adjustments...
                double newAngle = currentAngle + angleDiff;

                // Multiply a speed with the newAngle calculated vertical and horizontal movement
                // cos(newAngle) gives the proportion of speed that goes horizontally (x-axis)
                // sin(newAngle) gives the proportion of speed that goes vertically (y-axis)
                vx = Math.cos(newAngle) * speed;  // horizontal velocity
                vy = Math.sin(newAngle) * speed;  // vertical velocity

                setPosition(getX() + (int)vx, getY() + (int)vy);
            }
            else{ // Regular projectile action (move in a straight line)
                // Apply the changes to the shuriken projectile in an iterative way

                // Set animation to normal spinning animation, and speed to normal
                curAnimation = spin1;
                speed = 6;

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

    @Override
    public boolean handleCollision(GameObject collidingObject) {
        // If the object is not one of the category "disable collision" then remove (despawn) the shuriken on touch
        if (!collidingObject.isDisableCollision()) {
            if (collidingObject == currentPlayer)
                GameThread.gameAudio.playClip("EnemyAlert3.wav");
            GameThread.data.removeObjectWhenSafe(this);
            return true;
        }
        else
            return true;
    }

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
