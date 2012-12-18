package ogo.spec.game.model;

public class AirCreature extends Creature
{
    private int energy;
    
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
        throw new UnsupportedOperationException("Not supported yet.");
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