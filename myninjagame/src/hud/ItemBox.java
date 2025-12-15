package hud;

import gameframework.GameThread;
import gameframework.animations.Animation;
import gameframework.animations.Spritesheet;

import java.awt.image.BufferedImage;

/**
 * This class holds the Item box, and generates the current item being used by the player.
 */
public class ItemBox
{
    //Sprites
    public static final BufferedImage emptyBox = GameThread.resourceManager.loadImageResource("hud/itemBox.png","");
    private final BufferedImage shuriken;
    private Animation cooldown;

    public ItemBox()
    {
        shuriken = GameThread.resourceManager.loadImageResource("hud/shuriken.png","");

    }

    public BufferedImage initializeCurrentItem()
    {
        BufferedImage currentItem;
        return currentItem = shuriken;
    }
}
