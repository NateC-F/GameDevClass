package gameframework;

import gameframework.display.GameDisplay;
import gameframework.gamecharacters.Player;
import gameframework.gameobjects.GameObject;
import gameframework.gameobjects.GameObjects;

import java.awt.*;

public class GameData
{
    private GameObjects objects;

    // auxiliary lists used to keep track of objects that must be added/removed after an update
    private GameObjects addAfterUpdate;
    private GameObjects removeAfterUpdate;

    public GameData()
    {
        //initialize game objects list
        objects = new GameObjects(true);

        //create auxiliary lists
        addAfterUpdate = new GameObjects(false);
        removeAfterUpdate = new GameObjects(false);

        //load first game level
        try
        {
            GameThread.getCurrentLevel().load(this);
        }
        catch (Exception e)
        {
            System.out.println("Unable to load level " +
                    GameThread.getCurrentLevel().getName() + "\n" +
                    "Reason: " + e.getMessage());
        }
    }

    //Add a playable character, if the boolean is set to true then this is set
    //as the initial player character (can switch characters later on)
    public boolean addPlayableCharacter(Player playableCharacter,
                                        boolean startingCharacter)
    {
        boolean success = true;

        Player.addPlayer(playableCharacter, startingCharacter);

        if (startingCharacter)
        {
            objects.add(playableCharacter);
            //override position of the character with that of the current level
            Point playerStartPos = GameThread.getCurrentLevel().getPlayerStartPos();
            playableCharacter.setPosition(playerStartPos.x, playerStartPos.y);
        }
        return success;
    }

    /*This method is triggered during each update of the game loop, it iterates through the whole list of
      objects and allows them to use a fraction of the time to do their tasks and update themselves. */
    public synchronized void update(int tickNumber)
    {
        /*GameObjects objectsToUpdate = objects.getUpdateObjects();
        for (GameObject go : objectsToUpdate)
            go.update(objects);*/

        GameObjects objectsToUpdate = objects.getUpdateObjects();

        for (GameObject go : objectsToUpdate)
        {
            if (GameDisplay.objectWithinCameraView(go))
            {
                go.update(objects);
            }
            else
            {
                // Update off-screen objects less frequently
                if (tickNumber % GameThread.OFFSCREEN_OBJECTS_UPDATE_INTERVAL == 0)
                    go.update(objects);
            }
        }
        performPostUpdateTasks();
    }

    /* Perform any tasks in this method that can't be performed during the update cycle. For example
    *  any changes to the object list (like adding or removing objects) can't be performed during the
    *  update cycle, or we risk triggering a concurrent modification exception. */
    private void performPostUpdateTasks()
    {
        // If any objects were queued for addition or removal, perform the actions at this stage
        for (GameObject gameObject : addAfterUpdate)
            objects.add(gameObject);
        //objects.addAll(addAfterUpdate); Note: We cannot use addAll, because the add method in GameObjects handles z order
        addAfterUpdate.clear();

        for (GameObject gameObject : removeAfterUpdate)
            objects.remove(gameObject);
        removeAfterUpdate.clear();
    }

    /* Thread safe methods to add or remove objects to/from the game engine. Whenever an object
     * add/remove is triggered by the input handling thread, we should handle it using these methods
     * as they are synchronized with the object update loop in the main thread. */
     public synchronized boolean addObject(GameObject gameObject)
     {
         return objects.add(gameObject);
     }

    public synchronized boolean removeObject(GameObject gameObject)
    {
        return objects.remove(gameObject);
    }
    /****/

    /* These methods should be used by game developers when adding/removing objects while updating
     * any of the objects or characters in their game, these methods queue the action and perform
     * the actual object addition or removal once the update cycle is over to prevent concurrency
     * issues. */
    public boolean addObjectWhenSafe(GameObject gameObject)
    {
        return addAfterUpdate.add(gameObject);
    }
    public boolean removeObjectWhenSafe(GameObject gameObject)
    {
        return removeAfterUpdate.add(gameObject);
    }

    //get all objects in the game
    public GameObjects getObjects() {
        return objects;
    }

    //Returns a list of all objects in the game that are of the requested type.
    GameObjects getObjectsOfType(int type)
    {
        GameObjects foundObjects = objects.getObjectsOfType(type);
        return foundObjects;

    }
}
