package gameframework.gameobjects;

import gameframework.GameThread;
import gameframework.animations.Animation;
import gameframework.gamecharacters.Player;
import java.awt.image.BufferedImage;

public class Collectible extends GameObject
{
    private final int X_COLLECTION_BOUNDS  = 120;
    private final int Y_COLLECTION_BOUNDS = 120;
    public Collectible(String name, int x, int y, int z, int scaleWidth, int scaleHeight)
    {
        super(name, GameObjectType.COLLECTIBLE, x, y, z, scaleWidth, scaleHeight);
        BufferedImage image = GameThread.resourceManager.loadImageResource(name, GameThread.getCurrentLevel().getName());
        initializeBaseAnimation(image);
    }


    protected void initializeBaseAnimation(BufferedImage image)
    {
        Animation collectible = new Animation(image, getName(), scaleWidth, scaleHeight);

        if (!collectible.getCurrentFrameBorders(0, 0, false).verifyBoundPoints())
            System.out.println("Unable to verify bound points for " + getName());
        changeActiveAnimation(collectible, true);
    }


    @Override
    public boolean handleObjectCollision(GameObject object)
    {

        return false;
    }

    @Override
    public void update(GameObjects objects)
    {
        int playerX = Player.getActivePlayer().getX();
        int playerY = Player.getActivePlayer().getY();

        if (Math.abs(playerX-getX())<X_COLLECTION_BOUNDS && Math.abs(playerY-getY())<Y_COLLECTION_BOUNDS)
            collect();

        super.update(objects);
    }

    public void collect()
    {
        System.out.println(this+" COLLECTED "+getName());
        GameThread.data.removeObjectWhenSafe(this);
    }


}
