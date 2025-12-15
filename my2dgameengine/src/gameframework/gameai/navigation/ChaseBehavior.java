package gameframework.gameai.navigation;

import gameframework.gameobjects.GameObjects;

public interface ChaseBehavior {
    /**
     * @return true -> still in chase/attack mode
     *         false -> lost sight; return to patrol
     */
    boolean chase(GameObjects objects, long currentTime, int playerX);

    void resetAssault();
}