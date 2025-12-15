package gameobjects;

import gameframework.GameThread;
import gameframework.animations.Animation;
import gameframework.gameobjects.GameObject;
import gameframework.gameobjects.InanimateObject;
import gameframework.gameobjects.GameObjects;

import java.awt.*;
import java.awt.image.BufferedImage;

import static java.lang.Math.sqrt;

public class PeaProjectile extends InanimateObject {
    GameObject targetPlayer;
    Point targetPosition;
    Point startPosition;
    private boolean atBoss = true;
    private boolean atTarget = false;
    private int speed = 6;
    private double vx;
    private double vy;

    // bouncing mechanics
    private int maxBounces = 2; // max number of bounces allowed
    private int bounceCount = 0;

    public PeaProjectile(String name, int x, int y, int z, int scaleWidth, int scaleHeight, GameObject player) {
        super(name, x, y, z, scaleWidth, scaleHeight);
        setType(NinjaGameObjectType.PEA_PROJECTILE);

        this.targetPlayer = player;
        this.targetPosition = new Point(player.getX(), player.getY());
        this.startPosition = new Point(x, y);
        requiresUpdating = true;

        setGravity(0);

        initVelocityVectors();
    }

    @Override
    protected void initializeBaseAnimation(BufferedImage image) {
        BufferedImage peaImg = GameThread.resourceManager.loadImageResource("pea_projectile.png", "level1");
        Animation projAnim = new Animation(peaImg, "pea_projectile", scaleWidth, scaleHeight);
        projAnim.setSpeed(0);

        // adjust borders for unmovable objects
        if (isUnmovable()) {
            projAnim.adjustFrameBordersPosition(getX(), getY());
        }

        changeActiveAnimation(projAnim, true);
    }

    @Override
    public boolean shouldIgnoreCollisionWith(GameObject other) {
        boolean shouldIgnore = false;

        switch (other.getType()) {
            case NinjaGameObjectType.PEASHOOTER_MINI_BOSS:
                shouldIgnore = true;  // don't collide with the boss
                break;

            case NinjaGameObjectType.PEA_PROJECTILE:
                shouldIgnore = true;  // don't collide with other pea projectiles
                break;
        }
        return shouldIgnore;
    }

    @Override
    public boolean handleObjectCollision(GameObject object) {
        boolean handled = true;

        // hit a wall or obstacle, bounce if we haven't reached max bounces
        if (bounceCount < maxBounces) {
            handleBounce(object);
            bounceCount++;

        } else {
            // max bounces reached, remove projectile
            GameThread.data.removeObjectWhenSafe(this);
        }
        return handled;
    }

    @Override
    public boolean isUnmovable() {
        return false;  // projectile moves
    }

    @Override
    public void update(GameObjects objects) {
        super.update(objects);

        // if at boss position initially, move to start position
        if (atBoss) {
            setPosition(startPosition.x, startPosition.y);
            atBoss = false;
        }

        // move the projectile if not at target
        if (!atTarget) {
            int currentX = getX();
            int currentY = getY();

            // simple straight-line movement
            setPosition(currentX + (int)vx, currentY + (int)vy);
        }
    }

    private void initVelocityVectors() {
        // calculate the direction vector
        int dx = targetPosition.x - startPosition.x;
        int dy = targetPosition.y - startPosition.y;

        // calculate distance
        double len = sqrt(dx * dx + dy * dy);

        // normalize and apply speed
        double nx = dx / len;
        double ny = dy / len;

        vx = nx * speed;
        vy = ny * speed;
    }

    // bouncing logic when hitting  wall or platform
    private void handleBounce(GameObject wall) {
        Rectangle peaBounds = this.getCollisionBounds();
        Rectangle wallBounds = wall.getCollisionBounds();

        // calculate the overlap on each side
        int overlapLeft = (peaBounds.x + peaBounds.width) - wallBounds.x;
        int overlapRight = (wallBounds.x + wallBounds.width) - peaBounds.x;
        int overlapTop = (peaBounds.y + peaBounds.height) - wallBounds.y;
        int overlapBottom = (wallBounds.y + wallBounds.height) - peaBounds.y;

        // find the smallest overlap to determine collision side
        int minOverlap = Math.min(Math.min(overlapLeft, overlapRight),
                Math.min(overlapTop, overlapBottom));

        // bounce based on which side had the smallest overlap
        if (minOverlap == overlapLeft || minOverlap == overlapRight) {
            // hit left or right side, reverse horizontal velocity
            vx = -vx;

            // push the pea away from the wall to prevent getting stuck
            if (minOverlap == overlapLeft) {
                setPosition(wallBounds.x - peaBounds.width - 2, getY());
            } else {
                setPosition(wallBounds.x + wallBounds.width + 2, getY());
            }
        } else {
            // hit top or bottom side, reverse vertical velocity
            vy = -vy;

            // push the pea away from the wall to prevent getting stuck
            if (minOverlap == overlapTop) {
                setPosition(getX(), wallBounds.y - peaBounds.height - 2);
            } else {
                setPosition(getX(), wallBounds.y + wallBounds.height + 2);
            }
        }

         // reduce speed slightly on each bounce
         // double damping = 0.9; // 90% of original speed after bounce
         // vx *= damping;
         // vy *= damping;
    }
}