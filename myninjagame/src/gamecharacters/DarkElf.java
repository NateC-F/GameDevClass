package gamecharacters;

import gameframework.animations.Animation;
import gameframework.animations.Spritesheet;
import gameframework.gamecharacters.GameCharacter;
import gameframework.gameobjects.GameObject;
import gameframework.gameobjects.GameObjects;
import gameobjects.NinjaGameObjectType;

import java.util.LinkedList;

public class DarkElf extends GameCharacter
{
    public DarkElf(String name,
                   int x, int y, int z,
                   int scaleWidth, int scaleHeight)
    {
        super(name, NinjaGameObjectType.DARK_ELF,
                x, y, scaleWidth, scaleHeight);
    }

    @Override
    public void initializeStatus()
    {
        speed = 3;
        setTotalHealth(200);
        setCurHealth(200);
        curAnimation = walkLeft;
        gravity = 0;
    }

    @Override
    public void initializeAnimations()
    {
        Spritesheet spritesheet = new Spritesheet("elf2_walk_left.png",
                3, 2,5, false);
        walkLeft = new Animation(spritesheet, scaleWidth, scaleHeight);
        idle = walkLeft;
    }

    public void update(GameObjects objects)
    {
        super.update((GameObjects) objects);

        int randomNumber = (int)(Math.random() * 50);

        if (randomNumber < 12)
            moveRight(false);
        else if (randomNumber < 25)
            moveLeft(false);
        /*else if (randomNumber < 45)
            moveUp(false);
        else
            moveDown(false);*/
    }

    @Override
    /*public boolean handleObjectCollision(GameObject object)
    {
        return false;
    }*/

    //This method is used for an object to attach to another and use it as a platform
    public boolean latch(GameObject platformObject)
    {
        return  super.latch(platformObject);
    }
}
