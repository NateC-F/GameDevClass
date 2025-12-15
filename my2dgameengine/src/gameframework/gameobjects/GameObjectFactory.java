package gameframework.gameobjects;

import java.util.Scanner;

/*
 * This is class used to create general game objects that apply to any game by extracting the information
 * from a text string (usually loaded from a file). Game Developers should extend this class in order to
 * create game objects that are specific to their game.
 */
public class GameObjectFactory
{
    // The information of the game object in the text line to be read consists of the following attributes
    // in that same order
    protected int posX;
    protected int posY;
    protected int type;
    protected String subtype;
    protected int scaleWidth;
    protected int scaleHeight;
    protected boolean thrownByPlayer;
    protected GameObject owner;

    public GameObject createGameObject(String objectStr)
    {
        GameObject gameObject = null;
        Scanner scanner = new Scanner(objectStr);
        scanner.next();          //All object info description lines in the level file are enclosed in '<' and '>', this skips the starting '<'
        posX = scanner.nextInt();
        posY = scanner.nextInt();
        type = scanner.nextInt();
        subtype = scanner.next();
        scaleWidth = scanner.nextInt();
        scaleHeight = scanner.nextInt();

        switch (type)
        {
            case GameObjectType.NPC:
                break;
            case GameObjectType.INANIMATE:
                gameObject = new InanimateObject(subtype + ".png", posX, posY, 2, scaleWidth, scaleHeight);
                break;
            case GameObjectType.COLLECTIBLE:
                gameObject = new Collectible(subtype + ".png", posX, posY, 2, scaleWidth, scaleHeight);
                break;

        }
        return gameObject;
    }

}
