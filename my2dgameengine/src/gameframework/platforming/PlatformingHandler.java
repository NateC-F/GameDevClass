package gameframework.platforming;

import gameframework.animations.Animation;
import gameframework.gamecharacters.GameCharacter;
import gameframework.gamecharacters.Player;
import gameframework.gameobjects.GameObject;
import gameframework.gameobjects.GameObjects;

import java.awt.Rectangle;

/**
 * This class is used to handle platforming for games that require it. Platforming is mainly
 * concerned with a given object landing on other objects and using them as a moving surface
 * or platform. This class governs many aspects like for example what section of the object's
 * bounds must be within the platform in other to successfully latch onto it, and other issues.
 */
public class PlatformingHandler
{
    private GameObject mainObject;
    private GameObject platformObject;

    /* This attribute determines what percentage of an object's width must fall within a platform
     * in order to latch to it (if less than that portion is in, the object falls from platform).*/
    private final static double DEFAULT_PLATFORMING_PERCENTAGE = 0.35;
    private double platformingPercentage;

    /**
     * The vertical distance (in pixels) that a latched object is positioned above
     * the platform surface. This small offset prevents continuous collision detection
     * from re-triggering while standing on a platform, ensuring stable latching behavior.
     * Increasing this value slightly can help resolve jittering or overlap issues
     * caused by pixel rounding or uneven sprite boundaries. */
    private static final int PLATFORM_LATCH_OFFSET = 5;

    public PlatformingHandler(GameObject object, GameObject platformObject)
    {
        mainObject = object;
        this.platformObject = platformObject;
        platformingPercentage = DEFAULT_PLATFORMING_PERCENTAGE;
    }

    /**
     * return the platform object (object that is right below this object and holding it)
     */
    public GameObject getPlatformObject()
    {
        return platformObject;
    }

    /**
     * set the platform object (object that is right below this object and holding it)
     */
    public void setPlatformObject(GameObject platformObject)
    {
        if (platformObject != null)
            this.platformObject = platformObject;
    }

    //Returns true if this object is currently placed on top of another object that acts as a platform for it
    public boolean isPlacedOnTopOf(GameObject otherObject)
    {
        return (platformObject == otherObject);
    }

    //Returns true if the object we are evaluating is standing on top of any platform
    public boolean isPlacedOnTopOfPlatform()
    {
        return (platformObject != null);
    }

    public boolean isLandingOnTopOf(GameObject otherObject)
    {
        Rectangle bounds = mainObject.getCollisionBounds();
        Rectangle otherBounds = otherObject.getCollisionBounds();

        // Distance between the bottom of this object and the top of the platform
        int verticalDistance = otherBounds.y - (bounds.y + bounds.height);

        // Platform must be below, not above
        if (verticalDistance > 2 * PLATFORM_LATCH_OFFSET)
            return false; // Too far below or overlapping from the top

        // Require that 90% of this object’s body is above the platform’s top
        boolean mostlyAbove = (otherBounds.y - bounds.y) >= 0.90 * bounds.height;

        // Require sufficient horizontal overlap
        boolean horizontallyAligned = otherObject.encompasses(mainObject, 'H', platformingPercentage);

        return mostlyAbove && horizontallyAligned;
    }

    //This method is called from within an object's update method in order to update its platforming status
    public void update(GameObjects objects)
    {
        //platforming updates are only relevant in games that use gravity
        if (mainObject.getGravity() == 0)
            return;

        /* check if the object is no longer standing on a platform (either because its
         * currently jumping or fell outside the platform) and update it accordingly */
        if ( platformObject != null && detachedFromPlatform() )
        {
            platformObject = null;

            /* If a platform consists of multiple tiles aligned together, when we detach to a tile, we must immediately
             * latch to the next to give the impression of it being just one platform object. */
            boolean foundPlatform = relatchToAdjacentPlatform(objects);
            if (!foundPlatform /*&& mainObject.isNinja()  just for debugging */)
                    System.out.println("Object: " + mainObject.getName() + " detached from platform.");
            /****/
        }

        /* if the object is affected by gravity and isn't standing on a platform,
         * then it means the object is currently in midair. */
        if (platformObject == null)
        {
            mainObject.setInMidAir(true);
        }
    }

    /**
     * Finds a platform directly beneath the main object after it detaches from
     * its current one. Used to smoothly relatch onto adjacent or aligned tiles
     * (e.g., multi-tile ground surfaces) without triggering a falling state.
     * Returns true if we were able to latch to another platform (tile) or false
     * otherwise.
     */
    private boolean relatchToAdjacentPlatform(GameObjects objects)
    {
        boolean success = false;

        if (mainObject.disableAutoRelatching())
            return false;

        for (GameObject candidatePlatform : objects.getNeighborObjects(mainObject))
        {
            if (mainObject.isLandingOnTopOf(candidatePlatform))
            {
                success = latch(candidatePlatform);
                break;
            }
        }
        return success;
    }

    //returns true if the object is no longer standing on a platform
    private boolean detachedFromPlatform()
    {
        boolean detached = false;

        /* When an object latches to a platform, they are barely separate from
         * it by a small distance of PLATFORM_LATCH_OFFSET pixels, so if we increase the
         * y coordinate by PLATFORM_LATCH_OFFSET pixels and test for collision, the test
         * will return true if the object is still standing on the platform below or false
         * otherwise. Note that for all testing to be consistent we base it on the same
         * animation and even same frame (0) (Other frames could be separated from the
         * ground by a larger distance in the same animation). */

        /* temporary for debugging*/
        if (mainObject.isNinja())
            detached = detached;

        // Save active animation, and also the current frame in the reference animation
        // so we can restore them after
        Animation originalAnimation = mainObject.getActiveAnimation();
        Animation referenceAnimation = mainObject.getPlatformingReferenceAnimation();
        int referenceAnimationFrame = referenceAnimation.getCurrentFrameIndex();

        // Temporarily change the animation of the object to the reference one to test
        mainObject.changeActiveAnimation(referenceAnimation, true);

        // We use setY in this case instead of setPosition to change the object position, because we are
        // only changing the position temporarily to test, our intention isn't to really change the object
        // position in the game.
        mainObject.setY(mainObject.getY() + PLATFORM_LATCH_OFFSET + 1);

        if (mainObject.collidesWith(platformObject))
            detached = false;
        else
            detached = true;

        //restore object position and animation frame
        mainObject.setY(mainObject.getY() - (PLATFORM_LATCH_OFFSET + 1));

        referenceAnimation.setCurrentFrame(referenceAnimationFrame);
        mainObject.changeActiveAnimation(originalAnimation, false);

        return detached;
    }

    /**
     * Repositions the main object so that its reference animation (at frame 0)
     * is exactly PLATFORM_LATCH_OFFSET pixels above the platform object. */
    private void attachToPlatform()
    {
        if (platformObject == null || mainObject == null)
            return;

        // Save active animation and also the current frame in reference animation so we can restore them after
        Animation originalAnimation = mainObject.getActiveAnimation();
        Animation referenceAnimation = mainObject.getPlatformingReferenceAnimation();
        int referenceAnimationFrame = referenceAnimation.getCurrentFrameIndex();

        // We always base latching distances on the same animation (reference animation) and on the same
        // frame (frame 0), so we can evaluate latch/detach distances consistently.
        mainObject.changeActiveAnimation(referenceAnimation, true);

        // Compute platform and main object bounds
        Rectangle platformBounds = platformObject.getCollisionBounds();
        Rectangle mainBounds = mainObject.getCollisionBounds();
        // Calculate new Y so that bottom of the object sits exactly PLATFORM_LATCH_OFFSET pixels above platform top
        int newY = platformBounds.y - mainBounds.height - PLATFORM_LATCH_OFFSET;
        // Update the main object's Y position (keep same X)
        mainObject.setPosition(mainObject.getX(), newY);

        // Restore object's original animation (and frame position) now that test is done
        mainObject.changeActiveAnimation(originalAnimation, false);
        // Restore reference animation frame
        referenceAnimation.setCurrentFrame(referenceAnimationFrame);
    }

    public boolean latch(GameObject platformObject)
    {
        this.platformObject = platformObject;
        attachToPlatform();
        // mark as not in midair
        mainObject.setInMidAir(false);
        // ensure jump counters are reset for characters
        if (mainObject instanceof GameCharacter)
            ((GameCharacter)mainObject).resetRemainingJumps();

        //System.out.println("Object: " + mainObject.getName() + " latched to platform " + platformObject.getName());
        return true;
    }

    /* returns the total effect of gravitational forces on this object, this includes
     * the object's gravity but also surface resistance (if standing on a platform) and
     * maybe even other forces (like an antigravity field, etc.). This method is still
     * incomplete & pending, we will know better how to implement it as we develop more
     * games using the engine.
     */
    public double getEffectiveGravity()
    {
        return mainObject.getGravity();
    }

    /**
     * return the platformingPercentage
     */
    public double getPlatformingPercentage()
    {
        return platformingPercentage;
    }

    /**
     * change the platformingPercentage for an object
     */
    public void setPlatformingPercentage(double platformingPercentage)
    {
        if (platformingPercentage > 0.0)
            this.platformingPercentage = platformingPercentage;
    }

}
