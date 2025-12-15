package gameframework.gameobjects;

/*
 * Here is where we define all general object types that apply to the
 * game engine. Types ordinals 0 to 9 are currently reserved for system use.
 * Game Developers should extend this class in their games in order to
 * define their own game specific types.
 */
public class GameObjectType
{
    /* Type numbers 0-9 are reserved for the engine */
    public static final int PLAYER = 0;
    public static final int NPC = 1;
    public static final int COLLECTIBLE = 2;
    public static final int INANIMATE = 3;
}
