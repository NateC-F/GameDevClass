package gameframework.animations;

import java.awt.Point;

/**
 * Class defining a border point, very useful when working
 * with sprite borders, works like a normal point but has
 * some flexibility on how to sort them.
 *
 */
public class BorderPoint extends Point implements Comparable<BorderPoint>
{
    private static boolean sortByX = true;

    public BorderPoint(int x, int y)
    {
        super(x,y);
    }

    @Override
    public Object clone()
    {
        BorderPoint copy = new BorderPoint(x, y);
        return copy;
    }

    public static void setSortByX(boolean sortX)
    {
        sortByX = sortX;
    }

    @Override
    public int compareTo(BorderPoint p)
    {
        if ( sortByX )
        {
            int cmp = Integer.compare(this.x, p.x);
            if (cmp != 0) return cmp;
            return Integer.compare(this.y, p.y);
        }
        else
        {
            int cmp = Integer.compare(this.y, p.y);
            if (cmp != 0) return cmp;
            return Integer.compare(this.x, p.x);
        }
    }

}

