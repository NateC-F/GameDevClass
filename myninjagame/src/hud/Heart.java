package hud;

import java.awt.image.BufferedImage;

import gameframework.GameThread;

/**
 * This class represents the state of the individual hearts in the hud.
 * It holds the sprites and the state that of the heart full or empty.
 */

public class Heart
{
    //heart sprites
    private static BufferedImage fullHeart;
    private static BufferedImage emptyHeart;

    private boolean heartIsEmpty;

    public void setHeartIsEmpty(boolean heartIsEmpty)
    {
        this.heartIsEmpty = heartIsEmpty;
    }

    public Heart(boolean isEmpty)
    {
        fullHeart = GameThread.resourceManager.loadImageResource("hud/fullHeart.png","");
        emptyHeart = GameThread.resourceManager.loadImageResource("hud/emptyHeart.png","");

        this.heartIsEmpty = isEmpty;
    }

    /**
     * @return Returns the sprite that is relative to the state of the heart, full or empty
     */
    public BufferedImage getSpriteState()
    {
        if (!heartIsEmpty)
        {
            return fullHeart;
        }
        else
        {
            return emptyHeart;
        }
    }
}
