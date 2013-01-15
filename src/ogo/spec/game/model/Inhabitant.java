package ogo.spec.game.model;

public class Inhabitant
{

    protected Tile currentTile;
    
    protected int id;
    
    public Inhabitant(int id){
        this.id = id;
    }
    
    public int getId(){
        return id;
    }

    /**
     * Set the tile.
     *
     * This method should only be called by Tile.setInhabitant.
     */
    public void setCurrentTile(Tile tile)
    {
        assert tile.hasInhabitant() && tile.getInhabitant() == this;
        currentTile = tile;
    }

    /**
     * Get the current tile.
     */
    public Tile getCurrentTile()
    {
        return currentTile;
    }
}
