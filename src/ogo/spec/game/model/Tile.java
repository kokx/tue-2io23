package ogo.spec.game.model;

import java.util.Set;
import java.util.HashSet;

public class Tile
{
    private Inhabitant inhabitant;
    private TileType type;

    protected int x, y;

    public Tile(TileType type, int x, int y)
    {
        this.type = type;
        this.x = x;
        this.y = y;
    }

    /**
     * Set an inhabitant to this tile.
     *
     * This method also calls the setTile() method on the inhabitant.
     */
    public void setInhabitant(Inhabitant i)
    {
        this.inhabitant = i;
        i.setCurrentTile(this);
    }

    /**
     * Does this tile have this inhabitant.
     */
    public boolean hasInhabitant()
    {
        return (this.inhabitant != null);
    }

    /**
     * Get the type.
     */
    public TileType getType()
    {
        return type;
    }

    /**
     * Is the given tile adjacent to this one.
     */
    public boolean isAdjacent(Tile t)
    {
        return (Math.abs(this.x - t.x) <= 1) && (Math.abs(this.y - t.y) <=1);
    }
}
