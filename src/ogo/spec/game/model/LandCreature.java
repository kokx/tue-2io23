package ogo.spec.game.model;

public class LandCreature extends Creature
{
    
    @Override
    protected int getMoveSpeed(TileType tileType) {
        assert (tileType != TileType.DEEP_WATER);
        if(tileType == TileType.LAND)
        {
            return Creature.TICKS_PER_TILE_FAST;
        }
        if(tileType == TileType.SHALLOW_WATER)
        {
            return Creature.TICKS_PER_TILE_SLOW;
        }
        return -1;
    }

    @Override
    protected int getEatValue(Creature creature) {
        assert !(creature instanceof LandCreature);
        if(creature instanceof AirCreature)
        {
            return 4;
        }
        if(creature instanceof SeaCreature)
        {   
            return 6;
        }
        return 0;
    }
    
}