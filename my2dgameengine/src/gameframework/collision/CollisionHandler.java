package gameframework.collision;

import gameframework.gamecharacters.GameCharacter;
import gameframework.gameobjects.Direction;
import gameframework.gameobjects.GameObject;
import gameframework.animations.SpriteBorder;

import java.awt.*;

/**
 * This class manages all collision detection between objects and also how to handle
 * those collisions in a general way, that is it handles how to reposition objects after
 * collision while leaving specific actions to the objects/characters themselves.
 */
public class CollisionHandler
{
    private GameObject objectTracked;          //object we are handling collision for

    //attributes related to how we evaluate collisions
    public static boolean disableCollisions;
    private boolean enableBordersCollision;    //if true collisions are evaluated based on border points, if false we only use the bounds rectangle

    //attributes related to how we resolve collisions
    private Direction collisionDirection;      //direction of the collision (need to move to the opposite direction to resolv
    private static final int COLLISION_RESOLVE_TRIES = 20;   //number of attempts to be performed to resolve a collision
    private boolean alwaysUseRectBoarders = false;

    public CollisionHandler(GameObject object)
    {
        objectTracked = object;
        //by default we handle collisions
        disableCollisions = false;
        //by default we evaluate collisions based on sprite border points
        enableBordersCollision = true;
        collisionDirection = Direction.NONE;
    }

    //If borders collision is disabled then we use the collision bounds (tightest rectangle bounds fit
    //to the sprite borders) when evaluating all kind of collisions.
    public void enableSpriteBordersCollision(boolean enable)
    {
        enableBordersCollision = enable;
    }

    public boolean isSpriteBordersCollisionEnabled() {return enableBordersCollision;}

    // This method returns true if the object tracked collides with the given object
    // or false otherwise.
    public boolean checkCollision(GameObject otherObject)
    {
        boolean objectsCollide = false;
        SpriteBorder borders = null, otherBorders = null;

        //if collision handling is disabled, simply return false
        if (disableCollisions)
            return false;

        //First collision check involves making sure the bound rectangles of the objects intersect
        if ( checkBoundsCollision(otherObject) )
        {
            //If the first collision check passes now we should look for border points collision
            //(For border points collision to be activated both objects must have it enabled)
            if (enableBordersCollision && otherObject.isSpriteBordersCollisionEnabled())
            {
                borders = objectTracked.getSpriteBorders(true);
                otherBorders = otherObject.getSpriteBorders(true);

                /* If this object's sprite borders couldn't be computed then just
                 * base the collision on bounds rect and return true.*/
                if (borders == null || otherBorders == null)
                    objectsCollide = true;
                else
                {
                    if (otherObject.alwaysUseRectCollision == false)
                        objectsCollide = borders.bordersIntersect(otherBorders);
                    else
                        objectsCollide = borders.borderIntersectWithRect(otherBorders);
                }
            }
            else
                objectsCollide = true;
        }
        return objectsCollide;
    }

    /* Check if the bound rectangles of two objects collide. Note this is not a final collision
     * test since the objects might not intersect at the sprite borders level despite the bound
     * rectangles intersecting. */
    private boolean checkBoundsCollision(GameObject otherObject)
    {
        boolean objectsCollide = false;

        Rectangle bounds = objectTracked.getCollisionBounds();
        Rectangle otherBounds = otherObject.getCollisionBounds();
        Rectangle intersection = bounds.intersection(otherBounds);

        // If the intersection isn't empty then the collision bounds collide
        if (!intersection.isEmpty())
        {
            objectsCollide = true;
        }

        return objectsCollide;
    }

    /* Get a bounds rectangle for the tracked object which is suitable for collision detection,
     * that is, a tight fit to the actual borders of the current sprite frame in the object's
     * animation
     */
    public Rectangle getCollisionBounds()
    {
        /* We need to avoid repositioning the sprite borders (it will reposition
         * all points), since we only need to reposition bound points to find
         * the collision bounds, hence we call getSpriteBorders with option set
         * as false. This method is called very frequently in the game, so we
         * need to optimize its performance as much as possible.
         */
        SpriteBorder sb = objectTracked.getSpriteBorders(false);
        Rectangle collisionBounds = null;

        if (!sb.verifyBoundPoints())
        {
            //If border bound points are not valid, just return default object bounds
            collisionBounds = objectTracked.getBounds();
            System.out.println("Unable to verify bound points for " +
                    objectTracked.getName());
        }
        else
        {
            /* Inanimate objects that are unmovable already have absolute (final)
             * border positions (the engine detects them and assigns them final positions
             * automatically when they are created) as opposed to moving objects that have
             * to be readjusted depending on how the position of the object changes.
             */
            collisionBounds = sb.getBordersRectangle();

            if (!objectTracked.isUnmovable())
            {
                //Since we didn't choose to reposition all points (for performance),
                //we have to reposition just the bound points ourselves.
                collisionBounds.x += objectTracked.getX();
                collisionBounds.y += objectTracked.getY();
            }
        }
        return collisionBounds;
    }

    //This method is used to handle general collision between game objects
    public boolean handleCollision(GameObject collidingObject)
    {
        if (objectTracked.isInanimate())
            return handleInanimateObjectsCollision(collidingObject);
        else
            return handleCharacterCollision(collidingObject);
    }

    // This method is used to handle general collision between a game character and any other object
    public boolean handleCharacterCollision(GameObject collidingObject)
    {
        // This method is not intended for inanimate objects
        if (objectTracked.isInanimate())
            return false;

        // If a character is falling then collision is handled in a special way
        if ( ((GameCharacter)objectTracked).isFalling())
            return handleFallingCollision(collidingObject);

        /* When dealing with the player colliding with any other object or a non player
         * character colliding with the player or an npc colliding with any object, we
         * simply adjust the position of the tracked object to resolve the collision */
        resolveCollision(collidingObject);
        return true;
    }

    // This method is used to handle general collision between inanimate objects
    public boolean handleInanimateObjectsCollision(GameObject collidingObject)
    {
        // This method is only intended for inanimate objects
        if (!objectTracked.isInanimate())
            return false;

        // If an object is falling then collision is handled in a special way
        if ( ((GameCharacter)objectTracked).isFalling())
            return handleFallingCollision(collidingObject);

        resolveCollision(collidingObject);
        return true;
    }

    // We consider the special case when an object collides with another while
    // falling and handle it in this method.
    public boolean handleFallingCollision(GameObject collidingObject)
    {
        boolean handled = true;

        /* We differentiate two cases, one is when the object collides with
         * an object below of it that stops its fall and then acts as a surface
         * and another case when an object collides with some object on the side
         * but keeps falling after that.*/
        if (objectTracked.isLandingOnTopOf(collidingObject))
        {
            //colliding because of landing on the other object
            handled = objectTracked.latch(collidingObject);
            if (!handled)
            {
                System.out.println("failed to latch to platform " + collidingObject.getName());
                //System.out.println("Try setting a higher latching intensity level.");
            }
        }
        else
        {
            //Handle all other falling collision cases here

            /* Its possible that this object might have just started to fall after colliding with a
             * platform above, we handle this case here.
             */
            if (collidingObject.encompasses(objectTracked, 'H', 0.30) &&
                    collidingObject.getY() < objectTracked.getY())
            {
                resolveCollision(collidingObject, Direction.UP);
                return handled;
            }
            /****/

            /* Collision with an object either to the right or left
             * while falling, we resolve it by repositioning the
             * collision bounds of the character.*/
            Rectangle bounds = getCollisionBounds();
            Rectangle otherBounds = collidingObject.getCollisionBounds();

            int objectLeft = bounds.x, objectRight = objectLeft + bounds.width;
            int collidingObjectLeft = otherBounds.x;
            int collidingObjectRight = collidingObjectLeft + otherBounds.width;

            if (objectLeft > collidingObjectLeft) //collision on the left
                objectTracked.setCollisionX(collidingObjectRight);
            else                                  //collision on the right
                objectTracked.setCollisionX(collidingObjectLeft - bounds.width);
        }
        return handled;
    }

    /* Determine the direction in which a collision occurs with respect to
     * the object being tracked. This method assumes that a collision has
     * already been detected between the two objects. */
    private Direction determineCollisionDirection_(GameObject collidingObject)
    {
        Rectangle objectBounds = objectTracked.getCollisionBounds();
        Rectangle otherObjectBounds = collidingObject.getCollisionBounds();
        Rectangle boundsIntersection = objectBounds.intersection(otherObjectBounds);

        if (boundsIntersection.isEmpty())
        {
            return Direction.NONE;
        }

        // Use center-to-center vector to determine dominant collision axis and side
        double cxA = objectBounds.getCenterX();
        double cyA = objectBounds.getCenterY();
        double cxB = otherObjectBounds.getCenterX();
        double cyB = otherObjectBounds.getCenterY();

        // Vector from A to B
        double dx = cxB - cxA;
        double dy = cyB - cyA;

        // Determine dominant axis
        if (Math.abs(dx) > Math.abs(dy))
        {
            return (dx < 0) ? Direction.LEFT : Direction.RIGHT;
        } else
        {
            return (dy < 0) ? Direction.UP : Direction.DOWN;
        }
    }
    /* Determine the collision direction from the collision bounds intersection, this is
     * more accurate in most cases than relying on the objects's movement direction (because
     * of changes of animation and animation frames while an object is moving). */
    private Direction determineCollisionDirection(GameObject collidingObject)
    {
        Direction direction = Direction.NONE;

        Rectangle objectBounds = objectTracked.getCollisionBounds();
        Rectangle otherObjectBounds = collidingObject.getCollisionBounds();
        Rectangle boundsIntersection = objectBounds.intersection(otherObjectBounds);

        /* We determine the direction of the collision by finding the largest metric in
         * the intersection bounds rect, if the height is larger then its a collision in
         * a horizontal direction and if the width is larger then its a vertical collision. */
        if (boundsIntersection.height > boundsIntersection.width)
        {
            /* The collision is in a horizontal direction, now determine
             * if its left or right based which side of the tracked object's
             * collision bounds the intersection bounds align with. */
            if (boundsIntersection.x == objectBounds.x)
                direction = Direction.LEFT;
            else
                direction = Direction.RIGHT;
        }
        else if (boundsIntersection.height < boundsIntersection.width)
        {
            /* The collision is in a vertical direction, now determine
             * if its up or down based on which side of the tracked object's
             * collision bounds the intersection bounds align with. */
            if (boundsIntersection.y == objectBounds.y)
                direction = Direction.UP;
            else
                direction = Direction.DOWN;
        }
        return direction;
    }

    /* Adjust the position of an object by shifting to the opposite direction in
     * gradual decrements, until the object reaches a valid position (no collision)
     * or a maximum threshold is reached. This method is very useful when trying to
     * resolve a collision. Returns true if the collision is resolved or false if
     * no value within the range manages to do it. */
    public boolean getClosestValidPosition(int range, GameObject collidingObject,
                                           Direction direction, int variation)
    {
        // Initialize the current position//
        boolean success = false;
        Point curPos = objectTracked.getPosition();
        //if no direction is supplied, then use the object's moving direction
        direction = (direction != Direction.NONE) ? direction : objectTracked.getDirection();
        Direction verticalDirection = Direction.getVerticalComponent(direction);
        Direction horizontalDirection = Direction.getHorizontalComponent(direction);

        for (int i = 1; i <= range; i++ )
        {
            if (verticalDirection == Direction.UP)
                curPos.y += variation;
            else if (verticalDirection == Direction.DOWN)
                curPos.y -= variation;

            if (horizontalDirection == Direction.LEFT)
                curPos.x += variation;
            else if (horizontalDirection == Direction.RIGHT)
                curPos.x -= variation;

            objectTracked.setPosition(curPos.x, curPos.y);

            if ( !objectTracked.collidesWith(collidingObject))
            {
                success = true;
                break;
            }
        }
        return success;
    }

    public boolean resolveCollision(GameObject collidingObject)
    {
        //Resolve the collision using the current applicable method
        boolean success = true;

        /* Determine the direction in which the collision occurs with respect to the
         * object and then use it to resolve the collision by moving the object in the
         * opposite direction, note that the direction in which the object is moving is
         * not necessarily the collision direction, for example character can be moving
         * up and rub a wall on its right. */
        collisionDirection = determineCollisionDirection(collidingObject);
        // If collision is horizontal and the tracked object is a GameCharacter in mid-air,
        // mark it as touching a wall for potential wall-jump behavior.
        if ((collisionDirection == Direction.LEFT || collisionDirection == Direction.RIGHT) &&
                objectTracked instanceof gameframework.gamecharacters.GameCharacter &&
                ((gameframework.gamecharacters.GameCharacter)objectTracked).isInMidAir())
        {
            ((gameframework.gamecharacters.GameCharacter)objectTracked).setWallContact(collisionDirection);
        }
        //System.out.println("Resolving collision " + collisionDirection );
        success = getClosestValidPosition(COLLISION_RESOLVE_TRIES, collidingObject,
                collisionDirection, ((GameCharacter)objectTracked).getSpeed());
        return success;

    }

    public boolean resolveCollision(GameObject collidingObject, Direction direction)
    {
        //Resolve the collision using the current applicable method
        boolean success = true;

        // If requested direction is horizontal and the tracked object is a GameCharacter in mid-air,
        // mark wall contact as well.
        if ((direction == Direction.LEFT || direction == Direction.RIGHT) &&
                objectTracked instanceof gameframework.gamecharacters.GameCharacter &&
                ((gameframework.gamecharacters.GameCharacter)objectTracked).isInMidAir())
        {
            ((gameframework.gamecharacters.GameCharacter)objectTracked).setWallContact(direction);
        }
        //System.out.println("Resolving collision " + collisionDirection );
        success = getClosestValidPosition(COLLISION_RESOLVE_TRIES, collidingObject,
                direction, ((GameCharacter)objectTracked).getSpeed());
        return success;

    }

}
