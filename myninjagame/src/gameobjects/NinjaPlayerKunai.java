package gameobjects;

import gameframework.GameThread;
import gameframework.animations.Animation;
import gameframework.gameobjects.GameObject;
import gameframework.gameobjects.GameObjects;
import gameframework.inputhandlers.MouseHandler;

import java.awt.*;
import java.util.Random;

import static java.lang.Math.sqrt;

public class NinjaPlayerKunai extends ThrownWeapon {
    Point targetPositon;
    Point startPosition;
    private int speed = 8;
    private long lastRecordedTime = 0;
    private long delay = 2000;
    private double vx;
    private double vy;
    private Animation disappear;
    private Animation staticView;
    private Random rand = new Random();
    private boolean thrownByPlayer;
    private long ttlStartTime = 0;
    private int ttlDelay;
    private final int DAMAGE = 25;

    public NinjaPlayerKunai(String name, int x, int y, int z, int scaleWidth, int scaleHeight,
                            boolean thrownByPlayer, GameObject owner, Point startPosition, int TTL) {
        super(name, x, y, z, scaleWidth, scaleHeight, thrownByPlayer, owner);
        setType(NinjaGameObjectType.NINJA_PLAYER_KUNAI);
        this.startPosition = startPosition;
        this.thrownByPlayer = thrownByPlayer;
        this.targetPositon = new Point(MouseHandler.targetX, MouseHandler.targetY);
        this.ttlDelay = TTL;
        this.ttlStartTime = System.currentTimeMillis();
        requiresUpdating = true;
        setDamage(DAMAGE);
        initVelocityVectors();
    }


    @Override
    public boolean handleObjectCollision(GameObject object) {
        return false;
    }

    @Override
    public boolean isUnmovable() {
        return false;
    }

    @Override
    public void update(GameObjects objects)
    {
        super.update(objects);

        setPosition(getX() + (int)vx, getY() + (int)vy);
        long currentTime = System.currentTimeMillis();

        for (GameObject other : objects) {
            if (other == this) continue;

            // check collision with walls/solid objects (inanimate objects)
            if (other.isInanimate() && getBounds().intersects(other.getBounds()) && other.getType() != NinjaGameObjectType.NINJA_PLAYER_KUNAI && other.getType() != NinjaGameObjectType.NINJA_SHURIKEN && other.getType() != NinjaGameObjectType.COIN && other.getType() != NinjaGameObjectType.KEY && other.getType() != NinjaGameObjectType.PEA_PROJECTILE) {
                GameThread.data.removeObjectWhenSafe(this);
                return;
            }
        }

        // Check if the TTL (Time To Live) has run out
        if (currentTime > ttlStartTime + ttlDelay) {
            // If it has then, it is time to delete the object
            GameThread.data.removeObjectWhenSafe(this);
            return;
        }
    }

    private void initVelocityVectors() {
        // Calculate the direction vector by getting the length to the target in X and length to the target in Y
        double dx = targetPositon.x - (startPosition.x + 50);
        double dy = targetPositon.y - (startPosition.y + 50);

        // Use the pythagorean theorem to determine the length of the hypotenuse (the distance between the start and the end point)
        double len = (sqrt(dx*dx + dy*dy));

        // Normalize each point... Converts the vector into a unit that has a length of exactly one
        double nx = dx / len;
        double ny = dy / len;

        // Multiply a speed and get a projectile that moves in a constant way
        vx = nx * speed;
        vy = ny * speed;
    };
}
