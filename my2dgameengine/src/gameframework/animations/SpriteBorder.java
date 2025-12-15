package gameframework.animations;

import gameframework.gameobjects.Direction;
import gameframework.supportfunctions.GraphicsLibrary;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.TreeSet;

/*
 * This is class consists basically on an ordered set (by x coordinate) of border points,
 * we create it by extending the java TreeSet class and providing extra functionality like
 * the ability to check if borders intersect, among many others.
 */
public class SpriteBorder extends TreeSet<BorderPoint>
{
    /* We store the bound points of the borders in this array.
     * These are basically the leftmost, rightmost,topmost and
     * bottommost points within the borders. These points are
     * very useful when detecting collisions.
     */
    private ArrayList<BorderPoint> boundPoints;

    public SpriteBorder()
    {
        super();
        boundPoints = new ArrayList<BorderPoint>(4);
        for (int i = 0; i < 4; i++)
            boundPoints.add(new BorderPoint(-1,-1));
    }

    /* This method is used to do a deep copy of a SpriteBorder object (which contains points
     * representing the borders of a sprite).*/
    @Override
    public SpriteBorder clone()
    {
        SpriteBorder copy = new SpriteBorder();

        for (BorderPoint p: this)
        {
            BorderPoint clonedPoint = (BorderPoint)p.clone();
            copy.add(clonedPoint);

            if (boundPoints == null)
                continue;

            //make sure to also keep track of the
            //four bound points in the new border object
            if (boundPoints.contains(p))
                copy.boundPoints.set(boundPoints.indexOf(p), clonedPoint);

            if ( boundPoints.size() > 4)
                System.out.println("sprite borders error, this should never happen!");
        }
        return copy;
    }

    private BorderPoint getBoundPoint(Direction direction)
    {
        BorderPoint requestedPoint = null;

        if (direction == Direction.LEFT)
            requestedPoint = boundPoints.get(0);
        else if (direction == Direction.RIGHT)
            requestedPoint = boundPoints.get(2);
        else if (direction == Direction.UP)
            requestedPoint = boundPoints.get(1);
        else if (direction == Direction.DOWN)
            requestedPoint = boundPoints.get(3);

        return requestedPoint;
    }

    private boolean setBoundPoint(BorderPoint boundPoint, Direction direction)
    {
        boolean success = true;

        if (direction == Direction.LEFT)
            boundPoints.set(0, boundPoint);
        else if (direction == Direction.RIGHT)
            boundPoints.set(2, boundPoint);
        else if (direction == Direction.UP)
            boundPoints.set(1, boundPoint);
        else if (direction == Direction.DOWN)
            boundPoints.set(3, boundPoint);
        else
            success = false;

        add(boundPoint);
        return success;
    }

    /* Returns false if bound points don't represent a valid rectangle. This can happen for
     * some specific sprites (for example if the sprite has no transparent background) where
     * we don't end up with four unique values for the points. */
    public boolean verifyBoundPoints()
    {
        boolean valid = true;

        if (boundPoints == null)
            return false;

        //Check that all four bound points are actually in the tree set
        for (BorderPoint bpt : boundPoints)
        {
            if ( bpt == null || !contains(bpt))
            {
                valid = false;
                break;
            }
        }

        //Save the verification time in the future by setting boundPoints to null
        if (!valid)
            boundPoints = null;

        return valid;

    }

    /* Returns a rectangle that fitly encloses the borders. This is very useful when doing
     * collision detection. We use the bound points to generate that Rectangle. */
    public Rectangle getBordersRectangle()
    {
        int posX   = getBoundPoint(Direction.LEFT).x;
        int posY   = getBoundPoint(Direction.UP).y;
        int width  = getBoundPoint(Direction.RIGHT).x - getBoundPoint(Direction.LEFT).x;
        int height = getBoundPoint(Direction.DOWN).y - getBoundPoint(Direction.UP).y;

        return new Rectangle(posX, posY, width, height);
    }

    /* Adjusts all border point positions to reflect the current sprite position, returns
     * a new sprite border object with the updated info. */
    public SpriteBorder reposition(int posX, int posY)
    {
        SpriteBorder adjustedBorders = (SpriteBorder)this.clone();

        for (BorderPoint p: adjustedBorders)
        {
            p.x += posX;
            p.y += posY;
        }
        return adjustedBorders;
    }

    /* This method checks if this sprite intersects with another sprite by looking to see
     * if any of the points composing their borders are common to both. Note that this expects
     * that the set of points for both border objects has already been adjusted to reflect the
     * actual position of the points on screen.
     */
    public boolean bordersIntersect(SpriteBorder otherSpriteBorders )
    {
        for (BorderPoint p : this) {
            if (otherSpriteBorders.contains(p)) {
                return true; // found a common border point
            }
        }
        return false;
    }

    public boolean borderIntersectWithRect(SpriteBorder otherSpriteBorders) {
        //Get rect of other object and check if contains point
        Rectangle rect = otherSpriteBorders.getBordersRectangle();
        for (BorderPoint p : this) {
            if (rect.contains(p))
                return true;
        }
        return false;
    }


    /* Returns all points that surround an actual sprite, these are the real borders of the sprite
     * that should be used for most accurate collision detection rather than the bounds rectangle.
     * To obtain the borders this method skips all transparent pixels from the background and determines
     * where the actual sprite border pixels are. */
    public static SpriteBorder getSpriteBorders(Image image,
                                                int scaleWidth,
                                                int scaleHeight)
    {
        SpriteBorder borders = new SpriteBorder();
        BufferedImage scaledImage = GraphicsLibrary.generateScaledImage(image, scaleWidth, scaleHeight);

        // We also determine the border bound points (the left most, top most, right most
        // and bottom most points), which are very useful for collision detection

        int rightMostPixel = Integer.MIN_VALUE;
        int leftMostPixel = Integer.MAX_VALUE;
        int topMostPixel = Integer.MAX_VALUE;
        int bottomMostPixel = Integer.MIN_VALUE;

        BorderPoint boundPointLeft = null;
        BorderPoint boundPointRight = null;
        BorderPoint boundPointTop = null;
        BorderPoint boundPointBottom = null;

        for (int row = 0; row < scaledImage.getHeight(); row++)
        {
            //For every row find first pixel that is not transparent
            //starting from the right and also the one starting from
            //the left, and add them to the border set.

            //Find left most relevant pixel in this row
            for (int col = 0; col < scaledImage.getWidth(); col++)
            {
                int color = scaledImage.getRGB(col, row);

                if (!GraphicsLibrary.isColorTransparent(color))
                {
                    BorderPoint borderPoint = new BorderPoint(col, row);

                    //keep track of left most pixel among all rows)
                    if (col < leftMostPixel)
                    {
                        leftMostPixel = col;
                        boundPointLeft = borderPoint;
                    }

                    //pixel isn't transparent so add it as border point
                    borders.add(borderPoint);
                    break;
                }
            }

            //Find right most relevant pixel in this row
            for (int col = scaledImage.getWidth() - 1; col >= 0; col--)
            {
                int color = scaledImage.getRGB(col, row);

                if (!GraphicsLibrary.isColorTransparent(color))
                {
                    BorderPoint borderPoint = new BorderPoint(col, row);

                    //keep track of right most pixel among all rows)
                    if (col > rightMostPixel)
                    {
                        rightMostPixel = col;
                        boundPointRight = borderPoint;
                    }

                    // pixel isn't transparent so add it as border point
                    borders.add(borderPoint);
                    break;
                }
            }
        }

        /* Done with horizontal scans so set horizontal bound points, Keeping
         * track of bound points is useful for collision detection. */
        borders.setBoundPoint(boundPointLeft, Direction.LEFT);
        borders.setBoundPoint(boundPointRight, Direction.RIGHT);

        for (int col = 0; col < scaledImage.getWidth(); col++)
        {
            //For every column find first pixel that is not transparent
            //starting from the top and also the one starting from
            //the bottom, and add them to the border set.

            //Find top most relevant pixel in this column
            for (int row = 0; row < scaledImage.getHeight(); row++)
            {
                int color = scaledImage.getRGB(col, row);

                if (!GraphicsLibrary.isColorTransparent(color))
                {
                    BorderPoint borderPoint = new BorderPoint(col, row);

                    //keep track of top most pixel among all cols)
                    if (row < topMostPixel)
                    {
                        //make sure to avoid duplicate bound points (in some sprites the left/
                        //right most and top most pixels can be the same)
                        if (!borders.boundPoints.contains(borderPoint))
                        {
                            topMostPixel = row;
                            boundPointTop = borderPoint;
                        }
                    }

                    //pixel isn't transparent so add it as border point
                    borders.add(borderPoint);
                    break;
                }
            }

            //Find bottom most relevant pixel in this column
            for (int row = scaledImage.getHeight() - 1; row >= 0; row--)
            {
                int color = scaledImage.getRGB(col, row);

                if (!GraphicsLibrary.isColorTransparent(color))
                {
                    BorderPoint borderPoint = new BorderPoint(col, row);

                    //keep track of bottom most pixel among all cols)
                    if (row > bottomMostPixel)
                    {
                        //make sure to avoid duplicate bound points (in some sprites the left/
                        //right most and bottom most pixels can be the same)
                        if (!borders.boundPoints.contains(borderPoint))
                        {
                            bottomMostPixel = row;
                            boundPointBottom = borderPoint;
                        }
                    }

                    //pixel isn't transparent so add it as border point
                    borders.add(borderPoint);
                    break;
                }
            }

        }

        /* Done with vertical scans so set vertical bound points, Keeping
         * track of bound points is useful for collision detection. */
        borders.setBoundPoint(boundPointTop, Direction.UP);
        borders.setBoundPoint(boundPointBottom, Direction.DOWN);

        return borders;
    }

}
