package gameframework.gameai.navigation.flying;

import gameframework.gamecharacters.GameCharacter;
import gameframework.gamecharacters.Player;

public class FlyingMeleeBehavior implements FlyingChaseBehavior {

    private final GameCharacter enemy;
    private final double speed;

    public FlyingMeleeBehavior(GameCharacter enemy, double speed) {
        this.enemy = enemy;
        this.speed = speed;
    }

    @Override
    public void chase(Player target) {
        if (target == null) {
            enemy.setVelX(0);
            enemy.setVelY(0);
            return;
        }

        double dx = target.getX() - enemy.getX();
        double dy = target.getY() - enemy.getY();
        double dist = Math.sqrt(dx * dx + dy * dy);

        if (dist == 0) {
            enemy.setVelX(0);
            enemy.setVelY(0);
            return;
        }

        // Scale velocity based on speed
        double vx = (dx / dist) * speed;
        double vy = (dy / dist) * speed;

        enemy.setVelX((int) Math.round(vx));
        enemy.setVelY((int) Math.round(vy));
    }
}