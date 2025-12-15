package gameframework.gameobjects;

public enum Direction
{
    NONE, LEFT, UP, RIGHT, DOWN,
    RIGHTUP, RIGHTDOWN, LEFTUP, LEFTDOWN;

    public static Direction ordinalToDirection(int ordinal)
    {
        if (ordinal < 0 || ordinal >= values().length)
        {
            return Direction.NONE; // safe fallback
        }
        return values()[ordinal];
    }

    public static Direction getCompositeDirection(Direction vertical, Direction horizontal)
    {
        if (vertical == Direction.NONE) return horizontal;
        if (horizontal == Direction.NONE) return vertical;

        if (vertical == Direction.UP)
        {
            return (horizontal == Direction.LEFT) ? Direction.LEFTUP : Direction.RIGHTUP;
        } else
        {
            return (horizontal == Direction.LEFT) ? Direction.LEFTDOWN : Direction.RIGHTDOWN;
        }
    }

    public static boolean isCompositeDirection(Direction direction)
    {
        return (direction == Direction.LEFTDOWN || direction == Direction.LEFTUP ||
                direction == Direction.RIGHTDOWN || direction == Direction.RIGHTUP);
    }

    public static boolean isHorizontal(Direction direction)
    {
        return (direction == Direction.LEFT || direction == Direction.RIGHT);
    }

    public static boolean isVertical(Direction direction)
    {
        return (direction == Direction.UP || direction == Direction.DOWN);
    }

    public static Direction getVerticalComponent(Direction direction)
    {
        if (direction == Direction.UP || direction == Direction.RIGHTUP || direction == Direction.LEFTUP)
            return Direction.UP;

        if (direction == Direction.DOWN || direction == Direction.RIGHTDOWN || direction == Direction.LEFTDOWN)
            return Direction.DOWN;

        return Direction.NONE;
    }

    public static Direction getHorizontalComponent(Direction direction)
    {
        if (direction == Direction.RIGHT || direction == Direction.RIGHTUP || direction == Direction.RIGHTDOWN)
            return Direction.RIGHT;

        if (direction == Direction.LEFT || direction == Direction.LEFTUP || direction == Direction.LEFTDOWN)
            return Direction.LEFT;

        return Direction.NONE;
    }
}