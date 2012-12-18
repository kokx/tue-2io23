package ogo.spec.game.model;

import java.util.concurrent.ConcurrentLinkedQueue;

public class CreaturePath
{

    ConcurrentLinkedQueue<Tile> path = new ConcurrentLinkedQueue<Tile>();

    // the map
    GameMap map;

    // current and previous tile
    Tile current, previous;

    /**
     * Constructor.
     */
    public CreaturePath(GameMap map, Tile initial)
    {
        this.map = map;
        previous = initial;
        current = initial;
    }

    /**
     * Get the current tile.
     */
    synchronized public Tile getCurrentTile()
    {
        return current;
    }

    /**
     * Get the previous tile.
     */
    synchronized public Tile getPreviousTile()
    {
        return previous;
    }

    /**
     * Get the next tile.
     */
    synchronized public Tile getNextTile()
    {
        return path.peek();
    }

    /**
     * Go to the next tile.
     */
    synchronized public Tile step()
    {
        previous = current;
        current = path.poll();
        return current;
    }

    public final static void main(String[] args)
    {
        System.err.println("test");
    }

    /**
     * Calculate a path to the given tile.
     *
     * This method uses the A* algorithm.
     */
    public void calculatePath(Tile tile)
    {
    }
}
