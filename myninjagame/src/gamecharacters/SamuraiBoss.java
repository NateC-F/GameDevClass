package gamecharacters;

import gameframework.GameThread;
import gameframework.animations.Animation;
import gameframework.animations.Spritesheet;
import gameframework.gamecharacters.GameCharacter;
import gameframework.gamecharacters.Player;
import gameframework.gameobjects.GameObject;
import gameframework.gameobjects.GameObjects;
import gameobjects.NinjaGameObjectType;



public class SamuraiBoss extends GameCharacter
{
    protected Animation jumpLeft;
    protected Animation jumpRight;

    private final int DEFAULT_HEALTH = 600;
    private final int DEFAULT_SCORE = 1000;

    private final int DETECTION_RANGE = 1000;
    private final int ATTACK_RANGE_RIGHT = 175;
    private final int ATTACK_RANGE_LEFT = 100;
    private int attack_range;

    private final int ATTACK_DELAY = 100;
    private int attackTimer;
    private final int DEFAULT_DAMAGE = 50;
    private final int HEIGHT_DIFFERENCE_THRESHOLD = 300;


    private final int PATROL_WAIT_TIMER = 500;
    private int patrol_timer;

    private int stored_x_position;
    private int frame_count;

    private final int JUMP_COOLDOWN = 10000;
    private final int JUMP_RANGE = 250;
    private long last_jump_used;
    private double run_speed;

    public SamuraiBoss(String name, int x, int y, int z, int scaleWidth, int scaleHeight)
    {
        // The scale values are now doubled as attack animations required 32x32 frames, so each animation has been resized for consistency.
        super(name, NinjaGameObjectType.SAMURAI_MINI_BOSS, x, y, scaleWidth*2, scaleHeight*2);
    }

    @Override
    public void initializeStatus()
    {
        speed = 3;
        setTotalHealth(DEFAULT_HEALTH);
        setCurHealth(getTotalHealth());
        curAnimation = idle;
        patrol_timer=0;
        attackTimer = ATTACK_DELAY;
        stored_x_position = getX();
        frame_count = 0;
        last_jump_used = 0;
        jumpImpulse = -5;
        attack_range = ATTACK_RANGE_LEFT;
        run_speed = speed*2;
    }

    @Override
    public void initializeAnimations()
    {
        Spritesheet spritesheet;

        spritesheet = new Spritesheet("samurai_walk_right.png",1,4,4);
        walkRight = new Animation(spritesheet, scaleWidth,scaleHeight);
        walkRight.setSpeed(3);

        spritesheet = new Spritesheet("samurai_walk_left.png", 1,4,4);
        walkLeft = new Animation(spritesheet,scaleWidth,scaleHeight);
        walkLeft.setSpeed(3);

        spritesheet = new Spritesheet("samurai_idle.png",1,2,2);
        idle = new Animation(spritesheet,scaleWidth,scaleHeight);
        idle.setSpeed(10);

        spritesheet = new Spritesheet("samurai_jump_right.png",3,1,3);
        jumpRight = new Animation(spritesheet,scaleWidth,scaleHeight);
        jumpRight.setSpeed(3);

        spritesheet = new Spritesheet("samurai_jump_left.png",3,1,3);
        jumpLeft = new Animation(spritesheet,scaleWidth,scaleHeight);
        jumpLeft.setSpeed(3);

        spritesheet = new Spritesheet("samurai_sword_attack_right.png",1,7,7);
        attackRight = new Animation(spritesheet,scaleWidth,scaleHeight);
        attackRight.setSpeed(6);

        spritesheet = new Spritesheet("samurai_sword_attack_left.png",1,7,7);
        attackLeft = new Animation(spritesheet,scaleWidth,scaleHeight);
        attackLeft.setSpeed(6);

    }

    public void update(GameObjects objects)
    {
        super.update(objects);

        if(getCurHealth()<=0)
        {
            GameThread.data.removeObjectWhenSafe(this);
            Player.getActivePlayer().setScore(Player.getActivePlayer().getScore() + DEFAULT_SCORE);
        }

        if (curAnimation != attackLeft && curAnimation != attackRight)
        {
            setDisableCollision(false);
            attackLeft.reset();
            attackRight.reset();
        }

        if (!canSeePlayer())
        {
            if (patrol_timer < (PATROL_WAIT_TIMER / 2))
            {
                setVelX(-speed);
                curAnimation = walkLeft;
            }
            else
            {
                setVelX(speed);
                curAnimation = walkRight;
            }
            patrol_timer++;
            if (patrol_timer == PATROL_WAIT_TIMER)
                patrol_timer = 0;

            if(!didXValueUpdate(getX()) && frame_count>10)
            {

                setVelX(-getVelX());
                if (getVelX()>0)
                {
                    curAnimation = walkRight;
                    patrol_timer = 30;
                }
                else
                {
                    curAnimation = walkLeft;
                    patrol_timer = 0;
                }
                frame_count = 0;
            }
        }
        else
        {

            // Leaving this here for use when jump functionality is ready

            /*if (getDistanceFromPlayer()>JUMP_RANGE && (System.currentTimeMillis()-last_jump_used) > JUMP_COOLDOWN)
            {
                last_jump_used = System.currentTimeMillis();
                jumpAtPlayer();
            }*/

            if (getDistanceFromPlayer() < attack_range  && Math.abs(NinjaPlayer.getActivePlayer().getY()-getY())<HEIGHT_DIFFERENCE_THRESHOLD) // Swing at the player if they are close enough
            {
                if (attackTimer >= ATTACK_DELAY)
                {
                    setDisableCollision(true);
                    attack();
                    attackTimer = 0;
                }
            }
            else // Otherwise move toward the player as long as they are within the detection range
            {
                if (Player.getActivePlayer().getX() < getX())
                {
                    setVelX(-run_speed);
                    curAnimation = walkLeft;
                    attack_range= ATTACK_RANGE_LEFT;
                }
                else
                {
                    setVelX(run_speed);
                    curAnimation = walkRight;
                    attack_range=ATTACK_RANGE_RIGHT;
                }
            }

            // Keeps track regardless of which action samurai is performing
            if (attackTimer < ATTACK_DELAY)
                attackTimer++;
        }
        stored_x_position = getX();

    }

    public boolean canSeePlayer()
    {
        return getDistanceFromPlayer() < DETECTION_RANGE;
    }

    public boolean didXValueUpdate(int new_x)
    {
        if (new_x!=stored_x_position)
            return true;
        else return false;
    }


    public void attack()
    {
        if(Player.getActivePlayer().getX() > getX())
            curAnimation = attackRight;
        else
            curAnimation = attackLeft;
    }

    public void jumpAtPlayer()
    {
        if(Player.getActivePlayer().getX() > getX())
        {
            curAnimation = jumpRight;
            performJump();
        }
        else
        {
            curAnimation = jumpLeft;
            performJump();
        }

    }

    public double getDistanceFromPlayer()
    {
        return Math.abs(NinjaPlayer.getActivePlayer().getX()-getX());
    }

    @Override
    public boolean handleCollision(GameObject collidingObject)
    {
        if (collidingObject.isNinja())
        {
            System.out.println(NinjaPlayer.getActivePlayer().getCurHealth());
            Player.getActivePlayer().takeDamage(DEFAULT_DAMAGE);
            System.out.println(NinjaPlayer.getActivePlayer().getCurHealth());
            return false;
        }
        else
            return super.handleCollision(collidingObject);
    }
}
