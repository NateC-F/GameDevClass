package gameframework.gameobjects;

import gameframework.GameThread;
import gameframework.display.GameDisplay;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.awt.*;
import java.util.Set;

/* This class extends the functionality of a normal LinkedList of
 * game objects in order to be able to add objects to the list based on
 * z order or optionally track objects that require frequent updating on
 * a hidden internal list, among other features.
 * Updated: We have added functionality to the list in order to maintain
 * hidden spatial area sublists (grid cells) to group nearby objects together
 * and speed up collision checks and other object interactions.
 */
public class GameObjects extends LinkedList<GameObject>
{
    /* This list is to store references only to objects
     * that require frequent updating (objects that are
     * moving or changing state)
     */
    private GameObjects updateObjects;

    /* This is a grid of cells representing game areas, the entire game background
     * is divided into areas, and an object list is assigned to each area, only the objects
     * in that area are included on each list. If in an object is part of more than one area,
     * its reference its added to all appropriate lists. This is independent of the main list that
     * has objects sorted by  z-order. */
    private ArrayList<ArrayList<GameObjects>> spatialGrid;
    private int gridCols;
    private int gridRows;
    private int cellWidth;
    private int cellHeight;

    public GameObjects(boolean enableSublists)
    {
        super();
        if (enableSublists)
        {
            updateObjects = new GameObjects(false);
            // Initialize spatial partitioning grid
            initializeSpatialGrid();
        }
        else
        {
            updateObjects = null;
            spatialGrid = null;
        }
    }

    private void initializeSpatialGrid()
    {
        BufferedImage background = GameDisplay.getCurBackground();
        if (background == null) return; // No background yet, skip setup, shouldn't happen!

        // If the optimization hasn't been enabled by the developer then we just use the main object list to manage all objects
        if (GameThread.AREA_GRID_COLS == 0 || GameThread.AREA_GRID_ROWS == 0)
        {
            spatialGrid = null;
            return;
        }

        gridCols = Math.max(1, GameThread.AREA_GRID_COLS);
        gridRows = Math.max(1, GameThread.AREA_GRID_ROWS);

        cellWidth = background.getWidth() / gridCols;
        cellHeight = background.getHeight() / gridRows;

        spatialGrid = new ArrayList<>();
        for (int row = 0; row < gridRows; row++)
        {
            ArrayList<GameObjects> rowList = new ArrayList<>();
            for (int col = 0; col < gridCols; col++)
            {
                rowList.add(new GameObjects(false));
            }
            spatialGrid.add(rowList);
        }
    }

    @Override
    public boolean add(GameObject go)
    {
        /* Add objects to the game object list but make
         * sure objects with a higher z priority attribute
         * (lower value) are stored after in the list.
         * That way they will display over the
         * other objects.
         */
        boolean success = true;

        int index = size() - 1;
        int curZ = 0;

        if (index != -1)
            curZ = get(index).getZ();

        while (go.getZ() > curZ && index >= 0)
        {
            index--;
            if (index >= 0)
                curZ = get(index).getZ();
        }
        add(index + 1, go);

        //add objects that require frequent
        //updating to the update list
        if (go.requiresUpdating() && updateObjects != null)
            updateObjects.add(go);

        // Add to one or more spatial areas
        if (spatialGrid != null)
            addToSpatialCells(go);

        return success;
    }

    @Override
    public boolean remove(Object go)
    {
        //Overwritten to also remove object from internal update
        //list when appropriate and to update spatial area lists
        boolean success = true;

        GameObject gameObject = (GameObject)go;

        if (gameObject.requiresUpdating() && updateObjects != null)
            updateObjects.remove(gameObject);

        if (spatialGrid != null)
            removeFromSpatialCells(gameObject);

        success = super.remove(go);

        return success;
    }

    @Override
    public void clear()
    {
        super.clear();

        //Make sure to clear any objects from the internal
        //objects update list if any
        if (updateObjects != null)
            updateObjects.clear();

        //clear the collision area lists
        if (spatialGrid != null)
        {
            for (ArrayList<GameObjects> cellRow : spatialGrid)
            {
                for (GameObjects cell : cellRow)
                {
                    cell.clear();  // clear the objects inside the collision area cell
                }
            }
        }
    }

    // Compute which grid cells an object's bounds overlap
    private ArrayList<Point> computeCellsForObject(GameObject go)
    {
        Rectangle bounds = go.getCollisionBounds();

        int startCol = Math.max(0, bounds.x / cellWidth);
        int endCol   = Math.min(gridCols - 1, (bounds.x + bounds.width) / cellWidth);
        int startRow = Math.max(0, bounds.y / cellHeight);
        int endRow   = Math.min(gridRows - 1, (bounds.y + bounds.height) / cellHeight);

        ArrayList<Point> cells = new ArrayList<>();
        for (int row = startRow; row <= endRow; row++)
        {
            for (int col = startCol; col <= endCol; col++)
            {
                cells.add(new Point(col, row));
            }
        }
        return cells;
    }

    private void addToSpatialCells(GameObject go)
    {
        ArrayList<Point> newCells = computeCellsForObject(go);
        for (Point cell : newCells)
        {
            spatialGrid.get(cell.y).get(cell.x).add(go);
        }

        // store background areas in the object itself for efficient retrieval later
        go.setBackgroundAreas(newCells);
    }

    private void removeFromSpatialCells(GameObject go)
    {
        ArrayList<Point> oldCells = go.getBackgroundAreas();

        for (Point cell : oldCells)
        {
            spatialGrid.get(cell.y).get(cell.x).remove(go);
        }
        oldCells.clear();


        // remove the background areas from the object itself
        go.getBackgroundAreas().clear();
    }

    public GameObjects getNeighborObjects(GameObject go)
    {
        GameObjects result = new GameObjects(false);

        if (spatialGrid == null) {
            // if spatial partitioning not enabled, just return everything
            result.addAll(this);
            return result;
        }

        // Use a HashSet to avoid duplicates
        Set<GameObject> objectSet = new HashSet<>();

        // Figure out which cells the object overlaps
        for (Point cell : computeCellsForObject(go))
        {
            objectSet.addAll(spatialGrid.get(cell.y).get(cell.x));
        }

        // Transfer to result list
        result.addAll(objectSet);
        return result;
    }

    /**
     * Efficiently update which spatial grid cells this object belongs to.
     * Only moves it if the set of cells actually changed. */
    public void updateSpatialCells(GameObject go)
    {
        if (spatialGrid == null) return;

        // Build new set of cells this object should be in
        ArrayList<Point> newCells = computeCellsForObject(go);
        // Get the object’s currently tracked cells (stored inside GameObject)
        ArrayList<Point> oldCells = go.getBackgroundAreas();

        // If no change, then skip
        if (oldCells.equals(newCells)) return;

        // Remove from cells no longer relevant
        for (Point cell : oldCells)
        {
            if (!newCells.contains(cell))
                spatialGrid.get(cell.y).get(cell.x).remove(go);
        }

        // Add newly relevant cells
        for (Point cell : newCells)
        {
            if (!oldCells.contains(cell))
                spatialGrid.get(cell.y).get(cell.x).add(go);
        }

        // Update the cell areas in the object
        go.setBackgroundAreas(newCells);
    }

    //Returns a sublist of all objects currently in this
    //list that are of the requested type. Returns null
    //if no objects of the requested type are found.
    public GameObjects getObjectsOfType(int type)
    {
        GameObjects foundObjects = null;

        for (GameObject go : this)
        {
            if (go.getType() == type)
            {
                if (foundObjects == null)
                    foundObjects = new GameObjects(false);
                foundObjects.add(go);
            }
        }
        return foundObjects;
    }

    //Return all objects within a certain area of the game world. The fullyEnclosed
    //parameter determines whether we filter out partially contained objects.
    public GameObjects getObjectsWithinBounds(Rectangle bounds, boolean fullyEnclosed)
    {
        GameObjects objectsWithin = new GameObjects(false);

        for (GameObject go: this)
        {
            if ( fullyEnclosed && go.isWithinBounds(bounds) ||
                    !fullyEnclosed && go.getBounds().intersects(bounds))
                objectsWithin.add(go);
        }
        return objectsWithin;

    }

    public GameObjects getUpdateObjects()
    {
        return updateObjects;
    }

}
