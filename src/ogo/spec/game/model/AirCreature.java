package ogo.spec.game.model;

public class AirCreature extends Creature
{
    public final static int MAX_ENERGY = 100;
    
    private int energy;
    
    public AirCreature()
    {
        this.energy = MAX_ENERGY;
    }
    
    @Override
    public void tick()
    {
        energyTick();
        super.tick();
    }
    
    private void energyTick()
    {
        
    }
    
    @Override
    protected int getMoveSpeed(TileType tileType) {
        return Creature.TICKS_PER_TILE_AVG;
    }

    @Override
    protected int getEatValue(Creature creature) {
        if(creature instanceof AirCreature)
        {
            return 5;
        }
        if(creature instanceof LandCreature)
        {
            return 5;
        }
        if(creature instanceof SeaCreature)
        {
            return 6;
        }
        return 0;
    }
    
    @Override
    protected boolean canMove()
    {
        return true;
    }
}