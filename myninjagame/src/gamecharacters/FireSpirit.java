package gamecharacters;

import gameframework.GameThread;
import gameframework.animations.Animation;
import gameframework.animations.Spritesheet;
import gameframework.gameai.navigation.flying.FlyingEnemyAI;
import gameframework.gameai.navigation.flying.FlyingPatrolBehavior;
import gameframework.gameai.navigation.flying.FlyingMeleeBehavior;
import gameframework.gamecharacters.GameCharacter;
import gameframework.gamecharacters.Player;
import gameframework.gameobjects.GameObject;
import gameframework.gameobjects.GameObjects;
import gameobjects.NinjaGameObjectType;

import java.awt.*;

public class FireSpirit extends GameCharacter {

    private Player currentPlayer = null;
    private boolean isDead = false;
    private final double EXPLOSION_DISTANCE = 120;

    private int explosionTimer = 40;

    private FlyingEnemyAI movementAI;
    private FlyingPatrolBehavior patrolBehavior;
    private FlyingMeleeBehavior chaseBehavior;

    public FireSpirit(String name, int x, int y, int z, int scaleWidth, int scaleHeight) {
        super(name, NinjaGameObjectType.FIRE_SPIRIT, x, y, scaleWidth, scaleHeight);
    }

    @Override
    public void initializeStatus() {
        speed = 5;
        setTotalHealth(100);
        setCurHealth(100);
        patrolBehavior = new FlyingPatrolBehavior(this, new Point(getX() - 100, getY()), new Point(getX() + 100, getY()));
        chaseBehavior = new FlyingMeleeBehavior(this, speed);
        movementAI = new FlyingEnemyAI(this, patrolBehavior, chaseBehavior, 500);
        curAnimation = idle;
    }

    @Override
    public void initializeAnimations() {
        Spritesheet idleSheet = new Spritesheet("fire_idle.png", 1, 2, 2, false);
        idle = new Animation(idleSheet, scaleWidth, scaleHeight);
        idle.setSpeed(8);

        Spritesheet rightSheet = new Spritesheet("fire_right.png", 2, 1, 2, false);
        walkRight = new Animation(rightSheet, scaleWidth, scaleHeight);
        walkRight.setSpeed(10);
        runRight = walkRight;

        Spritesheet leftSheet = new Spritesheet("fire_left.png", 2, 1, 2, false);
        walkLeft = new Animation(leftSheet, scaleWidth, scaleHeight);
        walkLeft.setSpeed(10);
        runLeft = walkLeft;

        Spritesheet spritesheet3 = new Spritesheet("fire_explode.png", 2, 2, 4, false);
        dieDown = new Animation(spritesheet3, scaleWidth, scaleHeight);
        dieDown.setSpeed(10);
    }

    @Override
    public void update(GameObjects objects) {
        if (isDead) {
            explosionTimer--;

            if (explosionTimer <= 0) {

                if (currentPlayer != null) {

                    int damage = 50;
                    int newHealth = Math.max(0, currentPlayer.getCurHealth() - damage);
                    currentPlayer.setCurHealth(newHealth);
                    System.out.println("Player took " + damage + " damage from explosion!");
                }

                GameThread.data.removeObjectWhenSafe(this);
            }
            return;
        }

        super.update(objects);

        if (currentPlayer == null || !objects.contains(currentPlayer)) {
            currentPlayer = null;
            for (GameObject obj : objects) {
                if (obj instanceof NinjaPlayer p) {
                    currentPlayer = p;
                    break;
                }
            }
            if (currentPlayer == null) return;
        }

        boolean chasing = movementAI.update(currentPlayer);
        if (chasing) {
            double dist = calculateDistanceToObject(currentPlayer);

            if (dist <= EXPLOSION_DISTANCE) {
                explode();
                return;
            }

            if (getVelX() > 0) curAnimation = runRight;
            else if (getVelX() < 0) curAnimation = runLeft;

        } else {
            if (getVelX() > 0) curAnimation = walkRight;
            else if (getVelX() < 0) curAnimation = walkLeft;
            else curAnimation = idle;
        }
    }

    private void explode() {
        if (isDead) return;

        System.out.println("FireSpirit starting explosion...");
        isDead = true;
        setVelX(0);
        setVelY(0);

        curAnimation = dieDown;
        curAnimation.reset();

    }
}