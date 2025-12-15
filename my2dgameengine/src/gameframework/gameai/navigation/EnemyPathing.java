package gameframework.gameai.navigation;

import gameframework.gameobjects.GameObjects;


/**
 * Defines the movement and behavioral logic for enemies that can patrol and chase players.
 *
 * Implementing classes should manage:
 * 1. Patrol behavior when the player is outside detection range.
 * 2. Chase behavior when the player is detected within a certain range.
 * 3. Switching between patrol and chase based on distance or other conditions.
 *
 * This interface lays out the basic navigation methods an enemy can use
 * to control their movement and reaction to a player being detected
 */
public interface EnemyPathing {

    /**
     * Defines the enemy’s patrol behavior when no player is in detection range.
     *
     * Implementations should handle:
     * - Moving back and forth between patrol boundaries.
     * - Checking for edges or obstacles to avoid falling.
     * - Updating movement direction or animation accordingly.
     *
     * @param objects the collection of game objects in the scene, used for collision or ledge checks.
     */
    void patrol(GameObjects objects);

    /**
     * Defines the enemy’s chase behavior when a player is detected.
     *
     * Implementations should handle:
     * - Moving toward the player’s position.
     * - Avoiding hazards or obstacles.
     * - Possibly returning to patrol when the player moves out of range.
     *
     * @param dx horizontal distance to the player (positive = player is right, negative = player is left)
     * @param dy vertical distance to the player (positive = player is below, negative = player is above)
     * @param objects the collection of game objects in the scene for collision/floor detection.
     */
    void chasePlayer(double dx, double dy, GameObjects objects);

    /**
     * Returns the detection range of this enemy.
     *
     * The detection range defines the radius within which the enemy will
     * switch from patrolling to chasing the player.
     *
     * @return detection range in pixels.
     */
    double getDetectionRange();
}