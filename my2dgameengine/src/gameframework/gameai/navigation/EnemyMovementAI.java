package gameframework.gameai.navigation;

import gameframework.gamecharacters.GameCharacter;
import gameframework.gameobjects.GameObjects;

public class EnemyMovementAI {

    private final GameCharacter enemy;
    private final PatrolBehavior patrol;
    private final ChaseBehavior chase;
    private final int detectionRange;

    public EnemyMovementAI(GameCharacter enemy,
                           PatrolBehavior patrol,
                           ChaseBehavior chase,
                           int detectionRange) {

        this.enemy = enemy;
        this.patrol = patrol;
        this.chase = chase;
        this.detectionRange = detectionRange;
    }

    /**
     * @return true = chasing, false = patrolling (or returning)
     */
    public boolean update(GameObjects objects, long currentTime, int playerX) {

        int dx = playerX - enemy.getX();
        int dist = Math.abs(dx);

        // Player near -> CHASE
        if (dist < detectionRange) {
            return chase.chase(objects, currentTime, playerX);
        }

        // Player too far -> PATROL
        chase.resetAssault();
        patrol.patrol(objects);

        return false;
    }
}