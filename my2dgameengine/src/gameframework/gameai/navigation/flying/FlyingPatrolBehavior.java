package gameframework.gameai.navigation.flying;

import gameframework.gamecharacters.GameCharacter;

import java.awt.*;

public class FlyingPatrolBehavior {

    private final GameCharacter enemy;
    private final Point leftBound;
    private final Point rightBound;
    private int lastX;
    private int stuckFrames = 0;

    private boolean movingRight = true;

    public FlyingPatrolBehavior(GameCharacter enemy, Point leftBound, Point rightBound) {
        this.enemy = enemy;
        this.leftBound = leftBound;
        this.rightBound = rightBound;
    }

    public void patrol() {

        int x = enemy.getX();
        enemy.setVelY(0);

        // Stuck detection
        if (enemy.getX() == lastX) {
            stuckFrames++;
        } else {
            stuckFrames = 0;
        }
        lastX = enemy.getX();

        if (stuckFrames > 30) {
            movingRight = !movingRight;
            stuckFrames = 0;
        }

        // Bounds check
        if (movingRight && x >= rightBound.x) {
            movingRight = false;
        } else if (!movingRight && x <= leftBound.x) {
            movingRight = true;
        }

        // Horizontal movement (left to right)
        if (movingRight) {
            enemy.setVelX(enemy.getSpeed());
        } else {
            enemy.setVelX(-enemy.getSpeed());
        }
    }
}