package gameframework.gamecharacters;

import gameframework.GameThread;
import gameframework.gameobjects.Direction;
import gameframework.gameobjects.GameObject;
import gameframework.gameobjects.GameObjectType;
import gameframework.animations.Animation;
import gameframework.gameobjects.GameObjects;
import gameframework.weapons.Weapon;

/**
 * This class handles general support for characters in the game.
 * Everything in this class applies to any kind of character including
 * player characters, friendly NPCs and enemy characters.
 */
public abstract class GameCharacter extends GameObject {

    //Attributes representing all different animations a character should ideally support.
    protected Animation idle;
    protected Animation jump;
    protected Animation walkRight;
    protected Animation walkLeft;
    protected Animation walkUp;
    protected Animation walkDown;
    protected Animation runRight;
    protected Animation runLeft;
    protected Animation runUp;
    protected Animation runDown;
    protected Animation attackRight;
    protected Animation attackLeft;
    protected Animation attackUp;
    protected Animation attackDown;
    protected Animation attackRangedRight;
    protected Animation attackRangedLeft;
    protected Animation attackRangedDown;
    protected Animation attackRangedUp;
    protected Animation dieRight;
    protected Animation dieLeft;
    protected Animation dieUp;
    protected Animation dieDown;
    protected Animation guardRight;
    protected Animation guardLeft;
    protected Animation guardUp;
    protected Animation guardDown;

    //Attributes for all game characters like health and speed
    private static final int DEFAULT_TOTAL_HEALTH = 100;
    private static final int DEFAULT_SPEED = 2;
    private static final int DEFAULT_JUMP_IMPULSE = -10;

    private int totalHealth;
    private int curHealth;
    protected int speed;
    protected int jumpImpulse;
    private int knockbackImpulse;

    // Double-jump support: number of jumps allowed (including the ground jump)
    protected int maxJumps = 2; // default: double jump (2 total jumps)
    protected int remainingJumps; // jumps remaining in current air sequence

    //DV
    private Weapon weapon;

    // Wall-jump support
    protected boolean touchingWall = false;
    protected gameframework.gameobjects.Direction touchingWallSide = gameframework.gameobjects.Direction.NONE;
    protected int wallJumpHorizontalImpulse = (int) (DEFAULT_SPEED * 5); // configurable horizontal impulse for wall-jump


    // Movement smoothing
    protected double targetVelX = 0.0;     // desired horizontal velocity
    protected double accel = 0.6;          // pixels/frame^2 when accelerating toward target
    protected double decel = 0.8;          // pixels/frame^2 when decelerating toward zero
    protected double airControlMultiplier = 0.6; // reduce horizontal control when airborne

    // Coyote time and jump buffering (frames)
    protected int coyoteTimeFrames = 6;    // frames after leaving ground during which jump is allowed
    protected int coyoteTimer = 0;
    protected int jumpBufferFrames = 6;    // frames to buffer jump input
    protected int jumpBufferedTimer = 0;

    // Wall slide
    protected double wallSlideSpeed = 2.0; // max fall speed when sliding on wall

    // Jump hold (variable jump height)
    protected boolean jumpHeld = false;
    protected int maxJumpHoldFrames = 12; // how many frames jump can be extended by holding
    protected int jumpHoldTimer = 0;

    // Wall stick (small grace period when contacting wall to make wall-jumps easier)
    protected int wallStickFrames = 6;
    protected int wallStickTimer = 0;

    public GameCharacter(String name, int type,
                         int x, int y,
                         int scaleWidth, int scaleHeight) {
        super(name, type, x, y, 3, scaleWidth, scaleHeight);
        totalHealth = DEFAULT_TOTAL_HEALTH;
        curHealth = totalHealth;
        speed = DEFAULT_SPEED;
        jumpImpulse = DEFAULT_JUMP_IMPULSE;
        // initialize jump counters
        remainingJumps = maxJumps;

        initializeAnimations();
        initializeStatus();
    }

    public abstract void initializeStatus();

    public abstract void initializeAnimations();

    public int getTotalHealth() {
        return totalHealth;
    }

    public void setTotalHealth(int totalHealth) {
        if (totalHealth >= 0)
            this.totalHealth = totalHealth;
    }

    public int getCurHealth() {
        return curHealth;
    }

    public void setCurHealth(int curHealth) {
        if (curHealth >= 0)
            this.curHealth = curHealth;
        else this.curHealth = 0;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    // Returns the animation used when determining if a character is latched to a platform or not
    public Animation getPlatformingReferenceAnimation() {
        return getIdleAnimation();
    }

    public Animation getMoveRightAnimation() {
        return walkRight;
    }

    public Animation getMoveLeftAnimation() {
        return walkLeft;
    }

    public Animation getMoveUpAnimation() {
        return walkUp;
    }

    public Animation getMoveDownAnimation() {
        return walkDown;
    }

    public Animation getRunRightAnimation() {
        return runRight;
    }

    public Animation getRunLeftAnimation() {
        return runLeft;
    }

    public Animation getRunUpAnimation() {
        return runUp;
    }

    public Animation getRunDownAnimation() {
        return runDown;
    }

    public Animation getIdleAnimation() {
        return idle;
    }

    public Animation getJumpAnimation() {
        return jump;
    }

    public Animation getAttackRightAnimation() {
        return attackRight;
    }

    public Animation getAttackLeftAnimation() {
        return attackLeft;
    }

    public Animation getAttackUpAnimation() {
        return attackUp;
    }

    public Animation getAttackDownAnimation() {
        return attackDown;
    }

    public Animation getGuardRightAnimation() {
        return guardRight;
    }

    public Animation getGuardLeftAnimation() {
        return guardLeft;
    }

    public Animation getGuardUpAnimation() {
        return guardUp;
    }

    public Animation getGuardDownAnimation() {
        return guardDown;
    }

    public Animation getDieRightAnimation() {
        return dieRight;
    }

    public Animation getDieLeftAnimation() {
        return dieLeft;
    }

    public Animation getDieUpAnimation() {
        return dieUp;
    }

    public Animation getDieDownAnimation() {
        return dieDown;
    }

    public boolean isAttackingRight() {
        return (curAnimation == getAttackRightAnimation());
    }

    public boolean isAttackingLeft() {
        return (curAnimation == getAttackLeftAnimation());
    }

    public boolean isAttackingUp() {
        return (curAnimation == getAttackUpAnimation());
    }

    public boolean isAttackingDown() {
        return (curAnimation == getAttackDownAnimation());
    }


    public boolean isDefending() {
        return (curAnimation == getGuardRightAnimation() ||
                curAnimation == getGuardLeftAnimation() ||
                curAnimation == getGuardUpAnimation() ||
                curAnimation == getGuardDownAnimation()
        );
    }

    public boolean isMoving() {
        return (curAnimation == getMoveRightAnimation() ||
                curAnimation == getMoveLeftAnimation() ||
                curAnimation == getMoveUpAnimation() ||
                curAnimation == getMoveDownAnimation() ||
                isRunning()
        );
    }

    public boolean isRunning() {
        return (curAnimation == getRunRightAnimation() ||
                curAnimation == getRunLeftAnimation() ||
                curAnimation == getRunUpAnimation() ||
                curAnimation == getRunDownAnimation()
        );
    }

    public boolean isMovingRight() {
        return (curAnimation == getMoveRightAnimation() ||
                curAnimation == getRunRightAnimation()
        );
    }

    public boolean isMovingLeft() {
        return (curAnimation == getMoveLeftAnimation() ||
                curAnimation == getRunLeftAnimation()
        );
    }

    public boolean isMovingUp() {
        return (curAnimation == getMoveUpAnimation() ||
                curAnimation == getRunUpAnimation()
        );
    }

    public boolean isMovingDown() {
        return (curAnimation == getMoveDownAnimation() ||
                curAnimation == getRunDownAnimation()
        );
    }

    public boolean isJumping() {
        return (getJumpAnimation() != null &&
                curAnimation == getJumpAnimation());
    }

    public boolean isInTheMiddleOfJump() {
        return isJumping() && !curAnimation.isPaused();
    }

    public boolean isDead() {
        return (curAnimation == getDieLeftAnimation() ||
                curAnimation == getDieRightAnimation() ||
                curAnimation == getDieUpAnimation() ||
                curAnimation == getDieDownAnimation());
    }

    @Override
    public boolean isFalling() {
        //If we are in midair and not in the middle of a jump then we are falling.
        return (isInMidAir() && !(isInTheMiddleOfJump()) && getVelY() > 0);
    }

    //Disable automatic nearby tile relatching for characters that are jumping
    public boolean disableAutoRelatching() {
        return isJumping();
    }

    /* These methods change the speed, direction and animation of a character
     * in order to make it move it in a certain direction. */
    public void moveRight(boolean running) {
        if (!isAbleToMove())
            return;

        if (running) {
            runRight();
            return;
        }
        changeActiveAnimation(getMoveRightAnimation(), true);
        direction = Direction.RIGHT;
        targetVelX = speed;
    }

    public void moveLeft(boolean running) {
        if (!isAbleToMove())
            return;

        if (running) {
            runLeft();
            return;
        }
        changeActiveAnimation(getMoveLeftAnimation(), true);
        direction = Direction.LEFT;
        targetVelX = -speed;
    }

    public void moveUp(boolean running) {
        if (!isAbleToMove())
            return;

        // Prevent vertical movement while gravity is active
        if (getGravity() > 0)
            return;

        if (running) {
            runUp();
            return;
        }
        changeActiveAnimation(getMoveUpAnimation(), true);
        direction = Direction.UP;
        setVelY(-speed);
    }

    public void moveDown(boolean running) {
        if (!isAbleToMove())
            return;

        // Prevent vertical movement while gravity is active
        if (getGravity() > 0)
            return;

        if (running) {
            runDown();
            return;
        }
        changeActiveAnimation(getMoveDownAnimation(), true);
        direction = Direction.DOWN;
        setVelY(speed);
    }

    public void stop() {
        if (isMoving())
            changeActiveAnimation(getIdleAnimation(), true);
        direction = Direction.NONE;
        setVelX(0);
        setVelY(0);
    }

    public void stopX() {
        // Smoothly decelerate to 0
        targetVelX = 0;
        // Only reset to idle if there's no vertical motion (so we don't stop mid-jump)
        if (getVelY() == 0) {
            if (isMoving())
                changeActiveAnimation(getIdleAnimation(), true);
            direction = Direction.NONE;
        }
    }

    public void stopY() {
        setVelY(0);
        // Only reset to idle if not moving horizontally
        if (getVelX() == 0) {
            if (isMoving())
                changeActiveAnimation(getIdleAnimation(), true);
            direction = Direction.NONE;
        }
    }

    /* These methods change the speed, direction and animation of a character
     * in order to make it run in a certain direction (When running, the engine
     * sets the speed to double the walking speed, for the time being). */
    private void runRight() {
        targetVelX = speed * 2;
        changeActiveAnimation(getRunRightAnimation(), true);
        direction = Direction.RIGHT;
    }

    private void runLeft() {
        targetVelX = -speed * 2;
        changeActiveAnimation(getRunLeftAnimation(), true);
        direction = Direction.LEFT;
    }

    private void runUp() {
        setVelY(-speed * 2);
        changeActiveAnimation(getRunUpAnimation(), true);
        direction = Direction.UP;
    }

    private void runDown() {
        setVelY(speed * 2);
        changeActiveAnimation(getRunDownAnimation(), true);
        direction = Direction.DOWN;
    }

    /********/

    public void attack() {
        // Change by DV
        //changeActiveAnimation(attackRight, true);
        weapon.attack();
    }

    /* Method used to determine if a character is able to move in the current situation
     * or not. Note that currently this method is only called to evaluate if the player
     * can move in response to an input event.*/
    public boolean isAbleToMove() {

        boolean ableToMove = false;

        //Can't move if player is dead
        if (isDead())
            return false;
        else if (curAnimation == getIdleAnimation()
                || isMoving()) {
            //If the character is idle then we can move and if we are moving we
            // can always change direction.
            ableToMove = true;
        } else {
            //Performing a different action that isn't idle or moving
            if (getGravity() != 0 && isInTheMiddleOfJump()) {
                //The engine allows characters to still move while
                //they are in the middle of a jump (still have jump impulse)
                ableToMove = true;
            } else {
                //We can move as long as we aren't in the middle
                //of another action (animation must be completed).
                if (curAnimation.isPaused())
                    ableToMove = true;
            }
        }
        return ableToMove;

    }

    //method for a game character receiving damage when being hit by a weapon -DV
    public void takeDamage(int damage) {
        curHealth -= damage;
        System.out.println(getName() + " CURRENT HEALTH: " + curHealth);

        //character dies
        if (curHealth <= 0) {
            System.out.println(getName() + " IS DEAD");
            GameThread.data.removeObjectWhenSafe(this);
        }
    }

    public boolean handleObjectCollision(GameObject object) {
        //Handle collision with objects in a general way (applies to all game characters)
        boolean handled = true;

        switch (object.getType()) {
            case GameObjectType.INANIMATE:
                //Handle how general characters handle collision with inanimate objects
                handled = handleCollision(object);
                break;
            default:
                //Call the general collision handler that provides basic collision
                //for all objects (repositioning to resolve the collision)
                handled = handleCollision(object);
                break;
        }
        return handled;
    }

    public void update(GameObjects objects) {
        // Clear per-frame wall contact — collisions during this update will set it again if present
        touchingWall = false;
        touchingWallSide = gameframework.gameobjects.Direction.NONE;

        // Step timers (coyote and jump buffer)
        if (coyoteTimer > 0)
            coyoteTimer--;
        if (jumpBufferedTimer > 0)
            jumpBufferedTimer--;

        // If jump buffered and we can jump now (coyote, on ground, or touching wall) attempt jump
        if (jumpBufferedTimer > 0) {
            boolean canJumpNow = (!isInMidAir()) || (coyoteTimer > 0) || touchingWall || remainingJumps > 0;
            if (canJumpNow) {
                // Call performJump() hook which subclasses (e.g., Player) override to perform the actual jump
                performJump();
                // Clear buffer
                jumpBufferedTimer = 0;
            }
        }

        // Apply horizontal smoothing toward targetVelX before physics step
        applyHorizontalSmoothing();

        super.update(objects);

        // After physics and collision have run, if touching a wall while falling, apply wall-slide
        if (touchingWall) {
            if (wallStickTimer > 0) {
                // temporary stick: slow down fall more while just touching the wall
                wallStickTimer--;
                if (getVelY() > wallSlideSpeed / 2.0)
                    setVelY(wallSlideSpeed / 2.0);
            } else if (getVelY() > wallSlideSpeed) {
                setVelY(wallSlideSpeed);
            }
        }

        // Variable jump height: while jump is held and character is rising, reduce effective gravity a bit
        if (jumpHeld && getVelY() < 0 && jumpHoldTimer > 0) {
            // partially cancel gravity to extend jump
            setVelY(getVelY() + getGravity() * 0.5); // reduce downward acceleration while holding
            jumpHoldTimer--;
        }
    }

    protected void applyHorizontalSmoothing() {
        double current = getVelX();
        double target = targetVelX;
        // Choose accel or decel depending on whether we're speeding up or slowing down
        double usedAccel;
        boolean slowing = (Math.abs(target) < Math.abs(current)) || (Math.signum(target) != Math.signum(current));
        usedAccel = slowing ? decel : accel;
        if (isInMidAir())
            usedAccel *= airControlMultiplier;

        if (Math.abs(target - current) <= usedAccel) {
            setVelX(target);
        } else if (target > current) {
            setVelX(current + usedAccel);
        } else if (target < current) {
            setVelX(current - usedAccel);
        }
    }

    // Buffer a jump input for a short period (jump buffering)
    public void bufferJump() {
        jumpBufferedTimer = jumpBufferFrames;
    }

    // Cancel any buffered jump input
    public void cancelJumpBuffer() {
        jumpBufferedTimer = 0;
    }

    // Allow external callers (input handlers) to set whether jump is held for variable jump height
    public void setJumpHeld(boolean held) {
        this.jumpHeld = held;
        if (held)
            this.jumpHoldTimer = this.maxJumpHoldFrames;
        else
            this.jumpHoldTimer = 0;
    }

    // Reset the remaining jumps to the configured maximum (used when landing / latching to platform)
    public void resetRemainingJumps() {
        this.remainingJumps = this.maxJumps;
    }

    // Mark that this character is touching a wall on the given side (used by collision handler)
    public void setWallContact(Direction side) {
        if (side == null)
            return;
        if (side == Direction.LEFT || side == Direction.RIGHT) {
            this.touchingWall = true;
            this.touchingWallSide = side;
            this.wallStickTimer = this.wallStickFrames;
        }
    }

    // Allow external code to set knockback impulse applied to this character when knocked back
    public void setKnockbackImpulse(int impulse) {
        this.knockbackImpulse = impulse;
    }

    // Apply a knockback impulse to this character. If toLeft is true, push left, otherwise push right.
    public void knockback(boolean toLeft) {
        int horiz = toLeft ? -Math.abs(knockbackImpulse) : Math.abs(knockbackImpulse);
        // Apply horizontal knockback and a small upward knock
        setVelX(horiz);
        setVelY(-Math.max(2, Math.abs(knockbackImpulse) / 2));
        // Mark as in mid-air so physics/coyote/jump logic behaves correctly
        setInMidAir(true);
    }

    /**
     * Hook invoked when the character should perform a jump (called from coyote/jump-buffer logic).
     * Subclasses (like Player) should override this to invoke their jump behavior.
     */
    protected void performJump() {
        // default: no-op. Player overrides this.
    }

    public Weapon getWeapon() {
        return weapon;
    }

    public void setWeapon(Weapon weapon) {
        this.weapon = weapon;
    }
}
