package gameframework.gameobjects;

import gameframework.GameThread;
import gameframework.animations.Animation;

import java.awt.image.BufferedImage;

public class InanimateObject extends GameObject
{
    public InanimateObject(String name,
                           int x, int y, int z,
                           int scaleWidth, int scaleHeight)
    {
        super(name, GameObjectType.INANIMATE, x, y, 4,
                scaleWidth, scaleHeight);
        BufferedImage image = GameThread.resourceManager.loadImageResource(name, GameThread.getCurrentLevel().getName());
        initializeBaseAnimation(image);

        //disable frequent updates for inanimate objects
        requiresUpdating = false;
        alwaysUseRectCollision = true;
    }

    /* Inanimate objects have a default animation consisting of one frame, this
     * method extracts the frame from an image object and initializes the animation,
     * depending on whether the object can move or never changes position we
     * perform some internal optimizations that help with engine performance. */
    protected void initializeBaseAnimation(BufferedImage image)
    {
        Animation inanimate = new Animation(image, getName(), scaleWidth, scaleHeight);

        /* If these borders will apply to an inanimate object that won't
         * ever be moving then its a good idea to adjust the border points
         * positions in the animation to reflect directly their final
         * positions on the screen, otherwise we have to recalculate the
         * positions on the fly every time while the game is running which
         * will (needlessly) affect performance.
         */
        if (isUnmovable())
            inanimate.adjustFrameBordersPosition(getX(), getY());

        if (!inanimate.getCurrentFrameBorders(0, 0, false).verifyBoundPoints())
            System.out.println("Unable to verify bound points for " + getName());

        changeActiveAnimation(inanimate, true);

    }

    @Override
    public boolean isInanimate()
    {
        return true;
    }

    @Override
    public boolean isUnmovable()
    {
        /* By default the engine makes all inanimate objects unmovable, but
         * extending classes should override this method and return false if
         * the object is intended to change positions.*/
        return true;
    }

    @Override
    public boolean handleObjectCollision(GameObject object)
    {
        boolean handled = true;
        /* No need to handle collision for inanimate objects that don't move. If an object
         * collides with an inanimate object its the responsibility of the moving object to
         * handle the collision. */
        if (!isUnmovable())
        {
            handled = handleCollision(object);
        }
        return handled;
    }


}
