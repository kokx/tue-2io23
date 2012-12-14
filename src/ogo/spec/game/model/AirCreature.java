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
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    @Override
    protected boolean canMove()
    {
        return true;
    }
}