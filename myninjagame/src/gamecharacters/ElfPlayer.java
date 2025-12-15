package gamecharacters;

import gameframework.GameThread;
import gameframework.gamecharacters.Player;
import gameframework.animations.Spritesheet;
import gameframework.animations.Animation;
import gameframework.gameobjects.GameObject;
import gameframework.gameobjects.GameObjects;
import gameobjects.NinjaGameObjectType;

import java.awt.image.BufferedImage;

public class ElfPlayer extends Player
{
    public ElfPlayer(String name, int x, int y,
                     int scaleWidth, int scaleHeight)
    {

        super(name, x, y, scaleWidth, scaleHeight);
        setType(NinjaGameObjectType.ELF_PLAYER);

    }

    private boolean isDead = false;

    @Override
    public void initializeStatus()
    {
        //initialize character attributes
        speed = 3;
        setTotalHealth(400);
        setCurHealth(400);
        curAnimation = idle;
    }

    @Override
    public void initializeAnimations()
    {
        //initialize all of this character's animations here
        BufferedImage image = GameThread.resourceManager.loadImageResource("spritesheets/elf_idle.png", "");
        idle = new Animation(image, "elf_idle", scaleWidth, scaleHeight);

        Spritesheet spritesheet = new Spritesheet("elf_walk_right.png", 3, 2, 5);
        walkRight = new Animation(spritesheet, scaleWidth, scaleHeight);
        walkRight.setSpeed(5);
        runRight = walkRight;

        spritesheet = new Spritesheet("elf_walk_left.png", 3, 2, 5, false);
        walkLeft = new Animation(spritesheet, scaleWidth, scaleHeight);
        walkLeft.setSpeed(5);
        runLeft = walkLeft;

        spritesheet = new Spritesheet("elf_attack_right.png", 3, 2, 5);
        attackRight = new Animation(spritesheet, (int)(scaleWidth * 1.2), scaleHeight);
        attackRight.setSpeed(4);
        //attackRight.runOnlyOnce();
        attackRight.setRunTimes(3);

        //just temporarily make jump animation the walk right animation
        spritesheet = new Spritesheet("elf_jump.png", 3, 2, 5);
        jump = new Animation(spritesheet, (int)(scaleWidth * 1.2) , (int)(scaleHeight * 1.2));
        jump.setSpeed(6);
        jump.runOnlyOnce();

    }

    @Override
    public Animation getMoveUpAnimation()
    {
        if (getVelX() < 0)
            return walkLeft;
        else return walkRight;
    }

    private int coins;

    public int getCoins()
    {
        return coins;
    }

    public void addCoins(int amount)
    {
        coins += amount;
    }

    @Override
    public Animation getMoveDownAnimation()
    {
        return getMoveUpAnimation();
    }

    @Override
    public Animation getRunUpAnimation()
    {
        if (getVelX() < 0)
            return runLeft;
        else return runRight;
    }

    // this would be called when it gets hit by a player
    public void takeDamage(int damage)
    {
        // If already dead, do nothing — prevents infinite loop
        if (isDead)
            return;
        setCurHealth(getCurHealth() - damage);

        // Optional: play a hurt animation
        System.out.println(getName() + " took " + damage + " damage! Health now: " + getCurHealth());

        if (getCurHealth() <= 0)
        {
            //isDead = true;
            setVelX(0);                    // stop movement immediately
            //curAnimation = idleDying;    // show death animation
            System.out.println(getName() + " has died!");

        }
    }

    @Override
    public Animation getRunDownAnimation()
    {
        return getRunUpAnimation();
    }

    @Override
    public boolean handleObjectCollision(GameObject go)
    {
        //Handle here any objects that require some specific action by the
        //elf character when collision occurs
        boolean handled = true;

        switch (go.getType())
        {

            // Example: check if the player hit a coin
            case NinjaGameObjectType.COIN:
                addCoins(1);               // Add coin to player inventory
                GameThread.data.getObjects().remove(go);  // Remove coin from the game
                System.out.println("YOU Picked up a coin! Total coins: " + getCoins());
                return true;


            case NinjaGameObjectType.LEVEL_BOSS:
                takeDamage(25);
                break;

            case NinjaGameObjectType.DARK_ELF:
                handleCollision(go);
                break;
            case NinjaGameObjectType.THROWN_WEAPON:
                break;
            default:
                handled = super.handleObjectCollision(go);
                break;
        }
        return handled;
    }

    @Override
    public void specialActionA(boolean startingAction)
    {
        attackRight.setRunTimes(8);
        attack();
    }

    @Override
    public void specialActionB(boolean startingAction)
    {
        attackRight.setRunTimes(4);
        attack();
    }

    @Override
    public void specialActionC(boolean startingAction)
    {
        attackRight.setRunTimes(6);
        attack();
    }

    @Override
    public boolean shouldIgnoreCollisionWith(GameObject other)
    {
        boolean shouldIgnore = false;

        switch (other.getType())
        {
            // temporarily ignore collision with shurikens
            case NinjaGameObjectType.NINJA_SHURIKEN:
                shouldIgnore =  true;
                break;
        }
        return shouldIgnore;
    }

    /* Temporary used for debugging*/
    public boolean isNinja() { return true;}
    public void setPosition(int x, int y)
    {
      /*  if (x != getX())
            x = x;*/
        if (x < getX() - 50)
            x = x;
        if (x < getX() - 70)
            x = x;
        if (x < getX() - 100)
            x = x;
        if (x < getX() - 30)
            x = x;
        super.setPosition(x,y);
    }
    public boolean latch(GameObject platformObject)
    {
        return super.latch(platformObject);
    }
    /******/

}
