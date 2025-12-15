package gameobjects;
import gamecharacters.ElfPlayer;
import gameframework.GameThread;
import gameframework.gameobjects.GameObject;
import gameframework.gameobjects.InanimateObject;

import java.awt.image.BufferedImage;

public class Coin_ extends InanimateObject
{
    private static final String COIN_IMAGE_NAME = "gold-coin";

    public Coin_(int x, int y, int z, int scaleWidth, int scaleHeight)
    {
        super(COIN_IMAGE_NAME, x, y, z, scaleWidth, scaleHeight);
        setType(NinjaGameObjectType.COIN);
    }

    @Override
    protected void initializeBaseAnimation(BufferedImage image)
    {
        if (image == null) {
            image = GameThread.resourceManager.loadImageResource("gold-coin.png", GameThread.getCurrentLevel().getName());
        }
        super.initializeBaseAnimation(image);
    }

    @Override
    public boolean isUnmovable()
    {
        return true;  // coin doesn’t move
    }

    @Override
    public boolean handleObjectCollision(GameObject object)
    {
        return true;  // coin does nothing on collision (player handles pickup)
    }
}
