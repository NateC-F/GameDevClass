package gameframework.gameai.navigation;

import gameframework.gamecharacters.GameCharacter;
import gameframework.gamecharacters.Player;
import gameframework.gameobjects.GameObject;
import gameframework.gameobjects.GameObjects;

/**
 * Base class for ground enemies that patrol an area and can chase the player.
 * Handles common logic for movement, patrol bounds, ledge detection, and returning to patrol.
 */
public abstract class GroundPathingEnemy extends GameCharacter implements EnemyPathing {

    // Patrol boundaries (left and right X coordinates)
    protected int patrolLeftX;
    protected int patrolRightX;
    // Direction the enemy is currently moving
    protected boolean movingRight = true;
    // Whether the enemy is returning to its patrol path after chasing
    protected boolean returningToPatrol = false;
    // Original X coordinate of the patrol center (used when returning)
    protected int originalX;
    // default detection range for enemies (can be modified in subclasses to increase/decrease range based on enemy
    protected double detectionRange = 250;

    /**
     * Constructor for ground pathing enemies.
     *
     * @param name   name of the enemy
     * @param type   type ID (NinjaGameObjectType)
     * @param x      initial X position
     * @param y      initial Y position
     * @param width  enemy width (used for collisions)
     * @param height enemy height (used for collisions)
     */
    public GroundPathingEnemy(String name, int type, int x, int y, int width, int height) {
        super(name, type, x, y, width, height);
        this.patrolLeftX = x - 100; // Default patrol distance to the left
        this.patrolRightX = x + 100; // Default patrol distance to the right
        originalX = x;
    }

    /**
     * Checks if there is ground directly ahead of the enemy.
     * Prevents walking off ledges.
     *
     * @param objects list of all game objects in the scene
     * @return true if there is ground ahead, false if a cliff is detected
     */
    protected boolean hasFloorAhead(GameObjects objects) {
        // Check a few pixels ahead of the enemy's feet based on movement direction from roughly half the width of the enemy’s body
        int checkX = movingRight ? getX() + scaleWidth / 2 + 4 : getX() - 4;
        int checkY = getY() + scaleHeight + 4;

        // Loop through all game objects to see if any provide ground at the check point
        for (GameObject obj : objects) {
            if (obj == this) continue;
            if (obj.getCollisionBounds().contains(checkX, checkY)) {
                return true; // Ground detected
            }
        }
        return false; // No ground detected
    }

    /**
     * Patrol logic for moving back and forth between patrol bounds.
     * If returningToPatrol is true, moves toward the patrol center first.
     *
     * @param objects list of all game objects for collision and ledge detection
     */
    public void patrol(GameObjects objects) {
        // If currently returning to patrol path
        if (returningToPatrol) {
            // Move back toward original patrol center
            if (Math.abs(getX() - originalX) <= speed) {
                // Reached patrol center
                returningToPatrol = false;
            } else if (getX() < originalX) {
                // Move right toward patrol center
                //setX(getX() + speed);
                movingRight = true;
                moveRight(false);
            } else if (getX() > originalX) {
                // Move left toward patrol center
                //setX(getX() - speed);
                movingRight = false;
                moveLeft(false);
            }
            return; // Skip normal patrol while returning
        }

        // Normal patrol logic: flip at bounds or cliff
        if (!hasFloorAhead(objects) || getX() <= patrolLeftX || getX() >= patrolRightX) {
            movingRight = !movingRight;
        }

        // Move enemy based on current direction
        if (movingRight) {
            //setX(getX() + speed);
            moveRight(false); // Update animation (false = not chasing)
        } else {
            //setX(getX() - speed);
            moveLeft(false);
        }
    }

    /**
     * Update called every frame to handle movement, patrolling, and chasing.
     * Patrol is resumed if the player leaves detection range.
     *
     * @param objects list of all game objects in the scene
     */
    @Override
    public void update(GameObjects objects) {
        super.update(objects); // Apply physics, collisions, gravity, etc.

        Player player = Player.getActivePlayer();
        boolean chasing = false;

        if (player != null) {
            double dx = player.getX() - getX(); // Horizontal distance to player
            double dy = player.getY() - getY(); // Vertical distance (unused here, but can be used for jump logic later)
            double distance = Math.sqrt(dx*dx + dy*dy);

            // Start chasing if player is within detection range
            if (distance < getDetectionRange()) {
                chasing = true;
                chasePlayer(dx, dy, objects); // Abstract method implemented by subclasses
                returningToPatrol = true; // will return to patrolling once chasing ends
            }
        }

        // If not chasing, continue patrolling
        if (!chasing) patrol(objects);
    }

    /**
     * Abstract method for player chasing logic.
     * Must be implemented by subclasses.
     *
     * @param dx horizontal distance to player
     * @param dy vertical distance to player
     * @param objects list of all game objects for collision/floor checking
     */
    @Override
    public abstract void chasePlayer(double dx, double dy, GameObjects objects);

    /**
     * Returns the detection range of the enemy.
     * Can be overridden by subclasses for unique enemies detection ranges.
     *
     * @return detection range in pixels
     */
    @Override
    public double getDetectionRange() {
        return detectionRange;
    }
}