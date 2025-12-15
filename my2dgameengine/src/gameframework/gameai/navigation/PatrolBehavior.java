package gameframework.gameai.navigation;

import gameframework.gamecharacters.GameCharacter;
import gameframework.gameobjects.GameObjects;

import java.awt.*;

public class PatrolBehavior {

    private final GameCharacter enemy;
    private final Point leftBound;
    private final Point rightBound;
    private final LedgeDetector ledgeDetector = new LedgeDetector();
    private boolean movingRight = true;
    private int stuckFrames = 0;
    private int lastX;

    public PatrolBehavior(GameCharacter enemy, Point leftBound, Point rightBound) {
        this.enemy = enemy;
        this.leftBound = leftBound;
        this.rightBound = rightBound;
    }

    public void patrol(GameObjects objects) {

        // Detect if enemy is stuck (not moving)
        if (enemy.getX() == lastX) {
            stuckFrames++;
        } else {
            stuckFrames = 0;
        }
        lastX = enemy.getX();

        // If stuck for 60 frames (1 sec), flip direction
        if (stuckFrames > 60) {
            movingRight = !movingRight;
            stuckFrames = 0;
        }

        boolean ledgeAhead = !ledgeDetector.hasFloorAhead(enemy, movingRight, objects);

        if (ledgeAhead) {
            movingRight = !movingRight;
        } else if (movingRight && enemy.getX() >= rightBound.x) {
            movingRight = false;
        } else if (!movingRight && enemy.getX() <= leftBound.x) {
            movingRight = true;
        }

        // If true, start movement
        if (movingRight) enemy.moveRight(true);
        else enemy.moveLeft(true);
    }
}