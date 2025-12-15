package gameframework.gameai.navigation.flying;

import gameframework.gamecharacters.GameCharacter;
import gameframework.gamecharacters.Player;

public class FlyingEnemyAI {

    private final GameCharacter enemy;
    private final FlyingPatrolBehavior patrolBehavior;
    private final FlyingChaseBehavior chaseBehavior;

    private final double detectionRange;

    public FlyingEnemyAI(GameCharacter enemy,
                         FlyingPatrolBehavior patrolBehavior,
                         FlyingChaseBehavior chaseBehavior,
                         double detectionRange) {

        this.enemy = enemy;
        this.patrolBehavior = patrolBehavior;
        this.chaseBehavior = chaseBehavior;
        this.detectionRange = detectionRange;
    }

    public boolean update(Player target) {

        if (target == null) {
            patrolBehavior.patrol();
            return false;  // not chasing
        }

        double dx = target.getX() - enemy.getX();
        double dy = target.getY() - enemy.getY();
        double dist = Math.sqrt(dx * dx + dy * dy);

        if (dist <= detectionRange) {
            chaseBehavior.chase(target);
            return true;   // chasing
        } else {
            patrolBehavior.patrol();
            return false;  // patrolling
        }
    }
}
