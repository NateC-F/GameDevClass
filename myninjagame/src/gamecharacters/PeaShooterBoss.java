package gamecharacters;


import gameframework.GameThread;
import gameframework.animations.Animation;
import gameframework.animations.Spritesheet;
import gameframework.gamecharacters.GameCharacter;
import gameframework.gamecharacters.Player;
import gameframework.gameobjects.GameObject;
import gameframework.gameobjects.GameObjects;
import gameobjects.NinjaGameObjectType;
import gameobjects.PeaProjectile;

import java.awt.image.BufferedImage;

public class PeaShooterBoss extends GameCharacter {
    private boolean isDead = false;

    // shooting
    private long lastShootTime;
    private static final long SHOOT_DELAY = 1000 ;
    private static final float PROJECTILE_SPEED = 6.0f ;
    private static final float TRACKING_RANGE = 400.0f; // Max distance to track player

    // diagonal animations
    private Animation attackUpRight;
    private Animation attackUpLeft;

    // death animation
    private Animation idleDead;

    public PeaShooterBoss(String name, int x, int y, int z, int scaleWidth, int scaleHeight) {
        super(name, NinjaGameObjectType.PEASHOOTER_MINI_BOSS, x, y, scaleWidth, scaleHeight);
        System.out.println("PeaShooterBoss created at position: " + x + ", " + y);
        lastShootTime = System.currentTimeMillis();
    }

    @Override
    public void initializeStatus() {
        speed = 0;
        setTotalHealth(400);
        setCurHealth(400);
        curAnimation = idle;
    }

    @Override
    public void initializeAnimations() {
        Spritesheet spritesheet;

        // idle
        BufferedImage idleImg = GameThread.resourceManager.loadImageResource("levels/level1/MiniBoss_2.png", "");
        idle = new Animation(idleImg, "peashooter_idle", scaleWidth, scaleHeight);

        // right
        spritesheet = new Spritesheet("peashooter_attack/peashooter_right.png", 1, 1, 1);
        attackRight = new Animation(spritesheet, scaleWidth, scaleHeight);

        // upright (diagonal)
        spritesheet = new Spritesheet("peashooter_attack/peashooter_upright.png", 1, 1, 1);
        attackUpRight = new Animation(spritesheet, scaleWidth, scaleHeight);

        // up
        spritesheet = new Spritesheet("peashooter_attack/peashooter_up.png", 1, 1, 1);
        attackUp = new Animation(spritesheet, scaleWidth, scaleHeight);

        // upleft (diagonal)
        spritesheet = new Spritesheet("peashooter_attack/peashooter_upleft.png", 1, 1, 1);
        attackUpLeft = new Animation(spritesheet, scaleWidth, scaleHeight);

        // left
        spritesheet = new Spritesheet("peashooter_attack/peashooter_left.png", 1, 1, 1);
        attackLeft = new Animation(spritesheet, scaleWidth, scaleHeight);

        // dead
        spritesheet = new Spritesheet(("peashooter_attack/peashooter_dead.png"), 1, 1, 1);
        idleDead = new Animation(spritesheet, scaleWidth, scaleHeight);

        curAnimation = idle;
    }

    public void update(GameObjects objects) {
        if (isDead) {
            curAnimation = idleDead;
            return;
        }

        super.update(objects);

        setVelX(0);
        setVelY(0);

        long currentTime = System.currentTimeMillis();

        // projectile shooting
        if (currentTime - lastShootTime >= SHOOT_DELAY) {
            shoot();
            lastShootTime = currentTime;
        }
    }

    private void shoot() {
        // Get the active player
        gameframework.gamecharacters.Player player = gameframework.gamecharacters.Player.getActivePlayer();
        if (player == null) return;

        int playerCenterX = player.getX() + (player.getBounds().width / 2);
        int playerCenterY = player.getY() + (player.getBounds().height / 2);

        float dx = (float)(playerCenterX - (getX() + scaleWidth / 2));
        float dy = (float)(playerCenterY - (getY() + scaleHeight / 2));

        // Calculate distance to player
        float distance = (float) Math.sqrt(dx * dx + dy * dy);
        if (distance == 0) return; // player is at same position

        // Check if player is within tracking range
        if (distance > TRACKING_RANGE) {
            // Player is out of range, return to idle animation
            curAnimation = attackRight;
            return;
        }

        // Update animation to face the player
        updateAnimationTowardsPlayer(dx, dy);

        spawnPeaProjectile(dx, dy, distance, player);
    }

    private void spawnPeaProjectile(float dx, float dy, float distance, Player player) {
        // spawn position at a center of boss
        int spawnX = getX() + (scaleWidth / 2) - 15; // -15 to center the 30px pea
        int spawnY = getY() + (scaleHeight / 2) - 15;

        // create and add projectile
        PeaProjectile pea = new PeaProjectile("pea_projectile", spawnX, spawnY, 5, 30, 30, player);

        GameThread.data.addObjectWhenSafe(pea);
        System.out.println("PeaShooter fired projectile at player!");
    }

    private void updateAnimationTowardsPlayer(float dx, float dy) {
        double angle = Math.atan2(dy, dx);
        double degrees = Math.toDegrees(angle);

        if (degrees < 0) degrees += 360;

        // Choose animation based on angle (8 directions, but we only have 5)
        if (degrees >= 337.5 || degrees < 22.5) {
            curAnimation = attackRight;
        } else if (degrees >= 22.5 && degrees < 67.5) {
            curAnimation = attackUpRight;
        } else if (degrees >= 67.5 && degrees < 112.5) {
            curAnimation = attackUp;
        } else if (degrees >= 112.5 && degrees < 157.5) {
            curAnimation = attackUpLeft;
        } else if (degrees >= 157.5 && degrees < 202.5) {
            curAnimation = attackLeft;
        } else if (degrees >= 202.5 && degrees < 247.5) {
            curAnimation = attackUpLeft;
        } else if (degrees >= 247.5 && degrees < 292.5) {
            curAnimation = attackUp;
        } else {
            curAnimation = attackUpRight;
        }
    }

    @Override
    public boolean shouldIgnoreCollisionWith(GameObject other) {
        boolean shouldIgnore = false;

        switch (other.getType()) {
            case NinjaGameObjectType.PEA_PROJECTILE:
                shouldIgnore = true;  // doesn't collide with own projectiles
                break;
        }
        return shouldIgnore;
    }

    public void takeDamage(int damage) {
        setCurHealth(getCurHealth() - damage);

        if (getCurHealth() <= 0) {
            System.out.println("PeaShooterBoss defeated!");
            isDead = true;
        }
    }

    @Override
    public boolean handleObjectCollision(GameObject object) {
        boolean handled = true;

        switch (object.getType()) {
            case NinjaGameObjectType.NINJA_SHURIKEN:
                // take damage from player projectiles
                takeDamage(20);
                // remove the projectile
                GameThread.data.removeObjectWhenSafe(object);
                break;

            default:
                handled = super.handleObjectCollision(object);
                break;
        }
        return handled;
    }
}