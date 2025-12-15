package gamecharacters;

import gameframework.GameThread;
import gameframework.animations.Animation;
import gameframework.animations.Spritesheet;
import gameframework.gameai.navigation.EnemyMovementAI;
import gameframework.gameai.navigation.PatrolBehavior;
import gameframework.gameai.navigation.RangedChaseBehavior;
import gameframework.gamecharacters.GameCharacter;
import gameframework.gameobjects.GameObject;
import gameframework.gameobjects.GameObjects;
import gameobjects.AwarenessIndicator;
import gameobjects.NinjaGameObjectType;
import gameobjects.SkeletonBone;

import java.awt.*;

public class Skeleton extends GameCharacter {

    private ElfPlayer elfPlayer;
    private NinjaPlayer ninja;
    private SkeletonBone bone;
    private long lastAttackTime = 0;
    private long attackDelay = 1200;
    private long currentTime;
    private boolean playingAttackAnimation = false;
    private boolean wasChasing = false;

    // AI components
    private EnemyMovementAI movementAI;
    private PatrolBehavior patrolBehavior;
    private RangedChaseBehavior chaseBehavior;

    public Skeleton(String name, int x, int y, int z, int scaleWidth, int scaleHeight) {
        super(name, NinjaGameObjectType.SKELETON, x, y, scaleWidth, scaleHeight);
    }

    @Override
    public void initializeStatus() {
        speed = 3;
        setCurHealth(150);
        setTotalHealth(150);

        // Patrol from x-150 to x+150
        patrolBehavior = new PatrolBehavior(
                this,
                new Point(getX() - 150, getY()),
                new Point(getX() + 150, getY())
        );

        // Chase until 500 px away, then shoot
        chaseBehavior = new RangedChaseBehavior(
                this,
                2500,   // assault, rush period
                500     // stop distance for throwing bones
        );

        movementAI = new EnemyMovementAI(
                this,
                patrolBehavior,
                chaseBehavior,
                800      // detection range
        );

        curAnimation = idle;
    }

    @Override
    public void initializeAnimations() {
        Spritesheet spritesheet = new Spritesheet("Skeleton_Enemy.png", 1, 1, 1, false);
        idle = new Animation(spritesheet, scaleWidth, scaleHeight);
        Spritesheet spritesheet1 = new Spritesheet("skeleton_walk_left.png", 4, 1, 4, false);
        walkLeft = new Animation(spritesheet1, scaleWidth, scaleHeight);
        Spritesheet spritesheet2 = new Spritesheet("skeleton_walk_right.png", 4, 1, 4, false);
        walkRight = new Animation(spritesheet2, scaleWidth, scaleHeight);
        Spritesheet spritesheet3 = new Spritesheet("skeleton_dab.png", 2, 1, 2, false);
        jump = new Animation(spritesheet3, scaleWidth, scaleHeight);
        idle.setSpeed(2);
    }

    @Override
    public void update(GameObjects objects) {

        super.update(objects);
        currentTime = System.currentTimeMillis();

        if (ninja == null) {
            for (GameObject obj : objects) {
                if (obj instanceof NinjaPlayer) {
                    ninja = (NinjaPlayer) obj;
                    break;
                }
            }
            if (ninja == null) return;
        }

        // Movement AI
        boolean chasing = movementAI.update(objects, currentTime, ninja.getX());

        if (chasing) {

            int dx = ninja.getX() - getX();
            setSpeed(4);

            if (!playingAttackAnimation) {
                if (dx > 0) curAnimation = walkRight;
                else if (dx < 0) curAnimation = walkLeft;
            }

            if (Math.abs(dx) <= 500) {
                if (currentTime - lastAttackTime >= attackDelay) {
                    GameObject bone = spawnBone();
                    GameThread.data.addObjectWhenSafe(bone);
                    curAnimation = jump;
                    playingAttackAnimation = true;

                    lastAttackTime = currentTime;

                }
            }
        } else {
            setSpeed(3);
            
            if (wasChasing) {
                setInMidAir(false);
            }
            
            if (getVelX() > 0) curAnimation = walkRight;
            else if (getVelX() < 0) curAnimation = walkLeft;
            else curAnimation = idle;
            playingAttackAnimation = false;
        }
        wasChasing = chasing;
    }

    private GameObject spawnBone() {
        return new SkeletonBone("bone_static.png", getX(), getY(), 20, 100, 100, ninja.getPosition(), 3500);
    }
}