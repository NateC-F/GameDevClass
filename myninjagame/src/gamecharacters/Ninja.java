package gamecharacters;

import gameframework.GameThread;
import gameframework.animations.Animation;
import gameframework.animations.Spritesheet;
import gameframework.gamecharacters.GameCharacter;
import gameframework.gamecharacters.Player;
import gameframework.gameobjects.GameObject;
import gameframework.gameobjects.GameObjects;
import gameobjects.AwarenessIndicator;
import gameobjects.NinjaGameObjectType;
import gameobjects.NinjaShuriken;

import java.awt.*;
import java.util.Random;

public class Ninja extends GameCharacter {

    private final int MAX_CHARACTER_VISION = 500;
    private final int MAX_CHARACTER_SENSE = 700;
    private long lastMoveTime = 0; // store when the last movement happened
    private long moveDelay = 2000;
    private long assualtTime = 500;
    private boolean assualtExhausted = false;
    private boolean isDead = false;
    // Attack loop variables
    private long attackDelay = 1500;
    private long lastAttackTime = 0;
    private long currentTime;
    private double distanceToPlayer;
    private Player currentPlayer;
    private Point spawnPoint;
    private Point leftPatrolBounds;
    private Point rightPatrolBounds;
    private boolean movingRight = true;
    private boolean atPatrolBound = false;
    private Random rand = new Random();
    private AwarenessIndicator awarenessIndicator;

    public Ninja(String name,
                 int x, int y, int z,
                 int scaleWidth, int scaleHeight)
    {
        super(name, NinjaGameObjectType.NINJA,
                x, y, scaleWidth, scaleHeight);
    }

    @Override
    public void initializeStatus() {

        speed = 1;
        setTotalHealth(75);
        setCurHealth(75);
        //gravity = 10;
        //jumpImpulse = 4;

        curAnimation = idle;
    }

    //@Override
    public void initializeAnimations() {
        Spritesheet spritesheetLeft = new Spritesheet("ninja_enemy_walk_left.png",
                2, 1, 2, false);
        walkLeft = new Animation(spritesheetLeft, scaleWidth, scaleHeight);
        walkLeft.setSpeed(5);

        Spritesheet spritesheetRight = new Spritesheet("ninja_enemy_walk_right.png",
                2, 1, 2, false);
        walkRight = new Animation(spritesheetRight, scaleWidth, scaleHeight);
        walkRight.setSpeed(5);

        Spritesheet spritesheetAttackLeft = new Spritesheet("ninja_attack_left.png",
                2, 1, 2, false);
        attackLeft = new Animation(spritesheetAttackLeft, scaleWidth, scaleHeight);
        attackLeft.setSpeed(20);

        Spritesheet spritesheetAttackRight = new Spritesheet("ninja_attack_right.png",
                2, 1, 2, false);
        attackRight = new Animation(spritesheetAttackRight, scaleWidth, scaleHeight);
        attackRight.setSpeed(20);

        Spritesheet spritesheetDeath = new Spritesheet("ninja_death_animation3.png",
                3, 3, 9, true);
        dieDown = new Animation(spritesheetDeath, scaleWidth + 30, scaleHeight + 15);
        dieDown.setSpeed(5);
        dieDown.runOnlyOnce();

        Spritesheet spritesheetIdle = new Spritesheet("ninja_idle.png",
                1, 1, 1, false);
        idle = new Animation(spritesheetIdle, scaleWidth, scaleHeight);
        idle.setSpeed(0);
    }

    public void update(GameObjects objects) {
        super.update(objects);
        // get the current system time
        currentTime = System.currentTimeMillis();

        // Code to grab the elf player instance from the object list
        for (GameObject obj : objects) {
                currentPlayer = Player.getActivePlayer();
//                System.out.println("found the player!"); // Test debug statement
                break;
            }


        if (awarenessIndicator == null){
            awarenessIndicator = new AwarenessIndicator("suspicion_indicator.png",
                    getX(), getY(), getZ() + 10, 45, 45, this, 23, -45);
            GameThread.data.addObjectWhenSafe(awarenessIndicator);
        }

        // LAZY Instance creator, check if the spawnPoint is null and if yes, set the spawn point to the current x and y (spawn point)
        if (spawnPoint == null) {
            spawnPoint = new Point(getX(), getY());
            // Create the left and right patrol bound points, a deviation of the spawn point
            leftPatrolBounds = new Point(spawnPoint.x - 50, spawnPoint.y);
            rightPatrolBounds = new Point(spawnPoint.x + 50, spawnPoint.y);

//            awarenessIndicator.setOff();

            // For the very first time this happens, skip the rest of the update
            return;
        }

        if (!isDead) {
            if (!isPlayerClose()) { // If the player is not close by then it is time to patrol

                // Check if the player is nearby
                if (isPlayerNearby()) // If the player is nearby, set the icon on the awarenessIndicator
                    awarenessIndicator.setSuspicious();
                else // If the player is not nearby, turn off the awarenessIndicator
                    awarenessIndicator.setOff();

                speed = 1;
                lastAttackTime = 0; // reset timer for attack phase
                assualtExhausted = false;
                // while the ninja is not at a patrolBound, execute the following
                if (!atPatrolBound) {
                    // If the ninja movingRight boolean is true (true by default) then set the movement to right
                    // and set the current animation to walking right animation
                    if (movingRight) {
                        moveRight(false);
                        curAnimation = walkRight;

                        // If we are at the patrol bounds heading right, then it's time to sit for a while...
                        // set the movingRight to false and the atPatrolBounds to true to break the loop
                        if (getX() >= rightPatrolBounds.x) {
                            movingRight = false;
                            atPatrolBound = true;
                        }
                        // If moving right is false, then we must be moving left...
                        // set movement to moveLeft and current animation to walkLeft animation
                    } else {
                        moveLeft(false);
                        curAnimation = walkLeft;

                        // If we are at the patrol bounds heading left, then it's time to turn around...
                        // set the movingRight to true and the atPatrolBounds to true to break the loop
                        if (getX() <= leftPatrolBounds.x) {
                            movingRight = true;
                            atPatrolBound = true;
                        }
                    }
                }
                // If we get to the else block that means that we are at a patrol bounds
                else { // atPatrolBound == true

                    // For the very first frame we need to create a random amount of time to wait before we go forward
                    // We also stop the ninja and set the current animation to idle
                    if (lastMoveTime == 0) {
                        moveDelay = rand.nextInt(2000);
                        lastMoveTime = currentTime;
                        stop();
                        targetVelX = 0;
                        curAnimation = idle;
                    }

                    // If the random time delay (set above) has passed then it's time to get back to patrolling
                    // Set the atPatrolBound too false to break this loop and set the last move time to 0 for the next time we reach a bound
                    if (currentTime - lastMoveTime >= moveDelay) {
                        // Done idling, resume patrol
                        atPatrolBound = false;
                        lastMoveTime = 0; // reset timer for next bound
                    }
                }
            }
            // The player is nearby, it's time to attack!
            else {

                //Set the awareness indicator
                awarenessIndicator.setAlert();

                // Stop the ninja from patrolling, set speed higher so it can catch the player
                stop();
                setSpeed(2);

                // Check the players directional x position by determining the horizontal difference
                int dx = currentPlayer.getX() - getX();

                // Check if the attack timer is properly set (it equals 0)
                if (lastAttackTime == 0) {
                    lastAttackTime = currentTime;
                }

                // If that difference is positive, then the player must be on the right side
                if (dx > 0) {
                    // if we are in the starting assualt period (moving right) && the ninjas assualt period is not exhausted, then rush towards the player
                    if ((currentTime - lastAttackTime <= assualtTime) && (!assualtExhausted)) {
                        moveRight(true);
                        curAnimation = walkRight;
                    }
                    // if we are not in the assualt period then it's time to attack the player
                    else {
                        stop();
                        // Shoot quicker if it is the first time in the loop
                        if (!assualtExhausted) {
                            lastAttackTime = 900;
                        }
                        assualtExhausted = true; // If we get to this step we should exhaust the assualt charge ability
                        curAnimation = attackRight;
                        if (currentTime - lastAttackTime >= attackDelay) {
                            GameObject newShuriken = spawnShuriken();
                            GameThread.data.addObjectWhenSafe(newShuriken);
                            lastAttackTime = currentTime;
                        }
                    }
                }
                // If it's not on the right side (positive) then it must be on the left (negative)
                else {
                    // if we are in the starting assualt period (moving left) && the ninjas assualt period is not exhausted, then rush towards the player
                    if ((currentTime - lastAttackTime <= assualtTime) && (!assualtExhausted)) {
                        moveLeft(true);
                        curAnimation = walkLeft;
                    }
                    // if we are not in the assault period then it's time to attack the player
                    else {
                        stop();
                        // Shoot quicker if it is the first time in the loop
                        if (!assualtExhausted) {
                            lastAttackTime = 900;
                        }
                        assualtExhausted = true; // If we get to this step we should exhaust the assault charge ability
                        curAnimation = attackLeft;
                        if (currentTime - lastAttackTime >= attackDelay) {
                            GameObject newShuriken = spawnShuriken();
                            GameThread.data.addObjectWhenSafe(newShuriken);
                            lastAttackTime = currentTime;
                        }
                    }
                }

            }
        } else {
            stop();
            // Removes the character from the update list after the death animation because the character has died
            if (dieDown.getCurrentFrameIndex() >= 8)
                GameThread.data.removeObjectWhenSafe(this);
        }
    }

    private GameObject spawnShuriken(){
        return new NinjaShuriken("shuriken_static.png", getX(), getY(), 5, 50, 50, currentPlayer, 3500);
    }


    private boolean isPlayerClose() {
        distanceToPlayer = calculateDistanceToObject(currentPlayer);
        if (distanceToPlayer <= MAX_CHARACTER_VISION) {
            return true;
        } else {
            return false;
        }
    };

    private boolean isPlayerNearby() {
        distanceToPlayer = calculateDistanceToObject(currentPlayer);
        if (distanceToPlayer <= MAX_CHARACTER_SENSE) {
            return true;
        } else {
            return false;
        }
    };

    @Override
    public boolean shouldIgnoreCollisionWith(GameObject other)
    {
        boolean shouldIgnore = false;

        switch (other.getType())
        {
            // list all types to be ignored here
            case NinjaGameObjectType.NINJA_SHURIKEN:
                shouldIgnore =  true;
                break;
        }
        return shouldIgnore;
    }

    @Override
    public boolean handleCollision(GameObject collidingObject) {
        if(collidingObject.getClass().getSuperclass().getSimpleName().equals("ThrownWeapon")) {
            int dmg = collidingObject.getDamage();
            takeDamage(dmg);
            GameThread.data.removeObjectWhenSafe(collidingObject);
        }

        if(getCurHealth() <= 0){
            stop();
            curAnimation = dieDown;
            GameThread.data.removeObjectWhenSafe(awarenessIndicator);
            isDead = true;
        }

        return super.handleCollision(collidingObject);
    }

    @Override
    public void takeDamage(int damage) {
        setCurHealth(getCurHealth() - damage);
    }

    /* Steve: Temporary, used for debugging*/
    /*public boolean isNinja() { return true; }
    public void setPosition(int x, int y)
    {
        if (this.getY() != y )
            y = y;
        super.setPosition(x,y);
    }
    public boolean latch(GameObject platformObject)
    {
        return super.latch(platformObject);
    }*/
    /******/
}
