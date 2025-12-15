/*
 * Implementation of a class that represents a straight line including useful functions such as
 * computing the slope or finding the point of intersection among lines.
 */
package gameframework.supportfunctions;

import java.awt.Point;
import java.awt.Rectangle;

public class Line
{
    private Point start;
    private Point end;
    private double slope;

    public Line(Point start, Point end)
    {
        this.start = start;
        this.end = end;
        computeSlope();
    }

    private void computeSlope()
    {
        if ( start.x != end.x )
            slope = (double)(end.y - start.y) / (end.x - start.x);
    }

    public double getLength()
    {
        return GraphicsLibrary.computePointsDistance(start, end);
    }

    //returns true if lines intersect or false otherwise
    public boolean intersectsWith(Line otherLine)
    {
        boolean intersects = false;

        if (isHorizontal() && otherLine.isHorizontal() && start.y == otherLine.start.y)
        {
            if (
                    ( start.x >= otherLine.start.x && start.x <= otherLine.end.x) ||
                            ( end.x >= otherLine.start.x && end.x <= otherLine.end.x) ||
                            ( start.x < otherLine.start.x && end.x > otherLine.end.x)
            )
                intersects = true;
        }
        else if (isVertical() && otherLine.isVertical() && start.x == otherLine.start.x)
        {
            if (
                    ( start.y >= otherLine.start.y && start.y <= otherLine.end.y) ||
                            ( end.y >= otherLine.start.y && end.y <= otherLine.end.y)||
                            ( start.y < otherLine.start.y && end.y > otherLine.end.y)
            )
                intersects = true;
        }
        else
            intersects = (findIntersectionPoint(otherLine) != null);

        return intersects;
    }

    //Finds the length of the line portion shared between two lines.
    //Applies only to horizontal and vertical lines, returns null otherwise.
    public Integer getIntersectionLength(Line otherLine)
    {
        Integer intersectionLength = null;
        int intersectionStart = 0, intersectionEnd = 0;

        if (isHorizontal() && otherLine.isHorizontal())
        {
            intersectionStart = (start.x < otherLine.start.x) ? otherLine.start.x : start.x;
            intersectionEnd = (end.x > otherLine.end.x) ? otherLine.end.x : end.x;
        }
        else if (isVertical() && otherLine.isVertical())
        {
            intersectionStart = (start.y < otherLine.start.y) ? otherLine.start.y : start.y;
            intersectionEnd = (end.y > otherLine.end.y) ? otherLine.end.y : end.y;
        }

        intersectionLength = intersectionEnd - intersectionStart + 1;
        return intersectionLength;
    }

    /* Given a coordinate (X or Y as determined by a boolean),
     * returns a point within the line with that coordinate or
     * null if no such point exists in the line.
     */
    public Point findPointInLine(int coordinate, boolean coordIsX )
    {
        Point point = null;
        int otherCoord = 0;
        int lowerBound, upperBound;

        if (coordIsX)
        {
            otherCoord = (int)(slope * (coordinate - start.x) + end.y);

            if (start.y < end.y)
            {
                lowerBound = start.y;
                upperBound = end.y;
            }
            else
            {
                lowerBound = end.y;
                upperBound = start.y;
            }

            if (otherCoord >= lowerBound && otherCoord <= upperBound )
                point = new Point(coordinate,otherCoord);

        }
        else
        {
            otherCoord = (int)((coordinate - start.y) / slope + start.x);

            if (start.x < end.x)
            {
                lowerBound = start.x;
                upperBound = end.x;
            }
            else
            {
                lowerBound = end.x;
                upperBound = start.x;
            }

            if (otherCoord >= lowerBound && otherCoord <= upperBound )
                point = new Point(otherCoord, coordinate);
        }
        return point;
    }

    public boolean isHorizontal()
    {
        return start.y == end.y;
    }

    public boolean isVertical()
    {
        return start.x == end.x;
    }

    /* Finds intersection point between two lines
     * Returns null if there is no intersection.
     */
    public Point findIntersectionPoint(Line otherLine)
    {
        Point intersection = null;
        int coordX = 0;
        int coordY = 0;

        double slope1 = this.slope;
        double slope2 = otherLine.slope;

        if (otherLine.isHorizontal())
        {
            //if both lines are horizontal makes no sense to find intersection point
            if (isHorizontal())
                intersection = null;
            else
            {
                coordY = otherLine.start.y;
                intersection = findPointInLine(coordY, false);
            }
        }
        else if (otherLine.isVertical())
        {
            //if both lines are vertical makes no sense to find intersection point
            if (isVertical())
                intersection = null;
            else
            {
                coordX = otherLine.start.x;
                intersection = findPointInLine(coordX, true);
            }
        }
        else if (slope1 != slope2)
        {
            //If the slopes aren't the same compute the intersection,
            //otherwise the lines are parallel and therefore no intersection.

            /* Line formulas for reference
             * y = m1(x - x1) + y1
             * y = m2(x - x2) + y2
             * x = (y2 - y1 - m2x2 + m1x1) / m1 - m2
             */

            double dividend = (otherLine.getStart().y - start.y -
                    slope2 * otherLine.getStart().x  + slope1 * start.x) ;
            double divisor = (slope1 - slope2);

            coordX = (int)(dividend / divisor);
            intersection = findPointInLine(coordX, true);
        }
        return intersection;
    }

    //Get the rectangle whose diagonal is this line
    public Rectangle getEnclosingRectangle()
    {
        Rectangle rect = new Rectangle();
        if (start.x < end.x)
            rect.x = start.x;
        else
            rect.x = end.x;

        if (start.y < end.y)
            rect.y = start.y;
        else
            rect.y = end.y;

        rect.width = Math.abs(start.x - end.x);
        rect.height = Math.abs(start.y - end.y);

        return rect;
    }

    // Finds the angle between this line and another line
    public double findAngleBetweenLines(Line otherLine)
    {
        double angle1 = Math.atan2(this.getStart().getY() - this.getEndPoint().getY(),
                this.getStart().getX() - this.getEndPoint().getX());
        double angle2 = Math.atan2(otherLine.getStart().getY() - otherLine.getEndPoint().getY(),
                otherLine.getStart().getX() - otherLine.getEndPoint().getX());
        return Math.abs(angle1) - Math.abs(angle2);
    }

    /**
     * @return the start point of the line
     */
    public Point getStart()
    {
        return start;
    }

    /**
     * change the start point of the line
     */
    public void setStart(Point start)
    {
        this.start = start;
        computeSlope();
    }

    /**
     * @return the end point of the line
     */
    public Point getEndPoint()
    {
        return end;
    }

    /**
     * change the end point of the line
     */
    public void setEndPoint(Point end)
    {
        this.end = end;
        computeSlope();
    }

    /**
     * @return the line slope
     */
    public double getSlope()
    {
        return slope;
    }

    public Point getCenterPoint()
    {
        return new Point( (start.x + end.x) / 2,
                (start.y + end.y) / 2
        );
    }



}
