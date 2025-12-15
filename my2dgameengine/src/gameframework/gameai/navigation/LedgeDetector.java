package gameframework.gameai.navigation;

import gameframework.gamecharacters.GameCharacter;
import gameframework.gameobjects.GameObjects;

public class LedgeDetector {

    /**
     * Returns true if the enemy is safely standing on a platform
     * AND there is still platform ahead when stepping forward.
     */
    public boolean hasFloorAhead(GameCharacter enemy, boolean movingRight, GameObjects objects) {

        // If already falling -> definitely no floor
        if (enemy.isInMidAir())
            return false;

        // Temporarily nudge enemy forward by 1px and test if it remains grounded
        int originalX = enemy.getX();

        enemy.setX(originalX + (movingRight ? 2 : -2));

        // After moving, let engine recalc collision/platform
        boolean safe = !enemy.isInMidAir();

        // restore position
        enemy.setX(originalX);

        return safe;
    }
}