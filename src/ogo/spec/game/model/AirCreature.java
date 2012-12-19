package ogo.spec.game.model;

import java.util.Set;
import java.util.HashSet;

public class AirCreature extends Creature
{
    public final static int MAX_ENERGY = 100;
    
    private int energy;

    public AirCreature(GameMap map)
    {
        super(map);
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
    protected Set<TileType> getAllowedTypes()
    {
        HashSet<TileType> types = new HashSet<TileType>();
        types.add(TileType.DEEP_WATER);
        types.add(TileType.SHALLOW_WATER);
        types.add(TileType.LAND);
        return types;
    }

    @Override
    protected boolean canMove()
    {
        return true;
    }
}
