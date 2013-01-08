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
     * Set the inhabitant for this tile.
     *
     * This method also calls the setTile() method on the inhabitant.
     */
    public void setInhabitant(Inhabitant i)
    {
        inhabitant = i;
        if(i != null)
            i.setCurrentTile(this);
    }

    /**
     * Get the inhabitant for this tile.
     *
     * This method also calls the setTile() method on the inhabitant.
     */
    public Inhabitant getInhabitant()
    {
        return inhabitant;
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
    
    public boolean isDiagonal(Tile t)
    {
        return (this.isAdjacent(t) && this.x != t.x && this.y != t.y);
    }

    /**
     * Get the x value.
     */
    public int getX()
    {
        return x;
    }

    /**
     * Get the y value.
     */
    public int getY()
    {
        return y;
    }
    
    @Override
    public String toString() {
        return "(" + x + "," + y + ")";
    }
}
