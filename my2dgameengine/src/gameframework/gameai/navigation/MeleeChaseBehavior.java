package gameframework.gameai.navigation;

import gameframework.gamecharacters.GameCharacter;
import gameframework.gameobjects.GameObjects;

public class MeleeChaseBehavior implements ChaseBehavior {

    private final GameCharacter enemy;
    private final LedgeDetector ledgeDetector = new LedgeDetector();

    private final double attackReach;   // how close melee must be to "hit"
    private final long assaultDuration; // optional sprint toward the player

    private boolean assaultExhausted = false;
    private long assaultStartTime = 0;

    public MeleeChaseBehavior(GameCharacter enemy,
                              double attackReach,
                              long assaultDuration) {

        this.enemy = enemy;
        this.attackReach = attackReach;
        this.assaultDuration = assaultDuration;
    }

    /**
     * @return true = still chasing/attacking
     *         false = lost track of player
     */
    public boolean chase(GameObjects objects, long currentTime, int playerX) {

        int dx = playerX - enemy.getX();
        double dist = Math.abs(dx);
        boolean movingRight = dx > 0;

        // If close enough for melee attack, stop horizontal movement
        if (dist <= attackReach) {
            enemy.stopX();
            return true;      // still in chase/attack mode
        }

        // Start assault timer
        if (assaultStartTime == 0) {
            assaultStartTime = currentTime;
        }

        boolean inAssault =
                (currentTime - assaultStartTime <= assaultDuration && !assaultExhausted);

        // After assault ends, enemy walks instead of running
        if (!inAssault) {
            assaultExhausted = true;
        }

        // If ledge ahead, turn around but stay aggressive.
        if (!ledgeDetector.hasFloorAhead(enemy, movingRight, objects)) {
            if (movingRight) {
                enemy.moveLeft(false);   // turn around, walk
            } else {
                enemy.moveRight(false);
            }
            return true;
        }

        // CHASE MOVEMENT
        if (movingRight) {
            enemy.moveRight(inAssault);   // run during assault, walk after
        } else {
            enemy.moveLeft(inAssault);
        }

        return true;
    }

    //Reset assault rush when returning to patrol
    public void resetAssault() {
        assaultExhausted = false;
        assaultStartTime = 0;
    }
}