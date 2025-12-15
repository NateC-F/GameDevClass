package gameframework.gameobjects;

import gameframework.GameThread;
import gameframework.animations.Animation;
import gameframework.animations.BorderPoint;
import gameframework.animations.SpriteBorder;
import gameframework.collision.CollisionHandler;
import gameframework.display.GameDisplay;
import gameframework.platforming.PlatformingHandler;
import gameframework.supportfunctions.GraphicsLibrary;
import gameframework.supportfunctions.Line;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public abstract class GameObject
{
    private String name;
    private int type;
    private int x;
    private int y;
    private int z;

    private double velX;
    private double velY;

    protected Direction direction;
    private int damage = 0;

    protected int scaleWidth;
    protected int scaleHeight;

    private static final double DEFAULT_GRAVITY = 0.3;
    private static final double DEFAULT_TERMINAL_VELOCITY = 10;
    protected double gravity;
    protected double terminal_velocity;

    protected Animation curAnimation;

    protected boolean requiresUpdating;

    // if true object is not currently on top of a platform
    protected boolean inMidAir;

    protected boolean constrainToBackground;

    public static boolean drawBoundsRect = false;
    public static boolean drawSpriteBorders = false;
    public static boolean disableRendering = false;

    // internal object attribute used to handle collisions
    private CollisionHandler collisionHandler;
    protected boolean disableCollision;
    // internal object attribute used to manage all platforming for this object
    public boolean alwaysUseRectCollision = false;
    private PlatformingHandler platformingHandler;

    /* This attribute is used to keep track of which area or areas of the background a
     * given object is currently in, this is used for optimizing collision handling in
     * the engine. */
    private ArrayList<Point> backgroundAreas;

    /* temporary, only for debugging!!!!*/
    public boolean isNinja() { return false;}
    /****/

    public GameObject(String name, int type,
                      int x, int y, int z,
                      int scaleWidth, int scaleHeight
    )
    {
        this.name = name;
        this.type = type;
        this.x = x;
        this.y = y;
        this.z = z;
        direction = Direction.NONE;
        this.scaleHeight = scaleHeight;
        this.scaleWidth = scaleWidth;
        velX = velY = 0;
        gravity = DEFAULT_GRAVITY;
        terminal_velocity = DEFAULT_TERMINAL_VELOCITY;

        // initialize collision handler
        collisionHandler = new CollisionHandler(this);
        //enable collision by default (child classes can override it), but disable collision for
        //predefined objects determined by the game developer
        disableCollision = false;
        System.out.println(name);
        if (GameThread.disableCollisionNames.contains(name))
        {
            disableCollision = true;
            System.out.println("Disabled collision for " + name);
        }
        //initialize platforming handler
        platformingHandler = new PlatformingHandler(this, null);

        //intialize array that keeps track of background areas this object belongs to
        backgroundAreas = new ArrayList<Point>();

        // By default all objects require to be updated
        requiresUpdating = true;
        // All objects start on the ground (platform) by default
        inMidAir = false;

        // By default objects cannot move beyond the game's background
        constrainToBackground = true;
    }

    public int getX() {return x;}
    public int getY() {return y;}
    public int getZ() {return z;}
    /* Setter for x and y are provided for special cases, but should be avoided when changing the position of
     * the object in the game, that should be changed exclusively using the setPosition method, so the engine
     * can keep track internally of important object positioning and tracking info. */
    public void setX(int x)
    {
        this.x = x;
    }
    public void setY(int y)
    {
        this.y = y;
    }
    /***/
    public void setZ(int z)
    {
        if ( z >= 0 )
            this.z = z;
    }

    public double getVelX()
    {
        return velX;
    }

    public void setVelX(double velX)
    {
        this.velX = velX;
    }

    public double getVelY()
    {
        return velY;
    }

    public void setVelY(double velY)
    {
        this.velY = velY;
    }

    //these methods are used in the weapons class to get the gameObject's scaleWidth and height - DV
    public int getScaleWidth()
    {
        return scaleWidth;
    }

    public int getScaleHeight()
    {
        return scaleHeight;
    }

    public String getName()
    {
        return name;
    }

    public int getType()
    {
        return type;
    }

    public void setType(int type)
    {
        if (type >= 0)
            this.type = type;
    }

    public Direction getDirection()
    {
        return direction;
    }

    public boolean isInanimate()
    {
        return false;
    }
    //Is this an inanimate object that never changes position
    public boolean isUnmovable()
    {
        return isInanimate();
    }

    //Used to disable nearby tile relatching for objects in certain situations
    public boolean disableAutoRelatching() { return false; }  //objects automatically relatch to nearby tiles by default

    public boolean requiresUpdating()
    {
        return requiresUpdating;
    }

    /* We use this function to render either the bounds rectangle or the actual borders of a sprite
     * depending on which of those options are enabled. */
    protected void renderBorders(Graphics g)
    {
        Color oldColor = g.getColor();

        if (drawBoundsRect)
        {
            //draw the bounds rectangle of every object
            Rectangle collisionBounds = getCollisionBounds();
            g.setColor(Color.BLUE);
            g.drawRect(collisionBounds.x, collisionBounds.y, collisionBounds.width, collisionBounds.height);
        }

        if (drawSpriteBorders)
        {
            SpriteBorder borders = getSpriteBorders(true);

            if (borders == null)
                return;

            //traverse through set of points and draw each point
            for (BorderPoint p: borders)
            {
                GraphicsLibrary.drawPixel(g, p, Color.RED);
            }
        }
        //restore previous color in graphics object
        g.setColor(oldColor);
    }

    public void render(Graphics g)
    {
        //draw sprite borders
        renderBorders(g);

        if ( !disableRendering && curAnimation != null)
        {
            curAnimation.drawFrame(g, x, y);
            curAnimation.nextFrame();
        }
    }

    public Animation getActiveAnimation() {
        return curAnimation;
    }

    /**
     * Change the current animation and make sure to reset it
     */
    public void changeActiveAnimation(Animation newAnimation, boolean reset)
    {
        if ( newAnimation != null)
        {

            /* Trying to change the animation  to the same one while its still running will
             * result in weird effects and it should be prevented (there is no reason to do that
             * anyway), changing to a different animation is fine at anytime.
             */
            if ( curAnimation != null && (!curAnimation.isPaused() && newAnimation == curAnimation))
                return;

            //System.out.println("Old animation was: " + (curAnimation != null ? curAnimation.getName() : "None"));
            //stop old animation completely
            if (curAnimation != null)
                curAnimation.stop();
            curAnimation = newAnimation;
            if (reset)
                curAnimation.reset(); //reset the new animation
            //System.out.println("New animation is: " + curAnimation.getName());

        }
    }

    // Returns the animation used when determining if an object is latched to a platform or not
    public Animation getPlatformingReferenceAnimation()
    {
        return getActiveAnimation();
    }

    public Point getPosition()
    {
        return new Point((int)x,(int)y);
    }

    /* This method should be the only way used to change positions, because it
     * internally updates important data structures used for collision optimization,
     * and in order to easily trace position changes while debugging the game.*/
    public void setPosition(int x, int y)
    {
        this.x = x;
        if ( y > this.y && platformingHandler.getPlatformObject() != null)
        {
            //If an object is standing on a platform, its y coord should never increase
            y = this.y;
        }
        this.y = y;

        GameObjects gameObjects = GameThread.data.getObjects();
        gameObjects.updateSpatialCells(this);
    }

    public double getGravity()
    {
        return gravity;
    }

    public void setGravity(double gravity)
    {
        // For the time being assume gravity must be positive
        if (gravity >= 0)
        {
            this.gravity = gravity;
        }
    }

    private void applyGravityEffect()
    {
        //If an object isn't affected by gravity this method has no effect
        if (getGravity() == 0)
            return;

        if (isInMidAir())
        {
            velY += gravity;
            if (velY > terminal_velocity && terminal_velocity >= 0)
                velY = DEFAULT_TERMINAL_VELOCITY;
        }
        else
        {
            //when an object is no longer in the air we must make sure to set its vel y back to 0
            velY = 0;
        }
    }

    /* returns the total effect of gravitational forces on this object, this includes
     * the object's gravity but also surface resistance (if standing on a platform) and
     * maybe even other forces (like an anti gravity field, etc).
     */
    private double getEffectiveGravity()
    {
        return platformingHandler.getEffectiveGravity();
    }

    public boolean isFalling()
    {
        return inMidAir;
    }

    //returns true if an object has no surface below (might be falling or jumping)
    public boolean isInMidAir()
    {
        return inMidAir;
    }

    public void setInMidAir(boolean onAir)
    {
        //If an object isn't affected by gravity this method has no effect
        if (getGravity() == 0)
            return;

        this.inMidAir = onAir;
    }

    //Returns true if the object is falling on top of the other object
    public boolean isLandingOnTopOf(GameObject otherObject)
    {
        return platformingHandler.isLandingOnTopOf(otherObject);
    }

    //Returns true if this object is currently placed on top of another object that acts as a platform for it
    public boolean isPlacedOnTopOf(GameObject otherObject)
    {
        return platformingHandler.isPlacedOnTopOf(otherObject);
    }

    //This method is used for an object to attach to another and use it as a platform
    public boolean latch(GameObject platformObject)
    {
        return  platformingHandler.latch(platformObject);
    }

    /* Every object must override and extend these methods in order to handle
     * their own individual updates and how they handle collision with the
     * other objects in the game.
     */
    public void update(GameObjects objects)
    {
        // implement support for platforming for games that consider gravity
        if (getGravity() > 0)
        {
            applyGravityEffect();
            platformingHandler.update(objects);
        }

        /* Only objects that move need to update their position or handle their own collisions. By having
         * only the object moving (cause of the collision) handle the collision, we eliminate a  lot of the
         * computational overhead. */
        if ( !isUnmovable() )
        {
            setPosition(getX() + (int)velX, getY() + (int)velY);
            collision(objects);
        }
    }
    public abstract boolean handleObjectCollision(GameObject object);

    /** This method is used to enable/disable collision based on sprite borders, if disabled
     * then we use the collision bounds (tightest rectangle bounds fit to the sprite borders)
     * when evaluating all kind of collisions. */
    public void enableSpriteBordersCollision(boolean enable)
    {
        collisionHandler.enableSpriteBordersCollision(enable);
    }

    public boolean isSpriteBordersCollisionEnabled()
    {
        return collisionHandler.isSpriteBordersCollisionEnabled();
    }

    // Get a bounds rectangle for the overall object, based on its general position
    // and scaling width and height factors.
    public Rectangle getBounds()
    {
        Rectangle boundsRect = new Rectangle((int)x, (int)y, scaleWidth,
                scaleHeight);
        return boundsRect;
    }

    /* Get a bounds rectangle which is suitable for collision detection, that is,
     * a tight fit to the actual borders of the current sprite frame in the
     * object's animation */
    public Rectangle getCollisionBounds()
    {
        return collisionHandler.getCollisionBounds();
    }

    /* This method returns all points comprising the borders of the sprite of the current frame within
     * the active animation. When performance is an issue we can disable repositioning. */
    public SpriteBorder getSpriteBorders(boolean reposition)
    {
        SpriteBorder spriteBorders = null;

        if (curAnimation != null)
        {
            // unmovable objects are automatically repositioned by the engine at load time for performance
            // reasons, so we ignore any further repositioning requests

            spriteBorders = curAnimation.getCurrentFrameBorders((int)x, (int)y, !isUnmovable() && reposition);
        }
        return spriteBorders;
    };

    // This method returns true if the object collides with another
    // given object or false otherwise.
    public boolean collidesWith(GameObject otherObject)
    {
        boolean objectsCollide = false;
        objectsCollide = collisionHandler.checkCollision(otherObject);
        return objectsCollide;
    }

    public boolean handleCollision(GameObject collidingObject)
    {
        return collisionHandler.handleCollision(collidingObject);
    }

    public void collision(GameObjects gameObjects)
    {
        // Loop through all other objects and handle any collisions between this object
        // and any other one
        final int TOLERANCE_PIXELS = 10;

        // Retrieve only the objects that are in the same area(s) of the screen as this object and
        // therefore could potentially collide.
        GameObjects collisionObjects = gameObjects.getNeighborObjects(this);

        for ( int i = 0; i < collisionObjects.size(); i++)
        {
            GameObject go = collisionObjects.get(i);

            if (go == this)
                continue;

            //Ignore objects that should not collide with this one
            if (shouldIgnoreCollisionWith(go) || go.shouldIgnoreCollisionWith(this) || go.isDisableCollision())
                continue;

            // ignore objects that are acting as a platform for this one
            // as those are handled by the platforming handler
            if (isPlacedOnTopOf(go) ||
                    // objects vertically aligned with this object's platform could be sections of the same platform
                    go.isAtSimilarHeightAs(platformingHandler.getPlatformObject(), TOLERANCE_PIXELS))
                continue;

            // Handle collision here for any objects that require some action
            // by the game object or character when collision occurs
            if ( collidesWith(go))
            {
                // allow each character/object to handle the collision in a specific way
                boolean handled = handleObjectCollision(go);

                if (!handled)
                {
                    /*System.out.println("Unable to handle collision with object "
                            + go.getName());*/
                    break;
                }
            }
        }
        return;
    }

    // Determine if an object is fully contained within the given bounds.
    public boolean isWithinBounds(Rectangle bounds)
    {
        boolean withinBounds = false;

        if (
                getX() + scaleWidth > bounds.x &&
                        getX() < bounds.x + bounds.width &&
                        getY() + scaleHeight > bounds.y &&
                        getY() < bounds.y + bounds.height
        )
            withinBounds = true;

        return withinBounds;
    }

    //Make sure this object is never positioned outside the game world
    public void enforceBackgroundBounds()
    {
        //This method is only effective if the constrain to background flag is enabled
        if (constrainToBackground)
        {
            BufferedImage background = GameDisplay.getCurBackground();
            if (background != null)
            {
                Rectangle backgroundBounds = new Rectangle(0, 0, background.getWidth(), background.getHeight());
                enforceBounds(backgroundBounds);
            }
        }
    }

    //Make sure this objects is never positioned outside the given bounds area
    public void enforceBounds(Rectangle boundArea )
    {
        int scaledWidth = curAnimation.getScaleWidth();
        int scaledHeight = curAnimation.getScaleHeight();

        int updatedX = 0, updatedY = 0;

        if (x < boundArea.x)
        {
            updatedX = boundArea.x;
        }
        else if (x > boundArea.x + boundArea.width - scaledWidth)
        {
            updatedX = boundArea.x + boundArea.width - scaledWidth;
        }

        if (y < boundArea.y)
        {
            updatedY = boundArea.y;
        }
        else if (y > boundArea.y + boundArea.height - scaledHeight)
        {
            updatedY = boundArea.y + boundArea.height - scaledHeight;
        }

        setPosition(updatedX, updatedY);
    }

    // Calculates the point-to-point distance between the center of this object and another one.
    public double calculateDistanceToObject(GameObject go)
    {
        Point centerObject = new Point(this.getX() + scaleWidth / 2,
                this.getY() + scaleHeight / 2);

        Point centerOtherObject = new Point(go.getX() + go.scaleWidth / 2,
                go.getY() + go.scaleHeight / 2);

        return GraphicsLibrary.computePointsDistance(centerObject, centerOtherObject);
    }

    /* This method is used to determine if this object encompasses another
     * object (the object is within its bounds for one dimension) either
     * horizontally or vertically. Its possible to also set a percentage in
     * which case the  method returns true if a certain part of the object is
     * within the height/width of the other.
     */
    public boolean encompasses(GameObject otherObject, char horzOrVert, double percentage)
    {
        boolean success = false;
        Rectangle bounds = getCollisionBounds();
        Rectangle otherBounds = otherObject.getCollisionBounds();
        Line line = null, otherLine = null;

        if (horzOrVert == 'H')
        {
            line = new Line(new Point(bounds.x, 0), new Point(bounds.x + bounds.width, 0));
            otherLine = new Line(new Point(otherBounds.x, 0),
                    new Point(otherBounds.x + otherBounds.width, 0));
        }
        else
        {
            line = new Line(new Point(0, bounds.y), new Point(0, bounds.y + bounds.height));
            otherLine = new Line(new Point(0, otherBounds.y),
                    new Point(0, otherBounds.y + otherBounds.height));
        }

        Integer intersectionLength = line.getIntersectionLength(otherLine);

        //To qualify, a certain portion of one line must be contained within the other
        int amount = (int)((horzOrVert == 'H' ? otherBounds.width : otherBounds.height) * percentage);
        if ( line.intersectsWith(otherLine)  &&
                intersectionLength >= amount
        )
            success = true;

        return success;
    }

    public ArrayList<Point> getBackgroundAreas()
    {
        return backgroundAreas;
    }

    public void setBackgroundAreas(ArrayList<Point> backgroundAreas)
    {
        if (backgroundAreas != null && !backgroundAreas.isEmpty())
            this.backgroundAreas = backgroundAreas;
    }

    public boolean shouldIgnoreCollisionWith(GameObject other)
    {
        return false;  // default behavior
    }

    /**
     * Checks if this object is at a similar vertical (Y) position as another object.
     * This can be useful for determining if two objects are roughly on the same platform
     * or ground level. The comparison uses a small tolerance (in pixels) to allow for minor
     * differences.
     **/
    public boolean isAtSimilarHeightAs(GameObject otherObject, int tolerance)
    {
        if (otherObject == null)
            return false;
        return Math.abs(getY() - otherObject.getY()) <= tolerance;
    }

    /* These methods are used to reposition objects at the collision bounds
     * level rather than the general object bounds. They will reposition the
     * object so that the bounds of the current animation frame (collision
     * bounds) will start at the given position.
     */
    public void setCollisionX(int x)
    {
        Rectangle bounds = getBounds();
        Rectangle collisionBounds = getCollisionBounds();

        int differenceX = collisionBounds.x - bounds.x;

        setPosition(x - differenceX, y);
    }

    public void setCollisionY(int y)
    {
        Rectangle bounds = getBounds();
        Rectangle collisionBounds = getCollisionBounds();

        int differenceY = collisionBounds.y - bounds.y;

        setPosition(x,y - differenceY);
    }

    public boolean isDisableCollision()
    {
        return disableCollision;
    }

    public void setDisableCollision(boolean disableCollision)
    {
        this.disableCollision = disableCollision;
    }

    public int getDamage()
    {
        return damage;
    }

    public void setDamage(int damage)
    {
        this.damage = damage;
    }
    /**/
}
