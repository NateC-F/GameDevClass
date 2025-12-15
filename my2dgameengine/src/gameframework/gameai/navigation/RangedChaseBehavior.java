package gameframework.gameai.navigation;

import gameframework.gamecharacters.GameCharacter;
import gameframework.gameobjects.GameObjects;

public class RangedChaseBehavior implements ChaseBehavior {

    private final GameCharacter enemy;
    private final LedgeDetector ledgeDetector = new LedgeDetector();
    private final long assaultDuration;
    private final double stopDistance;
    private boolean assaultExhausted = false;
    private long assaultStartTime = 0;

    public RangedChaseBehavior(GameCharacter enemy, long assaultDuration, double stopDistance) {
        this.enemy = enemy;
        this.assaultDuration = assaultDuration;
        this.stopDistance = stopDistance;
    }

    /**
     * @return true if we are still in "chase/attack" mode.
     *         EnemyMovementAI treats a false as "out of detection range".
     */
    public boolean chase(GameObjects objects, long currentTime, int playerX) {

        int dx = playerX - enemy.getX();
        double dist = Math.abs(dx);
        boolean movingRight = dx > 0;

        // Close enough to attack so enemy stands at edge of range and attacks
        if (dist <= stopDistance) {
            enemy.stopX();          // stop horizontal motion
            return true;           // still in chase/attack mode
        }

        if (assaultStartTime == 0) {
            assaultStartTime = currentTime;
        }

        boolean inAssault =
                (currentTime - assaultStartTime <= assaultDuration && !assaultExhausted);

        // Assault finished: stay put and attack from here
        if (!inAssault) {
            assaultExhausted = true;
            enemy.stopX();          // no more rushing, but keep chase/attack state
            return true;
        }

        // Ledge ahead: do NOT fall. Stop at ledge and let enemy throw projectiles
        if (!ledgeDetector.hasFloorAhead(enemy, movingRight, objects)) {
            enemy.stopX();          // stop horizontal movement, but do NOT drop out of chase
            return true;
        }

        // --- Normal chase rush movement toward player ---
        if (movingRight) enemy.moveRight(true);
        else             enemy.moveLeft(true);

        return true; // still chasing
    }

    // Reset for returning to patrol
    public void resetAssault() {
        assaultExhausted = false;
        assaultStartTime = 0;
    }
}
