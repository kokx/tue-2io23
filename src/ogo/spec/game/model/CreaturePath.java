package ogo.spec.game.model;

import java.util.concurrent.ConcurrentLinkedQueue;

public class CreaturePath
{

    ConcurrentLinkedQueue<Tile> path = new ConcurrentLinkedQueue<Tile>();

    // the map
    Map map;

    // current and previous tile
    Tile current, previous;

    /**
     * Constructor.
     */
    public CreaturePath(Map map, Tile initial)
    {
        this.map = map;
        previous = initial;
        current = initial;
    }

    /**
     * Get the current tile.
     */
    public Tile getCurrentTile()
    {
        return current;
    }

    /**
     * Get the previous tile.
     */
    public Tile getPreviousTile()
    {
        return previous;
    }

    /**
     * Get the next tile.
     */
    public Tile getNextTile()
    {
        return path.peek();
    }

    /**
     * Go to the next tile.
     */
    public Tile step()
    {
        return path.poll();
    }

    /**
     * Calculate a path to the given tile.
     *
     * This method uses the A* algorithm.
     */
    public Tile calculatePath(Tile tile)
    {
        return tile;
    }
}
