package gameframework;
import gameframework.gamecharacters.Player;
import gameframework.gameobjects.GameObject;

import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedList;

public class GameLevel
{
    private String name;
    private int number;
    private String background;
    private String theme;
    private Point playerStartPos;
    private boolean playThemeOnLoad;

    public GameLevel(String initName, int initNumber, String initBackground,
                     String initTheme,
                     Point initPlayerStartPos)
    {
        name = initName;
        number = initNumber;
        background = initBackground;
        theme = initTheme;
        playerStartPos = initPlayerStartPos;
        playThemeOnLoad = true /*false*/;
    }

    public String getName()
    {
        return name;
    }

    public int getNumber()
    {
        return number;
    }

    public String getBackground()
    {
        return background;
    }

    public String getTheme()
    {
        return theme;
    }

    public Point getPlayerStartPos()
    {
        return playerStartPos;
    }

    /* This method reads a text file with the information for the level and loads all
     * objects from the game into the game objects list. */
    public boolean load(GameData data) throws Exception
    {
        LinkedList<GameObject> gameObjects = data.getObjects();
        /* Make sure we first free all resources allocated for previous levels otherwise
         * we might run out of memory (level background images and some audio clips can take
         * huge amounts of memory in particular).
         */
        GameThread.resourceManager.freeResources();

        ArrayList<String> text = GameThread.resourceManager.loadTextResource(name + ".txt", name);

        //make sure to clear game objects list before loading objects for new level
        gameObjects.clear();

        for (String textLine : text)
        {
            //If a line in the file is commented then ignore it
            if ( textLine.startsWith("//"))
                continue;
            GameObject gameObject = GameThread.gameObjectFactory.createGameObject(textLine);
            //ignore any objects that fail to load
            if (gameObject == null )
                continue;
            gameObjects.add(gameObject);
        }

        //set player starting position and add player to level
        Player player = Player.getActivePlayer();
        if (player != null)
        {
            player.setPosition(playerStartPos.x, playerStartPos.y);
            gameObjects.add(player);
        }

        //set level theme
        GameThread.gameAudio.setMainTheme(theme);
        if (playThemeOnLoad())
            GameThread.gameAudio.playMainTheme(false);
        else
            GameThread.gameAudio.stopMainTheme(true);

        return true;
    }

    public boolean playThemeOnLoad()
    {
        return playThemeOnLoad;
    }

    // enable/disable automatic playing of the level theme tune
    public void setPlayThemeOnLoad(boolean playThemeOnLoad)
    {
        this.playThemeOnLoad = playThemeOnLoad;
    }

}
